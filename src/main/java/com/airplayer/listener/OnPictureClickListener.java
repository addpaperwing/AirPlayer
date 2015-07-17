package com.airplayer.listener;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;

import com.airplayer.R;
import com.airplayer.activity.ResponseActivity;
import com.airplayer.fragment.dialog.MenuDialogFragment;
import com.airplayer.model.PictureGettable;

import java.io.File;

/**
 * Created by ZiyiTsang on 15/7/17.
 */
public class OnPictureClickListener implements View.OnClickListener {

    private PictureGettable mItem;
    private Context mContext;
    private FragmentManager mFragmentManager;

    public OnPictureClickListener(Context context, PictureGettable item, FragmentManager fragmentManager) {
        mContext = context;
        mItem = item;
        mFragmentManager = fragmentManager;
    }

    @Override
    public void onClick(View v) {
        MenuDialogFragment dialog = new MenuDialogFragment() {
            @Override
            public void onFirstItemClick(View v) {
                Intent intent = new Intent(getActivity(), ResponseActivity.class);
                intent.putExtra(ResponseActivity.QUERY_TARGET, mItem);
                getActivity().startActivityForResult(intent, PictureGettable.REQUEST_CODE_FETCH_PICTURE);
            }

            @Override
            public void onSecondItemClick(View v) {
                deleteHeaderPicture();
            }
        };
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        dialog.show(mFragmentManager, null);
    }

    private boolean deleteHeaderPicture() {
        File file = new File(mItem.getPicturePath());
        if (file.exists()) {
            onPictureDelete();
            Toast.makeText(mContext, R.string.toast_picture_delete, Toast.LENGTH_SHORT).show();
            return file.delete();
        } else {
            Toast.makeText(mContext, R.string.toast_picture_not_found, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void onPictureDelete() {}
}
