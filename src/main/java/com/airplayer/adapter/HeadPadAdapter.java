package com.airplayer.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.airplayer.R;
import com.airplayer.fragment.NavigationDrawerFragment;
import com.airplayer.util.ImageUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/7/6.
 */
public abstract class HeadPadAdapter extends AirAdapter {

    public HeadPadAdapter(Context context, List<?> list) {
        super(context, list);
    }

    @Override
    public AirHeadViewHolder onCreateHeadViewHolder(ViewGroup parent) {
        return new PadHeader(getLayoutInflater().inflate(R.layout.recycler_header_padding, parent, false));
    }

    @Override
    public void onBindHeadViewHolder(AirHeadViewHolder headHolder) {
        if (headHolder instanceof PadHeader) {
            PadHeader header = (PadHeader) headHolder;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
            Picasso.with(getContext())
                    .load(Uri.parse(sp.getString(NavigationDrawerFragment.PREF_THEME_PIC, "")))
                    .into(header.imageView);
        }
    }

    class PadHeader extends AirAdapter.AirHeadViewHolder {

        ImageView imageView;

        public PadHeader(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.padding_image);
        }
    }
}
