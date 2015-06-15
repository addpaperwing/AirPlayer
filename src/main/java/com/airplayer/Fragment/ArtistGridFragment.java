package com.airplayer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
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

    private List<Artist> getList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);

        // load a list from Media data base
        getList = QueryUtils.loadArtistList(getParentFragment().getActivity());

        //find a recycler view and set it up
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getParentFragment().getActivity(), 2));
        mRecyclerView.setAdapter(new ArtistAdapter(getParentFragment().getActivity(), getList));
        return rootView;
    }

    private class ArtistAdapter extends AirAdapter<Artist> {

        public ArtistAdapter(Context context, List<Artist> list) {
            super(context, list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ArtistViewHolder(getLayoutInflater().inflate(R.layout.recycler_item_artist, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
            ArtistViewHolder artistViewHolder = (ArtistViewHolder) viewHolder;
            artistViewHolder.imageView.setImageBitmap(ImageUtils.getListItemThumbnail(getList().get(i).getImagePath()));
            artistViewHolder.textView.setText(getList().get(i).getName());
            artistViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(getContext(), ArtistActivity.class);
//                    intent.putExtra("artist_name", getList().get(i).getName());
//                    startActivity(intent);
                    FragmentTransaction ft = getParentFragment().getActivity()
                            .getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, ArtistFragment.newInstance(getList.get(i)));
                    ft.addToBackStack(null);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }
            });
        }
    }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        CardView cardView;

        public ArtistViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.artist_image);
            textView = (TextView) itemView.findViewById(R.id.artist_name);
            cardView = (CardView) itemView.findViewById(R.id.artist_item);
        }
    }
}
