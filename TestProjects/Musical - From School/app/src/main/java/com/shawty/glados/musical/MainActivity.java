package com.shawty.glados.musical;

import android.Manifest;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {
    public ArrayList<Song> songList;
    public ArrayList<Song> currentSongList = null;
    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound=false;
    private MusicController controller;
    private boolean paused = false;
    private boolean playbackPaused = false;
    private FragmentManager fm;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    public static final int ALL = -1;
    public static final int ALBUMS = 0;
    public static final int ARTISTS = 1;
    public static final int GENRES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_menu);

        fm = getFragmentManager();
        Fragment frag = MainFragment.newInstance();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_layout, frag);
        fragmentTransaction.commit();

        songList = new ArrayList<Song>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else{
            populateSongList();
        }

        Collections.sort(songList, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getName().compareTo(b.getName());
            }
        });

        setController();
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

    @Override
    public void onBackPressed() {
        if(fm.getBackStackEntryCount() != 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

private ServiceConnection musicConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
        musicService = binder.getService();
        musicService.setSongs(songList);
        musicBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicBound = false;
    }
};

    private void setController() {
        controller = new MusicController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrevious();
            }
        });
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.main_layout));
        controller.setEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        paused = true;
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(paused) {
            setController();
            paused = false;
        }
    }

    @Override
    protected void onDestroy(){
        unbindService(musicConnection);
        stopService(playIntent);
        musicService = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch(item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void songPicked(ArrayList<Song> curSongs, int position) {
        musicService.setSongs(curSongs);
        musicService.setSong(position);
        musicService.playSong();

        if(playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    public void populateSongList() {
        //Retrieve song info
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
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
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    populateSongList();
                } else {
                    Toast.makeText(this, "Read permissions were denied!", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void playNext() {
        musicService.playNext();
        if(playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    private void playPrevious() {
        musicService.playPrevious();
        if(playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    @Override
    public void start() {
        musicService.start();
    }

    @Override
    public void pause() {
        playbackPaused = true;
        musicService.pause();
    }

    @Override
    public int getDuration() {
        if(musicService != null && musicBound && musicService.isPlaying())
            return musicService.getDuration();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(musicService != null && musicBound && musicService.isPlaying())
            return musicService.getPosition();
        else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicService.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(musicService != null && musicBound)
            return musicService.isPlaying();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
