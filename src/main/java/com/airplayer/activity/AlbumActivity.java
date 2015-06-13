package com.airplayer.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.adapter.AlbumAdapter;
import com.airplayer.adapter.SongAdapter;
import com.airplayer.model.Album;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;
import com.airplayer.util.QueryUtils;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/12.
 */
public class AlbumActivity extends AppCompatActivity {

    public static final String TAG = "AlbumActivity";

    private ImageView mImageView;
    private RecyclerView mRecyclerView;
    private List<Song> mList;
    private Toolbar mToolbar;

    // service
    private PlayMusicService.PlayerControlBinder mBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (PlayMusicService.PlayerControlBinder) service;
            Log.d(TAG, "service has connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "service has disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_activity);

        Intent bindIntent = new Intent(this, PlayMusicService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        Intent intent = getIntent();
        String albumTitle = intent.getStringExtra("album_title");

        TextView albumTitleTextView = (TextView) findViewById(R.id.activity_artist_artist_name);
        albumTitleTextView.setText(albumTitle);

        mList = QueryUtils.loadSongList(this,
                "album = ?", new String[]{ albumTitle }, MediaStore.Audio.Media.TRACK);

        mToolbar = (Toolbar) findViewById(R.id.artist_activity_toolbar);
        setSupportActionBar(mToolbar);

        mImageView = (ImageView) findViewById(R.id.activity_artist_image);

        mRecyclerView = (RecyclerView) findViewById(R.id.artist_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new SongAdapter(this, mList, mBinder));
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }
}
