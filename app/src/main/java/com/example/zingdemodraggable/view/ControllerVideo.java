package com.example.zingdemodraggable.view;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.zingdemodraggable.R;

public class ControllerVideo extends ConstraintLayout {
    ImageButton bigPlayPauseBtn;
    ImageButton bigNextBtn;
    ImageButton bigPreviousBtn;

//    VideoControllerInterface listener;
//
//    public void setVideoControllerListener (VideoControllerInterface listener){
//        this.listener = listener;
//    }


    public ControllerVideo(Context context) {
        super(context);
    }

    public ControllerVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_MOVE) {
            return false;
        } else {
//            Log.d("ZingDemoDraggable", "onIntercept Controller");

            if (isViewHit(bigPlayPauseBtn, (int) ev.getX(), (int) ev.getY())
                    || isViewHit(bigNextBtn, (int) ev.getX(), (int) ev.getY())
                    || isViewHit(bigPreviousBtn, (int) ev.getX(), (int) ev.getY())

            ) {
                return false;
            } else {
                return true;
            }
        }

    }

    public boolean onTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        if (action == MotionEvent.ACTION_MOVE) {
            return false;
        } else {


            Log.d("ZingDemoDraggable", "viewHIt");

            if (isViewHit(bigPlayPauseBtn, (int) ev.getX(), (int) ev.getY())
                    || isViewHit(bigNextBtn, (int) ev.getX(), (int) ev.getY())
                    || isViewHit(bigPreviousBtn, (int) ev.getX(), (int) ev.getY())

            ) {
                return true;
            } else {
                return false;
            }
        }



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
    protected void onFinishInflate() {
        bigPlayPauseBtn = findViewById(R.id.big_play_pause_button);
        bigNextBtn = findViewById(R.id.big_next_button);
        bigPreviousBtn = findViewById(R.id.big_previous_button);
//        bigPlayPauseBtn.setOnTouchListener(new View.OnTouchListener(){
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d("ZingDemoDraggable", "clicked");
//                return false;
//            }
//        });
//        bigPlayPauseBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                Log.d("ZingDemoDraggable", "clicked");
//
//            }
//        });

        super.onFinishInflate();
    }
//    public interface VideoControllerInterface {
//        void playPause();
//    }
}
