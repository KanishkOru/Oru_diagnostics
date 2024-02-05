package com.oruphones.nativediagnostic.util.DevelopmentTools;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class Receivers {
    private static Receivers receiversInstance;
    private Context context;
    private boolean Result = false;
    private BroadcastReceiver bluetoothReceiver;
    private BroadcastReceiver wifiReceiver;
    private BroadcastReceiver locationReceiver;
    private BroadcastReceiver mediaReceiver;
    private BroadcastReceiver batteryReceiver;
    private BroadcastReceiver connectivityReceiver;
    private BroadcastReceiver smsReceiver;
    private BroadcastReceiver callReceiver;
    private BroadcastReceiver bootReceiver;
    private BroadcastReceiver screenReceiver;
    public static final String BLUETOOTH_RECEIVER = "bluetooth";
    public static final String WIFI_RECEIVER = "wifi";
    public static final String LOCATION_RECEIVER = "location";
    public static final String MEDIA_RECEIVER = "media";
    public static final String BATTERY_RECEIVER = "battery";
    public static final String CONNECTIVITY_RECEIVER = "connectivity";
    public static final String SMS_RECEIVER = "sms";
    public static final String CALL_RECEIVER = "call";
    public static final String BOOT_RECEIVER = "boot";
    public static final String SCREEN_RECEIVER = "screen";

    private Receivers(Context context) {
        this.context = context;
    }

    public static Receivers getInstance(Context context) {
        if (receiversInstance == null) {
            receiversInstance = new Receivers(context);
        }
        return receiversInstance;
    }

    public boolean registerReceiver(String receiverName) {
        switch (receiverName) {
            case BLUETOOTH_RECEIVER:
                bluetoothReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                            int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                            switch (bluetoothState) {
                                case BluetoothAdapter.STATE_ON:
                                    Result = true;
                                    DLog.d("Receivers" + Result);
                                    break;
                                case BluetoothAdapter.STATE_OFF:
                                    Result = false;
                                    DLog.d("Receivers" + Result);
                                    break;
                            }
                        }
                    }
                };
                IntentFilter bluetoothFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                context.registerReceiver(bluetoothReceiver, bluetoothFilter);
                break;

            case WIFI_RECEIVER:
                wifiReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Implement Wi-Fi receiver logic here
                    }
                };
                IntentFilter wifiFilter = new IntentFilter(/* Add Wi-Fi related action here */);
                context.registerReceiver(wifiReceiver, wifiFilter);
                break;

            case LOCATION_RECEIVER:
                locationReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Implement location receiver logic here
                    }
                };
                IntentFilter locationFilter = new IntentFilter(/* Add location related action here */);
                context.registerReceiver(locationReceiver, locationFilter);
                break;

            case MEDIA_RECEIVER:
                mediaReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Implement media receiver logic here
                    }
                };
                IntentFilter mediaFilter = new IntentFilter(/* Add media related action here */);
                context.registerReceiver(mediaReceiver, mediaFilter);
                break;

            case BATTERY_RECEIVER:
                batteryReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Implement battery receiver logic here
                    }
                };
                IntentFilter batteryFilter = new IntentFilter(/* Add battery related action here */);
                context.registerReceiver(batteryReceiver, batteryFilter);
                break;

            case CONNECTIVITY_RECEIVER:
                connectivityReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Implement connectivity receiver logic here
                    }
                };
                IntentFilter connectivityFilter = new IntentFilter(/* Add connectivity related action here */);
                context.registerReceiver(connectivityReceiver, connectivityFilter);
                break;

            case SMS_RECEIVER:
                smsReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Implement SMS receiver logic here
                    }
                };
                IntentFilter smsFilter = new IntentFilter(/* Add SMS related action here */);
                context.registerReceiver(smsReceiver, smsFilter);
                break;

            case CALL_RECEIVER:
                callReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Implement call receiver logic here
                    }
                };
                IntentFilter callFilter = new IntentFilter(/* Add call related action here */);
                context.registerReceiver(callReceiver, callFilter);
                break;

            case BOOT_RECEIVER:
                bootReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Implement boot receiver logic here
                    }
                };
                IntentFilter bootFilter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
                context.registerReceiver(bootReceiver, bootFilter);
                break;

            case SCREEN_RECEIVER:
                screenReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Implement screen receiver logic here
                    }
                };
                IntentFilter screenFilter = new IntentFilter(/* Add screen related action here */);
                context.registerReceiver(screenReceiver, screenFilter);
                break;

            default:
                break;
        }
        return Result;
    }

    public boolean unregisterReceiver(String receiverName) {
        switch (receiverName) {
            case BLUETOOTH_RECEIVER:
                if (bluetoothReceiver != null) {
                    context.unregisterReceiver(bluetoothReceiver);
                    bluetoothReceiver = null;
                    return true;
                }
                break;

            case WIFI_RECEIVER:
                if (wifiReceiver != null) {
                    context.unregisterReceiver(wifiReceiver);
                    wifiReceiver = null;
                    return true;
                }
                break;

            case LOCATION_RECEIVER:
                if (locationReceiver != null) {
                    context.unregisterReceiver(locationReceiver);
                    locationReceiver = null;
                    return true;
                }
                break;

            case MEDIA_RECEIVER:
                if (mediaReceiver != null) {
                    context.unregisterReceiver(mediaReceiver);
                    mediaReceiver = null;
                    return true;
                }
                break;

            case BATTERY_RECEIVER:
                if (batteryReceiver != null) {
                    context.unregisterReceiver(batteryReceiver);
                    batteryReceiver = null;
                    return true;
                }
                break;

            case CONNECTIVITY_RECEIVER:
                if (connectivityReceiver != null) {
                    context.unregisterReceiver(connectivityReceiver);
                    connectivityReceiver = null;
                    return true;
                }
                break;

            case SMS_RECEIVER:
                if (smsReceiver != null) {
                    context.unregisterReceiver(smsReceiver);
                    smsReceiver = null;
                    return true;
                }
                break;

            case CALL_RECEIVER:
                if (callReceiver != null) {
                    context.unregisterReceiver(callReceiver);
                    callReceiver = null;
                    return true;
                }
                break;

            case BOOT_RECEIVER:
                if (bootReceiver != null) {
                    context.unregisterReceiver(bootReceiver);
                    bootReceiver = null;
                    return true;
                }
                break;

            case SCREEN_RECEIVER:
                if (screenReceiver != null) {
                    context.unregisterReceiver(screenReceiver);
                    screenReceiver = null;
                    return true;
                }
                break;

            default:
                break;
        }
        return false;
    }

    public boolean isRegistered(String receiverName) {
        switch (receiverName) {
            case BLUETOOTH_RECEIVER:
                return bluetoothReceiver != null;

            case WIFI_RECEIVER:
                return wifiReceiver != null;

            case LOCATION_RECEIVER:
                return locationReceiver != null;

            case MEDIA_RECEIVER:
                return mediaReceiver != null;

            case BATTERY_RECEIVER:
                return batteryReceiver != null;

            case CONNECTIVITY_RECEIVER:
                return connectivityReceiver != null;

            case SMS_RECEIVER:
                return smsReceiver != null;

            case CALL_RECEIVER:
                return callReceiver != null;

            case BOOT_RECEIVER:
                return bootReceiver != null;

            case SCREEN_RECEIVER:
                return screenReceiver != null;

            default:
                break;
        }
        return false;
    }
}
