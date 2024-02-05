package com.oruphones.nativediagnostic.manualtests;



import static com.oruphones.nativediagnostic.models.PDConstants.BLUETOOTH_TOGGLE;
import static com.oruphones.nativediagnostic.models.tests.TestName.BLUETOOTHCONNECTIVITYTEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.CALLTEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.CAMERAFLASHTEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.EARPHONETEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.EARPIECETEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.FRONTFLASHTEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.HARDKEYTEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.NFCTEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.SOFTKEYTEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.SPEAKERTEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.VIBRATIONTEST;
import static com.oruphones.nativediagnostic.models.tests.TestName.WIFICONNECTIVITYTEST;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.WifiManualTestUnusedActivity;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.api.ManualTest;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.common.CustomButton;
import com.oruphones.nativediagnostic.models.ManualTestItem;
import com.oruphones.nativediagnostic.models.tests.ManualTestEvent;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.result.ResultsActivity;
import com.oruphones.nativediagnostic.result.ResultsSummeryActivity;
import com.oruphones.nativediagnostic.util.AnimatedGifUtils;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DeviceInfo;
import com.oruphones.nativediagnostic.util.ODDUtils;
import com.oruphones.nativediagnostic.util.PreferenceUtil;
import com.oruphones.nativediagnostic.util.StartLocationAlert;
import com.oruphones.nativediagnostic.util.TestUtil;
import com.oruphones.nativediagnostic.util.Util;
import com.pervacio.batterydiaglib.util.NetworkUtil;

import org.pervacio.onediaglib.diagtests.DiagTimer;
import org.pervacio.onediaglib.diagtests.ITimerListener;
import org.pervacio.onediaglib.utils.AppUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Pervacio on 29/08/2017.
 */

public class ManualTestsTryActivity extends BaseActivity {
    public final int[] testTry = {0};
    /**
     * my edi :sidd
     */

    public TextView result_Display_Name, result_Observation, retry_Button;
    public ImageView result_image_view, test_image;
    BaseActivity baseActivity;
    Button mStartTest, mCancel;
    String mCurrentTest = null;
    private static String TAG = ManualTestsTryActivity.class.getSimpleName();
    TextView prevTest;
    TextView nextTest, nextManualTest;
    TextView mTestName, mTestDescription, test_tile_description, result_text_view;
    TextView testNumView;
    ScrollView scrollView;
    ImageView imageView, prevResultView;
    AlertDialog alertDialog = null;
    LinearLayout prev_tests_list, next_tests_list;
    LinearLayout curr_test_card, manualTestComplete;
    LayoutInflater layoutInflater;
    Button continueBtn;
    View animatedView;
    LinearLayout mGIFMovieViewContainer;
    LinearLayout autoTestComplete;
    RecyclerView recyclerView;
    List<ManualTestItem> dataList;
    View prev_test_view[] = new View[selectedManualTests.size()];
    View next_test_view[] = new View[selectedManualTests.size()];
    Button okButton;
    private Intent getIntent;
    private String mDevicekeys;
    private String sdCardPath;
    private ProgressBar mProgressBar;
    private int currentTestIndex = -1;
    ManualTestTryAdapter adapter;
    Handler resultHandler;
    ProgressBar progressBar;
    public static ArrayMap<Integer, String> excludeNumbers = new ArrayMap<>();

//    private KeyTestAdapter keyTestAdapter;

    private boolean mIsTestFinished=false;
    private ArrayList<keyPOJO> keysPOJOArray = new ArrayList<keyPOJO>();
    private ArrayList<String> mDevicekeysList;

    private ArrayList<String> audioTest = new ArrayList<String>();

    DiagTimer mDiagTimer;


    {
        audioTest.add(SPEAKERTEST);
        audioTest.add(TestName.MICROPHONETEST);
        audioTest.add(TestName.MICROPHONE2TEST);
        audioTest.add(EARPHONETEST);
        audioTest.add(EARPIECETEST);
    }





    class keyPOJO {
        private boolean checked = false;
        private String keyName;

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String getKeyName() {
            return keyName;
        }

        public void setKeyName(String keyName) {
            this.keyName = keyName;
        }
    }
    private Handler getHandler(String TestName){
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String result = msg.getData().getString("result");
                if (TestResult.ACCESSDENIED.equalsIgnoreCase(result)) {
                    manualTestResultDialog(TestName, result, ManualTestsTryActivity.this);
                } else{



                    if (isAssistedApp) {
                        sendManualTestResultToServer(TestName, "COMPLETED");
                    }

                }
            }
        };
        return handler;
    }



    public static void startActivity(Activity activity, String currTest, String reTest) {
        Intent intent = new Intent(activity, ManualTestsTryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(TEST_NAME, reTest);   // send next test
        intent.putExtra("currentTest", currTest); // send current test result
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DLog.d(TAG, "isRestry  [ ManualTesttry ]: " + globalConfig.isIsRetry());

        DLog.d(TAG, "ManualTestTryActivity" + selectedManualTestsResult.size());
        getIntent = getIntent();

        ArrayList<String> receivedDataList = getIntent().getStringArrayListExtra("PassTest");


        if (receivedDataList!=null && !receivedDataList.isEmpty()){

            DLog.d(TAG, "Item: " + receivedDataList);
            ShowAlertDialog(receivedDataList);
        }


        RecyclerviewUI();
        /********************************************** */

        /* this will inflate card view of current test with name , description , start , skip button */
        mCurrentTest = getIntent.getStringExtra(TEST_NAME);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        mStartTest = (Button) findViewById(R.id.accept_tv);
        mCancel = (Button) findViewById(R.id.cancel_tv);
        mCancel.setText(R.string.str_skip);
        mStartTest.setText(R.string.str_start_test);
        mTestName = (TextView) findViewById(R.id.current_test_view);
        mTestDescription = (TextView) findViewById(R.id.test_desc_view);

        /**  my edi :sidd*/

//        animatedGIFll

        mGIFMovieViewContainer = findViewById(R.id.animatedGIFll);

//        testNumView = findViewById(R.id.test_num_view);
        curr_test_card = findViewById(R.id.curr_test_card_view);    //activity_manual_test_new (linear layout)
        //autoTestComplete = findViewById(R.id.autoTestComplete);   // ok button with test
        //  okButton = findViewById(R.id.okButton);
        test_tile_description = (TextView) findViewById(R.id.testOneLineDecription);
        retry_Button = findViewById(R.id.retry_button_manual_test);
        layoutInflater = LayoutInflater.from(getApplicationContext());


        /********************************************** */

        if (mCurrentTest.equalsIgnoreCase("EndTest")) {
            continueBtn = (Button) findViewById(R.id.continueBtn);
            manualTestComplete = findViewById(R.id.manualTestComplete);
            manualTestComplete.setVisibility(View.VISIBLE);
            curr_test_card.setVisibility(View.GONE);
            continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(globalConfig.isVerification()) {
                        DLog.d("");
                        lastSellerTest();
                    }
                    else {
                        ResultsActivity.start(ManualTestsTryActivity.this);
                    }
                    finish();
                }
            });
        }





//        if (manualStart) { // set true in  welcomeScreenActivity
//
//
//            startTestUI();
//
//        } else {
//            /** my implementation */
//            setPrevTestResultUI();
//            nextTestUI();
//        }

//        prevTest = findViewById(R.id.prev_test_view);
//        prevResultView = findViewById(R.id.prev_manual_test_result_img);
//        nextTest = findViewById(R.id.next_test_view);
//        nextManualTest = findViewById(R.id.next_manual_test);

        if (selectedManualTestsResult.size() > 2 && !globalConfig.isIsRetry()) {
           // autoScroll(scrollView);
        }


        if (selectedManualTestsResult.size() > 2 && globalConfig.isIsRetry() && selectedManualTests.indexOf(mCurrentTest) - 1 > 1) {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, prev_test_view[selectedManualTests.indexOf(mCurrentTest) - 1].getTop());
                }
            });
        }

        currentTestIndex = selectedManualTests.indexOf(mCurrentTest);
        imageView = (ImageView) findViewById(R.id.manual_test_img);
        if (!mCurrentTest.equalsIgnoreCase("EndTest")) {
            DLog.d(TAG, "test name " + mCurrentTest);
            imageView.setImageResource(TestUtil.manualtestImageMap.get(mCurrentTest));
        }
//        testNumView.setText(getString(R.string.manual_test_number, currentTestIndex + 1, selectedManualTests.size()));
//        prevResultView = findViewById(R.id.prev_manual_test_result_img);
//        if (currentTestIndex > 0) {
////            String mTestResult = getIntent.getStringExtra(TEST_RESULT);
//////            String mTestResult = baseActivity.selectedManualTestsResult.get(mCurrentTest);
////            Log.d("ManualTestTryActivity", "mTestResult: " + mTestResult + " currentTestIndex: " + currentTestIndex + " selectedManualTests.size(): " + selectedManualTests.size());
////            prevTest.setText(PervacioTest.getInstance().getGlobalConfig().getTestDisplayName(selectedManualTests.get(currentTestIndex - 1)));
////            if (mTestResult != null) {
////                if (mTestResult.equalsIgnoreCase(TestResult.PASS)) {
////                    prevResultView.setImageResource(R.drawable.ic_passed);
////                } else if (mTestResult.equalsIgnoreCase(TestResult.FAIL)) {
////                    prevResultView.setImageResource(R.drawable.ic_failed);
////                } else if (mTestResult.equalsIgnoreCase(TestResult.SKIPPED)) {
////                    prevResultView.setImageResource(R.drawable.ic_skipped);
////                }
////            }
//
//        } else {
//            prevTest.setText("");
//            prevResultView.setVisibility(View.INVISIBLE);
//        }
//        if (currentTestIndex < selectedManualTests.size() - 1)
//            nextTest.setText(getDisplayName(selectedManualTests.get(currentTestIndex + 1)));
//        else {
//            nextTest.setText("");
//            nextManualTest.setVisibility(View.INVISIBLE);
//        }
        if (Util.disableSkip()) {
            mCancel.setVisibility(View.GONE);
        }

        if (mCurrentTest != null && !mCurrentTest.equalsIgnoreCase("EndTest")) {

            mTestName.setText(getDisplayName(mCurrentTest));
            mCurrentManualTest = mCurrentTest;
            int resourceID = getResourceID(mCurrentTest, TEST_TRY_MESAGE, Util.disableSkip());
            if (resourceID == 0) {
                resourceID = getResourceID(mCurrentTest, TEST_TRY_MESAGE);
            }

            if (mCurrentTest.equalsIgnoreCase(BLUETOOTH_TOGGLE)) {
                mTestDescription.setText("BLUETOOTH TEST");
            } else if(mCurrentTest.equalsIgnoreCase(WIFICONNECTIVITYTEST)) {
                mTestDescription.setText(getResources().getText(R.string.WifiDescription));

            } else if (mCurrentTest.equalsIgnoreCase(NFCTEST)) {
                mTestDescription.setText(getResources().getText(R.string.nfc_test_tile_desc));

            } else {

                mTestDescription.setText(resourceID);
            }


            /** my edit */
            try {
                AnimatedGifUtils.addToView(mGIFMovieViewContainer, getApplicationContext(), mCurrentTest);
            } catch (Exception e) {
                DLog.e(TAG + "AnimatedGifUtils", "Error on finding GIF for current Test:" + mCurrentTest);
            }

//            Toast.makeText(getApplicationContext(),"nameSidd :"+ AnimatedGifUtils.manualtestGifMap.get(mCurrentTest),Toast.LENGTH_SHORT).show();

            //imageView.setImageResource(getResourceID(mCurrentTest, TEST_TRY_IMAGE));
        }
