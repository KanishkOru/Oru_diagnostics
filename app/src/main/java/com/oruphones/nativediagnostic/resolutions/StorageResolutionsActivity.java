package com.oruphones.nativediagnostic.resolutions;



import static com.oruphones.nativediagnostic.api.Resolution.EDIT_REQUEST_CODE;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oruphones.nativediagnostic.BaseActivity;
import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.api.FileInfo;
import com.oruphones.nativediagnostic.api.Resolution;
import com.oruphones.nativediagnostic.communication.CommandServer;
import com.oruphones.nativediagnostic.communication.api.PDStorageFileInfo;
import com.oruphones.nativediagnostic.controller.ShowCustomFilePreview;
import com.oruphones.nativediagnostic.controller.callbacks.OnItemSelectListener;
import com.oruphones.nativediagnostic.controller.resolutions.StorageResolutionsNewAdapter;
import com.oruphones.nativediagnostic.models.tests.ResolutionName;
import com.oruphones.nativediagnostic.util.CommonUtil;
import com.oruphones.nativediagnostic.util.CustomComparator;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Surya Polasanapali  on 17-10-2017.
 */
public class StorageResolutionsActivity extends BaseActivity implements OnItemSelectListener<FileInfo>, View.OnClickListener {

    private RecyclerView mRecyclerView = null;
    private StorageResolutionsNewAdapter storageResolutionsAdapter;
    private ArrayList<FileInfo> fileList = new ArrayList<>();
    private static String TAG = StorageResolutionsActivity.class.getSimpleName();

    private ShowCustomFilePreview showPreview = null;
    private ImageView sortByName, sortByDate, sortBySize;
    private LinearLayout sortByNameContainer, sortByDateContainer, sortBySizeContainer;

    private TextView mCancel, actionDelete, mDate;
    private CheckBox checkAll = null;
    private String resolution_type;
    private String fileType = "duplicate";
    private Button duplicateBtn, largeBtn;
    private TextView toolbarText;


