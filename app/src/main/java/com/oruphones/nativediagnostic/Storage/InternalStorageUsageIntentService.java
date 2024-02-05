package com.oruphones.nativediagnostic.Storage;

import android.app.IntentService;
import android.content.Intent;

import org.pervacio.onediaglib.internalstorage.FileVO;

import java.util.List;
import java.util.Map;

public class InternalStorageUsageIntentService extends IntentService {

	private InternalStorageUsageHandler fileHandler;

	public InternalStorageUsageIntentService() {
		super("InternalStorageUsageIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		String filePath = intent.getExtras().getString("SERVICE_SEARCH_FILE_PATH");
		boolean logCacheSize = intent.getExtras().getBoolean("LOG_CACHE_SIZE");
        //List<String> excludedDirs = intent.getExtras().getStringArrayList(Constants.INTENT_BUNDLE_EXCLUDED_DIRS);
        //this.pendingIntent = (PendingIntent) intent.getExtras().getParcelable("PENDING_INTENT");
        //this.intent = intent;
        Map<Integer, List<FileVO>> duplicates = null;
        try {
            //showNotification(getString(R.string.notification_finding));
            this.fileHandler = new InternalStorageUsageHandler(filePath);
            this.fileHandler.getAppsSizeInfo(getApplicationContext(), logCacheSize);
            
            //this.fileHandler.getInstalledApps(getApplicationContext());
            //duplicates = this.fileHandler.findDuplicate(filePath/*, excludedDirs*/);            
            //this.fileHandler.saveDataToFile();
            
            //stopForeground(true);
            //return duplicates;
        } catch (Exception e) {
        	e.printStackTrace();
        } catch (Throwable e) {
			e.printStackTrace();
		}
		
	}
	
	

}
