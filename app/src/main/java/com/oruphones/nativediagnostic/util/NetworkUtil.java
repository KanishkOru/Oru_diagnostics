package com.oruphones.nativediagnostic.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import org.pervacio.onediaglib.atomicfunctions.AFMobileData;
import org.pervacio.onediaglib.atomicfunctions.AFWiFi;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Prem Raj on 05-05-2021.
 * Utility class to get Network Information
 */
public class NetworkUtil {

    private static String TAG = NetworkUtil.class.getSimpleName();

    public static boolean isWifiStatusOn() {
        return (new AFWiFi()).getState();
    }

    public static boolean isMobileDataOn() {
        return (new AFMobileData()).isMobileDataEnabled();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static boolean isDataRoamingOn(Context context) {
        if (Build.VERSION.SDK_INT >= 29) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                return telephonyManager.isDataRoamingEnabled();
            } else {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT < 29 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SubscriptionManager subMngr = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                int id = SubscriptionManager.getDefaultDataSubscriptionId();
                try {
                    SubscriptionInfo ino = subMngr.getActiveSubscriptionInfo(id);
                    DLog.d(TAG, "From Subscription: " + ino.getDataRoaming());
                    if (ino == null)
                        return false;
                    return ino.getDataRoaming() == 1;
                } catch (Exception e) {
                    DLog.e(TAG, "Exception getting Data Roaming: " + e.getMessage());
                    return false;
                }
            } else if (Build.VERSION.SDK_INT < 17) {
                return (Settings.System.getInt(context.getContentResolver(), Settings.Secure.DATA_ROAMING, 0) == 1);
            } else {
                return (Settings.Global.getInt(context.getContentResolver(), Settings.Global.DATA_ROAMING, 0) == 1);
            }
        }
    }

    public static boolean isDualSimDevice(Context context) {
        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(context);
        return telephonyInfo.isDualSIM();
    }

    public static String getNetworkType(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        DLog.d(TAG, "Network Type: " + networkType);
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "4G";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G";
            default:
                return "NA";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static String getNetworkOperator(Context context, int simNumber, boolean isIccid) {
        String operator = "NA";
        String iccid = "NA";
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                SubscriptionManager subMngr = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                try {
                    List<SubscriptionInfo> SubscriptionList = subMngr.getActiveSubscriptionInfoList();
                for (SubscriptionInfo ino : SubscriptionList) {
                    int simSlot = ino.getSimSlotIndex();
                    DLog.d(TAG, "simSlot="+simSlot);
                    if (simSlot == simNumber) {
                        operator = ino.getCarrierName().toString();
                        iccid = ino.getIccId();
                    }
                }
            } catch(Exception e){
                e.printStackTrace();
            }
        } else {
                DLog.d(TAG, "getNetworkOperator: Permission Denied");
            }
        return isIccid?iccid:operator;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static String getDefaultMobileDataOperator(Context context) {
        String defaultDataOperator = "NA";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            SubscriptionManager subMngr = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            int id = -1;
            if(Build.VERSION.SDK_INT >=24) {
                 id = SubscriptionManager.getDefaultDataSubscriptionId();
            } else {
                Method method_getSmsDefaultSim;

                Object tm = context.getSystemService(Context.TELEPHONY_SERVICE);
                int smsDefaultSim = -1;
                try {
                    method_getSmsDefaultSim = tm.getClass().getDeclaredMethod("getDefaultSim");
                    id = (Integer) method_getSmsDefaultSim.invoke(tm);
                    DLog.d(TAG, "getDefaultMobileData Sim id: " + smsDefaultSim);
                } catch (Exception e) {
                    id = 0;
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    DLog.d(TAG, "Exception: " + e.getMessage());
                }
            }
            try {
                SubscriptionInfo ino = subMngr.getActiveSubscriptionInfo(id);
                defaultDataOperator = ino.getCarrierName().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DLog.d(TAG, "getDefaultMobileDataOperator: Permission Denied");
        }
        return defaultDataOperator;
    }
}