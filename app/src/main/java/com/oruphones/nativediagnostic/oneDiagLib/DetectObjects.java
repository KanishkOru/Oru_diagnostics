package com.oruphones.nativediagnostic.oneDiagLib;


import static com.oruphones.nativediagnostic.BaseActivity.context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.manualtests.CameraPictureTestActivity;

import org.pervacio.onediaglib.advancedtest.cloudapis.ObjectDetectionResult;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DetectObjects {
    public static final String OBJECT_FACE = "FACE";
    public static final String OBJECT_HAND = "HAND";
    public static final String OBJECT_DETECT = "OBJECT_DETECT";
    private static final String TAG = "DetectObjects";
    private boolean enableFaceDetection = false;
    private ArrayList<String> handObjectsList = new ArrayList<>(Arrays.asList("Hand", "Finger", "Gesture", "Thumb", "Nail", "Wrist"));
    private ArrayList<String> faceObjectsList = new ArrayList<>(Arrays.asList("Forehead", "Head", "Cheek,", "Eyebrow", "Mouth", "Eyelash", "Selfie", "Beard", "Neck", "Jaw", "Chin", "Facial hair", "Moustache", "Mouth", "Eyewear"));
    private ArrayList<String> detectedObjects = new ArrayList<>();
    private ObjectDetectionCallback objectDetectionCallbackListener;
    private static final int MSG_DETECT_HAND = 101;
    private static final String DETECT_TYPE = "type";
    private static final String FRAME_NUMBER = "frame_number";
    private long executionTime;
    private long startTime;
    FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getCloudImageLabeler();
    Camera mCamera;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 101:
                    Bundle bundle = msg.getData();
                    String detectObject = bundle.getString("type");
                    Log.d("DetectObjects", "handleMessage detectObject: " + detectObject);
                    detectObjectFromCamera(detectObject);
                default:
            }
        }
    };

    public void detectObjectsFromCamera(CameraPictureTestActivity cameraPictureTestActivity, String mCurrentTest, ObjectDetectionCallback callback) {
        setObjectDetectionCallbackListener(callback);
        startTime = System.currentTimeMillis();
        Log.d(TAG, "detectObjectFromCamera startTime " + startTime + " detectObject " + mCurrentTest);

        if (mCamera == null) {
            mCamera = openCamera();
        }

        SurfaceView surfaceView = new SurfaceView(context);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mCamera.startPreview();

        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                Bitmap bitmap = decodeByteArray(data, previewSize.width, previewSize.height);

                detectObject(bitmap, mCurrentTest);
            }
        });
    }

    public void detectObjectsFromCamera(CameraPictureTestActivity cameraPictureTestActivity, String mCurrentTest) { startTime = System.currentTimeMillis();
        Log.d(TAG, "detectObjectFromCamera startTime " + startTime + " detectObject " + mCurrentTest);

        if (mCamera == null) {
            mCamera = openCamera();
        }

        SurfaceView surfaceView = new SurfaceView(context);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mCamera.startPreview();

        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                Bitmap bitmap = decodeByteArray(data, previewSize.width, previewSize.height);

                detectObject(bitmap, mCurrentTest);
            }
        });
    }

    public interface ObjectDetectionCallback {
        void onObjectDetectionCompleted(ObjectDetectionResult objectDetectionResult);

        void onError(int resultCode, String resultDescription);
    }

    public void setObjectDetectionCallbackListener(ObjectDetectionCallback objectDetectionCallbackListener) {

        Log.d("DetectObjects", "setObjectDetectionCallbackListener listener is set");
        this.objectDetectionCallbackListener = objectDetectionCallbackListener;
    }

    public DetectObjects() {
    }

    private void detectObject(Bitmap bitmap, final String target) {
        Toast.makeText(OruApplication.getAppContext(), "DetectObjects", Toast.LENGTH_SHORT).show();
        Log.d("DetectObjects", "enter detectObject target " + target);
        String localTarget = "FACE";
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        if (enableFaceDetection) {
            if (target.equalsIgnoreCase("FACE")) {
                localTarget = "FACE";
            } else if (target.equalsIgnoreCase("HAND")) {
                localTarget = "OBJECT_DETECT";
            }
        } else {
            localTarget = "OBJECT_DETECT";
        }

        if (localTarget.equalsIgnoreCase("OBJECT_DETECT")) {
            labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                    Log.d("DetectObjects", target + " onSuccess Case " + labels.size());
                    Iterator var2 = labels.iterator();

                    String feature;
                    while (var2.hasNext()) {
                        FirebaseVisionImageLabel label = (FirebaseVisionImageLabel) var2.next();
                        String text = label.getText();
                        feature = label.getEntityId();
                        float confidence = label.getConfidence();
                        Log.d("DetectObjects", target + " onSuccess Case label " + text);
                        DetectObjects.this.detectedObjects.add(text);
                    }

                    boolean result = false;
                    int featuresCount;
                    Iterator var9;
                    if (target.equalsIgnoreCase("FACE")) {
                        featuresCount = 0;
                        var9 = DetectObjects.this.faceObjectsList.iterator();

                        while (var9.hasNext()) {
                            feature = (String) var9.next();
                            if (DetectObjects.this.detectedObjects.contains(feature)) {
                                ++featuresCount;
                                Log.d("DetectObjects", "Matched Face Features Count " + featuresCount + " feature " + feature);
                            }
                        }

                        if (featuresCount >= 4) {
                            result = true;
                        } else {
                            Log.d("DetectObjects", "Face Features are less " + featuresCount);
                        }
                    } else if (target.equalsIgnoreCase("HAND")) {
                        featuresCount = 0;
                        var9 = DetectObjects.this.handObjectsList.iterator();

                        while (var9.hasNext()) {
                            feature = (String) var9.next();
                            if (DetectObjects.this.detectedObjects.contains(feature)) {
                                ++featuresCount;
                                Log.d("DetectObjects", "Matched Hand Features Count " + featuresCount + " feature " + feature);
                            }
                        }

                        if (featuresCount >= 3) {
                            result = true;
                        }
                    }

                    DetectObjects.this.sendResultToCaller(result, target);
                }
            }).addOnFailureListener(new OnFailureListener() {
                public void onFailure(Exception e) {
                    Log.d("DetectObjects", "calling processNextFrames target " + target + " exception " + e);
                    DetectObjects.this.processNextFrames(target);
                }
            });
        }
    }

    public void onTestEnd(boolean result, String objectClass) {
        Toast.makeText(OruApplication.getAppContext(), "result"+result, Toast.LENGTH_SHORT).show();
        sendResultToCaller(result, objectClass);
    }

    private void processNextFrames(String target) {
        Log.d("DetectObjects", "enter processNextFrames target " + target);
        Log.d("DetectObjects", "enter processNextFrames target " + target + "calling sendResultToCaller");
        this.sendResultToCaller(false, target);
    }

    private void sendResultToCaller(boolean result, String objectClass) {
        this.executionTime = System.currentTimeMillis() - this.startTime;
        Log.d("DetectObjects", "enter sendResultToCaller executionTime " + this.executionTime + " mSec");
        Log.d("DetectObjects", "enter sendResultToCaller " + result + " " + objectClass + " " + this.objectDetectionCallbackListener);
        if (this.objectDetectionCallbackListener != null) {
            ObjectDetectionResult objectDetectionResult = new ObjectDetectionResult();
            objectDetectionResult.setObjectClass(objectClass);
            objectDetectionResult.setResult(result);
            this.objectDetectionCallbackListener.onObjectDetectionCompleted(objectDetectionResult);
        } else {
            Log.d("DetectObjects", "enter sendResultToCaller objectDetectionCallbackListener is null");
        }
    }

    public void detectObjectFromCamera(String detectObject) {
        startTime = System.currentTimeMillis();
        Log.d(TAG, "detectObjectFromCamera startTime " + startTime + " detectObject " + detectObject);

        if (mCamera == null) {
            mCamera = openCamera();
        }

        SurfaceView surfaceView = new SurfaceView(context);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mCamera.startPreview();

        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                Bitmap bitmap = decodeByteArray(data, previewSize.width, previewSize.height);

                detectObject(bitmap, detectObject);
            }
        });
    }

    private Camera openCamera() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            Log.e("DetectObjects", "Error opening camera: " + e.getMessage());
        }
        return camera;
    }

    private Bitmap decodeByteArray(byte[] data, int width, int height) {
        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, out);
        byte[] bytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
