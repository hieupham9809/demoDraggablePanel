package com.example.zingdemodraggable.view;


import android.app.ProgressDialog;
import android.content.Context;

import android.content.res.TypedArray;
import android.graphics.Canvas;

import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.zingdemodraggable.R;
import com.example.zingdemodraggable.datamodel.Video;


public class DragYouTubeLayout extends RelativeLayout {
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;
    private static final Boolean DEFAULT_ENABLE_PROGRESS_BAR = true;

    // Set replaceView 's Id and layout
    private final int replaceViewId = R.id.mini_info;
    private int replaceViewLayout = R.layout.minimize_info;

    // Set mainView 's Id and layout
    private LinearLayout mainView;
    private final int mainViewId = R.id.main_view;


    // Set secondView 's Id and layout
    private RelativeLayout secondView;
    private final int secondViewId = R.id.info_layout;
    private final int secondViewLayout = R.layout.full_layout_info;


    private VideoView mainViewChild;
    private int mainViewChildId;

    private Context context;
    private int mDraggingState = 0;
    private ViewDragHelper mDragHelper;
    private int mDraggingBorder;
    private int mVerticalRange = 0;
    private boolean mIsOpen;


    private Boolean isEnableProgressBar;

    public Boolean getEnableProgressBar() {
        return isEnableProgressBar;
    }

    private NumberProgressBar numberProgressBar;
    private RelativeLayout miniInfo;

    private OnDragViewChangeListener listener;

    private ViewGroup parent;

    private float mDragOffset;

    private boolean isRelease = false;
    private Animation animation;

    private int currentDrag = 0;

    private final float MIN_SCALE = 0.5f;
    private final float MIN_SCALE_COLLAPSE = 0.3f;
    private final float WIDTH_SCALE = 0.35f;
    private final int WIDTH_SCALE_RANGE = 150;

    private final float TRANSFORM_POINT = 0.85f;
    private float scaleView = 1f;
//    private float deltaScaleView = 0f;

    public void setOnDragViewChangeListener(OnDragViewChangeListener listener){
        this.listener = listener;
        listener.onMiniViewReplaced();

    }
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
            listener.onLayoutChanged();
            mDraggingBorder = top;

            mDragOffset = (float) top / mVerticalRange;

            scaleView = 1 - mDragOffset / (1 / (1 - MIN_SCALE));
            mainViewChild.setPivotX(0);
            mainViewChild.setPivotY(0);
//            deltaScaleView = 1 - scaleView;

//                mainViewChild.setPivotX(mainViewChild.getWidth());
//                mainViewChild.setPivotY(0);
//                mainViewChild.setScaleX(1 - mDragOffset / 2);
//                mainViewChild.setScaleY(1 - mDragOffset / 2);


            if (isRelease && mDraggingBorder >= mVerticalRange && currentDrag < mVerticalRange) {
                mainView.startAnimation(animation);
//                secondView.startAnimation(animation);
                isRelease = false;
                currentDrag = mVerticalRange + WIDTH_SCALE_RANGE;
//                Log.d("ZingDemoDraggable", " ati onviewpositionchanged animation actived, duration " );
            }
            if (mDragOffset <= TRANSFORM_POINT) {
                secondView.setAlpha((float) ((4 / 2.89) * mDragOffset * mDragOffset - (4 / 1.7) * mDragOffset + 1));
            } else {
                secondView.setAlpha((float) ((4 / 0.09) * mDragOffset * mDragOffset - (6.8 / 0.09) * mDragOffset + 2.89 / 0.09));
            }

            if (mDraggingBorder == 0 && currentDrag != 0) {
                currentDrag = 0;
            }
            if (mDraggingBorder == mVerticalRange && currentDrag < mVerticalRange) {
                currentDrag = mVerticalRange;
            }
//            secondView.setAlpha((float)((1/0.1275)*mDragOffset*mDragOffset - (1/0.1275) * mDragOffset + 1));

