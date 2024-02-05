package com.oruphones.nativediagnostic.home;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.CategoryInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Pervacio on 07/08/2017.
 */
public class CustomAdapter extends ArrayAdapter<CustomAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<CategoryInfo> categoryList;
    private String selectState;
    private int[] categoryIcons;
    private HashMap<String, Integer> categoryIconsMap = new HashMap<>();
    public int ROBOTO_LIGHT=0;
    public int ROBOTO_MEDIUM=1;
    public int ROBOTO_REGULAR=2;
    public int ROBOTO_THIN=3;

    {
        categoryIconsMap.put("BatteryCharging", R.drawable.menu_battery);
        categoryIconsMap.put("SystemCrash", R.drawable.menu_freezecrash);
        categoryIconsMap.put("Connectivity", R.drawable.menu_connectivity);
        categoryIconsMap.put("AudioVibrate", R.drawable.menu_sound);
        categoryIconsMap.put("Camera", R.drawable.menu_camera);
        categoryIconsMap.put("Hardware", R.drawable.menu_hardware);
        categoryIconsMap.put("DisplayTouch", R.drawable.menu_display);
        categoryIconsMap.put("Apps", R.drawable.menu_hardware);
        categoryIconsMap.put("CheckIn", R.drawable.menu_hardware);
        categoryIconsMap.put("CheckOut", R.drawable.menu_hardware);
        categoryIconsMap.put("PPPVerification", R.drawable.menu_hardware);
        categoryIconsMap.put("RunAllDiagnostics", R.drawable.menu_hardware);
        categoryIconsMap.put("NoNavigation", R.drawable.menu_nonavigation);
        categoryIconsMap.put("Sensors", R.drawable.sensor);
    }

    public CustomAdapter(ArrayList<CategoryInfo> categoryList, int[] categoryIcons, Context context, String selectState) {
        super(context, R.layout.custom_list_view);
        this.mContext = context;
        this.selectState = selectState;
        this.categoryList = categoryList;
        this.categoryIcons = categoryIcons;
    }


    @Override
    public int getCount() {
        if (categoryList != null)
            return categoryList.size();
        else
            return 0;
    }

    /*    @Override
        public int getItem(int position) {
            return categoryTestsPojos.get(position);
        }*/
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        MyViewHolder myViewHolder;
        if (convertView == null) {
            myViewHolder = new MyViewHolder();
            if ("Category".equalsIgnoreCase(selectState)) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_view, parent, false);
                myViewHolder.testName = (TextView) convertView.findViewById(R.id.test_name_tv);
                myViewHolder.testSubHeadName = (TextView) convertView.findViewById(R.id.test_subname_tv);
                myViewHolder.categoryImages = (ImageView) convertView.findViewById(R.id.images_category);
                //setFontToView(myViewHolder.testName,ROBOTO_REGULAR);
                //setFontToView(myViewHolder.testSubHeadName,ROBOTO_LIGHT);

            } else {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_list_view, parent, false);
                myViewHolder.historyText = (TextView) convertView.findViewById(R.id.history_tv);
                myViewHolder.historySubText = (TextView) convertView.findViewById(R.id.history_sub_text_tv);
                myViewHolder.time = (TextView) convertView.findViewById(R.id.time_tv);
                setFontToView(myViewHolder.historyText,ROBOTO_REGULAR);
                setFontToView(myViewHolder.historySubText,ROBOTO_LIGHT);
                setFontToView(myViewHolder.time,ROBOTO_LIGHT);
            }

            view = convertView;
            convertView.setTag(myViewHolder);

        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
            view = convertView;
        }
        if ("Category".equalsIgnoreCase(selectState)) {
            CategoryInfo catagoryInfo = categoryList.get(position);
            myViewHolder.testName.setText(catagoryInfo.getDisplayName());
            myViewHolder.testSubHeadName.setText(catagoryInfo.getDescription());
            int imgId = categoryIconsMap.get(catagoryInfo.getName()) != null ? categoryIconsMap.get(catagoryInfo.getName())
                    : R.drawable.menu_hardware;
            myViewHolder.categoryImages.setImageResource(imgId);
            // myViewHolder.categoryImages.setImageResource((Integer) testsPojo.categoryImage.get(position));
        } else {

        }
        return view;
    }

 /*   @Override
    public int getViewTypeCount() {
        if (getCount() != 0)
            return getCount();

        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }*/

    class MyViewHolder {
        TextView testName, testSubHeadName, historyText, historySubText, time;
        ImageView categoryImages;
    }

    public void setFontToView(TextView tv, int type){
        Typeface tf=null;
        if (type == ROBOTO_LIGHT) {
            tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_light.ttf");
        } else if (type == ROBOTO_MEDIUM) {
            tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_medium.ttf");
        } else if (type == ROBOTO_REGULAR) {
            tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_regular.ttf");
        } else if (type == ROBOTO_THIN) {
            tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_thin.ttf");
        }else{
            tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/roboto_regular.ttf");
        }
        tv.setTypeface(tf);
    }
}
