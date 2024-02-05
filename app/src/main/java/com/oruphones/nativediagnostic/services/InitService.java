package com.oruphones.nativediagnostic.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.oruphones.nativediagnostic.api.FileInfo;
import com.oruphones.nativediagnostic.api.PervacioTest;
import com.oruphones.nativediagnostic.api.Resolution;
import com.oruphones.nativediagnostic.communication.api.PDStorageFileInfo;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;
import com.oruphones.nativediagnostic.util.Util;

import org.pervacio.onediaglib.diagtests.TestAppStorageResolutions;
import org.pervacio.onediaglib.diagtests.TestListener;
import org.pervacio.onediaglib.diagtests.TestResult;
import org.pervacio.onediaglib.internalstorage.FileData;
import org.pervacio.onediaglib.internalstorage.FileVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pervacio on 6-9-2017.
 */
public class InitService extends IntentService implements TestListener {
    private static String TAG = InitService.class.getSimpleName();
    public InitService() {
        super("InitService");
    }

    private ArrayList<String> musicfileExtension = new ArrayList<String>(Arrays.asList("mp3", "flac", "3ga", "zab", "cda", "arf", "wpl", "xspf", "avr", "sesx", "mpdp", "trm", "aa", "gp5", "ocdf", "bnk", "rec", "xwm",
            "mus", "moi", "aax", "ct3", "cs3", "dss", "wem", "mv3", "nwc", "nvf", "wv", "ca3", "ds2", "amr", "sib", "tsi", "xkr", "fsb", "ajp", "dvf", "nmf", "zvr", "m4a", "ram", "adts",
            "wrf", "alb", "wav", "cdfs", "oma", "aaf", "audionote", "sng", "ad4", "dcf", "br5", "fls", "asx", "vdj", "ses", "ytif", "aac", "2ch", "ove", "mka", "2", "wma", "nmsv", "mp4a",
            "elastik", "au", "caf", "br4", "pcm", "mgu", "m4r", "cdg", "vox", "vpl", "nki", "dkd", "mogg", "spx", "bmw", "thd", "i3pack", "voc", "ap4", "muk", "snd", "stem.mp4",
            "sdif", "ogg", "midi", "rpp", "ulaw", "kux", "gp4", "efa", "rns", "uax", "xwb", "kam", "sf2", "mtd", "gtp", "m4p", "kfn", "omf", "gog", "sdx", "tak", "mxl", "pbf", "aud", "svq",
            "rx2", "gpx", "sf", "band", "sgu", "sabs", "gig", "w02", "rip", "ngrr", "m4b", "kar", "mx6", "nsmp", "wax", "asf", "seq", "swa", "dlp", "mx5", "aif", "smf", "vsb", "rtm", "mmp",
            "sps", "sabl", "hma", "xpf", "abk", "ra", "shn", "rms", "logic", "tl", "cdo", "rfl", "vm", "aob", "acd", "adg", "sts", "h2p", "gpbank", "vm1", "dtshd", "cwp", "aiff"));
    private ArrayList<String> imagefileExtension = new ArrayList<String>(Arrays.asList("png", "jpg", "jpeg", "jfif", "jpeg 2000", "exif", "tiff", "gif", "bmp"));
    private ArrayList<String> videofileExtension = new ArrayList<String>(Arrays.asList("avi", "mp4", "dav", "mov", "arf", "mkv", "avc", "exo", "fbr", "dash", "flv", "3gp", "mks", "m4v", "3gpp", "mvc", "ogm", "mpeg4", "mpeg2", "mpeg1"));

    @Override
    protected void onHandleIntent(Intent intent) {
        DLog.d(TAG, "Init service started.........");


        TestAppStorageResolutions appStorageResolutions = TestAppStorageResolutions.getInstance();
        appStorageResolutions.getStorageInfo(true);
        DLog.d(TAG, "storage process started");
        appStorageResolutions.setTestFinishListener(this);
        if(!PervacioTest.getInstance().isOfflineDiagnostics()) {
            submitOfflineDataToServer();
        }

    }
    private void submitOfflineDataToServer() {
         DLog.d(TAG, "sending Offline Data To Server...");
        Intent intent = new Intent(this, OfflineTransactionService.class);
        startService(intent);
    }
    @Override
    public void onTestStart() {

    }

