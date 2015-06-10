package com.airplayer.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.model.Music;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public class SongRecyclerAdapter extends RecyclerView.Adapter<SongRecyclerAdapter.SongViewHolder> {

    private LayoutInflater mLayoutInflater;

    private List<Music> mList;

    public SongRecyclerAdapter(Context context, List<Music> list) {
        mLayoutInflater = LayoutInflater.from(context);
        mList = list;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new SongViewHolder(mLayoutInflater.inflate(R.layout.recycler_song_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(SongViewHolder songViewHolder, int i) {
        songViewHolder.titleText.setText(mList.get(i).getTitle());
        songViewHolder.albumText.setText(mList.get(i).getAlbum());
        songViewHolder.artistText.setText(mList.get(i).getArtist());
        songViewHolder.durationText.setText(String.valueOf(mList.get(i).getDuration()));
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView albumText;
        TextView artistText;
        TextView durationText;

        public SongViewHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.song_title);
            albumText = (TextView) itemView.findViewById(R.id.song_album_title);
            artistText = (TextView) itemView.findViewById(R.id.song_artist_name);
            durationText = (TextView) itemView.findViewById(R.id.song_duration);
        }
    }
}
