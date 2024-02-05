package com.oruphones.nativediagnostic.home;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.Resolution;
import com.oruphones.nativediagnostic.autotests.AutoTestActivity;
import com.oruphones.nativediagnostic.common.CustomViewPager;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.Util;


public class HomeActivity extends BaseActivity {

    private CustomViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private static String TAG = HomeActivity.class.getSimpleName();
    //private TabLayout mTablayout;
    private String mTestName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //Start Tracking
        mInteractionMonitor.setIsEligibleForTimeOut(true,globalConfig.getUserInteractionSessionTimeOut());

        mTestName = getIntent().getStringExtra(BaseActivity.TEST_NAME);
        //viewPager = (CustomViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        String indexString = (getIntent().getStringExtra("param"));
        if (indexString != null && !indexString.equalsIgnoreCase("")) {
            TabsPagerAdapter.currentItem = Integer.parseInt(indexString);
        }
        viewPager.setAdapter(mAdapter);

        viewPager.setOffscreenPageLimit(1);
        /*mTablayout = (TabLayout) findViewById(R.id.tab_layout);
        mTablayout.setupWithViewPager(viewPager);
        mTablayout.setTabMode(TabLayout.MODE_FIXED);
        mTablayout.setSelectedTabIndicatorHeight(3);
        updateTabs(0);
        mTablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                int position = tab.getPosition();
                TabsPagerAdapter.currentItem = position;
                updateTabs(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    setIconToTab(1,R.drawable.test_deselected);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });*/




      /*  viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                //  actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });*/
        viewPager.setSwipeEnabled(true);
        /*if (!BaseActivity.isAssistedApp) {
            viewPager.setSwipeEnabled(true);
        } else {
            mTablayout.clearOnTabSelectedListeners();
            viewPager.setSwipeEnabled(false);
            if ("1".equalsIgnoreCase(mTestName)) {
                updateTabs(1);
            } else if("2".equalsIgnoreCase(mTestName)) {
                updateTabs(2);
            }
            viewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }*/

        viewPager.setCurrentItem(0);
        try {
            int v = getPackageManager().getPackageInfo("com.google.android.gms", 0).versionCode;
            String vName = getPackageManager().getPackageInfo("com.google.android.gms", 0).versionName;
            DLog.d(TAG, "Play Service Versoncode " + v + " Play Service Versoin Name " + vName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*private void setIconToTab(int position, @DrawableRes int resId){
        TabLayout.Tab tab = mTablayout.getTabAt(position);
        if(tab!=null){
            tab.setIcon(resId);
        }
    }
    private void updateTabs(int position){
        setIconToTab(0, position==0?R.drawable.test_selected:R.drawable.test_deselected);
        setIconToTab(1, position==1?R.drawable.categories_selected:R.drawable.categories_deselected);
        setIconToTab(2, position==2?R.drawable.history_selected:R.drawable.history_deselected);
    }*/



    @Override
    protected String getToolBarName() {
        return getResources().getString(R.string.mobile_diagnostics);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Clear all previous results before performing new transaction
        pervacioTest.getAutoTestResult().clear();
        pervacioTest.getManualTestResult().clear();
        pervacioTest.getTestResult().clear();

        Resolution resolution = Resolution.getInstance();
        resolution.clearResolutionsDataForApps();
        //Resetting the RAN number to avoid stale RAN Number being sent to server.
        Util.ranNumber = null;
    }
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_dummy;
    }


    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    public void onBackPressed() {
        if(isAssistedApp)
            return;
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DLog.d(TAG, "requestCode: " + requestCode + "   resultCode: " + resultCode);
        Intent intent = new Intent(this, AutoTestActivity.class);
        startActivity(intent);
       /* if (!isFinishing() && requestCode == 111 && (resultCode == RESULT_CANCELED || resultCode == RESULT_OK)) {
            Intent intent = new Intent(this, AutoTestActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }*/
    }



}

