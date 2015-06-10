package com.airplayer.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.airplayer.database.AirPlayerDB;
import com.airplayer.fragment.MyLibraryFragment;
import com.airplayer.fragment.NavigationDrawerFragment;
import com.airplayer.fragment.NowPlayingFragment;
import com.airplayer.R;
import com.airplayer.fragment.PlayMusicFragment;
import com.airplayer.model.Music;
import com.airplayer.util.QueryUtils;


public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String PREF_IS_FIRST_OPEN = "pref_is_first_open";

    public static final String PREF_DATA_BASE_VERSION = "pref_data_base_version";

    private SharedPreferences sp;

    private NavigationDrawerFragment mNavigationDrawFragment;

    private Toolbar mToolbar;

    private FragmentManager mFragmentManager;

    private AirPlayerDB db;

    private int dbVersion;

    private ProgressDialog mProgressDialog;

    private boolean isFirstOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_settings) {
                    dbVersion = sp.getInt(PREF_DATA_BASE_VERSION, -1);
                    if (dbVersion != -1) {
                        dbVersion++;
                        db = AirPlayerDB.newInstance(MainActivity.this, dbVersion);
                        setUpData(MainActivity.this);
                        sp.edit().putInt(PREF_DATA_BASE_VERSION, dbVersion).apply();
                        Toast.makeText(MainActivity.this,
                                "Upgrade succeed, now the db version is " + dbVersion,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Fail to set up data, please try again", Toast.LENGTH_SHORT).show();
                    }
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }
                }
                if (item.getItemId() == R.id.action_testing) {

                }
                return true;
            }
        });

        mFragmentManager = getSupportFragmentManager();

        mFragmentManager.beginTransaction()
                .add(R.id.sliding_fragment_container, new PlayMusicFragment()).commit();

        mNavigationDrawFragment = (NavigationDrawerFragment) mFragmentManager
                .findFragmentById(R.id.navigation_drawer);

        mNavigationDrawFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        isFirstOpen = sp.getBoolean(PREF_IS_FIRST_OPEN, true);
        dbVersion = sp.getInt(PREF_DATA_BASE_VERSION, 1);

        db = AirPlayerDB.newInstance(this, dbVersion);

        if (isFirstOpen) {
            setUpData(this);
            isFirstOpen = false;
            sp.edit().putBoolean(PREF_IS_FIRST_OPEN, isFirstOpen).apply();
            sp.edit().putInt(PREF_DATA_BASE_VERSION, dbVersion).apply();
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }

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

    private void setUpData(Context context) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setMessage("Loading data");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Audio.Media.IS_MUSIC,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.ALBUM_ID
                },
                null, null, MediaStore.Audio.Media.TITLE);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getInt(0) == 1) {
                        Music music = new Music();
                        music.setTitle(cursor.getString(1));
                        music.setAlbum(cursor.getString(2));
                        music.setArtist(cursor.getString(3));
                        music.setDuration(cursor.getInt(4));
                        music.setPath(cursor.getString(5));
                        music.setAlbumArt(QueryUtils.getAlbumArtPath(context, cursor.getInt(6)));
                        db.saveMusicInfo(music);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }
}
