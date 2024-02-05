package com.oruphones.nativediagnostic.util.DevelopmentTools;

import android.app.ProgressDialog;
import android.content.Context;



public class GlobalUiLoaders {

    // Example loader with ProgressDialog and animated circular ProgressBar
    public static ProgressDialog showCircularLoader(Context context, String title, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);

        // Use a style that includes an indeterminate circular animation
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }


    private static void dismissLoader(ProgressDialog progressDialog) {
        // Dismiss the ProgressDialog
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
