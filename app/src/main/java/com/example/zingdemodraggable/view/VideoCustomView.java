package com.example.zingdemodraggable.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class VideoCustomView extends VideoView {
    public VideoCustomView(Context context) {
        super(context);
    }

    public VideoCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performClick(){
        super.performClick();
        return true;
    }
}
