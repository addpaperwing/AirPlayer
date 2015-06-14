package com.airplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.fragment.AlbumFragment;
import com.airplayer.model.Album;
import com.airplayer.util.ImageUtils;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/11.
 */


public class AlbumAdapter extends AirAdapter<Album> {

    public AlbumAdapter(Context context, List<Album> list) {
        super(context, list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlbumViewHolder(getLayoutInflater().inflate(R.layout.recycler_album_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        AlbumViewHolder albumViewHolder = (AlbumViewHolder) viewHolder;
        albumViewHolder.imageView.setImageBitmap(ImageUtils.getListItemThumbnail(getList().get(i).getAlbumArt()));
        albumViewHolder.textView.setText(getList().get(i).getTitle());
        albumViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), AlbumActivity.class);
//                intent.putExtra("album_title", getList().get(i).getTitle());
//                getContext().startActivity(intent);
                FragmentTransaction ft = ((AirMainActivity) getContext())
                        .getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, AlbumFragment.newInstance(getList().get(i)));
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });
    }


    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        CardView cardView;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.album_art);
            textView = (TextView) itemView.findViewById(R.id.album_title);
            cardView = (CardView) itemView.findViewById(R.id.album_item);
        }
    }
}