            requestLayout();


        }

        public int getViewVerticalDragRange(View child) {
            return mVerticalRange;
        }

        @Override
        public boolean tryCaptureView(View view, int i) {
//            Log.d("ZingDemoDraggable", "second " + (view.getId() == R.id.mini_info));
            return (view.getId() == R.id.main_view || view.getId() == R.id.mini_info);
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
//            final int rightBound = getWidth() - mainViewChild.getMeasuredWidth();
////            Log.d("ZingDemoDraggable", "left bound " + leftBound + "right bound " + rightBound);
//
//            int newLeft = Math.min(Math.max(left, leftBound), rightBound);
//
//            return newLeft;
//        }
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final float rangeToCheck = mVerticalRange;
            if (mDraggingBorder == 0) {
                mIsOpen = false;
                return;
            }
            if (mDraggingBorder == rangeToCheck) {
                mIsOpen = true;
//                return;
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

            if (settleToOpen) {
                isRelease = true;
            }

            final int initWidth = mainView.getMeasuredWidth();
            float vel = (yvel > 3000f) ? yvel : 3000f;
            float lowerBound = 0;
            if (mDraggingBorder >= mVerticalRange) {

                lowerBound = currentDrag;

            }
            final float lowerBoundConstant = lowerBound;

            animation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {

                    currentDrag = Math.round(interpolatedTime * WIDTH_SCALE_RANGE + mVerticalRange );
                    if (currentDrag >= lowerBoundConstant) {
                        requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };
            animation.setDuration((int) ((initWidth * (1 - WIDTH_SCALE) * 1f * 1000 / Math.abs(vel))));

            if (mDraggingBorder >= mVerticalRange) {
                currentDrag = mVerticalRange + WIDTH_SCALE_RANGE;

                mainView.startAnimation(animation);

//                isRelease = false;
//                Log.d("ZingDemoDraggable", "animation actived ");
                return;
            }
            if (mDragHelper.settleCapturedViewAt(0, settleDestY)) {

//                requestLayout();
                ViewCompat.postInvalidateOnAnimation(DragYouTubeLayout.this);
            }

        }
    }

    public DragYouTubeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
        initAtribute(attrs);
//        video = new Video();
//        video.setEpisode(22);
//        video.setFullName("Xuân Hoa Thu Nguyệt - Tập 22");
//        video.setReleaseDay("26/07/2019");
//        mIsOpen = false;
    }

    @Override
    protected void onFinishInflate() {
        mainView = findViewById(mainViewId);
        secondView = findViewById(secondViewId);
        numberProgressBar = findViewById(R.id.progress_bar);
        mainViewChild = findViewById(mainViewChildId);
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
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (currentDrag == 0){
            listener.onLayoutFlattened();
        } else {
            listener.onLayoutChanged();
        }

//        mVerticalRange = 1 - width * 9 / 16;
        if (mVerticalRange == 0){
            mVerticalRange = Math.round(getHeight() - getWidth() * 9f / 16 * MIN_SCALE);
        }




//        if ((float) mDraggingBorder / mVerticalRange > TRANSFORM_POINT) {
        if (currentDrag >= mVerticalRange) {
//            Log.d("ZingDemoDraggable", "final call " + currentDrag);

            // Replace view info
            if (secondView.getId() != replaceViewId) {
                parent = (ViewGroup) secondView.getParent();
                int index = parent.indexOfChild(secondView);
                parent.removeView(secondView);
                secondView = (RelativeLayout) LayoutInflater.from(context).inflate(replaceViewLayout, parent, false);
                parent.addView(secondView, index);

                listener.onSecondViewReplaced();
                //move{

                
                //}
            }
            if (currentDrag > mVerticalRange + WIDTH_SCALE_RANGE) {
                currentDrag = mVerticalRange + WIDTH_SCALE_RANGE;
            }
//            Log.d("ZingDemoDraggable", "measured height " + mainView.getMeasuredWidth());

            int originalVideoHeight = mainView.getMeasuredWidth() * 9 / 16;
//            Log.d("ZingDemoDraggable", "top onLayout" + Math.round(currentDrag * originalVideoHeight *(MIN_SCALE - MIN_SCALE_COLLAPSE) * 1f / WIDTH_SCALE_RANGE + mVerticalRange - mVerticalRange * (MIN_SCALE - MIN_SCALE_COLLAPSE) * originalVideoHeight * 1f / WIDTH_SCALE_RANGE));
            mainView.layout(
                    0,
                    Math.round(currentDrag * originalVideoHeight *(MIN_SCALE - MIN_SCALE_COLLAPSE) * 1f / WIDTH_SCALE_RANGE + mVerticalRange - mVerticalRange * (MIN_SCALE - MIN_SCALE_COLLAPSE) * originalVideoHeight * 1f / WIDTH_SCALE_RANGE),
                    Math.round(right * ((WIDTH_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (WIDTH_SCALE - 1) * 1f / WIDTH_SCALE_RANGE)),
                    bottom

            );

//            Log.d("ZingDemoDraggable", "mainViewChild " + mainViewChild);

            if (mainViewChild != null) {
                mainViewChild.setScaleX(((MIN_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (MIN_SCALE - 1) * 1f / WIDTH_SCALE_RANGE));
                mainViewChild.setScaleY(((MIN_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (MIN_SCALE - 1) * 1f / WIDTH_SCALE_RANGE));
            }
            secondView.layout(
                    Math.round(right * ((WIDTH_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (WIDTH_SCALE - 1) * 1f / WIDTH_SCALE_RANGE)),
                    Math.round(currentDrag * originalVideoHeight *(MIN_SCALE - MIN_SCALE_COLLAPSE) * 1f / WIDTH_SCALE_RANGE + mVerticalRange - mVerticalRange * (MIN_SCALE - MIN_SCALE_COLLAPSE) * originalVideoHeight * 1f / WIDTH_SCALE_RANGE),
                    right,
                    bottom
            );


        } else {
            if (secondView.getId() != secondViewId) {
                parent = (ViewGroup) secondView.getParent();
                int index = parent.indexOfChild(secondView);
                parent.removeView(secondView);
                secondView = (RelativeLayout) LayoutInflater.from(context).inflate(secondViewLayout, parent, false);
                parent.addView(secondView, index);

                listener.onMiniViewReplaced();


            }
            if (mainViewChild != null) {
                mainViewChild.setScaleX(1.0f);
                mainViewChild.setScaleY(1.0f);
            }
            mainView.layout(
                    0,
                    mDraggingBorder,
                    right,
                    mDraggingBorder + mainView.getMeasuredHeight()

            );

            secondView.layout(
                    0,
                    mDraggingBorder + mainView.getMeasuredHeight(),
                    right,
                    bottom
            );
        }
        // move{
        if (getEnableProgressBar()) {
            numberProgressBar.layout(
                    0,
                    mainView.getBottom() - numberProgressBar.getMeasuredHeight(),
                    numberProgressBar.getMeasuredWidth(),
                    mainView.getBottom()
            );
        }
        // }

    }


    private void onStopDraggingToClosed() {
        // To be implemented
    }

    private void onStartDragging() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        fullName.setText(video.getFullName());
//        episode.setText(String.format("Tập: %s", video.getEpisode()));
//        releaseDay.setText(String.format("Ngày ra mắt: %s", video.getReleaseDay()));
//        thumbnail.setImageBitmap(imageThumbnail);

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();
//        Log.d("ZingDemoDraggable", "action " + action);

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }

        Boolean should = mDragHelper.shouldInterceptTouchEvent(ev);
//        Log.d("ZingDemoDraggable", "onIntercept DragLayout " + should);

        return should;

//        return mDragHelper.shouldInterceptTouchEvent(ev);
//        return false;
    }

    private boolean isViewHit(View view, int x, int y) {
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;
        return screenX >= viewLocation[0]
                && screenX < viewLocation[0] + view.getWidth()
                && screenY >= viewLocation[1]
                && screenY < viewLocation[1] + view.getHeight();
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        Log.d("ZingDemoDraggable", "onTouchEvent DragLayout action " + ev.getAction());
        mDragHelper.processTouchEvent(ev);
        boolean isDragViewHit = isViewHit(mainView, (int) ev.getX(), (int) ev.getY()) || isViewHit(secondView,(int) ev.getX(), (int) ev.getY());

//        if (mDraggingState == MotionEvent.ACTION_DOWN && ev.getAction() == MotionEvent.ACTION_UP){
////            Log.d("ZingDemoDraggable", "release");
//
//            return false;
//        }
        return isDragViewHit;
    }

    @Override
    public void computeScroll() { // needed for automatic settling.
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int childWidthMeasureSpec;
        int childHeightMeasureSpec;
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        float collapseScale = 0;
        if (currentDrag >= mVerticalRange){
            if (currentDrag <= mVerticalRange + WIDTH_SCALE_RANGE){
                collapseScale = currentDrag * (MIN_SCALE - MIN_SCALE_COLLAPSE) * 1f / WIDTH_SCALE_RANGE + mVerticalRange * (MIN_SCALE_COLLAPSE - MIN_SCALE) * 1f / WIDTH_SCALE_RANGE;
            } else {
                collapseScale = MIN_SCALE - MIN_SCALE_COLLAPSE;
            }
        } else {
            collapseScale = 0;
        }
//        Log.d("ZingDemoDraggable", "scale " + (scaleView - collapseScale));

//        Log.d("ZingDemoDraggable", "collapse scale "+ collapseScale);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
//            if (child.getId() == R.id.main_image ) {
            switch (child.getId()){
                case mainViewId:
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth()), MeasureSpec.EXACTLY);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() * 1f * 9 / 16 * (scaleView - collapseScale)), MeasureSpec.EXACTLY);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                    break;
                case secondViewId:
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth()), MeasureSpec.EXACTLY);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() * scaleView), MeasureSpec.EXACTLY);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                    break;
                case replaceViewId:
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() * (1 - WIDTH_SCALE) ), MeasureSpec.EXACTLY);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth()* 1f * 9 / 16 * (scaleView - collapseScale)), MeasureSpec.EXACTLY);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                    break;
                default:
                    break;

            }


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
    public interface OnDragViewChangeListener{
        void onMiniViewReplaced();
        void onSecondViewReplaced();
        void onLayoutChanged();
        void onLayoutFlattened();
    }
    private void initAtribute(AttributeSet attributeSet){
        if (attributeSet == null){
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DragYouTubeLayout);
        this.isEnableProgressBar = typedArray.getBoolean(R.styleable.DragYouTubeLayout_progress_bar_enable, DEFAULT_ENABLE_PROGRESS_BAR);

        this.mainViewChildId = typedArray.getResourceId(R.styleable.DragYouTubeLayout_main_child_view_id, -1);


    }
}
