package com.oruphones.nativediagnostic;


import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.oruphones.nativediagnostic.util.ProductFlowUtil;
import com.oruphones.nativediagnostic.util.Util;


public class TermsAndConditionsActivity extends BaseActivity {
    Button aceeptTextview, cancelTextview;
    TextView terms_conditions_tv;
    AppCompatCheckBox checkBox;
    private int PIN_AUTH_CODE = 1613;
    //WebView wv;
    public final static int RC_TERMS_CONDITIONS = 1002;


    public static void startActivity(Activity activity){
        Intent intent = new Intent(activity, TermsAndConditionsActivity.class);
        activity.startActivityForResult(intent,RC_TERMS_CONDITIONS);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aceeptTextview = (Button) findViewById(R.id.accept_tv);
        cancelTextview = (Button) findViewById(R.id.cancel_tv);
        checkBox = findViewById(R.id.tc_checkbox);
        //wv = (WebView) findViewById(R.id.wv);

        terms_conditions_tv = (TextView) findViewById(R.id.terms_conditions_tv);
        setFontToView(aceeptTextview,ROBOTO_MEDIUM);
        setFontToView(cancelTextview,ROBOTO_MEDIUM);
        //setFontToView(terms_conditions_tv,ROBOTO_LIGHT);
        LinearLayout btn_layout=(LinearLayout)findViewById(R.id.continue_ll);
        btn_layout.setBackgroundColor(getResources().getColor(R.color.white));


        permissionDenied=false;
        String tcHTMLString = getString(R.string.terms_and_conditions_text);
        if(ProductFlowUtil.isCustomerTelefonicaO2UK()){
            tcHTMLString = getString(R.string.terms_and_conditions_text_tfo2uk);
        }else if(Util.isCustomerClaroPeru()){
            tcHTMLString = getString(R.string.terms_and_conditions_text_claro);
        }else if(ProductFlowUtil.isCustomerVivoBrazil()){
            tcHTMLString = getString(R.string.terms_and_conditions_text_vivo_brazil);
        }


        if(Util.showConfirmPin()) {
            aceeptTextview.setText(R.string.str_next);
            cancelTextview.setText(R.string.btn_exit);
            aceeptTextview.setEnabled(false);
            checkBox.setVisibility(View.VISIBLE);
        } else {
            aceeptTextview.setText(R.string.accept);
            cancelTextview.setText(R.string.decline);
            aceeptTextview.setEnabled(true);
        }

        cancelTextview.setEnabled(true);
        aceeptTextview.setClickable(true);
        cancelTextview.setClickable(true);
        aceeptTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("TermsAndConditions", 1);
                editor.commit();

                if (isOnline()){
/*                    Intent intent = new Intent(TermsAndConditionsActivity.this, ProdConfigActivity.class);
                    startActivity(intent);
                    finish();*/
                    notifyResult(true);
                    //acceptPermissionDilaogueCheck();
                }
                else
                    showNetworkDialogue(hasOfflineData(), getResources().getString(R.string.alert), getResources().getString(R.string.network_msz), 0, TermsAndConditionsActivity.this);

            }
        });
        cancelTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyResult(false);
            }
        });
/*        if(getDeviceRingerMode() != 2 && AppUtils.VersionUtils.hasMarsMallow()){
            if (!isNotificationPolicyAccessGranted()) {
                  showDNDPermissionPopup();
            } else {
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
            }
        }*/

        SpannableString spannablePolicyBody;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            terms_conditions_tv.setText(Html.fromHtml(tcHTMLString, Html.FROM_HTML_MODE_COMPACT));
            spannablePolicyBody = new SpannableString(Html.fromHtml(tcHTMLString, Html.FROM_HTML_MODE_COMPACT));
        } else {
            terms_conditions_tv.setText(Html.fromHtml(tcHTMLString));
            spannablePolicyBody = new SpannableString(Html.fromHtml(tcHTMLString));
        }

        ClickableSpan clickablePrivacy = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent policiIntent = new Intent(getApplicationContext(), PrivacyStatement.class);
                startActivity(policiIntent);
            }
        };

        String linkText = getString(R.string.privacy_statement);
        String strPolicyBody = spannablePolicyBody.toString();
        int index = strPolicyBody.indexOf(linkText);
        if(index > 0) {
            spannablePolicyBody.setSpan(clickablePrivacy, index,
                    strPolicyBody.indexOf(linkText) + linkText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        terms_conditions_tv.setText(spannablePolicyBody, TextView.BufferType.SPANNABLE);
        terms_conditions_tv.setMovementMethod(LinkMovementMethod.getInstance());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                if (ischecked) {
                    confirmPin();
                    aceeptTextview.setEnabled(true);
                } else {
                    aceeptTextview.setEnabled(false);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
/*        if (!isOnline()) {
            networkMessageDialog(getResources().getString(R.string.alert), getResources().getString(R.string.network_msz), TermsAndConditionsActivity.this);
        }*/
    }

    @Override
    protected String getToolBarName() {
        return getResources().getString(R.string.terms_and_conditions);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*if (requestCode == 0) {
            for (int i = 0; i < permissions.length; i++) {
                checkPermissions(permissions[i]);
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("permissionsdenied", permissionDenied);
            editor.commit();

        }
      //  startApp();
        checkUsageStats();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == USAGE_STATS) {
            startApp();
        } else if (requestCode == WRITE_SETTINGS)
            checkUsageStats();
        else if (requestCode == PIN_AUTH_CODE) {
            if (resultCode == RESULT_OK) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("TermsAndConditions", 1);
                editor.commit();

                if (isOnline()){
/*                    Intent intent = new Intent(TermsAndConditionsActivity.this, ProdConfigActivity.class);
                    startActivity(intent);
                    finish();*/
                    notifyResult(true);
                    //acceptPermissionDilaogueCheck();
                }
                else
                    showNetworkDialogue(hasOfflineData(), getResources().getString(R.string.alert), getResources().getString(R.string.network_msz), 0, TermsAndConditionsActivity.this);
                checkBox.setEnabled(false);
            } else {
                checkBox.setChecked(false);
            }
        }
    }

    @Override
    protected boolean setBackButton() {
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
     /*  getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.about).setEnabled(false);*/
        return false;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_terms_and_conditions;
    }
    private void confirmPin() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

            if (km.isKeyguardSecure() && !isTermsAccepted()) {
                Intent authIntent = km.createConfirmDeviceCredentialIntent(getString(R.string.terms_and_conditions), getString(R.string.confirm_pin));
                startActivityForResult(authIntent, PIN_AUTH_CODE);
            }
        }
    }

    private void notifyResult(boolean pass){
        setResult(pass?Activity.RESULT_OK:Activity.RESULT_CANCELED);
        finish();
    }
}
