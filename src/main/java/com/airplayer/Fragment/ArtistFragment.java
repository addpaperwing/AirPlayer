package com.airplayer.fragment;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.adapter.AlbumAdapter;
import com.airplayer.model.Album;
import com.airplayer.model.Artist;
import com.airplayer.util.ImageUtils;
import com.airplayer.util.QueryUtils;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/14.
 */
public class ArtistFragment extends Fragment {

    public static final String ARTIST_RECEIVED = "artist_received";

    private Artist mArtist;

    public static ArtistFragment newInstance(Artist artist) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARTIST_RECEIVED, artist);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArtist = (Artist) getArguments().get(ARTIST_RECEIVED);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.cover_fragment, container, false);

//        TextView artistName = (TextView) rootView.findViewById(R.id.cover_fragment_title);
//        artistName.setText(mArtist.getName());

//        ImageView artistImage = (ImageView) rootView.findViewById(R.id.cover_fragment_image);
//        artistImage.setImageBitmap(ImageUtils.getListItemThumbnail(mArtist.getImagePath()));

        List<Album> albumList = QueryUtils.loadAlbumList(getActivity(),
                "artist = ?", new String[] { mArtist.getName() }, MediaStore.Audio.Albums.FIRST_YEAR);


        return rootView;
    }
}
