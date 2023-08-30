package com.agent.musical;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.agent.musical.models.Song;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.agent.musical.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity /*implements MediaController.MediaPlayerControl*/ {

    public static final String TAG = "MainActivity";
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
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

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}