package com.oruphones.nativediagnostic.Storage;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import org.pervacio.onediaglib.internalstorage.DumpSysExec;
import org.pervacio.onediaglib.internalstorage.FileVO;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternalStorageUsage {

	private static String TAG = InternalStorageUsage.class.getSimpleName();

	private Intent intentService;
	private static ServiceConnection mConnection = null;
	private static boolean mIsBound = false;
	public static Intent intent1;


	public String filePath;

	private Date startTime;
	private Date endTime;
    private Context context =null;
	
	public void getInternalStorageUsage(Context context, boolean logCacheSize) {
		this.context = context;
		this.filePath = getExternalStoragePath(DumpSysExec.dumpIT0("printenv"));
		//initConnection();
		findDuplicateFiles(logCacheSize);

	}

	/*private void initConnection() {

		mConnection = new ServiceConnection() {
			
			@Override
			public void onServiceConnected(ComponentName className,
					IBinder binder) {
				InternalStorageUsage.this.service = ((MyBinder) binder)
						.getService();
				InternalStorageUsage.this.mIsBound = true;
				if (InternalStorageUsage.this.service != null) {
					new BackgroundWorker(InternalStorageUsage.this)
							.execute(new String[] { InternalStorageUsage.this.filePath });
				}
			}
			 @Override
			public void onServiceDisconnected(ComponentName className) {
				//Toast.makeText(InternalStorageUsage.this, "Service disConnected", 1).show();
				InternalStorageUsage.this.service = null;
			}
		};
	}*/
	
	private String getExternalStoragePath(String cmdOutput) {
		String path = "";
		if (cmdOutput.length() == 0)
			return path;

		String[] lines = cmdOutput.split("\n");
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("EXTERNAL_STORAGE=")) {
				path = lines[i].split("=")[1];
			}
		}
		return path;
	}

	/*private static class BackgroundWorker extends
			AsyncTask<String, Integer, Map<Integer, List<FileVO>>> {
		private InternalStorageUsage activity;
		Map<Integer, List<FileVO>> duplicatesMap;

		
		 * renamed from:
		 * com.rm.duplicatefinder.DuplicatefinderActivity.BackgroundWorker.1
		 
		class C00471 implements DialogInterface.OnClickListener {
			C00471() {
			}

			public void onClick(DialogInterface dialog, int which) {
				BackgroundWorker.this.activity.service.cancelProgress();
				BackgroundWorker.this.activity.doUnBindService();
				BackgroundWorker.this.activity.service
						.stopService(BackgroundWorker.this.activity.intentService);
				BackgroundWorker.this.cancel(true);
			}
		}

		public BackgroundWorker(InternalStorageUsage activity) {
			//this.activity = null;
			this.duplicatesMap = null;
			//this.cancelSearchListener = new C00471();
			this.activity = activity;
		}

		protected Map<Integer, List<FileVO>> doInBackground(String... filePath) {
			try {
				//publishProgress(new Integer[] { Integer.valueOf(10) });
				this.duplicatesMap = this.activity.service
						.handledDuplicateFileIntent(this.activity.intentService);
			} catch (Exception e) {
				Log.d(TAG, "Exception doInBackground() : "+e.getMessage());
			}
			return this.duplicatesMap;
		}

		protected void onProgressUpdate(Integer... progress) {
			
			 * if (this.activity.progressDialog == null ||
			 * !this.activity.progressDialog.isShowing()) {
			 * this.activity.progressDialog = new
			 * ProgressDialog(this.activity.listSearchDirs.getContext());
			 * this.activity
			 * .progressDialog.setMessage(this.activity.getString(R.
			 * string.progress_duplicate_file));
			 * this.activity.progressDialog.setButton
			 * (this.activity.getString(R.string.general_cancel),
			 * this.cancelSearchListener);
			 * this.activity.progressDialog.setCanceledOnTouchOutside(false);
			 * this.activity.progressDialog.setCancelable(false);
			 * this.activity.progressDialog.show(); }
			 
		}

		protected void onPostExecute(Map<Integer, List<FileVO>> result) {
			//  this.activity.progressDialog.dismiss();
			 // this.activity.showDuplicateDialog(result);
			this.activity.calculateTime();
			
			doUnBindService();
		}
	}*/
	
	private void findDuplicateFiles(boolean logCacheSize)
	{
		/*InternalStorageUsage.this.startTime = new Date();
        Intent intent1 = new Intent(context, InetrnalStorageUsageService.class);
        intent1.setFlags(536870912);
        InternalStorageUsage.this.doBindService(intent1);*/
		
		
		InternalStorageUsage.this.startTime = new Date();
        intent1 = new Intent(context, InternalStorageUsageIntentService.class);
        intent1.putExtra("SERVICE_SEARCH_FILE_PATH", filePath);
        intent1.putExtra("LOG_CACHE_SIZE", logCacheSize);
        context.startService(intent1);
	}
	
	private void calculateTime() {
        this.endTime = new Date();
        long timeTaken = (this.endTime.getTime() - this.startTime.getTime()) / 1000;
        DLog.d(TAG, ""+ timeTaken + "seconds");
    }
	
	private void showDuplicateDialog(Map<Integer, List<FileVO>> results) {
        Map<Integer, List<FileVO>> map;
        if (results == null) {
            map = new HashMap<Integer, List<FileVO>>();
        } else {
            map = results;
        }
        int totalGroup = map.size();
        int totalElements = 0;
        for (Integer grpId : map.keySet()) {
            totalElements += ((List) map.get(grpId)).size();
        }
        this.endTime = new Date();
        long timeTaken = (this.endTime.getTime() - this.startTime.getTime()) / 1000;
        DLog.d(TAG, "" + totalGroup + " in " + timeTaken + "seconds");
        
        String msg = "";
        String title = map.size() == 0 ? "" : "duplicate_found";
        msg = new StringBuilder(String.valueOf("Time taken: " + timeTaken + " seconds")).append("\n------------------------------").toString();
        if (totalElements > 0) {
            msg = new StringBuilder(String.valueOf(msg)).append("\n").append("alert_dup_file_msg : ").append(totalGroup).toString();
        } else {
            msg = "duplicate_not_found";
        }
        DLog.d(TAG, "message : "+msg);
    }
	private void doBindService(Intent intent1) {
		
		  this.intentService = intent1;
		  intent1.putExtra("SERVICE_SEARCH_FILE_PATH", this.filePath);
		  //intent1.putStringArrayListExtra(Constants.INTENT_BUNDLE_EXCLUDED_DIRS, new
		  //ArrayList(this.dirToExclude));
		  //intent1.putExtra(Constants.INTENT_BUNDLE_SETTING_VO,
		  //AppContext.getInstance
		 // (getApplicationContext()).getCurrentSettingsProfile());
		  intent1.putExtra("START_TIME", this.startTime.getTime());
		  context.bindService(intent1, this.mConnection, 1);
		 
	}

	/*private static void doUnBindService() {
		if (mIsBound) {
			context.unbindService(mConnection);
			mIsBound = false;
		}
	}*/

//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		Log.d(TAG, "onDestroy!!!!!!");
//		doUnBindService();
//	}

}
