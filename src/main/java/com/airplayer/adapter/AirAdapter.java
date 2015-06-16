package com.airplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.airplayer.R;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public abstract class AirAdapter<T> extends RecyclerView.Adapter<AirAdapter.AirViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private LayoutInflater mLayoutInflater;

    private Context mContext;

    private List<T> mList;

    private ClickListener mClickListener;

    // constructor
    public AirAdapter(Context context, List<T> list) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mList = list;
    }

    // getter
    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    public Context getContext() {
        return mContext;
    }

    public List<T> getList() {
        return mList;
    }


    @Override
    public int getItemViewType(int position) {
        if (position == TYPE_HEADER) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size() + 1;
    }

    // set click listener method that can be called at fragment or activity
    public void setItemClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    // view holder for items
    public class AirViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private int holderType;
        private FrameLayout clickableItem;

        public AirViewHolder(View itemView, int holderType) {
            super(itemView);
            this.holderType = holderType;
            itemView.setOnClickListener(this);
            clickableItem = (FrameLayout) itemView.findViewById(R.id.clickable_item);
            if (clickableItem != null) {
                clickableItem.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            switch (holderType) {
                case AirAdapter.TYPE_HEADER:
                    mClickListener.headerClicked(v);
                    break;
                case AirAdapter.TYPE_ITEM:
                    mClickListener.itemClicked(v, getPosition());
                    break;
            }
        }
    }

    // interface to package click event
    public interface ClickListener {
        void itemClicked(View view, int position);
        void headerClicked(View view);
    }
}
