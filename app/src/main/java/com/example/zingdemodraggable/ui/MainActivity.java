package com.example.zingdemodraggable.ui;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.example.zingdemodraggable.R;
import com.example.zingdemodraggable.view.DragLayout;
import com.example.zingdemodraggable.view.DragYouTubeLayout;
//import com.example.zingdemodraggable.view.TestDrag;

public class MainActivity extends AppCompatActivity {
    VideoView video;
//    String video_url = "android.resource://" + getPackageName() + "/" + R.raw.sample;
    ProgressDialog pd;

    private DragYouTubeLayout dragLayout;
    private RelativeLayout outerLayout;
//    private TestDrag testDrag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
//        testDrag = findViewById(R.id.test_drag);
        dragLayout = findViewById(R.id.drag_layout);
        outerLayout = findViewById(R.id.outer_layout);
        video = findViewById(R.id.main_image);
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Buffering video ...");
        pd.show();

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample);
        video.setVideoURI(uri);
        video.start();

        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                pd.dismiss();
            }
        });
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
