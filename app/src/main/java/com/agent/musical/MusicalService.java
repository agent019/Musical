package com.agent.musical;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class MusicalService extends Service {
    private IBinder serviceBinder = new ServiceBinder();
    static MediaPlayer mediaPlayer;

    public static MediaPlayer getMediaPlayer() {
        if(mediaPlayer == null)
        {
            mediaPlayer = new MediaPlayer();
        }
        return mediaPlayer;
    }

    public static int currentSongPosition = -1;

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
}
