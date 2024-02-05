package com.oruphones.nativediagnostic.util;

/**
 * Created by Pervacio on 09-11-2016.
 */

import android.app.Activity;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

/**
 * Created by Pervacio on 09-11-2016.
 */


public class StartLocationAlert implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static String TAG = StartLocationAlert.class.getSimpleName();
    Activity context;
    public static final int RC_GPS_SETTINGS = 111;
    GoogleApiClient googleApiClient;
    private LocationCallback mLocationCallback;
    private LocationsSucessCallback mLocationSuccessCallbak;


    // TODO: 20/5/21 this should move to onedaig
    public static boolean has30_R_11(){
        return Build.VERSION.SDK_INT >= 30;
    }


    public StartLocationAlert(Activity context, LocationCallback callback) {
        this.context = context;
        this.mLocationCallback =  callback;
        googleApiClient = getInstance();
        if (googleApiClient != null) {
            settingsrequest();
            googleApiClient.connect();
        }
    }

    public StartLocationAlert(Activity context, LocationCallback callback, LocationsSucessCallback locationsSucessCallback) {
        this.context = context;
        this.mLocationCallback =  callback;
        this.mLocationSuccessCallbak =  locationsSucessCallback;

        googleApiClient = getInstance();
        if (googleApiClient != null) {
            settingsrequest();
            googleApiClient.connect();
        }
    }

    public GoogleApiClient getInstance() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        return mGoogleApiClient;
    }

    public void settingsrequest() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        DLog.d(TAG,"Button Clicked1");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        DLog.d(TAG,"Button Clicked1");
                        try {
                            status.startResolutionForResult(context, RC_GPS_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        Toast.makeText(context, context.getResources().getString(R.string.location_text), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        DLog.d(TAG, "onConnected : ");
        if(mLocationSuccessCallbak!=null) {
            mLocationSuccessCallbak.onLocationConnectionSuccess(true, "");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        DLog.d(TAG, "onConnectionSuspended : " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String failReason;
        DLog.d(TAG, "onConnectionFailed: " + connectionResult.toString() + connectionResult.getErrorCode());
        if (connectionResult.getErrorCode() == 2) {
            failReason = "* Google Play Services Requires Update.";
        } else {
            failReason = "* Some Problem with Google Play Services.";
        }
       DLog.d(TAG, "failReason : " + failReason);
        if(mLocationCallback!=null) {
            mLocationCallback.onLocationConnectionFailed(false, failReason);
        }

      /* if (context instanceof PinValidationActivity){
            PinValidationActivity pinValidationActivity = (PinValidationActivity)context;
            pinValidationActivity.locationConnectionFailed();
        }*/
    }

    public interface LocationCallback {
        void onLocationConnectionFailed(boolean resultPassFail, String reason);
    }

    public interface LocationsSucessCallback {
        void onLocationConnectionSuccess(boolean resultPassFail, String reason);
    }
}