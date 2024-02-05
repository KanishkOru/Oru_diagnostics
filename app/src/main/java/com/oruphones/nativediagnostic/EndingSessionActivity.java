package com.oruphones.nativediagnostic;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.oruphones.nativediagnostic.api.BuildConfig;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.communication.CommandServer;
import com.oruphones.nativediagnostic.logging.LogUploadWork;
import com.oruphones.nativediagnostic.models.AbortReasons;
import com.oruphones.nativediagnostic.util.AppUtils;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.PreferenceUtil;
import com.oruphones.nativediagnostic.util.Util;


import java.io.File;

public class EndingSessionActivity extends BaseActivity {

    public static String TAG = EndingSessionActivity.class.getSimpleName();
    TextView endsessionMessage;
    TextView diagnosticsview;
    boolean exitDelay = false;
    private AbortReasons mAbortReasons = AbortReasons.END_SESSION;

    public static void exit(Context context, AbortReasons reasons) {
        Intent intent = new Intent(context, EndingSessionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(PreferenceUtil.EX_EXIT, true);
        intent.putExtra(PreferenceUtil.EX_RESULT, reasons);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean shouldExit = getIntent().getBooleanExtra(PreferenceUtil.EX_EXIT, false);
        exitDelay = getIntent().getBooleanExtra("ExitDelay", false);
        mAbortReasons = (AbortReasons) getIntent().getSerializableExtra(PreferenceUtil.EX_RESULT);

        if(mAbortReasons==null){
            mAbortReasons = AbortReasons.END_SESSION;
        }

        if(shouldExit) {
            LogUploadWork.scheduleTheFileUpload(GlobalConfig.getInstance().getServerUrl(),GlobalConfig.getInstance().getServerKey(),String.valueOf(GlobalConfig.getInstance().getSessionId()));
            CommandServer.getInstance(getApplicationContext()).stopAllThreads();
            CommandServer.getInstance(getApplicationContext()).reset();
            AppUtils.triggerUninstall(this,true, BuildConfig.APPLICATION_ID,mAbortReasons);
        }
        endsessionMessage= (TextView) findViewById(R.id.endingSessionText);
        CommandServer.getInstance(getApplicationContext()).setUIHandler(handler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Util.showCSATScreen() && !exitDelay) {
            CustomerRatingDialogActivity.startActivity(EndingSessionActivity.this,GlobalConfig.getInstance().getSessionId());
        }
        else
        finishAppHandler.sendEmptyMessageDelayed(0,5000);
    }

    Handler finishAppHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            finishAppHandler.removeMessages(0);
            exit(getApplicationContext(),mAbortReasons);
        }
    };

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void triggerUninstall() {

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.end_session_new_layout;
    }

    @Override
    protected String getToolBarName() {
        return null;
    }

    @Override
    protected boolean setBackButton() {
        return false;
    }

    public void onEndingButtonClick(View v) {
        if (v.getId() == R.id.EndingSessionFinishButton) {
            Toast.makeText(EndingSessionActivity.this, getResources().getString(R.string.title_activity_ending_session), Toast.LENGTH_SHORT).show();
            DLog.d(TAG, "Session Ends");
            endActivity();
        } else {
            // Handle other cases if needed
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            String result = bundle.getString("result");
            String message = bundle.getString("message");
            String cmdName = bundle.getString("cmdName");
            if("CMD_END_SESSION".equals(cmdName)) {
                CommandServer.getInstance(getApplicationContext()).stopAllThreads();
                finish();

            }

        }
    };





    protected void getServerData(Bundle data) {
       // data.getBundle("cmdName");
        String cmdName = data.getString("cmdName");
        //Log.d("cmdName", cmdName);
        try {
            if("CMD_END_SESSION".equals(cmdName)){

                Intent intent = new Intent(this, WelcomeScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Exit me", true);
                startActivity(intent);
                finish();
            }

        } catch (Exception e) {
            e.printStackTrace();
            DLog.e(TAG, e.toString());
        }}

    private void endActivity() {
//         Handler handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                String result =   msg.getData().getString("result");
//                if(result.equalsIgnoreCase())
//            }
//        };

//
//      StateMachine.getInstance(getApplicationContext())
//                .postPDCommand(
//                        PDCmdName.STOP_SESSION,
//                        "SUCCESS");
//        Intent in1 = new Intent(EndingSessionActivity.this,
//                TermsAndConditionActivity.class);
//        in1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        in1.putExtra("exit", true);
//        startActivity(in1);

    }



    /*public void frontCameraFolderDelete() {
        try{


            File dir = new File(getApplicationContext().getFilesDir(), CareConstants.camerafolder);
        //File dir = new File(Environment.getExternalStorageDirectory()+"/"+CareConstants.frontcamerafolder);
        deleteDir(dir);
            Log.i("abcd","ab");
        }catch(Exception e){
            Log.d(TAG,"Exception:"+e.getMessage());
        }

 }*/
   /* public void rearCameraFolderDelete() {

        File dir = new File(getApplicationContext().getFilesDir(),CareConstants.camerafolder);
       // File dir = new File(Environment.getExternalStorageDirectory()+"/"+CareConstants.rearcamerafolder);
        deleteDir(dir);


    }*/
    public static boolean deleteDir(File dir) {
        try {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                if (children.length > 0) {
                    for (int i = 0; i < children.length; i++) {
                        boolean success = deleteDir(new File(dir, children[i]));

                        if (!success) {
                            return false;
                        }

                    }
                }
            }
        }
        catch(Exception e){
            DLog.e(TAG,"Exception:"+e.getMessage());
        }
        // The directory is now empty so delete it
        return dir.delete();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected boolean isFullscreenActivity() {
        return true;
    }
}
