package com.oruphones.nativediagnostic;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsSeekBar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;


import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DeviceInfo;

import java.lang.reflect.Field;

public class AboutActivity extends BaseActivity {

    private static String TAG = AboutActivity.class.getSimpleName();
    TextView mCancel, mDone,rate_app,comment_app,privacy_policy;
    TextView version_number, build_release_number, serverVersionNumber;
    TextView select_submit;
    EditText structured_edittext_answer;
    private RatingBar ratingBar;
    private LayerDrawable stars = null;
    private Drawable drawable;
    private GlobalConfig globalConfig = null;
    private DeviceInfo deviceinfo = null;
    private String versionNumber;
    private String rating="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        globalConfig = GlobalConfig.getInstance();
        deviceinfo = DeviceInfo.getInstance(context);
        mCancel = (TextView) findViewById(R.id.cancel_tv);
        mDone = (TextView) findViewById(R.id.accept_tv);
        rate_app = (TextView) findViewById(R.id.rate_app);
        comment_app = (TextView) findViewById(R.id.comment_app);
        select_submit = (TextView) findViewById(R.id.select_submit);
        privacy_policy = (TextView)findViewById(R.id.privacy_policy);
        structured_edittext_answer =(EditText) findViewById(R.id.structured_edittext_answer);
        build_release_number = (TextView) findViewById(R.id.build_release_number);
        serverVersionNumber = (TextView) findViewById(R.id.serverVersionNumber);
        version_number = (TextView) findViewById(R.id.version_number);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        mCancel.setEnabled(true);
        mCancel.setVisibility(View.GONE);
      // setFontToView(mCancel,ROBOTO_MEDIUM);
      /*  setFontToView(mDone,ROBOTO_MEDIUM);*/
        setFontToView(version_number,ROBOTO_LIGHT);
        setFontToView(build_release_number,ROBOTO_LIGHT);
        setFontToView(rate_app,ROBOTO_LIGHT);
        setFontToView(comment_app,ROBOTO_LIGHT);
        setFontToView(select_submit,ROBOTO_LIGHT);
        structured_edittext_answer.setText("");  //add here because whenever user open aboutPage commnet box should be empty.// rating bar should be set to 0 rating.
        mDone.setText(getString(R.string.str_ok));
        mCancel.setText(getString(R.string.action_cancel));
        String app_ver;
        ratingBar.setRating(0);
        try {
            app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            app_ver = BuildConfig.VERSION_NAME;
        }
        versionNumber = app_ver;
        version_number.setText(String.format("%s %s", getString(R.string.version_number), app_ver));
        build_release_number.setText(String.format("%s %s", getString(R.string.build_release_date), getBuildDate()));
        serverVersionNumber.setText(getString(R.string.server_war_version,globalConfig.getServerWarVersion()));
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        privacy_policy.setMovementMethod(LinkMovementMethod.getInstance());
        mDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                rating = String.valueOf(Math.round(ratingBar.getRating()));

            }
        });
        select_submit.setText(getString(R.string.agree_to_submit_data));

    }


    @Override
    protected void onResume() {
        super.onResume();
       // if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
             DLog.d(TAG, "build " + Build.VERSION.SDK_INT);

            drawable = ratingBar.getProgressDrawable();
            if (drawable instanceof DrawableWrapper) {
                drawable = ((DrawableWrapper) drawable).getDrawable();
            }
            stars = (LayerDrawable) drawable;
            stars.getDrawable(2)
                    .setColorFilter(getResources().getColor(R.color.ratingbar_color),
                            PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1)
                    .setColorFilter(getResources().getColor(R.color.gray),
                            PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(0)
                    .setColorFilter(getResources().getColor(R.color.gray),
                            PorterDuff.Mode.SRC_ATOP);

      //  }else
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
            try {
                Field field =
                        AbsSeekBar.class.getDeclaredField("mTouchProgressOffset");
                field.setAccessible(true);
                field.set(ratingBar, 0.2f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        structured_edittext_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        });
    }

    @Override
    protected String getToolBarName() {
        return getString(R.string.about);
    }

    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_email_summary;
    }

    private String getBuildDate() {
        try {
            /*String date[] = BuildConfig.BUILD_DATE.split("-");
             String buildDate = date[2]+"-"+date[1]+"-"+date[0];
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            //String s = SimpleDateFormat.getInstance().format(new java.util.Date(time));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.mm.dd");
            String s = formatter.format(time);*/
            return BuildConfig.BUILD_DATE;
        } catch (Exception e) {
            return "2017.11.13";
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }


    public void showDataSendNetworkMessage(final Context context) {
        try {
            if (myDialog != null && myDialog.isShowing()) {
                myDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        myDialog = new Dialog(this);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.read_more_data_send);
        myDialog.setCancelable(false);
        myDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        Button BL_alert_ok = (Button) myDialog.findViewById(R.id.read_alert_yes);
        BL_alert_ok.setText(R.string.str_ok);
        BL_alert_ok.setTextColor(getResources().getColor(R.color.button_color));
        BL_alert_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.show();
        myDialog.setCancelable(false);
        myDialog.setCanceledOnTouchOutside(false);
    }
}
