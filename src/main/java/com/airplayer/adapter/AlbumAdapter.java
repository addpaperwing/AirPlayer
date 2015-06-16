package com.airplayer.adapter;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.fragment.AlbumFragment;
import com.airplayer.model.Album;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/11.
 */


public class AlbumAdapter extends AirAdapter<Album> {

    public AlbumAdapter(Context context, List<Album> list) {
        super(context, list);
    }

    @Override
    public AirAdapter.AirViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case AirAdapter.TYPE_HEADER:
                return new AlbumHeaderViewHolder(getLayoutInflater()
                        .inflate(R.layout.recycler_header_text, parent, false),
                        AirAdapter.TYPE_HEADER);
            case AirAdapter.TYPE_ITEM:
                return new AlbumViewHolder(getLayoutInflater()
                        .inflate(R.layout.recycler_item_album, parent, false),
                        AirAdapter.TYPE_ITEM);
            default:
                throw new RuntimeException("no type match, make sure you use types correctly. " +
                        "unmatchable viewType : " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(AirAdapter.AirViewHolder holder, final int i) {

        if (holder instanceof AlbumViewHolder) {
            AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
            albumViewHolder.textView.setText(getList().get(i - 1).getTitle());
            albumViewHolder.artistText.setText(getList().get(i - 1).getAlbumArtist());

            Picasso.with(getContext()).load(getList().get(i - 1).getAlbumArtUri())
                    .into(albumViewHolder.imageView);
        }

        if (holder instanceof AlbumHeaderViewHolder) {

        }
    }


    public class AlbumViewHolder extends AirAdapter.AirViewHolder {
        ImageView imageView;
        TextView textView;
        TextView artistText;

        public AlbumViewHolder(View itemView, int holderType) {
            super(itemView, holderType);
            imageView = (ImageView) itemView.findViewById(R.id.album_art);
            textView = (TextView) itemView.findViewById(R.id.album_title);
            artistText = (TextView) itemView.findViewById(R.id.album_artist_name);
        }
    }

    public class AlbumHeaderViewHolder extends AirAdapter.AirViewHolder {

        public AlbumHeaderViewHolder(View itemView, int holderType) {
            super(itemView, holderType);
        }
    }

}