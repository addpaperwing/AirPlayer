package com.airplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.database.AirPlayerDB;
import com.airplayer.model.Music;
import com.airplayer.util.ImageUtils;
import com.airplayer.util.LibraryListAdapter;

import java.util.List;

import cn.trinea.android.common.service.impl.ImageCache;

/**
 * Created by ZiyiTsang on 15/6/5.
 */
public class ArtistActivity extends Activity {

    private ImageView mImageView;
    private ListView mListView;
    private List<Music> list;
    private AirPlayerDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        Intent intent = getIntent();
        String artistName = intent.getStringExtra("artist_name");

        TextView artistNameTextView = (TextView) findViewById(R.id.activity_artist_artist_name);
        artistNameTextView.setText(artistName);

        db = AirPlayerDB.newInstance(this);
        list = db.loadList(
                new String[] { AirPlayerDB.ALBUM, AirPlayerDB.ALBUM_ART },
                AirPlayerDB.ARTIST + " = ?",
                new String[] { artistName },
                1
        );

        mImageView = (ImageView) findViewById(R.id.activity_artist_image);

        mListView = (ListView) findViewById(R.id.activity_artist_list);
        mListView.setAdapter(new LibraryListAdapter(this, R.layout.library_list_item,
                list, LibraryListAdapter.ALBUM_LIST));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mImageView.setImageBitmap(ImageUtils.getListItemThumbnail(list.get(position).getAlbumArt()));
            }
        });
    }
}
