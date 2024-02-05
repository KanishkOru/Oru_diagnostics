package com.oruphones.nativediagnostic.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.FileInfo;
import com.oruphones.nativediagnostic.communication.api.PDStorageFileInfo;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


import java.util.List;

public class ShowCustomFilePreview extends LinearLayout {
    private static String TAG = ShowCustomFilePreview.class.getSimpleName();
    protected ImageView showPreviewClose;

    private SimpleExoPlayer mExoPlayer;
    private TextView noPreview;
    private Context context;
    private PlayerView mPlayerView;
    private ImageView showPreviewImageView;
    private ProgressBar progressBar;
    private boolean playWhenReady;


    public ShowCustomFilePreview(Context context) {
        super(context);
        initViews( context);
    }

    public ShowCustomFilePreview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public ShowCustomFilePreview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        this.context = context;
        inflate(context,R.layout.show_custom_file_preview,this);
        mPlayerView = findViewById(R.id.showPreviewVideo);
        showPreviewImageView = findViewById(R.id.showPreviewImageView);
        progressBar = findViewById(R.id.progressBar);
        noPreview = findViewById(R.id.noPreview);
        showPreviewClose = findViewById(R.id.showPreviewClose);
        showPreviewClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*onBackPressed();*/
            }
        });



    }

    public void showPreview(@NonNull FileInfo fileInfo){
        DLog.d(TAG, "Video File Path: " + fileInfo.getFilePath());
        noPreview(false);
        if (TextUtils.isEmpty(fileInfo.getFileType()) || fileInfo.getFileSizeInDouble() <= 0){
            noPreview(true);
            return;
        }




        switch (fileInfo.getFileType()) {
            case PDStorageFileInfo.FILE_TYPE_IMAGE:
                previewImage(fileInfo.getFilePath());
                break;
            case PDStorageFileInfo.FILE_TYPE_AUDIO:
            case PDStorageFileInfo.FILE_TYPE_VIDEO:
                previewVideo(fileInfo.getFilePath());
                break;
            case PDStorageFileInfo.FILE_TYPE_OTHER:
            default:
                noPreview(true);
        }

    }

    public void setonCloseListener(@NonNull  OnClickListener clickListener){
        if(showPreviewClose!=null){
            showPreviewClose.setOnClickListener(clickListener);
        }
    }

    private MediaSource buildMediaSource(Uri uri, String type) {
        return new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(context, "exoplayer-sample"))
                .createMediaSource(MediaItem.fromUri(uri));
    }


    private void initPlayer(String filePath){
        mExoPlayer = new SimpleExoPlayer.Builder(context).build();
        MediaSource mediaSource = buildMediaSource(Uri.parse(filePath), "type");
        mExoPlayer.setMediaSource(mediaSource);
        //mExoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);


        mExoPlayer.prepare();
        mExoPlayer.setPlayWhenReady(isPlayWhenReady());
        if(!isPlayWhenReady()){
            setPlayWhenReady(true);
        }
        mExoPlayer.addListener(new Player.Listener() {

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_BUFFERING)
                    progressBar.setVisibility(View.VISIBLE);
                else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
                    progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCues(List<Cue> cues) {

            }

            @Override
            public void onMetadata(Metadata metadata) {

            }
        });

    }

    //
    //VIDEO VIEW
    private void previewVideo(String filePath) {
        releaseResources();
        showPreviewImageView.setVisibility(View.GONE);
        initPlayer(filePath);
        mPlayerView.setPlayer(mExoPlayer);
        mPlayerView.setVisibility(View.VISIBLE);
    }


    //View Image
    private void noPreview(boolean show) {
        noPreview.setVisibility(show?View.VISIBLE:GONE);
    }

    private void previewImage(String filePath) {
        releaseResources();
        mPlayerView.setVisibility(View.GONE);
        //final ImageView showPreviewImageView = findViewById(R.id.showPreviewImageView);
        showPreviewImageView.setVisibility(View.VISIBLE);
        if ((filePath != null) && (!"".equals(filePath))) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(filePath);
            try {
                showPreviewImageView.setImageBitmap(imageBitmap);

            } catch (Exception e) {
                DLog.d(TAG, e.getMessage());
            }
        }
    }

    public void releaseResources() {
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
    }


    /*Play When Ready */

    public boolean isPlayWhenReady() {
        return playWhenReady;
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.playWhenReady = playWhenReady;
    }
}
