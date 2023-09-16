package com.agent.musical;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.agent.musical.model.Song;

import java.util.ArrayList;

public class MusicPlayerFragment extends Fragment {
    TextView songPrimary, songSecondary, currentTime, totalTime;
    SeekBar seekBar;
    ImageView playPause, nextButton, prevButton, albumArt;
    ArrayList<Song> songList;
    int currentSongIndex;
    Song currentSong;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MusicPlayerFragment.
     */
    public static MusicPlayerFragment newInstance() {
        MusicPlayerFragment fragment = new MusicPlayerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*songList = (ArrayList<Song>) getIntent().getSerializableExtra("LIST");
        currentSongIndex = (int) getIntent().getSerializableExtra("SONG_POSITION");
        currentSong = songList.get(currentSongIndex);

        setResourcesWithMusic();

        MusicPlayerFragment.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(musicalService != null){
                    seekBar.setProgress(musicalService.getCurrentPosition() / 1000);
                    currentTime.setText(TimeHelpers.getDurationAsText(musicalService.getCurrentPosition(), getApplicationContext().getResources().getConfiguration().getLocales().get(0)));

                    if(musicalService.isPlaying()){
                        playPause.setImageResource(R.drawable.pause);
                    }else{
                        playPause.setImageResource(R.drawable.play_arrow);
                    }

                }
                new Handler().postDelayed(this,100);
            }
        });*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_player, container, false);

        songPrimary = view.findViewById(R.id.song_primary);
        songSecondary = view.findViewById(R.id.song_secondary);
        currentTime = view.findViewById(R.id.current_time);
        totalTime = view.findViewById(R.id.total_time);

        seekBar = view.findViewById(R.id.seek_bar);

        playPause = view.findViewById(R.id.play_pause);
        nextButton = view.findViewById(R.id.next);
        prevButton = view.findViewById(R.id.previous);
        albumArt = view.findViewById(R.id.album_art);

        return view;
    }

    public void setResourcesWithMusic() {
        songPrimary.setText(currentSong.getName());
        songSecondary.setText(String.format("%s - %s", currentSong.getArtist(), currentSong.getAlbum()));

        // totalTime.setText(TimeHelpers.getDurationAsText(currentSong.getDuration(), getApplicationContext().getResources().getConfiguration().getLocales().get(0)));

        playPause.setOnClickListener(v -> playPause());
        nextButton.setOnClickListener(v -> playNextSong());
        prevButton.setOnClickListener(v -> playPreviousSong());
    }

    /***************************************************************************/
    private void playPause() {
        /*if(musicalService.isPlaying()){
            musicalService.pauseMedia();
        } else {
            musicalService.resumeMedia();
        }*/
    }

    private void playNextSong() {
        /*if (currentSongIndex == songList.size() - 1) {
            currentSongIndex = 0;
        } else {
            currentSongIndex += 1;
        }
        currentSong = songList.get(currentSongIndex);
        playAudio(currentSong.getUri());
        setResourcesWithMusic();*/
    }

    private void playPreviousSong() {
        /*if (currentSongIndex == 0) {
            currentSongIndex= songList.size() - 1;
        } else {
            currentSongIndex -= 1;
        }
        currentSong = songList.get(currentSongIndex);
        playAudio(currentSong.getUri());
        setResourcesWithMusic();*/
    }
}