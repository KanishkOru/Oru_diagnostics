package com.oruphones.nativediagnostic.manualtests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.common.CustomButton;
import com.oruphones.nativediagnostic.models.ManualTestItem;
import com.oruphones.nativediagnostic.models.tests.TestName;
import com.oruphones.nativediagnostic.models.tests.TestResult;
import com.oruphones.nativediagnostic.util.AnimatedGifUtils;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.TestUtil;

import java.util.List;
import java.util.Objects;

public class ManualTestTryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CURRENT_ITEM = 1;
    private static final int TYPE_COMPLETED_ITEM = 2;
    private int currentTestPosition = 0;
    private List<ManualTestItem> dataList;
    private Context itemContext;
    private onStartClicked onStartClicked;
    private onSkipClicked skipClickListener;
    private onLastItem LastItemListener;
    private onRetryClicked RetryClicked;
    private onFailClicked FailClicked;
    private Boolean isFailedAttempt = false;
    private final RecyclerView recyclerView;
    private GlobalConfig globalConfig;


    // initialise manual test try adapter from here
    public ManualTestTryAdapter(List<ManualTestItem> dataList, Context itemContext, RecyclerView recyclerView, GlobalConfig globalConfig) {
        this.dataList = dataList;
        this.itemContext = itemContext;
        this.recyclerView = recyclerView;
        this.globalConfig = globalConfig;
    }

    // Sets a listener for the start test event.
    public void setOnStartClickListner(onStartClicked listener) {
        this.onStartClicked = listener;
    }

    // Sets a listener for handling test failure.
    public void setOnFailClickListner(onFailClicked listener) {
        this.FailClicked = listener;
    }
    // Sets a listener for retrying a test.
    public void setOnRetryClickListner(onRetryClicked listener) {
        this.RetryClicked = listener;
    }
    // Sets a listener for skipping a test.
    public void setOnSkipClickListener(onSkipClicked listener){
        this.skipClickListener = listener;
    }
    // Sets a listener for handling the last item in the test sequence.
    public void setOnLastItem(onLastItem listner){
        this.LastItemListener = listner;
    }

    // Updates the adapter's data list with skipped states of tests
    public void setSkippedStates(List<ManualTestItem> skippedStates) {

        for (ManualTestItem item : skippedStates){
                for (int i = 0; i < dataList.size(); i++) {
                    if (Objects.equals(item.getTestName(), dataList.get(i).getTestImageName() )){
                        if (dataList.get(i).getTestResult()==null){
                            dataList.get(i).setSkipped(true);
                            dataList.get(i).setIsReattempted(true);
                        }else if (dataList.get(i).getTestResult()!=null && !Objects.equals(dataList.get(i).getTestResult(), TestResult.PASS)){
                            dataList.get(i).setIsReattempted(true);
                        }
                    }
                }
                notifyDataSetChanged();
        }


    }
    private List<ManualTestItem> getDataList(){
        return dataList;
    }


    // Interface for handling the start test event.
    public interface onStartClicked {
        void onStartClick(String testName, int position, TextView timerText, GridView keyLayout, LinearLayout RetryLayout, CustomButton RetryBtn, CustomButton FailBtn, LinearLayout mainLayout, FrameLayout frameLayout, ProgressBar Progressbar, LinearLayout numLayout1, LinearLayout numLayout2, Button viewOne, Button viewTwo, Button viewThree, Button viewFour, Button viewFive, Button viewSix, Button viewSeven, Button viewEight);
    }

    // Interface for handling the last item event.
    public interface onLastItem{
        void onLastItem();
    }
    // Interface for handling the skip test event.
    public interface onSkipClicked{
        void onSkipClick(String testName,int position,RecyclerView recyclerView);
    }
    // Interface for retrying a test.
    public interface onRetryClicked{
        void onRetryClicked(String testName);
    }
    // Interface for failing a test.
    public interface onFailClicked{
        void onFailClicked(String testName);
    }
    // Marks an item as completed in the test sequence.
    public void markItemAsCompleted(int position) {
        if (position >= 0 && position < dataList.size()) {
            ManualTestItem currentItem = dataList.get(position);
            currentItem.setSkipped(true);
            currentItem.setCurrent(false);
        }
    }
    // Notifies the adapter that the current item has changed.
    public void notifyCurrentItemChanged(int newPosition) {
        if (currentTestPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(currentTestPosition);
            currentTestPosition = newPosition;
        }
    }


    public int findNextUnattemptedPosition(int startPosition) {
        List<ManualTestItem> dataList = getDataList();

        for (int i = startPosition; i < dataList.size(); i++) {
            ManualTestItem item = dataList.get(i);
            // Check if the test result is not pass or the test result is null (unattempted)
            if (item.getTestResult() == null || !TestResult.PASS.equalsIgnoreCase(item.getTestResult())) {
                return i;
            }
        }
        return -1;

    }

    // Inflates the appropriate layout for each view type in the RecyclerView.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_CURRENT_ITEM) {
            View itemView = inflater.inflate(R.layout.current_item_layout, parent, false);
            return new CurrentItemViewHolder(itemView, this);
        } else if (viewType == TYPE_COMPLETED_ITEM) {
            View itemView = inflater.inflate(R.layout.manual_test_result, parent, false);
            return new CompletedItemViewHolder(itemView,this);
        }


        return new DefaultViewHolder(new View(parent.getContext()));
    }



    // Binds data to each item in the RecyclerView.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ManualTestItem currentItem = dataList.get(position);

