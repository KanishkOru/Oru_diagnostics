package com.oruphones.nativediagnostic.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;

import androidx.annotation.StringRes;

import com.oruphones.nativediagnostic.R;


/**
 * Created by jugal on 09-07-2020.
 */
public class CustomProgressDialog {
    private ProgressDialog progressDialog;
    private String title, message;
    private Activity mContext;

    public CustomProgressDialog(Activity activity) {
        mContext = activity;
        progressDialog = new ProgressDialog(activity, AlertDialog.THEME_HOLO_LIGHT);
        setInfo(R.string.connecting_to_server, R.string.please_wait);
        progressDialog.setCancelable(false);
    }



    public CustomProgressDialog setInfo(String title, String message) {
        this.title = title;
        this.message = message;
        return this;
    }

    public CustomProgressDialog setInfo(@StringRes int title, @StringRes int message) {
        this.title = mContext.getString(title);
        this.message = mContext.getString(message);
        return this;
    }

    public boolean isShowing(){
        return progressDialog != null && progressDialog.isShowing();
    }

    public void show() {
        try{
            progressDialog.setTitle(title);
            progressDialog.setMessage(message);
            if(mContext==null || mContext.isFinishing())
                return;
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void hide() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
