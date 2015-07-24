package com.airplayer.fragment.child;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.airplayer.activity.AirMainActivity;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.SongAdapter;
import com.airplayer.model.AirModelSingleton;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public class SongListFragment extends MyLibraryChildFragment {


    private List<Song> mList;

    private PlayMusicService.PlayerControlBinder mBinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinder = ((AirMainActivity) getParentFragment().getActivity()).getPlayerControlBinder();
        mList = AirModelSingleton.getInstance(getParentFragment().getActivity())
                .getSongArrayList(null, null, MediaStore.Audio.Media.TITLE);
    }

    @Override
    public void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getParentFragment().getActivity()));
        SongAdapter adapter = new SongAdapter(getParentFragment().getActivity(), mList);

        adapter.setOnItemClickListener(new AirAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                mBinder.playMusic(position - 1, mList);
            }

//            @Override
//            public void headerClicked(View view) {
//                List<Song> listOrdered = QueryUtils.loadSongList(
//                        getParentFragment().getActivity(), null, null, MediaStore.Audio.Media.TITLE
//                );
//                ArrayList<Song> listShuffled = new ArrayList<Song>();
//                do {
//                    int shuffle = (int) Math.round(Math.random() * (listOrdered.size() - 1));
//                    listShuffled.add(listOrdered.get(shuffle));
//                    listOrdered.remove(shuffle);
//                } while (listOrdered.size() > 0);
//                mBinder.playMusic(0, listShuffled);
//            }
        });
        recyclerView.setAdapter(adapter);
    }

//    public class SongListAdapter extends SongAdapter {
//        public SongListAdapter(Context context, List<Song> list) {
//            super(context, list);
//        }
//
//        @Override
//        public SongListHeadViewHolder onCreateHeadViewHolder(ViewGroup parent) {
//            return new SongListHeadViewHolder(getLayoutInflater()
//                    .inflate(R.layout.recycler_header_image_fab, parent, false));
//        }
//
//        @Override
//        public void onBindHeadViewHolder(AirAdapter.AirHeadViewHolder holder) {
//            SongListHeadViewHolder header = (SongListHeadViewHolder) holder;
//            header.image.setMinimumHeight(getContext().getResources().getInteger(R.integer.padding_tabs) + getContext().getResources().getInteger(R.integer.padding_action_bar));
//            header.image.setMaxHeight(getContext().getResources().getInteger(R.integer.padding_tabs) + getContext().getResources().getInteger(R.integer.padding_action_bar));
//            header.title.setText("My Songs");
//            header.subTitle.setText(mList.size() + " songs");
//
//            int minYear = 2015;
//            int maxYear = 1970;
//            for (int i = 0; i < getList().size(); i++) {
//                int year = ((Song)getList().get(i)).getYear();
//                if (year < minYear && year != 0) minYear = year;
//                if (year > maxYear && year != 0) maxYear = year;
//            }
//            header.desc.setText(minYear + " ~ " + maxYear);
//        }
//
//        private class SongListHeadViewHolder extends AirAdapter.AirHeadViewHolder {
//
//            private ImageView image;
//            private TextView title;
//            private TextView subTitle;
//            private TextView desc;
//
//            public SongListHeadViewHolder(View itemView) {
//                super(itemView);
//                image = (ImageView) itemView.findViewById(R.id.header_image);
//                title = (TextView) itemView.findViewById(R.id.header_title);
//                subTitle = (TextView) itemView.findViewById(R.id.header_sub_title);
//                desc = (TextView) itemView.findViewById(R.id.header_desc);
//            }
//        }
//    }
}