    private Map<Integer, List<FileVO>> duplicateFilesMap = new HashMap<Integer, List<FileVO>>();
    private ArrayList<FileData> fileMusicList = new ArrayList<FileData>();
    private ArrayList<FileData> fileVideoList = new ArrayList<FileData>();
    private ArrayList<FileData> fileImageList = new ArrayList<FileData>();
    private ArrayList<FileData> fileOtherList = new ArrayList<FileData>();

    
    
    
    @Override
    public void onTestEnd(TestResult testResult) {

         DLog.d(TAG,"storage process ended result" + testResult.getResultCode());

        if(testResult.getResultCode() == TestResult.RESULT_PASS) {
            TestAppStorageResolutions appStorageResolutions = TestAppStorageResolutions.getInstance();
            duplicateFilesMap = appStorageResolutions.getDuplicateFilesMap();
             DLog.d(TAG,"storage process ended duplicate" + appStorageResolutions.getDuplicateFilesMap().size());

            fileImageList = appStorageResolutions.getFileImageList();
             DLog.d(TAG,"storage process ended image" + appStorageResolutions.getFileImageList().size());

            fileMusicList = appStorageResolutions.getFileMusicList();
             DLog.d(TAG,"storage process ended music" + appStorageResolutions.getFileMusicList().size());

            fileOtherList = appStorageResolutions.getFileOtherList();
             DLog.d(TAG,"storage process ended other" + appStorageResolutions.getFileOtherList().size());

            fileVideoList = appStorageResolutions.getFileVideoList();
             DLog.d(TAG,"storage process ended video" + appStorageResolutions.getFileVideoList().size());

            duplicateFilesMap = appStorageResolutions.getDuplicateFilesMap();

            saveDataToFile(getApplicationContext());
            Resolution.getInstance().setFileResolutionDone(true);
        }

    }