    private HashMap<String, FileInfo> appInfoHashMap = new HashMap<>();
    private List<FileInfo> filesToBeDeleted;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            String result = bundle.getString("result");
            String message = bundle.getString("message");
            String cmdName = bundle.getString("cmdName");
            selectAllFiles(false);
            if (Resolution.RESULT_OPTIMIZED.equalsIgnoreCase(result)) {
                String[] deletedKeys = TextUtils.split(message, ",");
                if (isAssistedApp && !TextUtils.isEmpty(message)) {
                    sendResult(deletedKeys);
                }
                if (deletedKeys.length <= 1)
                    Toast.makeText(StorageResolutionsActivity.this, deletedKeys.length + " " + getString(R.string.file_deleted), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(StorageResolutionsActivity.this, deletedKeys.length + " " + getString(R.string.files_are_deleted), Toast.LENGTH_LONG).show();
            } else {
                if ("CMD_DELETE_FILES".equals(cmdName)) {

                    try {
                        if (!TextUtils.isEmpty(result)) {
                            JSONArray jsonArray = new JSONArray(result);
                            List<FileInfo> selectedFilesList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                selectedFilesList.add(appInfoHashMap.get(jsonArray.getString(i)));
                            }
                            updateSelectedFiles(selectedFilesList);
                            if (isAssistedApp)
                                actionDelete.performClick();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            checkAll.setChecked(false);
            if (getFileList().size() == 0) {
                if (!isAssistedApp) {
                    finish();
                }
                updateList(getFileList());

            } else {
                updateList(getFileList());
            }
        }
    };


    private void sortViews() {
        sortByName = findViewById(R.id.sortByName);
        sortByDate = findViewById(R.id.sortByDate);
        sortBySize = findViewById(R.id.sortBySize);
        sortBySize.setOnClickListener(this);

        sortByNameContainer = findViewById(R.id.sortByNameContainer);
        sortByNameContainer.setOnClickListener(this);

        sortByDateContainer = findViewById(R.id.sortByDateContainer);
        sortByDateContainer.setOnClickListener(this);

        sortBySizeContainer = findViewById(R.id.sortBySizeContainer);
        sortBySizeContainer.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resolution_type = getIntent().getStringExtra(TEST_NAME);
        mCancel = (TextView) findViewById(R.id.cancel_tv);
        initPreviewView();
        // mCancel.setVisibility(View.GONE);
        actionDelete = (TextView) findViewById(R.id.accept_tv);
        actionDelete.setText(getString(R.string.str_delete));
        mCancel.setText(getString(R.string.action_cancel));
        setFontToView(actionDelete, OPENSANS_MEDIUM);
        setFontToView(mCancel, OPENSANS_MEDIUM);
        sortViews();

        mDate = (TextView) findViewById(R.id.id_header_date);
        if (isAssistedApp) {
            mDate.setVisibility(View.INVISIBLE);
        }

        toolbarText = findViewById(R.id.toolbar_title);
        toolbarText.setText(getString(R.string.str_storage_resolutions));
        duplicateBtn = findViewById(R.id.id_header_duplicate_files);
        largeBtn = findViewById(R.id.id_header_large_files);
        duplicateBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
        duplicateBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.oru_color)));
        duplicateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTabs("duplicate");
            }
        });

        largeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTabs("large");
            }
        });

        checkAll = (CheckBox) findViewById(R.id.checkAll);
        if (isAssistedApp) {
            checkAll.setVisibility(View.INVISIBLE);
            sortByName.setEnabled(false);
        }
        checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllFiles(checkAll.isChecked());
            }
        });
        actionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setUpRecyclerView();
    }

    private void handleTabs(String type) {
        fileType = type;
        if (type.equalsIgnoreCase("duplicate")) {
            duplicateBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            duplicateBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.oru_color)));
            largeBtn.setTextColor(getResources().getColor(R.color.light_black_titles));
            largeBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            updateList(getFileList());
        } else if (type.equalsIgnoreCase("large")) {
//            duplicateBtn.setTextColor(getResources().getColor(R.color.light_black_titles));
//            largeBtn.setTextColor(getResources().getColor(R.color.oru_color));
            largeBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            largeBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.oru_color)));
            duplicateBtn.setTextColor(getResources().getColor(R.color.light_black_titles));
            duplicateBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            updateList(getFileList());
        }
    }


    //Recycler View
