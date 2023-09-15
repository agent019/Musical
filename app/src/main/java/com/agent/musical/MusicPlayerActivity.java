package com.agent.musical;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;

import com.agent.musical.model.Song;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity {
    TextView songPrimary, songSecondary, currentTime, totalTime;
    SeekBar seekBar;
    ImageView playPause, nextButton, prevButton, albumArt;
    ArrayList<Song> songList;
    int currentSongIndex;
    Song currentSong;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_music_player);

        songPrimary = findViewById(R.id.song_primary);
        songSecondary = findViewById(R.id.song_secondary);
        currentTime = findViewById(R.id.current_time);
        totalTime = findViewById(R.id.total_time);

        seekBar = findViewById(R.id.seek_bar);

        playPause = findViewById(R.id.play_pause);
        nextButton = findViewById(R.id.next);
        prevButton = findViewById(R.id.previous);
        albumArt = findViewById(R.id.album_art);

        songList = (ArrayList<Song>) getIntent().getSerializableExtra("LIST");
        currentSongIndex = (int) getIntent().getSerializableExtra("SONG_POSITION");
        currentSong = songList.get(currentSongIndex);

        setResourcesWithMusic();


        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(player != null){
                    long curTime = player.getCurrentPosition();
                    long totalTime = player.getDuration();
                    seekBar.setProgress((int) ((curTime * 100)/ totalTime));
                    currentTime.setText(TimeHelpers.getDurationAsText(player.getCurrentPosition(), getApplicationContext().getResources().getConfiguration().getLocales().get(0)));

                    if(player.isPlaying()){
                        playPause.setImageResource(R.drawable.pause);
                    }else{
                        playPause.setImageResource(R.drawable.play_arrow);
                    }

                }
                new Handler().postDelayed(this,100);
            }
        });
    }

    public void setResourcesWithMusic() {
        songPrimary.setText(currentSong.getName());
        songSecondary.setText(String.format("%s - %s", currentSong.getArtist(), currentSong.getAlbum()));

        totalTime.setText(TimeHelpers.getDurationAsText(currentSong.getDuration(), getApplicationContext().getResources().getConfiguration().getLocales().get(0)));

        playPause.setOnClickListener(v -> playPause());
        nextButton.setOnClickListener(v -> playNextSong());
        prevButton.setOnClickListener(v -> playPreviousSong());
    }

    private void playAudio(String mediaUri) {
        MediaItem mediaItem = MediaItem.fromUri(mediaUri);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void playPause() {
        if(player.isPlaying()) {
            player.pause();
        } else {
            player.play();
        }
    }

    private void playNextSong() {
        if (currentSongIndex == songList.size() - 1) {
            currentSongIndex = 0;
        } else {
            currentSongIndex += 1;
        }
        currentSong = songList.get(currentSongIndex);
        playAudio(currentSong.getUri());
        setResourcesWithMusic();
    }

    private void playPreviousSong() {
        if (currentSongIndex == 0) {
            currentSongIndex= songList.size() - 1;
        } else {
            currentSongIndex -= 1;
        }
        currentSong = songList.get(currentSongIndex);
        playAudio(currentSong.getUri());
        setResourcesWithMusic();
    }
}