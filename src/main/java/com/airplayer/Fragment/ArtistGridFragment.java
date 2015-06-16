package com.airplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.model.Artist;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.util.ImageUtils;
import com.airplayer.util.QueryUtils;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/9.
 */
public class ArtistGridFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private List<Artist> mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);

        // load a list from Media data base
        mList = QueryUtils.loadArtistList(getParentFragment().getActivity());

        //find a recycler view and set it up
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        final GridLayoutManager manager = new GridLayoutManager(getParentFragment().getActivity(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? manager.getSpanCount() : 1;
            }
        });
        mRecyclerView.setLayoutManager(manager);


        ArtistAdapter adapter = new ArtistAdapter(getParentFragment().getActivity(), mList);
        adapter.setItemClickListener(new AirAdapter.ClickListener() {
            @Override
            public void itemClicked(View view, int position) {
                FragmentTransaction ft = getParentFragment().getActivity()
                        .getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, ArtistFragment.newInstance(mList.get(position - 1)));
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

            @Override
            public void headerClicked(View view) {

            }
        });
        mRecyclerView.setAdapter(adapter);
        return rootView;
    }

    private class ArtistAdapter extends AirAdapter<Artist> {

        public ArtistAdapter(Context context, List<Artist> list) {
            super(context, list);
        }

        @Override
        public AirHeadViewHolder onCreateHeadViewHolder(ViewGroup parent) {
            return new ArtistHeaderViewHolder(getLayoutInflater()
                    .inflate(R.layout.recycler_header, parent, false));
        }

        @Override
        public AirItemViewHolder onCreateItemViewHolder(ViewGroup parent) {
            return new ArtistItemViewHolder(getLayoutInflater()
                    .inflate(R.layout.recycler_item_artist, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int i) {
            if (holder instanceof ArtistItemViewHolder) {
                ArtistItemViewHolder artistViewHolder = (ArtistItemViewHolder) holder;
                artistViewHolder.imageView.setImageBitmap(
                        ImageUtils.getListItemThumbnail(
                                getList().get(i - 1).getImagePath()
                        ));
                artistViewHolder.textView.setText(getList().get(i - 1).getName());
            }

            if (holder instanceof ArtistHeaderViewHolder) {

            }
        }

        public class ArtistItemViewHolder extends AirItemViewHolder {
            ImageView imageView;
            TextView textView;

            public ArtistItemViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.artist_image);
                textView = (TextView) itemView.findViewById(R.id.artist_name);
            }
        }

        public class ArtistHeaderViewHolder extends AirHeadViewHolder {

            public ArtistHeaderViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

}
