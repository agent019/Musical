package com.agent.musical;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MusicalService extends Service {
    private IBinder serviceBinder = new ServiceBinder();
    public static MediaPlayer mediaPlayer;

    public static MediaPlayer getMediaPlayer() {
        if(mediaPlayer == null)
        {
            mediaPlayer = new MediaPlayer();
        }
        return mediaPlayer;
    }

    public String mediaUri;
    private int pausePosition;
    public static int currentSongPosition = -1;

    private void initMediaPlayer() {
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.prepareAsync();
        }
    }

    private void playMedia() {
        if(!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if(mediaPlayer == null) return;
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    private void pauseMedia() {
        if(mediaPlayer == null) return;
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            pausePosition = mediaPlayer.getCurrentPosition();
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(pausePosition);
            mediaPlayer.start();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    public class ServiceBinder extends Binder {
        public MusicalService getService() {
            return MusicalService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
    }
}
