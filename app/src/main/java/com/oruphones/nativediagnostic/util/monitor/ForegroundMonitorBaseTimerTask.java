package com.oruphones.nativediagnostic.util.monitor;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;



import java.util.TimerTask;

public abstract class ForegroundMonitorBaseTimerTask extends TimerTask {
    private static String TAG = ForegroundMonitorBaseTimerTask.class.getSimpleName();
    protected Context mContext = OruApplication.getAppContext();
    protected boolean isMessageShown;

        @Override
        public void run() {
            checkAndBringappToForeground();
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        private void checkAndBringappToForeground() {
            try {
                boolean isOurAppInFg = CommonUtil.checkIfAppInForeground(mContext);
                if (isOurAppInFg) {
                    isMessageShown = false;
                }

               // Log.d(TAG, "InAppBackground : isOurAppInFg " + isOurAppInFg + " isMessageShown : " + isMessageShown);

                if (!isOurAppInFg  && !isMessageShown) {
                    if(!reportServerAboutBackground()){
                         /*if ((RetailAdvancedUtil.getStringfromPref(getApplicationContext(), DiagConstants.TEST_NAME)).contains("Keys") && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)) {
                            messagePopupHandler.sendEmptyMessageDelayed(0, 0);
                            if (!(fgPkgName.equals("com.samsung.android.app.spage"))) {
                                am.moveTaskToFront(ourTaskId, 0);
                            }
                        }*/
                         return;
                    }
                    isMessageShown = true;

                }
            } catch (Exception e) {
                DLog.e(TAG, "checkAndBringappToForeground Exception: "+ e);
            }
        }



        protected abstract boolean reportServerAboutBackground();



       /* private boolean allowHomeButtonEnabled() {
            try {
                SharedPreferences settings = getSharedPreferences(DiagConstants.PREFS_FILE_NAME, Context.MODE_PRIVATE);
                return settings.getBoolean(DiagConstants.PREF_ALLOW_HOME_BUTTON, false);
            } catch (Exception e) {
                LogUtil.printLog(TAG, "Exception allowHomeButtonEnabled: ", e, LogUtil.LogType.EXCEPTION);
            }
            return false;
        }*/

    }
