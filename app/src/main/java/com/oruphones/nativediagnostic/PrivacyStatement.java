package com.oruphones.nativediagnostic;


import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.widget.TextView;

public class PrivacyStatement extends BaseActivity {
    TextView privacy_statement_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        privacy_statement_tv = (TextView) findViewById(R.id.privacy_stmnt_tv);
        setFontToView(privacy_statement_tv,ROBOTO_LIGHT);
        permissionDenied=false;
        SpannableString spannablePolicyBody;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            privacy_statement_tv.setText(Html.fromHtml(getString(R.string.privacy_statement_text), Html.FROM_HTML_MODE_COMPACT));
            spannablePolicyBody = new SpannableString(Html.fromHtml(getString(R.string.privacy_statement_text), Html.FROM_HTML_MODE_COMPACT));
        } else {
            privacy_statement_tv.setText(Html.fromHtml(getString(R.string.privacy_statement_text)));
            spannablePolicyBody = new SpannableString(Html.fromHtml(getString(R.string.privacy_statement_text)));
        }

        privacy_statement_tv.setText(spannablePolicyBody, TextView.BufferType.SPANNABLE);
        privacy_statement_tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected String getToolBarName() {
        return getResources().getString(R.string.privacy_statement);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_privacy_statement;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}

