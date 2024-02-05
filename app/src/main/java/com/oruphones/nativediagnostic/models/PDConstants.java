/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oruphones.nativediagnostic.models;

import com.google.gson.Gson;

/**
 *
 * @author Shyam
 */
public interface PDConstants {
    public static final Gson gson=new Gson();
    // Common Constants
    public static final String PDPASS = "PASS";
    public static final String PDFAIL = "FAIL";
    public static final String PDCANBEIMPROVED = "CANBEIMPROVED";
    public static final String PDIMPROVED = "IMPROVED";
    public static final String PDACCESSDENIED = "ACCESSDENIED";
    public static final String PDNOTEQUIPPED = "NOTEQUIPPED";
    public static final String PDINPROGRESS = "INPROGRESS";
    public static final String PDCOMPLETE = "PDCOMPLETE";
    public static final String PDDONE = "PDDONE";
    public static final String PDAUTOPASSTRUE = "AUTOPASSTRUE";
    public static final String OPTIMIZABLE = "OPTIMIZABLE";
    public static final String OPTIMIZED = "OPTIMIZED";
    

     //public static final String PDCANOTIMPROVED         ="CANOTIMPROVED";
    public static final String PDON = "ON";
    public static final String PDOFF = "OFF";

    public static final String PDTRUE = "TRUE";
    public static final String PDFALSE = "FALSE";

    public static final String PDENABLED = "ENABLED";
    public static final String PDDISABLED = "DISABLED";

    public static final String PDYES = "YES";
    public static final String PDNO = "NO";

    // Device Information Screens
    public static final String AUTO_TEST_ANALYSIS_RESULTS = "AutoTestRsltsAnly";
    public static final String DEVICE_INFORMATION = "DevInformation";
    public static final String DEVICE_BATTERY_INFORMATION = "DevBatInformation";
    public static final String RUN_ALL_AUTO_TESTS = "RunAllAutoTests";
    public static final String AUTO_TEST_RESULT = "AutoTestResults";
    public static final String MANUAL_TEST_RESULT = "ManualTestResults";
    public static final String MANUAL_TEST_LIST1 = "ManulTestList1";
    public static final String MANUAL_TEST_LIST2 = "ManulTestList2";
    public static final String MANUAL_TEST_LIST3 = "ManulTestList3";
    public static final String DIAGNOSTICS_RESULT = "DiagnosticsResults";
    public static final String DIAGNOSTICS_RESULT_BEFORE = "DiagnosticsResultsBefore";
    public static final String DIAGNOSTICS_RESULT_AFTER = "DiagnosticsResultsAfter";

    // Physical Damage Categories
//    public static final String PHYSICAL_DAMAGE          = "PhysicalDamage";
//    public static final String WATER_DAMAGE             = "WaterDamage";
//    public static final String SCREEN_DAMAGE            = "ScreenDamage";
    // Issue Categories
    public static final String SELECT_ISSUE = "SelectIssue";
    public static final String BATTERY_CHARGING = "BatteryCharging";
    public static final String SYSTEM_CRASH = "SystemCrash";
    public static final String CONNECTIVITY ="connectivity";
    public static final String AUDIO_VIBRATE ="audio_vibrate";
    public static final String CAMERA ="camera";
    public static final String DISPLAY_TOUCH ="displaytouch";
    public static final String APPS ="apps";
    public static final String HARDWARE = "hardware";
    public static final String RUN_ALL_DIAGNOSTICS = "RunAllDiagnostics";
    public static final String QUICK_CHECK ="quickcheck";
    public static final String TRADE_IN = "TradeIn";
    public static final String VERIFY = "VerifyDevice";
    public static final String BUYER_VERIFY = "BuyerVerification";
    public static final String FINAL_VERIFY = "FinalVerification";

    // Battery Issue Sub Categories
    public static final String BATTERY_DRAINS_QUICKLY = "BatteryDrainsQuickly";
    public static final String BATTERY_WONT_CHARGE = "BatteryWontCharge";
    public static final String BATTERY_OTHER = "BatteryOther";
    public static final String SYSTEM_CRASH_OTHER = "SystemCrashOther";
    public static final String INTERNERT_CONNECTIVITY = "InternetConnectivity";
    public static final String CALL_CONNECTIVITY = "CallConnectivity";
    public static final String CONNECTIVITYOTHER = "connectivityOther";
    public static final String AUDIO_ISSUES = "AudioIssues";
    public static final String VIBRATION_ISSUES = "VibrationIssues";
    
