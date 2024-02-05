package com.oruphones.nativediagnostic.oneDiagLib;



public interface AudioPlayTestListener extends TestListener {
    void onInitialized(AudioPlayer var1);

    void onPlayStarted(AudioPlayer var1, int var2);

    void onPlayPaused(AudioPlayer var1);

    void onPlayResumed(AudioPlayer var1);

    void onPlayStopped(AudioPlayer var1);
}
