package com.unlam.droidamp.models;

public class Album {

    private String album;
    private String id;
    private String artist;

    public String getId() {
        return id;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public Album(String id, String album, String artist)
    {
        this.id = id;
        this.album = album;
        this.artist = artist;
    }
}