    public static final String AUDIO_VIBRATEOTHER = "audio_vibrateOther";
    public static final String PHOTO_ISSUES = "PhotoIssues";
    public static final String VIDEO_ISSUES = "VideoIssues";
    public static final String CAMERA_OTHER = "cameraOther";
    public static final String DISPLAY_ISSUES = "DisplayIssues";
    public static final String TOUCH_ISSUES = "TouchscreenIssues";
    public static final String DISPLAY_TOUCHOTHER = "displaytouchOther";
    public static final String APPLICATIONOTHER = "appsOther";
    public static final String APPDONOTINSTALL = "AppsDontInstall";
    
    //Resolution
    public static final String OS = "OS";
    public static final String POWER_SAVING = "Battery";
    public static final String APP = "Apps";
    public static final String CONNECTIVITY_RES = "Connectivity";
    public static final String MEMORY = "Memory";
    public static final String STORAGE = "Storage";
    public static final String Hardware = "Hardware";
    public static final String Physical = "Physical";
    public static final String Tradein = "TradeIn";
    

    // Application Issue Sub Categories
    public static final String APPLICATION_ISSUES = "ApplicationIssues";
    public static final String SCREEN_FREEZES = "ScreenFreezes";
    public static final String DEVICE_REBOOTS = "DeviceReboots";

    // Auto Test Categories
   
    public static final String BATTERY = "Battery";
    
    public static final String SETTINGS = "Settings";
    public static final String CONNECTIONS = "Connections";
    public static final String APPLICATIONS = "Applications";
    public static final String MEMORYSTORAGE = "MemoryStorage";
    // public static final String HARDWARE              = "Hardware";
    // Already this Macro is defined.

    // Auto Tests
    public static final String BLUETOOTH_ON = "BluetoothOnTest";
    public static final String BLUETOOTH_OFF = "BluetoothOffTest";
    public static final String BLUETOOTH_TOGGLE = "BluetoothToggleTest";
    
    public static final String BRIGHTNESS = "Brightness";
    
    public static final String GPS_ON = "GPSOnTest";
    public static final String GPS_OFF = "GPSOffTest";
    public static final String GPS_TOGGLE = "GPSToggleTest";
    
    public static final String NFC_ON = "NFCOnTest";
    public static final String NFC_OFF = "NFCOffTest";
    public static final String NFC_TOGGLE = "NFCToggleTest";
    
    public static final String MOBILE_HOTSPOT_ON = "MobileHotSpotOnTest";
    public static final String MOBILE_HOTSPOT_OFF = "MobileHotSpotOffTest";
    public static final String MOBILE_HOTSPOT_TOGGLE = "MobileHotSpotToggleTest";
    
    public static final String SCREEN_TIMEOUT = "Screen timeout";
    public static final String WIFI_ON = "WLANOnTest";
    public static final String WIFI_OFF = "WLANOffTest";
    public static final String WIFI_TOGGLE = "WLANToggleTest";
    
