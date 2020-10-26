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
    private String album;

    public MusicResolverTask(MusicResolverCallback<ArrayList<MusicFile>> callback, Context context, String album)
    {
        this.callback = callback;
        this.context = context;
        this.album = album;
    }

    /**
     * Defines work to perform on the background thread.
     */
    @Override
    protected ArrayList<MusicFile> doInBackground(Void... voids) {
        ArrayList<MusicFile> result = new ArrayList<>();

        if (!isCancelled()) {
            try {
                String where = MediaStore.Audio.Media.ALBUM + "=?";
                String whereVal[] = {album};

                String[] projection = new String[]{MediaStore.Audio.Media._ID,  MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_KEY};
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
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                //int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                String albumkey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY));

                result.add(new MusicFile(id, title, data, albumkey));
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
