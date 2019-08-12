package com.example.zingdemodraggable.view;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.zingdemodraggable.R;
import com.example.zingdemodraggable.datamodel.Video;

public class DragYouTubeLayout extends RelativeLayout {
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;

    private final int BOTTOM_LEFT = 1;
    private final int BOTTOM_CENTER = 2;
    private final int BOTTOM_RIGHT = 3;

    private int bottomMode = BOTTOM_RIGHT;
    private int finalLeft;

    private int mDraggingState = 0;
    //    private Button mQueenButton;
    private ViewDragHelper mDragHelper;
    private int mDraggingBorder;
    private int mVerticalRange;
    private boolean mIsOpen;

    private LinearLayout wrapVideo;
    private VideoView mMainImage;
    private LinearLayout mInfoPanel;

    private Video video;

    private TextView fullName;
    private TextView episode;
    private TextView releaseDay;
    private ImageView thumbnail;

    private Boolean requestLayoutFlag = true;

    private float mDragOffset;

    private boolean isRelease = false;
    private Animation animation;

    private int currentDrag = 0;

    private final float MIN_SCALE = 0.5f;
    private final float WIDTH_SCALE = MIN_SCALE - 0.2f;
    private final int WIDTH_SCALE_RANGE = 100;
    private final float TRANSFORM_POINT = 0.85f;
    private float scaleView = 1f;
    private float deltaScaleView = 0f;

    private int infoPanelLeft;
    private int infoPanelTop;
    private int infoPanelRight;
    private int infoPanelBottom;

    private Bitmap imageThumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.small_thumbnail);
    public class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public void onViewDragStateChanged(int state) {
            if (state == mDraggingState) { // no change
                return;
            }
            if ((mDraggingState == ViewDragHelper.STATE_DRAGGING || mDraggingState == ViewDragHelper.STATE_SETTLING) &&
                    state == ViewDragHelper.STATE_IDLE) {
                // the view stopped from moving.

                if (mDraggingBorder == 0) {
                    onStopDraggingToClosed();
                } else if (mDraggingBorder == mVerticalRange) {
                    mIsOpen = true;
                }
            }
            if (state == ViewDragHelper.STATE_DRAGGING) {
                onStartDragging();
            }
            mDraggingState = state;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mDraggingBorder = top;

            mDragOffset = (float) top / mVerticalRange;
//            this.setPivotX(mMainImage.getWidth());
//            mMainImage.setPivotY(mMainImage.getHeight());
//            mMainImage.setScaleX(1 - mDragOffset / 2);
//            mMainImage.setScaleY(1 - mDragOffset / 2);
            scaleView = 1 - mDragOffset/(1 / (1 - MIN_SCALE));
//            deltaScaleView = 1 - scaleView;

//                mMainImage.setPivotX(mMainImage.getWidth());
//                mMainImage.setPivotY(0);
//                mMainImage.setScaleX(1 - mDragOffset / 2);
//                mMainImage.setScaleY(1 - mDragOffset / 2);
            if (isRelease && mDraggingBorder >= mVerticalRange){
                wrapVideo.startAnimation(animation);
                mInfoPanel.startAnimation(animation);
                isRelease = false;
                currentDrag = mVerticalRange + WIDTH_SCALE_RANGE;
                Log.d("ZingDemoDraggable", "animation actived");
            }
            if (mDragOffset <= TRANSFORM_POINT){
                mInfoPanel.setAlpha((float)((4/2.89)*mDragOffset*mDragOffset - (4/1.7) * mDragOffset + 1));
            } else {
                mInfoPanel.setAlpha((float)((4/0.09)*mDragOffset*mDragOffset - (6.8/0.09)*mDragOffset + 2.89/0.09));
            }

            if (mDraggingBorder == 0 && currentDrag != 0){
                currentDrag = 0;
            }
            if (mDraggingBorder == mVerticalRange && currentDrag < mVerticalRange){
                currentDrag = mVerticalRange;
            }
//            mInfoPanel.setAlpha((float)((1/0.1275)*mDragOffset*mDragOffset - (1/0.1275) * mDragOffset + 1));
            requestLayout();
//            if (scaleView != MIN_SCALE && requestLayoutFlag) {
//                requestLayout();
//            } else {
//                requestLayoutFlag = false;
//                if (mDraggingBorder < mVerticalRange * 2 / 3){
//                    requestLayoutFlag = true;
//                }
//            }
        }

        public int getViewVerticalDragRange(View child) {
            return mVerticalRange;
        }

        @Override
        public boolean tryCaptureView(View view, int i) {
            return (view.getId() == R.id.video_wrap);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
//            Log.d("ZingDemoDraggable", "vertical");

            currentDrag += dy;
            final int topBound = getPaddingTop();
            final int bottomBound = mVerticalRange;
            if (currentDrag < mVerticalRange) {
                return Math.min(Math.max(top, topBound), bottomBound);
            } else {
                return mVerticalRange;
            }
        }
