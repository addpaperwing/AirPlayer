package com.airplayer.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.google.SlidingTabLayout;

/**
 * Created by ZiyiTsang on 15/6/2.
 */
public class MyLibraryFragment extends Fragment {

    ViewPager viewPager;
    SlidingTabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_library, container, false);
        rootView.setPadding(0, getResources().getInteger(R.integer.padding_action_bar), 0, 0);
        ((AirMainActivity)getActivity()).getToolbar().setVisibility(View.VISIBLE);

        FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

        viewPager = (ViewPager) rootView.findViewById(R.id.my_library_pager);
        viewPager.setAdapter(new LibraryPagerAdapter(fm));
        tabLayout = (SlidingTabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setDistributeEvenly(true);
        tabLayout.setViewPager(viewPager);

        return rootView;
    }

    class LibraryPagerAdapter extends FragmentPagerAdapter {
        String[] tabItemArray = getResources().getStringArray(R.array.tab_item_array);

        public LibraryPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ArtistGridFragment();
                case 1:
                    return new AlbumGridFragment();
                case 2:
                    return new SongListFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabItemArray[position];
        }
    }
}

