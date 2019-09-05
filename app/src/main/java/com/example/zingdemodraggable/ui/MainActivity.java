package com.example.zingdemodraggable.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.zingdemodraggable.R;
import com.example.zingdemodraggable.fragments.ListFragment;
import com.example.zingdemodraggable.fragments.VideoFragment;

public class MainActivity extends AppCompatActivity implements ListFragment.OnListFragmentListener {
    String urlVideo = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

    }

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
