package com.oruphones.nativediagnostic.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage the preferences
 */
public class PreferenceHelper {

    private static SharedPreferences prefs;
    private static PreferenceHelper instance;
    private final Map<String, Object> cachedValues = new HashMap<>();

    private PreferenceHelper() {
    }
    public static PreferenceHelper getInstance(Context ctx) {
        Context context = ctx.getApplicationContext();
        prefs = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        if (instance == null) {
            instance = new PreferenceHelper();
        }
        return instance;
    }





    public void putBooleanItem(String key, boolean value) {

        prefs.edit().putBoolean(key, value).apply();
    }





    public int getIntegerItem(String key, int defaultValue) {
//        if (cachedValues.containsKey(key)) {
//            Object value = cachedValues.get(key);
//            if (value instanceof Integer) {
//                return (Integer) value;
//            } else if (value instanceof String) {
//                try {
//                    return Integer.parseInt((String) value);
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
        int value = prefs.getInt(key, defaultValue);
    //    cachedValues.put(key, value);
        return value;
    }

    public void putIntegerCount(String key, int defaultValue){
        prefs.edit().putInt(key, defaultValue).apply();
    }
    private int getInteractionCount() {
        return getIntegerItem(Constants.INTERACTION_COUNT_KEY, 0);
    }
    public Boolean getBooleanItem(String key) {
        if (cachedValues.containsKey(key)) {
            Object value = cachedValues.get(key);
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
        }
        String value = getStringItem(key, false, true);
        if (value != null) {
            try {
                Boolean parsedValue = Boolean.valueOf(value);
                cachedValues.put(key, parsedValue);
                return parsedValue;
            } catch (Exception e) {
                // If the value cannot be parsed then just return default
            }
        }
        return Boolean.FALSE;
    }

    public void putIntegerItem(String key,
                               Integer value) {
        String stringValue = value != null ? value.toString() : null;
        cachedValues.put(key, value);
        putStringItem(key, stringValue, false, true);
    }

    private String getStringItem(String key,
                                 boolean cache,
                                 boolean isEncoded) {
        if (cache && cachedValues.containsKey(key)) {
            Object value = cachedValues.get(key);
            if (value instanceof String) {
                return (String) value;
            }
        }
        if (prefs.contains(key)) {
            try {
                String item;
                item = prefs.getString(key, null);
                if (cache) {
                    cachedValues.put(key, item);
                }
                return item;
            } catch (Exception e) {
                // If the item cannot be retrieved from the preferences then it is older version and needs to be reset.
            }
        }
        cachedValues.remove(key);
        return null;
    }
    private void putStringItem(String key,
                               String value,
                               boolean cache,
                               boolean encode) {
        if (value != null) {
            String valueToPut = value;
            prefs.edit().putString(key, valueToPut).apply();
            if (cache) {
                cachedValues.put(key, value);
            }
        } else {
            prefs.edit().remove(key).apply();
            cachedValues.remove(key);
        }
    }
    public void putObjectAsJson(String key,
                                Object obj) {
        String json = new Gson().toJson(obj);
        putStringItem(key, json);
    }

    public <T> T getObjectFromJson(String key,
                                   Type objClass) {
        String json = getStringItem(key);
        return new Gson().fromJson(json, objClass);
    }

    public void putStringItem(String key,
                              String value) {
        putStringItem(key, value, true, true);
    }

    public String getStringItem(String key) {
        return getStringItem(key, true, true);
    }


    public void putBooleanItem(String key,
                               Boolean value) {
        String stringValue = value != null ? value.toString() : null;
        cachedValues.put(key, value);
        putStringItem(key, stringValue, false, true);
    }
    public void putLongItem(String key,
                            Long value) {
        String stringValue = value != null ? value.toString() : null;
        cachedValues.put(key, value);
        putStringItem(key, stringValue, false, true);
    }

    public void clearPreferences() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PrefConstants.LOG).apply();
        editor.clear();
        editor.commit();
        cachedValues.remove(PrefConstants.LOG);
        cachedValues.clear();
    }

}
