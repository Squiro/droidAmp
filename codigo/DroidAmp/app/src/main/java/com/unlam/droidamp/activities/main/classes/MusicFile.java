package com.unlam.droidamp.activities.main.classes;

public class MusicFile {

    private long id;
    private String title;
    private String artist;
    private String data;
    private String albumKey;

    public long getID() {return id;}
    public String getTitle() {return title;}
    public String getArtist() {return artist;}
    // public long getAlbumID() {return alid;}
    public String getPath() {return data;}
    public String getAlbumKey() {return albumKey;}

    public MusicFile(long id, String title, String artist, String data, String albumKey)
    {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.data = data;
        this.albumKey = albumKey;
    }


}
