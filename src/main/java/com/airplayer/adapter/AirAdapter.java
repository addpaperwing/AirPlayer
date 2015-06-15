package com.airplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public abstract class AirAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private LayoutInflater mLayoutInflater;

    private Context mContext;

    private List<T> mList;

    public AirAdapter(Context context, List<T> list) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mList = list;
    }

    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == TYPE_HEADER) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    public List<T> getList() {
        return mList;
    }

    @Override
    public abstract void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i);

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size() + 1;
    }
}
