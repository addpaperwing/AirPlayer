package com.airplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;
import com.airplayer.util.ImageUtils;
import com.airplayer.util.QueryUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/11.
 */

public class SongAdapter extends AirAdapter<Song> {

    private PlayMusicService.PlayerControlBinder mBinder;

    private Context mContext;

    private ImageView mImageView;

    public SongAdapter(Context context, List<Song> list) {
        super(context, list);
        mContext = context;
    }

    public SongAdapter(Context context, List<Song> list, PlayMusicService.PlayerControlBinder binder) {
        this(context, list);
        mBinder = binder;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongViewHolder(getLayoutInflater().inflate(R.layout.recycler_song_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        SongViewHolder songViewHolder = (SongViewHolder) viewHolder;

        songViewHolder.imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        mImageView = songViewHolder.imageView;
        mImageView.setTag(getList().get(position).getAlbum());


        songViewHolder.titleText.setText(getList().get(position).getTitle());
        songViewHolder.artistText.setText(getList().get(position).getArtist());
        songViewHolder.durationText.setText(formatTime(getList().get(position).getDuration()));
        songViewHolder.listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinder.playMusic(position, getList());
            }
        });
        new FindImageTask().execute(getList().get(position).getAlbum());
    }

    public class FindImageTask extends AsyncTask<String, Void, Bitmap> {

        String albumTitle;

        @Override
        protected Bitmap doInBackground(String... params) {
            albumTitle = params[0];
            return ImageUtils.getListItemThumbnail(QueryUtils.getAlbumArtPath(mContext, albumTitle));
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (albumTitle.equals(mImageView.getTag())) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView artistText;
        TextView durationText;
        Toolbar listItem;

        public SongViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.song_imageView);
            titleText = (TextView) itemView.findViewById(R.id.song_title);
            artistText = (TextView) itemView.findViewById(R.id.song_artist_name);
            durationText = (TextView) itemView.findViewById(R.id.song_duration);
            listItem = (Toolbar) itemView.findViewById(R.id.song_item);
        }
    }

    private String formatTime(int duration) {
        int min = 0;
        int sec;

        sec = duration / 1000;
        if(sec > 60){
            min = sec / 60;
            sec = sec % 60;
        }
        return String.format("%02d:%02d", min, sec);
    }
}

