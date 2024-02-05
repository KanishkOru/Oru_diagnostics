package com.oruphones.nativediagnostic.oneDiagLib;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


import org.pervacio.onediaglib.atomicfunctions.AFUsbConnection;
import org.pervacio.onediaglib.diagtests.DiagTimer;
import org.pervacio.onediaglib.diagtests.ITimerListener;
import org.pervacio.onediaglib.diagtests.TestListener;
import org.pervacio.onediaglib.diagtests.TestResult;

public class TestUsbConnection implements ITimerListener {
    Context context = OruApplication.getAppContext();
    IntentFilter intentFilter;
   UsbStateReceiver usbStateReceiver;
    TestListener testListener;
    private DiagTimer diagTimer;
    boolean action2;
    private boolean isTestingUSBConnection;
    private static String TAG = TestUsbConnection.class.getSimpleName();

    public TestUsbConnection() {
    }

    public void setOnTestCompletedListener(TestListener testListener) {
        this.testListener = testListener;
    }

    public void USBConnectionAutotest() {
        AFUsbConnection usbConnection = new AFUsbConnection();
        boolean isConnected = usbConnection.isUSBConnected();
        TestResult testResult;
        if (isConnected) {
            testResult = new TestResult();
            testResult.setResultCode(0);
            if (this.testListener != null) {
                this.testListener.onTestEnd(testResult);
            }
        } else {
            testResult = new TestResult();
            testResult.setResultCode(1);
            if (this.testListener != null) {
                this.testListener.onTestEnd(testResult);
            }
        }

    }

    public void USBConnectionManualTest() {
        this.intentFilter = new IntentFilter();
        this.intentFilter.addAction("android.hardware.usb.action.USB_STATE");
        this.usbStateReceiver = new UsbStateReceiver();
        this.context.registerReceiver(this.usbStateReceiver, this.intentFilter);
        this.diagTimer = new DiagTimer(this);
        this.isTestingUSBConnection = true;
        this.diagTimer.startTimer(DiagTimer.MANUALTEST_TIMEOUT);
    }

    public void stopUSBConnectionTest() {
        if (isTestingUSBConnection) {
            if (this.diagTimer != null) {
                this.diagTimer.stopTimer();
            }
            if (this.usbStateReceiver != null) {
                try {
                    this.context.unregisterReceiver(this.usbStateReceiver);
                } catch (Exception e) {
                    DLog.e(TAG, "Exception: " + e);
                }
            }
            isTestingUSBConnection = false;
        }
    }

    public void timeout() {
        TestResult testResult = new TestResult();
        testResult.setResultCode(3);
        if (this.testListener != null) {
            this.testListener.onTestEnd(testResult);
        }

    }

    public void removeTestListener() {
        this.testListener = null;
        this.diagTimer.stopTimer();

        try {
            this.context.unregisterReceiver(this.usbStateReceiver);
        } catch (Exception var2) {
            DLog.e(TAG, "Exception");
        }

    }

    public class UsbStateReceiver extends BroadcastReceiver {
        public UsbStateReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            boolean isUsbConnected = false;
            if (intent != null && intent.getExtras() != null) {
                isUsbConnected = intent.getExtras().getBoolean("connected");
            }

            if (isUsbConnected) {
                if (TestUsbConnection.this.diagTimer != null) {
                    TestUsbConnection.this.diagTimer.stopTimer();
                }

                TestResult testResult = new TestResult();
                testResult.setResultCode(0);
                if (TestUsbConnection.this.testListener != null) {
                    TestUsbConnection.this.testListener.onTestEnd(testResult);
                }

                if (TestUsbConnection.this.usbStateReceiver != null) {
                    context.unregisterReceiver(TestUsbConnection.this.usbStateReceiver);
                }
            }

        }
    }
}
