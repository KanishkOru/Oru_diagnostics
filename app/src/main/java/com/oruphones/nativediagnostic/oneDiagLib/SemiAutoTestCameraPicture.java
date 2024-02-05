package com.oruphones.nativediagnostic.oneDiagLib;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.api.GlobalConfig;

import org.pervacio.onediaglib.advancedtest.cameraautomation.CameraAccuracy;
import org.pervacio.onediaglib.advancedtest.cloudapis.ObjectDetectionResult;
import org.pervacio.onediaglib.diagtests.TestCameraPicture;
import org.pervacio.onediaglib.diagtests.TestCameraResult;
import org.pervacio.onediaglib.diagtests.TestListener;
import org.pervacio.onediaglib.diagtests.TestResult;

public class SemiAutoTestCameraPicture implements TestListener {
    private static final String TAG = "SemiAutoTestCameraPicture";
    private DetectObjects detectObjects = new DetectObjects();
    private TestCameraPicture testCameraPicture = new TestCameraPicture();
    private TestListener mTestFinishListener;
    private String facing;
    private CameraAccuracy cameraAccuracy;
    private String filepath = null;
    private TestCallback callback;

    public interface TestCallback {
        void onCaptureCompleted(String filePath, boolean success, String detectedClass);
    }

    public void setTestCallback(TestCallback callback) {
        this.callback = callback;
    }

    public SemiAutoTestCameraPicture() {
        this.testCameraPicture.setSaveToFile(true);
        this.testCameraPicture.setTestListener(this);
        this.cameraAccuracy = new CameraAccuracy();
        this.detectObjects.setObjectDetectionCallbackListener(new DetectObjects.ObjectDetectionCallback() {
            @Override
            public void onObjectDetectionCompleted(ObjectDetectionResult objectDetectionResult) {
                boolean result = objectDetectionResult.isResult();
                String detectedClass = objectDetectionResult.getObjectClass();

                if (callback != null) {
                    callback.onCaptureCompleted(filepath, result, detectedClass);
                }

                TestCameraResult testCameraResult = new TestCameraResult();
                testCameraResult.setPath(filepath);
                Log.d(TAG, "onObjectDetectionCompleted " + result + " class " + detectedClass);
                GlobalConfig.getInstance().setCameraTestResult(result);

                if (result) {
                    testCameraResult.setResultCode(0);
                    testCameraResult.setResultDescription(detectedClass + " Detected");
                } else {
                    testCameraResult.setResultCode(1);
                    testCameraResult.setResultDescription(detectedClass + " Not Detected");
                }

                if (mTestFinishListener != null) {
                    mTestFinishListener.onTestEnd(testCameraResult);
                }
            }

            @Override
            public void onError(int resultCode, String resultDescription) {
            }
        });
    }

    public void setTestFinishListener(TestListener mTestFinishListener) {
        this.mTestFinishListener = mTestFinishListener;
    }

    public void onTestStart() {
    }

    public void onTestEnd(TestResult testResult) {
        Toast.makeText(OruApplication.getAppContext(), "testResult"+testResult, Toast.LENGTH_SHORT).show();
        TestCameraResult testCameraResult = (TestCameraResult) testResult;
        int resultCode = testCameraResult.getResultCode();
        Log.d(TAG, "enter onTestEnd capture completed");
        Log.d(TAG, "enter onTestEnd resultCode =" + resultCode);
        Log.d(TAG, "enter onTestEnd File Path =" + testCameraResult.getPath());
        if (resultCode == 0) {
            filepath = testCameraResult.getPath();
            if (facing.equals("front")) {
                detectObjects.detectObjectFromCamera("FACE");
            } else if (facing.equals("rear")) {
                detectObjects.detectObjectFromCamera("HAND");
            }
        } else {
            try {
                if (mTestFinishListener != null) {
                    mTestFinishListener.onTestEnd(testCameraResult);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        testCameraPicture.stopCamera();
    }

    public void startCamera(String facing, ViewGroup frameLayout) {
        this.facing = facing;
        Log.d(TAG, "camera facing " + facing);
        testCameraPicture.startCamera(facing, -1, frameLayout);
    }

    public void capture() {
        testCameraPicture.capture();
    }

    public void setSaveToFile(boolean saveToFile) {
        testCameraPicture.setSaveToFile(saveToFile);
    }

    public void setTestListener(TestListener testListener) {
        mTestFinishListener = testListener;
    }
}
