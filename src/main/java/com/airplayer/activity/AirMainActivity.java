package com.airplayer.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airplayer.fragment.child.EqualizerFragment;
import com.airplayer.fragment.child.MyLibraryFragment;
import com.airplayer.fragment.child.RecentFragment;
import com.airplayer.R;
import com.airplayer.fragment.PlayMusicFragment;
import com.airplayer.model.AirModel;
import com.airplayer.model.PictureGettable;
import com.airplayer.service.PlayMusicService;
import com.airplayer.util.BitmapUtils;
import com.airplayer.util.StorageUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;


public class AirMainActivity extends AppCompatActivity {

    public static final String TAG = "AirMainActivity";

    public static final String EXTERNAL_PICTURE_FOLDER = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getPath() + "/AirPlayer/";

    // handle replace theme picture operation
    public static final int PICK_PHOTO = 9;

    // what value of message instance that handle the pictures of NavigationView
    private static final int MSG_SAVE_THEME_PICTURE_SUCCEED = 3;
    private static final int MSG_SAVE_THEME_PICTURE_FAIL = 2;

    // mProgress dialog when saving background picture
    private ProgressDialog mProgress;

    // thread to save bitmap of draweeView which is the head of NavigationView
    private void saveBitmapTask(final Bitmap bitmap, final Uri uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    StorageUtils.saveImage(AirMainActivity.this, "Theme.jpg", bitmap);
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

    // ===== user interface =====
    // ----- Left NavigationView -----
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private SimpleDraweeView mNavigationHeadDraweeView;
    private TextView mNavigationHintTextView;

    // ----- Top ActionBar -----
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;
    private TabLayout mTabs;

    // ----- Bottom SlidingUpPanel
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    // ===== service =====
    private PlayMusicService.PlayerControlBinder playerControlBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playerControlBinder = (PlayMusicService.PlayerControlBinder) service;
            // set up bottom sliding fragment when service is connected
            mPlayMusicFragment = new PlayMusicFragment();
            mFragmentManager.beginTransaction()
                    .replace(R.id.sliding_fragment_container,
                            mPlayMusicFragment).commit();

            // set sliding up panel invisible when activity create
            if (!playerControlBinder.isPlaying()) {
                mSlidingUpPanelLayout.setTouchEnabled(false);
                mSlidingUpPanelLayout.setPanelHeight(0);
            } else {
                mSlidingUpPanelLayout.setTouchEnabled(true);
                mSlidingUpPanelLayout.setPanelHeight(mSize);
            }
            Log.d(TAG, "service has connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "service has disconnected");
        }
    };

    // ===== receiver =====
    private PlayerStateReceiver mPlayerStateReceiver;

