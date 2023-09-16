package com.agent.musical;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import com.agent.musical.model.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MusicalService
        extends Service
        implements MediaPlayer.OnCompletionListener,
            MediaPlayer.OnPreparedListener,
            MediaPlayer.OnErrorListener,
            AudioManager.OnAudioFocusChangeListener {
    public static final String TAG = "MusicalService";
    public static final String MEDIA_INTENT_TAG = "media";
    private final IBinder serviceBinder = new ServiceBinder();
    private static MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private AudioFocusRequest focusRequest;

    private List<Song> songList;

    private Song nowPlaying;
    private int pausePosition;

    public void initMediaPlayer() {
        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);
        }
        mediaPlayer.reset();

        populateSongList();

        /*try {
            mediaPlayer.setDataSource(mediaUri);
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }*/
        // mediaPlayer.prepareAsync();
    }

    public void playMedia() {
        if(mediaPlayer == null) return;
        if(!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void stopMedia() {
        if(mediaPlayer == null) return;
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void pauseMedia() {
        if(mediaPlayer == null) return;
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            pausePosition = mediaPlayer.getCurrentPosition();
        }
    }

    public void resumeMedia() {
        if(mediaPlayer == null) return;
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(pausePosition);
            mediaPlayer.start();
        }
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public List<Song> getSongList() {
        return this.songList;
    }

    public Song getCurrentSong() {
        return this.nowPlaying;
    }

    public void populateSongList() {
        //Retrieve song info
        songList = new ArrayList<Song>();
        ContentResolver musicResolver = getContentResolver();
        Cursor musicCursor = musicResolver.query(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int uriColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int artistIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int isMusicColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            //add songs to list
            do {
                if(musicCursor.getInt(isMusicColumn) != 0) {
                    String uri = musicCursor.getString(uriColumn);
                    String album = musicCursor.getString(albumColumn);
                    Long albumId = musicCursor.getLong(albumIdColumn);
                    String artist = musicCursor.getString(artistColumn);
                    Long artistId = musicCursor.getLong(artistIdColumn);
                    Long duration = musicCursor.getLong(durationColumn);
                    long id = musicCursor.getLong(idColumn);
                    String name = musicCursor.getString(titleColumn);

                    if(artist.equals("<unknown>"))
                        artist = "Unknown Artist";

                    int musicId = Integer.parseInt(musicCursor.getString(idColumn));

                    Uri genreUri = MediaStore.Audio.Genres.getContentUriForAudioId("external", musicId);
                    Cursor genresCursor = musicResolver.query(genreUri,
                            null, null, null, null);
                    int genre_column_index = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);

                    String genre = "Unknown Genre";
                    if (genresCursor.moveToFirst()) {
                        do {
                            genre = genresCursor.getString(genre_column_index) + " ";
                        } while (genresCursor.moveToNext());
                    }
                    genresCursor.close();

                    songList.add(new Song(uri, id, name, artist, artistId, album, albumId, genre, duration));
                }
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }

        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getName().compareTo(b.getName());
            }
        });

        /*songList.add(new Song("uri", 1, "Sample song A", "Artist A", 101, "Sample Album A", 1001, "Hip-Hop/Rap", 260000));
        songList.add(new Song("uri", 2, "Sample song B", "Artist A", 101, "Sample Album A", 1001, "EDM", 260000));
        songList.add(new Song("uri", 3, "Sample song C", "Artist A", 101, "Sample Album B", 1002, "Hip-Hop/Rap", 260000));
        songList.add(new Song("uri", 4, "Sample song D", "Artist B", 102, "Sample Album C", 1003, "Hip-Hop/Rap", 260000));
        songList.add(new Song("uri", 5, "Sample song E", "Artist B", 102, "Sample Album C", 1003, "Pop", 260000));
        songList.add(new Song("uri", 6, "Sample song F", "Artist B", 102, "Sample Album C", 1003, "Pop", 260000));
        songList.add(new Song("uri", 7, "Sample song G", "Artist C", 103, "Sample Album D", 1004, "Pop", 260000));
        songList.add(new Song("uri", 8, "Sample song H", "Artist D", 104, "Sample Album E", 1005, "Hip-Hop/Rap", 260000));
        songList.add(new Song("uri", 9, "Sample song I", "Artist E", 105, "Sample Album E", 1005, "EDM", 260000));
        songList.add(new Song("uri", 10, "Sample song J", "Artist E", 105, "Sample Album F", 1006, "EDM", 260000));*/
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
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        stopSelf();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
    }

    /**
     * Automatically invoked when an error occurs in media playback.
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d(TAG, "Media Error: Not valid for progressive playback - " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d(TAG, "Media Error: Server Died - " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d(TAG, "Media Error: Unknown - " + extra);
                break;
        }
        return false;
    }

    /**
     * Automatically invoked when focus changes.
     * @param focusState audio focus change state
     */
    @Override
    public void onAudioFocusChange(int focusState) {
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // Focus is given back to this service
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Focus lost for an unspecified amount of time
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Focus lost for a short time
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Focus lost for a short time but don't need to stop
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).build();
        int result = audioManager.requestAudioFocus(focusRequest);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocusRequest(focusRequest);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*try {
            loadMedia(intent.getExtras().getString(MEDIA_INTENT_TAG));
        } catch (NullPointerException e) {
            Log.e(TAG, "Media url not passed to onStartCommand", e);
            stopSelf();
        }

        if(mediaUri != null && !mediaUri.isEmpty()) {
            initMediaPlayer();
        }*/

        initMediaPlayer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();
    }
}
