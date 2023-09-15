package com.agent.musical;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import com.agent.musical.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MusicalService extends MediaSessionService {
    public static final String TAG = "MusicalService";
    private MediaSession mediaSession = null;
    public ArrayList<Song> songList;

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

        populateSongList();

        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getName().compareTo(b.getName());
            }
        });
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

    public void populateSongList() {
        //Retrieve song info
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
                    long albumId = musicCursor.getLong(albumIdColumn);
                    String artist = musicCursor.getString(artistColumn);
                    long artistId = musicCursor.getLong(artistIdColumn);
                    long duration = musicCursor.getLong(durationColumn);
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
}