    public static final String UNINSTALL = "Uninstall application";
 //   public static final String JAILBROKEN = "Jailbroken/Rooted";
	public static final String ROOTED="Rooted";
	public static final String JAILBROKEN="Jailbroken";
    public static final String FIRMWARE = "Firmware";
    public static final String OWNERSHIPCHECK="OwnershipCheckTest";
    public static final String LIVEWALLPAPER = "LiveWallpaper";
    public static final String BATTERYCONDITION = "Battery Condition";
    public static final String BATTERYFULLDISCHARGE = "Battery full discharge";
    public static final String SIMCARD = "SIM card";
    public static final String FREEMEMORY = "Free memory";
    public static final String INTERNALSTORAGE = "Internal Storage";
    public static final String READWRITECAPACITY = "Read/Write Capacity";
    public static final String SDCARD = "SD Card";
    public static final String SDCARDREADWRITE = "SD card read/write";
    public static final String SDCARDCAPACITY = "SD card capacity";
    public static final String AIRPLANEMODE = "Airplane mode";
    public static final String MALWAREAPPS = "Malware Apps";
    public static final String RISKYAPPS = "Risky Apps";
    public static final String BATTERYCONSUMINGAPPS = "Battery Draining Apps";
    public static final String ADWAREAPPS = "Adware Apps";
    public static final String BANDWIDTHAPPS = "Bandwidth Consuming Apps";
    public static final String OUTDATEDAPPS = "Outdated Apps";
    public static final String LTETEST = "LTETest";
    public static final String SECURITYLOCKTEST = "SecurityLockTest";
    public static final String IMEITest = "IMEITest";
    public static final String SPENTEST = "SPenTest";
    public static final String SPENHOVERINGTEST = "SPenHoveringTest";
    public static final String TSPHOVERINGTEST = "TSPHoveringTest";
    public static final String BLUETOOTHCONNECTIVITYTEST = "BluetoothConnectivityTest";
    public static final String FINGERPRINTSENSORTEST = "FingerPrintSensorTest";
    public static final String BATTERYRESOLUTIONSTART = "BatteryResolutionStart";
    public static final String POWERSAVINGRESOLUTIONSTART = "BatteryResolutionStart";
    public static final String DEEPDIVEFAILS = "DeepDiveTestFails";
    public static final String BATTERYRESOLUTION = "BatteryResolution";
    public static final String BATTERYRESOLUTIONDISABLEAPPS = "BatteryResolutionDisableApps";
    public static final String BATTRESOLBATTERYCONSUMEAPPS = "BattResolBatteryConsumeApps";
    public static final String BATTERYRESOLUTIONSUMMARY = "BatteryResolutionSummary";
    public static final String SETTINGSRESOLUTIONSUMMARY = "SettingsResolutionSummary";
    public static final String APPRESOLUTIONSTART = "AppResolutionStart";
    public static final String OSRESOLUTION = "OsResolution";
    public static final String OSRESOLUTIONSTART = "OsResolutionStart";
    public static final String FIRMWAREOUTOFDATE= "FirmwareOutOfDate";
    public static final String APPRESOLUTION = "AppResolution";
    public static final String APPRESOLUTIONSUMMARY = "AppResolutionSummary";
    public static final String APPLICATIONDETAILS = "ApplicationDetails";
    public static final String ACCELEROMETERTEST = "Accelerometer test";
    public static final String ACCELEROMETERTESTSTART = "AccelerometerTestStart";
    public static final String ACCELEROMETERTESTRESULT = "AccelerometerTestResult";
    public static final String MEMORYRESOLUTIONSTART = "MemoryResolutionStart";
    public static final String MEMORYRESOLUTION = "MemoryResolution";
    public static final String MEMORYRESOLUTIONSUMMARY = "MemoryResolutionSummary";
    public static final String MEMORYRESOLUTIONBACKGROUND = "MemoryResolutionBackground";
    public static final String MEMORYRESOLUTIONFOREGROUND = "MemoryResolutionForeground";
    public static final String MEMORYRESOLUTIONAUTOSTART = "MemoryResolutionAutoStart";
    public static final String STORAGERESOLUTIONSTART = "StorageResolutionStart";
    public static final String STORAGERESOLUTIONS = "StorageResolution";
    public static final String STORAGERESOLUTIONSDUPLICATEFILES = "StorageResolutionDuplicateFiles";
    public static final String STORAGERESOLUTIONAPPS = "StorageResolutionApps";
    public static final String STORAGERESOLUTIONIMAGE = "StorageResolutionImage";
    public static final String STORAGERESOLUTIONVIDEO = "StorageResolutionVideo";
    public static final String STORAGERESOLUTIONMUSIC = "StorageResolutionMusic";
    public static final String STORAGERESOLUTIONTEMPFILES = "StorageResolutionTempFiles";
    public static final String STORAGERESOLUTIONSUMMARY = "StorageResolutionSummary";
    public static final String STORAGERESOLUTIONSDCARD= "StorageResolutionSdcard";
    public static final String FIRMWARERESOLUTION= "AutoTestsFirmware";
    public static final String RESOLUTIONLIST = "ResolutionList";
    
    public static final String SORTORDERNAME = "Name";
    public static final String SORTORDERDATE = "Date";
    public static final String SORTORDERSIZE = "Size";
    public static final String APPRISKY = "Risky";
    public static final String APPADWARE = "Adware";
    public static final String APPBANDWIDTH = "BandwidthConsume";
    public static final String APPBATTERY = "BattryConsume";
    public static final String APPOUTDATED = "Outdated";
    public static final String APPMALWARE = "Malware";

    // Auto Test Results
    public static final String NONE = "NONE";           // NONE - BLACK-
    public static final String NORESULT = "NORESULT";  
    public static final String WORKS_FINE = "WORKS_FINE";     // NEED NOT IMPROVE - GREEN
    public static final String REPAIR = "REPAIR";         // TO BE IMPROVED - LIGHT BLUE
    public static final String REPAIED = "REPAIED";        // IMPROVED BY OUR APP - DARK BLUE
    public static final String CANNOT_IMPROVE = "CANNOT_IMPROVE"; // CANNOT IMPROVE   - RED
    public static final String INFO = "INFO";           // ANY OTHER INFO - GREY

