package com.unlam.droidamp.activities.album.asynctasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.unlam.droidamp.models.Album;
import com.unlam.droidamp.interfaces.MusicResolverCallback;

import java.util.ArrayList;

public class AlbumResolverTask extends AsyncTask<Void, Integer, ArrayList<Album>> {

    private MusicResolverCallback<ArrayList<Album>> callback;
    private Context context;

    public AlbumResolverTask(MusicResolverCallback<ArrayList<Album>> callback, Context context)
    {
        this.callback = callback;
        this.context = context;
    }

    /**
     * Defines work to perform on the background thread.
     */
    @Override
    protected ArrayList<Album> doInBackground(Void... voids) {
        ArrayList<Album> result = new ArrayList<>();

        if (!isCancelled()) {
            try {
                String[] projection = new String[]{MediaStore.Audio.Albums.ALBUM_ID,  MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ARTIST};
                Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, null, null, null);
                traverseCursor(result, cursor);
                cursor = context.getContentResolver().query(MediaStore.Audio.Albums.INTERNAL_CONTENT_URI, projection, null, null, null);
                traverseCursor(result, cursor);

            } catch(Exception e) {
                Log.i("Exception", e.toString());
            }
        }
        return result;
    }

    private void traverseCursor(ArrayList<Album> result, Cursor cursor)
    {
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));

                result.add(new Album(id, album, artist));
            }
            cursor.close();
        }
    }

    /**
     * Updates the MusicResolverCallback with the result.
     */
    @Override
    protected void onPostExecute(ArrayList<Album> result) {
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
