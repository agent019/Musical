package com.agent.musical;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.agent.musical.model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity /*implements MediaController.MediaPlayerControl*/ {

    private FragmentManager fm;
    public static final String TAG = "MainActivity";
    private AppBarConfiguration appBarConfiguration;
    public ArrayList<Song> songList;
    public ArrayList<Song> currentSongList = null;

    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO = 0;
    public static final int ALL = -1;
    public static final int ALBUMS = 0;
    public static final int ARTISTS = 1;
    public static final int GENRES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO - Consider ViewBinding instead of calling by id here?
        // https://developer.android.com/topic/libraries/view-binding
        setContentView(R.layout.main_menu);

        fm = getSupportFragmentManager();
        Fragment frag = MainFragment.newInstance();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_layout, frag);
        fragmentTransaction.commit();

        songList = new ArrayList<Song>();
        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_AUDIO") != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Requesting permissions for audio media.");
            ActivityCompat.requestPermissions(this, new String[]{ "android.permission.READ_MEDIA_AUDIO" }, MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO);
        } else{
            Log.i(TAG, "Audio permissions already approved.");
            populateSongList();
        }

        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getName().compareTo(b.getName());
            }
        });
    }

    public ArrayList<Song> getSongList() {
        return songList;
    }

    public void setCurrentSongList(ArrayList<Song> curSongList) {
        this.currentSongList = curSongList;
    }

    public ArrayList<Song> getCurrentSongList() {
        return currentSongList;
    }

    public void swapFragment(Fragment f) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.main_layout, f);
        fragmentTransaction.addToBackStack(f.getClass().getName());
        fragmentTransaction.commit();
    }

    public void populateSongList() {
        //Retrieve song info
        ContentResolver musicResolver = getContentResolver();
        Cursor musicCursor = musicResolver.query(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
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

                    Uri uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", musicId);
                    Cursor genresCursor = musicResolver.query(uri,
                            null, null, null, null);
                    int genre_column_index = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);

                    String genre = "Unknown Genre";
                    if (genresCursor.moveToFirst()) {
                        do {
                            genre = genresCursor.getString(genre_column_index) + " ";
                        } while (genresCursor.moveToNext());
                    }
                    genresCursor.close();

                    songList.add(new Song(id, name, artist, artistId, album, albumId, genre, duration));
                }
            }
            while (musicCursor.moveToNext());
            musicCursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    populateSongList();
                } else {
                    Log.i(TAG, "length: " + grantResults.length + " and result: " + grantResults[0]);
                    // todo: snack-bar?
                    Toast.makeText(this, "Read permissions were denied! - War were declared!", Toast.LENGTH_LONG).show();
                }
            }
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(fm.getBackStackEntryCount() != 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}