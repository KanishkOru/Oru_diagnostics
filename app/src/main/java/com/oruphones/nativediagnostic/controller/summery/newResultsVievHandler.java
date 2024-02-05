package com.oruphones.nativediagnostic.controller.summery;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oruphones.nativediagnostic.BaseUnusedActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.models.DeviceInfoDataSet;
import com.oruphones.nativediagnostic.models.SummaryDisplayElement;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import java.util.List;

public class newResultsVievHandler extends RelativeLayout {

    private TextView summeryContentHeaderText,summaryNewResultHeader;
    private LinearLayout summeryContentContainer,rightResultContainer,leftResultContainer;
    private SummaryDisplayElement mDisplayElement;

    private  boolean isCustomResultLayoutView = true;
    private static String TAG = newResultsVievHandler.class.getSimpleName();

    public newResultsVievHandler(Context context) {
        super(context);
        initView();
    }

    public newResultsVievHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public newResultsVievHandler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        inflate(getContext(), R.layout.leftviewdisplaytestresultlist, this);
        summeryContentHeaderText  =  findViewById(R.id.newResultHeader);
        summeryContentContainer =  findViewById(R.id.recyclerViewParent);
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
//    @SuppressLint("ClickableViewAccessibility")
//    private void updateSessionId(final TextView sessin_id_value,final String value) {
//        Drawable image = getContext().getResources().getDrawable(R.drawable.ic_copytoclipboard);
//        int h = image.getIntrinsicHeight();
//        int w = image.getIntrinsicWidth();
//        image.setBounds(0, 0, w, h);
//        sessin_id_value.setCompoundDrawables(null, null, image, null);
//        sessin_id_value.setText(value);
//        sessin_id_value.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                final int DRAWABLE_LEFT = 0;
//                final int DRAWABLE_TOP = 1;
//                final int DRAWABLE_RIGHT = 2;
//                final int DRAWABLE_BOTTOM = 3;
//
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    if (motionEvent.getRawX() >= (sessin_id_value.getRight() - sessin_id_value.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//                        ClipData clip = ClipData.newPlainText("copied text",value);
//                        clipboard.setPrimaryClip(clip);
//                        Toast.makeText(getContext(), getResources().getString(R.string.session_id_copied_msg), Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//    }


    public void updateView(List<DeviceInfoDataSet> dataSets, OnClickListener clickListener){
//        for(DeviceInfoDataSet deviceInfoDataSet :dataSets){
//            View summeryItem  = LayoutInflater.from(getContext()).inflate(R.layout.custom_result_layout, null);
//            TextView title =  summeryItem.findViewById(R.id.result_test_name_result);
//          //  TextView value =  summeryItem.findViewById(R.id.summeryItemValueOrStatus);
//            ImageView summeryItemLeftIcon =  summeryItem.findViewById(R.id.result_image_result_view);
//         //   ImageView summeryItemRightIcon =  summeryItem.findViewById(R.id.result_image);
//          //  View summeryRightContainer =  summeryItem.findViewById(R.id.rightContainer);
//           // ImageView summeryItemRightIconInfo =  summeryItem.findViewById(R.id.summeryItemRightIconInfo);
//
//            switch (mDisplayElement){
//                case SuggestedFixes:
//                    //summeryItem.findViewById(R.id.summeryItemDivider).setVisibility(VISIBLE);
//                    // FOR INTERNAL STORAGE WE NEED TO HIDE ITEM STATUS , RIGHT ARROW & LEFT ICON
//                   // summeryItem.findViewById(R.id.summeryItemStatus).setVisibility(deviceInfoDataSet.isHideStatus()?View.GONE:View.VISIBLE);
//                   // summeryItemRightIcon.setVisibility((BaseActivity.isIsAssistedApp()||deviceInfoDataSet.isHideStatus())?View.GONE:View.VISIBLE);
//                  //  summeryRightContainer.setVisibility((TextUtils.isEmpty(deviceInfoDataSet.getValue()) && deviceInfoDataSet.isHideStatus())?View.GONE:View.VISIBLE);
//                    if(deviceInfoDataSet.hasIcon()) {
//                        summeryItemLeftIcon.setVisibility(View.VISIBLE);
//                        summeryItemLeftIcon.setImageResource(deviceInfoDataSet.getDrawableId());
//                    }
//                    if(clickListener!=null && deviceInfoDataSet instanceof SummeryDataSet){
//                        SummeryDataSet<String > summeryDataSet = (SummeryDataSet<String>) deviceInfoDataSet;
//                    //    summeryItemRightIcon.setTag(summeryDataSet.getExtra());
//                      //  summeryItemRightIcon.setOnClickListener(clickListener);
//                    }
//                    break;
//                case TestResults:
//                    if(deviceInfoDataSet.hasIcon()) {
//                      //  summeryItemRightIcon.setVisibility(View.VISIBLE);
//                      //  summeryItemRightIcon.setImageResource(deviceInfoDataSet.getDrawableId());
//                    }
//                    if((deviceInfoDataSet.getAdditionalTestInfo()!=null) && (deviceInfoDataSet.getAdditionalTestInfo().isEmpty()==false)){
//                        Log.d("SummaryContentView"," -- Title "+deviceInfoDataSet.getTitle()+" deviceInfoDataSet.getAdditionalTestInfo() "+deviceInfoDataSet.getAdditionalTestInfo());
//                    //    summeryItemRightIconInfo.setVisibility(View.VISIBLE);
//                        String data = deviceInfoDataSet.getTitle()+"&"+deviceInfoDataSet.getAdditionalTestInfo();
//                     //   summeryItemRightIconInfo.setTag(data);
//                      //  summeryItemRightIconInfo.setOnClickListener(clickListener);
//                    }
//                    break;
//                case DeviceInfo:
//                    if(!BaseActivity.isIsAssistedApp() && getContext().getString(R.string.session_id).equalsIgnoreCase(deviceInfoDataSet.getTitle()) && !"NA".equalsIgnoreCase(deviceInfoDataSet.getValue())){
//                       // updateSessionId(value,deviceInfoDataSet.getValue());
//                    }
//                    break;
//                case BatteryTest:
//                    if(deviceInfoDataSet.hasIcon()) {
//                        summeryItemLeftIcon.setVisibility(View.VISIBLE);
//                        summeryItemLeftIcon.setImageResource(deviceInfoDataSet.getDrawableId());
//                    }
//                    break;
//                case PhysicalDamage:
//
//                    break;
//                case TradeInEligibility:
//                    title.setTextColor(getResources().getColor(deviceInfoDataSet.getTitleColor()));
//                    break;
//            }
//            title.setText(deviceInfoDataSet.getTitle());
//         ///   value.setText(deviceInfoDataSet.getValue());
//
//
//        }
        View summeryItem  = LayoutInflater.from(getContext()).inflate(R.layout.leftviewdisplaytestresultlist, null);
        TextView heading  = summeryItem.findViewById(R.id.newResultHeader);
        RecyclerView recyclerView = summeryItem.findViewById(R.id.summary_result_view);
        ResultGridAdapter gridAdapter = new ResultGridAdapter(dataSets);
//        int spacingPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing); // Replace with your desired spacing in pixels
        int Columns =1;
        int spacingPixels = 2; //


        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = spacingPixels;
                outRect.right = spacingPixels;
                outRect.top = spacingPixels;
                outRect.bottom = spacingPixels;
            }
        });



        switch (mDisplayElement){
            case TestResults:

            case DeviceInfo:

                Columns=2;

                break;
//                    if(!BaseActivity.isIsAssistedApp() && getContext().getString(R.string.session_id).equalsIgnoreCase(deviceInfoDataSet.getTitle()) && !"NA".equalsIgnoreCase(deviceInfoDataSet.getValue())){
//                       // updateSessionId(value,deviceInfoDataSet.getValue());
//                    }
        }

        if (heading.getText().length()<2) {
            heading.setVisibility(View.GONE);
        }
        recyclerView.setAdapter(gridAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), Columns));
        summeryContentContainer.addView(summeryItem);
    }

    public  View getViewAt(int index){
        if (summeryContentContainer==null || summeryContentContainer.getChildCount()<=0 || index > summeryContentContainer.getChildCount())
            return null;
        return summeryContentContainer.getChildAt(index);
    }

}
