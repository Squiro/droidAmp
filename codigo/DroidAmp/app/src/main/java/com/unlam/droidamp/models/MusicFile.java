package com.unlam.droidamp.models;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

public class MusicFile {

    private long id;
    private String title;
    private String data;
    private String albumID;
    private Integer track;
    private String duration;

    public long getID() {return id;}
    public String getTitle() {return title;}
    public String getPath() {return data;}
    public String getAlbumID() {
        return albumID;
    }
    public Integer getTrack() {
        return track;
    }
    public String getDuration() {
        return duration;
    }

    public MusicFile(long id, String title, String data, Integer track, Context context)
    {
        this.id = id;
        this.title = title;
        this.data = data;
        this.track = track;
        this.duration = calculateDuration(this.data, context);
    }

    public String calculateDuration(String data, Context context)
    {
        Uri uri = Uri.parse(data);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(context, uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return formateMilliSeccond((long) Integer.parseInt(durationStr));
    }

    /**
     * Function to convert milliseconds time to
     * Timer Format
     * Hours:Minutes:Seconds
     */
    public static String formateMilliSeccond(long milliseconds) {
        String finalTimerString = "";
        String secondsString;

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        //      return  String.format("%02d Min, %02d Sec",
        //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
        //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
        //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        // return timer string
        return finalTimerString;
    }


}
