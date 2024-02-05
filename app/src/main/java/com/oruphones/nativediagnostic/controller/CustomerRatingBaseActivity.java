package com.oruphones.nativediagnostic.controller;


import static com.oruphones.nativediagnostic.controller.CustomerRatingBaseActivity.Rating.BAD;
import static com.oruphones.nativediagnostic.controller.CustomerRatingBaseActivity.Rating.EXCELLENT;
import static com.oruphones.nativediagnostic.controller.CustomerRatingBaseActivity.Rating.GOOD;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;
import androidx.appcompat.widget.ListPopupWindow;

import com.oruphones.nativediagnostic.BaseUnusedActivity;
import com.oruphones.nativediagnostic.models.CSATData;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.ThemeUtil;

public abstract class CustomerRatingBaseActivity extends BaseUnusedActivity implements CSatReasonAdapter.ReasonAdapterListener {

    protected static String EX_TYPE = "RatindType";
    protected static String EX_SESSION_ID = "session_id";
    private static String TAG = CustomerRatingBaseActivity.class.getSimpleName();
    String ratingType = "Customer";
    String[] resonList;
    TextView cust_rate_head, cust_rate_msg;
    String customerRating = "";
    String customerRatingReason = "";
    private RadioGroup mRadioGroup;

    private RelativeLayout spinnerLayout;
    private TextView txtSelectedItem;
    private int selectedPosition = -1;
    private FrameLayout spinnerFrame;
    protected Button mSubmitBtn;
    private ListPopupWindow listPopupWindow;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({EXCELLENT, GOOD, BAD})
    public @interface Rating {
        String EXCELLENT = "Excellent";
        String GOOD = "Good";
        String BAD = "Bad";
    }


    public abstract void submitFeedBack(CSATData feedBackData);

    private void initValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            ratingType = bundle.getString(EX_TYPE);
        }
    }


    /* protected void fullScreen() {
         requestWindowFeature(Window.FEATURE_NO_TITLE);
         Window window = getWindow();
         window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
         window.setBackgroundDrawableResource(android.R.color.transparent);
         setFinishOnTouchOutside(false);
         setOrientation( this);
     }

     // Method
     public static void setOrientation(Activity context) {
         if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.O)
             context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
         else
             context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
     }*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fullScreen();
        super.onCreate(savedInstanceState);

        // TODO: 20/08/20 this is only for temporary purpose it need to be handled by base activity only
        ThemeUtil.onActivityCreateSetTheme(this);
        initValues();
        initViews();
        listeners();

        if ("Agent".equalsIgnoreCase(ratingType)) {
            customerRating = getIntent().getStringExtra("CustomerRating");
            customerRatingReason = getIntent().getStringExtra("CustomerRatingReason");
            cust_rate_head.setText(R.string.agent_satisfy_title);
            cust_rate_msg.setText(R.string.agent_satisfy_msg);
        }
        reasonSpinner();
        enableDisableSubmit();
    }


    @Override
    public void onBackPressed() {


    }

    private void listeners() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                enableDisableSubmit();
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSubmitBtn.setEnabled(false);
                String rating = getSelectedRating();
                DLog.d(TAG, "rating - " + rating);
                CSATData feedBackData = new CSATData();
                feedBackData.setCustomerRating(rating);
                if (BAD.equalsIgnoreCase(rating)) {
                    if (selectedPosition > 0) {
                        feedBackData.setCustomerRatingReason(resonList[selectedPosition]);
                    } else {
                        enableDisableSubmit();
                        return;
                    }
                }
                feedBackData.setAgentRating("");
                feedBackData.setAgentRatingReason("");

                DLog.d(TAG, "Submit CSAT : " + feedBackData.toString());
                submitFeedBack(feedBackData);
            }
            //}
        });
    }


    private void showListPopupWindow(View anchorView) {
        if(listPopupWindow==null){
            listPopupWindow = new ListPopupWindow(this);
        }

        /* listPopupWindow.setWidth(600);*/
        listPopupWindow.setHeight(600);


        listPopupWindow.setAnchorView(anchorView);
        CSatReasonAdapter myCustomAdapter = new CSatReasonAdapter(CustomerRatingBaseActivity.this
                , R.layout.custom_spinner_row
                , resonList);
        listPopupWindow.setAdapter(myCustomAdapter);
        listPopupWindow.show();
    }

    private void hideListPopup(){
        if(listPopupWindow!=null){
            listPopupWindow.dismiss();
        }
        listPopupWindow = null;
    }

    private void reasonSpinner() {
        spinnerLayout = (RelativeLayout) findViewById(R.id.spinner_layout);
        spinnerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListPopupWindow(v);
            }
        });

    }



    private void initViews() {
        resonList = getResources().getStringArray(R.array.reasons_list);

        txtSelectedItem = findViewById(R.id.txtSelectedItem);
        mRadioGroup = findViewById(R.id.rating_layout);
        spinnerFrame = findViewById(R.id.spinner_frame);
        spinnerFrame.setVisibility(View.VISIBLE);


        cust_rate_head = findViewById(R.id.cust_rate_head);
        cust_rate_msg = findViewById(R.id.cust_rate_msg);
        mSubmitBtn = findViewById(R.id.submit_btn);
    }

    //Enable submit button only after the selection of rating value
    private void enableDisableSubmit() {
        int checkedId = mRadioGroup.getCheckedRadioButtonId();
        spinnerLayout.setVisibility(checkedId == R.id.bad_rb ? View.VISIBLE : View.GONE);
        if (checkedId == R.id.bad_rb) {
            mSubmitBtn.setEnabled(selectedPosition > 0);
        } else {
            mSubmitBtn.setEnabled(checkedId > 0);
        }
    }

    //Rating text selected by user
    @SuppressLint("NonConstantResourceId")
    @Rating
    private String getSelectedRating() {
        int checkedId = mRadioGroup.getCheckedRadioButtonId();
        if (checkedId==R.id.excellent_rb){
            return EXCELLENT;
        }else if (checkedId == R.id.good_rb){
            return GOOD;
        } else if (checkedId == R.id.bad_rb) {
            return BAD;
        }
//        switch (checkedId) {
//            case R.id.excellent_rb:
//                return EXCELLENT;
//            case R.id.good_rb:
//                return GOOD;
//            case R.id.bad_rb:
//                return BAD;
//        }
        return null;
    }


    @Override
    protected boolean isFullscreenActivity() {
        return true;
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
    protected int getLayoutResource() {
        return R.layout.activity_customer_rating_dialog;
    }

    @Override
    public void onReasonSelection(String s, int position) {
        hideListPopup();
        selectedPosition = position;
        enableDisableSubmit();
        if (position > 0) {
            txtSelectedItem.setText(resonList[position]);
            txtSelectedItem.setTextColor(Color.BLACK);
        } else {
            txtSelectedItem.setText("");
            txtSelectedItem.setHint(resonList[position]);
            txtSelectedItem.setTextColor(Color.DKGRAY);
        }

        txtSelectedItem.setHorizontallyScrolling(true);
        txtSelectedItem.setSelected(true);
    }
}

