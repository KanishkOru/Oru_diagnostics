package com.oruphones.nativediagnostic.util.monitor;

import android.os.Handler;
import android.os.Message;

public class UserInteractionMonitor {
    private   long connectionTimeOut = 5 * 60 * 1000; // 10 min = 5 * 60 * 1000 ms
    private   boolean isEligibleForTimeOut = false; // 10 min = 5 * 60 * 1000 ms
    private   Runnable disconnectCallback;
    private static UserInteractionMonitor mMonitor;


    private UserInteractionMonitor() {
    }

    public static UserInteractionMonitor getInstance(){
        if(mMonitor==null)
            mMonitor = new UserInteractionMonitor();

        return mMonitor;
    }

    //Time OUT
    public  void setConnectionTimeOut(int connectionTimeOutInMin) {
        if(connectionTimeOutInMin<=0)
            return;
        setConnectionTimeOut((long) (connectionTimeOutInMin * 60 * 1000));
    }

    public  void setConnectionTimeOut(long connectionTimeOutInMin) {
        this.connectionTimeOut = connectionTimeOutInMin;
    }


    //If Start Timer to track user transactions
    public void setIsEligibleForTimeOut(boolean isEligibleForTimeOut,int connectionTimeOut) {
        this.isEligibleForTimeOut = isEligibleForTimeOut;
        setConnectionTimeOut(connectionTimeOut);
        if(isEligibleForTimeOut){
            stop();
        }
    }


    private static Handler disconnectHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return true;
        }
    });


    public void stop() {
        stopUserInteractionTimer();
    }

    public void onResume(Runnable runnable) {
        disconnectCallback = runnable;
        resetUserInteractionTimer();

    }

    public void resetUserInteractionTimer() {
        stopUserInteractionTimer();
        if (isEligibleForTimeOut && disconnectCallback != null)
            disconnectHandler.postDelayed(disconnectCallback, connectionTimeOut);

    }

    public void stopUserInteractionTimer() {
        if (null != disconnectCallback)
            disconnectHandler.removeCallbacks(disconnectCallback);
    }

}
