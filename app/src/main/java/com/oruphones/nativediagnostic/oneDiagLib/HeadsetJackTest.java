package com.oruphones.nativediagnostic.oneDiagLib;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.view.KeyEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import org.pervacio.onediaglib.audio.HeadsetMediaButtonReceiver;
import org.pervacio.onediaglib.audio.HeadsetPlugStateListener;
import org.pervacio.onediaglib.utils.AppUtils;

public class HeadsetJackTest {
    private final Context context;
    private HeadsetPlugStateReceiver headsetPlugStateReceiver;
    private HeadsetJackStateListener headsetJackStateListener;
    private static String TAG = HeadsetJackTest.class.getSimpleName();
    private MediaSession mediaSession;

    public HeadsetJackTest(@NonNull Context context) {
        this.context = context.getApplicationContext() == null ? context : context.getApplicationContext();
    }

    public void setHeadsetJackStateListener(@Nullable HeadsetJackStateListener headsetJackStateListener) {
        this.headsetJackStateListener = headsetJackStateListener;
    }

    public void registerHeadsetStateEventReceiver() {
        if (this.headsetPlugStateReceiver == null) {

            DLog.d(TAG,"entered");
            this.headsetPlugStateReceiver = new HeadsetPlugStateReceiver();
            IntentFilter intentFilter = new IntentFilter("android.intent.action.HEADSET_PLUG");
            intentFilter.setPriority(1000);
            this.context.registerReceiver(this.headsetPlugStateReceiver, intentFilter);
            AudioManager audioManager = (AudioManager)this.context.getSystemService(Context.AUDIO_SERVICE);
            ComponentName componentName = new ComponentName(this.context, HeadsetMediaButtonReceiver.class);
            if (AppUtils.VersionUtils.hasLollipop()) {
                this.mediaSession = new MediaSession(this.context, HeadsetJackTest.class.getName());
                this.mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS);
                this.mediaSession.setCallback(new MediaSession.Callback() {
                    public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                        KeyEvent keyEvent = (KeyEvent)mediaButtonIntent.getParcelableExtra("android.intent.extra.KEY_EVENT");
                        if (HeadsetJackTest.this.headsetJackStateListener != null) {
                            HeadsetJackTest.this.headsetJackStateListener.onHeadsetButtonEvent(keyEvent);
                        }

                        return super.onMediaButtonEvent(mediaButtonIntent);
                    }
                });
                this.mediaSession.setActive(true);
            } else if (AppUtils.VersionUtils.hasJellybeanMR2()) {
                DLog.d(TAG,"entered11");
                Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
                intent.setComponent(componentName);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, PendingIntent.FLAG_MUTABLE);
                audioManager.registerMediaButtonEventReceiver(pendingIntent);
            } else {
                DLog.d(TAG,"entered12");
                audioManager.registerMediaButtonEventReceiver(componentName);
            }

        }
    }

    public void unregisterHeadsetStateEventReceiver() {
        if (this.headsetPlugStateReceiver != null) {
            DLog.d(TAG,"entered1");
            this.context.unregisterReceiver(this.headsetPlugStateReceiver);
            this.headsetPlugStateReceiver = null;
            AudioManager audioManager = (AudioManager)this.context.getSystemService(Context.AUDIO_SERVICE);
            if (AppUtils.VersionUtils.hasLollipop()) {
                DLog.d(TAG,"entered2");
                if (this.mediaSession != null) {
                    DLog.d(TAG,"entered3");
                    this.mediaSession.release();
                    this.mediaSession = null;
                }
            } else if (AppUtils.VersionUtils.hasJellybeanMR2()) {
                DLog.d(TAG,"entered4");
                Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
                ComponentName componentName = new ComponentName(this.context, HeadsetMediaButtonReceiver.class);
                intent.setComponent(componentName);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_MUTABLE);
                if (pendingIntent == null) {
                    DLog.d(TAG,"entered5");
                    pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, PendingIntent.FLAG_MUTABLE);
                }

                audioManager.unregisterMediaButtonEventReceiver(pendingIntent);
            } else {
                DLog.d(TAG,"entered6");
                ComponentName componentName = new ComponentName(this.context, HeadsetMediaButtonReceiver.class);
                audioManager.unregisterMediaButtonEventReceiver(componentName);
            }

        }
    }

    private class HeadsetPlugStateReceiver extends BroadcastReceiver {
        private HeadsetPlugStateReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.HEADSET_PLUG".equals(action)) {
                int state = intent.getIntExtra("state", -1);
                DLog.d(TAG,"entered7" + state);
                if (HeadsetJackTest.this.headsetJackStateListener != null) {
                    if (state == 1) {
                        HeadsetJackTest.this.headsetJackStateListener.onHeadsetPlugStateChange(true);
                    } else if (state == 0) {
                        HeadsetJackTest.this.headsetJackStateListener.onHeadsetPlugStateChange(false);
                    }
                }
            }

        }
    }

    public interface HeadsetJackStateListener extends HeadsetPlugStateListener {
        void onHeadsetButtonEvent(KeyEvent var1);
    }
}
