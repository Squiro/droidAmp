package com.unlam.droidamp.activities.main.asynctask;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.unlam.droidamp.models.MusicFile;
import com.unlam.droidamp.interfaces.MusicResolverCallback;
import java.util.ArrayList;

public class MusicResolverTask extends AsyncTask<Void, Integer, ArrayList<MusicFile>> {

    private MusicResolverCallback<ArrayList<MusicFile>> callback;
    private Context context;
    private String albumID;

    public MusicResolverTask(MusicResolverCallback<ArrayList<MusicFile>> callback, Context context, String albumID)
    {
        this.callback = callback;
        this.context = context;
        this.albumID = albumID;
    }

    /**
     * Defines work to perform on the background thread.
     */
    @Override
    protected ArrayList<MusicFile> doInBackground(Void... voids) {
        ArrayList<MusicFile> result = new ArrayList<>();

        if (!isCancelled()) {
            try {
                String where = MediaStore.Audio.Media.ALBUM_ID + "=?";
                String[] whereVal = {albumID};
                String[] projection = new String[]{MediaStore.Audio.Media._ID,  MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TRACK, MediaStore.Audio.Media.IS_MUSIC };
                Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, where, whereVal, null);
                traverseCursor(result, cursor);
                cursor = context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, projection, where, whereVal, null);
                traverseCursor(result, cursor);
            } catch(Exception e) {
                Log.i("Exception", e.toString());
            }
        }
        return result;
    }

    private void traverseCursor(ArrayList<MusicFile> result, Cursor cursor)
    {
        if (cursor != null) {
            while (cursor.moveToNext()) {

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC))) != 0)
                {
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    int track = cursor.getInt((cursor.getColumnIndex((MediaStore.Audio.Media.TRACK))));

                    result.add(new MusicFile(id, title, data, track));
                }
            }
            cursor.close();
        }
    }

    /**
     * Updates the MusicResolverCallback with the result.
     */
    @Override
    protected void onPostExecute(ArrayList<MusicFile> result) {
        this.context = null;
        if (result != null && callback != null)
        {
            callback.updateFromMusicResolver(result);
            callback.finishRequest();
        }
    }

    /**
     * Override to add special behavior for cancelled AsyncTask.
     */
    @Override
    protected void onCancelled() {
        callback.finishRequest();
    }
}
