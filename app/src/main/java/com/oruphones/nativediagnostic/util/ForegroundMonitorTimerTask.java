package com.oruphones.nativediagnostic.util;


import com.oruphones.nativediagnostic.util.monitor.ForegroundMonitorBaseTimerTask;

public class ForegroundMonitorTimerTask extends ForegroundMonitorBaseTimerTask {

    @Override
    protected boolean reportServerAboutBackground() {
        return false;
    }
}
