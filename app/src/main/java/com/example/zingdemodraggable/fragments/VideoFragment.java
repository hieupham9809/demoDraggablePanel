package com.example.zingdemodraggable.fragments;


import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.example.zingdemodraggable.R;
import com.example.zingdemodraggable.datamodel.Video;
import com.example.zingdemodraggable.view.DragYouTubeLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private Video video;
    private VideoView videoView;
    private DragYouTubeLayout dragLayout;

    private ProgressDialog pd;

    // TODO: Rename and change types of parameters
    private String mUrl;


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
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        videoView = view.findViewById(R.id.main_image);
        dragLayout = view.findViewById(R.id.drag_layout);
        dragLayout.bringToFront();
        dragLayout.invalidate();
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Buffering video ...");
        pd.show();

        Uri uri = Uri.parse(mUrl);
        videoView.setVideoURI(uri);
        dragLayout.setVideoView(videoView);
        dragLayout.startVideo(pd);



        return view;
    }

}
