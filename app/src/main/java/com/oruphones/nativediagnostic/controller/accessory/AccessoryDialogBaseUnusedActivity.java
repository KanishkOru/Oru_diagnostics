package com.oruphones.nativediagnostic.controller.accessory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.oruphones.nativediagnostic.BaseUnusedActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.models.AccessoryDataSet;
import com.oruphones.nativediagnostic.util.PreferenceUtil;
import com.oruphones.nativediagnostic.util.ThemeUtil;

import java.util.ArrayList;
import java.util.List;

public class AccessoryDialogBaseUnusedActivity extends BaseUnusedActivity {

    private static final String TAG = "AccessoryDialogBaseActi";
    private static final String EX_LIST = "AccessoryDialogBaseActi";
    private RecyclerView recyclerView ;


    public static void startActivity(Activity activity, List<AccessoryDataSet> list) {
        Intent intent = new Intent(activity, AccessoryDialogBaseUnusedActivity.class);
        intent.putExtra(EX_LIST,(ArrayList<AccessoryDataSet>)list);
        activity.startActivity(intent);
    }

    public static void startForFinishActivity(Activity activity) {
        Intent intent = new Intent(activity, AccessoryDialogBaseUnusedActivity.class);
        intent.putExtra(PreferenceUtil.EX_EXIT,true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fullScreen();
        super.onCreate(savedInstanceState);

        // TODO: 20/08/20 this is only for temporary purpose it need to be handled by base activity only
        ThemeUtil.onActivityCreateSetTheme(this);
        ((TextView)findViewById(R.id.dialogAccessoryTitle)).setText(R.string.accessory_dialog_title);
        ((TextView)findViewById(R.id.dialogAccessoryMessage)).setText(R.string.accessory_dialog_message);
        initViews();
        listeners();
        fetchIntent();
    }


    @Override
    public void onBackPressed() {


    }

    private void fetchIntent(){
        if(getIntent() !=null ){
            if(getIntent().hasExtra(PreferenceUtil.EX_EXIT)){
                finish();
            }else{
                List<AccessoryDataSet> accessoryDataSets = (List<AccessoryDataSet>) getIntent().getSerializableExtra(EX_LIST);
                if(accessoryDataSets!=null && !accessoryDataSets.isEmpty()){
                    setRc(accessoryDataSets);
                }
            }

        }
    }

    private void setRc(List<AccessoryDataSet> list){
        AccessoryAdapter accessoryAdapter = new AccessoryAdapter(list);
        recyclerView.setAdapter(accessoryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        accessoryAdapter.notifyDataSetChanged();
    }
    private void listeners() {
        findViewById(R.id.accessoryOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
            //}
        });
    }






    private void initViews() {
        recyclerView = findViewById(R.id.dialogAccessoryRV);
    }



    @Override
    protected boolean isFullscreenActivity() {
        return true;
    }

    @Override
    protected String getToolBarName() {
        return null;
    }

    @Override
    protected boolean setBackButton() {
        return false;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_accessory;
    }

}