//        if (TestName.HARDKEYTEST.equalsIgnoreCase(mCurrentTest)) {
//            mDevicekeys = PervacioTest.getInstance().getGlobalConfig().getDeviceHardKeys();
//        } else {
//            mDevicekeys = PervacioTest.getInstance().getGlobalConfig().getDeviceSoftKeys();
//        }
        if (SOFTKEYTEST.equalsIgnoreCase(mCurrentTest)) {
            if (mDevicekeys != null && mDevicekeys.contains("HOME")) {
                TestUtil.manualtestGifMap.put(SOFTKEYTEST, "softkeyhome.gif");
            }
        }

        // As per customer requirement, Volume level is set to 60% for ear-phone test.
        // hence this warning is not required.
        /* if(TestName.EARPHONETEST.equalsIgnoreCase(mCurrentTest)){
            showWorning(1);
        }*/
        if ((mCurrentTest.equalsIgnoreCase(FRONTFLASHTEST) || mCurrentTest.equalsIgnoreCase(CAMERAFLASHTEST)) && Util.showEpilepsyPopUp()) {
            showWorning(3);
        }

        mStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPermissionGranted()) {
                    if (storageAvailable()) {
                        if (SPEAKERTEST.equalsIgnoreCase(mCurrentTest) || EARPIECETEST.equalsIgnoreCase(mCurrentTest) || TestName.MICROPHONETEST.equalsIgnoreCase(mCurrentTest) || TestName.MICROPHONE2TEST.equalsIgnoreCase(mCurrentTest)) {
                            checkBluetoothHeadphoneConnected(mCurrentTest);
                        }
//                        else if (TestName.WIFI_MANUAL_TEST.equalsIgnoreCase(mCurrentTest)) {
//                            startManualTestWithResult(mCurrentTest, ManualTestsTryActivity.this, WifiManualTestActivity.RC_WIFI_MANUAL);
//                        }/*else if(TestName.FINGERPRINTSENSORTEST.equalsIgnoreCase(mCurrentTest)){
//                            startManualTestWithResult(mCurrentTest,ManualTestsTryActivity.this,FingerPrintTestActivity.RC_FP_MANUAL);
//                        }
                    else {
//                            if(TestName.PROXIMITYTEST.equalsIgnoreCase(mCurrentTest)||TestName.AMBIENTTEST.equalsIgnoreCase(mCurrentTest)||TestName.EARPHONEJACKTEST.equalsIgnoreCase(mCurrentTest)||TestName.USBTEST.equalsIgnoreCase(mCurrentTest)||TestName.CHARGINGTEST.equalsIgnoreCase(mCurrentTest)||TestName.FINGERPRINTSENSORTEST.equalsIgnoreCase(mCurrentTest)) {
//                                Intent intent = new Intent(getApplicationContext(), MiddleActivity.class);
//                                intent.putExtra("test_name", mCurrentTest);
//                                startActivity(intent);
//                            }else{
//                            isRetry=false;
                            startManualTest(mCurrentTest);
//                            }
                        }

                    } else {
                        showResultMessage();
                    }
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DLog.d(TAG, "cancel test name....." + mCurrentTest);
                selectedManualTestsResult.put(mCurrentTest, TestResult.SKIPPED);
                updateTestResult(mCurrentTest, TestResult.SKIPPED);
//                isRetry=false;
                startNextManualTest();

//                mGIFMovieViewContainer.addView(CommonUtil.getNewGIFMovieView(getApplicationContext(), AnimatedGifUtils.manualtestGifMap.get(mCurrentTest)));

                try {
                    AnimatedGifUtils.addToView(mGIFMovieViewContainer, getApplicationContext(), mCurrentTest);
                } catch (Exception e) {
                    DLog.e(TAG + "AnimatedGifUtils", "Error on finding GIF for current Testv:" + mCurrentTest);
                }
            }
        });
        setFontToView(mCancel, OPENSANS_MEDIUM);
        setFontToView(mStartTest, OPENSANS_MEDIUM);


        /*setFontToView(mTestDescription,ROBOTO_LIGHT);
        setFontToView(mTestName,ROBOTO_LIGHT);*/
    }


