package com.oruphones.nativediagnostic;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.oruphones.nativediagnostic.result.ResultsActivity;

public class ManualTestEndActivity extends BaseActivity {
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler.removeMessages(0);
            finish();
            ResultsActivity.start(ManualTestEndActivity.this);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler.sendEmptyMessageDelayed(0,3000);
    }

    @Override
    protected String getToolBarName() {
        return getResources().getString(R.string.manual_tests);
    }

    @Override
    protected boolean setBackButton() {
        return false;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_manual_test_end;
    }
}
