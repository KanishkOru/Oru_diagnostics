package com.oruphones.nativediagnostic.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public  final class AnimatedGifUtils implements TestName {

    public static  void  addToView(LinearLayout ll , Context context , String testName){



        ImageView imageView = new ImageView(context);

            if(FRONTCAMERAVIDEOTEST.equalsIgnoreCase(testName)||REARCAMERAVIDEOTEST.equalsIgnoreCase(testName)||
            FINGERPRINTSENSORTEST.equalsIgnoreCase(testName)||CAMERAFLASHTEST.equalsIgnoreCase(testName)||
            FRONTFLASHTEST.equalsIgnoreCase(testName)||USBTEST.equalsIgnoreCase(testName)||
            CHARGINGTEST.equalsIgnoreCase(testName)||CALLTEST.equalsIgnoreCase(testName)||

            VIBRATIONTEST.equalsIgnoreCase(testName)||SPEAKERTEST.equalsIgnoreCase(testName)||
            MICROPHONETEST.equalsIgnoreCase(testName)||MICROPHONE2TEST.equalsIgnoreCase(testName)||
            EARPIECETEST.equalsIgnoreCase(testName)||FRONTCAMERAPICTURETEST.equalsIgnoreCase(testName)||REARCAMERAPICTURETEST.equalsIgnoreCase(testName)

            ){  //for setting png
                ll.removeAllViews();
                imageView.setImageBitmap(getNewPNGImageView(context, manualtestGifMap.get(testName)));
                ll.addView(imageView);
             }
            else if(DISPLAYTEST.equalsIgnoreCase(testName)||DIMMINGTEST.equalsIgnoreCase(testName)||
                    TOUCHTEST.equalsIgnoreCase(testName)||EARPHONEJACKTEST.equalsIgnoreCase(testName)||
                    EARPHONETEST.equalsIgnoreCase(testName)||PROXIMITYTEST.equalsIgnoreCase(testName)||
                    ACCELEROMETERTEST.equalsIgnoreCase(testName)||AMBIENTTEST.equalsIgnoreCase(testName)||
                    SPENTEST.equalsIgnoreCase(testName)||
                    HARDKEYTEST.equalsIgnoreCase(testName)||DEADPIXELTEST.equalsIgnoreCase(testName)||

                    DISCOLORATIONTEST.equalsIgnoreCase(testName)||SCREENBURNTEST.equalsIgnoreCase(testName)||NFCTEST.equalsIgnoreCase(testName)||WIFICONNECTIVITYTEST.equalsIgnoreCase(testName) ||BLUETOOTHCONNECTIVITYTEST.equalsIgnoreCase(testName)||BLUETOOTH_TOGGLE.equalsIgnoreCase(testName)

            ) {
                //for setting gif
                ll.removeAllViews();
                ll.addView(CommonUtil.getNewGIFMovieView(context, manualtestGifMap.get(testName)));
                 }
            else {
                ll.removeAllViews();
                imageView.setImageBitmap(getNewPNGImageView(context, manualtestGifMap.get("DEFAULT_ICON")));
                ll.addView(imageView);
            }

    }


    public static  void  setResultIcon(LinearLayout ll , Context context , String flag){

        ImageView imageView = new ImageView(context);

        if(flag.equalsIgnoreCase(TestResult.PASS)){
            ll.removeAllViews();
            imageView.setImageBitmap(getNewPNGImageView(context, "success.png"));
            ll.addView(imageView);
        }else {
            ll.removeAllViews();
            imageView.setImageBitmap(getNewPNGImageView(context, "fail.png"));
            ll.addView(imageView);
        }
    }


//    public static  void  add_testImage(FrameLayout ll , Context context , String flag){
//
//        ImageView imageView = new ImageView(context);
//
//        if(flag.equalsIgnoreCase("SIMCardTest")){
//            ll.setBackgroundResource(R.drawable.icon_success);
//            imageView.setImageBitmap(getNewPNGImageView(context, manualtestGifMap.get("SIMCardTest")));
//            ll.addView(imageView);
//        }else if(flag.equalsIgnoreCase("LastRestart")){
//            ll.setBackgroundResource(R.drawable.icon_success);
//            imageView.setImageBitmap(getNewPNGImageView(context,manualtestGifMap.get("LastRestart") ));
//            ll.addView(imageView);
//        }
//        else {
//            ll.removeAllViews();
//            imageView.setImageBitmap(getNewPNGImageView(context, "gear.png"));
//            ll.addView(imageView);
//        }
//    }
//




    public static Bitmap getNewPNGImageView(Context context, String name) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(name);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }





    public static HashMap<String, String> manualtestGifMap = new HashMap<String, String>();
    static {
        manualtestGifMap.put(DISPLAYTEST,"display_animated.gif");
        manualtestGifMap.put(DIMMINGTEST,"dimming_animated.gif");
        manualtestGifMap.put(TOUCHTEST,"dimming_animated.gif");
        manualtestGifMap.put(EARPHONEJACKTEST,"earphone_jack.gif");
        manualtestGifMap.put(EARPHONETEST,"earphone_jack.gif");
        manualtestGifMap.put(PROXIMITYTEST,"ambiwnt.gif");
        manualtestGifMap.put(ACCELEROMETERTEST,"rotate_phone.gif");
        manualtestGifMap.put(AMBIENTTEST,"ambiwnt.gif");
        manualtestGifMap.put(HARDKEYTEST,"hard_key.gif");

        manualtestGifMap.put(SPENTEST,"spen.gif");
        manualtestGifMap.put(DEADPIXELTEST,"display_animated.gif");
        manualtestGifMap.put(DISCOLORATIONTEST,"display_animated.gif");
        manualtestGifMap.put(SCREENBURNTEST,"display_animated.gif");
        manualtestGifMap.put(NFCTEST, "nfc_test_manual.gif");
        manualtestGifMap.put(WIFICONNECTIVITYTEST,"wifi_toggle_hardware.gif");
        manualtestGifMap.put(BLUETOOTHCONNECTIVITYTEST, "bluetooth_toggle_hardware.gif");
        manualtestGifMap.put(BLUETOOTH_TOGGLE, "bluetooth_toggle_hardware.gif");



        /***  png */

        manualtestGifMap.put(FRONTCAMERAVIDEOTEST,"front_cam.png");
        manualtestGifMap.put(REARCAMERAVIDEOTEST,"rear_cam.png");
        manualtestGifMap.put(FINGERPRINTSENSORTEST,"fingerprint.png");
        manualtestGifMap.put(CAMERAFLASHTEST,"camera_flash.png");
        manualtestGifMap.put(FRONTFLASHTEST,"flash.png");
        manualtestGifMap.put(USBTEST,"usb.png");
        manualtestGifMap.put(CHARGINGTEST,"charging.png");
        manualtestGifMap.put(CALLTEST,"call_test.png");


        manualtestGifMap.put(VIBRATIONTEST,"phone_vibration.png");
        manualtestGifMap.put(SPEAKERTEST,"speaker.png");
        manualtestGifMap.put(MICROPHONETEST,"mic.png");
        manualtestGifMap.put(MICROPHONE2TEST,"mic.png");
        manualtestGifMap.put(EARPIECETEST,"call_test.png");
        manualtestGifMap.put(FRONTCAMERAPICTURETEST,"front_cam.png");
        manualtestGifMap.put(REARCAMERAPICTURETEST,"rear_cam.png");
        manualtestGifMap.put("DEFAULT_ICON","gear.png");




        manualtestGifMap.put("SIMCardTest","usb.png");
        manualtestGifMap.put("LastRestart","charging.png");
    }









    public static HashMap<String, Integer> autoTestIcon = new HashMap<String, Integer>();


    public static HashMap<String, String> autoTestIcon2 = new HashMap<String, String>();


    static {

        autoTestIcon.put("AccelerometerTest", R.drawable.accelerometer);
        autoTestIcon.put("GyroscopeSensorTest", R.drawable.ic_gyroscope);
        autoTestIcon.put("RotationVectorSensorTest", R.drawable.ic_screen_rotation);
        autoTestIcon.put("LastRestart", R.drawable.ic_phonerestart);
        autoTestIcon.put("InternalStorageCapacityTest", R.drawable.ic_internal_storage_capacity);
        autoTestIcon.put("SIMCardTest", R.drawable.ic_sim_card);
        autoTestIcon.put("ScreenBrightnesTest", R.drawable.ic_screen_brightness);
        autoTestIcon.put("LiveWallpaperTest", R.drawable.ic_live_wallpaper);
        autoTestIcon.put("ScreenTimeoutTest", R.drawable.ic_screen_timeout);
        autoTestIcon.put("BarometerTest", R.drawable.ic_barometer);
        autoTestIcon.put("WLANOnTest", R.drawable.ic_wifi);
        autoTestIcon.put("QuickBatteryAutoTest", R.drawable.ic_quick_battery_auto);
        autoTestIcon.put("VibrationTest", R.drawable.ic_vibration);
        autoTestIcon.put("GameRotationSensorTest", R.drawable.ic_game_rotation_sensor);
        autoTestIcon.put("GeomagneticRotationSensorTest", R.drawable.ic_geomagnetic_rotation_sensor);
        autoTestIcon.put("MagneticSensorTest", R.drawable.ic_magnetic_sensor);
        autoTestIcon.put("GenuineOSTest",R.drawable.ic_genuinos);
        autoTestIcon.put("RAMMemoryTest",R.drawable.ic_ram_memo);
        autoTestIcon.put("SDCardTest",R.drawable.ic_sdcard);
        autoTestIcon.put("WifiHardwaretest", R.drawable.ic_test_wifi);


        autoTestIcon2.put("RearCameraPictureTest", "ic_rear_camera_picture.png");
        autoTestIcon2.put("FrontCameraPictureTest", "ic_front_camera_picture.png");
        autoTestIcon2.put("EarpieceTest", "ic_speaker.png");
        autoTestIcon2.put("SpeakerTest", "ic_speaker.png");
        autoTestIcon2.put("MicTest", "ic_mic.png");
        autoTestIcon2.put("Mic2Test", "ic_mic.png");
        autoTestIcon2.put("LinearAccelerationSensorTest", "ic_linear_acceleration_sensor.png");
        autoTestIcon2.put("UnusedApp", "ic_unused_app.png");
        autoTestIcon2.put("BluetoothOnTest", "ic_bluetooth.png");
        autoTestIcon2.put("BluetoothOffTest", "ic_bluetooth.png");
        autoTestIcon2.put("GPSOffTest", "ic_gps.png");
        autoTestIcon2.put("GPSOnTest", "ic_gps.png");
        autoTestIcon2.put("BluetoothToggleTest", "ic_bluetooth.png");
        autoTestIcon2.put("NFCOnTest","ic_nfc.png");
        autoTestIcon2.put("NFCOffTest","ic_nfc.png");
        autoTestIcon2.put("GoogleLockCheckTest","ic_lock.png");
        autoTestIcon2.put("PinPasscodePatternCheckTest","ic_passcode.png");


    }




    public static void setImageRes(FrameLayout ll , Context context , String testName){

        ImageView imageView = new ImageView(context);

                if(
                testName.equalsIgnoreCase("GyroscopeSensorTest") ||
                testName.equalsIgnoreCase("RotationVectorSensorTest") ||
                testName.equalsIgnoreCase("LastRestart") ||
                testName.equalsIgnoreCase("InternalStorageCapacityTest") ||
                testName.equalsIgnoreCase("SIMCardTest") ||
                testName.equalsIgnoreCase("ScreenBrightnesTest") ||
                testName.equalsIgnoreCase("LiveWallpaperTest") ||
                testName.equalsIgnoreCase("ScreenTimeoutTest") ||
                testName.equalsIgnoreCase("BarometerTest") ||
                testName.equalsIgnoreCase("WLANOnTest") ||
                testName.equalsIgnoreCase("QuickBatteryAutoTest") ||
                testName.equalsIgnoreCase("VibrationTest") ||
                testName.equalsIgnoreCase("GameRotationSensorTest")||
                testName.equalsIgnoreCase("GeomagneticRotationSensorTest")||
                testName.equalsIgnoreCase("MagneticSensorTest")||
                testName.equalsIgnoreCase("GenuineOSTest")||
                testName.equalsIgnoreCase("RAMMemoryTest") || testName.equalsIgnoreCase("SDCardTest")||testName.equalsIgnoreCase("AccelerometerTest") ||
                        testName.equalsIgnoreCase("WifiHardwaretest")

                 )
        {  ll.removeAllViews();
            imageView.setImageResource(autoTestIcon.get(testName));
            ll.addView(imageView);
        }else if(
                        testName.equalsIgnoreCase("RearCameraPictureTest") ||
                                testName.equalsIgnoreCase("FrontCameraPictureTest") ||
                                testName.equalsIgnoreCase("EarpieceTest") ||
                                testName.equalsIgnoreCase("SpeakerTest") ||
                                testName.equalsIgnoreCase("MicTest") ||
                                testName.equalsIgnoreCase("Mic2Test") ||
                                testName.equalsIgnoreCase("LinearAccelerationSensorTest") ||
                                testName.equalsIgnoreCase("UnusedApp") ||
                                testName.equalsIgnoreCase("BluetoothOnTest") ||
                                testName.equalsIgnoreCase("BluetoothOffTest") ||
                                testName.equalsIgnoreCase("GPSOffTest") ||
                                testName.equalsIgnoreCase("GPSOnTest") ||
                                testName.equalsIgnoreCase("BluetoothToggleTest")||
                                testName.equalsIgnoreCase("NFCOnTest") ||
                                testName.equalsIgnoreCase("NFCOffTest") ||
                                testName.equalsIgnoreCase("GoogleLockCheckTest") ||
                                testName.equalsIgnoreCase("GPSPinPasscodePatternCheckTestOnTest")

                ){
            ll.removeAllViews();
//                    LogUtil.printLog(TAG + "#111" ," testname : : "+testName);
            imageView.setImageBitmap(getNewPNGImageView(context,autoTestIcon2.get(testName)));
            ll.addView(imageView);
        }
                else
                    {
                        ll.removeAllViews();
                        imageView.setImageBitmap(getNewPNGImageView(context, "gear.png"));
                        ll.addView(imageView);

                    }



    }



}
