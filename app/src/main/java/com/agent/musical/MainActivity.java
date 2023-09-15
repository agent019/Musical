package com.agent.musical;

import android.content.ComponentName;
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
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.navigation.ui.AppBarConfiguration;

import com.agent.musical.model.Song;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fm;
    public static final String TAG = "MainActivity";
    private MediaController player;
    private ListenableFuture<MediaController> mediaControllerFuture;

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
        setContentView(R.layout.main_menu);

        fm = getSupportFragmentManager();
        Fragment frag = MainFragment.newInstance();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.main_layout, frag);
        fragmentTransaction.commit();

        if (ContextCompat.checkSelfPermission(this, "android.permission.READ_MEDIA_AUDIO") != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Requesting permissions for audio media.");
            ActivityCompat.requestPermissions(this, new String[]{ "android.permission.READ_MEDIA_AUDIO" }, MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO);
        } else{
            Log.i(TAG, "Audio permissions already approved.");
            startMediaSessionService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMediaSessionService();
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MediaController.releaseFuture(mediaControllerFuture);
    }

    public void startMediaSessionService() {
        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, MusicalService.class));

        mediaControllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();

        mediaControllerFuture.addListener(() -> {
            try {
                this.player = mediaControllerFuture.get();
            } catch (Exception e) {
                return;
            }
        }, MoreExecutors.directExecutor());
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
}