package com.example.zingdemodraggable.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.example.zingdemodraggable.R;
import com.example.zingdemodraggable.view.DragLayout;
import com.example.zingdemodraggable.view.DragYouTubeLayout;
import com.example.zingdemodraggable.view.NumberProgressBar;

import java.lang.ref.WeakReference;
//import com.example.zingdemodraggable.view.TestDrag;

public class MainActivity extends AppCompatActivity {
    VideoView video;

//    String video_url = "android.resource://" + getPackageName() + "/" + R.raw.sample;
    ProgressDialog pd;

    private MainActivity mainActivity = this;
    private DragYouTubeLayout dragLayout;
    private RelativeLayout outerLayout;
    private NumberProgressBar numberProgressBar;
    private MediaController mediaController;
//    private TestDrag testDrag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
//        testDrag = findViewById(R.id.test_drag);
        dragLayout = findViewById(R.id.drag_layout);
        outerLayout = findViewById(R.id.outer_layout);
        video = findViewById(R.id.main_image);

        dragLayout.setPlayPauseCustomOnClickListener(new DragYouTubeLayout.PlayPauseCustomOnClickListener() {
            @Override
            public void onClick() {

            }
        });

        numberProgressBar = findViewById(R.id.progress_bar);
//        if (mediaController == null){
//            mediaController = new MediaController(MainActivity.this);
//            mediaController.setAnchorView(video);
//            Log.d("ZingDemoDraggable", mediaController.setProgre
//
//            video.setMediaController(mediaController);
//
//        }
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Buffering video ...");
        pd.show();

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample);
        video.setVideoURI(uri);
        dragLayout.setVideoView(video);
        dragLayout.startVideo(pd);
//        video.start();

//        numberProgressBar.incrementProgressBy(50);


//        mMainLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                if (outerLayout.isMoving()) {
//                    v.setTop(oldTop);
//                    v.setBottom(oldBottom);
//                    v.setLeft(oldLeft);
//                    v.setRight(oldRight);
//                }
//            }
//        });
//        dragLayout.setVisibility(View.VISIBLE);

    }//onCreate


}
