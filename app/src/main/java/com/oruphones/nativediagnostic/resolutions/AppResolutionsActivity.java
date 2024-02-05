package com.oruphones.nativediagnostic.resolutions;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.AppInfo;
import com.oruphones.nativediagnostic.api.Resolution;
import com.oruphones.nativediagnostic.communication.CommandServer;
import com.oruphones.nativediagnostic.models.tests.ResolutionName;
import com.oruphones.nativediagnostic.util.CustomComparator;
import com.pervacio.batterydiaglib.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by PERVACIO on 17-10-2017.
 */
public class AppResolutionsActivity extends BaseActivity {

    private ExpandableListView appsListView = null;
    private TextView mCancel, mUninstall, mDate;
    private CheckBox checkAllApps = null;
    private ExpandableListAdapter expandablelistAdpter;
    private String mCurrentResoltion;
    private ImageView sortByName;
    private ArrayList<AppInfo> appInfoArrayList;
    private boolean orderIsAscending = true;
    private HashMap<String, String> titleMap = null;
    List<String> deletedApps = new ArrayList<>();
    private boolean sendData = false;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String result = bundle.getString("result");
            String packageName = bundle.getString("message");
            if (Resolution.FOREGROUND_APPS.equalsIgnoreCase(mCurrentResoltion)
                    || Resolution.BACKGROUND_APPS.equalsIgnoreCase(mCurrentResoltion)
                    || Resolution.AUTOSTART_APPS.equalsIgnoreCase(mCurrentResoltion)) {
                ExpandableListAdapter.selectedAppslist.clear();
                if (Resolution.RESULT_OPTIMIZED.equalsIgnoreCase(result))
                    Toast.makeText(AppResolutionsActivity.this, getString(R.string.kill_app), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(AppResolutionsActivity.this, getString(R.string.unable_to_kill), Toast.LENGTH_LONG).show();
            } else {
                String cmdName = bundle.getString("cmdName");
                if("CMD_UNINSTALL_APPS".equals(cmdName)) {
                    ExpandableListAdapter.selectedAppslist.clear();
                    deletedApps.clear();
                    sendData = false;
                    try {
                        if (!TextUtils.isEmpty(result)) {
                            HashMap<String, AppInfo> appInfoHashMap = new HashMap<>();
                            ArrayList<AppInfo> appInfos = getAppsList();
                            for (AppInfo info : appInfos) {
                                appInfoHashMap.put(info.getPackageName(), info);
                            }
                            JSONArray jsonArray = new JSONArray(result);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ExpandableListAdapter.selectedAppslist.add(appInfoHashMap.get(jsonArray.getString(i)));
                            }
                            if(isAssistedApp)
                                mUninstall.performClick();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (!TextUtils.isEmpty(packageName) && !deletedApps.contains(packageName)) {
                    deletedApps.add(packageName);
//                    if(ExpandableListAdapter.selectedAppslist.size() == 1 || sendData) {
//                        sendResult();
//                    }
                }
            }
            checkAllApps.setChecked(false);
            if (!isAssistedApp && getAppsList().size() == 0) {
                finish();
            } else {
                expandablelistAdpter = new ExpandableListAdapter(AppResolutionsActivity.this, getAppsList(), checkAllApps);
                appsListView.setAdapter(expandablelistAdpter);
                expandablelistAdpter.notifyDataSetChanged();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentResoltion = getIntent().getStringExtra(TEST_NAME);
        appsListView = (ExpandableListView) findViewById(R.id.riskyListView);
        mCancel = (TextView) findViewById(R.id.cancel_tv);
       // mCancel.setVisibility(View.GONE);
        mUninstall = (TextView) findViewById(R.id.accept_tv);
        mCancel.setText(getString(R.string.action_cancel));
        setFontToView(mUninstall,OPENSANS_MEDIUM);
        setFontToView(mCancel,OPENSANS_MEDIUM);
        sortByName = (ImageView) findViewById(R.id.sortByName);
        sortByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderIsAscending = !orderIsAscending;
                if (orderIsAscending)
                    sortByName.setImageResource(R.drawable.ic_dropdown);
                else
                    sortByName.setImageResource(R.drawable.ic_dropup);
                Collections.sort(appInfoArrayList, new CustomComparator(orderIsAscending));
                expandablelistAdpter = new ExpandableListAdapter(AppResolutionsActivity.this, appInfoArrayList, checkAllApps);
                appsListView.setAdapter(expandablelistAdpter);
                expandablelistAdpter.notifyDataSetChanged();
            }
        });
        mUninstall.setText(getString(R.string.str_uninstall));
        if (mCurrentResoltion.equalsIgnoreCase(ResolutionName.AUTOSTART_APPS) || mCurrentResoltion.equalsIgnoreCase(ResolutionName.FOREGROUND_APPS) || mCurrentResoltion.equalsIgnoreCase(ResolutionName.BACKGROUND_APPS)) {
            mUninstall.setText(getString(R.string.str_stop));
        }
        checkAllApps = (CheckBox) findViewById(R.id.checkAll);
        mDate = (TextView) findViewById(R.id.id_header_date);
        ExpandableListAdapter.selectedAppslist.clear();
        if(isAssistedApp) {
            checkAllApps.setEnabled(false);
            sortByName.setEnabled(false);
            checkAllApps.setVisibility(View.INVISIBLE);
            mDate.setVisibility(View.INVISIBLE);
        }
        checkAllApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllApps(checkAllApps.isChecked());
            }
        });
        mUninstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ExpandableListAdapter.selectedAppslist.size() == 0) {
                    Toast.makeText(AppResolutionsActivity.this, getString(R.string.selet_one_to_continue), Toast.LENGTH_SHORT).show();
                } else {
                    Resolution.getInstance().performAppResolution(mCurrentResoltion, ExpandableListAdapter.selectedAppslist, handler);
                }
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        appInfoArrayList = getAppsList();
        Collections.sort(appInfoArrayList, new CustomComparator(true));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!isAssistedApp) {
            ExpandableListAdapter.selectedAppslist.clear();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (deletedApps.size() > 0) {
                    sendResult();
//                    sendData = true;
                } else if (ExpandableListAdapter.selectedAppslist.size() > 0 && deletedApps.size() == 0) {
                    sendResult();
                }
            }
        }, 2000)*/;

        CommandServer.getInstance(getApplicationContext()).setUIHandler(handler);
        expandablelistAdpter = new ExpandableListAdapter(this, getAppsList(), checkAllApps);
        appsListView.setAdapter(expandablelistAdpter);
        expandablelistAdpter.notifyDataSetChanged();
    }


    private ArrayList<AppInfo> getAppsList() {
        ArrayList<AppInfo> appInfos = null;
        mCurrentResoltion = getIntent().getStringExtra(TEST_NAME);
        switch (mCurrentResoltion) {
            case ResolutionName.AUTOSTART_APPS:
                appInfos = Resolution.getInstance().getAutostartAppList();
                break;
            case ResolutionName.BACKGROUND_APPS:
                appInfos = Resolution.getInstance().getBackgroundAppList();
                break;
            case ResolutionName.FOREGROUND_APPS:
                appInfos = Resolution.getInstance().getForegroundAppList();
                break;
            case ResolutionName.RISKYAPPS:
                appInfos = Resolution.getInstance().getRiskyAppsList();
                break;
            case ResolutionName.MALWAREAPPS:
                appInfos = Resolution.getInstance().getMalwareAppsList();
                break;
            case ResolutionName.ADWAREAPPS:
                appInfos = Resolution.getInstance().getAddwareAppsList();
                break;
            case ResolutionName.OUTDATEDAPPS:
                appInfos = Resolution.getInstance().getOutdatedAppsList();
                break;
            case ResolutionName.UNUSEDAPPS:
                appInfos=Resolution.getInstance().getUnusedAppsList();
                break;
            default:
                break;
        }

        Collections.sort(appInfos, new Comparator<AppInfo>() {

            @Override
            public int compare(AppInfo appInfo, AppInfo t1) {
                return appInfo.getAppName().compareToIgnoreCase(t1.getAppName());
            }
        });
        if(appInfos!=null){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("testName : ").append(mCurrentResoltion).append("\n");
            for(AppInfo appInfo :appInfos){
                stringBuilder.append("app : ").append(appInfo.getAppName()).append(" : ").append(appInfo.getPackageName());
            }
            LogUtil.debug(stringBuilder.toString());
        }


        return appInfos;
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_app_resolutions;
    }

    @Override
    protected String getToolBarName() {
        return getTitle(getIntent().getStringExtra(TEST_NAME));
    }

    @Override
    protected boolean setBackButton() {
        return true;
    }

    @Override
    protected boolean exitOnBack() {
        return false;
    }

    private void selectAllApps(boolean isSelectAll) {
        ExpandableListAdapter.selectedAppslist.clear();
        if (isSelectAll)
            ExpandableListAdapter.selectedAppslist.addAll(getAppsList());
        expandablelistAdpter = new ExpandableListAdapter(this, getAppsList(), checkAllApps);
        appsListView.setAdapter(expandablelistAdpter);
        expandablelistAdpter.notifyDataSetChanged();
    }

    private String getTitle(String testName) {
        if (titleMap == null) {
            titleMap = new HashMap<>();
            titleMap.put(ResolutionName.AUTOSTART_APPS, getString(R.string.title_autostartapps));
            titleMap.put(ResolutionName.FOREGROUND_APPS, getString(R.string.title_runningapps));
            titleMap.put(ResolutionName.BACKGROUND_APPS, getString(R.string.title_backgroundapps));
            titleMap.put(ResolutionName.RISKYAPPS, getString(R.string.title_riskyapps));
            titleMap.put(ResolutionName.MALWAREAPPS, getString(R.string.title_malwareapps));
            titleMap.put(ResolutionName.ADWAREAPPS, getString(R.string.title_adwareapps));
            titleMap.put(ResolutionName.OUTDATEDAPPS, getString(R.string.title_outdatedapps));
            titleMap.put(ResolutionName.UNUSEDAPPS, getString(R.string.title_unusedapps));
        }
        return titleMap.get(testName);
    }

    private void sendResult() {
        ArrayList result = new ArrayList();
        result.addAll(deletedApps);
        CommandServer.getInstance(this).postEventData("DELETED_APPS", result);
        deletedApps.clear();
        ExpandableListAdapter.selectedAppslist.clear();
        expandablelistAdpter.notifyDataSetChanged();
    }
}
