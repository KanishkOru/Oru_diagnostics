package com.oruphones.nativediagnostic.util;


import static com.oruphones.nativediagnostic.util.Constants.INTERACTION_COUNT_KEY;
import static com.oruphones.nativediagnostic.util.Constants.NO_THANKS_CLICKED_KEY;
import static com.oruphones.nativediagnostic.util.Constants.RATINGSCOUNTGAP;
import static com.oruphones.nativediagnostic.util.Constants.REMIND_LATER_CLICKED_KEY;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


public class CustomRatingsDialog {

    private Activity activity;
    private CustomRatingsDialog customRatingsDialog;
    private PreferenceHelper prefsHelper;

    public CustomRatingsDialog(Activity activity){
        this.activity = activity;
        prefsHelper = PreferenceHelper.getInstance(activity);
    }
    public CustomRatingsDialog getInstance() {
        if (customRatingsDialog!=null){
            return customRatingsDialog;
        }else{
            return customRatingsDialog = new CustomRatingsDialog(activity);
        }

    }
    public void createRatingsDialog() {

        Increament();
        CardView manualalertcard = activity.findViewById(R.id.rate_us_dialog);
        View view = LayoutInflater.from(activity).inflate(R.layout.rate_us_dialog, manualalertcard);
        TextView Rate_us_btn = view.findViewById(R.id.RateUsBtn);
        TextView remind_me_later = view.findViewById(R.id.RemindLaterbtn);
        TextView no_thanks = view.findViewById(R.id.NoBtn);


        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        builder.setCancelable(false);

        final AlertDialog alertdialog = builder.create();


        Rate_us_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String appPackageName = "com.oruphones.oru";
                onOptionClicked(false,false);
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException e) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

                alertdialog.dismiss();
            }
        });
        remind_me_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionClicked(true,false);
                alertdialog.dismiss();
            }
        });

        no_thanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionClicked(false,true);
                alertdialog.dismiss();
            }
        });

        if (alertdialog.getWindow() != null) {
            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        }
        DLog.d("AlertBool", String.valueOf(shouldShowRatingDialog()));
        if (shouldShowRatingDialog()){
            alertdialog.show();
        }


    }


    private void Increament(){
        if (prefsHelper.getBooleanItem(REMIND_LATER_CLICKED_KEY)){
            int interactionCount = getInteractionCount();

            interactionCount++;

            prefsHelper.putIntegerCount(INTERACTION_COUNT_KEY, interactionCount);
        }
    }

    public void onOptionClicked(Boolean RemindLater, Boolean NoThanks){
            prefsHelper.putBooleanItem(REMIND_LATER_CLICKED_KEY,RemindLater);
            prefsHelper.putBooleanItem(NO_THANKS_CLICKED_KEY, NoThanks);
    }

    private int getInteractionCount() {
        return prefsHelper.getIntegerItem(INTERACTION_COUNT_KEY, 0);
    }
    public boolean shouldShowRatingDialog() {
        int interactionCount = getInteractionCount();
        int ratingsCountGap = prefsHelper.getIntegerItem(RATINGSCOUNTGAP, 3);

        if (ratingsCountGap == 0) {
            ratingsCountGap = 3;
        }

        return interactionCount >= 0 && interactionCount % ratingsCountGap == 0 && !prefsHelper.getBooleanItem(NO_THANKS_CLICKED_KEY);
    }

}
