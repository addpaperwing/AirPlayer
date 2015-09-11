package com.airplayer.activity.SingleItemActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.airplayer.R;
import com.airplayer.activity.fetchpicture.FetchPictureActivity;
import com.airplayer.fragment.singleitem.SettableRecyclerView;
import com.airplayer.fragment.dialog.MenuDialogFragment;
import com.airplayer.model.PictureGettable;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

/**
 * Created by Administrator on 2015/9/11 0011.
 */
public abstract class SingleItemActivity extends AppCompatActivity implements SettableRecyclerView {

    private static final String TAG = SingleItemActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private FragmentManager mFragmentManager;

    protected SimpleDraweeView mDraweeView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        setContentView(R.layout.recycler_collapsing_toolbar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.collapsing_toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mDraweeView = (SimpleDraweeView) findViewById(R.id.head_image);
        setupDraweeView();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.setupRecyclerView(mRecyclerView);
    }

    /* package */ abstract class OnPictureClickListener implements View.OnClickListener {

        private PictureGettable item;
        private Class<?> aClass;

        public OnPictureClickListener(PictureGettable item, Class<?> aClass) {
            this.item = item;
            this.aClass = aClass;
        }

        @Override
        public void onClick(View v) {
            MenuDialogFragment dialog = new MenuDialogFragment() {
                @Override
                public void onFetchButtonClick(View v) {
                    Intent intent = new Intent(getActivity(), aClass);
                    intent.putExtra(FetchPictureActivity.EXTRA_QUERY_KEYWORD, item.getQueryKeyword());
                    intent.putExtra(FetchPictureActivity.EXTRA_SAVE_NAME, item.getSaveName());
                    getActivity().startActivityForResult(intent, PictureGettable.REQUEST_CODE_FETCH_PICTURE);
                }

                @Override
                public void onDeleteButtonClick(View v) {
                    File file = new File(item.getPicturePath());
                    if (file.exists()) {
                        file.delete();
                        onPictureDelete();
                        Toast.makeText(getActivity(), R.string.toast_picture_delete, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), R.string.toast_picture_not_found, Toast.LENGTH_SHORT).show();
                    }
                }
            };
            dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            dialog.show(mFragmentManager, null);
        }

        public abstract void onPictureDelete();
    }

    public abstract void setupDraweeView();
}