    // HW Tests
    public static final String CALLTEST = "Call test";
    public static final String CALLTESTSTART = "Call test try";
    public static final String HEADSETTEST = "Headset test";
    public static final String HEADSETAUDIOTEST = "Headset audio test";
    public static final String SPEAKERTEST = "Speaker test";
    public static final String SPEAKERTESTPLAY = "SpeakerTestPlay";
    public static final String SPEAKERTESTRESULT = "SpeakerTestResult";
    public static final String EARPIECETEST = "Earpiece test";
    public static final String EARPIECETESTPLAY = "EarpieceTestPlay";
    public static final String EARPIECETESTRESULT = "EarpieceTestResult";
    public static final String MICROPHONETEST = "Microphone test";
    
    public static final String COMPREHENSIVEEARPHONETEST = "ComprehensiveEarphoneTest";
    
    public static final String REARCAMERAPICTURETEST = "Rear camera picture test";
    public static final String REARCAMERAPICTURETESTTRY = "RearCameraPictureTestTry";
    public static final String REARCAMERAPICTURETESTCAPTURE = "RearCameraPictureTestCapture";
    public static final String REARCAMERAPICTURERESULT = "RearCameraPictureTestResult";

    public static final String REARCAMERAVIDEOTEST = "Rear camera video test";
    public static final String REARCAMERAVIDEOTESTTRY = "RearCameraVideoTestTry";
    public static final String REARCAMERAVIDEOTESTRECORD = "RearCameraVideoTestRecord";
    public static final String REARCAMERAVIDEOTESTPLAY = "RearCameraVideoTestPlay";
    public static final String REARCAMERAVIDEORESULT = "RearCameraVideoTestResult";

    public static final String FRONTCAMERAPICTURETEST = "Front camera picture test";
    public static final String FRONTCAMERAPICTURETESTTRY = "FrontCameraPictureTestTry";
    public static final String FRONTCAMERAPICTURETESTCAPTURE = "FrontCameraPictureTestCapture";
    public static final String FRONTCAMERAPICTURERESULT = "FrontCameraPictureTestResult";

    public static final String FRONTCAMERAVIDEOTEST = "Front camera video test";
    public static final String FRONTCAMERAVIDEOTESTTRY = "FrontCameraVideoTestTry";
    public static final String FRONTCAMERAVIDEOTESTRECORD = "FrontCameraVideoTestRecord";
    public static final String FRONTCAMERAVIDEOTESTPLAY = "FrontCameraVideoTestPlay";
    public static final String FRONTCAMERAVIDEORESULT = "FrontCameraVideoTestResult";

    public static final String TOUCHTEST = "Touch test";
    public static final String TOUCHSCREENTESTTRY = "Touch test try";
    public static final String TOUCHSCREENTESTRESULT = "TouchTestResult";
    public static final String EARPHONEJACKTESTSTART = "EarPhoneJackTestStart";
    public static final String VIBRATIONTEST = "Vibration test";
    public static final String VIBRATIONTESTSTART = "VibrationTestStart";
    public static final String VIBRATIONTESTRESULT = "VibrationTestResult";
    public static final String HARDKEYTEST = "Hard keys test";
    public static final String HARDKEYTESTTRY = "Hard keys test try";
    public static final String SOFTKEYTEST = "Soft keys test";
    public static final String SOFTKEYTESTTRY = "Soft keys test try";
    public static final String GUESTURETEST = "Gesture test";
    public static final String GUESTURETESTTRY = "Gesture Test try";
    public static final String AMBIENTTEST = "Ambient light test";
    public static final String AMBIENTTESTTRY = "Ambient light test try";
    public static final String PROXIMITYTEST = "Proximity test";
    public static final String PROXIMITYTESTTRY = "Proximity test try";
    public static final String MICROPHONETESTSPEAK = "MicrophoneSpeak";
    public static final String MICROPHONETESTPLAY = "MicrophonePlay";
    public static final String MICROPHONETESTRESULT = "MicrophoneResult";
    public static final String RECEIVERTEST = "Receiver test";
    public static final String RECEIVERTESTRESULT = "ReceiverTestResult";
    public static final String EARPHONETEST = "EarPhone test";
    public static final String EARPHONEJACKTEST ="Earphone jack test";
    public static final String SCREENCRACKDETECTIONTEST ="ScreenCrackDetectionTest";
    public static final String EARPHONETESTPLAY = "EarPhoneTestPlay";
    public static final String EARPHONETESTRESULT = "EarPhoneTestResult";
    public static final String DIMMINGTEST = "Dimming test";
    public static final String DIMMINGTESTSTART = "DimmingTestStart";
    public static final String DIMMINGTESTRESULT = "DimmingTestResult";
    public static final String DISPLAYTEST = "Display test";
    public static final String LCDTEST = "Display test";
    public static final String LCDTESTSTART = "LCDTestStart";
    public static final String LCDTESTRESULT = "LCDTestResult";

