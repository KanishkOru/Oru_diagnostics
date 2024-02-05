package com.oruphones.nativediagnostic.oneDiagLib;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


import org.pervacio.onediaglib.diagtests.ITimerListener;
import org.pervacio.onediaglib.diagtests.TestCameraResult;
import org.pervacio.onediaglib.diagtests.TestListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestFlash implements ITimerListener, SurfaceHolder.Callback {
    private static String TAG = TestFlash.class.getSimpleName();
    private Context context;
    private String failCause;
    private boolean flashOn;
    private DiagTimer diagTimer;
    private TestListener testListener;
    private TestCameraResult testCameraResult;
    private FrameLayout preview;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder = null;
    private boolean previewIsRunning;
    private Intent intent;
    private boolean isFrontFlashTest;
    private boolean isFlashAdvanceTest = false;
    private static final int MSG_TUN_OFF_FLASH = 200;
    private static final int MSG_TUN_ON_FLASH = 201;
    int flashCount = 0;
    int flashedCount = 0;
    CameraManager manager = null;
    boolean flashAvailable = false;
    int lensFacing;
    Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    TestFlash.this.turnOffFlash(TestFlash.this.isFlashAdvanceTest);
                    this.sendEmptyMessageDelayed(201, 2000L);
                    break;
                case 201:
                   // DLog.d("FlashCount", " "+TestFlash.this.flashedCount);
                    if (TestFlash.this.flashedCount < TestFlash.this.flashCount) {
                        TestFlash.this.turnOnFlash(TestFlash.this.isFlashAdvanceTest);
                        this.sendEmptyMessageDelayed(200, 3000L);
                    } else if (TestFlash.this.testListener != null) {
                        TestFlash.this.testCameraResult.setResultCode(0);
                        TestCameraResult testCameraResult1 = TestFlash.this.testCameraResult;
                        TestCameraResult.setTestAdditionalInfo(String.valueOf(TestFlash.this.flashCount));
                        TestFlash.this.testListener.onTestEnd(TestFlash.this.testCameraResult);
                    }
            }

        }
    };

    public TestFlash(FrameLayout frameLayout) {
        this.preview = frameLayout;
        this.context = OruApplication.getAppContext();
        this.diagTimer = new DiagTimer(this);
        if (VERSION.SDK_INT >= 21) {
            this.manager = (CameraManager)this.context.getSystemService(Context.CAMERA_SERVICE);

            try {
                this.flashAvailable = (Boolean)this.manager.getCameraCharacteristics("0").get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                this.lensFacing = (Integer)this.manager.getCameraCharacteristics("0").get(CameraCharacteristics.LENS_FACING);
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

    }

    public boolean hasFeature() {
        return this.context.getPackageManager().hasSystemFeature("android.hardware.camera.flash");
    }

    public void setTestListener(TestListener testListener) {
        this.testListener = testListener;
        this.testCameraResult = new TestCameraResult();
    }

    public void init() {
        try {
            this.diagTimer.startTimer(DiagTimer.MANUALTEST_TIMEOUT);
            if (this.isFrontFlashTest) {
                int index = getFrontCameraId();
                if (index == -1 || !isFrontCameraFlashAvailable()) {
                    DLog.d(TAG, "No Front Camera Found");
                    return;
                }
            }
        } catch (Exception var2) {
            DLog.e(TAG, "Unable to open camera");
            this.failCause = "Unable to open camera";
            if (this.testListener != null) {
                this.testCameraResult.setResultCode(256);
                this.testCameraResult.setResultDescription("Unable to open camera");
            }
        }

    }

    public void turnOnFlash(boolean isAudioAdvanceTest) {
        ++this.flashedCount;
        this.diagTimer.restartTimer(DiagTimer.MANUALTEST_TIMEOUT);
        if (Build.MODEL.equalsIgnoreCase("XP7700")) {
            this.intent = new Intent("com.android.LEDFlashlight.longpress5");
            this.intent.putExtra("isLedOn", true);
            this.context.sendBroadcast(this.intent);
        } else {
            if (Build.MODEL.equalsIgnoreCase("HTC Desire 601") || Build.MODEL.equalsIgnoreCase("HUAWEI GRA-UL00") || Build.MODEL.equalsIgnoreCase("HM NOTE 1LTE")) {
                this.init();
            }

            try {
                DLog.d(TAG, "flash light turned on!");
                if (!this.context.getPackageManager().hasSystemFeature("android.hardware.camera.flash") && !"JOIN".equalsIgnoreCase(Build.MODEL) && !"Avvio_779".equalsIgnoreCase(Build.MODEL)) {
                    DLog.d(TAG, "Flash not available.");
                    this.flashOn = false;
                    this.failCause = this.failCause + "Camera flash is Not present for your device.\n";
                    DLog.d(TAG, "Camera flash is Not present for your device.");
                    this.flashOn = false;
                    if (this.testListener != null) {
                        this.testCameraResult.setResultCode(2);
                        this.testCameraResult.setResultDescription("Camera flash is Not present for your device.");
                        this.testListener.onTestEnd(this.testCameraResult);
                    }
                } else {
                    DLog.d(TAG, "Device has flash available.");
                    DLog.d(TAG, "FLASH MODE TORCH available");
                    if (VERSION.SDK_INT >= 23) {
                        this.manager.setTorchMode("0", true);
                        this.flashOn = true;
                    }

                    this.surfaceView = new SurfaceView(OruApplication.getAppContext());
                    this.preview.addView(this.surfaceView);
                    this.surfaceHolder = this.surfaceView.getHolder();
                    this.surfaceHolder.addCallback(this);
                    this.surfaceHolder.setType(3);
                    DLog.d(TAG, "flash turned on!");
                }
            } catch (Exception var3) {
                DLog.e(TAG, var3.getMessage());
                this.flashOn = false;
                this.failCause = this.failCause + "Exception flashLightOn()\n";
                if (this.testListener != null) {
                    this.testCameraResult.setResultCode(4);
                    this.testCameraResult.setResultDescription("Error in starting flash.");
                    this.testListener.onTestEnd(this.testCameraResult);
                }
            }

        }
    }

    public void turnOnFlash() {
        if (this.isFlashAdvanceTest) {
            this.flashCount = this.getRandomNumber(1, 3);
            DLog.d(TAG, "enter turnOnFlash Build Model " + Build.MODEL);
            DLog.d(TAG, "enter turnOnFlash Build Model " + Build.MANUFACTURER);
            if (Build.MANUFACTURER.equalsIgnoreCase("Sony")) {
                this.flashCount = 1;
            }
            GlobalConfig globalConfig = GlobalConfig.getInstance();
            globalConfig.addItemToList("Flash Test System Output : "+this.flashCount);

            DLog.d(TAG, "Total flashCount " + this.flashCount);
        } else {
            this.flashCount = 1;
        }

        this.mHandler.sendEmptyMessage(201);
    }

    public void turnOffFlash() {
        this.turnOffFlash(this.isFlashAdvanceTest);
    }

    private void turnOffFlash(boolean isAudioAdvanceTest) {
        DLog.d(TAG, "enter turnOffFlash");
        this.diagTimer.restartTimer(DiagTimer.MANUALTEST_TIMEOUT);
        if (Build.MODEL.equalsIgnoreCase("XP7700")) {
            this.intent = new Intent("com.android.LEDFlashlight.longpress5");
            this.intent.putExtra("isLedOn", false);
            this.context.sendBroadcast(this.intent);
        } else if (this.flashOn) {
            try {
                DLog.d(TAG, "Flash Off called!");
                DLog.d(TAG, "enter turnOffFlash camera ");
                if (this.manager != null) {
                    DLog.d(TAG, "Clearing all camera objects.");
                    if (VERSION.SDK_INT >= 23) {
                        this.manager.setTorchMode("0", false);
                    }

                    this.flashOn = false;
                    DLog.d(TAG, "Flash turned off!");
                }
            } catch (Exception var3) {
                DLog.d(TAG, var3.getMessage());
                this.flashOn = false;
            }

        }
    }

    public void release() {
        this.diagTimer.stopTimer();
        this.turnOffFlash();
        if (this.manager != null) {
            this.manager = null;
        }

    }

    public void timeout() {
        this.turnOffFlash();
        this.release();
        if (this.testListener != null) {
            this.testCameraResult.setResultCode(3);
            this.testCameraResult.setResultDescription("Time out");
            this.testListener.onTestEnd(this.testCameraResult);
        }

    }

    public void surfaceCreated(SurfaceHolder holder) {
        DLog.d(TAG, "surfaceCreated");

        try {
            this.previewIsRunning = true;
            if (Build.MODEL.equalsIgnoreCase("Galaxy Nexus") || "Z-01K".equalsIgnoreCase(Build.MODEL)) {
                this.flashOn = true;
            }

        } catch (Exception var3) {
            if (this.testListener != null) {
                this.testCameraResult.setResultCode(257);
                this.testCameraResult.setResultDescription("Error in showing preview");
                this.testListener.onTestEnd(this.testCameraResult);
            }

        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        DLog.d(TAG, "surfaceChanged");
        if (Build.MODEL.equalsIgnoreCase("Galaxy Nexus") || "Z-01K".equalsIgnoreCase(Build.MODEL)) {
            if (VERSION.SDK_INT >= 21) {
                this.manager = (CameraManager)this.context.getSystemService(Context.CAMERA_SERVICE);
            }

            try {
                if (VERSION.SDK_INT >= 23) {
                    this.manager.setTorchMode("0", true);
                }
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        DLog.d(TAG, "surfaceDestroyed");
    }

    private static int getFrontCameraId() {
        Camera.CameraInfo ci = new Camera.CameraInfo();

        for(int i = 0; i < Camera.getNumberOfCameras(); ++i) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == 1) {
                return i;
            }
        }

        return -1;
    }

    public void setFrontFlashTest(boolean frontFlashTest) {
        this.isFrontFlashTest = frontFlashTest;
    }

    public static boolean isFrontCameraFlashAvailable() {
        if ("BBB100-1".equalsIgnoreCase(Build.MODEL)) {
            return false;
        } else {
            Camera cam = null;

            try {
                int id = getFrontCameraId();
                if (id != -1) {
                    cam = Camera.open(id);
                    Camera.Parameters params = cam.getParameters();
                    List<String> modes = params.getSupportedFlashModes();
                    if (modes == null) {
                        cam.release();
                        return false;
                    }

                    if (modes.contains("torch") || modes.contains("on")) {
                        cam.release();
                        return true;
                    }
                }

                return false;
            } catch (Exception var4) {
                if (cam != null) {
                    cam.release();
                }

                return false;
            }
        }
    }

    public ArrayList<Integer> getRandomNumbers(int range, int n) {
        ArrayList<Integer> picked = new ArrayList();

        while(picked.size() < n) {
            int i = (new Random()).nextInt(range - 1) + 1;
            if (!picked.contains(i)) {
                picked.add(i);
            }
        }

        return picked;
    }

    private int getRandomNumber(int min, int max) {
        return (int)(Math.random() * (double)(max + 1 - min) + (double)min);
    }

    public boolean isFlashAdvanceTest() {
        return this.isFlashAdvanceTest;
    }

    public void setFlashAdvanceTest(boolean flashAdvanceTest) {
        this.isFlashAdvanceTest = flashAdvanceTest;
    }
}
