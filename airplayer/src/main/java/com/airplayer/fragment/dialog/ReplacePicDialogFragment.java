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
 * Created by ZiyiTsang on 15/7/13.
 */
public abstract class ReplacePicDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialog_replace_asking, container, false);
        TextView okText = (TextView) rootView.findViewById(R.id.fragment_asking_ok_button);
        okText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOkClick(v);
            }
        });
        TextView cancelText = (TextView) rootView.findViewById(R.id.fragment_asking_cancel_button);
        cancelText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return rootView;
    }

    public abstract void onOkClick(View view);
}
