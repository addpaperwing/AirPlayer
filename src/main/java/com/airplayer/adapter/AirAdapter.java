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
 * An abstract adapter for all adapter in app
 * packaged some method that can be reused
 * app 的全局 adapter 抽象类，封装了一些可以重用的方法，加强代码复用性
 */
public abstract class AirAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * type of item in a recycler view
     * use for switching item to display, a header, a footer or an ordinary item
     * recycler view 中每一个 item 的类型，通过这个类型决定这个 item 是一个 header, footer 还是一个 普通的 item
     */
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_FOOTER = 2;

    private LayoutInflater mLayoutInflater;

    private Context mContext;

    private List<?> mList;

    private OnItemClickListener mOnItemClickListener;
    private OnHeaderClickListener mOnHeaderClickListener;
    private OnFooterClickListener mOnFooterClickListener;

    /* constructor, construct and set up the field */
    public AirAdapter(Context context, List<?> list) {
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
    public List<?> getList() {
        return mList;
    }

    /**
     * return the type of item by position
     * the return value will be use in the { @link onCreateViewHolder } method
     * 根据每一个 item 的 position 决定返回的 item 属于哪种类型 ( header 还是 普通的 item )
     * 返回值会在 { @link onCreateViewHolder } 中用来决定返回的 viewHolder 对象
     * @param position of item in the recycler view, 0 is a header, the last one is footer, others are ordinary item
     * @return the view type which is set as field
     *
     */
    @Override
    public int getItemViewType(int position) {
        if (position == TYPE_HEADER) {
            return TYPE_HEADER;
        } else if (position == mList.size() + 1) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    /**
     * According to the viewType that return from { @link getItemViewType }
     * switch the create view method to call
     * 根据 viewType 的返回值，选择调用哪个类型的view 需要被创建
     * @param parent view group of the sub layout, here is a recycler view
     * @param viewType this value returns from { @link getItemViewType }
     * @return methods which could be over ride in sub class to create different view
     *
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                return onCreateHeadViewHolder(parent);
            case TYPE_ITEM:
                return onCreateItemViewHolder(parent);
            case TYPE_FOOTER:
                return onCreateFootViewHolder(parent);
            default:
                throw new RuntimeException("no type match, make sure you use types correctly. " +
                        "unmatchable viewType : " + viewType);
        }
    }

    /**
     * create an ordinary item view, must be implemented
     * 创建一个普通的 item view
     * @param parent view group of the sub layout, here is a recycler view
     * @return an ordinary item view holder
     *
     */
    public abstract AirAdapter.AirItemViewHolder onCreateItemViewHolder(ViewGroup parent);

    /**
     * create a header item view, if not over ride returns a empty header
     * 创建一个 header 默认为空
     * @param parent view group of the sub layout, here is a recycler view
     * @return a header item view holder
     *
     */
    public  AirAdapter.AirHeadViewHolder onCreateHeadViewHolder(ViewGroup parent) {
        return new AirHeadViewHolder(mLayoutInflater.inflate(R.layout.recycler_item_empty, parent, false));
    }

    /**
     * create a footer item view, if not over ride returns a empty footer
     * 创建一个 footer 默认为空
     * @param parent view group of the sub layout, here is a recycler view
     * @return a footer item view holder
     *
     */
    public AirAdapter.AirFootViewHolder onCreateFootViewHolder(ViewGroup parent) {
        return new AirFootViewHolder(mLayoutInflater.inflate(R.layout.recycler_item_empty, parent, false));
    }


    /**
     * According to the class of holder, switch which method to call
     * @param holder that created in OnCreateViewHolder
     * @param position that item located
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AirAdapter.AirHeadViewHolder) {
            AirAdapter.AirHeadViewHolder headHolder = (AirAdapter.AirHeadViewHolder) holder;
            onBindHeadViewHolder(headHolder);
        } else if (holder instanceof AirAdapter.AirItemViewHolder) {
            AirAdapter.AirItemViewHolder itemHolder = (AirAdapter.AirItemViewHolder) holder;
            onBindItemViewHolder(itemHolder, position);
        } else if (holder instanceof AirAdapter.AirFootViewHolder) {
            AirAdapter.AirFootViewHolder footHolder = (AirAdapter.AirFootViewHolder) holder;
            onBindFootViewHolder(footHolder);
        }
    }

    /**
     * method that set up a header which calls in method onBindViewHolder
     * 配置 header 的方法，会在 onBindViewHolder 里面调用，默认为空
     * @param headHolder past from onBindViewHolder
     *
     */
    public void onBindHeadViewHolder(AirAdapter.AirHeadViewHolder headHolder) { }

    /**
     * method that set up a footer which calls in method onBindViewHolder
     * 配置 footer 的方法，会在 onBindViewHolder 里面调用，默认为空
     * @param footHolder past from onBindViewHolder
     */
    public void onBindFootViewHolder(AirAdapter.AirFootViewHolder footHolder) { }

    /**
     * method that set up an item which calls in method onBindViewHolder
     * 配置 item 的方法，会在 onBindViewHolder 里面调用
     * @param itemHolder past from onBindViewHolder
     * @param position item located
     */
    public abstract void onBindItemViewHolder(AirAdapter.AirItemViewHolder itemHolder, int position);


    /**
     * @return header + list size + footer
     */
    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size() + 2;
    }


    /**
     * set click listener method that can be called at fragment or activity
     * so that the click action can use some resource that adapter can not get
     * @param listener class that implements the click method
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * set click listener method that can be called at fragment or activity
     * so that the click action can use some resource that adapter can not get
     * @param listener class that implements the click method
     */
    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        mOnHeaderClickListener = listener;
    }

    /**
     * set click listener method that can be called at fragment or activity
     * so that the click action can use some resource that adapter can not get
     * @param listener class that implements the click method
     */
    public void setOnFooterClickListener(OnFooterClickListener listener) {
        mOnFooterClickListener = listener;
    }

    /**
     * view holder for ordinary items
     */
    public class AirItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private FrameLayout clickableMask;

        public AirItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            clickableMask = (FrameLayout) itemView.findViewById(R.id.clickable_mask);
            if (clickableMask != null) {
                clickableMask.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
                mOnItemClickListener.onItemClicked(v, getPosition());
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
            mOnHeaderClickListener.onHeaderClicked(v);
        }
    }

    /**
     * view holder for footer
     */
    public class AirFootViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public AirFootViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnFooterClickListener.onFooterClicked(v);
        }
    }

    /**
     * interface of item click event
     */
    public interface OnItemClickListener {
        void onItemClicked(View view, int position);
    }

    /**
     * interface of header click event
     */
    public interface OnHeaderClickListener {
        void onHeaderClicked(View view);
    }

    /**
     * interface of footer click event
     */
    public interface OnFooterClickListener {
        void onFooterClicked(View view);
    }
}