    public void saveDataToFile(Context context) {
         DLog.d(TAG, "saveDataToFile...................");

        try {
            ArrayList<FileInfo> imageFileList = new ArrayList<FileInfo>();
            ArrayList<FileInfo> audioFileList = new ArrayList<FileInfo>();
            ArrayList<FileInfo> videoFileList = new ArrayList<FileInfo>();
            ArrayList<FileInfo> duplicateFileList = new ArrayList<FileInfo>();
            ArrayList<FileInfo> largeFileList = new ArrayList<FileInfo>();
            ArrayList<PDStorageFileInfo> pdStorageFileInfoList = new ArrayList<PDStorageFileInfo>();
            ArrayList<String> duplicateFilePathList = new ArrayList<String>();
            for (Integer in : this.duplicateFilesMap.keySet()) {
                long size = 0;
                int totalduplicateFiles = 0;
                int cureentduplicateFile = 0;
                List<FileVO> fileVOs = this.duplicateFilesMap.get(in);
                totalduplicateFiles = fileVOs.size() - 1;
                for (FileVO fileVO : fileVOs) {


                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setFileName(fileVO.getDisplayName());
                    fileInfo.setFilePath(fileVO.getFilePath());
                    fileInfo.setFileSize("" + Util.BtoKB(fileVO.getLength()));
                    fileInfo.setCreatedDate("" + fileVO.getLastModifiedTime());
//                    if file size is greater than 500 MB then add to large file list
//                    if(fileVO.getLength() > 500000000) {
//                        largeFileList.add(fileInfo);
//                    }


                    String[] filePath = fileVO.getFilePath().split("/");
                    String fileNames = filePath[filePath.length - 1];
                    String fileExt = getFileExtension(fileNames).toLowerCase();
                    if (musicfileExtension.indexOf(fileExt) != -1) {
                        fileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_AUDIO);
                        duplicateFilePathList.add(fileVO.getFilePath());
                        duplicateFileList.add(fileInfo);
                    }
                    if (imagefileExtension.indexOf(fileExt) != -1) {
                        fileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_IMAGE);
                        duplicateFilePathList.add(fileVO.getFilePath());
                        duplicateFileList.add(fileInfo);
                    }
                    if (videofileExtension.indexOf(fileExt) != -1) {
                        fileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_VIDEO);
                        duplicateFilePathList.add(fileVO.getFilePath());
                        duplicateFileList.add(fileInfo);
                    }
                }
                Resolution.getInstance().setDuplicateFileList(duplicateFileList);

            }

             DLog.d(TAG, "Apps are added");
            int index = 0;
            for (FileData fileData : fileImageList) {
                PDStorageFileInfo pdStorageFileInfo = new PDStorageFileInfo();
                pdStorageFileInfo.setKey("image"+index++);
                String fileName = utf8(fileData.getFileName());
                pdStorageFileInfo.setName(fileName);
                pdStorageFileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_IMAGE);
                String filePath = utf8(fileData.getFilePath());
                pdStorageFileInfo.setFilePath(filePath);
                pdStorageFileInfo.setSize(Util.BtoKB(fileData.getFileSizeInBytes()).longValue());
                pdStorageFileInfo.setCreatedDate(fileData.getLastModifiedTime());
                if (duplicateFilePathList != null && duplicateFilePathList.contains(pdStorageFileInfo.getFilePath())) {
                    pdStorageFileInfo.setDuplicate(true);
                } else {
                    pdStorageFileInfo.setDuplicate(false);
                }
                pdStorageFileInfoList.add(pdStorageFileInfo);
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_IMAGE);
                fileInfo.setFileName(fileData.getFileName());
                fileInfo.setFilePath(fileData.getFilePath());
                fileInfo.setFileSize("" + Util.BtoKB(fileData.getFileSizeInBytes()));
                fileInfo.setCreatedDate("" + fileData.getLastModifiedTime());
                imageFileList.add(fileInfo);
                //  if file size is greater than 500 MB then add to large file list
                if(fileData.getFileSizeInBytes() > 500000000) {
                    largeFileList.add(fileInfo);
                }
            }
            Resolution.getInstance().setImageFileList(imageFileList);
            index = 0;
            for (FileData fileData : fileMusicList) {
                PDStorageFileInfo pdStorageFileInfo = new PDStorageFileInfo();
                pdStorageFileInfo.setKey("audio"+index++);
                String fileName = utf8(fileData.getFileName());
                pdStorageFileInfo.setName(fileName);
                pdStorageFileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_AUDIO);
                String path = utf8(fileData.getFilePath());
                pdStorageFileInfo.setFilePath(path);
                pdStorageFileInfo.setSize(Util.BtoKB(fileData.getFileSizeInBytes()).longValue());
                pdStorageFileInfo.setCreatedDate(fileData.getLastModifiedTime());
                if (duplicateFilePathList != null && duplicateFilePathList.contains(pdStorageFileInfo.getFilePath())) {
                    pdStorageFileInfo.setDuplicate(true);
                } else {
                    pdStorageFileInfo.setDuplicate(false);
                }
                pdStorageFileInfoList.add(pdStorageFileInfo);

                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_AUDIO);
                fileInfo.setFileName(fileData.getFileName());
                fileInfo.setFilePath(fileData.getFilePath());
                fileInfo.setFileSize("" + Util.BtoKB(fileData.getFileSizeInBytes()));
                fileInfo.setCreatedDate("" + fileData.getLastModifiedTime());
                audioFileList.add(fileInfo);
                //  if file size is greater than 500 MB then add to large file list
                if(fileData.getFileSizeInBytes() > 500000000) {
                    largeFileList.add(fileInfo);
                }
            }
            Resolution.getInstance().setAudioFileList(audioFileList);
            index = 0;
            for (FileData fileData : fileVideoList) {
                PDStorageFileInfo pdStorageFileInfo = new PDStorageFileInfo();
                pdStorageFileInfo.setKey("video"+index++);
                String fileName = utf8(fileData.getFileName());
                pdStorageFileInfo.setName(fileName);
                pdStorageFileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_VIDEO);
                String filePath = utf8(fileData.getFilePath());
                pdStorageFileInfo.setFilePath(filePath);
                pdStorageFileInfo.setSize(Util.BtoKB(fileData.getFileSizeInBytes()).longValue());
                pdStorageFileInfo.setCreatedDate(fileData.getLastModifiedTime());
                if (duplicateFilePathList != null && duplicateFilePathList.contains(pdStorageFileInfo.getFilePath())) {
                    pdStorageFileInfo.setDuplicate(true);
                } else {
                    pdStorageFileInfo.setDuplicate(false);
                }
                pdStorageFileInfoList.add(pdStorageFileInfo);

                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_VIDEO);
                fileInfo.setFileName(fileData.getFileName());
                fileInfo.setFilePath(fileData.getFilePath());
                fileInfo.setFileSize("" + Util.BtoKB(fileData.getFileSizeInBytes()));
                fileInfo.setCreatedDate("" + fileData.getLastModifiedTime());
                videoFileList.add(fileInfo);
                //  if file size is greater than 500 MB then add to large file list
                if(fileData.getFileSizeInBytes() > 500000000) {
                    largeFileList.add(fileInfo);
                }
            }
            Resolution.getInstance().setVideoFileList(videoFileList);
            index = 0;
            for (FileData fileData : fileOtherList) {
                PDStorageFileInfo pdStorageFileInfo = new PDStorageFileInfo();
                pdStorageFileInfo.setKey("others"+index++);
                String fileName = utf8(fileData.getFileName());
                pdStorageFileInfo.setName(fileName);
                pdStorageFileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_OTHER);
                String filePath = utf8(fileData.getFilePath());
                pdStorageFileInfo.setFilePath(filePath);
                pdStorageFileInfo.setSize(Util.BtoKB(fileData.getFileSizeInBytes()).longValue());
                pdStorageFileInfo.setCreatedDate(fileData.getLastModifiedTime());
                if(duplicateFilePathList != null && duplicateFilePathList.contains(pdStorageFileInfo.getFilePath()))
                    pdStorageFileInfo.setDuplicate(true);
                else
                    pdStorageFileInfo.setDuplicate(false);
                pdStorageFileInfoList.add(pdStorageFileInfo);
                //  if file size is greater than 500 MB then add to large file list
                if(fileData.getFileSizeInBytes() > 500000000) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setFileType(PDStorageFileInfo.FILE_TYPE_OTHER);
                    fileInfo.setFileName(fileData.getFileName());
                    fileInfo.setFilePath(fileData.getFilePath());
                    fileInfo.setFileSize("" + Util.BtoKB(fileData.getFileSizeInBytes()));
                    fileInfo.setCreatedDate("" + fileData.getLastModifiedTime());
                    largeFileList.add(fileInfo);
                }
            }
            Resolution.getInstance().setLargeFileList(largeFileList);
            Resolution.getInstance().setStorageFileInfoList(pdStorageFileInfoList);
            if(pdStorageFileInfoList != null) {
                 DLog.d(TAG, "***************DONE**************++" + pdStorageFileInfoList.size());
            }
            else {
                 DLog.d(TAG, "***************DONE**************" + "pdStorageFileInfoList is null");
            }

        } catch (Exception e) {
             DLog.e(TAG, "DuplicateFileHandler, IOException : " + e.getMessage());
             DLog.e(TAG, "&EventName=StorageDetails&Status=Fail");
            e.printStackTrace();
        }
        Resolution.getInstance().setFileResolutionDone(true);
    }
    private String utf8(String in) {
        if (in == null)
            return "";
        in = in.replaceAll("&#x26;", "&");
        in = in.replaceAll("&#x60;", "<");
        in = in.replaceAll("&#x62;", ">");
        return (in);
    }
    private String getFileExtension(String fileName) {

        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            //String ext = fileName.substring(fileName.lastIndexOf(".") + 1)
            //	 LogUtil.printLog(TAG,"Extension:"+fileName.substring(fileName.lastIndexOf(".") + 1));
            return fileName.substring(fileName.lastIndexOf(".") + 1);

        } else return "";
    }
}