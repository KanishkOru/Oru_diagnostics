package com.oruphones.nativediagnostic.controller;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.oruphones.nativediagnostic.R;


public class CSatReasonAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private String[] resonList;
    private ReasonAdapterListener mListener;
    public CSatReasonAdapter(Context context, int textViewResourceId,
                             String[] objects) {
        super(context, textViewResourceId, objects);
        this.mContext = context;
        this.resonList = objects;
        this.mListener = (ReasonAdapterListener) context;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_row, parent, false);
        }
        TextView label = (TextView) convertView.findViewById(R.id.spinnerText);
        label.setText(resonList[position]);
        label.setTextColor(position == 0 ? ContextCompat.getColor(mContext, R.color.light_black) : Color.BLACK);
        if(mListener!=null){
            label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onReasonSelection(resonList[position],position);
                }
            });
        }

        return convertView;
    }

    public interface ReasonAdapterListener{
        void onReasonSelection(String s,int position);
    }
}