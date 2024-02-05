package com.oruphones.nativediagnostic.autotests;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.AnimatedGifUtils;
import com.oruphones.nativediagnostic.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

public class AutotestAdapter extends RecyclerView.Adapter<AutotestAdapter.ViewHolder> {

    private List<TestInfo> testList = null;
    private int highlightedPosition = -1;
    private Context context;

    public AutotestAdapter(Context context, ArrayList<TestInfo> testList) {
        this.context = context;
        this.testList = testList;
    }
    public void setHighlightedPosition(int position) {
        highlightedPosition = position;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout resultImage;
        TextView testNameResult;
        TextView testObservation;

        public ViewHolder(View itemView) {
            super(itemView);
            resultImage = itemView.findViewById(R.id.result_image);
            testNameResult = itemView.findViewById(R.id.result_test_name_result);
            testObservation = itemView.findViewById(R.id.result_test_observation);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.results_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TestInfo testInfo = testList.get(position);
        holder.testNameResult.setText(testInfo.getDisplayName());

        holder.testObservation.setText(CommonUtil.getMappedTestResult(testInfo.getTestResult()));
        AnimatedGifUtils.setImageRes(holder.resultImage,context,testInfo.getName());

        if (position == highlightedPosition) {
            // Apply highlighting styles to the highlighted item
            holder.itemView.setScaleX(1.0f);
            holder.itemView.setScaleY(1.0f);
            holder.itemView.setAlpha(1.0f);
        }
        else {
            holder.itemView.setScaleX(0.8f);
            holder.itemView.setScaleY(0.8f);
            holder.itemView.setAlpha(0.5f);
        }
        if (TestResult.CANBEIMPROVED.equals(testInfo.getTestResult())) {
            if (GlobalConfig.getInstance().getCompanyName().equalsIgnoreCase("TelefonicaO2UK")) {
                holder.resultImage.setBackgroundResource(R.drawable.results_optimizable_amber);
            } else {
                holder.resultImage.setBackgroundResource(R.drawable.icon_success);
            }
        } else if (TestResult.PASS.equals(testInfo.getTestResult())) {
            holder.resultImage.setBackgroundResource(R.drawable.icon_success);
        } else if (TestResult.FAIL.equals(testInfo.getTestResult())) {
            holder.resultImage.setBackgroundResource(R.drawable.icon_fail);
        } else if (TestResult.OPTIMIZED.equals(testInfo.getTestResult())) {
            holder.resultImage.setBackgroundResource(R.drawable.icon_success);
        } else if (TestResult.NOTEQUIPPED.equals(testInfo.getTestResult())) {
            holder.resultImage.setBackgroundResource(R.drawable.ic_not_equipped);
        } else if (TestResult.ACCESSDENIED.equals(testInfo.getTestResult())) {
            holder.resultImage.setBackgroundResource(R.drawable.ic_error);
        } else if (TestResult.NOTSUPPORTED.equals(testInfo.getTestResult())) {
            holder.resultImage.setBackgroundResource(R.drawable.ic_notsupported);
        } else {
            holder.resultImage.setBackgroundResource(R.drawable.blank_circle);
        }
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }
}

