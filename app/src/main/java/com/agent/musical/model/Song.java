package com.agent.musical.model;

import java.util.Locale;

/**
 * Created by GLaDOS on 4/19/2016.
 */
public class Song {
    private long id;
    private String name;
    private String artist;
    private String album;
    private String genre;
    private long albumId;
    private long artistId;
    private long duration;

    public Song(long id, String name, String artist, long artistId, String album, long albumId, String genre, long duration) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.albumId = albumId;
        this.artistId = artistId;
        this.duration = duration;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getArtist() {
        return this.artist;
    }

    public String getAlbum() {
        return this.album;
    }

    public String getGenre() {
        return this.genre;
    }

    public Long getAlbumId() {
        return this.albumId;
    }

    public Long getDuration() { return this.duration; }

    public String getDurationAsText(Locale curLocale) {
        //convert the song duration into string reading hours, mins seconds
        long hrs = (this.duration / 3600000);
        long mns = (this.duration / 60000) % 60000;
        long scs = (this.duration % 60000) / 1000;

        if(hrs == 0)
            return String.format(curLocale, "%02d:%02d", mns, scs);
        else
            return String.format(curLocale,"%02d:%02d:%02d", hrs,  mns, scs);
    }
}
