package com.airplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.model.Album;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/11.
 * an abstract class, which is used by recycler view that shows a album grid view
 * 一个以 grid view 显示专辑类型 item 的抽象类
 */
public abstract class AlbumAdapter extends AirAdapter<Album> {

    public AlbumAdapter(Context context, List<Album> list) {
        super(context, list);
    }

    /* implement onCreateItemViewHolder to set up every item view holder */
    /* 通过实现超类 onCreateItemViewHolder 抽象方法 来设置这种 recycler view 的每一个 item */
    @Override
    public AirItemViewHolder onCreateItemViewHolder(ViewGroup parent) {
        return new AlbumItemViewHolder(getLayoutInflater()
                .inflate(R.layout.recycler_item_album, parent, false));
    }

    /* over ride onBindViewHolder to set content in every item/header view */
    /* 重写 onBindViewHolder 来设置每一个 header 或 item 显示的内容 */
    /* 由于 使用 album adapter 的 recyclerView 的item 基本不会改变，而 header 会随时改变
     * 所以把 item 的 内容配置放在这里，而 header 则通过调用超类的抽象方法实现 */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int i) {

        // when holder is a item holder
        if (holder instanceof AlbumItemViewHolder) {
            AlbumItemViewHolder albumViewHolder = (AlbumItemViewHolder) holder;
            albumViewHolder.textView.setText(getList().get(i - 1).getTitle());
            albumViewHolder.artistText.setText(getList().get(i - 1).getAlbumArtist());

            Picasso.with(getContext()).load(getList().get(i - 1).getAlbumArtUri())
                    .into(albumViewHolder.imageView);
        }

        // when holder is a header holder
        if (holder instanceof AirAdapter.AirHeadViewHolder) {
            AirAdapter.AirHeadViewHolder albumHeaderViewHolder = (AirAdapter.AirHeadViewHolder) holder;
            setUpViewHolder(albumHeaderViewHolder);
        }
    }

    // item holder which would not change in sub class
    public class AlbumItemViewHolder extends AirItemViewHolder {
        ImageView imageView;
        TextView textView;
        TextView artistText;

        public AlbumItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.album_art);
            textView = (TextView) itemView.findViewById(R.id.album_title);
            artistText = (TextView) itemView.findViewById(R.id.album_artist_name);
        }
    }
}