//    private String formatDataList(ArrayList dataList) {
//        StringBuilder formattedText = new StringBuilder();
//        for (int index = 0; index < dataList.size(); index++) {
//            String item = dataList.get(index).toString();
//            formattedText.append(index + 1).append(". ").append(item);
//
//            if (index < dataList.size() - 1) {
//                formattedText.append("\n");
//            }
//        }
//        return formattedText.toString();
//    }

    private void RecyclerviewUI(){

        recyclerView = findViewById(R.id.recyclerviewManualTest);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        dataList = new ArrayList<>();
        adapter = new ManualTestTryAdapter(dataList,getApplicationContext(),recyclerView,globalConfig);

        for (int i = 0; i < selectedManualTests.size(); i++) {
            String testName = selectedManualTests.get(i);
            String displayName = getDisplayName(testName);
            if (displayName==null){
                displayName = getResources().getString(getResourceID(testName,0));
            }
            String description="";
            String Testdescription="";
            String testResult = selectedManualTestsResult.get(testName);



            int resourceID = getResourceID(testName, TEST_TRY_MESAGE, Util.disableSkip());
            if (resourceID == 0) {
                resourceID = getResourceID(testName, TEST_TRY_MESAGE);
            }


            Testdescription = getResources().getString(resourceID);


            try {

                description = getResources().getString(getResourceID(testName, TEST_RESULT_MESAGE));

            }catch (Exception e) {
                e.printStackTrace();

            }
            DLog.d("ManualTestResult",testResult  + testName );

            ManualTestItem currentItemData = new ManualTestItem(displayName, description, Testdescription,testName,testResult);
            dataList.add(currentItemData);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DLog.e("Manual Test Try activity  onDestroy Called");
    }

    private String formatDataList(ArrayList<String> dataList) {
        StringBuilder formattedText = new StringBuilder();
        for (int index = 0; index < dataList.size(); index++) {
            String item = dataList.get(index).toString();

            // Format camel case
            String formattedItem = formatCamelCase(item);

            formattedText.append(index + 1).append(". ").append(formattedItem);

            if (index < dataList.size() - 1) {
                formattedText.append("\n");
            }
        }
        return formattedText.toString();
    }

    private String formatCamelCase(String input) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);

            if (i > 0 && Character.isUpperCase(currentChar)) {
                // Add space before capital letter (except for the first character)
                result.append(" ");
            }

            result.append(currentChar);
        }

        return result.toString();
    }


    private void ShowAlertDialog(ArrayList dataList) {

        CardView manualalertcard = findViewById(R.id.ManualAlertDialog);
        View view = LayoutInflater.from(ManualTestsTryActivity.this).inflate(R.layout.custom_alert_dialog, manualalertcard);
        Button Okay = view.findViewById(R.id.OkBtn);

        TextView messageText = view.findViewById(R.id.messageTextView);


        messageText.setText(formatDataList(dataList));


        AlertDialog.Builder builder = new AlertDialog.Builder(ManualTestsTryActivity.this);
        builder.setView(view);

        final AlertDialog alertdialog = builder.create();


        Okay.findViewById(R.id.OkBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertdialog.dismiss();
            }
        });

        if (alertdialog.getWindow() != null) {
            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        }

        alertdialog.show();


    }


    public void startTestUI() {


//
        curr_test_card.setVisibility(View.GONE);    /**  hide current test card view we built above */
        //autoTestComplete.setVisibility(View.VISIBLE);  /**   show ok button with text above it */
        final boolean[] currTest = {false};
        for (int i = 0; i < (selectedManualTests.size()); i++) {
//            try {
//                if (next_test_view != null)
//                    if (next_test_view[i].getParent() != null) {
//                        ((ViewGroup) next_test_view[i].getParent()).removeAllViews();
////                prev_tests_list.removeAllViews();
//                    }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            next_test_view[i] = layoutInflater.inflate(R.layout.manual_test_result, null, false);  //3 element view { image  name  result ]
            next_tests_list = findViewById(R.id.next_test_list_view);    //linear layout for above list


            /**************************************  it will fill all the view ( 3 element row view ) in the list " next_text_view "  we just have to inflate them one by one */
            result_Display_Name = (TextView) next_test_view[i].findViewById(R.id.result_test_name_result);
            result_image_view = (ImageView) next_test_view[i].findViewById(R.id.result_image);
            test_image = (ImageView) next_test_view[i].findViewById(R.id.test_image);
            test_tile_description = (TextView) next_test_view[i].findViewById(R.id.testOneLineDecription);

            String testName = selectedManualTests.get(i);

            result_Display_Name.setText(getDisplayName(testName));

            try {
                test_tile_description.setText(getResourceID(testName, TEST_RESULT_MESAGE));
            }
             catch (Exception e)
             {
                 test_tile_description.setText(R.string.this_will_test_the_device_health);
             }

            //  result_image_view.setImageResource(R.drawable.ic_not_equipped);
            DLog.d(TAG, testName);
            test_image.setImageResource(TestUtil.manualtestImageMap.get(testName));

            /**********************************************************************/

        }


// its creating the list of test
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < (selectedManualTests.size()); i++) {
//                    if (prev_test_view[i].getParent() != null) {
//                        ((ViewGroup) prev_test_view[i].getParent()).removeAllViews();
//                    }
//                    prev_tests_list.removeView(prev_test_view);
                    if (selectedManualTests.get(i).equalsIgnoreCase(mCurrentTest)) {

                        currTest[0] = true;  /** check whether the current test is present in the list of test or not  if current test match to list name , set this boolean array true */
                    }
                    if (currTest[0]) {  /** if it is true , test found then */
                        try {
                            next_tests_list.addView(next_test_view[i]);   /** add that 3 element view ( row )   to this LinearLayout */
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        // ok button continue code

        for (int i = 0; i < (selectedManualTests.size()); i++) {
            if (next_test_view[i].getParent() != null) {
                ((ViewGroup) next_test_view[i].getParent()).removeAllViews();
            }
        }
        //   autoTestComplete.setVisibility(View.GONE);  /** remove ok with text above*/
        curr_test_card.setVisibility(View.VISIBLE);  /** show current test view card */

//        setPrevTestResultUI();
//        nextTestUI();


        manualStart = false;


//        okButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                for (int i = 0; i < (selectedManualTests.size()); i++) {
//                    if (next_test_view[i].getParent() != null) {
//                        ((ViewGroup) next_test_view[i].getParent()).removeAllViews();
//                    }
//                }
//                autoTestComplete.setVisibility(View.GONE);  /** remove ok with text above*/
//                curr_test_card.setVisibility(View.VISIBLE);  /** show current test view card */
//
//                setPrevTestResultUI();
//                nextTestUI();
//
//
//                manualStart = false;
//            }
//        });

    }


//    public void setPrevTestResultUI() {
//
//        int f = selectedManualTestsResult.size();
//        DLog.d(TAG, "totalCountPrev01=" + f);
//        if (globalConfig.isIsRetry()) {
//            f = selectedManualTests.indexOf(globalConfig.getRetryTestName());
//        }
//        DLog.d(TAG, "totalCountPrev01=" + f);
//
//        for (int i = 0; i < f; i++) {
//
//            prev_test_view[i] = layoutInflater.inflate(R.layout.manual_test_result, null, false);
//            prev_tests_list = findViewById(R.id.prev_test_list_view);     // list above current test view
//
//            /*******************************************************  set/ connect every view in list to its component , name , test img, result img , tests observation */
//            result_Observation = (TextView) prev_test_view[i].findViewById(R.id.result_test_observation);
//            result_Display_Name = (TextView) prev_test_view[i].findViewById(R.id.result_test_name_result);
//            result_image_view = (ImageView) prev_test_view[i].findViewById(R.id.result_image);
//            test_image = (ImageView) prev_test_view[i].findViewById(R.id.test_image);
//            test_tile_description = (TextView) prev_test_view[i].findViewById(R.id.testOneLineDecription);
//            retry_Button = prev_test_view[i].findViewById(R.id.retry_button_manual_test);
//            result_text_view = prev_test_view[i].findViewById(R.id.test_result_text);
//            String testName = selectedManualTests.get(i);
//            String testResult = selectedManualTestsResult.get(testName);  /** calling func from BaseActivity to get specific test result */
//
//            if (testName.equalsIgnoreCase(EARPHONETEST) && testResult.equalsIgnoreCase(TestResult.PASS)) {
//                globalConfig.setEarPhoneTestResult(true);
//            }
//            if (testName.equalsIgnoreCase(EARPHONETEST) && (testResult.equalsIgnoreCase(TestResult.SKIPPED)) || (testResult.equalsIgnoreCase(TestResult.FAIL)) || (testResult.equalsIgnoreCase(TestResult.TIMEOUT))) {
//                globalConfig.setEarPhoneTestResult(false);
//            }
////          handle testResult for null
//            if (testResult == null) {    /**  if null make it fail */
//                testResult = TestResult.FAIL;
//            }
//
//            result_Display_Name.setText(getDisplayName(testName));
//            result_Observation.setText(CommonUtil.getMappedTestResult(testResult));
//            if (testName.equalsIgnoreCase(BLUETOOTH_TOGGLE)) {
//                test_tile_description.setText(getResources().getText(R.string.BluetoothToggleTest));
//
//            }
// else if(testName.equalsIgnoreCase(WIFICONNECTIVITYTEST)) {
//                test_tile_description.setText(getResources().getText(R.string.WifiToggleTest));
//
//
//            } else if (testName.equalsIgnoreCase(NFCTEST)) {
//                test_tile_description.setText(getResources().getText(R.string.nfc_test_tile_desc));
//            }else {
//                test_tile_description.setText(getResourceID(testName, TEST_RESULT_MESAGE));
//            }
//
//            /** change result image according to result we get */
//            retry_Button.setVisibility(View.VISIBLE);
//            if (testResult.equalsIgnoreCase(TestResult.PASS)) {
//                result_image_view.setImageResource(R.drawable.success_test);
//                retry_Button.setText("Pass");
//                result_text_view.setVisibility(View.GONE);
//                retry_Button.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
//                prev_test_view[i].setBackgroundResource(R.drawable.test_tile_bg);
//            } else if (testResult.equalsIgnoreCase(TestResult.FAIL) || testResult.equalsIgnoreCase(TestResult.TIMEOUT)) {
//                result_image_view.setImageResource(R.drawable.fail_test);
//                retry_Button.setText("Fail");
//                result_text_view.setVisibility(View.VISIBLE);
//                prev_test_view[i].setBackgroundResource(R.drawable.test_tile_bg_yellow);
//            } else if (testResult.equalsIgnoreCase(TestResult.SKIPPED)) {
//                result_image_view.setImageResource(R.drawable.ic_skipped);
//                result_text_view.setVisibility(View.VISIBLE);
//                prev_test_view[i].setBackgroundResource(R.drawable.test_tile_bg_yellow);
//            } else {
//                result_image_view.setImageResource(R.drawable.ic_skipped);
//                result_text_view.setVisibility(View.VISIBLE);
//                prev_test_view[i].setBackgroundResource(R.drawable.test_tile_bg_yellow);
//            }
//            test_image.setImageResource(TestUtil.manualtestImageMap.get(testName));
//        }
//
//        int finalF = f;
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < finalF; i++) {
////                    try {
//                    if (prev_test_view[i].getParent() != null) {
//                        ((ViewGroup) prev_test_view[i].getParent()).removeAllViews();
//                    }
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                    }
////                    prev_tests_list.removeView(prev_test_view);
//                    try {
//                        prev_tests_list.addView(prev_test_view[i]);  /**  adding our /view row of test to linearLayout view / list of previous tests */
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    String testName = selectedManualTests.get(i);
//                    prev_test_view[i].setOnClickListener(new View.OnClickListener() {
//
//                        String testResult = selectedManualTestsResult.get(testName);
//
//                        @Override
//                        public void onClick(View v) {
//                            if (testResult != null && (testResult.equalsIgnoreCase(TestResult.PASS) || testResult.equalsIgnoreCase(TestResult.OPTIMIZED))) {
//                                DLog.d(TAG, "test : +" + mCurrentTest + "result :" + testResult);
//                            } else {
//                                showRetryDialog(testName);
//                            }
//                        }
//                    });
//                }
//
//                // Minimized bottom margin for last test (retry)
//                if(mCurrentTest.equalsIgnoreCase("EndTest") && globalConfig.getCheckNextList()) {
//                    if (prev_tests_list != null) {
//                        try {
//                            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) prev_tests_list.getLayoutParams();
//                            layoutParams.bottomMargin = 6;
//                            prev_tests_list.setLayoutParams(layoutParams);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//
//
//        });
//    }

    void autoScroll(ScrollView scroll) {
        try {
            scroll.post(new Runnable() {
                @Override
                public void run() {
                    DLog.d(TAG, "ManualTestTryActivity + autoScroll " + selectedManualTestsResult.size());
                    scroll.setSmoothScrollingEnabled(true);
                    scroll.setNestedScrollingEnabled(false);
//                                scrollView.scrollTo(0, (int) curr_test_card.getY() - 200)
//                                Log.d("Scroll", "Scrolling sr of y: " + curr_test_card.getY() + " sr of height: " + (scrollView.getHeight() / 2));
//                                scrollView.smoothScrollTo(0, (int) curr_test_card.getY() - (scrollView.getHeight() / 2));scrollView.fullScroll(View.)
                    if(globalConfig.getRetryTestName() != "" && globalConfig.getCheckScroll()) {
                        globalConfig.setCheckScroll(false);
                        try{
                            scroll.smoothScrollTo(0, prev_test_view[selectedManualTests.indexOf(globalConfig.getRetryTestName())].getTop());
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            scroll.smoothScrollTo(0, prev_test_view[selectedManualTestsResult.size()- 2].getTop());
                        }
                    }else {
                        scroll.smoothScrollTo(0, prev_test_view[selectedManualTestsResult.size()- 2].getTop());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//
//    void showRetryDialog(String testName)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Retry : "+ getDisplayName(testName));
//        builder.setMessage("Are you sure you want to proceed ?");
//        builder.setPositiveButton("PROCEED", (dialog, which) -> {
//            currentT=mCurrentTest;
//            isRetry=true;
//            startManualTest(testName);
//        });
//
//        builder.setNegativeButton("CANCEL", (dialog, which) -> {
//            dialog.dismiss();
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }


    void showRetryDialog(String testName) {
        globalConfig.setRetryTestName(testName);
        if (!globalConfig.isIsRetry()) globalConfig.setLastCurrentTest(mCurrentTest);
        globalConfig.setIsRetry(true);
        globalConfig.setCheckScroll(true);
        ManualTestsTryActivity.startActivity(ManualTestsTryActivity.this, mCurrentTest, testName);
        String lastTestName = selectedManualTests.get(selectedManualTests.size() - 1);
        if(mCurrentTest.equalsIgnoreCase("EndTest") && lastTestName.equalsIgnoreCase(testName)) {
            globalConfig.setCheckNextList(true);
        }
    }


    public void retryTest(String testName) {
        Handler handler = new Handler();
        try {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isPermissionGranted()) {
                        if (storageAvailable()) {
                            if (SPEAKERTEST.equalsIgnoreCase(mCurrentTest) || EARPIECETEST.equalsIgnoreCase(mCurrentTest) || TestName.MICROPHONETEST.equalsIgnoreCase(mCurrentTest) || TestName.MICROPHONE2TEST.equalsIgnoreCase(mCurrentTest)) {
                                checkBluetoothHeadphoneConnected(mCurrentTest);
                            } else if (TestName.WIFI_MANUAL_TEST.equalsIgnoreCase(mCurrentTest)) {
                                startManualTestWithResult(mCurrentTest, ManualTestsTryActivity.this, WifiManualTestUnusedActivity.RC_WIFI_MANUAL);
                            }/*else if(TestName.FINGERPRINTSENSORTEST.equalsIgnoreCase(mCurrentTest)){
                            startManualTestWithResult(mCurrentTest,ManualTestsTryActivity.this,FingerPrintTestActivity.RC_FP_MANUAL);
                        }*/ else {
                                startManualTest(mCurrentTest);
                            }

                        } else {
                            showResultMessage();
                        }
                    }
                }
            }, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public void nextTestUI() {
//
//
//        final boolean[] currTest = {false};
//        for (int i = 0; i < (selectedManualTests.size()); i++) {
////            try {
////                if (next_test_view != null)
////                    if (next_test_view[i].getParent() != null) {
////                        ((ViewGroup) next_test_view[i].getParent()).removeAllViews();
//////                prev_tests_list.removeAllViews();
////                    }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//            String testName = selectedManualTests.get(i);
//            next_test_view[i] = layoutInflater.inflate(R.layout.manual_test_result, null, false);
//            next_tests_list = findViewById(R.id.next_test_list_view);
//            result_Observation = (TextView) next_test_view[i].findViewById(R.id.result_test_observation);
//            result_Display_Name = (TextView) next_test_view[i].findViewById(R.id.result_test_name_result);
//            result_image_view = (ImageView) next_test_view[i].findViewById(R.id.result_image);
//            test_image = (ImageView) next_test_view[i].findViewById(R.id.test_image);
//            test_tile_description = (TextView) next_test_view[i].findViewById(R.id.testOneLineDecription);
//            retry_Button = next_test_view[i].findViewById(R.id.retry_button_manual_test);
//            result_text_view = next_test_view[i].findViewById(R.id.test_result_text);
//            String testResult = selectedManualTestsResult.get(testName);
//
//            if (testName.equalsIgnoreCase(BLUETOOTH_TOGGLE)) {
//                test_tile_description.setText(getResources().getText(R.string.BluetoothToggleTest));
//
//            }  else if (testName.equalsIgnoreCase(NFCTEST)) {
//                test_tile_description.setText(getResources().getText(R.string.nfc_test_tile_desc));
//
//            } else if(testName.equalsIgnoreCase(WIFICONNECTIVITYTEST)){
//                test_tile_description.setText(getResources().getText(R.string.WifiToggleTest));
//            }
//            else {
//                test_tile_description.setText(getResourceID(testName, TEST_RESULT_MESAGE));
//            }
//            result_Display_Name.setText(getDisplayName(testName));
//            //dont add this it will ruin ui |
//            //  result_image_view.setImageResource(R.drawable.ic_not_equipped);
//            test_image.setImageResource(TestUtil.manualtestImageMap.get(testName));
//
//            if(testResult != null) {
//                result_Observation.setText(CommonUtil.getMappedTestResult(testResult));
//                retry_Button.setVisibility(View.VISIBLE);
//                if (testResult.equalsIgnoreCase(TestResult.PASS)) {
//                    result_image_view.setImageResource(R.drawable.success_test);
//                    retry_Button.setText("Pass");
//                    result_text_view.setVisibility(View.GONE);
//                    retry_Button.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
//                    next_test_view[i].setBackgroundResource(R.drawable.test_tile_bg);
//                } else if (testResult.equalsIgnoreCase(TestResult.FAIL) || testResult.equalsIgnoreCase(TestResult.TIMEOUT)) {
//                    result_image_view.setImageResource(R.drawable.fail_test);
//                    retry_Button.setText("Fail");
//                    result_text_view.setVisibility(View.VISIBLE);
//                    next_test_view[i].setBackgroundResource(R.drawable.test_tile_bg_yellow);
//                } else if (testResult.equalsIgnoreCase(TestResult.SKIPPED)) {
//                    result_image_view.setImageResource(R.drawable.ic_skipped);
//                    result_text_view.setVisibility(View.VISIBLE);
//                    next_test_view[i].setBackgroundResource(R.drawable.test_tile_bg_yellow);
//                } else {
//                    result_image_view.setImageResource(R.drawable.ic_skipped);
//                    result_text_view.setVisibility(View.VISIBLE);
//                    next_test_view[i].setBackgroundResource(R.drawable.test_tile_bg_yellow);
//                }
//            }
//        }
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < (selectedManualTests.size()); i++) {
////                    if (prev_test_view[i].getParent() != null) {
////                        ((ViewGroup) prev_test_view[i].getParent()).removeAllViews();
////                    }
////                    prev_tests_list.removeView(prev_test_view);
//                    if (currTest[0]) {
//                        try {
//                            next_tests_list.addView(next_test_view[i]);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (selectedManualTests.get(i).equalsIgnoreCase(mCurrentTest)) {
//                        currTest[0] = true;
//                    }
//
//                    String testName = selectedManualTests.get(i);
//                    next_test_view[i].setOnClickListener(new View.OnClickListener() {
//
//                        String testResult = selectedManualTestsResult.get(testName);
//
//                        @Override
//                        public void onClick(View v) {
//                            if (testResult != null && (testResult.equalsIgnoreCase(TestResult.PASS) || testResult.equalsIgnoreCase(TestResult.OPTIMIZED))) {
//                                DLog.d(TAG, "test : +" + mCurrentTest + "result :" + testResult);
//                            }else if(testResult == null) {}
//                            else {
//                                showRetryDialog(testName);
//                            }
//                        }
//                    });
//                }
//
//                // Adding view if retry test is last test and also removing top margin for fixing UI issues
//                if(mCurrentTest.equalsIgnoreCase("EndTest") && globalConfig.getCheckNextList()) {
//                    if(next_test_view[selectedManualTests.size() - 1] != null) {
//                        next_tests_list.addView(next_test_view[selectedManualTests.size() - 1]);
//                    }
//                    globalConfig.setCheckNextList(false);
//                    if (next_tests_list != null) {
//                        try {
//                            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) next_tests_list.getLayoutParams();
//                            layoutParams.topMargin = 0;
//                            next_tests_list.setLayoutParams(layoutParams);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        });
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ManualTest.getInstance(ManualTestsTryActivity.this).onActivityResult(requestCode,resultCode,data);
        if (requestCode == StartLocationAlert.RC_GPS_SETTINGS) {
            if (resultCode != Activity.RESULT_OK) {
                if (mManualTestName!=null){

                    manualTestResultDialog(mManualTestName, TestResult.FAIL, ManualTestsTryActivity.this);
                }

            } else {

                if (mManualTestName!=null && resultHandler!=null){

                    takeTest(mManualTestName,resultHandler);
                }
            }
        }
        if (resultCode == Activity.RESULT_OK || data == null || data.getExtras() == null)
            return;

        Bundle bundle = data.getExtras();
        int result = bundle.getInt(PreferenceUtil.EX_RESULT, org.pervacio.onediaglib.diagtests.TestResult.RESULT_FAIL);

        switch (requestCode) {
            case WifiManualTestUnusedActivity.RC_WIFI_MANUAL:
                manualTestResultDialog(TestName.WIFI_MANUAL_TEST, TestUtil.getTestResult(result), ManualTestsTryActivity.this);
                break;
           /* case FingerPrintTestActivity.RC_FP_MANUAL:
                String testName = bundle.getString(PreferenceUtil.EX_TEST_NAME);
                manualTestResultDialog(testName, TestUtil.getTestResult(result), ManualTestsTryActivity.this);
                break;*/
        }
    }

    public void checkNumberForSpeaker(String mCurrentTest,View view) {
//    Button spButtons = new androidx.appcompat.widget.AppCompatButton() {
//        @Override
//        public void onClick(View v) {
//
//        }


//         else {
        //        view will be a button and check if the button text is contains to the testNumString or not
        view.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String testNumString = "";
                        if (mCurrentTest.equalsIgnoreCase(CAMERAFLASHTEST)) {
                            testNumString = GlobalConfig.getInstance().getFlashTestNumString();
                        } else if (mCurrentTest.equalsIgnoreCase(SPEAKERTEST) || mCurrentTest.equalsIgnoreCase(EARPIECETEST) || mCurrentTest.equalsIgnoreCase(EARPHONETEST)) {
                            testNumString = GlobalConfig.getInstance().getAudioTestNumString();
                        } else if (mCurrentTest.equalsIgnoreCase(VIBRATIONTEST)) {
                            testNumString = GlobalConfig.getInstance().getVibrationTestNumString();
                        }
                        DLog.d( "Test NumString:  " + testNumString);
                        String finalTestNumString = testNumString;


                        final String[] result = {TestResult.FAIL};


                        Button button = (Button) v;
                        String buttonText = button.getText().toString();
                        try{
                            if (!buttonText.isEmpty()){
                                if (mCurrentTest.equalsIgnoreCase(CAMERAFLASHTEST)) {
                                    globalConfig.addItemToList("Flash Test User Input : "+buttonText);
                                } else if (mCurrentTest.equalsIgnoreCase(SPEAKERTEST) ){


                                    globalConfig.saveIntegerForTest("Speaker User Input" ,Integer.valueOf(buttonText));


                                }else if( mCurrentTest.equalsIgnoreCase(EARPIECETEST) ){

                                    globalConfig.saveIntegerForTest("Earpiece User Input" ,Integer.valueOf(buttonText));

//                            }

                                }else if(mCurrentTest.equalsIgnoreCase(EARPHONETEST)) {

                                    globalConfig.saveIntegerForTest("Earphone User Input" ,Integer.valueOf(buttonText));



                                } else if (mCurrentTest.equalsIgnoreCase(VIBRATIONTEST)) {
                                    globalConfig.addItemToList("Vibration User Input : "+buttonText);

                                }
                            }else{
                                globalConfig.addItemToList(mCurrentTest + "User Input : NULL");
                            }
                        }
                        catch (Exception e){
                            DLog.e(TAG,e);
                        }





                        DLog.d(TAG,  "Button Input: "+buttonText);
                        try {
                            if (finalTestNumString.contains(buttonText)) { //&& !excludeNumbers.containsValue(buttonText)
                                int indx = excludeNumbers.size();
                                DLog.d(TAG, "indx: " + indx + " excludeNumbers: " + excludeNumbers);
                                excludeNumbers.put(indx, buttonText);
//                            check the sequence of the numbers
                                DLog.d(TAG, "excludeNumbers in if: " + finalTestNumString.charAt(indx) + ' ' + excludeNumbers.get(indx).charAt(0));
                                if (finalTestNumString.charAt(indx) != excludeNumbers.get(indx).charAt(0)) {
                                    result[0] = TestResult.FAIL;
                                    manualTestResultDialog(mCurrentTest, result[0], false, ManualTestsTryActivity.this);
                                }
                                DLog.d(TAG, "buttonText: " + buttonText + " is in testNumString: " + finalTestNumString);
                                button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.touch_color)));
                                button.setTextColor(getResources().getColor(R.color.white));
                            } else {
                                DLog.d(TAG, "buttonText: " + buttonText + " is not in testNumString: " + finalTestNumString);
                                button.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                button.setTextColor(getResources().getColor(R.color.white));
                                manualTestResultDialog(mCurrentTest, result[0], false, ManualTestsTryActivity.this);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }




                        DLog.d(TAG, "excludeNumbers: " + excludeNumbers.toString());
//                        if (excludeNumbers.size() == 3) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < excludeNumbers.size(); i++) {
                            stringBuilder.append(excludeNumbers.get(i));
                        }
                        if (finalTestNumString.equalsIgnoreCase(stringBuilder.toString())) {
                            DLog.d(TAG, "excludeNumbers into if: " + excludeNumbers);
                            String selectedNum = stringBuilder.toString();
//                            if (mCurrentTest.equalsIgnoreCase(TestName.SPEAKERTEST) || mCurrentTest.equalsIgnoreCase(TestName.EARPIECETEST)) {
//                                selectedNum = (excludeNumbers.get(0) + excludeNumbers.get(1) + excludeNumbers.get(2));
//                            }
                            if (selectedNum.equalsIgnoreCase(finalTestNumString)) {
                                result[0] = TestResult.PASS;
                            } else {
                                if (ODDUtils.suggestionTestMap.containsKey(mCurrentTest)) {
                                    if (!ODDUtils.suggestionTestMap.get(mCurrentTest)) {
                                        ODDUtils.suggestionTestMap.put(mCurrentTest, true);
                                        result[0] = TestResult.SHOW_SUGGESTION;
                                    } else {
                                        result[0] = TestResult.FAIL;
                                    }
                                } else {
                                    result[0] = TestResult.FAIL;
                                }
                            }
                            excludeNumbers.clear();
                            DLog.d(TAG, "selectedNum: " + result[0] + mCurrentTest);
                            manualTestResultDialog(mCurrentTest, result[0], false, ManualTestsTryActivity.this);
                        }
                    }
                }
        );
