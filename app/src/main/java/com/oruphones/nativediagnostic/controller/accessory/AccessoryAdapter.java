package com.oruphones.nativediagnostic.controller.accessory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.models.AccessoryDataSet;

import java.util.List;

/**
 * Created by Pervacio on 07/08/2017.
 */
public class AccessoryAdapter extends RecyclerView.Adapter<AccessoryAdapter.MyViewHolder> {

    private List<AccessoryDataSet> testResultList;

    public AccessoryAdapter(List<AccessoryDataSet> testResultList) {
        this.testResultList = testResultList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.dialog_accessory_rv_item, parent, false);
        return new MyViewHolder(photoView);
    }

    @Override
    public void onBindViewHolder(@NonNull final  MyViewHolder myViewHolder, int position) {
        AccessoryDataSet accessoryDataSet = (AccessoryDataSet) testResultList.get(position);

        myViewHolder.accessoryItemText.setText(accessoryDataSet.getTitle());
        myViewHolder.accessoryItemIcon.setImageResource(accessoryDataSet.getDrawableId());

    }

    @Override
    public int getItemCount() {
        return testResultList.size();
    }


    public class MyViewHolder  extends  RecyclerView.ViewHolder{
        TextView accessoryItemText;
        ImageView accessoryItemIcon;
        View mView;
        public MyViewHolder(@NonNull View view) {
            super(view);
            accessoryItemText = (TextView) view.findViewById(R.id.accessoryItemText);
            accessoryItemIcon = (ImageView) view.findViewById(R.id.accessoryItemIcon);
            this.mView = view;
        }
    }

}
