package com.agent.musical.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agent.musical.MusicalService;
import com.agent.musical.R;
import com.agent.musical.TimeHelpers;
import com.agent.musical.model.Song;

import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder> {
    private List<Song> songsList;
    private Context context;

    public SongListAdapter(Context c, List<Song> songsList) {
        this.songsList = songsList;
        this.context = c;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_view, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.setSong(songsList.get(position));

        holder.itemView.setOnClickListener(v -> {
            Song clickedSong = songsList.get(holder.getBindingAdapterPosition());
            Intent intent = new Intent();
            intent.setAction(MusicalService.PLAY_AUDIO_ACTION_NAME);
            intent.putExtra(MusicalService.ID_TAG, clickedSong.getId());
            context.sendBroadcast(intent);
        });
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder {
        private Song song;
        private TextView nameView;
        private TextView artistView;
        private TextView durationView;
        private ImageView albumView;

        public SongViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.song_name);
            artistView = (TextView) itemView.findViewById(R.id.song_artist);
            durationView = (TextView) itemView.findViewById(R.id.song_duration);
            albumView = (ImageView) itemView.findViewById(R.id.album_art);
        }

        public void setSong(Song song) {
            this.song = song;
            this.nameView.setText(song.getName());
            this.durationView.setText(TimeHelpers.getDurationAsText(song.getDuration(), context.getResources().getConfiguration().getLocales().get(0)));
            this.artistView.setText(song.getArtist());

            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, song.getAlbumId());
            // Glide.with(context).load(uri).asBitmap().into(albumView);
        }
    }
}