//        }


    }

    private void ManualTestCompleteLayout(){
        manualTestComplete = findViewById(R.id.manualTestComplete);
        continueBtn = (Button) findViewById(R.id.continueBtn);
        manualTestComplete.setVisibility(View.VISIBLE);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(globalConfig.isVerification()) {
                    lastSellerTest();
                    startActivity(new Intent(ManualTestsTryActivity.this,ResultsSummeryActivity.class));

                }
                else {
                    startActivity(new Intent(ManualTestsTryActivity.this,ResultsSummeryActivity.class));
                   // ResultsActivity.start(ManualTestsTryActivity.this);
                }
                finish();
            }
        });
    }

    private void takeTest(String testName,Handler handler){
        if(AppUtils.VersionUtils.hasQ())
        {
            DLog.d("Bluetooth Test",mManualTestName + "2");
            if (DeviceInfo.getInstance(this).isGPSEnabled()) {
                if (NetworkUtil.isOnline()) {

                    DLog.d("Bluetooth Test",mManualTestName + "3");
                    ManualTest.getInstance(ManualTestsTryActivity.this).performTest(testName, handler);
                }
            } else {

                DLog.d("Bluetooth Test",mManualTestName + "4");
                new StartLocationAlert(this,null);
            }
        }else{

            DLog.d("Bluetooth Test",mManualTestName + "5");
            ManualTest.getInstance(ManualTestsTryActivity.this).performTest(testName, handler);
        }

    }

    private String mManualTestName;

    @SuppressLint("HandlerLeak")
    private void setUpRecyclerView(){



       ManualTest manualtest = ManualTest.getInstance(ManualTestsTryActivity.this);
        adapter.setOnSkipClickListener(new ManualTestTryAdapter.onSkipClicked() {
            @Override
            public void onSkipClick(String testName,int position, RecyclerView recyclerView) {
                if (position != RecyclerView.NO_POSITION) {


                    updateTestResult(testName,TestResult.SKIPPED,false);
                    adapter.markItemAsCompleted(position);

                    // Find the next position with a test that is not passed.
                    int nextUnattemptedPosition = adapter.findNextUnattemptedPosition(position + 1);

                    if (nextUnattemptedPosition != -1) {
                        globalConfig.setScrollPosition(nextUnattemptedPosition);
                        globalConfig.setState(testName);
                        adapter.scrollToPosition(nextUnattemptedPosition);
                        adapter.notifyCurrentItemChanged(nextUnattemptedPosition);
                    } else {
                        ManualTestCompleteLayout();
                    }
//                    int nextPosition = position + 1;
//                    if (nextPosition < adapter.getItemCount()) {
//                        position = nextPosition;
//                        globalConfig.setScrollPosition(position);
//                        globalConfig.setState(testName);
//                        //lastClickedPosition = position;
//                        adapter.scrollToPosition(position);
//                        adapter.notifyCurrentItemChanged(position);
//
//                        //recyclerView.scrollToPosition(nextPosition);
//                    }else{
//                        ManualTestCompleteLayout();
//                    }
                }
            }
        });

        adapter.setOnFailClickListner(new ManualTestTryAdapter.onFailClicked() {
            @Override
            public void onFailClicked(String testName) {
                manualTestResultDialog(testName,TestResult.FAIL,false,ManualTestsTryActivity.this);
            }
        });

        adapter.setOnRetryClickListner(new ManualTestTryAdapter.onRetryClicked() {
            @Override
            public void onRetryClicked(String testName) {
                manualTestResultDialog(testName,TestResult.PASS,false,ManualTestsTryActivity.this);
            }
        });
        adapter.setOnLastItem(new ManualTestTryAdapter.onLastItem() {
            @Override
            public void onLastItem() {
                ManualTestCompleteLayout();
            }
        });
        adapter.setOnStartClickListner(new ManualTestTryAdapter.onStartClicked() {
            @Override
            public void onStartClick(String testName, int position, TextView timerText, GridView keyLayout, LinearLayout RetryLayout, CustomButton RetryBtn, CustomButton FailBtn, LinearLayout mainLayout, FrameLayout frameLayout, ProgressBar Progressbar, LinearLayout numLayout1, LinearLayout numLayout2, Button viewOne, Button viewTwo, Button viewThree, Button viewFour, Button viewFive, Button viewSix, Button viewSeven, Button viewEight) {
                if (isPermissionGranted()) {
                    if (storageAvailable()) {
                        progressBar = Progressbar;
                        if (testName.equalsIgnoreCase(HARDKEYTEST) || testName.equalsIgnoreCase(SOFTKEYTEST))
                            mDevicekeysList = getKeysList(testName);

                        switch (testName){

                            case CAMERAFLASHTEST:
                            case EARPIECETEST:
                            case EARPHONETEST:
                            case SPEAKERTEST:
                            case FRONTFLASHTEST:
                            case VIBRATIONTEST:
                            case WIFICONNECTIVITYTEST:
                            case BLUETOOTHCONNECTIVITYTEST:
                            case BLUETOOTH_TOGGLE:
                            case CALLTEST:
                                if (SPEAKERTEST.equalsIgnoreCase(testName) || EARPIECETEST.equalsIgnoreCase(testName)){
                                    checkBluetoothHeadphoneConnected(testName);
                                }

                                Progressbar.setVisibility(View.VISIBLE);

                                mainLayout.setVisibility(View.GONE);
                                resultHandler = new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        super.handleMessage(msg);
                                        String result = msg.getData().getString("result");
                                        DLog.d("TestHandler", "result:" + result + "Message:" + msg.what);
                                        if (testName.equalsIgnoreCase(WIFICONNECTIVITYTEST)||testName.equalsIgnoreCase(BLUETOOTHCONNECTIVITYTEST)){
                                            manualTestResultDialog(testName, result, ManualTestsTryActivity.this);
                                        }


                                        if (TestResult.ACCESSDENIED.equalsIgnoreCase(result) || TestResult.TIMEOUT.equalsIgnoreCase(result) ) {

                                            manualTestResultDialog(testName, result, ManualTestsTryActivity.this);
                                        } else if (TestResult.FAIL.equalsIgnoreCase(result)) {

                                            switch (testName){
                                                case EARPHONETEST:
                                                case VIBRATIONTEST:
                                                case CALLTEST:
                                                    manualTestResultDialog(testName, result, ManualTestsTryActivity.this);
                                            }


                                        } else{

                                            if (isAssistedApp) {
                                                sendManualTestResultToServer(testName, "COMPLETED");
                                            }

                                        }

                                        switch (msg.what) {


                                            case 0:
                                                DLog.d("ManualTest","0");
                                                switch (testName){
                                                    case BLUETOOTHCONNECTIVITYTEST:
                                                    case WIFICONNECTIVITYTEST:
                                                    case BLUETOOTH_TOGGLE:
                                                        manualTestResultDialog(testName,TestResult.PASS,false,ManualTestsTryActivity.this);
                                                        Progressbar.setVisibility(View.GONE);
                                                        break;
                                                    case VIBRATIONTEST:
                                                        RetryLayout.setVisibility(View.VISIBLE);
                                                        Progressbar.setVisibility(View.GONE);
                                                        numLayout1.setVisibility(View.VISIBLE);
                                                        numLayout2.setVisibility(View.GONE);
                                                        checkNumberForSpeaker(testName,viewOne);
                                                        checkNumberForSpeaker(testName,viewTwo);
                                                        checkNumberForSpeaker(testName,viewThree);
                                                        checkNumberForSpeaker(testName,viewFour);
                                                        checkNumberForSpeaker(testName,viewFive);
                                                        checkNumberForSpeaker(testName,viewSix);
                                                        checkNumberForSpeaker(testName,viewSeven);
                                                        checkNumberForSpeaker(testName,viewEight);
                                                        break;
                                                }
                                                break;
                                            case 1:
                                                DLog.d("ManualTest","1");
                                                switch (testName){
                                                    case BLUETOOTHCONNECTIVITYTEST:
                                                    case WIFICONNECTIVITYTEST:
                                                    case BLUETOOTH_TOGGLE:
                                                        manualTestResultDialog(testName,TestResult.FAIL,false,ManualTestsTryActivity.this);
                                                        Progressbar.setVisibility(View.GONE);
                                                        break;
                                                    case FRONTFLASHTEST:
                                                    case CAMERAFLASHTEST:
                                                        Progressbar.setVisibility(View.GONE);
                                                        RetryLayout.setVisibility(View.VISIBLE);
                                                        numLayout1.setVisibility(View.VISIBLE);
                                                        numLayout2.setVisibility(View.GONE);
                                                        checkNumberForSpeaker(testName,viewOne);
                                                        checkNumberForSpeaker(testName,viewTwo);
                                                        checkNumberForSpeaker(testName,viewThree);
                                                        checkNumberForSpeaker(testName,viewFour);
                                                        checkNumberForSpeaker(testName,viewFive);
                                                        checkNumberForSpeaker(testName,viewSix);
                                                        checkNumberForSpeaker(testName,viewSeven);
                                                        checkNumberForSpeaker(testName,viewEight);
                                                        break;
                                                }

                                                break;
                                            case 8:
                                                DLog.d("ManualTest","8");
                                                if (testName.equals(CALLTEST)) {
                                                    DLog.d("ManualTest", "81");
                                                    globalConfig.setCurentTestManual(CALLTEST);
                                                    DLog.d("ManualTest", "82");
                                                    recyclerView.scrollToPosition(position);
                                                    adapter.notifyDataSetChanged();
                                                    adapter.swapCurrentAndSkipped(position);
                                                    adapter.notifyItemChanged(position);
                                                } else {
                                                    Progressbar.setVisibility(View.GONE);
                                                    RetryLayout.setVisibility(View.VISIBLE);
                                                    numLayout1.setVisibility(View.VISIBLE);
                                                    numLayout2.setVisibility(View.VISIBLE);
                                                    checkNumberForSpeaker(testName, viewOne);
                                                    checkNumberForSpeaker(testName, viewTwo);
                                                    checkNumberForSpeaker(testName, viewThree);
                                                    checkNumberForSpeaker(testName, viewFour);
                                                    checkNumberForSpeaker(testName, viewFive);
                                                    checkNumberForSpeaker(testName, viewSix);
                                                    checkNumberForSpeaker(testName, viewSeven);
                                                    checkNumberForSpeaker(testName, viewEight);
                                                }



//                                            DLog.d("EarphoneTest","8");
//                                            if (TestResult.USERINPUT.equalsIgnoreCase(result))
//                                                launchResultActivity(TestName.EARPHONETEST);

                                                break;
                                            case ManualTestEvent.VIBRATION_INTENSITY_ZERO:
                                                Toast.makeText(ManualTestsTryActivity.this, getResources().getString(R.string.increase_vibration_intensity), Toast.LENGTH_SHORT).show();
                                                break;
                                            case org.pervacio.onediaglib.diagtests.TestResult.RESULT_PERMISSION_NOT_GRANTED:
                                                if (testName.equalsIgnoreCase(VIBRATIONTEST))
                                                     manualTestResultDialog(testName, TestResult.ACCESSDENIED, ManualTestsTryActivity.this);
                                                break;
                                            case ManualTestEvent.RELUANCH_VIBRATION_TEST:

                                                manualtest.performTest(testName,resultHandler);
                                                break;
                                            case ManualTestEvent.AUDIO_SHOW_ACCESSIBILITY_DIALOGUE:

                                                DLog.d("ManualTest", "ManualTestEvent.AUDIO_SHOW_ACCESSIBILITY_DIALOGUE");
                                                showAcessibilityDialogue();

                                                break;
                                            case ManualTestEvent.AUDIO_SHOW_EARPHONE_DIALOGUE:
                                                DLog.d("ManualTest", "ManualTestEvent.AUDIO_SHOW_EARPHONE_DIALOGUE");
                                                if (myDialog == null) {
                                                    showEarPhonePlugDiaglogue(getResources().getString(R.string.connect_earphones_dialog), "");
                                                }

                                                break;

                                            case ManualTestEvent.AUDIO_EARPHONE_UNPLUG_TOAST:

                                                DLog.d("ManualTest","ManualTestEvent.AUDIO_EARPHONE_UNPLUG_TOAST");
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.ear_jack_unplug_earphones), Toast.LENGTH_SHORT).show();
                                                break;
                                            case ManualTestEvent.AUDIO_EARPHONE_PLUGIN_TOAST:
                                                DLog.d("ManualTest","ManualTestEvent.AUDIO_EARPHONE_PLUGIN_TOAST");
                                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.connect_earphones_toast), Toast.LENGTH_SHORT).show();
                                                break;

                                            default:

                                                    checkNumberForSpeaker(testName,viewOne);
                                                    checkNumberForSpeaker(testName,viewTwo);
                                                    checkNumberForSpeaker(testName,viewThree);
                                                    checkNumberForSpeaker(testName,viewFour);
                                                    checkNumberForSpeaker(testName,viewFive);
                                                    checkNumberForSpeaker(testName,viewSix);
                                                    checkNumberForSpeaker(testName,viewSeven);
                                                    checkNumberForSpeaker(testName,viewEight);

                                                DLog.d("ManualTest","default1");
                                                break;


                                        }
                                    }
                                };
                                RetryBtn.setOnClickListener(view -> {
                                    Progressbar.setVisibility(View.VISIBLE);
                                    mainLayout.setVisibility(View.GONE);
                                    numLayout1.setVisibility(View.GONE);
                                    RetryLayout.setVisibility(View.GONE);
                                    numLayout2.setVisibility(View.GONE);
                                    if (CAMERAFLASHTEST.equalsIgnoreCase(testName)|| FRONTFLASHTEST.equalsIgnoreCase(testName)){
                                        manualtest.performCameraTest(testName, frameLayout, resultHandler);
                                    }else{
                                        manualtest.performTest(testName, resultHandler);
                                    }
                                });
                                FailBtn.setOnClickListener(view -> {
                                    manualTestResultDialog(testName, TestResult.FAIL, false, ManualTestsTryActivity.this);
                                });
                                if (CAMERAFLASHTEST.equalsIgnoreCase(testName)|| FRONTFLASHTEST.equalsIgnoreCase(testName)){
                                    manualtest.performCameraTest(testName, frameLayout, resultHandler);
                                } else if (WIFICONNECTIVITYTEST.equalsIgnoreCase(testName)|| BLUETOOTHCONNECTIVITYTEST.equalsIgnoreCase(testName) || BLUETOOTH_TOGGLE.equalsIgnoreCase(testName)) {

                                    DLog.d("Manual Test",testName);
                                    Progressbar.setVisibility(View.VISIBLE);
                                    if (permissionStatusCheck(testName)) {

                                            mManualTestName = testName;
                                            takeTest(testName,resultHandler);


                                    }


                                } else{
                                    manualtest.performTest(testName, resultHandler);
                                }
                                break;

//                            case HARDKEYTEST:
//                            case SOFTKEYTEST:
//
//                                keyLayout.setVisibility(View.VISIBLE);
//                                mainLayout.setVisibility(View.GONE);
//
//                                @SuppressLint("HandlerLeak")
//                                 Handler keysHandler = new Handler() {
//                                    @Override
//                                    public void handleMessage(Message msg) {
//                                        super.handleMessage(msg);
//                                        if (msg.what == 700) {
//                                            Bundle data = msg.getData();
//                                            String resultData = null;
//                                            PDTestResult testResult = null;
//                                            if (data != null) {
//                                                resultData = data.getString("result");
//                                            }
//                                            if (resultData != null) {
//                                                testResult = (PDTestResult) PervacioTest.getInstance().getObjectFromData(resultData, new TypeToken<PDTestResult>() {
//                                                }.getType());
//                                            }
//                                            if (testResult != null) {
//                                                updateTestResult(testResult.getName(), testResult.getStatus(), false);
//                                            }
//                                            keysTest.stopTest();
//                                            return;
//                                        }
//                                        Bundle bundle = msg.getData();
//                                        String result = bundle.getString("result");
//                                        if (result != null && !result.isEmpty()) {
//                                            if (result.equalsIgnoreCase("KeyEvent")) {
//                                                String key = bundle.getString("message");
//                                                keysTest.changeBG(key);
//                                            } else {
//                                                    if (result.equalsIgnoreCase(TestResult.PASS)) {
//                                                        manualTestResultDialog(testName, TestResult.PASS, true, ManualTestsTryActivity.this);
////                                                        finish();
//                                                    } else if (result.equalsIgnoreCase(TestResult.TIMEOUT)) {
//                                                        manualTestResultDialog(testName, TestResult.TIMEOUT,false, ManualTestsTryActivity.this);
//                                                    } else if (result.equalsIgnoreCase(TestResult.FAIL)) {
//                                                      manualTestResultDialog(testName, TestResult.FAIL, true, ManualTestsTryActivity.this);
////                                                        finish();
//
//                                                }
//                                            }
//                                        }
//                                    }
//                                };
//
//
//                                DLog.i(TAG, "mDevicekeysList " + mDevicekeysList);
//                                prepareKeysPojo();
//                                if (TestName.SOFTKEYTEST.equalsIgnoreCase(testName)) {
//                                    keyLayout.setNumColumns(1);
//                                } else {
//                                    keyLayout.setNumColumns(2);
//                                }
//                                keyTestAdapter = new KeyTestAdapter(ManualTestsTryActivity.this,testName);
//                                keyLayout.setAdapter(keyTestAdapter);
//                                KeysTestActivity.startTheTimer(timerText);
//                                try {
//                                    manualtest.performTest(testName, keysHandler);
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                }
//
//
//
//                                break;

                            default:
                                DLog.d("ManualTest","default running");
                                mainLayout.setVisibility(View.VISIBLE);
                                Progressbar.setVisibility(View.GONE);
                                numLayout1.setVisibility(View.GONE);
                                RetryLayout.setVisibility(View.GONE);
                                numLayout2.setVisibility(View.GONE);
                                globalConfig.setScrollPosition(position);
                                startManualTest(testName);
                                break;


                        }


