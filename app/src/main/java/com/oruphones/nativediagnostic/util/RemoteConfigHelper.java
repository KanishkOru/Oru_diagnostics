package com.oruphones.nativediagnostic.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


public class RemoteConfigHelper {

    private static String TAG = RemoteConfigHelper.class.getSimpleName();
    private Context context;
    private Activity activity;
    Integer RatingsCount;
    
    String CoutingGap = "countForRatingGap";

    public interface Callback {
        void onBackendRatingsCountGap(Long RatingsCount);
    }

    public RemoteConfigHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.RatingsCount = RatingsCount;
    }

    public void startBackendSwitchListener(Callback callback) {
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(activity, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            DLog.d(TAG, "Config params updated: " + updated);
//                            Toast.makeText(context, "Fetch and activate succeeded",
//                                    Toast.LENGTH_SHORT).show();

                            // Use the backEndUrl parameter here
                            Long RatingsCountGap = mFirebaseRemoteConfig.getLong(CoutingGap);
                            if (RatingsCountGap != null ) {
                               
                                callback.onBackendRatingsCountGap(RatingsCountGap);
//                                displayWelcomeMessage(backEndUrl);
                                // Do something with the updated backEndUrl if needed
                            }

                        } else {
//                            Toast.makeText(context, "Fetch failed",
//                                    Toast.LENGTH_SHORT).show();
                            DLog.d(TAG,"fetch failed");
                        }
                    }
                });

//        mFirebaseRemoteConfig.addOnConfigUpdateListener(new ConfigUpdateListener() {
//            @Override
//            public void onUpdate(ConfigUpdate configUpdate) {
//                DLog.d(TAG, "Updated keys: " + configUpdate.getUpdatedKeys());
//
//                mFirebaseRemoteConfig.activate().addOnCompleteListener(new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        Long RatingsCountGap = mFirebaseRemoteConfig.getLong(CoutingGap);
//                        if (RatingsCountGap != null) {
////                            RatingsCount = Math.toIntExact(RatingsCountGap);
////                            PreferenceHelper.getInstance(activity.getApplication()).putIntegerItem(Constants.RATINGSCOUNTGAP,RatingsCount);
//                            //  displayWelcomeMessage(backEndUrl);
//                            callback.onBackendRatingsCountGap(RatingsCountGap);
//                        }
//                       // displayWelcomeMessage(configUpdate.getUpdatedKeys().toString());
//                    }
//                });
//            }
//
//            @Override
//            public void onError(FirebaseRemoteConfigException error) {
//                DLog.w(TAG, "Config update error with code: " + error.getCode());
//            }
//        });
    }


    public void displayWelcomeMessage(String message)
        {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
}
