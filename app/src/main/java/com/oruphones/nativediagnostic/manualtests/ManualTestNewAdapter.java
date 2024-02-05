package com.oruphones.nativediagnostic.manualtests;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oruphones.nativediagnostic.BaseUnusedActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.models.tests.TestInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pervacio on 29/08/2017.
 */
public class ManualTestNewAdapter extends RecyclerView.Adapter<ManualTestNewAdapter.MyViewHolder> implements View.OnClickListener {

    private ArrayList<TestInfo> listOfManualTest;
    private ManualTestsActivity manualTestsActivity;
    private int tagId  = 199;

    public ManualTestNewAdapter(ManualTestsActivity manualTestsActivity, ArrayList<TestInfo> _hwTestListView) {
        this.manualTestsActivity = manualTestsActivity;
        this.listOfManualTest = _hwTestListView;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return this.listOfManualTest.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View photoView = inflater.inflate(R.layout.manual_list_view, parent, false);
        return new MyViewHolder(photoView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder viewHolder, int position) {

        final TestInfo testInfo = listOfManualTest.get(position);
        if (testInfo != null) {
            viewHolder.testName.setText(testInfo.getDisplayName());
            viewHolder.checkBox.setChecked(testInfo.isChecked());
            viewHolder.testName.setTag(position);
            viewHolder.checkBox.setTag(position);
            if (!BaseUnusedActivity.isIsAssistedApp()) {
                viewHolder.testName.setOnClickListener(this);
                viewHolder.checkBox.setOnClickListener(this);
            }
        }

    }


    public List<String> getSelectedTest() {
        List<String> selectedTest = new ArrayList<>();
        for (TestInfo testInfo : listOfManualTest) {
            if (testInfo.isChecked())
                selectedTest.add(testInfo.getName());
        }
        return selectedTest;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        boolean isChecked =  listOfManualTest.get(position).isChecked();
        listOfManualTest.get(position).setChecked(!isChecked);
        manualTestsActivity.updateSelectAllCheckBox();
        notifyItemChanged(position);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView testName;
        CheckBox checkBox;
        ImageView testImage;
        View mView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            testName = (TextView) itemView.findViewById(R.id.test_name_tv);
            testImage = (ImageView) itemView.findViewById(R.id.images_category);
            checkBox = (CheckBox) itemView.findViewById(R.id.manual_test_checkbox);
            this.mView = itemView;
        }
    }

}
