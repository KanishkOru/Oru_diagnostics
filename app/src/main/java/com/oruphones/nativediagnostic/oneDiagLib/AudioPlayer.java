package com.oruphones.nativediagnostic.oneDiagLib;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.util.Pair;
import android.util.SparseArray;

import com.oruphones.nativediagnostic.OruApplication;
import com.oruphones.nativediagnostic.api.GlobalConfig;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;


import org.pervacio.onediaglib.audio.AudioPlayInfo;
import org.pervacio.onediaglib.audio.AudioUtils;
import org.pervacio.onediaglib.diagtests.TestResult;
import org.pervacio.onediaglib.utils.AppUtils;
import org.pervacio.onediaglib.utils.AppUtils.VersionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class AudioPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, ITimerListener {
    public static final int RESULT_ERROR_DATA_SOURCE = 65536;
    public static final int RESULT_ERROR_MEDIA_PLAYER_PREPARE = 65537;
    private static final String TAG = AudioPlayer.class.getName();
    protected static final int EVENT_TYPE_INIT = 1;
    protected static final int EVENT_TYPE_TEST_START = 2;
    protected static final int EVENT_TYPE_PLAY_STARTED = 3;
    protected static final int EVENT_TYPE_PLAY_PAUSED = 4;
    protected static final int EVENT_TYPE_PLAY_RESUMED = 5;
    protected static final int EVENT_TYPE_PLAY_STOPPED = 6;
    protected static final int EVENT_TYPE_TEST_END = 7;
    protected static final int EVENT_TYPE_HEAD_SET_STATE_CHANGE = 8;
    protected static final int START_AUDIO_PLAY = 9;
    protected static final int PAUSE_AUDIO_PLAY = 16;
    protected static final int RESUME_AUDIO_PLAY = 17;
    protected static final int STOP_AUDIO_PLAY = 18;
    private static final int QUIT = 57005;
    private int changedVolumeLevel = 0;

    private int audioUriID = 0;
    private ArrayList<Integer> numbersToPlay;
    private Handler mHandler = new Handler();
    private final Context context;
    private final AudioPlayInfo audioPlayInfo;
    private MediaPlayer mediaPlayer;
    private boolean looping;
    protected MediaPlayerStatus mediaPlayerStatus;
    protected AudioVolumeMode audioVolumeMode;
    private VolumeUpdater volumeUpdater;
    private AudioPlayTestListener testListener;
    protected HeadsetPlugStateListener headsetPlugStateListener;
    protected HeadsetPlugStateReceiver headsetPlugStateReceiver;
    protected AudioHandler audioHandler;
    protected EventHandler eventHandler;
    protected int initialVolume;
    protected int initialVolumeRingtone;
    protected int initialMode;
    protected int initialRingerMode;
    private boolean initialSpeakerPhoneState;
    private int initialAutoHapticValue;
    private int initialSoundMannerMode;
    private int initialInterruptionFilter;
    private int initialAllSoundOff;
    private SparseArray<Pair<Integer, String>> defaultSoundModesFujitsu;
    private SparseArray<Pair<Integer, String>> defaultMannerVolumeFujitsu;
    private SparseArray<Pair<Integer, String>> defaultDeviceVolume;
    private Uri[] audioUriList;
    private int startVolumeLevel;
    private Runnable mRunnable;

    public AudioPlayer(Context context, AudioPlayInfo audioPlayInfo, AudioPlayTestListener testListener) {
        this.mediaPlayerStatus = MediaPlayerStatus.IDLE;
        this.audioVolumeMode = AudioVolumeMode.NORMAL;
        this.initialVolume = -1;
        this.initialVolumeRingtone = -1;
        this.initialMode = -1;
        this.initialRingerMode = -1;
        this.initialAutoHapticValue = -1;
        this.initialSoundMannerMode = -1;
        this.initialInterruptionFilter = -1;
        this.initialAllSoundOff = -1;
        this.defaultMannerVolumeFujitsu = null;
        this.defaultDeviceVolume = null;
        this.startVolumeLevel = -1;
        this.mRunnable = new Runnable() {
            public void run() {
                AudioPlayer.this.setRingerMode(AudioPlayer.this.getAudioManager(), AudioPlayer.this.initialRingerMode);
            }
        };
        this.context = context;
        this.audioPlayInfo = audioPlayInfo;
        this.testListener = testListener;
        Pair<Float, Float> pair = adjustVolume(audioPlayInfo.leftVolume, audioPlayInfo.rightVolume);
        this.audioPlayInfo.leftVolume = (Float)pair.first;
        this.audioPlayInfo.rightVolume = (Float)pair.second;
        if (audioPlayInfo.isAudioAdvanceTest) {
            this.audioUriList = this.getAudioUriList();
        } else {
            this.audioUriList = new Uri[]{audioPlayInfo.audioSource};
        }

    }

    public final Context getContext() {
        return this.context;
    }

    public final AudioPlayInfo getAudioPlayInfo() {
        return this.audioPlayInfo;
    }

    public void initialize(Handler eventHandler) {
        if (this.audioHandler == null) {
            this.initHandlers(eventHandler);
        }
    }

    public void timeout() {
        if (this.audioHandler != null) {
            this.audioHandler.post(new Runnable() {
                public void run() {
                    AudioPlayer.this.onTestEnd(3, "Test Time out");
                }
            });
        }

    }

    public boolean start() {
        if (this.audioHandler == null) {
            return false;
        } else if (this.isPlaying()) {
            return true;
        } else {
            AppUtils.sendHandlerMessage(this.audioHandler, 9, 0, 0, (Object)null);
            return true;
        }
    }

    public boolean pause() {
        if (this.mediaPlayer != null && this.audioHandler != null) {
            AppUtils.sendHandlerMessage(this.audioHandler, 16, 0, 0, (Object)null);
            return true;
        } else {
            return false;
        }
    }

    public boolean resume() {
        if (this.mediaPlayer != null && this.audioHandler != null) {
            AppUtils.sendHandlerMessage(this.audioHandler, 17, 0, 0, (Object)null);
            return true;
        } else {
            return false;
        }
    }

    protected void pausePlay() {
        if (this.mediaPlayerStatus == MediaPlayerStatus.STARTED || this.mediaPlayerStatus == MediaPlayerStatus.PLAYBACK_COMPLETED) {
            this.mediaPlayer.pause();
            this.onPostPlayStop();
            this.mediaPlayerStatus = MediaPlayerStatus.PAUSED;
            this.dispatchPlayStatusEvent(4, 0, 0, (Object)null);
        }

    }

    protected void resumePlay() {
        if (this.mediaPlayerStatus == MediaPlayerStatus.PAUSED) {
            this.onPrePlayStart();
            this.mediaPlayer.start();
            this.mediaPlayerStatus = MediaPlayerStatus.STARTED;
            if (VersionUtils.hasMarshmallow() && this.audioVolumeMode == AudioVolumeMode.CONSTANT) {
                int flag = 1024;
                this.getAudioManager().setStreamVolume(this.getAudioStreamType(), this.startVolumeLevel, flag);
            }

            this.dispatchPlayStatusEvent(5, 0, 0, (Object)null);
        }

    }

    public boolean stop() {
        if (this.audioHandler == null) {
            return false;
        } else {
            AppUtils.sendHandlerMessage(this.audioHandler, 18, 0, 0, (Object)null);
            return true;
        }
    }

    public void setVolume(int volume) {
        AudioManager audioManager = this.getAudioManager();
        int streamMaxVolume = audioManager.getStreamMaxVolume(this.getAudioStreamType());
        if (volume >= 0 && volume <= streamMaxVolume) {
            audioManager.setStreamVolume(this.getAudioStreamType(), volume, 0);
        }
    }

    public void setHeadsetPlugStateListener(HeadsetPlugStateListener headsetPlugStateListener) {
        if (headsetPlugStateListener != null) {
            this.registerHeadsetReceiver();
        }

        this.headsetPlugStateListener = headsetPlugStateListener;
    }

    public void listenHeadsetStatus() {
        this.registerHeadsetReceiver();
    }

    public void setVolume(float leftVolume, float rightVolume) {
        Pair<Float, Float> pair = adjustVolume(leftVolume, rightVolume);
        leftVolume = (Float)pair.first;
        rightVolume = (Float)pair.second;
        if (this.mediaPlayer != null) {
            this.mediaPlayer.setVolume(leftVolume, rightVolume);
        }

        this.audioPlayInfo.leftVolume = leftVolume;
        this.audioPlayInfo.rightVolume = rightVolume;
    }

    public void setAudioVolumeMode(AudioVolumeMode audioVolumeMode) {
        this.audioVolumeMode = audioVolumeMode;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public void setStartVolumeLevel(Double percent) {
        int maxLevel = this.getAudioManager().getStreamMaxVolume(this.getAudioStreamType());
        this.startVolumeLevel = (int)Math.ceil((double)maxLevel * percent / 100.0);
        if (this.startVolumeLevel >= maxLevel) {
            this.startVolumeLevel = maxLevel;
        }

        AppUtils.printLog(TAG, "setStartVolumeLevel  startVolumeLevel :" + this.startVolumeLevel, (Throwable)null, 3);
    }

    public int getDuration() {
        return this.mediaPlayer != null ? this.mediaPlayer.getDuration() : -1;
    }

    public boolean isPlaying() {
        try {
            return this.mediaPlayer != null && this.mediaPlayer.isPlaying();
        } catch (IllegalStateException var2) {
            AppUtils.printLog(TAG, "IllegalStateException MediaPlayer", var2, 6);
            return false;
        }
    }

    protected void initHandlers(Handler _eventHandler) {
        if (_eventHandler != null) {
            this.eventHandler = new EventHandler(_eventHandler.getLooper());
        }

        (new Thread("AudioPlayerThread") {
            public void run() {
                Looper.prepare();
                AudioPlayer.this.audioHandler = AudioPlayer.this.new AudioHandler();
                if (AudioPlayer.this.eventHandler == null) {
                    AudioPlayer.this.eventHandler = AudioPlayer.this.new EventHandler(true);
                }

                AppUtils.sendHandlerMessage(AudioPlayer.this.eventHandler, 1, 0, 0, (Object)null);
                Looper.loop();
            }
        }).start();
    }

    protected void quitHandlers() {
        if (this.eventHandler != null && this.eventHandler.isAllowQuit()) {
            AppUtils.sendHandlerMessage(this.eventHandler, 57005, 0, 0, (Object)null);
        } else if (this.audioHandler != null) {
            AppUtils.sendHandlerMessage(this.audioHandler, 57005, 0, 0, (Object)null);
        }

        this.eventHandler = null;
        this.audioHandler = null;
    }

    protected void onHeadsetPlugStateChange(boolean isConnected) {
        if (this.headsetPlugStateListener != null) {
            AppUtils.sendHandlerMessage(this.eventHandler, 8, isConnected ? 1 : 0, 0, (Object)null);
        }

    }

    protected void registerHeadsetReceiver() {
        if (this.headsetPlugStateReceiver == null) {
            this.headsetPlugStateReceiver = new HeadsetPlugStateReceiver();
            IntentFilter intentFilter = new IntentFilter("android.intent.action.HEADSET_PLUG");
            intentFilter.setPriority(999);
            this.getContext().getApplicationContext().registerReceiver(this.headsetPlugStateReceiver, intentFilter);
        }
    }

    protected void unregisterHeadsetReceiver() {
        if (this.headsetPlugStateReceiver != null) {
            this.getContext().getApplicationContext().unregisterReceiver(this.headsetPlugStateReceiver);
            this.headsetPlugStateReceiver = null;
        }
    }

    protected boolean isListenerSet() {
        return this.testListener != null;
    }

    protected void startPlay() {
        this.dispatchPlayStatusEvent(2, 0, 0, (Object)null);
        if (this.mediaPlayer == null) {
            this.mediaPlayer = new MediaPlayer();
        }

        this.mediaPlayer.reset();
        String cause = this.setDataSource();
        if (cause != null) {
            this.onTestEnd(65536, "Error while setting data source. Cause = " + cause);
        } else {
            this.mediaPlayerStatus = MediaPlayerStatus.INITIALIZED;
            this.mediaPlayer.setAudioStreamType(this.getAudioStreamType());
            if (VersionUtils.hasLollipop()) {
                AudioAttributes.Builder builder = new AudioAttributes.Builder();
                builder.setLegacyStreamType(this.getAudioStreamType());
                AudioAttributes audioAttributes = builder.build();
                this.mediaPlayer.setAudioAttributes(audioAttributes);
            }

            this.mediaPlayer.setOnErrorListener(this);
            this.mediaPlayerStatus = MediaPlayerStatus.PREPARING;
            cause = this._prepare();
            if (cause != null) {
                this.onTestEnd(65537, "Error while preparing MediaPlayer. Cause = " + cause);
            } else {
                this.onPrepared();
            }
        }
    }

    protected void stopPlay() {
        this._stop();
        this._release();
        this.onPostPlayStop();
    }

    private String setDataSource() {
        try {
            this.mediaPlayer.setDataSource(this.context, this.audioUriList[this.audioUriID]);
            return null;
        } catch (Exception var3) {
            AppUtils.printLog(TAG, "Exception while setting data source for MediaPlayer. Data source = " + this.audioUriList[this.audioUriID].toString(), var3, 6);
            String cause = var3.getMessage() != null ? var3.getMessage() : "";
            return cause;
        }
    }

    private String _prepare() {
        try {
            this.mediaPlayer.prepare();
            return null;
        } catch (Exception var3) {
            AppUtils.printLog(TAG, "Exception while preparing Media player", var3, 6);
            String cause = var3.getMessage() != null ? var3.getMessage() : "";
            return cause;
        }
    }

    private void _stop() {
        if (this.mediaPlayer != null && this.mediaPlayerStatus != MediaPlayerStatus.STOPPED && this.mediaPlayerStatus != MediaPlayerStatus.RELEASED) {
            try {
                this.mediaPlayer.stop();
                this.mediaPlayerStatus = MediaPlayerStatus.STOPPED;
            } catch (Exception var2) {
                AppUtils.printLog(TAG, "Exception while stopping media player", var2, 6);
            }

            if (this.eventHandler != null && this.isListenerSet()) {
                this.dispatchPlayStatusEvent(6, 0, 0, (Object)null);
            }

        }
    }

    private void _release() {
        try {
            if (this.mediaPlayer != null) {
                this.mediaPlayer.reset();
                this.mediaPlayer.release();
                this.mediaPlayerStatus = MediaPlayerStatus.RELEASED;
            }
        } catch (Exception var5) {
            AppUtils.printLog(TAG, "Exception while releasing media player", var5, 6);
        } finally {
            this.mediaPlayer = null;
        }

    }

    public void onCompletion(MediaPlayer mp) {
        checkValidListenerInvocation("onCompletion(MediaPlayer)");
        ++this.audioUriID;
        if (this.looping) {
            mp.start();
            this.mediaPlayerStatus = MediaPlayerStatus.STARTED;
        } else if (this.audioUriID < this.audioUriList.length) {
            this.start();
        } else {
            this.audioUriID = 0;
            this.mediaPlayerStatus = MediaPlayerStatus.PLAYBACK_COMPLETED;
            this.onTestEnd(0, "Playing completed");
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        checkValidListenerInvocation("onError(MediaPlayer,int,int)");
        this.onTestEnd(69633, "Error occurred while preparing Media player. Error code = " + what + ", extra = " + extra);
        return true;
    }

    private static void checkValidListenerInvocation(String info) {
        if (!Thread.currentThread().getName().equals("AudioPlayerThread")) {
            throw new RuntimeException("Not allowed to call " + info);
        }
    }

    private void onPrepared() {
        this.onPrePlayStart();
        this.mediaPlayerStatus = MediaPlayerStatus.PREPARED;
        int duration = this.mediaPlayer.getDuration();
        this.mediaPlayer.setOnCompletionListener(this);
        this.mediaPlayer.setVolume(this.audioPlayInfo.leftVolume, this.audioPlayInfo.rightVolume);
        if (this.eventHandler != null && this.isListenerSet()) {
            this.dispatchPlayStatusEvent(3, duration, 0, (Object)null);
        }

        this.mediaPlayer.setLooping(this.looping);
        this.mediaPlayer.start();
        this.mediaPlayerStatus = MediaPlayerStatus.STARTED;
        if (VersionUtils.hasMarshmallow() && this.audioVolumeMode == AudioVolumeMode.CONSTANT) {
            int flag = 1024;
            this.getAudioManager().setStreamVolume(this.getAudioStreamType(), this.startVolumeLevel, flag);
        }

    }

    protected void onPrePlayStart() {
        AudioManager audioManager = this.getAudioManager();
        if (this.mHandler != null) {
            this.mHandler.removeCallbacks(this.mRunnable);
        }

        String[] mannerModeDevices = new String[]{"SHARP", "FUJITSU", "Sony"};
        if (this.initialVolume == -1 && !Arrays.asList(mannerModeDevices).contains(Build.MANUFACTURER)) {
            this.initialVolume = audioManager.getStreamVolume(this.getAudioStreamType());
        }

        if (this.initialVolume == -1 && Build.MANUFACTURER.equalsIgnoreCase("Sony")) {
            if (this.audioPlayInfo.enableSpeaker) {
                this.initialVolume = System.getInt(OruApplication.getAppContext().getContentResolver(), "volume_music_speaker", -1);
            } else {
                this.initialVolume = this.getAudioManager().getStreamVolume(this.getAudioStreamType());
            }
        }

        if ("HUAWEI".equalsIgnoreCase(Build.MANUFACTURER) && "HW-01E".equalsIgnoreCase(Build.MODEL) && this.initialVolumeRingtone == -1) {
            this.initialVolumeRingtone = audioManager.getStreamVolume(2);
        }

        if (this.initialMode == -1) {
            this.initialMode = audioManager.getMode();
        }

        if (this.initialRingerMode == -1) {
            this.initialRingerMode = audioManager.getRingerMode();
        }

        this.initialSpeakerPhoneState = audioManager.isSpeakerphoneOn();
        this.getMannerVolumeForFUJITSU();
        this.getDeviceDefaultVolume();
        this.initSoundMannerMode();
        this.setSoundModeForFUJITSU();
        this.changeDeviceSettings();
        this.setInterruptionFilter();
        int streamMaxVolume = audioManager.getStreamMaxVolume(this.getAudioStreamType());
        if (this.audioVolumeMode == AudioVolumeMode.MAXIMUM) {
            audioManager.setStreamVolume(this.getAudioStreamType(), streamMaxVolume, 0);
        } else if (this.audioVolumeMode == AudioVolumeMode.INCREASE) {
            if (this.volumeUpdater == null) {
                int startVolume = this.computeStartVolumeLevel(streamMaxVolume);
                AppUtils.printLog(TAG, "onPrePlayStart: start volume :" + startVolume + " streamMaxVolume :" + streamMaxVolume, (Throwable)null, 3);
                this.volumeUpdater = new VolumeUpdater(startVolume);
            }

            this.audioHandler.removeCallbacks(this.volumeUpdater);
            this.audioHandler.post(this.volumeUpdater);
        } else if (this.audioVolumeMode == AudioVolumeMode.CONSTANT) {
            if (this.startVolumeLevel == -1) {
                this.startVolumeLevel = streamMaxVolume;
            }

            int flag = 1024;
            audioManager.setStreamVolume(this.getAudioStreamType(), this.startVolumeLevel, flag);
            AppUtils.printLog(TAG, "onPrePlayStart: start volume :" + this.startVolumeLevel + " streamMaxVolume :" + streamMaxVolume, (Throwable)null, 3);
        }

        audioManager.setSpeakerphoneOn(this.audioPlayInfo.enableSpeaker);
        if (this.audioPlayInfo.enableSpeaker) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            this.setRingerMode(audioManager, 2);
        } else if (!"Sony".equalsIgnoreCase(Build.MANUFACTURER) && !"Galaxy Nexus".equals(Build.MODEL)) {
            if (VersionUtils.hasHoneycomb()) {
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
            } else {
                audioManager.setMode(AudioManager.MODE_IN_CALL);
            }
        } else {
            audioManager.setMode(AudioManager.MODE_NORMAL);
        }

    }

    private void setRingerMode(AudioManager audioManager, int mode) {
        if (!AudioUtils.grantedModelForDND()) {
            audioManager.setRingerMode(mode);
        }

    }

    protected void onPostPlayStop() {
        if (this.volumeUpdater != null) {
            this.audioHandler.removeCallbacks(this.volumeUpdater);
            this.volumeUpdater = null;
        }

        AudioManager audioManager = this.getAudioManager();
        if (this.initialVolume != -1) {
            audioManager.setStreamVolume(this.getAudioStreamType(), this.initialVolume, 0);
        }

        if (this.initialVolumeRingtone != -1) {
            audioManager.setStreamVolume(2, this.initialVolumeRingtone, 0);
        }

        if (this.initialMode != -1) {
            audioManager.setMode(this.initialMode);
        }

        if (this.initialRingerMode != -1) {
            this.setRingerMode(audioManager, this.initialRingerMode);
        }

        if (Build.MANUFACTURER.equalsIgnoreCase("Sony") || Build.MANUFACTURER.equalsIgnoreCase("LGE")) {
            this.mHandler.removeCallbacks(this.mRunnable);
            this.mHandler.postDelayed(this.mRunnable, 1000L);
        }

        audioManager.setSpeakerphoneOn(this.initialSpeakerPhoneState);
        if (VersionUtils.hasJellybeanMR1()) {
            AppUtils.printLog(TAG, "Setting Auto Haptic value to default", (Throwable)null, 3);
            if (this.initialAutoHapticValue != -1 && AppUtils.isPermissionGranted("android.permission.WRITE_SECURE_SETTINGS")) {
                Global.putInt(OruApplication.getAppContext().getContentResolver(), "def_tactileassist_enable", this.initialAutoHapticValue);
            }
        }

        this.resetDeviceDefaultVolume();
        this.retMannerVolumeForFUJITSU();
        this.resetInterruptionFilter();
        this.resetSoundModeForFUJITSU();
        this.resetDeviceSettings();
        if (this.initialSoundMannerMode != -1) {
            this.resetSoundMannerMode();
        }

    }

    protected AudioManager getAudioManager() {
        AudioManager audioManager = (AudioManager)this.getContext().getSystemService(Context.AUDIO_SERVICE);
        return audioManager;
    }

    private int getAudioStreamType() {
        return this.audioPlayInfo.enableSpeaker ? 3 : 0;
    }

    protected void dispatchPlayStatusEvent(int eventType, int msgArg1, int msgArg2, Object msgObj) {
        if (this.eventHandler != null && this.isListenerSet()) {
            AppUtils.sendHandlerMessage(this.eventHandler, eventType, msgArg1, msgArg2, msgObj);
        }

    }

    protected void onTestEnd(int resultCode, String resultDescription) {
        this.stopPlay();
        AudioUtils.broadCastMannerState(this.context, this.defaultSoundModesFujitsu);
        this.changedVolumeLevel = 0;
        this.unregisterHeadsetReceiver();
        TestResult testResult = new TestResult();
        testResult.setResultCode(resultCode);
        testResult.setResultDescription(resultDescription);
        if (this.audioPlayInfo.isAudioAdvanceTest) {
            TestResult.setTestAdditionalInfo(this.getNumString());
        }

        this.dispatchPlayStatusEvent(7, 0, 0, testResult);
        this.quitHandlers();
    }

    public static boolean isHeadsetConnected(Context context) {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        boolean wiredHeadsetOn = audioManager.isWiredHeadsetOn();
        return wiredHeadsetOn;
    }

    protected static Pair<Float, Float> adjustVolume(float leftVolume, float rightVolume) {
        if (leftVolume == 0.0F) {
            leftVolume = 0.01F;
        } else if (leftVolume == 1.0F) {
            leftVolume = 0.99F;
        }

        if (rightVolume == 0.0F) {
            rightVolume = 0.01F;
        } else if (rightVolume == 1.0F) {
            rightVolume = 0.99F;
        }

        return new Pair(leftVolume, rightVolume);
    }

    private void setSoundModeForFUJITSU() {
        if (Build.MANUFACTURER.equals("FUJITSU")) {
            if (this.defaultSoundModesFujitsu == null) {
                this.defaultSoundModesFujitsu = AudioUtils.getMannerModeForFujitsu(this.context);
            }

            AudioUtils.disableMannerModeForFujitsu(OruApplication.getAppContext(), this.defaultSoundModesFujitsu);
        }

    }

    private void resetSoundModeForFUJITSU() {
        if (Build.MANUFACTURER.equals("FUJITSU") && this.defaultSoundModesFujitsu != null) {
            AudioUtils.setMannerModeForFujitsu(OruApplication.getAppContext(), this.defaultSoundModesFujitsu);
        }

    }

    private void getMannerVolumeForFUJITSU() {
        if (Build.MANUFACTURER.equals("FUJITSU") && this.defaultMannerVolumeFujitsu == null) {
            this.defaultMannerVolumeFujitsu = AudioUtils.getMannerModeVolume(this.context);
        }

    }

    private void retMannerVolumeForFUJITSU() {
        if (Build.MANUFACTURER.equals("FUJITSU") && this.defaultMannerVolumeFujitsu != null) {
            AudioUtils.setMannerModeVolume(this.context, this.defaultMannerVolumeFujitsu);
        }

    }

    private void getDeviceDefaultVolume() {
        if (Build.MANUFACTURER.equals("FUJITSU") && this.defaultDeviceVolume == null) {
            this.defaultDeviceVolume = AudioUtils.getDeviceDefaultVolume(this.context);
        }

    }

    private void resetDeviceDefaultVolume() {
        AudioUtils.setDeviceDefaultVolume(this.context, this.defaultDeviceVolume);
    }

    @TargetApi(17)
    private void changeDeviceSettings() {
        AppUtils.printLog(TAG, "changeDeviceSettings method", (Throwable)null, 3);
        if (VersionUtils.hasJellybeanMR1()) {
            try {
                this.initialAutoHapticValue = Global.getInt(OruApplication.getAppContext().getContentResolver(), "def_tactileassist_enable");
                if (AppUtils.isPermissionGranted("android.permission.WRITE_SECURE_SETTINGS")) {
                    Global.putInt(OruApplication.getAppContext().getContentResolver(), "def_tactileassist_enable", 0);
                }
            } catch (Settings.SettingNotFoundException var2) {
                AppUtils.printLog(TAG, "def_tactileassist_enable SettingNotFoundException", (Throwable)null, 6);
            }
        }

        if (this.initialAllSoundOff == -1) {
            this.initialAllSoundOff = AudioUtils.getAllSoundOffSetting();
        }

        if (this.initialAllSoundOff == 1) {
            AudioUtils.setAllSoundOffSetting(this.context, 0);
            AudioUtils.sendAllSoundOffBroadcast(this.context, 0);
        }

    }

    @TargetApi(17)
    private void resetDeviceSettings() {
        AppUtils.printLog(TAG, "resetDeviceSettings Method ", (Throwable)null, 3);
        if (VersionUtils.hasJellybeanMR1() && this.initialAutoHapticValue != -1 && AppUtils.isPermissionGranted("android.permission.WRITE_SECURE_SETTINGS")) {
            Global.putInt(OruApplication.getAppContext().getContentResolver(), "def_tactileassist_enable", this.initialAutoHapticValue);
        }

        if (this.initialAllSoundOff == 1) {
            AudioUtils.setAllSoundOffSetting(this.context, this.initialAllSoundOff);
            AudioUtils.sendAllSoundOffBroadcast(this.context, this.initialAllSoundOff);
        }

    }

    @TargetApi(17)
    private void initSoundMannerMode() {
        if (Build.MANUFACTURER.equalsIgnoreCase("SHARP")) {
            this.initSoundMannerModeSharp();
        } else if (Build.MANUFACTURER.equalsIgnoreCase("FUJITSU")) {
            this.initSoundMannerModeFujitsu();
        } else if (Build.MANUFACTURER.equalsIgnoreCase("LGE") && (VERSION.SDK_INT == 21 || VERSION.SDK_INT == 22)) {
            this.initialSoundMannerMode = Global.getInt(OruApplication.getAppContext().getContentResolver(), "zen_mode", -1);
        }

    }

    @TargetApi(17)
    private void resetSoundMannerMode() {
        if (Build.MANUFACTURER.equalsIgnoreCase("SHARP")) {
            this.resetSoundMannerModeSharp();
        } else if (Build.MANUFACTURER.equalsIgnoreCase("FUJITSU")) {
            this.resetSoundMannerModeFujitsu();
        } else if (Build.MANUFACTURER.equalsIgnoreCase("LGE") && (VERSION.SDK_INT == 21 || VERSION.SDK_INT == 22) && AppUtils.isPermissionGranted("android.permission.WRITE_SECURE_SETTINGS")) {
            Global.putInt(OruApplication.getAppContext().getContentResolver(), "zen_mode", this.initialSoundMannerMode);
        }

    }

    private void initSoundMannerModeSharp() {
        try {
            Class<?> audioCls = Class.forName("jp.co.sharp.android.media.AudioManagerEx");
            Constructor<?> constructor = audioCls.getDeclaredConstructor(Context.class);
            constructor.setAccessible(true);
            Object audioManagerEx = constructor.newInstance(OruApplication.getAppContext());
            Method method = audioCls.getDeclaredMethod("getRingerModeEx");
            method.setAccessible(true);
            Object mannerValue = method.invoke(audioManagerEx);
            if (mannerValue != null && mannerValue instanceof Integer) {
                this.initialSoundMannerMode = (Integer)mannerValue;
            }

            if (this.initialVolume == -1) {
                if (this.audioPlayInfo.enableSpeaker) {
                    this.initialVolume = System.getInt(OruApplication.getAppContext().getContentResolver(), "volume_music_speaker");
                } else {
                    this.initialVolume = this.getAudioManager().getStreamVolume(this.getAudioStreamType());
                }
            }
        } catch (Exception var6) {
            AppUtils.printLog(TAG, "Exception in initSoundMannerMode(): " + var6.getMessage(), var6, 6);
        }

    }

    private void resetSoundMannerModeSharp() {
        try {
            Class<?> audioCls = Class.forName("jp.co.sharp.android.media.AudioManagerEx");
            Constructor<?> constructor = audioCls.getDeclaredConstructor(Context.class);
            constructor.setAccessible(true);
            Object audioManagerEx = constructor.newInstance(OruApplication.getAppContext());
            Method method = audioCls.getDeclaredMethod("setRingerModeEx", Integer.TYPE);
            method.setAccessible(true);
            method.invoke(audioManagerEx, this.initialSoundMannerMode);
        } catch (Exception var5) {
            AppUtils.printLog(TAG, "Exception in resetSoundMannerMode(): " + var5.getMessage(), var5, 6);
        }

    }

    private void initSoundMannerModeFujitsu() {
        try {
            AudioManager audioManager = this.getAudioManager();
            Method method = audioManager.getClass().getDeclaredMethod("getMannerMode");
            method.setAccessible(true);
            Object object = method.invoke(audioManager);
            if (object != null) {
                this.initialSoundMannerMode = (Integer)object;
            }
        } catch (Exception var4) {
            AppUtils.printLog(TAG, "Exception while getting manner mode for Fujitsu", var4, 6);
        }

    }

    private void resetSoundMannerModeFujitsu() {
        try {
            Context var10000 = OruApplication.getAppContext();
            Context var10001 = this.context;
            AudioManager audioManager = (AudioManager)var10000.getSystemService(Context.AUDIO_SERVICE);
            Method method = audioManager.getClass().getDeclaredMethod("setMannerMode", Integer.TYPE);
            method.setAccessible(true);
            method.invoke(audioManager, this.initialSoundMannerMode);
        } catch (Exception var3) {
            AppUtils.printLog(TAG, "Exception while setting manner mode for Fujitsu", var3, 6);
        }

    }

    @TargetApi(23)
    private void setInterruptionFilter() {
        if (VersionUtils.hasMarshmallow()) {
            if (this.initialInterruptionFilter == -1) {
                this.initialInterruptionFilter = AudioUtils.getInterruptionFilter(this.context);
                AppUtils.printLog(TAG, "Initial InterruptionFilter=" + this.initialInterruptionFilter, (Throwable)null, 3);
            }

            if (this.initialInterruptionFilter != -1 && this.initialInterruptionFilter != 1) {
                AudioUtils.setInterruptionFilter(this.context, 1);
            }
        }

    }

    @TargetApi(23)
    private void resetInterruptionFilter() {
        if (this.initialInterruptionFilter != -1 && this.initialInterruptionFilter != 1) {
            AudioUtils.setInterruptionFilter(this.context, this.initialInterruptionFilter);
        }

    }

    private int computeStartVolumeLevel(int maxLevel) {
        int start = maxLevel / 2 + this.changedVolumeLevel;
        if (start >= maxLevel) {
            start = maxLevel;
        }

        return start;
    }

    private Uri[] getAudioUriList() {
        HashMap<Integer, Uri> audioUriMap = AudioUtils.getAudiosUriMap(this.context);
        this.numbersToPlay = this.getRandomNumbers(9, 3);
        DLog.d(TAG, "EarphoneTestRandomNumbers: "+ this.numbersToPlay);
        GlobalConfig globalConfig = GlobalConfig.getInstance();
        String currTest = globalConfig.getCurrentTest();
        if (currTest!=null){
            if (currTest.equals("EarpieceTest")){
                globalConfig.addItemToList("Earpiece Test System Output: "+this.numbersToPlay);
            }else if(currTest.equalsIgnoreCase("EarphoneTest")){
                globalConfig.addItemToList("Earphone Test System Output: "+this.numbersToPlay);
            }else if(currTest.equalsIgnoreCase("SpeakerTest")){
                globalConfig.addItemToList("Speaker Test System Output: "+this.numbersToPlay);
            }
        }


        return this.audioPlayInfo.enableSpeaker ? new Uri[]{(Uri)audioUriMap.get(-2), (Uri)audioUriMap.get(this.numbersToPlay.get(0)), (Uri)audioUriMap.get(this.numbersToPlay.get(1)), (Uri)audioUriMap.get(this.numbersToPlay.get(2))} : new Uri[]{(Uri)audioUriMap.get(-1), (Uri)audioUriMap.get(this.numbersToPlay.get(0)), (Uri)audioUriMap.get(this.numbersToPlay.get(1)), (Uri)audioUriMap.get(this.numbersToPlay.get(2))};
    }

    public ArrayList<Integer> getRandomNumbers(int range, int n) {
        ArrayList<Integer> picked = new ArrayList();

        while(picked.size() < n) {
            int i = (new Random()).nextInt(range - 1) + 1;
            if (!picked.contains(i)) {
                picked.add(i);
            }
        }

        return picked;
    }

    private String getNumString() {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator var2 = this.numbersToPlay.iterator();

        while(var2.hasNext()) {
            int num = (Integer)var2.next();
            stringBuilder.append(num);
        }

        return stringBuilder.toString();
    }

    protected class AudioHandler extends Handler {
        protected AudioHandler() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 9:
                    AudioPlayer.this.startPlay();
                    break;
                case 16:
                    AudioPlayer.this.pausePlay();
                    break;
                case 17:
                    AudioPlayer.this.resumePlay();
                    break;
                case 18:
                    AudioPlayer.this.onTestEnd(0, "Audio stopped manually");
                    break;
                case 57005:
                    this.removeCallbacksAndMessages((Object)null);
                    Looper looper = Looper.myLooper();
                    if (looper != null) {
                        looper.quit();
                    }
            }

        }
    }

    protected class EventHandler extends Handler {
        private boolean allowQuit;

        public EventHandler(boolean allowQuit) {
            this.allowQuit = allowQuit;
        }

        public EventHandler(Looper looper) {
            super(looper);
        }

        public boolean isAllowQuit() {
            return this.allowQuit;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (AudioPlayer.this.isListenerSet()) {
                        AudioPlayer.this.testListener.onInitialized(AudioPlayer.this);
                    }
                    break;
                case 2:
                    if (AudioPlayer.this.isListenerSet()) {
                        AudioPlayer.this.testListener.onTestStart();
                    }
                    break;
                case 3:
                    if (AudioPlayer.this.isListenerSet()) {
                        AudioPlayer.this.testListener.onPlayStarted(AudioPlayer.this, msg.arg1);
                    }
                    break;
                case 4:
                    if (AudioPlayer.this.isListenerSet()) {
                        AudioPlayer.this.testListener.onPlayPaused(AudioPlayer.this);
                    }
                    break;
                case 5:
                    if (AudioPlayer.this.isListenerSet()) {
                        AudioPlayer.this.testListener.onPlayResumed(AudioPlayer.this);
                    }
                    break;
                case 6:
                    if (AudioPlayer.this.isListenerSet()) {
                        AudioPlayer.this.testListener.onPlayStopped(AudioPlayer.this);
                    }
                    break;
                case 7:
                    if (AudioPlayer.this.isListenerSet()) {
                        TestResult testResult = (TestResult)msg.obj;
                        AudioPlayer.this.testListener.onTestEnd(testResult);
                    }
                    break;
                case 8:
                    if (AudioPlayer.this.headsetPlugStateListener != null) {
                        AudioPlayer.this.headsetPlugStateListener.onHeadsetPlugStateChange(msg.arg1 == 1);
                    }
                    break;
                case 57005:
                    if (this.allowQuit) {
                        this.removeCallbacksAndMessages((Object)null);
                        Looper looper = Looper.myLooper();
                        if (looper != null) {
                            looper.quit();
                        }
                    }
            }

        }
    }

    protected class VolumeUpdater implements Runnable {
        private int currentVolume;

        public VolumeUpdater(int currentVolume) {
            this.currentVolume = currentVolume;
        }

        public void run() {
            AudioManager audioManager = AudioPlayer.this.getAudioManager();
            int streamMaxVolume = audioManager.getStreamMaxVolume(AudioPlayer.this.getAudioStreamType());
            if (this.currentVolume <= streamMaxVolume) {
                int audioStreamType = AudioPlayer.this.getAudioStreamType();
                AppUtils.printLog(AudioPlayer.TAG, "CurrentVolume :" + this.currentVolume + "-->streamMaxVolume: " + streamMaxVolume, (Throwable)null, 3);
                int flag = 1024;
                audioManager.setStreamVolume(audioStreamType, this.currentVolume, flag);
                int volToIncrease = (int)Math.ceil((double)streamMaxVolume * 10.0 / 100.0);
                this.currentVolume += volToIncrease;
                AudioPlayer.this.changedVolumeLevel = AudioPlayer.this.changedVolumeLevel + volToIncrease;
                AudioPlayer.this.audioHandler.postDelayed(this, 2000L);
            } else {
                AudioPlayer.this.audioHandler.removeCallbacks(this);
            }

        }
    }

    protected class HeadsetPlugStateReceiver extends BroadcastReceiver {
        protected HeadsetPlugStateReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    AudioPlayer.this.onHeadsetPlugStateChange(false);
                    break;
                case 1:
                    AudioPlayer.this.onHeadsetPlugStateChange(true);
            }

        }
    }

    protected static enum MediaPlayerStatus {
        IDLE,
        INITIALIZED,
        PREPARING,
        PREPARED,
        STARTED,
        PAUSED,
        PLAYBACK_COMPLETED,
        STOPPED,
        RELEASED,
        ERROR;

        private MediaPlayerStatus() {
        }
    }

    public static enum AudioVolumeMode {
        NORMAL,
        MAXIMUM,
        INCREASE,
        CONSTANT;

        private AudioVolumeMode() {
        }
    }
}
