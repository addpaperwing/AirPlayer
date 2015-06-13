package com.airplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.model.Album;
import com.airplayer.adapter.AlbumAdapter;
import com.airplayer.util.QueryUtils;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/5.
 */
public class ArtistActivity extends AppCompatActivity {

    private ImageView mImageView;
    private RecyclerView mRecyclerView;
    private List<Album> mList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_activity);

        Intent intent = getIntent();
        String artistName = intent.getStringExtra("artist_name");

        TextView artistNameTextView = (TextView) findViewById(R.id.activity_artist_artist_name);
        artistNameTextView.setText(artistName);

        mList = QueryUtils.loadAlbumList(this,
                "artist = ?", new String[] { artistName }, MediaStore.Audio.Albums.FIRST_YEAR);

        toolbar = (Toolbar) findViewById(R.id.artist_activity_toolbar);
        setSupportActionBar(toolbar);

        mImageView = (ImageView) findViewById(R.id.activity_artist_image);

        mRecyclerView = (RecyclerView) findViewById(R.id.artist_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(new AlbumAdapter(this, mList));
    }
}