    public static final String DISPLAYTESTSTART = "DisplayTestStart";
    public static final String DISPLAYTESTRESULT = "DisplayTestResult";
    public static final String CAMERAFLASHTEST = "Camera flash test";
    public static final String CAMERAFLASHTESTSTART = "CameraFlasTestStart";
    public static final String CAMERAFLASHTESTRESULT = "CameraFlashResult";
    public static final String USBTEST = "USB connection test";
    public static final String SLEEPTEST = "Sleep test";
    public static final String USBTESTTESTSTART = "USBTestStart";
    public static final String USBTESTTESTRESULT = "USBResult";
    public static final String CHARGINGTEST = "Charging test";
    public static final String CHARGINGTESTSTART = "ChargingTestStart";
    public static final String BATTERY_TEST_LAUNCH = "BatteryTestLaunch";
    public static final String BATTERY_TEST_START = "BatteryTestStart";
    public static final String BATTERY_TEST_RESULT = "BatteryTestResult";
    public static final String BATTERY_TEST_ANALYSIS = "BatteryTestAnalysis";
    public static final String SPENTESTSTARTTRY = "SPenTestTry";
    public static final String SPENTESTRESULT = "SPenTestResult";
    public static final String BLUETOOTHCONNECTIVITYTESTSTART = "BluetoothConnectivityTestStart";
    public static final String COMPREHENSIVEEARPHONETESTSTART = "ComprehensiveEarphoneTestStart";
    public static final String COMPREHENSIVEEARJACKTESTSTART = "ComprehensiveEarjackTestStart";
    public static final String COMPREHENSIVEEARPHONETESTRESULT = "ComprehensiveEarphoneTestResult";
    public static final String SPENHOVERINGTESTSTARTTRY = "SPenHoveringTestTry";
    public static final String SPENHOVERINGTESTRESULT = "SPenHoveringTestResult";
    public static final String TSPHOVERINGTESTSTARTTRY = "TSPHoveringTestTry";
    public static final String TSPHOVERINGTESTRESULT = "TSPHoveringTestResult";

    public static final String CHARGINGTESTRESULT = "ChargingResult";
    public static final String APPRESOLUTIONRISKY = "AppResolutionRisky";
    public static final String APPRESOLUTIONMALWARE = "AppResolutionMalware";
    public static final String APPRESOLUTIONADWARE = "AppResolutionAdware";
    public static final String APPRESOLUTIONBTRCNSM = "AppResolutionBtrCnsm";
    public static final String APPRESOLUTIONOUTDATED = "AppResolutionOutDated";
    public static final String APPRESOLUTIONBANDWIDTH = "AppResolutionBandwidth";
   
    public static final String END_SESSION = "EndSession";

    // Did It Improve
    public static final String BACKUP = "Backup";
    public static final String FACTORY_RESET = "FactoryReset";
    public static final String TOOLTIP = "Tooltip";
    public static final String RESTART_DIAGNOSTICS = "RestartDiagnostics";

    public static final String PIN_INVALID = "PIN_INVALID";
    public static final String MANUALTERMINATION = "MANUALTERMINATION";
    public static final String UNKNOWNERROR = "UNKNOWNERROR";
    
    public static final String GYROSCOPESENSORTEST = "GyroscopeSensorTest";
    public static final String MAGNETICSENSORTEST = "MagneticSensorTest";
    public static final String BAROMETERTEST = "BarometerTest";
    public static final String GPSCOMPREHENSIVETEST = "GPSComprehensiveTest";
    public static final String WIFICOMPREHENSIVETEST = "WiFIComprehensiveTest";
    public static final String BLUETOOTHCOMPREHENSIVETEST = "BluetoothComprehensiveTest";
    public static final String ALL_TEST_RESULTS = "AllTestsResults";
    public static final String ALL_TESTS_SUMMARY = "AllTestsSummary";
    
    public static final String FINGERPRINTTESTSTART = "FingerprintTestStart";
    public static final String FINGERPRINTTESTTRY = "Fingerprint sensor test try";
    public static final String FINGERPRINTTESTRESULT = "FingerprintTestResult";
    
    public static final String FINGERPRINTAUTHTEST = "FingerprintAuthTest";
    public static final String FINGERPRINTAUTHTRY = "FingerprintAuthtry";
    public static final String FINGERPRINTAUTHRESULT = "FingerprintAuthResult";

    public static final String EXIT_DIAG_IN_MIDDLE = "Exit_Diag_In_Middle";
}
