package com.example.zingdemodraggable.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zingdemodraggable.R;
import com.example.zingdemodraggable.datamodel.Video;
import com.example.zingdemodraggable.view.ControllerVideo;
import com.example.zingdemodraggable.view.DragYouTubeLayout;
import com.example.zingdemodraggable.view.NumberProgressBar;
import com.example.zingdemodraggable.view.VideoCustomView;

import java.lang.ref.WeakReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private static final int SHOW_PROGRESS = 1;

    private Video video;
    private VideoCustomView videoView;
    private DragYouTubeLayout dragLayout;
    private VideoFragment videoFragment = this;
    private ProgressDialog pd;
    private ControllerVideo controllerVideo;

    private NumberProgressBar numberProgressBar;
    // TODO: Rename and change types of parameters
    private String mUrl;

    private ExtendsHandler extendsHandler;

    private Bitmap imageThumbnail;

    private Boolean isLayoutChanged = false;

    Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            controllerVideo.setVisibility(View.INVISIBLE);
        }
    };

    protected static class ExtendsHandler extends Handler {
        private final WeakReference<VideoFragment> videoFragmentWeakReference;

        public ExtendsHandler(VideoFragment videoFragment) {
            videoFragmentWeakReference = new WeakReference<>(videoFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            int pos;
            switch (msg.what) {

                case SHOW_PROGRESS:
                    pos = videoFragmentWeakReference.get().setProgress();
                    if (videoFragmentWeakReference.get().videoView.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;

            }
        }
    }

    public int setProgress() {
        int position = videoView.getCurrentPosition();
        int duration = videoView.getDuration();

        if (numberProgressBar != null) {
            if (duration > 0) {
                numberProgressBar.setProgress(position);

            }

        }

        return position;
    }


    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url Parameter 1.
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(String url) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();

        args.putString(ARG_PARAM1, url);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageThumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.small_thumbnail);

        video = new Video();
        video.setEpisode(22);
        video.setFullName("Xuân Hoa Thu Nguyệt - Tập 22");
        video.setReleaseDay("26/07/2019");
        if (getArguments() != null) {
            mUrl = getArguments().getString(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_video, container, false);
        videoView = view.findViewById(R.id.main_image);

        controllerVideo = view.findViewById(R.id.front_layout);
        videoView.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (!isLayoutChanged) {
                    int action = event.getAction();
                    if (action == MotionEvent.ACTION_UP) {

                        if (controllerVideo.getVisibility() == View.INVISIBLE) {
                            controllerVideo.setVisibility(View.VISIBLE);
                            final ImageButton bigPlayPauseButton = view.findViewById(R.id.big_play_pause_button);
                            final ImageButton bigNextButton = view.findViewById(R.id.big_next_button);
                            final ImageButton bigPreviousButton = view.findViewById(R.id.big_previous_button);

                            bigNextButton.setImageResource(R.drawable.next_button);
                            bigPreviousButton.setImageResource(R.drawable.previous_button);
                            if (videoView.isPlaying()) {
                                bigPlayPauseButton.setImageResource(R.drawable.big_pause);
                            } else {
                                bigPlayPauseButton.setImageResource(R.drawable.big_play);
                            }
                            bigPlayPauseButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mHandler.removeCallbacks(mRunnable);

                                    mHandler.postDelayed(mRunnable, 2000);
                                    if (videoView.isPlaying()) {
                                        videoView.pause();
                                        bigPlayPauseButton.setImageResource(R.drawable.big_play);

                                    } else {
                                        videoView.start();
                                        bigPlayPauseButton.setImageResource(R.drawable.big_pause);
                                        extendsHandler.sendEmptyMessage(SHOW_PROGRESS);

                                    }
                                }
                            });


                            mHandler.removeCallbacks(mRunnable);

                            mHandler.postDelayed(mRunnable, 2000);
                        } else {
                            controllerVideo.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                return true;
            }

        });

        dragLayout = view.findViewById(R.id.drag_layout);

        numberProgressBar = (dragLayout.getEnableProgressBar()) ? (NumberProgressBar) view.findViewById(R.id.progress_bar) : null;
        dragLayout.setOnDragViewChangeListener(new DragYouTubeLayout.OnDragViewChangeListener() {
            @Override
            public void onMiniViewReplaced() {
                TextView fullName = view.findViewById(R.id.full_name);
                TextView episode = view.findViewById(R.id.episode);
                TextView releaseDay = view.findViewById(R.id.releaseDay);
                ImageView thumbnail = view.findViewById(R.id.thumbnail);

                fullName.setText(video.getFullName());
                episode.setText(String.format("Tập: %s", video.getEpisode()));
                releaseDay.setText(String.format("Ngày ra mắt: %s", video.getReleaseDay()));
                thumbnail.setImageBitmap(imageThumbnail);

            }

            @Override
            public void onSecondViewReplaced() {
//                miniInfo = findViewById(R.id.mini_info);
                TextView miniTitle = view.findViewById(R.id.mini_title);
                TextView miniEpisode = view.findViewById(R.id.mini_episode);
                final ImageButton playPauseButton = view.findViewById(R.id.play_pause_button);

                if (videoView.isPlaying()) {
                    playPauseButton.setImageResource(R.drawable.pause_icon);

                } else {
                    playPauseButton.setImageResource(R.drawable.play_icon);
                }
                playPauseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        listener.onClick();
                        if (videoView.isPlaying()) {
                            videoView.pause();
                            playPauseButton.setImageResource(R.drawable.play_icon);

                        } else {
                            videoView.start();
                            playPauseButton.setImageResource(R.drawable.pause_icon);
                            extendsHandler.sendEmptyMessage(SHOW_PROGRESS);

                        }
                    }
                });

                miniTitle.setText(video.getFullName());
                miniEpisode.setText(String.format("Tập: %s", video.getEpisode()));

            }

            @Override
            public void onLayoutChanged() {
                if (isLayoutChanged) {
                    return;
                }
                isLayoutChanged = true;
                controllerVideo.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onLayoutFlattened() {
                isLayoutChanged = false;
            }
        });
        dragLayout.bringToFront();
        dragLayout.invalidate();

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Buffering video ...");
        pd.show();

        videoView.setVideoPath(mUrl);
        extendsHandler = new ExtendsHandler(videoFragment);

        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                pd.dismiss();

                if (numberProgressBar != null) {
                    numberProgressBar.setMax(videoView.getDuration());
                }
                extendsHandler.sendEmptyMessage(SHOW_PROGRESS);

            }
        });

        return view;
    }

}
