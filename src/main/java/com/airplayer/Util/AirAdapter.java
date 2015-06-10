package com.airplayer.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.model.Music;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public abstract class AirAdapter extends RecyclerView.Adapter<AirAdapter.AirRecyclerViewHolder> {

    private LayoutInflater mLayoutInflater;

    private List<Music> mList;

    public AirAdapter(Context context, List<Music> list) {
        mLayoutInflater = LayoutInflater.from(context);
        mList = list;
    }

    @Override
    public AirRecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new AirRecyclerViewHolder(getView(mLayoutInflater, viewGroup), getTextViewId(), getImageViewId());
    }

    @Override
    public void onBindViewHolder(AirRecyclerViewHolder airRecyclerViewHolder, final int i) {
        airRecyclerViewHolder.textView.setText(getText(i));
        airRecyclerViewHolder.imageView.setImageBitmap(ImageUtils.getListItemThumbnail(getImagePath(i)));
        airRecyclerViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public static class AirRecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;
        LinearLayout linearLayout;

        public AirRecyclerViewHolder(View itemView, int...ids) {
            super(itemView);
            textView = (TextView) itemView.findViewById(ids[0]);
            imageView = (ImageView) itemView.findViewById(ids[1]);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.item);
        }
    }

    public abstract View getView(LayoutInflater layoutInflater, ViewGroup viewGroup);

    public abstract String getText(int position);

    public abstract String getImagePath(int position);

    public abstract int getTextViewId();

    public abstract int getImageViewId();

    public abstract void onItemClick(int position);
}
