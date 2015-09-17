package com.airplayer.fragment.child;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.fragment.libchild.AlbumGridFragment;
import com.airplayer.fragment.libchild.ArtistGridFragment;
import com.airplayer.fragment.libchild.SongListFragment;

/**
 * Created by ZiyiTsang on 15/6/2.
 */
public class MyLibraryFragment extends ChildFragment {

    public static final String TAG = MyLibraryFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_library, container, false);

        FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

        ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.my_library_pager);

        TabLayout tabLayout = ((AirMainActivity) getActivity()).getTabLayout();
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.air_text_and_icon));
        tabLayout.setVisibility(View.VISIBLE);

        viewPager.setAdapter(new LibraryPagerAdapter(fm));
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    private class LibraryPagerAdapter extends FragmentPagerAdapter {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}

