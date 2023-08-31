package com.shawty.glados.musical;

import android.content.ContentUris;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private ArrayList<Song> songs;
    private Context context;

    public SongAdapter(Context c, ArrayList<Song> songs) {
        this.songs = songs;
        this.context = c;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        private ImageView albumView;

        public SongViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.song_name);
            artistView = (TextView) itemView.findViewById(R.id.song_artist);
            albumView = (ImageView) itemView.findViewById(R.id.albumArt);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        public void setSong(Song song) {
            this.song = song;
            this.nameView.setText(song.getName());
            this.artistView.setText(song.getArtist());

            final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri uri = ContentUris.withAppendedId(sArtworkUri, song.getAlbumId());
            Glide.with(context).load(uri).asBitmap().into(albumView);
        }

        @Override
        public void onClick(View v) {
            ((MainActivity) context).songPicked(songs, this.getAdapterPosition());
            ((MainActivity) context).setCurrentSongList(songs);
        }
    }
}