// code for current item view
        if (holder instanceof CurrentItemViewHolder) {
            CurrentItemViewHolder currentViewHolder = (CurrentItemViewHolder) holder;
//
//            int pos = findNextUnattemptedPosition(position + 1);
//
//            recyclerView.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (pos!=-1){
//                        scrollToPosition(pos);
//                        currentViewHolder.adapter.notifyItemChanged(pos);
//                    }else {
//                        currentItem.setIsLastItem(true);
//                        LastItemListener.onLastItem();
//                    }
//                }
//            });


            if (currentItem.getTestResult() != null && (currentItem.getTestResult().equalsIgnoreCase(TestResult.PASS) )) {


                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        int nextPosition = position + 1;
                        if (nextPosition < currentViewHolder.adapter.getItemCount()) {
                            DLog.d("PositionFail1","String.valueOf(finalPosition233)");
                           recyclerView.smoothScrollToPosition(nextPosition);
                            currentViewHolder.adapter.notifyCurrentItemChanged(nextPosition);
                        }else{
                            currentItem.setIsLastItem(true);
                            LastItemListener.onLastItem();
                        }
                    }
                });

            } else
                if (currentItem.getTestResult() != null && (currentItem.getTestResult().equalsIgnoreCase(TestResult.FAIL) )){
                int finalPosition = position;
                if (!isFailedAttempt){
                    DLog.d("PositionFail1","String.valueOf(finalPosition2)");
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            int nextPosition = finalPosition + 1;
                            if (nextPosition < currentViewHolder.adapter.getItemCount()) {
                                recyclerView.smoothScrollToPosition(nextPosition);
                                currentViewHolder.adapter.notifyCurrentItemChanged(nextPosition);
                            }else{
                                currentItem.setIsLastItem(true);
                                LastItemListener.onLastItem();
                            }
                        }
                    });

                }else{
                  currentView(currentViewHolder,currentItem,position);
                }

            } else{
                currentView(currentViewHolder,currentItem,position);

            }



        } else if (holder instanceof CompletedItemViewHolder) {

            // code for completed of skipped item view


            CompletedItemViewHolder completedViewHolder = (CompletedItemViewHolder) holder;
            try {
                completedViewHolder.testImage.setImageResource(TestUtil.manualtestImageMap.get(currentItem.getTestImageName()));
            }catch (Exception e){
                e.printStackTrace();
            }
            completedViewHolder.resultTestNameResult.setText(currentItem.getTestName());
            completedViewHolder.testOneLineDescription.setText(currentItem.getTestDescription());

            if (currentItem.isSkipped()){
                completedViewHolder.layoutBackground.setClickable(true);
                completedViewHolder.testResultText.setVisibility(View.VISIBLE);
                completedViewHolder.resultImage.setVisibility(View.VISIBLE);
                completedViewHolder.retryButtonManualTest.setVisibility(View.VISIBLE);
                completedViewHolder.resultImage.setImageResource(R.drawable.ic_skipped);
                completedViewHolder.layoutBackground.setBackgroundResource(R.drawable.test_tile_bg_yellow);
                completedViewHolder.retryButtonManualTest.setText(R.string.manual_test_result_skipped);
                completedViewHolder.retryButtonManualTest.setTextColor(ContextCompat.getColor(itemContext, android.R.color.holo_red_dark));
                int finalPosition2 = position;
                completedViewHolder.layoutBackground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (currentItem.isLastItem()){
                            DLog.d("PositionFail1","setlastfalse");
                            currentItem.setIsLastItem(false);
                            completedViewHolder.adapter.notifyCurrentItemChanged(position);
                        }else{
                            completedViewHolder.adapter.swapCurrentAndSkipped(finalPosition2);
                            completedViewHolder.adapter.notifyDataSetChanged();
                        }


                    }
                });

            }

            else if (currentItem.getTestResult()!= null) {
                currentItem.setCurrent(false);
                if (Objects.equals(currentItem.getTestImageName(), TestName.CALLTEST))
                    isFailedAttempt = true;
                completedViewHolder.testResultText.setVisibility(View.INVISIBLE);
                if (currentItem.getTestResult().equalsIgnoreCase(TestResult.PASS)){

                    completedViewHolder.resultImage.setVisibility(View.VISIBLE);
                    completedViewHolder.retryButtonManualTest.setVisibility(View.VISIBLE);
                    completedViewHolder.layoutBackground.setClickable(false);
                    completedViewHolder.resultImage.setImageResource(R.drawable.ic_pass);
                    completedViewHolder.layoutBackground.setBackgroundResource(R.drawable.test_tile_bg);
                    completedViewHolder.retryButtonManualTest.setText(R.string.result_pass);
                    completedViewHolder.retryButtonManualTest.setTextColor(ContextCompat.getColor(itemContext, android.R.color.holo_green_dark));
                }else if (currentItem.getTestResult().equalsIgnoreCase(TestResult.FAIL)){
                    completedViewHolder.resultImage.setVisibility(View.VISIBLE);
                    completedViewHolder.retryButtonManualTest.setVisibility(View.VISIBLE);
                    completedViewHolder.resultImage.setImageResource(R.drawable.ic_fail);
                    completedViewHolder.layoutBackground.setClickable(true);
                    completedViewHolder.layoutBackground.setBackgroundResource(R.drawable.test_tile_bg_yellow);
                    completedViewHolder.retryButtonManualTest.setText(R.string.result_fail);
                    int finalPosition2 = position;
                    completedViewHolder.layoutBackground.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DLog.d("PositionFail1",String.valueOf(finalPosition2));
                            isFailedAttempt = true;
                            completedViewHolder.adapter.swapCurrentAndSkipped(finalPosition2);

                            completedViewHolder.adapter.notifyDataSetChanged();

                        }
                    });
                    completedViewHolder.retryButtonManualTest.setTextColor(ContextCompat.getColor(itemContext, android.R.color.holo_red_dark));
                }
                else{
                    completedViewHolder.resultImage.setVisibility(View.VISIBLE);
                    completedViewHolder.retryButtonManualTest.setVisibility(View.VISIBLE);
                    completedViewHolder.resultImage.setImageResource(R.drawable.ic_fail);
                    completedViewHolder.layoutBackground.setBackgroundResource(R.drawable.test_tile_bg_yellow);
                    completedViewHolder.retryButtonManualTest.setText(R.string.result_fail);
                    completedViewHolder.retryButtonManualTest.setTextColor(ContextCompat.getColor(itemContext, android.R.color.holo_red_dark));
                }
            }
            else {

                completedViewHolder.testResultText.setVisibility(View.INVISIBLE);
                completedViewHolder.resultImage.setVisibility(View.INVISIBLE);
                completedViewHolder.layoutBackground.setClickable(false);
                completedViewHolder.layoutBackground.setBackgroundResource(R.drawable.test_tile_bg_default);
                completedViewHolder.retryButtonManualTest.setVisibility(View.INVISIBLE);

            }


        }
    }

    // function to keep the current item in middle
    public void scrollToPosition(int position) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;

            recyclerView.post(() -> {
                if (recyclerView.isAttachedToWindow()) {
                    int screenHeight = recyclerView.getHeight();
                    recyclerView.measure(View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                    recyclerView.post(() -> {
                        View itemView = linearLayoutManager.findViewByPosition(position);
                        if (itemView != null) {
                            int itemHeight = itemView.getHeight();
                            int offset = (screenHeight - itemHeight) / 2;
                            if (position < 3 || position > dataList.size() - 3) {
                                linearLayoutManager.scrollToPosition(position);

                            }else{
                                linearLayoutManager.scrollToPositionWithOffset(position, offset);
                            }


                        }
                    });
                }
            });
        }
    }

    private boolean allSubsequentItemsPassed(int position) {
        for (int i = position + 1; i < dataList.size(); i++) {
            ManualTestItem item = dataList.get(i);
            // Assuming getTestResult() returns null if not attempted, and there's a constant for "PASS"
            if (!TestResult.PASS.equalsIgnoreCase(item.getTestResult())) {
                return false;
            }
        }
        return true;
    }


    // Custom logic for displaying the current test view.
    private void currentView(CurrentItemViewHolder currentViewHolder ,ManualTestItem currentItem ,int position){
        currentViewHolder.currentTestView.setText(currentItem.getTestName());

        if ((globalConfig.getCurrentTestManual().equalsIgnoreCase(TestName.DIMMINGTEST) && currentItem.getTestImageName().equalsIgnoreCase(TestName.DIMMINGTEST)) ||
        globalConfig.getCurrentTestManual().equalsIgnoreCase(TestName.DISPLAYTEST)&& currentItem.getTestImageName().equalsIgnoreCase(TestName.DISPLAYTEST)||
                globalConfig.getCurrentTestManual().equalsIgnoreCase(TestName.CALLTEST)&& currentItem.getTestImageName().equalsIgnoreCase(TestName.CALLTEST)){
            DLog.d("Recyclerview Manual",globalConfig.getCurrentTestManual());
            currentViewHolder.testDescription.setText(currentItem.getTestDescription());
            currentViewHolder.retryBtn.setText("YES");
            currentViewHolder.failBtn.setText("NO");
            currentViewHolder.mainLayoutManualTest.setVisibility(View.GONE);
            currentViewHolder.retryLayout.setVisibility(View.VISIBLE);
            if (currentItem.getTestImageName().equalsIgnoreCase(TestName.CALLTEST)){


                currentViewHolder.failBtn.setOnClickListener(view -> {
                    FailClicked.onFailClicked(currentItem.getTestImageName());
                    globalConfig.setCurentTestManual("Test");
                    int nextPosition = position + 1;
                    currentItem.setCurrent(false);
                    currentViewHolder.adapter.notifyItemChanged(position);
                    if (nextPosition < currentViewHolder.adapter.getItemCount()) {
                        recyclerView.smoothScrollToPosition(nextPosition);
                        currentViewHolder.adapter.notifyCurrentItemChanged(nextPosition);
                    }else{
                        currentItem.setIsLastItem(true);
                        LastItemListener.onLastItem();
                    }

                });
                currentViewHolder.retryBtn.setOnClickListener(view -> {
                    RetryClicked.onRetryClicked(currentItem.getTestImageName());
                    globalConfig.setCurentTestManual("Test");
                    currentItem.setCurrent(false);
                    currentViewHolder.adapter.notifyItemChanged(position);
                    int nextPosition = position + 1;
                    if (nextPosition < currentViewHolder.adapter.getItemCount()) {
                        recyclerView.smoothScrollToPosition(nextPosition);
                        currentViewHolder.adapter.notifyCurrentItemChanged(nextPosition);
                    }else{
                        currentItem.setIsLastItem(true);
                        LastItemListener.onLastItem();
                    }
                });


            }else{
                currentViewHolder.failBtn.setOnClickListener(view -> {
                    FailClicked.onFailClicked(currentItem.getTestImageName());
                    globalConfig.setCurentTestManual("Test");
                });
                currentViewHolder.retryBtn.setOnClickListener(view -> {
                    RetryClicked.onRetryClicked(currentItem.getTestImageName());
                    globalConfig.setCurentTestManual("Test");
                });
            }




        }else{
            currentViewHolder.testDescription.setText(currentItem.getCurrentDescription());
        }
        currentViewHolder.acceptTv.setOnClickListener(view -> {
            if (currentItem.getTestResult() != null && (currentItem.getTestResult().equalsIgnoreCase(TestResult.FAIL) )){
                isFailedAttempt  = false;
            }
            currentItem.setCurrent(true);
            onStartClicked.onStartClick(currentItem.getTestImageName(), position,currentViewHolder.timerText,currentViewHolder.keysLayout,currentViewHolder.retryLayout,currentViewHolder.retryBtn,currentViewHolder.failBtn,currentViewHolder.mainLayoutManualTest,currentViewHolder.layoutFrame,currentViewHolder.progressBar,currentViewHolder.initialNumLayout,currentViewHolder.secondNumLayout
                    ,currentViewHolder.one,currentViewHolder.two,currentViewHolder.three,currentViewHolder.four,currentViewHolder.five,currentViewHolder.six,currentViewHolder.seven,currentViewHolder.eight);
        });
        currentViewHolder.cancelTv.setOnClickListener(view -> {
            if (position == dataList.size()-1){
                DLog.d("PositionFail1","lastItem");
                currentItem.setIsLastItem(true);
                currentViewHolder.adapter.notifyItemChanged(position);
            }
            if (allSubsequentItemsPassed(position)){
                DLog.d("PositionFail","All position");
                currentItem.setIsLastItem(true);
                currentViewHolder.adapter.notifyItemChanged(position);
            }


            int currentPosition = currentViewHolder.getAdapterPosition();
            skipClickListener.onSkipClick(currentItem.getTestImageName(),currentPosition,recyclerView);

        });
        try {
            currentViewHolder.manualTestImg.setImageResource(TestUtil.manualtestImageMap.get(currentItem.getTestImageName()));
        }catch (Exception e){
            e.printStackTrace();
        }

        AnimatedGifUtils.addToView(currentViewHolder.animatedGIFll,itemContext, currentItem.getTestImageName());
    }

    // Returns the appropriate view type for each position in the RecyclerView.
    @Override
    public int getItemViewType(int position) {
        ManualTestItem item = dataList.get(position);
    //    item.setIsLastItem(allSubsequentItemsPassed(position));
        if (currentTestPosition == position && !item.isLastItem() ) {
            DLog.d("PositionFail1","Current Item");
            return TYPE_CURRENT_ITEM;
        } else  if (currentTestPosition==position && item.getTestResult()!=null && (item.getTestResult().equalsIgnoreCase(TestResult.PASS) || item.getTestResult().equalsIgnoreCase(TestResult.FAIL))){
            DLog.d("PositionFail1","lastItemResult");
            return TYPE_COMPLETED_ITEM;
        }
        else if (item.isLastItem()) {
            DLog.d("PositionFail1","lastItems");
            return TYPE_COMPLETED_ITEM;
        } else {
            return TYPE_COMPLETED_ITEM;
        }
//        ManualTestItem item = dataList.get(position);
//        if (item.isCurrent()) {
//            return TYPE_CURRENT_ITEM;
//        } else {
//            return TYPE_COMPLETED_ITEM;
//        }
    }
    // Returns the total number of items in the data list.
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // ViewHolder class for the current test item view.
    private class CurrentItemViewHolder extends RecyclerView.ViewHolder {
        ImageView manualTestImg;
        TextView currentTestView,testDescription,timerText;
        LinearLayout animatedGIFll,loadingLayout,currentViewHolderLayout;
        CustomButton acceptTv,cancelTv,retryBtn,failBtn;
        ManualTestTryAdapter adapter;
        ProgressBar progressBar;
        LinearLayout initialNumLayout,secondNumLayout,mainLayoutManualTest,retryLayout;
        FrameLayout layoutFrame;
        GridView keysLayout;
        Button one, two, three, four, five, six, seven, eight;

        public CurrentItemViewHolder(@NonNull View itemView, ManualTestTryAdapter adapter) {
            super(itemView);

            manualTestImg = itemView.findViewById(R.id.manual_test_img);
            currentTestView = itemView.findViewById(R.id.current_test_view);
            testDescription = itemView.findViewById(R.id.testDescription);
            animatedGIFll = itemView.findViewById(R.id.animatedGIFll);
            acceptTv = itemView.findViewById(R.id.accept_tv);
            cancelTv = itemView.findViewById(R.id.cancel_tv);
            progressBar = itemView.findViewById(R.id.manual_Progressbar);
            initialNumLayout = itemView.findViewById(R.id.select_num_views_layout);
            secondNumLayout = itemView.findViewById(R.id.select_num_views_layout1);
            mainLayoutManualTest = itemView.findViewById(R.id.main_layout_manual_test);
            retryBtn = itemView.findViewById(R.id.retry_btn);
            failBtn = itemView.findViewById(R.id.fail_btn);
            layoutFrame =itemView.findViewById(R.id.frame_layout);
            one = itemView.findViewById(R.id.one);
            two = itemView.findViewById(R.id.two);
            three = itemView.findViewById(R.id.three);
            four = itemView.findViewById(R.id.four);
            five = itemView.findViewById(R.id.five);
            six = itemView.findViewById(R.id.six);
            seven = itemView.findViewById(R.id.seven);
            eight = itemView.findViewById(R.id.eight);
            keysLayout = itemView.findViewById(R.id.id_keys_grid);
            retryLayout = itemView.findViewById(R.id.retry_Layout);
            timerText = itemView.findViewById(R.id.timer_txt);
//            loadingLayout = itemView.findViewById(R.id.LoadingBar);
//            currentViewHolderLayout = itemView.findViewById(R.id.currentViewHolderLayout);

        this.adapter = adapter;


        }
    }

