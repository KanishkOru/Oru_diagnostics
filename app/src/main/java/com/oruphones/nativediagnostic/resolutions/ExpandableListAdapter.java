package com.oruphones.nativediagnostic.resolutions;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.AppInfo;
import com.oruphones.nativediagnostic.util.Util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by PERVACIO on 17-10-2017.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private static String TAG = ExpandableListAdapter.class.getSimpleName();
    public static ArrayList<AppInfo> selectedAppslist = new ArrayList<>();
    public int lastExpandedGroupPosition = -1;
    CheckBox checkAll;
    ArrayList<AppInfo> appInfoList;
    private Context context;
    public int ROBOTO_LIGHT=0;
    public int ROBOTO_MEDIUM=1;
    public int ROBOTO_REGULAR=2;
    public int ROBOTO_THIN=3;
    public int OPENSANS_REGULAR = 4;
    public int OPENSANS_LIGHT = 5;

    public ExpandableListAdapter(Context context, ArrayList<AppInfo> appInfoList, CheckBox _checkAll) {
        this.context = context;
        this.appInfoList = appInfoList;
        this.checkAll = _checkAll;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    @Override
    public int getGroupCount() {
        return appInfoList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return appInfoList.get(groupPosition);
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup
            parent) {
        LayoutInflater _layoutInflator = (LayoutInflater) context.getSystemService
                (context.LAYOUT_INFLATER_SERVICE);
        View convertView1 = _layoutInflator.inflate(R.layout.content_appresolution_listview, null);
        CheckBox checkBox = (CheckBox) convertView1.findViewById(R.id.header_checkbox);
        ImageView imageView = (ImageView) convertView1.findViewById(R.id.header_image);
        TextView name = (TextView) convertView1.findViewById(R.id.header_name);
        TextView date = (TextView) convertView1.findViewById(R.id.header_date);
        TextView size = (TextView) convertView1.findViewById(R.id.header_size);
        checkBox.setTag(groupPosition);
        try {
            AppInfo appInfo= appInfoList.get(groupPosition);
            name.setText(appInfo.getAppName());
            date.setText(getDate(appInfo.getInstalledDate()));
            String appSize= appInfo.getAppSizeKB();
            if(appSize.isEmpty())
                appSize= appInfo.getUsedRamKB();
                Double temp = Double.parseDouble(appSize)/1024;
            if(temp > 0) {
                size.setText(Util.setDecimalPointToTwo(temp) + " MB");
            } else {
                size.setText("-");
            }
            Drawable appIcon = appInfoList.get(groupPosition).getAppIcon();
            setFontToView(name,OPENSANS_REGULAR);
            setFontToView(size,OPENSANS_LIGHT);
            setFontToView(date,OPENSANS_LIGHT);

            if (appIcon != null)
                imageView.setImageDrawable(appIcon);
            checkBox.setChecked(selectedAppslist.contains(appInfoList.get(groupPosition)));
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = (int) buttonView.getTag();
                    CheckBox checkBox = (CheckBox) buttonView.findViewById(R.id.header_checkbox);
                    //appInfoList.get(position).setChecked(isChecked);
                    updateSelectedFileList(appInfoList.get(position),isChecked);
                    if (selectedAppslist.size()!=0 && selectedAppslist.size() == appInfoList.size())
                        checkAll.setChecked(true);
                    else
                        checkAll.setChecked(false);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(((BaseActivity)context).isAssistedApp) {
            checkBox.setEnabled(false);
        }
        return convertView1;
    }

    private String getDate(long timeStamp) {
        try {
            Timestamp stamp = new Timestamp(timeStamp);
            Date date = new Date(stamp.getTime());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
            return simpleDateFormat.format(date);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View
            convertView, ViewGroup parent) {
        //  View convertView1 = null;
      /* try {
            LayoutInflater _layoutInflator = (LayoutInflater) context.getSystemService
                    (context.LAYOUT_INFLATER_SERVICE);
            convertView1 = _layoutInflator.inflate(R.layout.expandable_resolutions_childview, null);

            AppResolutionPojo pdAppResolutionInfo = pdAppResolutionInfos
                    .getStringPDAppResolutionInfoArrayList().get(groupPosition);

            TextView appName = (TextView) convertView1.findViewById(R.id.appname);
            TextView category = (TextView) convertView1.findViewById(R.id.category);
            TextView reason = (TextView) convertView1.findViewById(R.id.reason);

            String[] name_array = pdAppResolutionInfo.getDiSpyName();
            String[] category_array = pdAppResolutionInfo.getDiSpyCategory();
            String[] justification_array = pdAppResolutionInfo.getJustification();


            String str_category = Arrays.toString(category_array);
            str_category = str_category.substring(1, str_category.length() - 1);

            String str_Justification = Arrays.toString(justification_array);
            str_Justification = str_Justification.substring(1, str_Justification.length() - 1).replaceAll(", ", "\n\n");

            String str_DiSpyName = Arrays.toString(name_array);
            str_DiSpyName = str_DiSpyName.substring(1, str_DiSpyName.length() - 1).replaceAll(", ", "\n");

            category.setText(str_category);
            reason.setText(str_Justification);
            appName.setText(str_DiSpyName);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return convertView;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        if (groupPosition != lastExpandedGroupPosition) {
        }
        super.onGroupExpanded(groupPosition);
    }

    private void updateSelectedFileList(AppInfo appInfo,boolean add) {
        if (add) {
            if (!selectedAppslist.contains(appInfo))
                selectedAppslist.add(appInfo);
        } else {
            if (selectedAppslist.contains(appInfo))
                selectedAppslist.remove(appInfo);
        }
    }


    public void setFontToView(TextView tv, int type){
        Typeface tf=null;
        if (type == ROBOTO_LIGHT) {
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_light.ttf");
        } else if (type == ROBOTO_MEDIUM) {
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_medium.ttf");
        } else if (type == ROBOTO_REGULAR) {
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");
        } else if (type == ROBOTO_THIN) {
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_thin.ttf");
        }else if (type == OPENSANS_REGULAR) {
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_thin.ttf");
        }else if (type == OPENSANS_LIGHT) {
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/opensans_light.ttf");
        }else{
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/opensans_regular.ttf");
        }
        tv.setTypeface(tf);
    }


}