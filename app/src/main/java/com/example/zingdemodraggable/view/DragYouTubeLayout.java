package com.example.zingdemodraggable.view;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.zingdemodraggable.R;
import com.example.zingdemodraggable.datamodel.Video;
import com.example.zingdemodraggable.ui.MainActivity;

import java.lang.ref.WeakReference;

public class DragYouTubeLayout extends RelativeLayout {
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;
    private static final int SHOW_PROGRESS = 1;
    private DragYouTubeLayout dragYouTubeLayout = this;
    private final int BOTTOM_LEFT = 1;
    private final int BOTTOM_CENTER = 2;
    private final int BOTTOM_RIGHT = 3;

    private int bottomMode = BOTTOM_RIGHT;
    private int finalLeft;

    private Context context;
    private int mDraggingState = 0;
    //    private Button mQueenButton;
    private ViewDragHelper mDragHelper;
    private int mDraggingBorder;
    private int mVerticalRange = 0;
    private boolean mIsOpen;

    private LinearLayout wrapVideo;
    private VideoView mMainImage;
    private RelativeLayout mInfoPanel;

    private NumberProgressBar numberProgressBar;
    private RelativeLayout miniInfo;

    private Video video;
    private VideoView videoView;

    private TextView fullName;
    private TextView episode;
    private TextView releaseDay;
    private ImageView thumbnail;

    private TextView miniTitle;
    private TextView miniEpisode;
    private ImageButton playPauseButton;

    private ViewGroup parent;

    private float mDragOffset;

    private boolean isRelease = false;
    private Animation animation;

    private int currentDrag = 0;

    private final float MIN_SCALE = 0.5f;
    private final float MIN_SCALE_COLLAPSE = 0.3f;
    private final float WIDTH_SCALE = 0.35f;
    private final int WIDTH_SCALE_RANGE = 150;

    private final float TRANSFORM_POINT = 0.85f;
    private float scaleView = 1f;
    private float deltaScaleView = 0f;
    private PlayPauseCustomOnClickListener listener;
    private ExtendsHandler extendsHandler;

    private Bitmap imageThumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.small_thumbnail);

    public void setVideoView(VideoView videoView) {
        this.videoView = videoView;
        extendsHandler = new ExtendsHandler(dragYouTubeLayout);

    }
    public void startVideo(final ProgressDialog pd){
        if (videoView == null){
            return;
        }
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                pd.dismiss();
//                Log.d("ZingDemoDraggable", "video duration "+ video.getDuration());
                numberProgressBar.setMax(videoView.getDuration());

                extendsHandler.sendEmptyMessage(SHOW_PROGRESS);

            }
        });
    }
    protected static class ExtendsHandler extends Handler
    {
        private final WeakReference<DragYouTubeLayout> dragYouTubeLayoutWeakReference;
        public ExtendsHandler(DragYouTubeLayout dragYouTubeLayout){
            dragYouTubeLayoutWeakReference = new WeakReference<>(dragYouTubeLayout);
        }

        @Override
        public void handleMessage(Message msg)
        {
            int pos;
            switch (msg.what)
            {
                // ...

                case SHOW_PROGRESS:
                    pos = dragYouTubeLayoutWeakReference.get().setProgress();
                    if ( dragYouTubeLayoutWeakReference.get().videoView.isPlaying())
                    {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;

                // ...
            }
        }
    };
    public int setProgress() {
//        if (mPlayer == null || mDragging) {
//            return 0;
//        }
        int position = videoView.getCurrentPosition();
        int duration = videoView.getDuration();
//        Log.d("ZingDemoDraggable", "current position "+ position);

//        Log.d("ZingDemoDraggable", "video duration "+ duration);
        if (numberProgressBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
//                long pos = 100L * position / duration;
//                numberProgressBar.setProgress( (int) pos);
                numberProgressBar.setProgress(position);

//                Log.d("ZingDemoDraggable", "set position " + pos);
            }

        }

        return position;
    }
    public interface PlayPauseCustomOnClickListener {
        void onClick();
    }
    public void setPlayPauseCustomOnClickListener(PlayPauseCustomOnClickListener listener){
        this.listener = listener;
    }
    public class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public void onViewDragStateChanged(int state) {
            if (state == mDraggingState) { // no change
                return;
            }
            if ((mDraggingState == ViewDragHelper.STATE_DRAGGING || mDraggingState == ViewDragHelper.STATE_SETTLING) &&
                    state == ViewDragHelper.STATE_IDLE) {
                // the view stopped from moving.

                if (mDraggingBorder == 0) {
                    onStopDraggingToClosed();
                } else if (mDraggingBorder == mVerticalRange) {
                    mIsOpen = true;
                }
            }
            if (state == ViewDragHelper.STATE_DRAGGING) {
                onStartDragging();
            }
            mDraggingState = state;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mDraggingBorder = top;

            mDragOffset = (float) top / mVerticalRange;

            scaleView = 1 - mDragOffset / (1 / (1 - MIN_SCALE));
            mMainImage.setPivotX(0);
            mMainImage.setPivotY(0);
//            deltaScaleView = 1 - scaleView;

//                mMainImage.setPivotX(mMainImage.getWidth());
//                mMainImage.setPivotY(0);
//                mMainImage.setScaleX(1 - mDragOffset / 2);
//                mMainImage.setScaleY(1 - mDragOffset / 2);

            if (isRelease && mDraggingBorder >= mVerticalRange && currentDrag < mVerticalRange) {
                wrapVideo.startAnimation(animation);
//                mInfoPanel.startAnimation(animation);
                isRelease = false;
                currentDrag = mVerticalRange + WIDTH_SCALE_RANGE;
//                Log.d("ZingDemoDraggable", " ati onviewpositionchanged animation actived, duration " );
            }
            if (mDragOffset <= TRANSFORM_POINT) {
                mInfoPanel.setAlpha((float) ((4 / 2.89) * mDragOffset * mDragOffset - (4 / 1.7) * mDragOffset + 1));
            } else {
                mInfoPanel.setAlpha((float) ((4 / 0.09) * mDragOffset * mDragOffset - (6.8 / 0.09) * mDragOffset + 2.89 / 0.09));
            }

            if (mDraggingBorder == 0 && currentDrag != 0) {
                currentDrag = 0;
            }
            if (mDraggingBorder == mVerticalRange && currentDrag < mVerticalRange) {
                currentDrag = mVerticalRange;
            }
//            mInfoPanel.setAlpha((float)((1/0.1275)*mDragOffset*mDragOffset - (1/0.1275) * mDragOffset + 1));

            requestLayout();


        }

        public int getViewVerticalDragRange(View child) {
            return mVerticalRange;
        }

        @Override
        public boolean tryCaptureView(View view, int i) {
            return (view.getId() == R.id.video_wrap);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
//            Log.d("ZingDemoDraggable", "vertical");

            currentDrag += dy;
            final int topBound = getPaddingTop();
            final int bottomBound = mVerticalRange;
            if (currentDrag < mVerticalRange) {
                return Math.min(Math.max(top, topBound), bottomBound);
            } else {
                return mVerticalRange;
            }
        }

        //        @Override
