package com.unlam.droidamp.activities.album;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.unlam.droidamp.R;
import com.unlam.droidamp.activities.album.fragments.AlbumResolverFragment;
import com.unlam.droidamp.activities.base.BaseActivity;
import com.unlam.droidamp.activities.main.MainActivity;
import com.unlam.droidamp.interfaces.BtnListener;
import com.unlam.droidamp.interfaces.MusicResolverCallback;
import com.unlam.droidamp.models.Album;
import com.unlam.droidamp.models.event.Event;

import java.util.ArrayList;

public class AlbumActivity extends BaseActivity implements MusicResolverCallback<ArrayList<Album>> {

    // UI Elements
    private ProgressBar pgBarAlbums;
    private RecyclerView rvAlbums;
    private RecyclerView.Adapter mAlbumAdapter;
    private AlbumResolverFragment albumResolverFragment;

    private ArrayList<Album> albumList;
    public static final String ALBUM_KEY = "album";
    public static final String ALBUM_ID_KEY = "album_id";
    public static final String ARTIST_KEY = "artist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        pgBarAlbums = findViewById(R.id.pgBarAlbums);
        albumResolverFragment = AlbumResolverFragment.getInstance(getSupportFragmentManager());
        // AlbumResolverFragment starts its background task onAttach, so... we better fire this event right now
        this.networkEventFragment.startEventTask(new Event(Event.TYPE_BACKGROUND, Event.DESCRIPTION_BACKGROUND), auth);
        setUpRecyclerView();
    }

    public void setUpRecyclerView()
    {
        // ----- RECYCLER VIEW FOR Albums -----
        albumList = new ArrayList<>();

        rvAlbums = findViewById(R.id.rvAlbums);
        rvAlbums.setHasFixedSize(true);
        rvAlbums.setLayoutManager(new LinearLayoutManager(this));
        mAlbumAdapter = new AlbumAdapter(albumList, new BtnListener() {
            @Override
            public void onClick(View v, int position) {
                playAlbum(position);
            }
        });
        rvAlbums.setAdapter(mAlbumAdapter);
    }

    public void playAlbum(int position)
    {
        Intent activity = new Intent(this, MainActivity.class);
        activity.putExtra(ALBUM_ID_KEY, albumList.get(position).getId());
        activity.putExtra(ALBUM_KEY, albumList.get(position).getAlbum());
        activity.putExtra(ARTIST_KEY, albumList.get(position).getArtist());
        startActivity(activity);
        //this.finish();
    }

    @Override
    public void updateFromMusicResolver(ArrayList<Album> result) {
        this.pgBarAlbums.setVisibility(View.INVISIBLE);
        this.albumList =  result;
        mAlbumAdapter = new AlbumAdapter(albumList, new BtnListener() {
            // OnClick handler for the music files
            @Override
            public void onClick(View v, int position) {
                playAlbum(position);
            }
        });
        rvAlbums.setAdapter(mAlbumAdapter);
        mAlbumAdapter.notifyDataSetChanged();
    }

    @Override
    public void finishRequest() {
        if (albumResolverFragment != null) {
            albumResolverFragment.cancelTask();
        }
    }
}
