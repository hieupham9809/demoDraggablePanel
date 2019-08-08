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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.zingdemodraggable.R;
import com.example.zingdemodraggable.datamodel.Video;

public class DragLayout extends RelativeLayout {
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

    private VideoView mMainImage;
    private LinearLayout mInfoPanel;

    private Video video;

    private TextView fullName;
    private TextView episode;
    private TextView releaseDay;
    private ImageView thumbnail;

    private Boolean requestLayoutFlag = true;

    private float mDragOffset;
    private final float MIN_SCALE = 0.5f;
    private final float TRANSFORM_POINT = 0.85f;
    private float scaleView = 1f;

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
            Log.d("ZingDemoDraggable", "mDragOffset " + mDragOffset + "y "+(float)((1/0.1275)*mDragOffset*mDragOffset - (1/0.1275) * mDragOffset + 1));
//            this.setPivotX(mMainImage.getWidth());
//            mMainImage.setPivotY(mMainImage.getHeight());
//            mMainImage.setScaleX(1 - mDragOffset / 2);
//            mMainImage.setScaleY(1 - mDragOffset / 2);
            scaleView = 1 - mDragOffset/(1 / (1 - MIN_SCALE));

//                mMainImage.setPivotX(mMainImage.getWidth());
//                mMainImage.setPivotY(0);
//                mMainImage.setScaleX(1 - mDragOffset / 2);
//                mMainImage.setScaleY(1 - mDragOffset / 2);
//            Log.d("ZingDemoDraggable", "onViewPositionChanged called " );

            if (mDragOffset <= TRANSFORM_POINT){
                mInfoPanel.setAlpha((float)((4/2.89)*mDragOffset*mDragOffset - (4/1.7) * mDragOffset + 1));
            } else {
                mInfoPanel.setAlpha((float)((4/0.09)*mDragOffset*mDragOffset - (6.8/0.09)*mDragOffset + 2.89/0.09));
            }
//            mInfoPanel.setAlpha((float)((1/0.1275)*mDragOffset*mDragOffset - (1/0.1275) * mDragOffset + 1));
            if (scaleView != MIN_SCALE && requestLayoutFlag) {
                requestLayout();
            } else {
                requestLayoutFlag = false;
                if (mDraggingBorder < mVerticalRange * 2 / 3){
                    requestLayoutFlag = true;
                }
            }
        }

        public int getViewVerticalDragRange(View child) {
            return mVerticalRange;
        }

        @Override
        public boolean tryCaptureView(View view, int i) {
            return (view.getId() == R.id.main_image);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
//            Log.d("ZingDemoDraggable", "vertical");
            final int topBound = getPaddingTop();
            final int bottomBound = mVerticalRange;
            return Math.min(Math.max(top, topBound), bottomBound);
        }
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            final int leftBound = getPaddingLeft();
            final int rightBound = getWidth() - mMainImage.getMeasuredWidth();
//            Log.d("ZingDemoDraggable", "left bound " + leftBound + "right bound " + rightBound);

            int newLeft = Math.min(Math.max(left, leftBound), rightBound);

            return newLeft;
        }
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
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
                switch (bottomMode) {
                    case BOTTOM_LEFT:
                        finalLeft = 0;
                        break;
                    case BOTTOM_CENTER:
                        finalLeft = Math.round(getWidth() * 1f / 2 - getWidth() * MIN_SCALE / 2);
                        break;
                    default:
                        finalLeft = Math.round(getWidth() - getWidth() * MIN_SCALE);
                }
            } else {
                finalLeft = 0;
            }

            if(mDragHelper.settleCapturedViewAt(finalLeft, settleDestY)) {
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            }
        }
    }

    public DragLayout(Context context, AttributeSet attrs) {
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
        int finalLeft = 0;
        switch (bottomMode){
            case BOTTOM_LEFT:
                finalLeft = 0;
                infoPanelLeft = finalLeft + Math.round(right * scaleView);
                infoPanelTop = mDraggingBorder;
                infoPanelRight = right;
                infoPanelBottom = mDraggingBorder + mMainImage.getMeasuredHeight();
                break;
            case BOTTOM_CENTER:
                finalLeft = Math.round(getWidth() * 1f / 2 - getWidth() * scaleView / 2);
                infoPanelLeft = finalLeft;
                infoPanelTop = mDraggingBorder - mMainImage.getMeasuredHeight();
                infoPanelRight = finalLeft + Math.round(right * scaleView);
                infoPanelBottom = mDraggingBorder;
                break;
            default:
                finalLeft = Math.round(getWidth() - getWidth() * scaleView);
                infoPanelLeft = left;
                infoPanelTop = mDraggingBorder;
                infoPanelRight = finalLeft;
                infoPanelBottom = mDraggingBorder + mMainImage.getMeasuredHeight();
        }
        mMainImage.layout(
                finalLeft,
                mDraggingBorder,
                finalLeft + Math.round(right * scaleView),
                mDraggingBorder + mMainImage.getMeasuredHeight()

        );

//        mInfoPanel.layout(
//                finalLeft,
//                mDraggingBorder + mMainImage.getMeasuredHeight(),
//                finalLeft + Math.round(right * scaleView),
//                mDraggingBorder + bottom
//        );

        if ((float) mDraggingBorder / mVerticalRange > TRANSFORM_POINT) {
//            mInfoPanel.setAlpha(1f);
            mInfoPanel.layout(
                    infoPanelLeft,
                    infoPanelTop,
                    infoPanelRight,
                    infoPanelBottom
            );
        } else {
            mInfoPanel.layout(
                finalLeft,
                mDraggingBorder + mMainImage.getMeasuredHeight(),
                finalLeft + Math.round(right * scaleView),
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
        int parentWidth = 0;
        int parentHeight = 0;
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++){
            View child = getChildAt(i);
//            if (child.getId() == R.id.main_image ) {

            if (child.getId() == R.id.main_image) {
                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() * scaleView), MeasureSpec.EXACTLY);
                int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() * 9 / 16 * scaleView), MeasureSpec.EXACTLY);
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            } else {
                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() * scaleView), MeasureSpec.EXACTLY);
                int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() * scaleView), MeasureSpec.EXACTLY);
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
