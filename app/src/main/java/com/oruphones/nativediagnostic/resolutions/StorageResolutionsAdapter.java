
package com.oruphones.nativediagnostic.resolutions;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.FileInfo;
import com.oruphones.nativediagnostic.controller.callbacks.OnItemSelectListener;
import com.oruphones.nativediagnostic.controller.widgets.CustomThumbnailView;
import com.oruphones.nativediagnostic.util.Util;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Surya Polasanapalli on 17/10/2017.
 */

public class StorageResolutionsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<FileInfo> fileList;
    private CheckBox checkAllItems;
    private OnItemSelectListener<FileInfo> mItemSelectListener;

    public int ROBOTO_LIGHT=0;
    public int ROBOTO_MEDIUM=1;
    public int ROBOTO_REGULAR=2;
    public int ROBOTO_THIN=3;

    public static ArrayList<FileInfo> selectedFilesList = new ArrayList<>();

    public StorageResolutionsAdapter(Context context, ArrayList<FileInfo> fileList, CheckBox checkBox) {
        this.context = context;
        this.fileList = fileList;
        this.checkAllItems = checkBox;

        if(context instanceof OnItemSelectListener){
            mItemSelectListener = (OnItemSelectListener<FileInfo>) context;
        }
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public FileInfo getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        CheckBox checkBox;
        TextView name;
        TextView date;
        TextView size;
        CustomThumbnailView itemThumbnailView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater _layoutInflator = (LayoutInflater) context.getSystemService
                    (context.LAYOUT_INFLATER_SERVICE);
            convertView = _layoutInflator.inflate(R.layout.content_storage_resolution_childview, null);
            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.header_checkbox);
            holder.name = (TextView) convertView.findViewById(R.id.header_name);
            holder.date = (TextView) convertView.findViewById(R.id.header_date);
            holder.itemThumbnailView = convertView.findViewById(R.id.itemThumbnailView);
            if(BaseActivity.isAssistedApp) {
                holder.date.setVisibility(View.INVISIBLE);
                holder.checkBox.setEnabled(false);
            }
            holder.size = (TextView) convertView.findViewById(R.id.header_size);
            setFontToView(holder.name,ROBOTO_REGULAR);
            setFontToView(holder.date,ROBOTO_LIGHT);
            setFontToView(holder.size,ROBOTO_LIGHT);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            final FileInfo fileInfo = fileList.get(position);
            holder.name.setText(fileInfo.getFileName());
            holder.name.setTag(position);
            holder.date.setText(getDate(fileInfo.getCreatedDate()));
            holder.checkBox.setChecked(selectedFilesList.contains(fileInfo));
            if (fileInfo.getFileSize() != null) {
                Double temp = Double.parseDouble(fileInfo.getFileSize())/1024;
                holder.size.setText(Util.setDecimalPointToTwo(temp)+" MB");
            } else {
                holder.size.setText("Cannot Estimate");
            }
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox item = (CheckBox) view;
                    //fileList.get(position).setChecked(item.isChecked());
                    updateSelectedFileList(fileList.get(position),item.isChecked());
                    if (selectedFilesList.size()!=0 && selectedFilesList.size() == fileList.size())
                        checkAllItems.setChecked(true);
                    else
                        checkAllItems.setChecked(false);
                }
            });

            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    if(mItemSelectListener!=null){
                        mItemSelectListener.onItemSelect(pos,getItem(pos));
                    }
                }
            });
            holder.itemThumbnailView.showPreview(fileInfo);
            holder.itemThumbnailView.setTag(position);
            holder.itemThumbnailView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    if(mItemSelectListener!=null){
                        mItemSelectListener.onItemSelect(pos,getItem(pos));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }


    private String getDate(String timeStamp) {
        Timestamp stamp = new Timestamp(Long.parseLong(timeStamp));
        Date date = new Date(stamp.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
        return simpleDateFormat.format(date);
    }

    private void updateSelectedFileList(FileInfo fileInfo, boolean isChecked) {
        if (isChecked) {
            if (!selectedFilesList.contains(fileInfo))
                selectedFilesList.add(fileInfo);
        } else {
            if (selectedFilesList.contains(fileInfo))
                selectedFilesList.remove(fileInfo);
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
        }else{
            tf = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");
        }
        tv.setTypeface(tf);
    }

}

