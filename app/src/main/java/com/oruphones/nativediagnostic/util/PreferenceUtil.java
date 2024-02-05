package com.oruphones.nativediagnostic.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.oruphones.nativediagnostic.OruApplication;


public class PreferenceUtil {
    private static SharedPreferences prefs = OruApplication.getAppContext().getSharedPreferences(OruApplication.getAppContext().getPackageName(), Context.MODE_PRIVATE);
    public static final String IS_CLOUD_VISION = "is_cloud_vision";
    public static final String PERMISSION_DENIED_RATIONALE = "should_show_request_permission_rationale";
    public static final String TERMS_CONDITIONS = "terms_conditions";
    public static final String EX_SELECTION_LIST = "selection_list";
    public static final String EX_IMEI = "imei";
    public static final String EX_SECTION_NAME = "sectionName";
    public static final String EX_SESSION_ID = "session_id";
    public static final String EX_ACK_ID = "ack_id";
    public static final String EX_NETWORK_STATUS_BEFORE = "network_status_before";
    public static final String EX_SECTION_INDEX = "sectionInd";
    public static final String EX_INDEX = "index";
    public static final String EX_COMMAND_NAME = "command_name";
    public static final String EX_TEST_NAME = "test_name";
    public static final String EX_RESULT = "result";
    public static final String EX_OBJECT = "serializable_object";
    public static final String EX_EXIT = "Exit";
    public static final String EX_PIN = "pin";
    public static final String EX_RESTORE_SESSION = "restore_session";
    public static final String EX_AUTH_SUCCESS = "AUTH_SUCCESS";
    public static final String EX_AUTH_ERROR = "AUTH_ERROR";
    public static final String EX_PERMISSION_DENIED = "permission_denied";
    public static final int RC_PERMISSION = 6545;
    public static final int WRITE_SETTINGS = 9001;
    public static final int PERMISSION_SETTINGS = 9003;
    public static final int USAGE_STATS = 2667;
    public static final String ACTION_RECORD = "Record";
    public static final String ACTION_PLAY = "Play";



    public static void putString(String key,
                               String value) {
        prefs.edit().putString(key,value).apply();
    }

    public static String getString(String key){
        return prefs.getString(key,null);
    }

    public static void putBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key,value).apply();
    }

    public static boolean getBoolean(String key){
        return prefs.getBoolean(key,false);
    }

    public static void firstTimeAskingPermission( String permission, boolean isFirstTime){
        prefs.edit().putBoolean(permission, isFirstTime).apply();
    }
    public static boolean isFirstTimeAskingPermission(String permission){
        return prefs.getBoolean(permission, true);
    }

}