//        public int clampViewPositionHorizontal(View child, int left, int dx) {
//            final int leftBound = getPaddingLeft();
//            final int rightBound = getWidth() - mMainImage.getMeasuredWidth();
////            Log.d("ZingDemoDraggable", "left bound " + leftBound + "right bound " + rightBound);
//
//            int newLeft = Math.min(Math.max(left, leftBound), rightBound);
//
//            return newLeft;
//        }
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final float rangeToCheck = mVerticalRange;
            if (mDraggingBorder == 0) {
                mIsOpen = false;
                return;
            }
            if (mDraggingBorder == rangeToCheck) {
                mIsOpen = true;
//                return;
            }
            boolean settleToOpen = false;
            if (yvel > AUTO_OPEN_SPEED_LIMIT) { // speed has priority over position
                settleToOpen = true;
            } else if (yvel < -AUTO_OPEN_SPEED_LIMIT) {
                settleToOpen = false;
            } else if (mDraggingBorder > rangeToCheck / 2) {
                settleToOpen = true;
            } else if (mDraggingBorder < rangeToCheck / 2) {
                settleToOpen = false;
            }

            final int settleDestY = settleToOpen ? mVerticalRange : 0;

            if (settleToOpen) {
                isRelease = true;
            }

            final int initWidth = wrapVideo.getMeasuredWidth();
            float vel = (yvel > 3000f) ? yvel : 3000f;
            float lowerBound = 0;
            if (mDraggingBorder >= mVerticalRange) {

                lowerBound = currentDrag;

            }
            final float lowerBoundConstant = lowerBound;

            animation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {

                    currentDrag = Math.round(interpolatedTime * WIDTH_SCALE_RANGE + mVerticalRange );
                    if (currentDrag >= lowerBoundConstant) {
                        requestLayout();
                    }
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };
            animation.setDuration((int) ((initWidth * (1 - WIDTH_SCALE) * 1f * 1000 / Math.abs(vel))));

            if (mDraggingBorder >= mVerticalRange) {
                currentDrag = mVerticalRange + WIDTH_SCALE_RANGE;

                wrapVideo.startAnimation(animation);
//                mInfoPanel.startAnimation(animation);

//                isRelease = false;
//                Log.d("ZingDemoDraggable", "animation actived ");
                return;
            }
            if (mDragHelper.settleCapturedViewAt(0, settleDestY)) {

//                requestLayout();
                ViewCompat.postInvalidateOnAnimation(DragYouTubeLayout.this);
            }

        }
    }

    public DragYouTubeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
        video = new Video();
        video.setEpisode(22);
        video.setFullName("Xuân Hoa Thu Nguyệt - Tập 22");
        video.setReleaseDay("26/07/2019");
        mIsOpen = false;
    }

    @Override
    protected void onFinishInflate() {
//        mQueenButton  = (Button) findViewById(R.id.queen_button);
        wrapVideo = findViewById(R.id.video_wrap);
        mMainImage = findViewById(R.id.main_image);
        mInfoPanel = findViewById(R.id.info_layout);
        fullName = findViewById(R.id.full_name);
        episode = findViewById(R.id.episode);
        releaseDay = findViewById(R.id.releaseDay);
        thumbnail = findViewById(R.id.thumbnail);
        numberProgressBar = findViewById(R.id.progress_bar);


        mIsOpen = false;
        super.onFinishInflate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        Log.d("ZingDemoDraggable", "onSizeChanged called");
//        mVerticalRange = (int) (h * 0.66);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        Log.d("ZingDemoDraggable", "onLayout called " );

//        mVerticalRange = 1 - width * 9 / 16;
        if (mVerticalRange == 0){
        mVerticalRange = Math.round(getHeight() - getWidth() * 9f / 16 * MIN_SCALE);
        }




//        if ((float) mDraggingBorder / mVerticalRange > TRANSFORM_POINT) {
        if (currentDrag >= mVerticalRange) {
//            Log.d("ZingDemoDraggable", "final call " + currentDrag);

            // Replace view info
            if (mInfoPanel.getId() != R.id.mini_info) {
                parent = (ViewGroup) mInfoPanel.getParent();
                int index = parent.indexOfChild(mInfoPanel);
                parent.removeView(mInfoPanel);
                mInfoPanel = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.minimize_info, parent, false);
                parent.addView(mInfoPanel, index);

                miniInfo = findViewById(R.id.mini_info);
                miniTitle = findViewById(R.id.mini_title);
                miniEpisode = findViewById(R.id.mini_episode);
                playPauseButton = findViewById(R.id.play_pause_button);
                if (videoView.isPlaying()){
                    playPauseButton.setImageResource(R.drawable.pause_icon);
                } else {
                    playPauseButton.setImageResource(R.drawable.play_icon);
                }
                playPauseButton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
//                        listener.onClick();
                        if(videoView.isPlaying()){
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
            if (currentDrag > mVerticalRange + WIDTH_SCALE_RANGE) {
                currentDrag = mVerticalRange + WIDTH_SCALE_RANGE;
            }
//            Log.d("ZingDemoDraggable", "measured height " + wrapVideo.getMeasuredWidth());

            int originalVideoHeight = wrapVideo.getMeasuredWidth() * 9 / 16;
//            Log.d("ZingDemoDraggable", "top onLayout" + Math.round(currentDrag * originalVideoHeight *(MIN_SCALE - MIN_SCALE_COLLAPSE) * 1f / WIDTH_SCALE_RANGE + mVerticalRange - mVerticalRange * (MIN_SCALE - MIN_SCALE_COLLAPSE) * originalVideoHeight * 1f / WIDTH_SCALE_RANGE));
            wrapVideo.layout(
                    0,
                    Math.round(currentDrag * originalVideoHeight *(MIN_SCALE - MIN_SCALE_COLLAPSE) * 1f / WIDTH_SCALE_RANGE + mVerticalRange - mVerticalRange * (MIN_SCALE - MIN_SCALE_COLLAPSE) * originalVideoHeight * 1f / WIDTH_SCALE_RANGE),
                    Math.round(right * ((WIDTH_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (WIDTH_SCALE - 1) * 1f / WIDTH_SCALE_RANGE)),
                    bottom

            );

//                Log.d("ZingDemoDraggable", "scale " + ((MIN_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (MIN_SCALE - 1) * 1f / WIDTH_SCALE_RANGE));
            mMainImage.setScaleX(((MIN_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (MIN_SCALE - 1) * 1f / WIDTH_SCALE_RANGE) );
            mMainImage.setScaleY(((MIN_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (MIN_SCALE - 1) * 1f / WIDTH_SCALE_RANGE) );

            mInfoPanel.layout(
                    Math.round(right * ((WIDTH_SCALE - 1) * currentDrag * 1f / WIDTH_SCALE_RANGE + 1 - mVerticalRange * (WIDTH_SCALE - 1) * 1f / WIDTH_SCALE_RANGE)),
                    Math.round(currentDrag * originalVideoHeight *(MIN_SCALE - MIN_SCALE_COLLAPSE) * 1f / WIDTH_SCALE_RANGE + mVerticalRange - mVerticalRange * (MIN_SCALE - MIN_SCALE_COLLAPSE) * originalVideoHeight * 1f / WIDTH_SCALE_RANGE),
                    right,
                    bottom
            );


        } else {
            if (mInfoPanel.getId() != R.id.info_layout) {
                parent = (ViewGroup) mInfoPanel.getParent();
                int index = parent.indexOfChild(mInfoPanel);
                parent.removeView(mInfoPanel);
                mInfoPanel = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.full_layout_info, parent, false);
                parent.addView(mInfoPanel, index);

                fullName = findViewById(R.id.full_name);
                episode = findViewById(R.id.episode);
                releaseDay = findViewById(R.id.releaseDay);
                thumbnail = findViewById(R.id.thumbnail);

                fullName.setText(video.getFullName());
                episode.setText(String.format("Tập: %s", video.getEpisode()));
                releaseDay.setText(String.format("Ngày ra mắt: %s", video.getReleaseDay()));
                thumbnail.setImageBitmap(imageThumbnail);
            }
            mMainImage.setScaleX(1.0f);
            mMainImage.setScaleY(1.0f);
            wrapVideo.layout(
                    0,
                    mDraggingBorder,
                    right,
                    mDraggingBorder + wrapVideo.getMeasuredHeight()

            );

            mInfoPanel.layout(
                    0,
                    mDraggingBorder + wrapVideo.getMeasuredHeight(),
                    right,
                    bottom
            );
        }
        numberProgressBar.layout(
                0,
                wrapVideo.getBottom() - numberProgressBar.getMeasuredHeight(),
                numberProgressBar.getMeasuredWidth(),
                wrapVideo.getBottom()
        );

    }


    private void onStopDraggingToClosed() {
        // To be implemented
    }

    private void onStartDragging() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        fullName.setText(video.getFullName());
        episode.setText(String.format("Tập: %s", video.getEpisode()));
        releaseDay.setText(String.format("Ngày ra mắt: %s", video.getReleaseDay()));
        thumbnail.setImageBitmap(imageThumbnail);

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        final int action = ev.getAction();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        boolean should = mDragHelper.shouldInterceptTouchEvent(ev);
        Log.d("ZingDemoDraggable", "onInterceptTouchEvent " + should);
        return should;

//        return mDragHelper.shouldInterceptTouchEvent(ev);
//        return false;
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
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d("ZingDemoDraggable", "onTouchEvent");
        mDragHelper.processTouchEvent(ev);
        boolean isDragViewHit = isViewHit(wrapVideo, (int) ev.getX(), (int) ev.getY());

        return isDragViewHit;
    }

    @Override
    public void computeScroll() { // needed for automatic settling.
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int childWidthMeasureSpec;
        int childHeightMeasureSpec;
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        float collapseScale = 0;
        if (currentDrag >= mVerticalRange){
            if (currentDrag <= mVerticalRange + WIDTH_SCALE_RANGE){
                collapseScale = currentDrag * (MIN_SCALE - MIN_SCALE_COLLAPSE) * 1f / WIDTH_SCALE_RANGE + mVerticalRange * (MIN_SCALE_COLLAPSE - MIN_SCALE) * 1f / WIDTH_SCALE_RANGE;
            } else {
                collapseScale = MIN_SCALE - MIN_SCALE_COLLAPSE;
            }
        } else {
            collapseScale = 0;
        }
//        Log.d("ZingDemoDraggable", "scale " + (scaleView - collapseScale));

//        Log.d("ZingDemoDraggable", "collapse scale "+ collapseScale);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
//            if (child.getId() == R.id.main_image ) {
            switch (child.getId()){
                case R.id.video_wrap:
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth()), MeasureSpec.EXACTLY);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() * 1f * 9 / 16 * (scaleView - collapseScale)), MeasureSpec.EXACTLY);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                    break;
                case R.id.info_layout:
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth()), MeasureSpec.EXACTLY);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() * scaleView), MeasureSpec.EXACTLY);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                    break;
                case R.id.mini_info:
                    childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth() * (1 - WIDTH_SCALE) ), MeasureSpec.EXACTLY);
                    childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(child.getMeasuredWidth()* 1f * 9 / 16 * (scaleView - collapseScale)), MeasureSpec.EXACTLY);
                    child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                    break;
                default:
                    break;

            }


        }
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
//        setMeasuredDimension(parentWidth, parentHeight);
    }

    public boolean isMoving() {
        return (mDraggingState == ViewDragHelper.STATE_DRAGGING ||
                mDraggingState == ViewDragHelper.STATE_SETTLING);
    }

    public boolean isOpen() {
        return mIsOpen;
    }
}
