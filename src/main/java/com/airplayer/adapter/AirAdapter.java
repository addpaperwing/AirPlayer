package com.airplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airplayer.R;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 * An abstract adapter for the hole app
 * packaged some method that can be reused
 * app 的全局 adapter 抽象类，封装了一些可以重用的方法，加强代码复用性
 */
public abstract class AirAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * type of item in a recycler view
     * use for switching item to display, a header or an ordinary item
     * recycler view 中每一个 item 的类型，通过这个类型决定这个 item 是一个 header 还是一个 普通的 item
     */
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private LayoutInflater mLayoutInflater;

    private Context mContext;

    private List<T> mList;

    private ClickListener mClickListener;

    /* constructor, construct and set up the field */
    public AirAdapter(Context context, List<T> list) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mList = list;
    }

    /* getter of layoutInflater, set here so that the sub class
     * can use this to get layoutInflater that set in constructor */
    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    /* getter of context, set here so that the sub class
     * can use this method to get context that set in constructor */
    public Context getContext() {
        return mContext;
    }

    /* getter of list, set here so that the sub class
     * can use this method to get list that set in constructor  */
    public List<T> getList() {
        return mList;
    }

    /**
     * return the type of item by position
     * the return value will be use in the { @link onCreateViewHolder } method
     * @param position of item in the recycler view, 0 is a header, others are ordinary item
     * @return the view type which is set as field
     *
     * 根据每一个 item 的 position 决定返回的 item 属于哪种类型 ( header 还是 普通的 item )
     * 返回值会在 { @link onCreateViewHolder } 中用来决定返回的 viewHolder 对象
     * 参数: position 每一个 item 在 recycler view 中所处的位置, 0 为 header, 其他为 普通的item
     * 返回值: 在类的常量中设置好的 view 类型的值
     */
    @Override
    public int getItemViewType(int position) {
        if (position == TYPE_HEADER) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    /**
     * According to the viewType that return from { @link getItemViewType }
     * switch the create view method to call
     * @param parent view group of the sub layout, here is a recycler view
     * @param viewType this value returns from { @link getItemViewType }
     * @return two abstract method which have to implements in sub class to create different view
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return onCreateHeadViewHolder(parent);
            case TYPE_ITEM:
                return onCreateItemViewHolder(parent);
            default:
                throw new RuntimeException("no type match, make sure you use types correctly. " +
                        "unmatchable viewType : " + viewType);
        }
    }

    /**
     * create an ordinary item view
     * @param parent view group of the sub layout, here is a recycler view
     * @return an ordinary item view holder
     */
    public abstract AirItemViewHolder onCreateItemViewHolder(ViewGroup parent);

    /**
     * create a header item view
     * @param parent view group of the sub layout, here is a recycler view
     * @return a header item view holder
     */
    public abstract AirHeadViewHolder onCreateHeadViewHolder(ViewGroup parent);

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size() + 1;
    }


    /**
     * set click listener method that can be called at fragment or activity
     * so that the click action can use some resource that adapter can not get
     * @param clickListener class that implements the click method
     */
    public void setItemClickListener(ClickListener clickListener) {
        mClickListener = clickListener;
    }

    /**
     * view holder for ordinary items
     */
    public class AirItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private FrameLayout clickableItem;

        public AirItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            clickableItem = (FrameLayout) itemView.findViewById(R.id.clickable_item);
            if (clickableItem != null) {
                clickableItem.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
                mClickListener.itemClicked(v, getPosition());
            }
        }

    /**
     * view holder for header
     */
    public class AirHeadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public AirHeadViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.headerClicked(v);
        }
    }


    /**
     * an abstract method to set up different header
     * @param holder use for setting up the views in header
     */
    public abstract void setUpViewHolder(AirAdapter.AirHeadViewHolder holder);

    /**
     * interface of click event
     */
    public interface ClickListener {
        void itemClicked(View view, int position);
        void headerClicked(View view);
    }
}
