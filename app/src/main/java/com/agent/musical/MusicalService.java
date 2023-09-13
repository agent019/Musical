package com.agent.musical;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

public class MusicalService extends MediaSessionService {
    public static final String TAG = "MusicalService";
    private MediaSession mediaSession = null;

    public void playMedia(String mediaUri) {
        loadMedia(mediaUri);
        mediaSession.getPlayer().play();
    }

    public void loadMedia(String mediaUri) {
        MediaItem mediaItem = MediaItem.fromUri(mediaUri);
        mediaSession.getPlayer().setMediaItem(mediaItem);
        mediaSession.getPlayer().prepare();
    }

    @Nullable
    @Override
    public MediaSession onGetSession(@NonNull MediaSession.ControllerInfo controllerInfo) {
        return mediaSession;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ExoPlayer player = new ExoPlayer.Builder(this).build();
        mediaSession = new MediaSession.Builder(this, player).build();
    }

    @Override
    public void onDestroy() {
        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        mediaSession.getPlayer().release();
        mediaSession.release();
        mediaSession = null;
        super.onTaskRemoved(rootIntent);
    }
}
