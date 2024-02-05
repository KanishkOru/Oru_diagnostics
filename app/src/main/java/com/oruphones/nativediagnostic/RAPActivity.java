package com.oruphones.nativediagnostic;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.api.ReportAProblem;
import com.oruphones.nativediagnostic.util.AppUtils;
import com.oruphones.nativediagnostic.util.Constants;
import com.oruphones.nativediagnostic.util.CustomProgressDialog;
import com.oruphones.nativediagnostic.util.DeviceInfo;
import com.oruphones.nativediagnostic.util.Util;
import com.oruphones.nativediagnostic.webservices.NetworkResponseListener;
import com.oruphones.nativediagnostic.webservices.ODDNetworkModule;

import okhttp3.ResponseBody;

public class RAPActivity extends BaseActivity {

    private static final int RC_RAN = 98654;
    private TextView rapPolicy, rapIMEI;
    private EditText rapDes, rapName, rapPhone;
    private Button rapSend;
    private ODDNetworkModule mNetworkModule;
    private CustomProgressDialog mProgressDialog;

    public static void openForResult(Activity activity, boolean finishCurrent) {
        Intent intent = new Intent(activity, RAPActivity.class);
        activity.startActivity(intent);
        if (finishCurrent) {
            activity.finish();
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNetworkModule = ODDNetworkModule.getInstance();
        mProgressDialog = new CustomProgressDialog(this);
        initView();
    }


    private void initView() {
        ((TextView) findViewById(R.id.rapDesHeading)).setText(Util.requiredText(getString(R.string.rap_description_h)));
        ((TextView) findViewById(R.id.rapNameHeading)).setText(Util.requiredText(getString(R.string.rap_name_h)));
        ((TextView) findViewById(R.id.rapPhoneHeading)).setText(Util.requiredText(getString(R.string.rap_phone_h)));
        rapDes = findViewById(R.id.rapDes);
        rapIMEI = findViewById(R.id.rapIMEI);
        rapName = findViewById(R.id.rapName);
        rapPhone = findViewById(R.id.rapPhone);

        rapIMEI.setText(DeviceInfo.getInstance(this).get_imei());
        // set listeners
        rapDes.addTextChangedListener(mTextWatcher);
        rapName.addTextChangedListener(mTextWatcher);
        rapPhone.addTextChangedListener(mTextWatcher);


        rapSend = findViewById(R.id.rapSend);
        rapSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReport();
            }
        });
        rapPolicy = findViewById(R.id.rapPolicy);
        rapPolicy.setText(clickablePolicy(getString(R.string.content_protection_policy_title), getString(R.string.rap_info_text)));
        rapPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        checkSendButton();
    }

    private void submitReport() {
        String description = getText(rapDes);
        String imei = getText(rapIMEI);
        String name = getText(rapName);
        String phone = getText(rapPhone);

        if (TextUtils.isEmpty(description) || TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            AppUtils.toast(getString(R.string.rap_error_validation));
            return;
        }
        StringBuilder problem = new StringBuilder();
        problem.append(getString(R.string.rap_name_h)).append(" : ").append(name).append("\n");
        problem.append(getString(R.string.rap_phone_h)).append(" : ").append(phone).append("\n");
        problem.append(getString(R.string.device_info_imei)).append(" : ").append(imei).append("\n");
        problem.append(getString(R.string.rap_description_h)).append(" : ").append(description).append("\n");
        ReportAProblem reportAProblem = new ReportAProblem(problem.toString(), GlobalConfig.getInstance().getProductName(), Constants.TO_ADDRESS);

        if (!isOnline()) {
            Util.DialogUtil.twoButtonDialog(RAPActivity.this, getResources().getString(R.string.alert), getResources().getString(R.string.network_msz), new String[]{getString(R.string.action_cancel), getString(R.string.btn_retry)},
                    null, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            submitReport();
                        }
                    }
            );
            return;
        }

        mProgressDialog.show();
        mNetworkModule.reportProblem(reportAProblem, new NetworkResponseListener<ResponseBody>() {
            @Override
            public void onResponseReceived(ResponseBody responseBody) {
                mProgressDialog.hide();
                AppUtils.toast(getString(R.string.rap_submit_success));
                finish();
            }

            @Override
            public void onError() {
                mProgressDialog.hide();
                finish();
                AppUtils.toast(getString(R.string.unknown_error));
            }
        });
    }

    private void checkSendButton() {
        boolean isEnable = !(TextUtils.isEmpty(getText(rapDes)) || TextUtils.isEmpty(getText(rapName)) || TextUtils.isEmpty(getText(rapPhone)));
        rapSend.setEnabled(isEnable);
    }

    //  create a textWatcher member
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkSendButton();
        }
    };

    private SpannableStringBuilder clickablePolicy(String clickAbleText, String wholeText) {

        int startIndex = wholeText.indexOf(clickAbleText);
        int endIndex = startIndex + clickAbleText.length();

        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(wholeText);
        ClickableSpan redClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Util.DialogUtil.twoButtonDialog(RAPActivity.this, getString(R.string.content_protection_policy_title), getString(R.string.content_protection_policy_description), new String[]{null,getString(R.string.str_ok)},
                        null, null
                );
            }
        };
        ssBuilder.setSpan(redClickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssBuilder;
    }

    @Override
    protected String getToolBarName() {
        return getResources().getString(R.string.rap_title);
    }


    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_report_problem;
    }


}
