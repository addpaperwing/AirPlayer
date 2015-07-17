package com.airplayer.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.model.Song;
import com.airplayer.util.StringUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/11.
 * an abstract class, which is used by recycler view that shows a song list
 */

public class SongAdapter extends HeadPadAdapter {

    private boolean showImage = true;

    public void showIconImage(boolean showImage) {
        this.showImage = showImage;
    }

    private boolean showAnimation = false;

    public void showEQAnimation(boolean isPlayList) {
        this.showAnimation = isPlayList;
    }

    public SongAdapter(Context context, List<Song> list) {
        super(context, list);
    }

    public SongAdapter(Context context, List<?> list, int paddingHeight) {
        super(context, list, paddingHeight);
    }

    @Override
    public AirItemViewHolder onCreateItemViewHolder(ViewGroup parent) {
        return new SongItemViewHolder(
                getLayoutInflater().inflate(R.layout.recycler_item_song, parent, false));
    }

    @Override
    public void onBindItemViewHolder(AirItemViewHolder itemHolder, int position) {
        if (itemHolder instanceof SongItemViewHolder) {
            SongItemViewHolder songItemViewHolder = (SongItemViewHolder) itemHolder;

            Song item = (Song) getList().get(position - 1);

            songItemViewHolder.titleText.setText(item.getTitle());
            songItemViewHolder.artistText.setText(item.getArtist());
            songItemViewHolder.durationText.setText(StringUtils.getFormatTime(item.getDuration()));

            if (showImage) {
                Picasso.with(getContext())
                        .load(item.getAlbumArtUri())
                        .into(songItemViewHolder.imageView);
            } else {
                int track = item.getTrack();
                if (track != 0) {
                    songItemViewHolder.trackNum.setText(track % 1000 + "");
                }
            }

            if (showAnimation) {
                if (item.isPlay()) {
                    AnimationDrawable animation;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        animation = (AnimationDrawable) getContext().getDrawable(R.drawable.animation_equalizer);
                    } else {
                        animation = (AnimationDrawable) getContext().getResources()
                                .getDrawable(R.drawable.animation_equalizer);
                    }
                    songItemViewHolder.playStateImage.setImageDrawable(animation);

                    if (item.isPause()) {
                        if (animation != null) animation.stop();
                    } else {
                        if (animation != null) animation.start();
                    }
                } else {
                    songItemViewHolder.playStateImage.setImageDrawable(null);
                }
            }
        }
    }

    public class SongItemViewHolder extends AirItemViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView artistText;
        TextView durationText;
        Toolbar listItem;
        TextView trackNum;
        ImageView playStateImage;

        public SongItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.song_imageView);
            titleText = (TextView) itemView.findViewById(R.id.song_title);
            artistText = (TextView) itemView.findViewById(R.id.song_artist_name);
            durationText = (TextView) itemView.findViewById(R.id.song_duration);
            listItem = (Toolbar) itemView.findViewById(R.id.song_item);
            trackNum = (TextView) itemView.findViewById(R.id.song_track_number);
            playStateImage = (ImageView) itemView.findViewById(R.id.playing_state);
        }
    }
}

