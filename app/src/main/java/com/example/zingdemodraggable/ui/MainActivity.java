package com.example.zingdemodraggable.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.zingdemodraggable.R;
import com.example.zingdemodraggable.fragments.ListFragment;
import com.example.zingdemodraggable.fragments.VideoFragment;
import com.example.zingdemodraggable.view.DragLayout;
import com.example.zingdemodraggable.view.DragYouTubeLayout;
import com.example.zingdemodraggable.view.NumberProgressBar;

import java.lang.ref.WeakReference;
//import com.example.zingdemodraggable.view.TestDrag;

public class MainActivity extends AppCompatActivity implements ListFragment.OnListFragmentListener {
    VideoView video;
    String urlVideo = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";
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
//        dragLayout = findViewById(R.id.drag_layout);
//        outerLayout = findViewById(R.id.outer_layout);
//        video = findViewById(R.id.main_image);
//        dragLayout.bringToFront();
//        dragLayout.invalidate();

//        addBtn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
////                if (event.getAction() == MotionEvent.ACTION_DOWN){
////                    Log.d("ZingDemoDraggable", "clicked event");
////                    return true;
////
////                } else {
////                    return false;
////                }
//                Log.d("ZingDemoDraggable", "clicked event");
//                return true;
//
//            }
//        });
//        addBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("ZingDemoDraggable", "clicked event");
//            }
//        });
//        dragLayout.setPlayPauseCustomOnClickListener(new DragYouTubeLayout.PlayPauseCustomOnClickListener() {
//            @Override
//            public void onClick() {
//
//            }
//        });


//        numberProgressBar = findViewById(R.id.progress_bar);
//        if (mediaController == null){
//            mediaController = new MediaController(MainActivity.this);
//            mediaController.setAnchorView(video);
//            Log.d("ZingDemoDraggable", mediaController.setProgre
//
//            video.setMediaController(mediaController);
//
//        }
//        pd = new ProgressDialog(MainActivity.this);
//        pd.setMessage("Buffering video ...");
//        pd.show();
//
//        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample);
//        video.setVideoURI(uri);
//        dragLayout.setVideoView(video);
//        dragLayout.startVideo(pd);
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

    @Override
    public void onAddButtonClicked(){
        VideoFragment videoFragment = VideoFragment.newInstance(urlVideo);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.video_fragment, videoFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onDelButtonClicked(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.video_fragment);
        fragmentManager.beginTransaction().remove(fragment).commit();

    }

}
