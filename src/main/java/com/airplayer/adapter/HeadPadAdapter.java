package com.airplayer.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.List;

/**
 * Created by ZiyiTsang on 15/7/6.
 */
public abstract class HeadPadAdapter extends AirAdapter {

    private int paddingHeight;

    public HeadPadAdapter(Context context, List<?> list) {
        super(context, list);
        paddingHeight = context.getResources().getInteger(R.integer.padding_action_bar) + context.getResources().getInteger(R.integer.padding_tabs);
    }

    public HeadPadAdapter(Context context, List<?> list, int paddingHeight) {
        super(context, list);
        this.paddingHeight = paddingHeight;
    }

    @Override
    public AirHeadViewHolder onCreateHeadViewHolder(ViewGroup parent) {
        return new PadHeader(getLayoutInflater().inflate(R.layout.recycler_header_padding, parent, false));
    }

    @Override
    public void onBindHeadViewHolder(AirHeadViewHolder headHolder) {
        if (headHolder instanceof PadHeader) {
            PadHeader header = (PadHeader) headHolder;
            header.image.setMaxHeight(paddingHeight);
            header.image.setMinimumHeight(paddingHeight);

            File file = new File(AirMainActivity.EXTERNAL_PICTURE_FOLDER + "Theme.jpg");
            header.image.setImageURI(Uri.fromFile(file));
        }
    }

    class PadHeader extends AirAdapter.AirHeadViewHolder {

        SimpleDraweeView image;

        public PadHeader(View itemView) {
            super(itemView);
            image = (SimpleDraweeView) itemView.findViewById(R.id.padding_image);
        }
    }
}