//Selected Files
    private void selectAllFiles(boolean isSelectAll) {
        if (storageResolutionsAdapter == null)
            return;

        if (isSelectAll) {
            updateSelectedFiles(fileList);
        } else {
            storageResolutionsAdapter.clearSelected();
        }
    }

    private void updateSelectedFiles(List<FileInfo> selected) {
        if (storageResolutionsAdapter == null)
            return;
        storageResolutionsAdapter.setSelectedFilesList(selected);
    }

    private void delete() {
        releasePreviewResources();
        if (storageResolutionsAdapter.getSelectedFilesList().size() == 0) {
            Toast.makeText(StorageResolutionsActivity.this, getString(R.string.selet_one_to_continue), Toast.LENGTH_LONG).show();
        } else {
            showConfirmationDialog(storageResolutionsAdapter.getSelectedFilesList());
        }
    }

    //RecyclerView
    private void setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.storageRv);
        storageResolutionsAdapter = new StorageResolutionsNewAdapter(this, fileList, checkAll);
        storageResolutionsAdapter.clearSelected();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(storageResolutionsAdapter);
        updateList(getFileList());
        sortByName(sortByName, CustomComparator.SortBy.NAME);
        initFirstTime();
    }

    private void updateList(List<FileInfo> list) {
        if (storageResolutionsAdapter == null || mRecyclerView == null)
            return;
        fileList.clear();
        fileList.addAll(list);
        storageResolutionsAdapter.notifyDataSetChanged();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!isAssistedApp) {
            storageResolutionsAdapter.clearSelected();
        }
        CommandServer.getInstance(getApplicationContext()).setUIHandler(handler);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_storage_resolutions_ssd;
    }

    @Override
    protected String getToolBarName() {
        return getTitle(getIntent().getStringExtra(TEST_NAME));
    }

    @Override
    protected boolean setBackButton() {
        return true;
    }

    private ArrayList<FileInfo> getFileList() {
        ArrayList<FileInfo> fileInfos = null;
        resolution_type = getIntent().getStringExtra(TEST_NAME);
        fileInfos = getFileInfosByType();
        if (ResolutionName.IMAGES.equalsIgnoreCase(resolution_type)) {
            fileInfos = Resolution.getInstance().getImageFileList();
        } else if (ResolutionName.MUSIC.equalsIgnoreCase(resolution_type)) {
            fileInfos = Resolution.getInstance().getAudioFileList();
        } else if (ResolutionName.VIDEO.equalsIgnoreCase(resolution_type)) {
            fileInfos = Resolution.getInstance().getVideoFileList();
        } else if (ResolutionName.DUPLICATE.equalsIgnoreCase(resolution_type)) {
            fileInfos = Resolution.getInstance().getDuplicateFileList();
        } else {
            if (fileType.equalsIgnoreCase("duplicate")) {
                fileInfos.addAll(Resolution.getInstance().getDuplicateFileList());
            } else if (fileType.equalsIgnoreCase("large")) {
                fileInfos.addAll(Resolution.getInstance().getLargeFileList());
            }
//            add all above file types
//            fileInfos = Resolution.getInstance().getImageFileList();
//            fileInfos.addAll(Resolution.getInstance().getAudioFileList());
//            fileInfos.addAll(Resolution.getInstance().getVideoFileList());

        }
        DLog.d(TAG, "getFileList: " + fileInfos.size() + " " + resolution_type);
        Collections.sort(fileInfos, new Comparator<FileInfo>() {
            @Override
            public int compare(FileInfo fileInfo, FileInfo t1) {
                return fileInfo.getFileName().compareToIgnoreCase(t1.getFileName());
            }
        });
        return fileInfos;
    }

    private String getTitle(String testName) {
        HashMap<String, String> titleMap = new HashMap<>();
        titleMap.put(ResolutionName.DUPLICATE, getString(R.string.title_duplicatefiles));
        titleMap.put(ResolutionName.IMAGES, getString(R.string.title_images));
        titleMap.put(ResolutionName.MUSIC, getString(R.string.title_music));
        titleMap.put(ResolutionName.VIDEO, getString(R.string.title_videos));
        return titleMap.get(testName);
    }

    private ArrayList<FileInfo> getFileInfosByType() {
        ArrayList<FileInfo> fileInfos = new ArrayList<>();
        ArrayList<PDStorageFileInfo> pdStorageFileInfos = Resolution.getInstance().getStorageFileInfoList();
        for (PDStorageFileInfo storageFileInfo : pdStorageFileInfos) {
            /*if(!isFileExists(storageFileInfo.getFilePath())) {
                Log.d("CommandServer","CommandServer:+"+storageFileInfo.getKey()+"Name:::"+storageFileInfo.getName());
                continue;
            }*/
            FileInfo fileInfo = null;
            if (resolution_type.equalsIgnoreCase(storageFileInfo.getFileType())) {
                fileInfo = getFileInfoFromPDStorageFileInfo(storageFileInfo);
                fileInfos.add(fileInfo);
                appInfoHashMap.put(storageFileInfo.getKey(), fileInfo);
            } else if (resolution_type.equalsIgnoreCase(ResolutionName.DUPLICATE) && storageFileInfo.isDuplicate()) {
                fileInfo = getFileInfoFromPDStorageFileInfo(storageFileInfo);
                fileInfos.add(fileInfo);
                appInfoHashMap.put(storageFileInfo.getKey(), fileInfo);
            }
            //appInfoHashMap.put(storageFileInfo.getKey(),fileInfo);
        }

        return fileInfos;
    }

    private FileInfo getFileInfoFromPDStorageFileInfo(PDStorageFileInfo pdStorageFileInfo) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setCreatedDate(pdStorageFileInfo.getCreatedDate() + "");
        fileInfo.setFileName(pdStorageFileInfo.getName());
        fileInfo.setFilePath(pdStorageFileInfo.getFilePath());
        fileInfo.setFileSize(pdStorageFileInfo.getSize() + "");
        fileInfo.setFileType(pdStorageFileInfo.getFileType());
        fileInfo.setKey(pdStorageFileInfo.getKey());
        return fileInfo;
    }

    private void sendResult(String[] deletedKeys) {
        if (deletedKeys.length > 0) {
            CommandServer.getInstance(this).postEventData("DELETED_FILES", deletedKeys);
        }
        if (storageResolutionsAdapter != null) {
            storageResolutionsAdapter.clearSelected();
        }
    }

    private void showConfirmationDialog(final List<FileInfo> listOfFiles) {
        this.filesToBeDeleted = listOfFiles;
        CommonUtil.DialogUtil.getAlert(this, getString(R.string.alert), getString(R.string.delete_alert_msg), getResources().getString(R.string.str_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Resolution.getInstance().performStorageResolution(StorageResolutionsActivity.this, StorageResolutionsActivity.this, resolution_type, listOfFiles, handler);
            }
        }, getResources().getString(R.string.str_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendResult(new String[1]);
            }
        }).show();
    }
    //show File Preview

    private void initFirstTime() {
        if (storageResolutionsAdapter != null && storageResolutionsAdapter.getItemCount() > 0) {
            onItemSelect(0, storageResolutionsAdapter.getItem(0));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePreviewResources();
    }

    private void releasePreviewResources() {
        if (showPreview != null) {
            showPreview.releaseResources();
            showPreview.setVisibility(View.GONE);
        }
    }

    private void initPreviewView() {
        showPreview = findViewById(R.id.showPreview);
        showPreview.setonCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releasePreviewResources();
            }
        });
    }

    @Override
    public void onItemSelect(int index, FileInfo item) {
        showPreview.setVisibility(View.VISIBLE);
        showPreview.showPreview(item);
    }

    //Name
    private void hideAllSortingArrow() {
        sortByName.setVisibility(View.GONE);
        sortByDate.setVisibility(View.GONE);
        sortBySize.setVisibility(View.GONE);
    }

    private void sortByName(ImageView sortingView, CustomComparator.SortBy sortBy) {
        hideAllSortingArrow();
        boolean orderIsAscending = true;
        if (sortingView.getTag() != null) {
            orderIsAscending = !(boolean) sortingView.getTag();
        }
        sortingView.setImageResource(orderIsAscending ? R.drawable.ic_dropdown : R.drawable.ic_dropup);
        sortingView.setTag(orderIsAscending);
        sortingView.setVisibility(View.VISIBLE);

        Collections.sort(fileList, new CustomComparator(orderIsAscending, sortBy));
        storageResolutionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.sortByNameContainer) {
            sortByName(sortByName, CustomComparator.SortBy.NAME);
        } else if (viewId == R.id.sortByDateContainer) {
            sortByName(sortByDate, CustomComparator.SortBy.DATE);
        } else if (viewId == R.id.sortBySizeContainer) {
            sortByName(sortBySize, CustomComparator.SortBy.SIZE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                /* Edit request granted; proceed. */
//                Log.d(TAG,"enter onActivityResult if case EDIT_REQUEST_CODE "+EDIT_REQUEST_CODE);
                Resolution.getInstance().updateStorageResolutionForAndroidR(filesToBeDeleted);
            } else {
                /* Edit request not granted; explain to the user. */
//                Log.d(TAG,"enter onActivityResult else case EDIT_REQUEST_CODE "+EDIT_REQUEST_CODE);
            }
        }
    }

    @Override
    protected boolean exitOnBack() {
        return false;
    }
}
