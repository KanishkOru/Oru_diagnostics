package com.oruphones.nativediagnostic.oneDiagLib;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


import org.pervacio.onediaglib.diagtests.ISensorEventListener;
import org.pervacio.onediaglib.diagtests.ISensors;
import org.pervacio.onediaglib.diagtests.ITimerListener;
import org.pervacio.onediaglib.diagtests.TestListener;
import org.pervacio.onediaglib.diagtests.TestResult;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

public class TestAutoSensor implements ISensors, SensorEventListener, ITimerListener {
    private static SensorManager mSensorManager;
    private Sensor mSensor;
    private static TestAutoSensor mTestAutoSensor;
    private static String TAG = TestAutoSensor.class.getSimpleName();
    private static String mSensorType;
    private DiagTimer mDiagTimer = new DiagTimer(this);
    private TestListener mTestFinishListener;
    private ISensorEventListener mISensorListener;
    private boolean istestpass = false;
    private String mUnits = null;
    private static float mMaxValue = 0.0F;
    private static float mMinValue = 0.0F;
    private float mSensorResult = 0.0F;
    public static final String GYROSCOPE = "gyroscope";
    public static final String MAGNETICSENSOR = "magneticsensor";
    public static final String BAROMETER = "barometer";
    public static final String TEMPERATURE = "temperature";
    public static final String HUMIDITY = "humidity";
    public static final String GAME_ROTATION_VECTOR = "game_rotation_vector";
    public static final String GEOMAGNETIC_ROTATION_VECTOR = "geomagnetic_rotation_vector";
    public static final String ROTATION_VECTOR = "rotation_vector";
    public static final String LINEAR_ACCELERATION = "linear_acceleration";
    public static final String ACCELEROMETERTEST = "accelerometer";
    public static String testAdditionaInfo = "";

    private TestAutoSensor() {
        this.mDiagTimer.startTimer(10000);
    }

    public static TestAutoSensor getInstance(String testName, float min, float max) {
        mSensorType = testName;
        mMinValue = min;
        mMaxValue = max;
        if (mTestAutoSensor == null) {
            mTestAutoSensor = new TestAutoSensor();
            Context var10000 = OruApplication.getAppContext();
            OruApplication.getAppContext();
            mSensorManager = (SensorManager)var10000.getSystemService(Context.SENSOR_SERVICE);
        }

        return mTestAutoSensor;
    }

    public void setTestFinishListener(TestListener mTestFinishListener) {
        this.mTestFinishListener = mTestFinishListener;
    }

    public void registerSensorResultListener(ISensorEventListener sensorListener) {
        this.mISensorListener = sensorListener;
    }

    public void unRegisterSensorResultListener() {
        this.mISensorListener = null;
        this.mTestFinishListener = null;
        mTestAutoSensor = null;
    }

    public void unRegisterOnSensorEventListener() {
        mSensorManager.unregisterListener(this);
    }

    public void registerSensorEventListener() {
        if (!mSensorType.equalsIgnoreCase("gyroscope")) {
            if (mSensorType.equalsIgnoreCase("magneticsensor")) {
                this.mSensor = mSensorManager.getDefaultSensor(2);
                this.mUnits = "  (uT)";
            } else if (mSensorType.equalsIgnoreCase("barometer")) {
                this.mSensor = mSensorManager.getDefaultSensor(6);
                this.mUnits = "hpa";
            } else if (mSensorType.equalsIgnoreCase("temperature")) {
                this.mSensor = mSensorManager.getDefaultSensor(13);
                this.mUnits = "C";
            } else if (mSensorType.equalsIgnoreCase("humidity")) {
                this.mSensor = mSensorManager.getDefaultSensor(12);
                this.mUnits = "%";
            } else if (mSensorType.equalsIgnoreCase("game_rotation_vector")) {
                this.mSensor = mSensorManager.getDefaultSensor(15);
            } else if (mSensorType.equalsIgnoreCase("geomagnetic_rotation_vector")) {
                this.mSensor = mSensorManager.getDefaultSensor(20);
            } else if (mSensorType.equalsIgnoreCase("rotation_vector")) {
                this.mSensor = mSensorManager.getDefaultSensor(11);
            } else if (mSensorType.equalsIgnoreCase("linear_acceleration")) {
                this.mSensor = mSensorManager.getDefaultSensor(10);
                this.mUnits = "m/s sq";
            } else if (mSensorType.equalsIgnoreCase("accelerometer")) {
                this.mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                this.mUnits = "m/s sq";
            }

            mSensorManager.registerListener(this, this.mSensor, 3);
        } else {
            List<Sensor> sensorList = mSensorManager.getSensorList(4);
            Iterator var2 = sensorList.iterator();

            while(var2.hasNext()) {
                Sensor sensor = (Sensor)var2.next();
                this.mUnits = "radians/second";
                this.mUnits = "rads/sec";
                mSensorManager.registerListener(this, sensor, 3);
            }

        }
    }

