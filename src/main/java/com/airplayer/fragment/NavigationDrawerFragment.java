package com.airplayer.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.util.BitmapUtils;
import com.airplayer.util.StorageUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.IOException;

/**
 * Created by ZiyiTsang on 15/6/1.
 */
public class NavigationDrawerFragment extends Fragment {

    private SharedPreferences mSp;
    /* guide user to use navigation drawer */
    /**
     * sharedPreference to save if user learn how to use the drawer
     */
    public static final String PREF_USER_LEARN_DRAWER = "pref_user_learn_drawer";

    private boolean mUserLearnDrawer;


    /* handle replace theme picture operation */
    public static final int PICK_PHOTO = 1;

    // what value of message instance
    private static final int MSG_SAVE_THEME_PICTURE_SUCCEED = 0;
    private static final int MSG_SAVE_THEME_PICTURE_FAIL = 2;

    // progress dialog when loading theme picture
    private ProgressDialog progress;

    // handler
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progress.dismiss();
            switch (msg.what) {
                case MSG_SAVE_THEME_PICTURE_SUCCEED:
                    setTopImage((Uri) msg.obj);
                    break;
                case MSG_SAVE_THEME_PICTURE_FAIL:
                    Toast.makeText(getActivity(),
                            "fail to save picture into external storage",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    // thread
    private void saveBitmapTask(final Bitmap bitmap, final Uri uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    StorageUtils.saveImage(getActivity(), "Theme.jpg", bitmap);
                    msg.what = MSG_SAVE_THEME_PICTURE_SUCCEED;
                    msg.obj = uri;
                } catch (Exception e) {
                    msg.what = MSG_SAVE_THEME_PICTURE_FAIL;
                } finally {
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /* User interface */
    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;


    // top theme image view of navigation drawer
    private SimpleDraweeView mTopImage;
    private TextView mTopImageHint;

    private void setTopImage(Uri uri) {
        if (uri == null) {
            File file = new File(AirMainActivity.EXTERNAL_PICTURE_FOLDER + "Theme.jpg");
            if (file.exists()) {
                uri = Uri.fromFile(file);
            } else {
                mTopImageHint.setVisibility(View.VISIBLE);
                return;
            }
        }
        mTopImage.setImageURI(uri);
        mTopImageHint.setVisibility(View.INVISIBLE);
    }

    // content of navigation drawer
    private RecyclerView mDrawerRecycler;

    // bottom of navigation drawer
    private FrameLayout mSetting;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle toggle;

    /* savedInstanceState */
    /**
     * The key of { @link mCurrentSelectedPosition }
     * which used to remember the position that selected
     */
    public static final String CURRENT_SELECTED_POSITION = "current_selected_position";

    private int mCurrentSelectedPosition;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get SharedPreference
        mSp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnDrawer = mSp.getBoolean(PREF_USER_LEARN_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(CURRENT_SELECTED_POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        mDrawerRecycler = (RecyclerView) rootView.findViewById(R.id.navigation_recycler);
        mDrawerRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDrawerRecycler.setAdapter(new NaviRecyclerAdapter(getActivity(),
                new String[]{getString(R.string.title_activities), getString(R.string.title_library), getString(R.string.title_equalizer)}));

        mTopImageHint = (TextView) rootView.findViewById(R.id.navigation_top_image_hint);
        mTopImage = (SimpleDraweeView) rootView.findViewById(R.id.navigation_image);
        setTopImage(null);
        mTopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO);
            }
        });

        mSetting = (FrameLayout) rootView.findViewById(R.id.navigation_bottom_button);
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        Toolbar toolbar = ((AirMainActivity) getActivity()).getToolbar();

        toggle = new ActionBarDrawerToggle(
                getActivity(),
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        );

        mDrawerLayout.setDrawerListener(toggle);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(mFragmentContainerView)) {
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                } else {
                    mDrawerLayout.openDrawer(mFragmentContainerView);
                }
            }
        });

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                toggle.syncState();
            }
        });

        if (!mUserLearnDrawer) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
            mUserLearnDrawer = true;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sp.edit().putBoolean(PREF_USER_LEARN_DRAWER, mUserLearnDrawer).apply();
        }

        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_PHOTO) {
                Uri uri = data.getData();
                Bitmap bm = BitmapUtils.getBitmap(getActivity(), uri);
                if (bm.getByteCount() > 14745600) {
                    Toast.makeText(getActivity(), R.string.navigation_top_toast, Toast.LENGTH_SHORT).show();
                } else {
                    progress = new ProgressDialog(getActivity());
                    progress.setMessage("saving picture");
                    progress.show();
                    saveBitmapTask(bm, uri);
                }
            }
        }
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public interface NavigationDrawerCallbacks {
        void onNavigationDrawItemSelected(int position);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerRecycler != null) {
            mDrawerRecycler.getAdapter().notifyDataSetChanged();
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawItemSelected(position);
        }
    }

    private class NaviRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private LayoutInflater layoutInflater;
        private String[] items;

        public NaviRecyclerAdapter(Context context, String[] items) {
            this.layoutInflater = LayoutInflater.from(context);
            this.items = items;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NaviRecyclerViewHolder(layoutInflater
                    .inflate(R.layout.recycler_item_navigation_drawer, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            NaviRecyclerViewHolder naviHolder = (NaviRecyclerViewHolder) holder;
            naviHolder.textView.setText(items[position]);
            switchDisplay(naviHolder, position);
        }

        @Override
        public int getItemCount() {
            return items.length;
        }

        private class NaviRecyclerViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;
            private FrameLayout checkedHighlight;

            public NaviRecyclerViewHolder(View itemView) {
                super(itemView);
                textView = (TextView) itemView.findViewById(R.id.navigation_recycler_item_text);
                checkedHighlight = (FrameLayout) itemView.findViewById(R.id.check_high_light);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectItem(getPosition());
                    }
                });
            }
        }

        private void switchDisplay(NaviRecyclerViewHolder holder, int position) {
            for (int i = 0; i < 3; i++) {
                if (mCurrentSelectedPosition == position) {
                    holder.checkedHighlight.setVisibility(View.VISIBLE);
                    holder.textView.setTextColor(getResources().getColor(R.color.air_text_and_icon));
                } else {
                    holder.checkedHighlight.setVisibility(View.INVISIBLE);
                    holder.textView.setTextColor(getResources().getColor(R.color.air_primary_text));
                }
            }
        }
    }
}

