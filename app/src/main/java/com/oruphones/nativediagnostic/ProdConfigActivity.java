package com.oruphones.nativediagnostic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.api.StoreIdConfig;
import com.oruphones.nativediagnostic.models.CompanyConfigData;
import com.oruphones.nativediagnostic.webservices.NetworkResponseListener;
import com.oruphones.nativediagnostic.webservices.ODDNetworkModule;

public class ProdConfigActivity extends BaseActivity {
    Button scanQR_Btn;
    EditText pin_et;
    ProgressDialog progressDialog;
    ImageButton btn_Exit;
    View customBar;
    private static ConstraintLayout continue_btn;
    private final int CAMERA_PER_REQ_CODE = 178;
    private final int SCANN_QR_REQ_CODE = 197;
    private final String SSD_PROD = "OnDeviceDiagnostics";
    private final String WRD_PROD = "WRD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //progressDialog = new ProgressDialog(ProdConfigActivity.this);
        //showStoreIdDialogue("No_ERROR");
        customBar = findViewById(R.id.include_action_bar);
        btn_Exit = customBar.findViewById(R.id.btnBack);

        btn_Exit.setOnClickListener(v -> onBackPressed());
        continue_btn = findViewById(R.id.btnpermission_continue);
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionGranted()) {
                    startDiagApp();
                } else {
                    promptPermissions();
                }
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startDiagApp();
    }

    public void showStoreIdDialogue(String errorMessage) {
        dialog = new Dialog(ProdConfigActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_storeid_layout);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        TextView h_tv = dialog.findViewById(R.id.BL_alert_head);
        LinearLayout ll = dialog.findViewById(R.id.ll_BL_alert_text_2);
        h_tv.setText(R.string.enter_code);
        TextView sh_tv = dialog.findViewById(R.id.BL_alert_text_2);
        if (errorMessage.equalsIgnoreCase("NO_ERROR")) {
            ll.setVisibility(View.GONE);
        } else {
            ll.setVisibility(View.VISIBLE);
            sh_tv.setText(errorMessage);
        }
        Button pin_alert_ok = (Button) dialog.findViewById(R.id.pin_alert_ok);
        Button pin_alert_cancel = (Button) dialog.findViewById(R.id.pin_alert_cancel);
        final EditText edirText = (EditText) dialog.findViewById(R.id.store_id);
        edirText.setHint(R.string.code);
        pin_alert_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String storeId = edirText.getText().toString();
                if (dialog != null) dialog.dismiss();
                if (storeId.isEmpty()) {
                    Toast.makeText(ProdConfigActivity.this, R.string.storeid_empty_msg, Toast.LENGTH_LONG).show();
                    showStoreIdDialogue("NO_ERROR");
                } else {
                    getDataFromSerever(storeId);
                }
            }
        });
        pin_alert_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) dialog.dismiss();
                Intent intent1 = new Intent(getApplicationContext(), EndingSessionActivity.class);
                intent1.putExtra("Exit", true);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected String getToolBarName() {
        return "Diagnostics";
    }

    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.diag_permission_prompt;
    }

    @Override
    protected boolean isFullscreenActivity() {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String qrData;
        if (SCANN_QR_REQ_CODE == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                int error = data.getIntExtra("Error", -1);
                if (error == ProdQRScanner.CAMERA_ERROR) {
                    showPopup(R.string.alert, R.string.prod_qr_scan_camera_error_msg);
                } else {
                    qrData = data.getStringExtra("Qr_Data");
                    //CompanyConfigData dcata = new Gson().fromJson(qrData, CompanyConfigData.class);
                    //CompanyConfigData dcata = (CompanyConfigData)o;
                    //saveComapnyConfigData(dcata);
                    //startDiagApp();
                    getDataFromSerever(qrData);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showPopup(final int title, final int message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(R.string.str_ok, null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void getDataFromSerever(String pin) {
        if (dialog != null) dialog.dismiss();
        progressDialog.setTitle(getResources().getString(R.string.connecting_to_server));
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        ODDNetworkModule nm = ODDNetworkModule.getInstance();
        nm.callStoreIdConfig(storeConfigListener, pin, globalConfig.getProductName());
    }

    NetworkResponseListener storeConfigListener = new NetworkResponseListener() {
        @Override
        public void onResponseReceived(Object objct) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            StoreIdConfig config = (StoreIdConfig) objct;
            if (objct != null) {
                if ("PASS".equalsIgnoreCase(config.getStatus())) {
                    CompanyConfigData companyData = config.getCompanyData();
                    saveComapnyConfigData(companyData);
                    startDiagApp();
                } else {
                    showStoreIdDialogue(getResources().getString(R.string.enter_valid_code));
                    Toast.makeText(ProdConfigActivity.this, R.string.invalid_storeid, Toast.LENGTH_LONG).show();
                }
            } else {
                showStoreIdDialogue(getResources().getString(R.string.internet_unvailable));
                Toast.makeText(ProdConfigActivity.this, R.string.try_again, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onError() {
            Toast.makeText(ProdConfigActivity.this, R.string.try_again, Toast.LENGTH_LONG).show();
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            showStoreIdDialogue(getResources().getString(R.string.internet_unvailable));
            //showDialogue();
        }
    };

    private void saveComapnyConfigData(CompanyConfigData companyData) {
        GlobalConfig globalConfig = GlobalConfig.getInstance();
        globalConfig.setStoreID(companyData.getStoreId());
        globalConfig.setServerUrl(companyData.getServerURL());
        globalConfig.setCompanyName(companyData.getCustomer());
        globalConfig.setProductName(companyData.getProduct());
        globalConfig.setServerKey(companyData.getSecretKey());
        /*globalConfig.setCaptureIMEI(config.getCaptureIMEI());
            globalConfig.setLanguage(config.getLanguage());
            globalConfig.setRepIdrequired(config.getIsRepIdRequired());
            globalConfig.setRepIdValidationRequired(config.getIsRepIdValidationRequired());
            globalConfig.setPasswordRequired(config.getIsPasswordrequired());*/
    }

    private void startDiagApp() {
        acceptPermissionDilaogueCheck();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PER_REQ_CODE) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == 0) {
                    startQRScanner();
                    break;
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startQRScanner() {
        Intent intent = new Intent(ProdConfigActivity.this, ProdQRScanner.class);
        startActivityForResult(intent, SCANN_QR_REQ_CODE);
    }

}