//    private void scrollToPosition(int position) {
//        if (position >= 3 && position < dataList.size() - 3) {
//            // Scroll to bring the item to the center
//            int screenHeight = recyclerView.getHeight();
//            int itemHeight =
//            int scrollY = (position * itemHeight) - ((screenHeight - itemHeight) / 2);
//            recyclerView.smoothScrollBy(0, scrollY);
//        } else {
//            // For first three and last three items, adjust as needed
//            // Optionally, do nothing to keep them in their place
//        }
//    }

    // Swaps the current test item with a skipped test item.
    public void swapCurrentAndSkipped(int skippedPosition) {
        if (currentTestPosition != RecyclerView.NO_POSITION && skippedPosition < dataList.size()) {


            ManualTestItem currentTest = dataList.get(currentTestPosition);
            ManualTestItem skippedTest = dataList.get(skippedPosition);

            DLog.d("PositionFail1",String.valueOf(currentTestPosition) + String.valueOf(skippedPosition));
            currentTest.setCurrent(false);
            skippedTest.setCurrent(true);

            currentTestPosition = skippedPosition;
        }
    }

    // ViewHolder class for completed test item view.
    private static class CompletedItemViewHolder extends RecyclerView.ViewHolder {
        ImageView testImage,resultImage;
        TextView resultTestNameResult,testOneLineDescription,resultTestObservation,retryButtonManualTest,testResultText;

        ConstraintLayout layoutBackground;
        ManualTestTryAdapter adapter;
        public CompletedItemViewHolder(@NonNull View itemView, ManualTestTryAdapter adapter) {
            super(itemView);

            testImage = itemView.findViewById(R.id.test_image);
            resultTestNameResult = itemView.findViewById(R.id.result_test_name_result);
            testOneLineDescription = itemView.findViewById(R.id.testOneLineDecription);
            resultTestObservation = itemView.findViewById(R.id.result_test_observation);
            resultImage = itemView.findViewById(R.id.result_image);
            layoutBackground = itemView.findViewById(R.id.testTileView);
            retryButtonManualTest = itemView.findViewById(R.id.retry_button_manual_test);
            testResultText = itemView.findViewById(R.id.test_result_text);
            this.adapter = adapter;



        }
    }

    // Default ViewHolder class for unrecognized view types.
    private static class DefaultViewHolder extends RecyclerView.ViewHolder {
        public DefaultViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
