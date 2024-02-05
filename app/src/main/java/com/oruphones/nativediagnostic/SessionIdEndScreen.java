package com.oruphones.nativediagnostic;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.oruphones.nativediagnostic.api.GlobalConfig;


public class SessionIdEndScreen extends BaseActivity {

    private Button mExitBtn;
    private TextView mSeesionIDview;
    private String mSessionId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mExitBtn = findViewById(R.id.end_btn_sid);
        mSeesionIDview = findViewById(R.id.session_id_tv);
        mSessionId = String.valueOf(GlobalConfig.getInstance().getSessionId());
        mSeesionIDview.setText(mSessionId);


        mSeesionIDview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (mSeesionIDview.getRight() - mSeesionIDview.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        ClipboardManager clipboard = (ClipboardManager) SessionIdEndScreen.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("copied text", mSessionId);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.session_id_copied_msg), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
        mExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SessionIdEndScreen.this, EndingSessionActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected String getToolBarName() {
        return getResources().getString(R.string.result_text);
    }

    @Override
    protected boolean setBackButton() {
        return false;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_session_id_end_screen;
    }
}
