package com.oruphones.nativediagnostic.manualtests;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.oneDiagLib.TestListener;
import com.oruphones.nativediagnostic.util.AnimatedGifUtils;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import org.pervacio.onediaglib.diagtests.TestResult;


public class NfcTestActivity extends MiddleActivity {

    private NfcAdapter nfcAdapter;
    private TextView resultTextView;
    private Button startTestButton;
    private CountDownTimer countdownTimer;
    private Integer resultCode;
    private String testResult;
    TestResult result = new TestResult();
    private TestListener mTestListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);


        try{
            startNfcTest();
        }catch (Exception e){
            DLog.e("NFC_Test_Err",e);
        }finally {

        }
    }

    @Override
    protected String getToolBarName() {
        return getDisplayName(TestName.NFCTEST);
    }

    private void startNfcTest() {
        if (nfcAdapter == null) {
            showNfcNotSupportedDialog();
            updateTestResult(TestName.NFCTEST, com.oruphones.nativediagnostic.models.tests.TestResult.NOTSUPPORTED);
            AnimatedGifUtils.setResultIcon(mGIFMovieViewContainer,getApplicationContext(), com.oruphones.nativediagnostic.models.tests.TestResult.FAIL);
            manualTestResultDialog(TestName.NFCTEST, com.oruphones.nativediagnostic.models.tests.TestResult.NOTSUPPORTED, NfcTestActivity.this);

            return;
        }

        if (!nfcAdapter.isEnabled()) {
            showEnableNfcDialog();
            return;
        }


        countdownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            //    Toast.makeText(NfcTestActivity.this,"Testing NFC... " + millisUntilFinished / 1000 + " seconds remaining",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFinish() {
//                result.setResultCode(TestResult.RESULT_FAIL);
//                NfcTestActivity.this.mTestListener.onTestEnd(result);
                setResultCode(TestResult.RESULT_FAIL);
                setResult(com.oruphones.nativediagnostic.models.tests.TestResult.FAIL);

                AnimatedGifUtils.setResultIcon(mGIFMovieViewContainer,getApplicationContext(), com.oruphones.nativediagnostic.models.tests.TestResult.FAIL);

           //     manualTestResultDialog(TestName/);
                Toast.makeText(NfcTestActivity.this,"Test failed: NFC device not found",Toast.LENGTH_SHORT).show();

            }
        }.start();



       // handleIntent(getIntent());
    }

    private void enableNfc() {
        Intent nfcIntent = new Intent(this, getClass());
        nfcIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, PendingIntent.FLAG_MUTABLE);

        IntentFilter[] intentFilters = new IntentFilter[]{
                new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        };

        String[][] techList = new String[][]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, techList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void setResult(String testResult){
        this.testResult = testResult;
    }

    private void setResultCode(Integer resultCode){
        this.resultCode = resultCode;
    }

    public Integer getResultCode(){
        return resultCode;
    }

    public String getTestResult(){
        return testResult;
    }
    private void showEnableNfcDialog() {
        new AlertDialog.Builder(this)
                .setTitle("NFC is not enabled")
                .setMessage("Do you want to enable NFC?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void handleIntent(Intent intent) {

        switch (intent.getAction()) {

            case NfcAdapter.ACTION_NDEF_DISCOVERED:
                Parcelable tagNdef = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                countdownTimer.cancel();
                stopCountdownTimer();
                DLog.d("NFC_Test","Test passed: NFC device found NDEF Tag Discovered");

                AnimatedGifUtils.setResultIcon(mGIFMovieViewContainer,getApplicationContext(), com.oruphones.nativediagnostic.models.tests.TestResult.PASS);
                //result.setResultCode(TestResult.RESULT_PASS);
                setResultCode(TestResult.RESULT_PASS);
                setResult(com.oruphones.nativediagnostic.models.tests.TestResult.PASS);
                manualTestResultDialog(TestName.NFCTEST, "PASS", NfcTestActivity.this);
                updateTestResult(TestName.NFCTEST,com.oruphones.nativediagnostic.models.tests.TestResult.PASS);
             //   NfcTestActivity.this.mTestListener.onTestEnd(result);
                Toast.makeText(this, "NDEF Tag Discovered", Toast.LENGTH_SHORT).show();
                break;
            case NfcAdapter.ACTION_TAG_DISCOVERED:
                Parcelable tagAny = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                countdownTimer.cancel();
                stopCountdownTimer();
                DLog.d("NFC_Test","Test passed: NFC device found Tag Discovered");
                AnimatedGifUtils.setResultIcon(mGIFMovieViewContainer,getApplicationContext(), com.oruphones.nativediagnostic.models.tests.TestResult.PASS);
              //  result.setResultCode(TestResult.RESULT_PASS);
          //      NfcTestActivity.this.mTestListener.onTestEnd(result);
                manualTestResultDialog(TestName.NFCTEST, "PASS", NfcTestActivity.this);
                setResultCode(TestResult.RESULT_PASS);
                updateTestResult(TestName.NFCTEST,com.oruphones.nativediagnostic.models.tests.TestResult.PASS);
                setResult(com.oruphones.nativediagnostic.models.tests.TestResult.PASS);
                Toast.makeText(this, "Any Tag Discovered", Toast.LENGTH_SHORT).show();
                break;
            case NfcAdapter.ACTION_TECH_DISCOVERED:
                countdownTimer.cancel();
                stopCountdownTimer();
                AnimatedGifUtils.setResultIcon(mGIFMovieViewContainer,getApplicationContext(), com.oruphones.nativediagnostic.models.tests.TestResult.PASS);
                DLog.d("NFC_Test","Test passed: NFC device found Tech Tag Discovered");
               // result.setResultCode(TestResult.RESULT_PASS);
              //  NfcTestActivity.this.mTestListener.onTestEnd(result);
                manualTestResultDialog(TestName.NFCTEST, "PASS", NfcTestActivity.this);
                setResultCode(TestResult.RESULT_PASS);
                updateTestResult(TestName.NFCTEST,com.oruphones.nativediagnostic.models.tests.TestResult.PASS);
                setResult(com.oruphones.nativediagnostic.models.tests.TestResult.PASS);
                Parcelable tagTech = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                Toast.makeText(this, "Tech Tag Discovered", Toast.LENGTH_SHORT).show();
                break;
            default:
                updateTestResult(TestName.NFCTEST,com.oruphones.nativediagnostic.models.tests.TestResult.PASS);
                manualTestResultDialog(TestName.NFCTEST, com.oruphones.nativediagnostic.models.tests.TestResult.FAIL, NfcTestActivity.this);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableNfc();

    }

    private void showNfcNotSupportedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("NFC not supported")
                .setMessage("This device does not support NFC.")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
}

//public class NfcTestActivity extends MiddleActivity {
//
//    private NfcAdapter nfcAdapter;
//    private String mTestName;
//    private String testResult;
//
//    private Boolean accessDenied=false;
//    private static final int NFC_PERMISSION_CODE = 123;
//    private PendingIntent pendingIntent;
//    private IntentFilter[] intentFiltersArray;
//    private Handler handler = new Handler()
//    {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            Bundle bundle = msg.getData();
//            String result =   bundle.getString("result");
//            String message =  bundle.getString("message");
//
//            if(TextUtils.isEmpty(message)){
//                Toast.makeText(NfcTestActivity.this, "NFC not found", Toast.LENGTH_SHORT).show();
//            }
//            manualTestResultDialog(mTestName, result, NfcTestActivity.this);
//        }
//    };
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mTestName = getIntent().getStringExtra(BaseActivity.TEST_NAME);
//
//
//        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
//
//
//        if (nfcAdapter == null) {
//
//            Toast.makeText(this, "NFC is not supported on this device", Toast.LENGTH_SHORT).show();
//        } else if (!nfcAdapter.isEnabled()) {
//           enableNFC();
//            Toast.makeText(this, "NFC is not enabled. Please enable NFC in your device settings.", Toast.LENGTH_SHORT).show();
//
//        }else{
//
//            accessDenied= false;
//            initializeNFC();
//        }
//
//
//
//
//    }
//
//    private void initializeNFC() {
//        DLog.d("NFC_test","Nfc1");
//        pendingIntent = PendingIntent.getActivity(
//                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
//
//        IntentFilter ndefIntentFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
//        ndefIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
//        intentFiltersArray = new IntentFilter[]{ndefIntentFilter};
//
//    }
//    private void requestNfcPermission() {
//        if (checkSelfPermission(Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.NFC}, NFC_PERMISSION_CODE);
//        } else {
//            enableNFC();
//        }
//    }
//
//    private void enableNFC() {
//        Intent enableNfcIntent = new Intent(android.provider.Settings.ACTION_NFC_SETTINGS);
//        startActivityForResult(enableNfcIntent, NFC_PERMISSION_CODE);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        if (nfcAdapter != null) {
//            nfcAdapter.disableForegroundDispatch(this);
//        }
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        DLog.d("NFC_test","Nfc2");
//
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())||NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())||NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
//            updateResultToHandler(TestResult.PASS);
//            DLog.d("NFC_test","Nfc4");
//            Toast.makeText(this, "NFC Test Passed!", Toast.LENGTH_SHORT).show();
//        }else{
//            updateResultToHandler(TestResult.FAIL);
//            DLog.d("NFC_test","Nfc45");
//            Toast.makeText(this,"No device discovered",Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == NFC_PERMISSION_CODE) {
//
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//            } else {
//                DLog.d("NFC_test","Nfc5");
//                accessDenied = true;
//                testResult = TestResult.ACCESSDENIED;
//                updateResultToHandler(testResult);
//            }
//        }
//    }
//
//    private void updateResultToHandler(String testResult) {
//        Message msg = new Message();
//        Bundle bundle = new Bundle();
//        bundle.putString("result", testResult);
//        msg.setData(bundle);
//        if (handler != null)
//            handler.sendMessage(msg);
//    }
//
//        @Override
//    protected void onResume() {
//        super.onResume();
//        if(!(alertDialog != null && alertDialog.isShowing())) {
//            if(!accessDenied) {
//                if (permissionStatusCheck(TestName.NFCTEST)) {
//                    if (nfcAdapter != null && nfcAdapter.isEnabled()) {
//                        DLog.d("NFC_test","Nfc7");
//                        assert pendingIntent != null;
//                        assert intentFiltersArray != null;
//                        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null);
//                    }
//
//                }
//            }
//        }
//
//    }
//
//
//
//    @Override
//    protected String getToolBarName() {
//        return "NFC Test";
//    }
//
//
//
//    @Override
//    protected boolean setBackButton() {
//        return true;
//    }
//
//
//
//}