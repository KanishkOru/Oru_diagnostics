package com.oruphones.nativediagnostic.controller.summery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.models.DeviceInfoDataSet;

import java.util.List;

public class ResultGridAdapter extends RecyclerView.Adapter<ResultGridAdapter.ViewHolder> {
    private List<DeviceInfoDataSet> deviceInfoDataSet;

    public ResultGridAdapter(List<DeviceInfoDataSet> deviceInfoDataSet) {
        this.deviceInfoDataSet = deviceInfoDataSet;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View summeryItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_result_layout, parent, false);
//        TextView title =  summeryItem.findViewById(R.id.summeryItemName);
//        TextView value =  summeryItem.findViewById(R.id.summeryItemValueOrStatus);
//        ImageView summeryItemLeftIcon =  summeryItem.findViewById(R.id.summeryItemLeftIcon);
//        ImageView summeryItemRightIcon =  summeryItem.findViewById(R.id.summeryItemRightIcon);
//        ImageView summeryItemRightIconInfo =  summeryItem.findViewById(R.id.summeryItemRightIconInfo);
//
//

        return new ViewHolder(summeryItem);
    }



    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        DeviceInfoDataSet data = deviceInfoDataSet.get(position);


        holder.title.setText(deviceInfoDataSet.get(position).getTitle());

        if (holder.title.getText().length() < 2) {
            holder.title.setVisibility(View.GONE);
        }
        if (deviceInfoDataSet.get(position).hasIcon()) {
            holder.value.setVisibility(View.GONE);
            holder.imageView.setImageResource(deviceInfoDataSet.get(position).getDrawableId());
           }
        else {
            holder.imageView.setVisibility(View.GONE);
            holder.value.setVisibility(View.VISIBLE);
            holder.value.setText(deviceInfoDataSet.get(position).getValue());
        }

    }


    public int getItemCount() {
        return deviceInfoDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title,value;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.result_image_result_view);
            title =  itemView.findViewById(R.id.result_test_name_result);
            value = itemView.findViewById(R.id.result_sub_text);
        }
    }
}