//


                    } else {
                        showResultMessage();
                    }
                }
            }
        });
    }

    public void prepareKeysPojo() {
        keysPOJOArray.clear();
        for (int i = 0; i < mDevicekeysList.size(); i++) {
           keyPOJO keyPOJO = new keyPOJO();
            keyPOJO.setKeyName(keyName(i));
            keyPOJO.setChecked(false);
            keysPOJOArray.add(i, keyPOJO);
        }
        DLog.d("Keys Pojo", keysPOJOArray + "");
    }

    public String keyName(int position) {
        String keyname = "";
        try {
            if (position < mDevicekeysList.size()) {
                keyname = mDevicekeysList.get(position).toLowerCase();
                keyname = keyname.replace("_", " ");
            }

            String[] tokens = keyname.split("\\s");
            keyname = "";
            for (int i = 0; i < tokens.length; i++) {
                char capLetter = Character.toUpperCase(tokens[i].charAt(0));
                keyname += " " + capLetter + tokens[i].substring(1);
            }
            keyname = keyname.trim();
        } catch (Exception e) {
        }
        return keyname;
    }
    private ArrayList<String> getKeysList(String mTestName) {

            if (HARDKEYTEST.equalsIgnoreCase(mTestName)) {
                mDevicekeys = PervacioTest.getInstance().getGlobalConfig().getDeviceHardKeys();
                DLog.d("Hard keys",mDevicekeys);
            } else {
                mDevicekeys = PervacioTest.getInstance().getGlobalConfig().getDeviceSoftKeys();
                DLog.i(TAG, "getKeysList mDevicekeys : " + mDevicekeys);
            }
//            return (new ArrayList<String>());


        mDevicekeys = mDevicekeys.replaceAll(",,", ",");       //Required two times to get single comma seperated keys.
        mDevicekeys = mDevicekeys.replaceAll("\n", "");
        mDevicekeys = mDevicekeys.replaceAll("\t", "");
        mDevicekeys = mDevicekeys.replaceAll(" ", "");
        ArrayList<String> keyslist = new ArrayList<String>(Arrays.asList(mDevicekeys.split(",")));
        return keyslist;
    }

