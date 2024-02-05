package com.oruphones.nativediagnostic.controller.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.FileInfo;
import com.oruphones.nativediagnostic.communication.api.PDStorageFileInfo;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.ODDCache;

import java.lang.ref.WeakReference;

public class CustomThumbnailView extends LinearLayout {
    private static String TAG = CustomThumbnailView.class.getSimpleName();
    protected ImageView iconPlay;
    private ImageView itemThumbnail;
    private RelativeLayout itemThumbnailContainer;
    private ProgressBar itemThumbnailProgress;
    private Context context;
    private TextView noPreview;
    private ODDCache mODDCache;


    public CustomThumbnailView(Context context) {
        super(context);
        initViews(context);
    }

    public CustomThumbnailView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public CustomThumbnailView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        this.context = context;
        inflate(context, R.layout.custom_thumbnail_view, this);
        mODDCache = ODDCache.getInstance();
        itemThumbnail = findViewById(R.id.itemThumbnail);
        iconPlay = findViewById(R.id.iconPlay);
        noPreview = findViewById(R.id.noPreview);
        itemThumbnailProgress = findViewById(R.id.itemThumbnailProgress);
        itemThumbnailContainer = findViewById(R.id.itemThumbnailContainer);
    }

    public void showPreviewTesT(@NonNull FileInfo fileInfo) {
        Glide.with(this)
                .asBitmap()
                .load(fileInfo.getFilePath())
                .into(itemThumbnail);
    }

    public void showPreview(@NonNull FileInfo fileInfo) {
        DLog.d(TAG, "Video File Path: " + fileInfo.getFilePath());
        noPreview(false);
        if (TextUtils.isEmpty(fileInfo.getFileType()) || fileInfo.getFileSizeInDouble() <= 0) {
            noPreview(true);
            return;
        }
        switch (fileInfo.getFileType()) {
            case PDStorageFileInfo.FILE_TYPE_IMAGE:
                preview(fileInfo.getFilePath(), false);
                break;
            case PDStorageFileInfo.FILE_TYPE_AUDIO:
                itemThumbnailContainer.setVisibility(VISIBLE);
                itemThumbnailProgress.setVisibility(VISIBLE);
                if (mODDCache.getBitmapFromMemCache(fileInfo.getFilePath()) != null) {
                    preview(mODDCache.getBitmapFromMemCache(fileInfo.getFilePath()), true);
                } else {
                    new SetThumb(new OnLoadCompleteListener() {

                        @Override
                        public void onComplete(boolean success) {
                            itemThumbnailProgress.setVisibility(GONE);
                            noPreview(!success);
                        }
                    }, itemThumbnail).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,fileInfo);
                }
                break;
            case PDStorageFileInfo.FILE_TYPE_VIDEO:
                itemThumbnailProgress.setVisibility(VISIBLE);
                preview(fileInfo.getFilePath(), true);
                break;
            case PDStorageFileInfo.FILE_TYPE_OTHER:
            default:
                noPreview(true);
        }
    }


    //View Image
    private void noPreview(boolean show) {
        iconPlay.setVisibility(GONE);
        noPreview.setVisibility(show ? View.VISIBLE : GONE);
        if (show) {
            itemThumbnailContainer.setVisibility(GONE);
        }
    }

    //
    private void preview(Object bmThumbnail, boolean playIconShow) {
        itemThumbnailProgress.setVisibility(GONE);
        try {
            if (bmThumbnail != null) {
                iconPlay.setVisibility(playIconShow ? VISIBLE : GONE);
                itemThumbnailContainer.setVisibility(VISIBLE);
                Glide.with(this)
                        .asBitmap()
                        .load(bmThumbnail)
                        .into(itemThumbnail);

            } else {
                noPreview(true);
            }
        } catch (Exception e) {
            noPreview(true);
        }
    }

    //Audio ThumbNail

    public Bitmap getAudioAlbumImageContentUri(String filePath) {
        try {
            MediaMetadataRetriever metaRetriver = new MediaMetadataRetriever();
            metaRetriver.setDataSource(filePath);
            byte[] art = metaRetriver.getEmbeddedPicture();
            return BitmapFactory.decodeByteArray(art, 0, art.length);
        } catch (Exception exception) {

        }
        return null;
    }

    interface OnLoadCompleteListener {
        void onComplete(boolean success);
    }

    class SetThumb extends AsyncTask<FileInfo, Void, Bitmap> {
        OnLoadCompleteListener mCompleteListener;
        private final WeakReference<ImageView> viewReference;

        public SetThumb(OnLoadCompleteListener completeListener, ImageView _imageView) {
            mCompleteListener = completeListener;
            viewReference = new WeakReference<ImageView>( _imageView );
        }

        @Override
        protected Bitmap doInBackground(FileInfo... fileInfos) {
            FileInfo fileInfo = fileInfos[0];
            if(TextUtils.isEmpty(fileInfo.getFilePath())){
                return null;
            }
            Bitmap bitmap = getAudioAlbumImageContentUri(fileInfo.getFilePath());
            if(bitmap!=null){
                mODDCache.addBitmapToMemoryCache(fileInfo.getFilePath(), bitmap);
            }

            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap bmThumbnail) {
            ImageView imageView = viewReference.get();
            if( imageView != null && bmThumbnail != null ) {
                imageView.setImageBitmap( bmThumbnail );
            }
            mCompleteListener.onComplete(bmThumbnail != null);
        }

    }

}
