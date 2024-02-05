package com.oruphones.nativediagnostic.manualtests;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.StartLocationAlert;

import org.pervacio.onediaglib.atomicfunctions.AFGPS;

public class GPSManualTestActivity extends BaseActivity implements OnMapReadyCallback, StartLocationAlert.LocationsSucessCallback {
    private static String TAG = GPSManualTestActivity.class.getSimpleName();
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    String mCurrentTest;
    private ProgressBar progressBar;
    private LinearLayout mResultBtnLayout;
    private Button mTestPass, mTestFail, mRetest;
    private TextView test_description;

    private Handler monitorGPSStatusHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DLog.d(TAG,"enter handleMessage");
            if(isGPSEnabled()){
                DLog.d(TAG,"enter handleMessage isGPSEnabled true");
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(GPSManualTestActivity.this);
                fetchLocation();
            }else{
                DLog.d(TAG,"enter handleMessage isGPSEnabled false");
                monitorGPSStatusHandler.sendEmptyMessageDelayed(100,500);

            }

        }
    };


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = TestResult.TIMEOUT;
            manualTestResultDialog(mCurrentTest, result,false, true, GPSManualTestActivity.this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DLog.d(TAG,"enter onCreate");
        progressBar = (ProgressBar) findViewById(R.id.manual_Progressbar);
        mResultBtnLayout = (LinearLayout) findViewById(R.id.result_btn_layout);
        test_description = (TextView)findViewById(R.id.test_description);
        mTestPass = (Button) findViewById(R.id.accept_tv);
        mTestFail = (Button) findViewById(R.id.cancel_tv);
        mTestFail.setText(getResources().getString(R.string.str_no));
        mTestPass.setText(getResources().getString(R.string.str_yes));
        test_description.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        mResultBtnLayout.setVisibility(View.GONE);
        handler.sendEmptyMessageDelayed(102,20000);
        mTestPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeMessages(102);
                mResultBtnLayout.setVisibility(View.GONE);
                String result = TestResult.PASS;
                manualTestResultDialog(mCurrentTest, result,false, true, GPSManualTestActivity.this);
            }
        });
        mTestFail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeMessages(102);
                mResultBtnLayout.setVisibility(View.GONE);
                String result = TestResult.FAIL;
                manualTestResultDialog(mCurrentTest, result,false, true, GPSManualTestActivity.this);
            }
        });

        mCurrentTest = getIntent().getStringExtra(BaseActivity.TEST_NAME);
        if(isGPSEnabled()){
            DLog.d(TAG,"GPS Enabled case");
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fetchLocation();
        }else{
            DLog.d(TAG,"GPS Not enabled");
            monitorGPSStatusHandler.sendEmptyMessageDelayed(100,500);
            new StartLocationAlert(this, null,GPSManualTestActivity.this);
        }
    }

    @Override
    protected String getToolBarName() {
        return null;
    }

    @Override
    protected boolean setBackButton() {
        return false;
    }

    @Override
    protected boolean isFullscreenActivity(){
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_gps_manual_test;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(getIntent().getBooleanExtra("startTest", false)) {
            getIntent().putExtra("startTest", false);
        }
    }

    private void fetchLocation() {
        DLog.d(TAG,"enter fetchLocation");
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            DLog.d(TAG,"enter fetchLocation permission not granted");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                DLog.d(TAG,"enter OnSuccessListener onSuccess");
                if (location != null) {
                    currentLocation = location;
                    DLog.d(TAG,"enter OnSuccessListener onSuccess "+currentLocation);

                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMap);
                    assert supportMapFragment != null;
                    supportMapFragment.getMapAsync(GPSManualTestActivity.this);

                    updateResultUI();
                }
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        googleMap.addMarker(markerOptions);
        DLog.d(TAG,"enter onMapReady");

        updateResultUI();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

    private void updateResultUI(){
        DLog.d(TAG,"enter updateResultUI");
        test_description.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        mResultBtnLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLocationConnectionSuccess(boolean resultPassFail, String reason) {
        DLog.d(TAG,"enter onLocationConnectionSuccess");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLocation();
    }

    private boolean isGPSEnabled() {
        AFGPS afgps = new AFGPS();
        return afgps.getState();
    }
}
