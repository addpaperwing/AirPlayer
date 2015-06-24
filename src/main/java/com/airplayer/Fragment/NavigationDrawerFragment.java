package com.airplayer.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/1.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * The key of { @link mCurrentSelectedPosition }
     * which used to remember the position that selected
     */
    public static final String CURRENT_SELECTED_POSITION = "current_selected_position";

    /**
     * The key of { @link mUserLearnDrawer }
     * which used to remember if user learn how to use the drawer
     */
    public static final String PREF_USER_LEARN_DRAWER = "pref_user_learn_drawer";


    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;


    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerRecycler;
    private View mFragmentContainerView;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle toggle;

    private int mCurrentSelectedPosition;
    private boolean mUserLearnDrawer;

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

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnDrawer = sp.getBoolean(PREF_USER_LEARN_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(CURRENT_SELECTED_POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        mDrawerRecycler = (RecyclerView) rootView.findViewById(R.id.navigation_recycler);
        mDrawerRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDrawerRecycler.setAdapter(new NaviRecyclerAdapter(getActivity(),
                new String[]{getString(R.string.title_play_now), getString(R.string.title_my_library)}));

//        mDrawerRecycler.setAdapter(new ArrayAdapter<String>(
//                getActivity(),
//                R.layout.recycler_item_navigation_drawer,
//                R.id.list_item_text,
//                new String[]{getString(R.string.title_play_now), getString(R.string.title_my_library)}
//        ));

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

        private void switchDisplay (NaviRecyclerViewHolder holder, int position) {
            switch (mCurrentSelectedPosition) {
                case 0:
                    if (position == 0) {
                        holder.checkedHighlight.setVisibility(View.VISIBLE);
                        holder.textView.setTextColor(getResources().getColor(R.color.air_text_and_icon));
                    }
                    if (position == 1) {
                        holder.checkedHighlight.setVisibility(View.INVISIBLE);
                        holder.textView.setTextColor(getResources().getColor(R.color.air_primary_text));
                    }
                    break;
                case 1:
                    if (position == 0) {
                        holder.checkedHighlight.setVisibility(View.INVISIBLE);
                        holder.textView.setTextColor(getResources().getColor(R.color.air_primary_text));
                    }
                    if (position == 1) {
                        holder.checkedHighlight.setVisibility(View.VISIBLE);
                        holder.textView.setTextColor(getResources().getColor(R.color.air_text_and_icon));
                    }
                    break;
            }
        }
    }
}

