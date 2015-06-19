package com.airplayer.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.airplayer.fragment.MyLibraryFragment;
import com.airplayer.fragment.NavigationDrawerFragment;
import com.airplayer.fragment.NowPlayingFragment;
import com.airplayer.R;
import com.airplayer.fragment.PlayMusicFragment;
import com.airplayer.service.PlayMusicService;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


public class AirMainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String TAG = "AirMainActivity";

    /* shared preference */
    public static final String PREF_IS_FIRST_OPEN = "pref_is_first_open";
    public static final String PREF_DATA_BASE_VERSION = "pref_data_base_version";
    private SharedPreferences sp;

    /* user interface */
    private NavigationDrawerFragment mNavigationDrawFragment;
    private Toolbar mToolbar;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    /* service */
    private PlayMusicService.PlayerControlBinder playerControlBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playerControlBinder = (PlayMusicService.PlayerControlBinder) service;
            // set up bottom sliding fragment when service is connected
            mFragmentManager.beginTransaction()
                    .add(R.id.sliding_fragment_container,
                            new PlayMusicFragment()).commit();

            // set sliding up panel invisible when activity create
            mSlidingUpPanelLayout.setTouchEnabled(false);
            mSlidingUpPanelLayout.setPanelHeight(0);
            Log.d(TAG, "service has connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "service has disconnected");
        }
    };

    /* others */
    private FragmentManager mFragmentManager;

    /* receiver */
    private PlayerStateReceiver mPlayerStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind service
        Intent playMusicServiceIntent = new Intent(this, PlayMusicService.class);
        playMusicServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(playMusicServiceIntent);
        bindService(playMusicServiceIntent, connection, BIND_AUTO_CREATE);

        // get sliding up panel
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_up_panel_layout);

        // get fragment manager
        mFragmentManager = getSupportFragmentManager();

        // set up tool bar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_testing) {

                }
                return true;
            }
        });

        // set up navigation drawer fragment
        mNavigationDrawFragment = (NavigationDrawerFragment) mFragmentManager
                .findFragmentById(R.id.navigation_drawer);

        mNavigationDrawFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mPlayerStateReceiver = new PlayerStateReceiver();
        IntentFilter filter = new IntentFilter(PlayMusicService.START_TO_PLAY_A_NEW_SONG);
        registerReceiver(mPlayerStateReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);                  /* unbind service when destroy activity */
        unregisterReceiver(mPlayerStateReceiver);   /* unregister receiver when destroy activity */
        super.onDestroy();
    }

    /**
     * implements the NavigationDrawerCallbacks
     * @param position of NavigationDrawer item selected
     */
    @Override
    public void onNavigationDrawItemSelected(int position) {
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, switchFragment(position)).commit();
    }

    /**
     * getter of main tool bar
     * @return tool bar of the hole app
     */
    public Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * getter of binder of service
     * @return service which control the music player and music play action
     */
    public PlayMusicService.PlayerControlBinder getPlayerControlBinder() {
        return playerControlBinder;
    }

    /**
     * inside receiver to receiver player state broadcast
     * to control the sliding up panel visible
     */
    public class PlayerStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSlidingUpPanelLayout.setTouchEnabled(true);
            mSlidingUpPanelLayout.setPanelHeight(R.integer.sliding_panel_height);
        }
    }

    /**
     * a convenient method to switch fragment with the position of selected item
     * @param position of selected item
     * @return a new Fragment whose name was selected in NavigationDrawer
     */
    private Fragment switchFragment(int position) {
        switch (position) {
            case 0:
                mToolbar.setTitle("Now Playing");
                if (Build.VERSION.SDK_INT >= 21) {
                    mToolbar.setElevation(16);
                }
                return new NowPlayingFragment();
            case 1:
                mToolbar.setTitle("My Library");
                if (Build.VERSION.SDK_INT >= 21) {
                    mToolbar.setElevation(0);
                }
                return new MyLibraryFragment();
            default:
                return null;
        }
    }
}