    // ===== helper classes =====
    private FragmentManager mFragmentManager;
    private PlayMusicFragment mPlayMusicFragment;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                mSlidingUpPanelLayout.setPanelHeight((int) msg.obj);
            } else if (msg.what == MSG_SAVE_THEME_PICTURE_FAIL || msg.what == MSG_SAVE_THEME_PICTURE_SUCCEED) {
                mProgress.dismiss();
                switch (msg.what) {
                    case MSG_SAVE_THEME_PICTURE_SUCCEED:
                        setNavigationImage((Uri) msg.obj);
                        break;
                    case MSG_SAVE_THEME_PICTURE_FAIL:
                        Toast.makeText(AirMainActivity.this,
                                "fail to save picture into external storage",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };

    // ===== other resource =====
    private int mSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        AirModel.initModels(this);
        setContentView(R.layout.activity_main);

        // ===== get actionbarSize in attrs =====
        int defaultInt = getResources().getDimensionPixelOffset(R.dimen.sliding_up_fragment_bottom_bar_height);
        int[] attrsArray = { R.attr.actionBarSize };
        TypedArray typedArray = obtainStyledAttributes(attrsArray);
        mSize = typedArray.getDimensionPixelOffset(0, defaultInt);
        typedArray.recycle();

        // ===== bind service =====
        Intent playMusicServiceIntent = new Intent(this, PlayMusicService.class);
        playMusicServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(playMusicServiceIntent);
        bindService(playMusicServiceIntent, connection, BIND_AUTO_CREATE);

        // ===== getup sliding up panel and set a listener =====
        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_up_panel_layout);
        mSlidingUpPanelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) { }

            @Override
            public void onPanelCollapsed(View view) {
                Toolbar toolbar = mPlayMusicFragment.getSlidingUpPanelTopBar();
                toolbar.getMenu().clear();
                if (playerControlBinder.isPause()) {
                    toolbar.inflateMenu(R.menu.menu_sliding_panel_down_play_menu);
                } else {
                    toolbar.inflateMenu(R.menu.menu_sliding_panel_down_pause_menu);
                }
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onPanelExpanded(View view) {
                Toolbar toolbar = mPlayMusicFragment.getSlidingUpPanelTopBar();
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_sliding_panel_up_menu);
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onPanelAnchored(View view) { }

            @Override
            public void onPanelHidden(View view) { }
        });

        // ===== get fragment manager =====
        mFragmentManager = getSupportFragmentManager();

        // ===== setup tool bar =====
        mToolbar = (Toolbar) findViewById(R.id.global_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbar_layout);
        mTabs = (TabLayout) findViewById(R.id.tabs);

        // ===== setup navigation drawer fragment =====
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        setupNavigationDrawer();
        mNavigationView.setCheckedItem(R.id.action_recent);
        onNavigationDrawItemSelected(0);

        // ===== setup and register receiver =====
        mPlayerStateReceiver = new PlayerStateReceiver();
        IntentFilter filter = new IntentFilter(PlayMusicService.PLAY_STATE_CHANGE);
        registerReceiver(mPlayerStateReceiver, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureGettable.REQUEST_CODE_FETCH_PICTURE:
                    mFragmentManager.findFragmentById(R.id.fragment_container)
                            .onActivityResult(requestCode, resultCode, data);
                    break;
                case PICK_PHOTO:
                    Uri uri = data.getData();
                    Bitmap bm = BitmapUtils.getBitmap(this, uri);
                    if (bm.getByteCount() > 5120000) {
                        Toast.makeText(this, R.string.navigation_top_toast, Toast.LENGTH_SHORT).show();
                    } else {
                        mProgress = new ProgressDialog(this);
                        mProgress.setMessage("saving picture");
                        mProgress.show();
                        saveBitmapTask(bm, uri);
                    }
                    break;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);                  /* unbind service */
        unregisterReceiver(mPlayerStateReceiver);   /* unregister receiver */
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            if (mPlayMusicFragment.isPlayListShow()) {
                super.onBackPressed();
                mPlayMusicFragment.setIsPlayListShow(false);
            } else {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        } else if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED){
            if (mPlayMusicFragment.isPlayListShow()) {
                super.onBackPressed();
                super.onBackPressed();
                mPlayMusicFragment.setIsPlayListShow(false);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    /**
     * a convenient method to package codes of setting up NavigationView
     */
    private void setupNavigationDrawer() {
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        final ActionBarDrawerToggle toggle;

        toggle = new ActionBarDrawerToggle(
                AirMainActivity.this,
                mDrawerLayout,
                mToolbar,
                R.string.app_name,
                R.string.app_name
        );

        mDrawerLayout.setDrawerListener(toggle);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                    mDrawerLayout.closeDrawers();
                } else {
                    mDrawerLayout.openDrawer(mNavigationView);
                }
            }
        });

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                toggle.syncState();
            }
        });

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        int position;
                        switch (menuItem.getItemId()) {
                            case R.id.action_recent:
                                position = 0;
                                break;
                            case R.id.action_library:
                                position = 1;
                                break;
                            case R.id.action_equalizer:
                                position = 2;
                                break;
                            default:
                                position = -1;
                                break;
                        }
                        onNavigationDrawItemSelected(position);
                        return true;
                    }
                });

        setupNavigationHeadView();
    }

    private void setupNavigationHeadView() {
        View headView = mNavigationView.inflateHeaderView(R.layout.navigation_header);
        mNavigationHeadDraweeView = (SimpleDraweeView) headView.findViewById(R.id.navigation_image);
        mNavigationHintTextView = (TextView) headView.findViewById(R.id.navigation_top_image_hint);
        setNavigationImage(null);
        mNavigationHeadDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PHOTO);
            }
        });
    }

    private void setNavigationImage(Uri uri) {
        if (uri == null) {
            File file = new File(AirMainActivity.EXTERNAL_PICTURE_FOLDER + "Theme.jpg");
            if (file.exists()) {
                uri = Uri.fromFile(file);
            } else {
                mNavigationHintTextView.setVisibility(View.VISIBLE);
                return;
            }
        }
        mNavigationHeadDraweeView.setImageURI(uri);
        mNavigationHintTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * handle NavigationDraw selected action
     * @param position of NavigationDrawer item selected
     */
    private void onNavigationDrawItemSelected(int position) {
        if (position == -1) return;
        String[] titles = { getResources().getString(R.string.toolbar_title_recent), getResources().getString(R.string.toolbar_title_Library), getResources().getString(R.string.toolbar_title_equalizer) };
        mToolbar.setTitle(titles[position]);
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, switchFragment(position)).commit();
    }

    /**
     * a convenient method to switch fragment with the position of selected item
     * @param position of selected item
     * @return a new Fragment whose name was selected in NavigationDrawer
     */
    private Fragment switchFragment(int position) {

        // pop all fragments in back stack
        mFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // set play list is not showing when it popped
        if (mPlayMusicFragment != null) {
            mPlayMusicFragment.setIsPlayListShow(false);
        }

        switch (position) {
            case 0:
                return new RecentFragment();
            case 1:
                return new MyLibraryFragment();
            case 2:
                return new EqualizerFragment();
            default:
                return null;
        }
    }

    /**
     * getter of main tool bar
     * @return main Toolbar
     */
    public Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * getter of main AppbarLayout, use to add a TabLayout in { @link com.airplayer.fragment.child.MyLibraryFragment}
     * @return main AppBarLayout
     */
    public AppBarLayout getAppBarLayout() {
        return mAppBarLayout;
    }

    /**
     * getter of main PaddingTabs, use to setup a 1px tab to block the hiding action
     * @return padding tabs
     */
    public TabLayout getTabLayout() {
        return mTabs;
    }

    /**
     * getter of main DrawerLayout, use to lock in child fragment
     * @return main DrawerLayout
     */
    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
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
            int playState = intent.getIntExtra(PlayMusicService.PLAY_STATE_KEY, -1);
            if (playState == PlayMusicService.PLAY_STATE_PLAY) {
                if (!mSlidingUpPanelLayout.isTouchEnabled()) {
                    mSlidingUpPanelLayout.setTouchEnabled(true);
                    showPanel();
                }
                if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    Toolbar toolbar = mPlayMusicFragment.getSlidingUpPanelTopBar();
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.menu_sliding_panel_down_pause_menu);
                }
            } else if (playState == PlayMusicService.PLAY_STATE_PAUSE) {
                if (mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    Toolbar toolbar = mPlayMusicFragment.getSlidingUpPanelTopBar();
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.menu_sliding_panel_down_play_menu);
                }
            }
        }
    }

    /**
     * package method to animate show panel when start to play music
     */
    private void showPanel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int height = 0; height < mSize; height += 2) {
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = height;
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
