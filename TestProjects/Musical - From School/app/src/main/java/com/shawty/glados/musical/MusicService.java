package com.shawty.glados.musical;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import java.security.spec.ECField;
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;

/**
 * Created by GLaDOS on 4/19/2016.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
    private MediaPlayer player;
    private ArrayList<Song> songs;
    private int songPosition;
    private final IBinder musicBind = new MusicBinder();
    private String songName = "";
    private static final int NOTIFY_ID = 1;
    private boolean shuffle = false;
    private Random rand;

    public void onCreate() {
        super.onCreate();
        songPosition = 0;
        rand = new Random();
        player = new MediaPlayer();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        initMusicPlayer();
    }

    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                try {
                    if (player == null) initMusicPlayer();
                    else if (!player.isPlaying()) player.start();
                    player.setVolume(1.0f, 1.0f);
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                try {
                    if (player.isPlaying()) player.stop();
                    player.release();
                    player = null;
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                try {
                    if (player.isPlaying()) player.pause();
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                try {
                    if (player.isPlaying()) player.setVolume(0.1f, 0.1f);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }

    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong() {
        try {
            player.reset();
            Song playSong = songs.get(songPosition);
            songName = playSong.getName();
            long curSong = playSong.getId();
            Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, curSong);
            try {
                player.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception e) {
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }
            player.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSong(int songIndex) {
        songPosition = songIndex;
    }

    public void setShuffle() {
        if(shuffle)
            shuffle = false;
        else shuffle = true;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        try {
            player.stop();
            player.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(songName)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songName);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }

    public int getPosition() {
        return player.getCurrentPosition();
    }

    public int getDuration() {
        return player.getDuration();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void pause() {
        player.pause();
    }

    public void seek(int position) {
        player.seekTo(position);
    }

    public void start() {
        player.start();
    }

    public void playPrevious() {
        songPosition--;
        if(songPosition < 0)
            songPosition = songs.size() - 1;
        playSong();
    }

    public void playNext() { //CODE TO IMPLEMENT BETTER SHUFFLE METHODS GOES HERE
        if(shuffle) {
            int newSong = songPosition;
            while(newSong == songPosition)
                newSong = rand.nextInt(songs.size());
            songPosition = newSong;
        } else {
            songPosition++;
            if (songPosition >= songs.size())
                songPosition = 0;
            playSong();
        }
    }
}
