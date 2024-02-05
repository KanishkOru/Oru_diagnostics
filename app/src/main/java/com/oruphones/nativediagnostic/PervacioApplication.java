package com.oruphones.nativediagnostic;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;


import org.pervacio.onediaglib.APPI;
import org.pervacio.onediaglib.audio.AudioUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Pervacio on 04-09-2017.
 */

public class PervacioApplication extends APPI {

    private static String testURL;
    public static List<String> deviceModelList = Arrays.asList("SGH-M919", "SM-G361F", "SAMSUNG-SGH-T989", "SM-J410G", "SM-J260M","SM-A013M");
    public static String[] manualDevice = new String[]{"Nokia 1.3","SM-A013M"};
    @Override
    public void onCreate() {
        super.onCreate();

//        FirebaseApp.initializeApp(this);
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        init();
    }
    private void init() {
        AudioUtils.dndSettingsNotSupportedDevicesArray = new String[]{"DM-02H", "LGLS676", "LGLS991", "LG-H831", "LG-K350", "LG-M160", "LG-H818", "moto e5 play", "CAG-L23", "5033A", "5002J", "DRA-LX3",
                "LG-K430T", "Nokia 2.1", "Nokia 1", "SM-J260M", "ZTE Blade A531", "ALE-L23", "AX823", "5033E",
                "AX824", "AX830", "SM-J410G", "KK536", "T965", "K504", "TA-1079 SS", "5033J", "LM-X120", "AX683", "AX960", "A5 2019", "Huawei Y5 Lite 2018", "TA-1127",
                "AX824+", "KS964", "KS605", "k536", "9009G", "ZTE A3 Lite", "Nokia 1 PLUS", "moto e6s", "moto g(8)", "moto g(8) power lite",
                "motorola one hyper", "motorola one macro", "SM-A015M", "SM-A515F", "SM-A910F", "SM-G980F", "SM-G985F", "5024J", "5033D", "SM-A013M", "SM-A013M-DS", "Nokia 1.3", "ZTE Blade A5 2019", "SM-G955U",
                "ELEMENT_PRO_2", "Element 4 Plus"
        };

    }


    public static String getTestURL() {
        return testURL;
    }

    public static void setTestURL(String testURL) {
        PervacioApplication.testURL = testURL;
    }
}
