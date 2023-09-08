package com.agent.musical;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.agent.musical.model.Song;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity {
    TextView songPrimary, songSecondary, currentTime, totalTime;
    SeekBar seekBar;
    ImageView playPause, nextButton, prevButton, albumArt;
    ArrayList<Song> songList;
    Song currentSong;

    MediaPlayer mediaPlayer = MusicalService.getMediaPlayer();


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

        // songPrimary.setSelected(true);

        songList = (ArrayList<Song>) getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTime.setText(TimeHelpers.getDurationAsText(mediaPlayer.getCurrentPosition(), getApplicationContext().getResources().getConfiguration().getLocales().get(0)));

                    if(mediaPlayer.isPlaying()){
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
        currentSong = songList.get(MusicalService.currentSongPosition);

        songPrimary.setText(currentSong.getName());
        songSecondary.setText(String.format("%s - %s", currentSong.getArtist(), currentSong.getAlbum()));

        totalTime.setText(TimeHelpers.getDurationAsText(currentSong.getDuration(), getApplicationContext().getResources().getConfiguration().getLocales().get(0)));

        playPause.setOnClickListener(v-> playPause());
        nextButton.setOnClickListener(v-> playNextSong());
        prevButton.setOnClickListener(v-> playPreviousSong());

        playMusic();
    }

    private void playMusic(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getUri());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playNextSong() {
        if(MusicalService.currentSongPosition == songList.size()-1) {
            MusicalService.currentSongPosition = 0;
        } else {
            MusicalService.currentSongPosition += 1;
        }
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void playPreviousSong() {
        if(MusicalService.currentSongPosition == 0) {
            MusicalService.currentSongPosition = songList.size()-1;
        } else {
            MusicalService.currentSongPosition -=1;
        }
        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void playPause() {
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }

}
