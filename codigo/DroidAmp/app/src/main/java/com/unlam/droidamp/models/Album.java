package com.unlam.droidamp.models;

public class Album {

    private String album;
    private long id;
    private String artist;

    public long getId() {
        return id;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public Album(long id, String album, String artist)
    {
        this.id = id;
        this.album = album;
        this.artist = artist;
    }
}
