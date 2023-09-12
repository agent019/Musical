package com.agent.musical;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import java.io.IOException;

/**
 * <a href="https://developer.android.com/static/images/mediaplayer_state_diagram.gif">...</a>
 */
public class MusicalService
        extends MediaSessionService {
    public static final String TAG = "MusicalService";
    private MediaSession mediaSession = null;

    public void playMedia(String mediaUri) {
        loadMedia(mediaUri);
        mediaSession.getPlayer().play();
    }

    public void playMedia() {
        mediaSession.getPlayer().play();
    }

    public void stopMedia() {
        mediaSession.getPlayer().stop();
    }

    public void pauseMedia() {
        mediaSession.getPlayer().pause();
    }

    public void resumeMedia() {
        mediaSession.getPlayer().play();
    }

    public boolean isPlaying() {
        return mediaSession.getPlayer().isPlaying();
    }

    public long getCurrentPosition() {
        return mediaSession.getPlayer().getCurrentPosition();
    }

    public void loadMedia(String mediaUri) {
        MediaItem mediaItem = MediaItem.fromUri(mediaUri);
        mediaSession.getPlayer().setMediaItem(mediaItem);
        mediaSession.getPlayer().prepare();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ExoPlayer player = new ExoPlayer.Builder(this).build();
        mediaSession = new MediaSession.Builder(this, player).build();
        return;
    }

    @Override
    public void onDestroy() {
        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;
        super.onDestroy();
    }
}
