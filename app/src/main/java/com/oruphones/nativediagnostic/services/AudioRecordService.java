package com.oruphones.nativediagnostic.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;


import com.oruphones.nativediagnostic.R;
import com.oruphones.nativediagnostic.util.BaseUtils;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import java.io.File;
import java.io.IOException;

/**
 * Created by Pervacio on 19-11-2015.
 */

public class AudioRecordService extends Service implements MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener {

    private static final String TAG = AudioRecordService.class.getName();

    private enum RecorderState {
        IDLE, INITIALIZED, PREPARED, STARTED, STOPPED, RELEASED, UNKNOWN;
    }

    private MediaRecorder mediaRecorder;
    private int recordDuration = 10 * 1000;
    private RecorderState recorderState = RecorderState.IDLE;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = intent.getStringExtra("command");
        if (command == null) {
            stopSelf();
            return START_STICKY;
        }
        int duration = intent.getIntExtra("duration", 10);
        if (duration > 0 && duration <= 60) {
            recordDuration = duration * 1000;
        }
        if (command.equals("captureaudiostart")) {
            startRecording();
        } else if (command.equals("captureaudiostop")) {
            stopRecording();
            stopSelf();
        } else {
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            stopRecording();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.recording_done), Toast.LENGTH_SHORT).show();
            String audioOutputFile = getAudioOutputFile(getApplicationContext(), false);
            stopSelf();
        }
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_ERROR_SERVER_DIED) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
        stopSelf();
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // if (VersionUtils.hasGingerbreadMR1()) {
        // mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        // } else {
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // }
        mediaRecorder.setOutputFile(getAudioOutputFile(getApplicationContext(), true));
        mediaRecorder.setMaxDuration(recordDuration);
        mediaRecorder.setOnErrorListener(this);
        mediaRecorder.setOnInfoListener(this);
        recorderState = RecorderState.INITIALIZED;
        try {
            mediaRecorder.prepare();
            recorderState = RecorderState.PREPARED;
            mediaRecorder.start();
            recorderState = RecorderState.STARTED;
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.recording_start), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            stopSelf();
            return;
        }
    }

    private void stopRecording() {
        if (mediaRecorder == null) {
            return;
        }
        if (recorderState == RecorderState.STARTED) {
            mediaRecorder.stop();
            recorderState = RecorderState.STOPPED;
        }
        mediaRecorder.release();
        recorderState = RecorderState.RELEASED;
        mediaRecorder = null;
        recorderState = RecorderState.IDLE;
    }

    public static boolean deleteAudioRecordsDirectory(Context context) {
        try {
            String string = getAudioOutputFile(context, false);
            File file = new File(string);
            // TODO remove hard coded value of "pvadiag"? as this value is taken
            // from
            // #getAudioOutputFile().
            while (file.getPath().contains("pvadiag")) {
                File f = file.getParentFile();
                BaseUtils.deleteFile(context, file);
                file = f;
            }
            return true;
        } catch (Exception e) {
             DLog.e(TAG, "Exception in deleteAudioRecordsDirectory(): " + e.getMessage());
        }
        return false;
    }

    public static String getAudioOutputFile(Context context, boolean delete) {
        File file = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                && Environment.getExternalStorageDirectory().getUsableSpace() > 100 * 1024) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "pvadiag"
                    + File.separator + "AudioRecords";
            file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            @SuppressWarnings("deprecation")
            int mode = MODE_WORLD_WRITEABLE;
            file = context.getDir("AudioRecords", mode);
        }
        file = new File(file, "recorded_audio.3gp");
        if (file.exists() && delete) {
            file.delete();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
            }
        }
        String absolutePath = file.getAbsolutePath();
        return absolutePath;
    }

    public static String _getAudioOutputFile(Context context, boolean delete) {
        File directory = Environment.getExternalStorageDirectory();
        directory = context.getDir("RecordedAudio", MODE_PRIVATE);
        File file = new File(directory, "recorded_audio.3gp");
        if (file.exists() && delete) {
            file.delete();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
            }
        }
        String absolutePath = file.getAbsolutePath();
        return absolutePath;
    }
}