//        @Override
//        public int clampViewPositionHorizontal(View child, int left, int dx) {
//            final int leftBound = getPaddingLeft();
//            final int rightBound = getWidth() - mMainImage.getMeasuredWidth();
////            Log.d("ZingDemoDraggable", "left bound " + leftBound + "right bound " + rightBound);
//
//            int newLeft = Math.min(Math.max(left, leftBound), rightBound);
//
//            return newLeft;
//        }
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            Log.d("ZingDemoDraggable", "onViewReleased called " + yvel);
            final float rangeToCheck = mVerticalRange;
            if (mDraggingBorder == 0) {
                mIsOpen = false;
                return;
            }
            if (mDraggingBorder == rangeToCheck) {
                mIsOpen = true;
                return;
            }
            boolean settleToOpen = false;
            if (yvel > AUTO_OPEN_SPEED_LIMIT) { // speed has priority over position
                settleToOpen = true;
            } else if (yvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = false;
            } else if (mDraggingBorder > rangeToCheck / 2) {
                settleToOpen = true;
            } else if (mDraggingBorder < rangeToCheck / 2) {
                settleToOpen = false;
            }

            final int settleDestY = settleToOpen ? mVerticalRange : 0;

            if (settleToOpen){
                isRelease = true;
            }
//            if (settleToOpen){
//                switch (bottomMode) {
//                    case BOTTOM_LEFT:
//                        finalLeft = 0;
//                        break;
//                    case BOTTOM_CENTER:
//                        finalLeft = Math.round(getWidth() * 1f / 2 - getWidth() * MIN_SCALE / 2);
//                        break;
//                    default:
//                        finalLeft = Math.round(getWidth() - getWidth() * MIN_SCALE);
//                }
//            } else {
//                finalLeft = 0;
//            }
            final int initWidth = wrapVideo.getMeasuredWidth();
            Log.d("ZingDemoDraggable", "init width "+yvel);
            float vel = (yvel > 5000f) ? yvel : 5000f;
            animation = new Animation()
            {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    if(interpolatedTime > 1 - WIDTH_SCALE){

                    }else{
                        wrapVideo.layout(
                                0,
                                mDraggingBorder,
                                Math.round(initWidth - initWidth * interpolatedTime),
                                mDraggingBorder + wrapVideo.getMeasuredHeight()

                        );
                        mInfoPanel.layout(
                                Math.round(initWidth - initWidth * interpolatedTime),
                                mDraggingBorder,
                                initWidth,
                                mDraggingBorder + wrapVideo.getMeasuredHeight()
                        );
                        wrapVideo.requestLayout();
                        mInfoPanel.requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };
//            Log.d("ZingDemoDraggable", "duration " + (int)((initWidth * WIDTH_SCALE) * 1f * 1000/ Math.abs(yvel) ) );
            animation.setDuration((int)((initWidth * (1 - WIDTH_SCALE) * 1f * 1000/ Math.abs(vel) )));
//            animation.setDuration((int)(initWidth / (wrapVideo.getContext().getResources().getDisplayMetrics().density)));

            // Collapse speed of 1dp/ms

            if(mDragHelper.settleCapturedViewAt(0, settleDestY)) {
//                currentDrag = settleToOpen ? mVerticalRange + WIDTH_SCALE_RANGE : 0;
//                Log.d("ZingDemoDraggable", "scroll completed");
//                if(yvel == 0){
//                    yvel = 5000;
//                }
//                if (yvel > 0) {
//                    for (int i = 1; i <= 100; i++) {
//                        currentDrag = Math.round(mVerticalRange + i );
//                        Log.d("ZingDemoDraggable", "current drag "+ currentDrag);
//                        //requestLayout();
//
//
//                    }
//                }
//                currentDrag = mVerticalRange + 20;
//                requestLayout();
                ViewCompat.postInvalidateOnAnimation(DragYouTubeLayout.this);
            }
        }
    }

    public DragYouTubeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
        video = new Video();
        video.setEpisode(22);
        video.setFullName("Xuân Hoa Thu Nguyệt - Tập 22");
        video.setReleaseDay("26/07/2019");
        mIsOpen = false;
    }

    @Override
    protected void onFinishInflate() {
//        mQueenButton  = (Button) findViewById(R.id.queen_button);
        wrapVideo = findViewById(R.id.video_wrap);
        mMainImage = findViewById(R.id.main_image);
        mInfoPanel = findViewById(R.id.info_layout);
        fullName = findViewById(R.id.full_name);
        episode = findViewById(R.id.episode);
        releaseDay = findViewById(R.id.releaseDay);
        thumbnail = findViewById(R.id.thumbnail);

        mIsOpen = false;
        super.onFinishInflate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        Log.d("ZingDemoDraggable", "onSizeChanged called");
//        mVerticalRange = (int) (h * 0.66);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom){
//        Log.d("ZingDemoDraggable", "onLayout called " );

//        mVerticalRange = 1 - width * 9 / 16;
        mVerticalRange = Math.round(getHeight() - getWidth() * 9/16 * MIN_SCALE);


//        Log.d("ZingDemoDraggable", "current drag "+ currentDrag );

//        mInfoPanel.layout(
//                finalLeft,
//                mDraggingBorder + mMainImage.getMeasuredHeight(),
//                finalLeft + Math.round(right * scaleView),
//                mDraggingBorder + bottom
//        );

//        if ((float) mDraggingBorder / mVerticalRange > TRANSFORM_POINT) {
        if (currentDrag >= mVerticalRange) {
//            Log.d("ZingDemoDraggable", "final call " + currentDrag);

            if (currentDrag <= mVerticalRange + WIDTH_SCALE_RANGE) {
//            mInfoPanel.setAlpha(1f);
//                Log.d("ZingDemoDraggable", "transform " + Math.round(right * ((WIDTH_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (WIDTH_SCALE - 1) * 1f / WIDTH_SCALE_RANGE)));

                wrapVideo.layout(
                        0,
                        mDraggingBorder,
                        Math.round(right * ((WIDTH_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (WIDTH_SCALE - 1) * 1f / WIDTH_SCALE_RANGE)),
                        mDraggingBorder + wrapVideo.getMeasuredHeight()

                );
                mInfoPanel.layout(
                        Math.round(right * ((WIDTH_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (WIDTH_SCALE - 1) * 1f / WIDTH_SCALE_RANGE)),
                        mDraggingBorder,
                        right,
                        mDraggingBorder + wrapVideo.getMeasuredHeight()
                );
            } else {
                currentDrag = mVerticalRange + WIDTH_SCALE_RANGE;
            }

        } else {
//            Log.d("ZingDemoDraggable", "top " + Math.round(mDraggingBorder + wrapVideo.getMeasuredWidth() *1f * (9f / 16) * (deltaScaleView * 1f / 2)) + "bottom " + Math.round(mDraggingBorder + wrapVideo.getMeasuredWidth() *1f * (9f / 16) * (1 -  deltaScaleView * 1f / 2)));

//            wrapVideo.layout(
//                    0,
//                    Math.round(mDraggingBorder + wrapVideo.getMeasuredWidth() *1f * (9f / 16) * (deltaScaleView * 1f / 2)),
//                    right,
//                    Math.round(mDraggingBorder + wrapVideo.getMeasuredWidth() *1f * (9f / 16) * (1 -  deltaScaleView * 1f / 2))
//
//            );
//            mInfoPanel.layout(
//                    0,
//                    Math.round(mDraggingBorder + wrapVideo.getMeasuredWidth() *1f * (9f / 16) * (1 -  deltaScaleView * 1f / 2)),
//                    right,
//                    mDraggingBorder + bottom
//            );
            wrapVideo.layout(
                    0,
                    mDraggingBorder,
                    right,
                    mDraggingBorder + wrapVideo.getMeasuredHeight()

            );
            mInfoPanel.layout(
                    0,
                    mDraggingBorder + wrapVideo.getMeasuredHeight(),
                    right,
                    mDraggingBorder + bottom
            );
        }

    }


    private void onStopDraggingToClosed() {
        // To be implemented
    }

    private void onStartDragging() {

    }
    @Override
    protected void onDraw(Canvas canvas){
        fullName.setText(video.getFullName());
        episode.setText(String.format("Tập: %s",video.getEpisode()));
        releaseDay.setText(String.format("Ngày ra mắt: %s",video.getReleaseDay()));
        thumbnail.setImageBitmap(imageThumbnail);

    }
//    private boolean isQueenTarget(MotionEvent event) {
//        int[] queenLocation = new int[2];
//        mQueenButton.getLocationOnScreen(queenLocation);
//        int upperLimit = queenLocation[1] + mQueenButton.getMeasuredHeight();
//        int lowerLimit = queenLocation[1];
//        int y = (int) event.getRawY();
//        return (y > lowerLimit && y < upperLimit);
//    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP){
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        Log.d("ZingDemoDraggable", "touched");
        mDragHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    public void computeScroll() { // needed for automatic settling.
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Log.d("ZingDemoDraggable", "onMeasure called");
        Log.d("ZingDemoDraggable", "current drag " + currentDrag);

        int parentWidth = 0;
        int parentHeight = 0;
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++){
            View child = getChildAt(i);
//            if (child.getId() == R.id.main_image ) {
            if (child.getId() == R.id.video_wrap) {
                if (currentDrag < mVerticalRange){
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() ), MeasureSpec.EXACTLY);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() * 9 / 16 * scaleView), MeasureSpec.EXACTLY);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                } else {
                    if (currentDrag <= mVerticalRange + WIDTH_SCALE_RANGE) {
                        childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() * ((WIDTH_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (WIDTH_SCALE - 1) * 1f / WIDTH_SCALE_RANGE)), MeasureSpec.EXACTLY);
                        childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() * 9 / 16 * scaleView), MeasureSpec.EXACTLY);
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mMainImage.getLayoutParams();
                        layoutParams.width = Math.round(child.getMeasuredWidth() * ((WIDTH_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (WIDTH_SCALE - 1) * 1f / WIDTH_SCALE_RANGE));

//                        Log.d("ZingDemoDraggable", "scale width " + Math.round(child.getMeasuredWidth() * ((WIDTH_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (WIDTH_SCALE - 1) * 1f / WIDTH_SCALE_RANGE)));
                        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                        mMainImage.setLayoutParams(layoutParams);

                        Log.d("ZingDemoDraggable", "width " + child.getMeasuredWidth());

                    } else {
                        currentDrag = mVerticalRange + WIDTH_SCALE_RANGE;
                    }
                }


            } else {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() ), MeasureSpec.EXACTLY);
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() * scaleView), MeasureSpec.EXACTLY);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            }
//            parentWidth += Math.round(child.getMeasuredWidth() * scaleView);
//            parentHeight += Math.round(child.getMeasuredWidth() * 9 / 16 * scaleView);
//            Log.d("ZingDemoDraggable", "" + parentWidth + " " + parentHeight);
//            break;
//            }

        }
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
//        setMeasuredDimension(parentWidth, parentHeight);
    }

    public boolean isMoving() {
        return (mDraggingState == ViewDragHelper.STATE_DRAGGING ||
                mDraggingState == ViewDragHelper.STATE_SETTLING);
    }

    public boolean isOpen() {
        return mIsOpen;
    }
}
