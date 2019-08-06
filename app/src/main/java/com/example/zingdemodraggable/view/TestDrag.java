//package com.example.zingdemodraggable.view;
//
//import android.content.Context;
//import android.support.v4.view.ViewCompat;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//import com.example.zingdemodraggable.R;
//
//public class TestDrag extends RelativeLayout {
//    private ViewDragHelper mDragHelper;
//    private ImageView imageView;
//    public TestDrag(Context context) {
//        super(context);
//    }
//
//    public TestDrag(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
//
//    }
//
//    public TestDrag(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//    public class DragHelperCallback extends ViewDragHelper.Callback {
//
//
//
//
//        @Override
//        public boolean tryCaptureView(View view, int i) {
//            return (view.getId() == R.id.image_view);
//        }
//
//        @Override
//        public int clampViewPositionVertical(View child, int top, int dy) {
////            Log.d("ZingDemoDraggable", "vertical");
//            final int topBound = getPaddingTop();
//            final int bottomBound = 500;
//            return Math.min(Math.max(top, topBound), bottomBound);
//        }
//        @Override
//        public int clampViewPositionHorizontal(View child, int left, int dx) {
//            final int leftBound = getPaddingLeft();
//            final int rightBound = getWidth() ;
//            Log.d("ZingDemoDraggable", "left bound " + leftBound + "right bound " + rightBound);
//
//            int newLeft = Math.min(Math.max(left, leftBound), rightBound);
//
//            return newLeft;
//        }
//
//    }
//    @Override
//    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        Log.d("ZingDemoDraggable", "onLayout called ");
//        imageView.layout(
//                0,
//                0,
//                100,
//                100
//        );
//    }
//    @Override
//    protected void onFinishInflate() {
////        mQueenButton  = (Button) findViewById(R.id.queen_button);
//        imageView = findViewById(R.id.image_view);
//
//        super.onFinishInflate();
//    }
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        final int action = ev.getAction();
//
//        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP){
//            mDragHelper.cancel();
//            return false;
//        }
//        return mDragHelper.shouldInterceptTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
////        Log.d("ZingDemoDraggable", "touched");
//        mDragHelper.processTouchEvent(ev);
//        return true;
//    }
//}
