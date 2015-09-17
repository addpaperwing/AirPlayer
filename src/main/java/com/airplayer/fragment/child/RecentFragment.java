package com.airplayer.fragment.child;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.AlbumAdapter;
import com.airplayer.fragment.child.HideTabsChildFragment;
import com.airplayer.fragment.singleitem.AlbumFragment;
import com.airplayer.model.AirModelSingleton;
import com.airplayer.model.Album;

import java.util.List;


/**
 * Created by ZiyiTsang on 15/6/4.
 */
public class RecentFragment extends HideTabsChildFragment {

    private List<Album> mActivityAlbums;
    private RecyclerView mRecyclerView;
    private int mRecentAlbumsSize;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AirModelSingleton singleton = AirModelSingleton.getInstance(getActivity());
        mActivityAlbums = singleton.getActivityAlbums();
        mRecentAlbumsSize = singleton.getRecentAlbumsSize();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mActivityAlbums.size() == 0) {
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 || position == 1 || position == mRecentAlbumsSize + 2 ?
                        manager.getSpanCount() : 1;
            }
        });
        mRecyclerView.setLayoutManager(manager);
        PlayNowAdapter adapter = new PlayNowAdapter(getActivity(), mActivityAlbums);
        adapter.setOnItemClickListener(new AirAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                FragmentTransaction ft = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, AlbumFragment.newInstance(mActivityAlbums.get(correctPosition(position))));
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }
        });
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }

    private int correctPosition(int position) {
        if (position > mRecentAlbumsSize + 2) {
            return position - 3;
        } else {
            return position - 2;
        }
    }

    private class PlayNowAdapter extends AlbumAdapter {

        public static final int TYPE_MESSAGE = 4;

        public PlayNowAdapter(Context context, List<Album> list) {
            super(context, list);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return TYPE_HEADER;
            if (position == 1) return TYPE_MESSAGE;
            if (position == mRecentAlbumsSize + 2) return TYPE_MESSAGE;
            if (position == 3 + getList().size()) return TYPE_FOOTER;
            return TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_MESSAGE) return onCreateMessageViewHolder(parent);
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public AirItemViewHolder onCreateItemViewHolder(ViewGroup parent) {
            CardView itemView = (CardView) getLayoutInflater().inflate(
                    R.layout.recycler_item_album_three_pre_line, parent, false);
            return new AlbumItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof AirAdapter.AirItemViewHolder) {
                AirItemViewHolder itemViewHolder = (AirItemViewHolder) holder;
                super.onBindItemViewHolder(itemViewHolder, correctPosition(position) + 1);
                return;
            } else if (holder instanceof MessageViewHolder) {
                onBindMessageViewHolder(holder, position);
                return;
            }
            super.onBindViewHolder(holder, position);
        }

        public MessageViewHolder onCreateMessageViewHolder(ViewGroup parent) {
            return new MessageViewHolder(getLayoutInflater().inflate(R.layout.recycler_item_message, parent, false));
        }

        public void onBindMessageViewHolder(RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder message = (MessageViewHolder) holder;
            if (position == 1) {
                message.textView.setText(R.string.title_text_recent_added);
            } else if (position == mRecentAlbumsSize + 2) {
                message.textView.setText(R.string.title_text_favour);
            }
        }

        @Override
        public AirFootViewHolder onCreateFootViewHolder(ViewGroup parent) {
            return new AirFootViewHolder(newPaddingLayout(20));
        }

        private LinearLayout newPaddingLayout(int layoutHeight) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight);
            linearLayout.setLayoutParams(params);
            return linearLayout;
        }

        @Override
        public int getItemCount() {
            return super.getItemCount() + 2;
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {

            TextView textView;

            public MessageViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.title_text);
            }
        }
    }
}
