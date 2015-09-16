package com.airplayer.fragment.singleitem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.activity.fetchpicture.FetchPictureActivity;
import com.airplayer.fragment.dialog.MenuDialogFragment;

import com.airplayer.model.PictureGettable;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

/**
 * Created by ZiyiTsang on 15/6/14.
 */
public abstract class SingleItemChildFragment extends Fragment implements SettableFragmentView {

    private static final String TAG = SingleItemChildFragment.class.getSimpleName();

    // ===== views and widgets =====
    private RecyclerView mRecyclerView;
    private FragmentManager mFragmentManager;
    protected SimpleDraweeView mDraweeView;
    protected Toolbar mToolbar;

    // ===== global widget setting =====
    private AppBarLayout mAppBarLayout;
    private TabLayout mPaddingTabs;
    private DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getActivity().getSupportFragmentManager();

        // ===== Invisible main app bar =====
        mAppBarLayout = ((AirMainActivity) getActivity()).getAppBarLayout();
        mPaddingTabs = ((AirMainActivity) getActivity()).getTabLayout();

        // ===== lock DrawerLayout =====
        mDrawerLayout = ((AirMainActivity) getActivity()).getDrawerLayout();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getRootViewId(), container, false);

        mToolbar = (Toolbar) rootView.findViewById(R.id.collapsing_toolbar);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        setupFab(rootView);

        mDraweeView = (SimpleDraweeView) rootView.findViewById(R.id.head_image);
        setupDraweeView();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        this.setupRecyclerView(mRecyclerView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // hide or lock global widget when resume
        mPaddingTabs.setVisibility(View.GONE);
        mAppBarLayout.setExpanded(false);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // global widget reset when destroy
        mAppBarLayout.setExpanded(true);
        mPaddingTabs.setVisibility(View.VISIBLE);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public abstract void setupDraweeView();

    public void setupFab(View rootView) {}
}
