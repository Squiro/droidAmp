package com.unlam.droidamp.activities.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unlam.droidamp.R;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // UI Elements
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // Audio files
    private ArrayList<MusicFile> musicFiles;

    private static String[] myDataset = new String []{"Hola", "2", "3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ----- OTHER -----
        musicFiles = new ArrayList();
        getMusicInfo();
        Log.i("Log", musicFiles.toString());
        // ----- RECYCLER VIEW -----
        recyclerView = findViewById(R.id.rvMusicFiles);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MediaAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);
    }

    public void getMusicInfo()
    {
        try {

            Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media.DISPLAY_NAME}, null, null, null);

            if (cursor != null)
            {
                while(cursor.moveToNext())
                {
                    //String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));

                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    // int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String albumkey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY));

                    musicFiles.add(new MusicFile(id, title, artist, data, albumkey));
                }

                cursor.close();
            }
        } catch (Exception e)
        {
            Log.i("Exception", e.toString());
        }

    }


}
