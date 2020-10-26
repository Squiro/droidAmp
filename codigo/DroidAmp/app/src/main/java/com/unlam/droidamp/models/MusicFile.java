package com.unlam.droidamp.models;

public class MusicFile {

    private long id;
    private String title;
    private String data;
    private String albumID;
    private Integer track;

    public long getID() {return id;}
    public String getTitle() {return title;}
    public String getPath() {return data;}
    public String getAlbumID() {
        return albumID;
    }
    public Integer getTrack() {
        return track;
    }

    public MusicFile(long id, String title, String data, Integer track)
    {
        this.id = id;
        this.title = title;
        this.data = data;
        this.track = track;
    }


}