    public boolean isFutureAvailable(String sensorType) {
        Context var10000 = OruApplication.getAppContext();
        OruApplication.getAppContext();
        SensorManager systemService = (SensorManager)var10000.getSystemService(Context.SENSOR_SERVICE);
        Sensor defaultSensor = null;
        if (sensorType.equalsIgnoreCase("gyroscope")) {
            defaultSensor = systemService.getDefaultSensor(4);
        } else if (sensorType.equalsIgnoreCase("magneticsensor")) {
            defaultSensor = systemService.getDefaultSensor(2);
        } else if (sensorType.equalsIgnoreCase("barometer")) {
            defaultSensor = systemService.getDefaultSensor(6);
        } else if (sensorType.equalsIgnoreCase("temperature")) {
            defaultSensor = systemService.getDefaultSensor(13);
        } else if (sensorType.equalsIgnoreCase("humidity")) {
            defaultSensor = systemService.getDefaultSensor(12);
        } else if (sensorType.equalsIgnoreCase("game_rotation_vector")) {
            defaultSensor = systemService.getDefaultSensor(15);
        } else if (sensorType.equalsIgnoreCase("geomagnetic_rotation_vector")) {
            defaultSensor = systemService.getDefaultSensor(20);
        } else if (sensorType.equalsIgnoreCase("rotation_vector")) {
            defaultSensor = systemService.getDefaultSensor(11);
        } else if (sensorType.equalsIgnoreCase("linear_acceleration")) {
            defaultSensor = systemService.getDefaultSensor(10);
        }else if (sensorType.equalsIgnoreCase("accelerometer")) {
            defaultSensor = systemService.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (defaultSensor == null) {
            TestResult result = new TestResult();
            result.setResultCode(2);
            result.setResultDescription("featureNotAvailable");
            if (this.mTestFinishListener != null) {
                this.mTestFinishListener.onTestEnd(result);
            }

            return false;
        } else {
            return true;
        }
    }

    public void timeout() {
        TestResult result = new TestResult();
        result.setResultCode(3);
        this.testFinished(result);
    }

    private void testFinished(TestResult result) {
        if (this.mTestFinishListener != null) {
            if (this.mDiagTimer != null) {
                this.mDiagTimer.stopTimer();
            }

            result.setTestName(mSensorType);
            this.mTestFinishListener.onTestEnd(result);
        }

        this.unRegisterOnSensorEventListener();
        this.unRegisterSensorResultListener();
    }

    public void onSensorChanged(SensorEvent event) {
        this.istestpass = false;
        this.mSensorResult = 0.0F;
        if (!mSensorType.equalsIgnoreCase("gyroscope") && !mSensorType.equalsIgnoreCase("magneticsensor") && !mSensorType.equalsIgnoreCase("game_rotation_vector") && !mSensorType.equalsIgnoreCase("geomagnetic_rotation_vector") && !mSensorType.equalsIgnoreCase("rotation_vector") && !mSensorType.equalsIgnoreCase("linear_acceleration") && !mSensorType.equalsIgnoreCase("accelerometer")) {
            this.mSensorResult = event.values[0];
            if (this.mSensorResult > mMinValue && this.mSensorResult < mMaxValue) {
                this.istestpass = true;
                testAdditionaInfo = "AdditionalInfo:" + Float.toString(this.mSensorResult);
            }
        } else {
            float axisX = event.values[0];
            float axisY = event.values[1];
            float axisZ = event.values[2];
            this.mSensorResult = (float)Math.sqrt((double)(axisX * axisX + axisY * axisY + axisZ * axisZ));
            DLog.d(TAG, "TestAutoSensor" +"enter onSensorChanged sensorType: " + mSensorType + " , mSensorResult " + this.mSensorResult);
            DLog.d(TAG, "TestAutoSensor"+ "enter onSensorChanged sensorType: " + mSensorType + " , mSensorResult : " + this.mSensorResult + " axisX " + axisX + " axisY " + axisY + " axisZ " + axisZ);
            if (this.mSensorResult > mMinValue && this.mSensorResult < mMaxValue) {
                this.istestpass = true;
                String units = "";
                if (this.mUnits != null) {
                    units = this.mUnits;
                }

                DecimalFormat df = new DecimalFormat("#.###");
                String axisXStr = df.format((double)axisX);
                String axisYStr = df.format((double)axisY);
                String axisZStr = df.format((double)axisZ);
                testAdditionaInfo = "X:" + axisXStr + " " + units + ",Y:" + axisYStr + " " + units + ",Z:" + axisZStr + " " + units;
            }
        }

        if (this.mISensorListener != null) {
            this.mISensorListener.onSensorEventListner(this.mSensorResult);
            this.mISensorListener.onSensorEventListner("" + mSensorType + " sensor value : " + this.mSensorResult + " " + this.mUnits);
        }

        if (this.istestpass) {
            DLog.d(TAG, "lib enter onSensorChanged sensorType: " + mSensorType + "TEST PASSS FINISHING");
            TestResult result = new TestResult();
            result.setResultCode(0);
            TestResult.setTestAdditionalInfo(testAdditionaInfo);
            this.testFinished(result);
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}

