package com.airplayer.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.airplayer.Fragment.MyLibraryFragment;
import com.airplayer.Fragment.NavigationDrawerFragment;
import com.airplayer.Fragment.NowPlayingFragment;
import com.airplayer.R;


public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawFragment;

    private Toolbar mToolbar;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });

        mFragmentManager = getSupportFragmentManager();

        mNavigationDrawFragment = (NavigationDrawerFragment) mFragmentManager
                .findFragmentById(R.id.navigation_drawer);

        mNavigationDrawFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }


    /**
     * implements the NavigationDrawerCallbacks
     * @param position position of NavigationDrawer item selected
     */
    @Override
    public void onNavigationDrawItemSelected(int position) {
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, switchFragment(position)).commit();
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * a convenient method to switch fragment with the position of selected item
     * @param position position of selected item
     * @return a new Fragment whose name was selected in NavigationDrawer
     */
    private Fragment switchFragment(int position) {
        switch (position) {
            case 0:
                mToolbar.setTitle("Now Playing");
                return new NowPlayingFragment();
            case 1:
                mToolbar.setTitle("My Library");
                return new MyLibraryFragment();
            default:
                return null;
        }
    }
}
