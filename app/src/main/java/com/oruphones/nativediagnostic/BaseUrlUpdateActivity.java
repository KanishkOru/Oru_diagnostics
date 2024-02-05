package com.oruphones.nativediagnostic;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.oruphones.nativediagnostic.api.GlobalConfig;

public class BaseUrlUpdateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_url_update);

        final EditText ipEditText = (EditText) findViewById(R.id.ip_edittext);
        Button okButton = (Button) findViewById(R.id.okButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseActivity.needDebugSupport = false;
                if(!TextUtils.isEmpty(ipEditText.getText().toString())) {
                    GlobalConfig.getInstance().setServerUrl(ipEditText.getText().toString().trim());
                } else {
                    GlobalConfig.getInstance().setServerUrl("http://54.244.244.114:8080");
                }
                Intent intent = new Intent(BaseUrlUpdateActivity.this,PinGenerationActivity.class);
                startActivity(intent);
            }
        });
    }
}
