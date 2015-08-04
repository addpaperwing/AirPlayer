package com.airplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.AlbumAdapter;
import com.airplayer.fragment.singleitem.AlbumFragment;
import com.airplayer.listener.SimpleAirScrollListener;
import com.airplayer.model.AirModelSingleton;
import com.airplayer.model.Album;

import java.util.List;


/**
 * Created by ZiyiTsang on 15/6/4.
 */
public class PlayNowFragment extends Fragment {

    private List<Album> recentAlbumList;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recentAlbumList = AirModelSingleton.getInstance(getActivity()).getRecentAlbumArrayList();
        Toolbar globalBar = ((AirMainActivity) getActivity()).getToolbar();
        globalBar.setTranslationY(0);
        globalBar.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 || position == 1 || position == 8 ? manager.getSpanCount() : 1;
            }
        });
        mRecyclerView.setLayoutManager(manager);
        PlayNowAdapter adapter = new PlayNowAdapter(getActivity(), recentAlbumList);
        adapter.setOnItemClickListener(new AirAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                FragmentTransaction ft = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, AlbumFragment.newInstance(recentAlbumList.get(position - 1)));
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }
        });
        mRecyclerView.setAdapter(adapter);

        final Toolbar toolbar = ((AirMainActivity) getActivity()).getToolbar();
        toolbar.setVisibility(View.VISIBLE);
        mRecyclerView.setOnScrollListener(new SimpleAirScrollListener(getResources().getInteger(R.integer.padding_action_bar), toolbar));
        return rootView;
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
            if (position == 8) return TYPE_MESSAGE;
            if (position == 3 + getList().size()) return TYPE_FOOTER;
            return TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_MESSAGE) return onCreateMessageViewHolder(parent);
            return super.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof AirAdapter.AirItemViewHolder) {
                AirItemViewHolder itemViewHolder = (AirItemViewHolder) holder;
                super.onBindItemViewHolder(itemViewHolder, position - 1);
                return;
            } else if (holder instanceof MessageViewHolder) {
                onBindMessageViewHolder(holder, position);
                return;
            }
            super.onBindViewHolder(holder, position);
        }

        @Override
        public AirHeadViewHolder onCreateHeadViewHolder(ViewGroup parent) {
            return new AirHeadViewHolder(getLayoutInflater()
                    .inflate(R.layout.recycler_header_padding_actionbar, parent, false));
        }

        public MessageViewHolder onCreateMessageViewHolder(ViewGroup parent) {
            return new MessageViewHolder(getLayoutInflater().inflate(R.layout.recycler_item_message, parent, false));
        }

        public void onBindMessageViewHolder(RecyclerView.ViewHolder holder, int position) {
            MessageViewHolder message = (MessageViewHolder) holder;
            if (position == 1) {
                message.textView.setText(R.string.title_text_recent_added);
            } else if (position == 8) {
                message.textView.setText(R.string.title_text_favour);
            }
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
