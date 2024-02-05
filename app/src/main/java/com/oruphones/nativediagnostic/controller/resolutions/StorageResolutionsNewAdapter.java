package com.oruphones.nativediagnostic.controller.resolutions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


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
import java.util.List;

/**
 * Created by Surya Polasanapalli on 17/10/2017.
 */
public class StorageResolutionsNewAdapter extends RecyclerView.Adapter<StorageResolutionsNewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<FileInfo> fileList;
    private CheckBox checkAllItems;
    private OnItemSelectListener<FileInfo> mItemSelectListener;
    private ArrayList<FileInfo> selectedFilesList = new ArrayList<>();

    public StorageResolutionsNewAdapter(Context context, ArrayList<FileInfo> fileList, CheckBox checkBox) {
        this.context = context;
        this.fileList = fileList;
        this.checkAllItems = checkBox;

        if (context instanceof OnItemSelectListener) {
            mItemSelectListener = (OnItemSelectListener<FileInfo>) context;
        }
    }

    public ArrayList<FileInfo> getSelectedFilesList() {
        return selectedFilesList;
    }

    public void setSelectedFilesList(List<FileInfo> selectedFilesList) {
        this.selectedFilesList.clear();
        this.selectedFilesList.addAll(selectedFilesList);
        notifyDataSetChanged();
    }

    public void clearSelected() {
        this.selectedFilesList.clear();
        notifyDataSetChanged();
    }

    public FileInfo getItem(int position) {
        return fileList.get(position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.content_storage_resolution_childview, parent, false);
        return new MyViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(position, getItem(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }


    private void updateSelectedFileList(FileInfo fileInfo, boolean isChecked) {
        if (isChecked) {
            if (!getSelectedFilesList().contains(fileInfo))
                getSelectedFilesList().add(fileInfo);
        } else {
            if (getSelectedFilesList().contains(fileInfo))
                getSelectedFilesList().remove(fileInfo);
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView name, date, size;
        CustomThumbnailView itemThumbnailView;
        View mView;

        public MyViewHolder(@NonNull View view) {
            super(view);
            checkBox = (CheckBox) view.findViewById(R.id.header_checkbox);
            name = (TextView) view.findViewById(R.id.header_name);
            date = (TextView) view.findViewById(R.id.header_date);
            itemThumbnailView = view.findViewById(R.id.itemThumbnailView);
            size = (TextView) view.findViewById(R.id.header_size);

            this.mView = view;
        }

        private void bind(final int position, final FileInfo fileInfo) {
            if (BaseActivity.isAssistedApp) {
                date.setVisibility(View.INVISIBLE);
                checkBox.setEnabled(false);
            }
            try {

                name.setText(fileInfo.getFileName());
                date.setText(getDate(fileInfo.getCreatedDate()));
                checkBox.setChecked(getSelectedFilesList().contains(fileInfo));
                if (fileInfo.getFileSize() != null) {
                    Double temp = Double.parseDouble(fileInfo.getFileSize()) / 1024;
                    size.setText(Util.setDecimalPointToTwo(temp) + " MB");
                } else {
                    size.setText("Cannot Estimate");
                }
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox item = (CheckBox) view;
                        updateSelectedFileList(fileList.get(position), item.isChecked());
                        if (getSelectedFilesList().size() != 0 && getSelectedFilesList().size() == fileList.size())
                            checkAllItems.setChecked(true);
                        else
                            checkAllItems.setChecked(false);
                    }
                });

                name.setTag(position);
                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPreview(v);
                    }
                });

                itemThumbnailView.showPreview(fileInfo);
                itemThumbnailView.setTag(position);
                itemThumbnailView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPreview(v);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getDate(String timeStamp) {
            Timestamp stamp = new Timestamp(Long.parseLong(timeStamp));
            Date date = new Date(stamp.getTime());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
            return simpleDateFormat.format(date);
        }

        private void playPreview(View v) {
            int pos = (int) v.getTag();
            if (mItemSelectListener != null) {
                mItemSelectListener.onItemSelect(pos, getItem(pos));
            }
        }

    }

}
