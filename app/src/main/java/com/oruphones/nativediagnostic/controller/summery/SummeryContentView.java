package com.oruphones.nativediagnostic.controller.summery;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.oruphones.nativediagnostic.BaseUnusedActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.models.DeviceInfoDataSet;
import com.oruphones.nativediagnostic.models.SummaryDisplayElement;
import com.oruphones.nativediagnostic.models.SummeryDataSet;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import java.util.List;

public class SummeryContentView extends LinearLayout {

    private TextView summeryContentHeaderText,summaryNewResultHeader;
    private LinearLayout summeryContentContainer,rightResultContainer,leftResultContainer;
    private SummaryDisplayElement mDisplayElement;
    private static String TAG = SummeryContentView.class.getSimpleName();

    private  boolean isCustomResultLayoutView = true;

    public SummeryContentView(Context context) {
        super(context);
        initView();
    }

    public SummeryContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SummeryContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        inflate(getContext(), R.layout.activity_summery_content_view, this);
        summeryContentHeaderText  =  findViewById(R.id.summeryContentHeaderText);
        summeryContentContainer =  findViewById(R.id.summeryContentContainer);
        summeryContentContainer.removeAllViews();
    }


    public void setTitle(String title){
        summeryContentHeaderText.setText(title);
    }
    public void setDisplayElement(SummaryDisplayElement summaryDisplayElement){
        this.mDisplayElement = summaryDisplayElement;
    }

    //D2D
    public void updateView(List<DeviceInfoDataSet> dataSets){
        for(DeviceInfoDataSet deviceInfoDataSet :dataSets){
            View summeryItem  = LayoutInflater.from(getContext()).inflate(R.layout.activity_summery_content_item, null);
            TextView title =  summeryItem.findViewById(R.id.summeryItemName);
            TextView value =  summeryItem.findViewById(R.id.summeryItemValueOrStatus);
            ImageView summeryItemLeftIcon =  summeryItem.findViewById(R.id.summeryItemLeftIcon);
            ImageView summeryItemRightIcon =  summeryItem.findViewById(R.id.summeryItemRightIcon);
            ImageView summeryItemRightIconInfo =  summeryItem.findViewById(R.id.summeryItemRightIconInfo);

//            customview for new result ui
            View summarynewItem = LayoutInflater.from(getContext()).inflate(R.layout.leftviewdisplaytestresultlist,null);


            switch (mDisplayElement){
                case SuggestedFixes:
                    //summeryItem.findViewById(R.id.summeryItemDivider).setVisibility(VISIBLE);
                    Toast.makeText(getContext(), "suggest", Toast.LENGTH_SHORT).show();

                    summeryItem.findViewById(R.id.summeryItemStatus).setVisibility(deviceInfoDataSet.isHideStatus()?View.GONE:View.VISIBLE);
                    summeryItemRightIcon.setVisibility(BaseUnusedActivity.isIsAssistedApp()?View.GONE:View.VISIBLE);
                    if(deviceInfoDataSet.hasIcon()) {
                        summeryItemLeftIcon.setVisibility(View.VISIBLE);
                        summeryItemLeftIcon.setImageResource(deviceInfoDataSet.getDrawableId());
                    }
                    break;
                case TestResults:
                    if(deviceInfoDataSet.hasIcon()) {
                        summeryItemRightIcon.setVisibility(View.VISIBLE);
                        summeryItemRightIcon.setImageResource(deviceInfoDataSet.getDrawableId());
                    }
                    if((deviceInfoDataSet.getAdditionalTestInfo()!=null) && (deviceInfoDataSet.getAdditionalTestInfo().isEmpty()==false)){
                        DLog.d(TAG,"Title "+deviceInfoDataSet.getTitle()+" deviceInfoDataSet.getAdditionalTestInfo() "+deviceInfoDataSet.getAdditionalTestInfo());
                        summeryItemRightIconInfo.setVisibility(View.VISIBLE);
//                        summeryItemRightIconInfo.setOnClickListener(clickListener);
                    }

                    break;

                case PhysicalDamage:
                    break;


            }
            DLog.e(TAG,"suggest"+"outside");
            title.setText(deviceInfoDataSet.getTitle());
            value.setText(deviceInfoDataSet.getValue());



            summeryContentContainer.addView(summeryItem);
        }

    }


    //SSD
    @SuppressLint("ClickableViewAccessibility")
    private void updateSessionId(final TextView sessin_id_value,final String value) {
        Drawable image = getContext().getResources().getDrawable(R.drawable.ic_copytoclipboard);
        int h = image.getIntrinsicHeight();
        int w = image.getIntrinsicWidth();
        image.setBounds(0, 0, w, h);
        sessin_id_value.setCompoundDrawables(null, null, image, null);
        sessin_id_value.setText(value);
        sessin_id_value.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (motionEvent.getRawX() >= (sessin_id_value.getRight() - sessin_id_value.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("copied text",value);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getContext(), getResources().getString(R.string.session_id_copied_msg), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
    }


    public void updateView(List<DeviceInfoDataSet> dataSets, OnClickListener clickListener){
        for(DeviceInfoDataSet deviceInfoDataSet :dataSets){
            View summeryItem  = LayoutInflater.from(getContext()).inflate(R.layout.activity_summery_content_item, null);
            TextView title =  summeryItem.findViewById(R.id.summeryItemName);
            TextView value =  summeryItem.findViewById(R.id.summeryItemValueOrStatus);
            ImageView summeryItemLeftIcon =  summeryItem.findViewById(R.id.summeryItemLeftIcon);
            ImageView summeryItemRightIcon =  summeryItem.findViewById(R.id.summeryItemRightIcon);
            View summeryRightContainer =  summeryItem.findViewById(R.id.rightContainer);
            LinearLayout tileContainer = summeryItem.findViewById(R.id.resultSummeryContentTile);
            ImageView summeryItemRightIconInfo =  summeryItem.findViewById(R.id.summeryItemRightIconInfo);

            switch (mDisplayElement){
                case SuggestedFixes:
                    //summeryItem.findViewById(R.id.summeryItemDivider).setVisibility(VISIBLE);
                    // FOR INTERNAL STORAGE WE NEED TO HIDE ITEM STATUS , RIGHT ARROW & LEFT ICON
                    summeryItem.findViewById(R.id.summeryItemStatus).setVisibility(deviceInfoDataSet.isHideStatus()?View.GONE:View.VISIBLE);
                    summeryItemRightIcon.setVisibility((BaseUnusedActivity.isIsAssistedApp()||deviceInfoDataSet.isHideStatus())?View.GONE:View.VISIBLE);
                    summeryRightContainer.setVisibility((TextUtils.isEmpty(deviceInfoDataSet.getValue()) && deviceInfoDataSet.isHideStatus())?View.GONE:View.VISIBLE);
                    if(deviceInfoDataSet.hasIcon()) {
                        summeryItemLeftIcon.setVisibility(View.VISIBLE);
                        summeryItemLeftIcon.setImageResource(deviceInfoDataSet.getDrawableId());
                    }
                    if(clickListener!=null && deviceInfoDataSet instanceof SummeryDataSet){
                        SummeryDataSet<String > summeryDataSet = (SummeryDataSet<String>) deviceInfoDataSet;
                        tileContainer.setTag(summeryDataSet.getExtra());
                        tileContainer.setOnClickListener(clickListener);
                    }
                    break;
                case TestResults:
                    if(deviceInfoDataSet.hasIcon()) {
                        summeryItemRightIcon.setVisibility(View.VISIBLE);
                        summeryItemRightIcon.setImageResource(deviceInfoDataSet.getDrawableId());
                    }
                    if((deviceInfoDataSet.getAdditionalTestInfo()!=null) && (deviceInfoDataSet.getAdditionalTestInfo().isEmpty()==false)){
                        DLog.d(TAG," -- Title "+deviceInfoDataSet.getTitle()+" deviceInfoDataSet.getAdditionalTestInfo() "+deviceInfoDataSet.getAdditionalTestInfo());
                        summeryItemRightIconInfo.setVisibility(View.VISIBLE);
                        String data = deviceInfoDataSet.getTitle()+"&"+deviceInfoDataSet.getAdditionalTestInfo();
                        summeryItemRightIconInfo.setTag(data);
                        summeryItemRightIconInfo.setOnClickListener(clickListener);
                    }
                    break;
                case DeviceInfo:
                    if(!BaseUnusedActivity.isIsAssistedApp() && getContext().getString(R.string.session_id).equalsIgnoreCase(deviceInfoDataSet.getTitle()) && !"NA".equalsIgnoreCase(deviceInfoDataSet.getValue())){
                        updateSessionId(value,deviceInfoDataSet.getValue());
                    }
                    break;
                case BatteryTest:
                    if(deviceInfoDataSet.hasIcon()) {
                        summeryItemLeftIcon.setVisibility(View.VISIBLE);
                        summeryItemLeftIcon.setImageResource(deviceInfoDataSet.getDrawableId());
                    }
                    break;
                case PhysicalDamage:

                    break;
                case TradeInEligibility:
                    title.setTextColor(getResources().getColor(deviceInfoDataSet.getTitleColor()));
                    break;
            }
            title.setText(deviceInfoDataSet.getTitle());
            value.setText(deviceInfoDataSet.getValue());


            summeryContentContainer.addView(summeryItem);
        }

    }

    public  View getViewAt(int index){
        if (summeryContentContainer==null || summeryContentContainer.getChildCount()<=0 || index > summeryContentContainer.getChildCount())
            return null;
        return summeryContentContainer.getChildAt(index);
    }

}
