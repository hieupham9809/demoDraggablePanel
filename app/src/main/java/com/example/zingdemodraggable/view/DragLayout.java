package com.example.zingdemodraggable.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.example.zingdemodraggable.R;

public class DragLayout extends RelativeLayout {
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;
    private int mDraggingState = 0;
//    private Button mQueenButton;
    private ViewDragHelper mDragHelper;
    private int mDraggingBorder;
    private int mVerticalRange;
    private boolean mIsOpen;

    private VideoView mMainImage;
    private ImageView mSecondImage;

    private Boolean requestLayoutFlag = true;

    private float mDragOffset;
    private final float MIN_SCALE = 0.7f;
    private float scaleView = 1f;
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

//                mMainImage.setPivotX(mMainImage.getWidth());
//                mMainImage.setPivotY(0);
//                mMainImage.setScaleX(1 - mDragOffset / 2);
//                mMainImage.setScaleY(1 - mDragOffset / 2);
            Log.d("ZingDemoDraggable", "onViewPositionChanged called " );

            mSecondImage.setAlpha(1 - mDragOffset);
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
            Log.d("ZingDemoDraggable", "left bound " + leftBound + "right bound " + rightBound);

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

            if(mDragHelper.settleCapturedViewAt(settleToOpen ? Math.round(getWidth() - getWidth() * MIN_SCALE) : 0, settleDestY)) {
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            }
        }
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());

        mIsOpen = false;
    }

    @Override
    protected void onFinishInflate() {
//        mQueenButton  = (Button) findViewById(R.id.queen_button);
        mMainImage = findViewById(R.id.main_image);
        mSecondImage = findViewById(R.id.second_image);

        mIsOpen = false;
        super.onFinishInflate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d("ZingDemoDraggable", "onSizeChanged called");
//        mVerticalRange = (int) (h * 0.66);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom){
        Log.d("ZingDemoDraggable", "onLayout called " );

//        mVerticalRange = 1 - width * 9 / 16;
        mVerticalRange = Math.round(getHeight() - getWidth() * 9/16 * MIN_SCALE);

//            if (mDraggingState == 0) {
                mMainImage.layout(
                        Math.round(getWidth() - getWidth() * scaleView),
                        mDraggingBorder,
                        right,
                        mDraggingBorder + mMainImage.getMeasuredHeight()

                );
//            }
//            mMainImage.layout(
//                    0,
//                    mDraggingBorder,
//                    right,
//                    mDraggingBorder + mMainImage.getMeasuredHeight()
//
//            );
            mSecondImage.layout(
                    Math.round(getWidth() - getWidth() * scaleView),
                    mDraggingBorder + mMainImage.getMeasuredHeight(),
                    right,
                    mDraggingBorder + bottom
            );


    }

    private void onStopDraggingToClosed() {
        // To be implemented
    }

    private void onStartDragging() {

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
