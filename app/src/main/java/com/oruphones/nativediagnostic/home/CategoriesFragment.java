package com.oruphones.nativediagnostic.home;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

import androidx.fragment.app.Fragment;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.CategoryInfo;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.autotests.AutoTestActivity;
import com.oruphones.nativediagnostic.manualtests.ManualTestsActivity;
import com.oruphones.nativediagnostic.models.PDConstants;
import com.oruphones.nativediagnostic.models.tests.TestInfo;
import com.oruphones.nativediagnostic.result.ResultsActivity;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.DeviceInfo;
import com.oruphones.nativediagnostic.util.StartLocationAlert;


public class CategoriesFragment extends Fragment implements StartLocationAlert.LocationCallback{
    private LinearLayout pickCategory;
    View view;
    ListView mListView;
    CustomAdapter mCustomAdapter;
    private static String TAG = CategoriesFragment.class.getSimpleName();
    TextView tv_pickCategory;
    private int[] categoryIcons = {R.drawable.menu_battery,
            R.drawable.menu_freezecrash,
            R.drawable.menu_connectivity,
            R.drawable.menu_sound,
            R.drawable.menu_camera,
            R.drawable.menu_display,
            R.drawable.menu_hardware,
            R.drawable.menu_display,
    };
    ArrayList<CategoryInfo> categoryList;
    //    GlobalConfig globalConfg = GlobalConfig.getInstance();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_categories, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        pickCategory=(LinearLayout)view.findViewById(R.id.pick_category_text);
        pickCategory.setVisibility(View.VISIBLE);
        tv_pickCategory= view.findViewById(R.id.text_pick_category);
        //Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_light.ttf");
        //tv_pickCategory.setTypeface(tf);



        categoryList = PervacioTest.getInstance().getGlobalConfig().getCategoryList();
        mCustomAdapter = new CustomAdapter(categoryList, categoryIcons, getContext(), "Category");
        mListView.setAdapter(mCustomAdapter);

        if(!BaseActivity.isAssistedApp) {
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    // String val = (String) adapterView.getItemAtPosition(position);
                    // mCustomAdapter.notifyDataSetChanged();
                    CategoryInfo categoryInfo = categoryList.get(position);
                    PervacioTest.getInstance().setSelectedCategory(categoryInfo.getName());
                    //PervacioTest.getInstance().initializeApps();
                    if (GlobalConfig.getInstance().getAutoTestList(categoryInfo.getName()).contains(new TestInfo(PDConstants.GPSCOMPREHENSIVETEST, PDConstants.GPSCOMPREHENSIVETEST)) && hasGPSPermission() && !DeviceInfo.getInstance(getContext()).isGPSEnabled()) {
                        gpsEnableDialog();
                    } else {
                        GlobalConfig globalConfig = PervacioTest.getInstance().getGlobalConfig();
                        ArrayList<TestInfo> testList = globalConfig.getAutoTestList(PervacioTest.getInstance().getSelectedCategory());
                        if (testList != null && testList.size() > 0) {
                            Intent intent = new Intent(getActivity(), AutoTestActivity.class);
                            startActivity(intent);
                        } else {
                            PervacioTest.getInstance().startSession();
                            if (globalConfig.getManualTestList(PervacioTest.getInstance().getSelectedCategory()).size() > 0) {
                                Intent manualTestSelection = new Intent(getActivity(), ManualTestsActivity.class);
                                manualTestSelection.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(manualTestSelection);
                            } else {
                                ResultsActivity.start(getActivity());
                            }
                        }
                        getActivity().finish();
                    }
                }
            });
        }
        return view;
    }

    public void gpsEnableDialog() {
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.gps_connectivity_recommondation))
                .setMessage(getString(R.string.enable_gps))
                .setNegativeButton(getResources().getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(getContext(), AutoTestActivity.class);
                        startActivity(intent);
                    }
                })
                .setPositiveButton(getResources().getString(R.string.str_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new StartLocationAlert(getActivity(), CategoriesFragment.this);
                    }
                }).show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    private boolean hasGPSPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void onLocationConnectionFailed(boolean resultPassFail, String reason) {
        DLog.d(TAG, "result: " + resultPassFail + "   reason: " + reason);
        Intent intent = new Intent(getContext(), AutoTestActivity.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
