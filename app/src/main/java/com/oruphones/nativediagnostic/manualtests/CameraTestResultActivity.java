package com.oruphones.nativediagnostic.manualtests;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.Util;

import org.pervacio.onediaglib.utils.CameraUtil;

import java.io.File;

/**
 * Created by Surya Polasanapalli on 23-09-2017.
 */
public class CameraTestResultActivity extends BaseActivity {

    ImageView cameraImagePreview = null;
    private String filePath;
    private String mTestName;
    private String mTestResult;
    private TextView mTestPass, mTestFail, mRetest;
    private static String TAG = CameraTestResultActivity.class.getSimpleName();
    private TextView test_title, testDescrption;
    private VideoView mVideoView;
    //private LinearLayout gifViewLayout;
    //private LinearLayout twoBtnLayouot;
    private LinearLayout threeBtnLayout;
    //GIFMovieView gifMovieView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTestName = getIntent().getStringExtra(TEST_NAME);
        mTestResult = getIntent().getStringExtra(TEST_RESULT);
        cameraImagePreview = (ImageView) findViewById(R.id.imageView);
        /*gifMovieView = getGIFMovieView(getApplicationContext(), mTestName);
        gifViewLayout = (LinearLayout)  findViewById(R.id.camResultGifViewLayout);
        gifViewLayout.addView(gifMovieView);*/
        mTestPass = (Button) findViewById(R.id.accept_tv);
        mTestFail = (Button) findViewById(R.id.cancel_tv);
        mRetest = (Button) findViewById(R.id.retest_tv);
        test_title = (TextView) findViewById(R.id.test_name);
        testDescrption = (TextView) findViewById(R.id.test_description);
        //twoBtnLayouot = (LinearLayout) findViewById(R.id.continue_ll);
        threeBtnLayout = (LinearLayout) findViewById(R.id.continue_3b);
        if(mTestName.equalsIgnoreCase(TestName.REARCAMERAPICTURETEST)
        ||mTestName.equalsIgnoreCase(TestName.FRONTCAMERAPICTURETEST)){
            threeBtnLayout.setVisibility(View.GONE);
        }
        if (getIntent().hasExtra("path")) {
            filePath = getIntent().getStringExtra("path");
        }
        mTestFail.setText(getResources().getString(R.string.str_no));
        mTestPass.setText(getResources().getString(R.string.str_yes));
        mRetest.setText(R.string.btn_retest);
        if(Util.isAdvancedTestFlow()) {
            test_title.setVisibility(View.GONE);
        }
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                float screenRatio = mVideoView.getWidth() / (float)
                        mVideoView.getHeight();
                float scaleX = videoRatio / screenRatio;
                if (scaleX >= 1f) {
                    mVideoView.setScaleX(scaleX);
                } else {
                    mVideoView.setScaleY(1f / scaleX);
                }
            }
        });
        if (mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST) ||
                mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST1) ||
                mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST2) ||
                mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST3) ||
                mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST4) ||
                mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST5) ||
                mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST6) ||

                mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST) ||
                mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST1) ||
                mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST2) ||
                mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST3) ||
                mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST4) ||
                mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST5) ||
                mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST6)) {
            mVideoView.setVisibility(View.VISIBLE);
            //gifMovieView.setVisibility(View.GONE);
            playVideo();
        } else {
            if(mTestName.equalsIgnoreCase(TestName.REARCAMERAPICTURETEST)
            ||mTestName.equalsIgnoreCase(TestName.FRONTCAMERAPICTURETEST)){
                String result = mTestResult;
                if (filePath != null) {
                    cameraFolderDelete(filePath);
                }
                manualTestResultDialog(mTestName, result,false, true, CameraTestResultActivity.this);
            }else {
                setPreview();
            }
        }
        test_title.setText(getDisplayName(mTestName));
        testDescrption.setText(getResourceID(mTestName, TEST_RESULT_MESAGE));

        mRetest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTestPass.setVisibility(View.GONE);
                mTestFail.setVisibility(View.GONE);
                mRetest.setVisibility(View.GONE);
                if (filePath != null) {
                    cameraFolderDelete(filePath);
                }
                restartCurrentTest();
            }
        });
        mTestPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTestPass.setVisibility(View.GONE);
                mTestFail.setVisibility(View.GONE);
                mRetest.setVisibility(View.GONE);
                String result = TestResult.PASS;
                if (filePath != null) {
                    cameraFolderDelete(filePath);
                }

                if (mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST) ||
                        mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST1) ||
                        mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST2) ||
                        mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST3) ||
                        mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST4) ||
                        mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST5) ||
                        mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST6)){
                    globalConfig.addItemToList("Rear Camera video test User Input: YES");
                } else if (mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST) ||
                        mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST1) ||
                        mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST2) ||
                        mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST3) ||
                        mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST4) ||
                        mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST5) ||
                        mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST6)) {
                    globalConfig.addItemToList("Front Camera video test User Input: YES");
                }
                manualTestResultDialog(mTestName, result,false, true, CameraTestResultActivity.this);

            }
        });
        mTestFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTestPass.setVisibility(View.GONE);
                mTestFail.setVisibility(View.GONE);
                mRetest.setVisibility(View.GONE);
                String result = TestResult.FAIL;
                if (filePath != null) {
                    cameraFolderDelete(filePath);
                }
                if (mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST) ||
                        mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST1) ||
                        mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST2) ||
                        mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST3) ||
                        mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST4) ||
                        mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST5) ||
                        mTestName.equalsIgnoreCase(TestName.REARCAMERAVIDEOTEST6)){

                    globalConfig.addItemToList("Rear Camera video test User Input: NO");
                } else if (mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST) ||
                        mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST1) ||
                        mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST2) ||
                        mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST3) ||
                        mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST4) ||
                        mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST5) ||
                        mTestName.equalsIgnoreCase(TestName.FRONTCAMERAVIDEOTEST6)) {

                    globalConfig.addItemToList("Front Camera video test User Input: YES");
                }


                manualTestResultDialog(mTestName, result,false, true, CameraTestResultActivity.this);
            }
        });
        setFontToView(mTestFail,OPENSANS_MEDIUM);
        setFontToView(mTestPass,OPENSANS_MEDIUM);
        setFontToView(testDescrption,OPENSANS_LIGHT);
        setFontToView(test_title,OPENSANS_LIGHT);
        sendManualTestResultToServer(mTestName,"COMPLETED");
    }



    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.camera_result;
    }

    @Override
    protected String getToolBarName() {
        return getDisplayName(getIntent().getStringExtra(TEST_NAME));
    }

    @Override
    protected boolean setBackButton() {
        return true;
    }



    private void setPreview() {
        Bitmap mReceiveImagebitmap = null;
        if ((filePath != null) && (!"".equals(filePath))) {
            DLog.d(TAG, "Picture File Path: " + filePath);
            mReceiveImagebitmap = BitmapFactory.decodeFile(filePath);
        } else {
            DLog.d(TAG, "filePath is null");
            mReceiveImagebitmap = CameraUtil.getCapturedBitmap();
        }
            try {
                if(mReceiveImagebitmap != null) {
                    //gifMovieView.setVisibility(View.GONE);
                    cameraImagePreview.setImageBitmap(mReceiveImagebitmap);
                    cameraImagePreview.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                DLog.e(TAG, e.getMessage());
            }

    }


    public void playVideo() {
        if (mVideoView != null) {
            try {
                DLog.d(TAG, "Video File Path: "+filePath);
                mVideoView.setVideoPath(filePath);
                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mVideoView.start();
                    }
                });
                mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (mVideoView != null)
                            mVideoView.start();
                    }
                });
            } catch (Exception e) {
                DLog.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if(filePath!=null)
            cameraFolderDelete(filePath);*/
    }
    private void cameraFolderDelete(String filepath)
    {
        try {
            File dir = new File(filepath);
            deleteDir(dir.getParentFile());
        } catch (Exception e) {
            DLog.d(TAG, "Exception:" + e.getMessage());
        }
    }


    public static boolean deleteDir(File dir) {

        if (dir.isDirectory()) {

            try {
                String[] children = dir.list();
                if (children.length > 0) {
                    for (int i = 0; i < children.length; i++) {
                        boolean success = deleteDir(new File(dir, children[i]));
                        //RearCameraPrerviewActivity
                        DLog.d(TAG, "File:" + success);
                        if (!success) {
                            return false;
                        }

                    }
                }
            } catch (Exception e) {
                DLog.e(TAG, "Exception:" + e.getMessage());
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }
}