//    class KeyTestAdapter extends BaseAdapter {
//
//        private Context context;
//        private String mTestName;
//
//        public KeyTestAdapter(Context context,String mTestName) {
//            this.context = context;
//            this.mTestName = mTestName;
//        }
//
//        @Override
//        public int getCount() {
//            return keysPOJOArray.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return keysPOJOArray.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        class ViewHolder {
//            TextView name;
//            ImageView imageView;
//
//            ViewHolder(View v) {
//                this.imageView = (ImageView) v.findViewById(R.id.id_checkbox_keys);
//                this.name = (TextView) v.findViewById(R.id.key_name);
//            }
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            ViewHolder holder;
//
//            if (convertView == null) {
//                LayoutInflater _layoutInflator = (LayoutInflater) context.getSystemService
//                        (context.LAYOUT_INFLATER_SERVICE);
//                if (SOFTKEYTEST.equalsIgnoreCase(mTestName)) {
//                    convertView = _layoutInflator.inflate(R.layout.keys_grid_child_softkeys, null);
//                } else {
//                    convertView = _layoutInflator.inflate(R.layout.keys_grid_child, null);
//                }
//                holder = new ViewHolder(convertView);
//
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//            addKeysTextUI(holder.name, position);
//            if (keysPOJOArray.get(position).isChecked()) {
//                holder.imageView.setImageResource(R.drawable.ic_passed);
//            } else {
//                holder.imageView.setImageResource(R.drawable.ic_checkblank);
//            }
//            return convertView;
//        }
//    }

    private void addKeysTextUI(TextView textView, int position) {
        String finalValue = mDevicekeysList.get(position);
        String appendKeyName = finalValue.toLowerCase();
        String key_value = "key_" + appendKeyName;
        int value = getResources().getIdentifier(key_value, "string", this.getPackageName());
        if (value != 0) {
            finalValue = getString(value);
        }
        textView.setText(finalValue);
    }


    @Override
    protected AlertDialog.Builder showTestSuggestions(final String testName){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.suggestion_pop_up, null);
        TextView msgView = view.findViewById(R.id.time_out_alrt_msg);
        TextView titleView = view.findViewById(R.id.time_out_alrt_title);
        Button fialBtn = view.findViewById(R.id.suggestion_fail_btn);
        Button retryBtn = view.findViewById(R.id.suggestion_retry_btn);
        ImageView imgView = view.findViewById(R.id.time_out_alrt_img);

        AlertDialog AlertDialog = dialogBuilder.setView(view).create();
        AlertDialog.setCancelable(false);
        if (ODDUtils.suggestionTestMap.containsKey(testName)) {
            titleView.setText(R.string.suggestions);
        }
        int messageID = getResourceID(testName, TEST_TIMEOUT_MESAGE);
        msgView.setText(messageID);
        int imageId = getResourceID(testName, TEST_TIMEOUT_IMAGE);
        if (imageId != 0) {
            imgView.setImageResource(imageId);
            imgView.setVisibility(View.VISIBLE);
        }

        retryBtn.setOnClickListener(view12 -> {

            AlertDialog.dismiss();
            if (testName.equalsIgnoreCase(EARPHONETEST)) {
                if (resultHandler != null&& progressBar!=null) {
                    progressBar.setVisibility(View.VISIBLE);
                    ManualTest.getInstance(ManualTestsTryActivity.this).performTest(testName, resultHandler);
                }
            }

        });

        fialBtn.setOnClickListener(view1 ->{

            manualTestResultDialog(testName,TestResult.FAIL,false,getApplicationContext());
            AlertDialog.dismiss();
        });


        AlertDialog.show();

        return dialogBuilder;
    }

    private void updateSkippedStates(List<ManualTestItem> newSkippedStates) {
        adapter.setSkippedStates(newSkippedStates);
    }
    @Override
    protected void onResume() {
        super.onResume();


        //ManualTestTryAdapter adapter = new ManualTestTryAdapter(dataList,getApplicationContext(),recyclerView);

      int lastClickedPosition = globalConfig.getScrollPosition();

        if (lastClickedPosition != RecyclerView.NO_POSITION) {
            recyclerView.scrollToPosition(lastClickedPosition);
            adapter.notifyCurrentItemChanged(lastClickedPosition);
            DLog.d("Recyclerview", String.valueOf(lastClickedPosition));
        }else{
            DLog.d("Recyclerview", String.valueOf(lastClickedPosition));
        }
        if (globalConfig.getTestStates()!=null){
            DLog.d("Recyclerview", String.valueOf(globalConfig.getTestStates()));
            updateSkippedStates(globalConfig.getTestStates());
        }
        else {
            DLog.d("Recyclerview ", String.valueOf(lastClickedPosition));
        }

        setUpRecyclerView();

        recyclerView.setAdapter(adapter);

        String lastTestName = selectedManualTests.get(selectedManualTests.size() - 1);
        if(mCurrentTest.equalsIgnoreCase("EndTest") && lastTestName.equalsIgnoreCase(globalConfig.getRetryTestName())) {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(0, prev_test_view[selectedManualTestsResult.size()- 2].getBottom());
                }
            });
        }
    }

    @Override
    protected String getToolBarName() {
        return testDisplayName();
    }

    private long getAvailableExternalMemorySize() {
        if ("mounted".equalsIgnoreCase(Environment.getExternalStorageState())) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = (long) stat.getBlockSize();
            long availableBlocks = (long) stat.getAvailableBlocks();
            return this.formatSize(availableBlocks * blockSize);
        } else {
            return 0L;
        }
    }

    private long getAvailableSDMemorySize() {
        this.sdCardPath = AppUtils.getSDCardPath();
        if (this.isSDCardAvailable()) {
            StatFs stat = new StatFs(this.sdCardPath);
            long blockSize = (long) stat.getBlockSize();
            long availableBlocks = (long) stat.getAvailableBlocks();
            return this.formatSize(availableBlocks * blockSize);
        } else {
            return 0L;
        }
    }

    private boolean isSDCardAvailable() {
        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20) {
            if (TextUtils.isEmpty(this.sdCardPath)) {
                return false;
            } else {
                File sdFile = new File(this.sdCardPath);
                return sdFile.exists();
            }
        } else {

            return false;
        }
    }

    private long formatSize(long size) {
        size /= 1024L;
        return size;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_manual_test_new;
    }


    @Override
    protected boolean setBackButton() {
        return true;
    }


    private String testDisplayName() {
        if (getIntent == null)
            getIntent = getIntent();
        String testName = getIntent.getStringExtra(TEST_NAME);
        return getDisplayName(testName);
    }

    public boolean permissionCheck(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean isPermissionGranted() {

        switch (mCurrentTest) {
            case CALLTEST:
                DLog.d(TAG, "permission");
                if (CALLTEST.equalsIgnoreCase(mCurrentTest)) {
                    if (!(permissionCheck(Manifest.permission.READ_PHONE_STATE)) || !(permissionCheck(Manifest.permission.CALL_PHONE))) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            ActivityCompat.requestPermissions(ManualTestsTryActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE},
                                    0);
                        }
                    } else {
                        return true;
                    }
                }
                break;
            case FRONTFLASHTEST:
            case CAMERAFLASHTEST:

            case TestName.REARCAMERAPICTURETEST:
            case TestName.REARCAMERAPICTURETEST1:
            case TestName.REARCAMERAPICTURETEST2:
            case TestName.REARCAMERAPICTURETEST3:
            case TestName.REARCAMERAPICTURETEST4:
            case TestName.REARCAMERAPICTURETEST5:
            case TestName.REARCAMERAPICTURETEST6:

            case TestName.FRONTCAMERAPICTURETEST:
            case TestName.FRONTCAMERAPICTURETEST1:
            case TestName.FRONTCAMERAPICTURETEST2:
            case TestName.FRONTCAMERAPICTURETEST3:
            case TestName.FRONTCAMERAPICTURETEST4:
            case TestName.FRONTCAMERAPICTURETEST5:
            case TestName.FRONTCAMERAPICTURETEST6:

                if ((!(permissionCheck(Manifest.permission.CAMERA)) || !(permissionCheck(Manifest.permission.WRITE_EXTERNAL_STORAGE))) && Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(ManualTestsTryActivity.this,
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                0);
                    }
                } else {
                    return true;
                }
                break;
            case TestName.REARCAMERAVIDEOTEST:
            case TestName.REARCAMERAVIDEOTEST1:
            case TestName.REARCAMERAVIDEOTEST2:
            case TestName.REARCAMERAVIDEOTEST3:
            case TestName.REARCAMERAVIDEOTEST4:
            case TestName.REARCAMERAVIDEOTEST5:
            case TestName.REARCAMERAVIDEOTEST6:

            case TestName.FRONTCAMERAVIDEOTEST:
            case TestName.FRONTCAMERAVIDEOTEST1:
            case TestName.FRONTCAMERAVIDEOTEST2:
            case TestName.FRONTCAMERAVIDEOTEST3:
            case TestName.FRONTCAMERAVIDEOTEST4:
            case TestName.FRONTCAMERAVIDEOTEST5:
            case TestName.FRONTCAMERAVIDEOTEST6:
                if ((!(permissionCheck(Manifest.permission.CAMERA)) || !(permissionCheck(Manifest.permission.WRITE_EXTERNAL_STORAGE)) || !(permissionCheck(Manifest.permission.RECORD_AUDIO))) && Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(ManualTestsTryActivity.this,
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                                0);
                    }
                } else {
                    return true;
                }
                break;
            case SPEAKERTEST:
            case TestName.MICROPHONETEST:
            case TestName.MICROPHONE2TEST:
            case EARPHONETEST:
            case EARPIECETEST:
                if (dndModePermissionCheck()) {
                    if (TestName.MICROPHONETEST.equalsIgnoreCase(mCurrentTest) || TestName.MICROPHONE2TEST.equalsIgnoreCase(mCurrentTest)) {
                        if (!(permissionCheck(Manifest.permission.RECORD_AUDIO))) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ActivityCompat.requestPermissions(ManualTestsTryActivity.this,
                                        new String[]{Manifest.permission.RECORD_AUDIO},
                                        0);
                            }
                        } else {
                            return true;
                        }
                    }
                    return true;
                }
                break;
            default:
                return true;

        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean anyTestAccessDenied = false;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                anyTestAccessDenied = true;
            DLog.d(TAG, "Permission: " + permissions[i] + " Result: " + grantResults[i]);
        }

        /*grantResults =0 Allow
        grantResults =-1 Deny*/
        if (!isAssistedApp) {
            if (anyTestAccessDenied) {
                manualTestResultDialog(mCurrentTest, TestResult.ACCESSDENIED, false, ManualTestsTryActivity.this);
                //startNextManualTest();
            } else {
                if (storageAvailable()) {
                    startManualTest(mCurrentTest);
                } else {
                    showResultMessage();
                }
            }
/*            if (TestName.FRONTCAMERAVIDEOTEST.equalsIgnoreCase(mCurrentTest) || TestName.FRONTCAMERAPICTURETEST.equalsIgnoreCase(mCurrentTest) || TestName.REARCAMERAPICTURETEST.equalsIgnoreCase(mCurrentTest) || TestName.REARCAMERAVIDEOTEST.equalsIgnoreCase(mCurrentTest)) {
                if (isPermissionGranted()) {
                    if (storageAvailable()) {
                      //  startManualTest(mCurrentTest);
                    } else {
                        showResultMessage();
                    }
                } else {
                    startManualTest(mCurrentTest);
                }
            }
            else {
                startManualTest(mCurrentTest);
            }*/
        }
    }

    private void removeDialog() {
        if (alertDialog == null)
            return;
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public void showResultMessage() {
        removeDialog();

        alertDialog = CommonUtil.DialogUtil.showAlert(this, getString(R.string.alert), getString(R.string.storage_msg), getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }


    public void showWorning(final int wid) {
        removeDialog();

        int messageId = 0;
        if (wid == 1) {
            messageId = R.string.eaphone_warning_msg;
        } else if (wid == 2) {
            messageId = R.string.bt_headset_connected_msg;
        } else if (wid == 3) {
            messageId = R.string.flash_warning;
        }


        DialogInterface.OnClickListener negativeListener = null;
        if (wid == 3) {
            negativeListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    alertDialog.dismiss();
                    updateTestResult(mCurrentTest, TestResult.SKIPPED);
//                    startNextManualTest();
                }
            };
        }
        alertDialog = CommonUtil.DialogUtil.getAlert(this, getString(R.string.warning), getString(messageId), getString(R.string.str_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (wid == 2) {
                    checkBluetoothHeadphoneConnected(mCurrentTest);
                }
            }
        }, getString(R.string.str_skip), negativeListener);

        alertDialog.show();

    }

    public boolean storageAvailable() {
        long requiredMemory = 32768L;
        switch (mCurrentTest) {
            case TestName.REARCAMERAPICTURETEST:
            case TestName.REARCAMERAPICTURETEST1:
            case TestName.REARCAMERAPICTURETEST2:
            case TestName.REARCAMERAPICTURETEST3:
            case TestName.REARCAMERAPICTURETEST4:
            case TestName.REARCAMERAPICTURETEST5:
            case TestName.REARCAMERAPICTURETEST6:

            case TestName.FRONTCAMERAPICTURETEST:
            case TestName.FRONTCAMERAPICTURETEST1:
            case TestName.FRONTCAMERAPICTURETEST2:
            case TestName.FRONTCAMERAPICTURETEST3:
            case TestName.FRONTCAMERAPICTURETEST4:
            case TestName.FRONTCAMERAPICTURETEST5:
            case TestName.FRONTCAMERAPICTURETEST6:

            case TestName.REARCAMERAVIDEOTEST:
            case TestName.REARCAMERAVIDEOTEST1:
            case TestName.REARCAMERAVIDEOTEST2:
            case TestName.REARCAMERAVIDEOTEST3:
            case TestName.REARCAMERAVIDEOTEST4:
            case TestName.REARCAMERAVIDEOTEST5:
            case TestName.REARCAMERAVIDEOTEST6:

            case TestName.FRONTCAMERAVIDEOTEST:
            case TestName.FRONTCAMERAVIDEOTEST1:
            case TestName.FRONTCAMERAVIDEOTEST2:
            case TestName.FRONTCAMERAVIDEOTEST3:
            case TestName.FRONTCAMERAVIDEOTEST4:
            case TestName.FRONTCAMERAVIDEOTEST5:
            case TestName.FRONTCAMERAVIDEOTEST6:
                if (getAvailableExternalMemorySize() >= requiredMemory) {
                    DLog.d(TAG, "Entered Free Memory Case");
                    return true;
                    //  return true;
                } else {
                    if (getAvailableSDMemorySize() < requiredMemory) {
                        // org.pervacio.onediaglib.utils.AppUtils.printLog(TAG, "Required memory is not available to store the image", (Throwable) null, 4);
                        DLog.d(TAG, "No Free Memory Case");
                        return false;
                    } else {
                        return true;
                    }
                }

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (!Util.needToRemoveBackButton()) {
            super.onBackPressed();
        }
    }

    private void checkBluetoothHeadphoneConnected(String testName) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        also get Intent.ACTION_HEADSET_PLUG for wired headset
        if (bluetoothAdapter != null && BluetoothProfile.STATE_CONNECTED == bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
            showWorning(2);
//        } else if (bluetoothAdapter != null && BluetoothProfile.STATE_CONNECTED == bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP)) {
//            showWorning(2);
        } else if (audioManager.isWiredHeadsetOn()) {
            showWorning(1);
        }
        else {
            if (testName.equalsIgnoreCase(EARPHONETEST) || testName.equalsIgnoreCase(SPEAKERTEST) || testName.equalsIgnoreCase(EARPIECETEST)){
                if (resultHandler !=null){
                    ManualTest.getInstance(ManualTestsTryActivity.this).performTest(testName, resultHandler);
                }
            }else{
                startManualTest(testName);
            }

        }
    }
}