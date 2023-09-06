package com.agent.musical.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agent.musical.MainActivity;
import com.agent.musical.R;
import com.agent.musical.model.Song;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private ArrayList<Song> songs;
    private Context context;

    public SongAdapter(Context c, ArrayList<Song> songs) {
        this.songs = songs;
        this.context = c;
    }

    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_view, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.setSong(songs.get(position));

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        public void setSong(Song song) {
            this.song = song;
            this.nameView.setText(song.getName());
            this.durationView.setText(song.getDurationAsText(context.getResources().getConfiguration().getLocales().get(0)));
            this.artistView.setText(song.getArtist());

            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, song.getAlbumId());
            // Glide.with(context).load(uri).asBitmap().into(albumView);
        }

        @Override
        public void onClick(View v) {
            // ((MainActivity) context).songPicked(songs, this.getAdapterPosition());
            ((MainActivity) context).setCurrentSongList(songs);
        }
    }
}
