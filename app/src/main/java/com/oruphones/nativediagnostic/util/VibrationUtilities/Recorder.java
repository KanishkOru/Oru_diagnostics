package com.oruphones.nativediagnostic.util.VibrationUtilities;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;


import com.oruphones.nativediagnostic.util.Callback;
import com.oruphones.nativediagnostic.util.DevelopmentTools.DLog;

import java.util.Objects;


public class Recorder {

    private Callback callback;
    private int audioSource = MediaRecorder.AudioSource.DEFAULT;
    private static String TAG = Recorder.class.getSimpleName();
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int sampleRate = 44100;
    private Thread thread;

    public String statuus;
    public Recorder() {
    }

    public Recorder(String status,Callback callback) {
        this.callback = callback;
        this.statuus=status;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    int i=16;
    int j =16;

    public void start() {

        DLog.d(TAG, "Recorder start(): "+statuus);


        if (thread != null) return;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

                int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioEncoding);
                AudioRecord recorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioEncoding, minBufferSize);

                if (recorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
                    Thread.currentThread().interrupt();
                    return;
                } else {
                    DLog.i(TAG, "Started.");
                    //callback.onStart();
                }
                byte[] buffer = new byte[minBufferSize];
                recorder.startRecording();

                if( statuus=="start" || statuus =="end") {
                    while (i != 0 && thread != null && !thread.isInterrupted() && recorder.read(buffer, 0, minBufferSize) > 0) {
                        // Log.d("25/09/2023", "run: i = "+i);
                        i--;
                        DLog.d(TAG, "statuus==\"start\" || statuus ==\"end\": "+statuus);

                        callback.onBufferAvailable(buffer,"0");
                    }
                }

                else
                {
                    try {

                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        // Handle interrupted exception if necessary
                    }

                    while (j!=0 && thread != null && !thread.isInterrupted() && recorder.read(buffer, 0, minBufferSize) > 0) {

                        //Log.d("25/09/2023", "run: j = "+j);
                        j--;
                        DLog.d(TAG, "statuus==vibration: "+statuus);

                        callback.onBufferAvailable(buffer,"0");
                    }



                }

                if(Objects.equals(statuus, "start"))
                {
                    DLog.d(TAG, "Objects.equals(statuus, \"start\"): "+statuus);

                    callback.onBufferAvailable(buffer,"laststart");
                }


                else if(Objects.equals(statuus, "vibration"))
                {
                    DLog.d(TAG, "Objects.equals(statuus, \"vibration\"): "+statuus);

                    callback.onBufferAvailable(buffer,"lastvibration");
                }

                else
                {
                    DLog.d(TAG, "Objects.equals(statuus, \"end\"): "+statuus);

                    callback.onBufferAvailable(buffer,"lastend");
                }

                recorder.stop();
                recorder.release();

            }


        }, Recorder.class.getName());
        thread.start();
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }
}