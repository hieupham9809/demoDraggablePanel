package com.example.zingdemodraggable.view;

import android.content.Context;

import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.example.zingdemodraggable.R;

public class ControllerVideo extends ConstraintLayout {
    ImageButton bigPlayPauseBtn;
    ImageButton bigNextBtn;
    ImageButton bigPreviousBtn;

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

        super.onFinishInflate();
    }

}
