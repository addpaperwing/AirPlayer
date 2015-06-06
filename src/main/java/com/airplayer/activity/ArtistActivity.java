package com.airplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.airplayer.R;
import com.airplayer.Util.ImageUtils;
import com.airplayer.Util.QueryUtils;
import com.airplayer.model.Album;
import com.airplayer.model.LibraryAdapter;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/5.
 */
public class ArtistActivity extends Activity implements LibraryAdapter.LibraryAdapterCallbacks {

    private ImageView mImageView;
    private ListView mListView;
    List<Album> albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        Intent intent = getIntent();
        String artistName = intent.getStringExtra("artist_name");
        final int artistId = intent.getIntExtra("artist_id", 0);

        TextView artistNameTextView = (TextView) findViewById(R.id.activity_artist_artist_name);
        artistNameTextView.setText(artistName);

        albums = QueryUtils.queryArtistAlbums(ArtistActivity.this, artistId);

        mImageView = (ImageView) findViewById(R.id.activity_artist_image);

        mListView = (ListView) findViewById(R.id.activity_artist_list);
        mListView.setAdapter(new LibraryAdapter<Album>(this, R.layout.library_list_item, albums, this));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public String onGetTitle(int position) {
        return albums.get(position).getTitle();
    }

    @Override
    public Bitmap onGetImage(int position) {
        return ImageUtils.getAlbumArt(albums.get(position).getArtPath());
    }
}
