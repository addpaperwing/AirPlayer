package com.airplayer.fragment.dialog;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airplayer.R;

/**
 * Created by ZiyiTsang on 15/7/11.
 */
public abstract class MenuDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialog_fetch_image, container, false);
        TextView fetch = (TextView) rootView.findViewById(R.id.fetch_picture_from_internet);
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFirstItemClick(v);
                dismiss();
            }
        });
        TextView delete = (TextView) rootView.findViewById(R.id.delete_picture_downloaded);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSecondItemClick(v);
                dismiss();
            }
        });
        return rootView;
    }

    public abstract void onFirstItemClick(View v);
    public abstract void onSecondItemClick(View v);
}
