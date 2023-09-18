package com.agent.musical;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.agent.musical.model.Song;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fm;
    public static final String TAG = "MainActivity";

    private MusicalService musicalService;
    private boolean serviceBound = false;

    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO = 0;
    public static final int ALL = -1;
    public static final int ALBUMS = 0;
    public static final int ARTISTS = 1;
    public static final int GENRES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        // TODO - Consider ViewBinding instead of calling by id here?
        // https://developer.android.com/topic/libraries/view-binding
        setContentView(R.layout.main_layout);

        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_AUDIO") != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Requesting permissions for audio media.");
            ActivityCompat.requestPermissions(this, new String[]{ "android.permission.READ_MEDIA_AUDIO" }, MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO);
        } else{
            Log.i(TAG, "Audio permissions already approved.");
            setupService();
        }
    }

    public void setupService() {
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MusicalService.class);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            Log.i(TAG, "Service already bound!");
            /*musicalService.stopMedia();
            musicalService.loadMedia(mediaUri);
            musicalService.initMediaPlayer();*/
        }
    }

    public List<Song> getSongList() {
        if(serviceBound) {
            return musicalService.getSongList();
        } else {
            Log.d(TAG, "Tried to get song list when no service connection was established!");
            return null;
        }
    }

    public Song getCurrentSong() {
        if(serviceBound) {
            return musicalService.getCurrentSong();
        } else {
            Log.d(TAG, "Tried to get song list when no service connection was established!");
            return null;
        }
    }

    public boolean isPlaying() {
        return musicalService.isPlaying();
    }

    public void play() {
        musicalService.playMedia();
    }

    public void pause() {
        musicalService.pauseMedia();
    }

    public void stop() {
        musicalService.stopMedia();
    }

    public void playNext() {
        // TODO;
    }

    public void playPrev() {
        // TODO;
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String action = intent.getAction();
            if (action.equals(MusicalService.PLAY_AUDIO_ACTION_NAME)) {
                long songId = intent.getLongExtra(MusicalService.ID_TAG, -1);
                if(songId != -1) {
                    Song toPlay = musicalService.getSongList().stream().filter(x -> x.getId() == songId).findFirst().orElse(null);
                    if (toPlay != null) {
                        // TODO: create and switch to new fragment with current song playing
                    } else {
                        Log.d(TAG, "Couldn't find song with id " + songId + " in song list.");
                    }
                } else {
                    Log.d(TAG, "Song id not included in intent to play song.");
                }
            }
        }
    };


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicalService.ServiceBinder binder = (MusicalService.ServiceBinder) service;
            musicalService = binder.getService();
            serviceBound = true;

            // It's probably a bad idea to load UI after a service has done background processing
            fm = getSupportFragmentManager();
            Fragment frag = SongListFragment.newInstance(ALL, null);
            fm.beginTransaction()
                    .add(R.id.main_layout, frag)
                    .setReorderingAllowed(true)
                    .commit();

            Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    public void swapFragment(Fragment f) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.main_layout, f);
        fragmentTransaction.addToBackStack(f.getClass().getName());
        fragmentTransaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupService();
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            musicalService.stopSelf();
        }
    }
}