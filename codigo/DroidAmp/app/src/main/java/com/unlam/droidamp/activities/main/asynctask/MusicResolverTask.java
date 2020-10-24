package com.unlam.droidamp.activities.main.asynctask;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.unlam.droidamp.activities.main.classes.MusicFile;
import com.unlam.droidamp.interfaces.MusicResolverCallback;
import java.util.ArrayList;

public class MusicResolverTask extends AsyncTask<Void, Integer, ArrayList<MusicFile>> {

    private MusicResolverCallback<ArrayList<MusicFile>> callback;
    private Context context;

    public MusicResolverTask(MusicResolverCallback<ArrayList<MusicFile>> callback, Context context)
    {
        this.callback = callback;
        this.context = context;
    }

    /**
     * Defines work to perform on the background thread.
     */
    @Override
    protected ArrayList<MusicFile> doInBackground(Void... voids) {
        ArrayList<MusicFile> result = new ArrayList<>();

        if (!isCancelled()) {
            try {
                String[] projection = new String[]{MediaStore.Audio.Media._ID,  MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_KEY};
                Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

                if (cursor != null)
                {
                    while(cursor.moveToNext())
                    {
                        long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                        // int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                        String albumkey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY));

                        result.add(new MusicFile(id, title, data, albumkey));
                    }

                    cursor.close();
                }
            } catch(Exception e) {
                Log.i("Exception", e.toString());
            }
        }
        return result;
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
