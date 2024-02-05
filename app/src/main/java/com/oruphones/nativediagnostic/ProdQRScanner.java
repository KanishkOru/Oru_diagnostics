package com.oruphones.nativediagnostic;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.BarcodeView;
import com.journeyapps.barcodescanner.CameraPreview;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


import java.util.List;

public class ProdQRScanner extends AppCompatActivity {

    public static final int CAMERA_ERROR = 100;

    private static final String TAG = ProdQRScanner.class.getSimpleName();
    private static boolean isQRCodeScanned;
    private DecoratedBarcodeView decoratedBarcodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_qrscanner);

        decoratedBarcodeView = (DecoratedBarcodeView) findViewById(R.id.prod_qr_scanner);
        decoratedBarcodeView.setStatusText(getString(R.string.prod_scan_qr));
        setQRCodeScanned(false);
        BarcodeView barcodeView = decoratedBarcodeView.getBarcodeView();
        barcodeView.addStateListener(new CameraPreview.StateListener() {
            @Override
            public void previewSized() {
                DLog.d(TAG, "previewSized: ");
            }

            @Override
            public void previewStarted() {
                DLog.d(TAG, "previewStarted: ");
            }

            @Override
            public void previewStopped() {
                DLog.d(TAG, "previewStopped: ");
            }

            @Override
            public void cameraError(Exception error) {
                DLog.e(TAG, "cameraError: ");
                Intent intent = new Intent();
                intent.putExtra("Error",CAMERA_ERROR);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void cameraClosed() {
                DLog.d(TAG, "cameraClosed: ");
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        scanQRCode(decoratedBarcodeView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        decoratedBarcodeView.pause();
    }

    private void scanQRCode(final DecoratedBarcodeView aBarcodeView) {
        aBarcodeView.resume();
        aBarcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult barcodeResult) {
                String messageString = barcodeResult.getText();
                DLog.d(TAG, "Got QR code message string: " + messageString);
                aBarcodeView.pause();

                setQRCodeScanned(true);
                Intent intent=new Intent();
                intent.putExtra("Qr_Data",messageString);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }

        });
    }

    static boolean isQRCodeScanned(){
        return isQRCodeScanned;
    }

    static void setQRCodeScanned(boolean scanned){
        isQRCodeScanned = scanned;
    }


}
