package com.airplayer.activity.fetchpicture;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airplayer.listener.EasyRecyclerViewListener;
import com.airplayer.multitask.DownloadURLTask;
import com.airplayer.R;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.fragment.dialog.ReplacePicDialogFragment;
import com.airplayer.model.Picture;
import com.airplayer.util.StorageUtils;
import com.airplayer.util.StringUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZiyiTsang on 15/7/12.
 * Activity that start when user click the top picture of
 * { @link com.airplayer.fragment.singleitem.AlbumFragment } which is an album art
 * or { @link com.airplayer.fragment.singleitem.ArtistFragment } which is an artist picture
 * This activity will search a picture about the item that pass with intent before start
 * The item is a { @link com.airplayer.model.PictureGettable } instance,
 * in this app is an { @link com.airplayer.model.Album } or an { @link com.airplayer.model.Artist }
 */
public abstract class FetchPictureActivity extends AppCompatActivity {

    /**
     * Key of { @link #mItem }
     * { @link #mItem } 的键
     */
    public static final String EXTRA_QUERY_KEYWORD = "extra_query_keyword";

    private String mQueryKeyword = null;

    public static final String EXTRA_SAVE_NAME = "extra_save_name";

    private String mSaveName;
    /**
     * Instance of { @link com.airplayer.model.PictureGettable }, pass when start { @link com.airplayer.activity.FetchPictureActivity }.
     * { @link com.airplayer.model.PictureGettable } 的实例, 启动{ @link com.airplayer.activity.FetchPictureActivity } 时传入。
     */

    /**
     * a link to search a album art, use as a param to execute { @link #executeDownloadTask }.
     * 查询专辑封面的链接，执行 { @link #executeDownloadTask } 时作为传入参数。
     */
    public static final String SEARCH_LINK_ALBUM_ART = "https://api.douban.com/v2/music/search?q=";

    /**
     * a link to search a artist picture, use as a param to execute { @link com.airplayer.multitask.DownloadURLTask  }.
     * 查询艺人图片的链接，执行 { @link com.airplayer.multitask.DownloadURLTask } 时作为传入参数。
     */
    public static final String SEARCH_LINK_ARTIST_PICTURE = "http://image.baidu.com/i?tn=baiduimagejson&word=";

    /**
     * Image url array list fetch from { @link com.airplayer.multitask.DownloadURLTask }.
     * 图片 url 的数组列表，从 { @link com.airplayer.multitask.DownloadURLTask } 获取。
     */
    private ArrayList<Picture> mPictureList = new ArrayList<>();

    // handle download image task
    /**
     * What value of message that will be sent when { @link #downloadImage } download succeed.
     * 在 { @link #downloadImage } 方法下载成功时发送的 message 的 what 值。
     */
    private static final int MSG_DOWNLOAD_PICTURE_SUCCEED = 1;
    private static final int MSG_DOWNLOAD_PICTURE_FAIL = 2;

    /**
     * What value of message that will be sent when { @link com.airplayer.multitask.DownloadURLTask } download fail.
     * 在 { @link com.airplayer.multitask.DownloadURLTask } 下载失败时发送的 message 的 what 值。
     */
    private static final int MSG_DOWNLOAD_IMAGE_URL_FAIL = 3;
    private static final int MSG_NO_RESULT_FOUND_OR_DECODE_FAIL = 4;

    private ProgressDialog progress;

    /**
     * If msg is { @link #MSG_DOWNLOAD_PICTURE_SUCCEED } dismiss progress and set resultCode to RESULT_OK.
     * If msg is { @link #MSG_DOWNLOAD_IMAGE_URL_FAIL } make a toast to tell user.
     * 如果 msg 是 { @link #MSG_DOWNLOAD_PICTURE_SUCCEED } 撤销进度条并设置 resultCode 为 RESULT_OK。
     * 如果 msg 是 { @link #MSG_DOWNLOAD_IMAGE_URL_FAIL } 发出一条 toast 告诉用户下载失败。
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (progress != null) {
                progress.dismiss();
            }
            String toastMessage;
            switch (msg.what) {
                case MSG_DOWNLOAD_PICTURE_SUCCEED:
                    setResult(RESULT_OK, null);
                    onBackPressed();
                    return;
                case MSG_DOWNLOAD_PICTURE_FAIL:
                    toastMessage = getResources().getString(R.string.toast_picture_source_not_exist);
                    break;
                case MSG_DOWNLOAD_IMAGE_URL_FAIL:
                    if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);
                    toastMessage = getResources().getString(R.string.toast_download_fail);
                    break;
                case MSG_NO_RESULT_FOUND_OR_DECODE_FAIL:
                    if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);
                    toastMessage = getResources().getString(R.string.toast_no_result_found_or_decode_fail);
                    break;
                default:
                    return;
            }
            Snackbar.make(mCoordinatorLayout, toastMessage, Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter.getList().size() > 0) {
                        onFetchMorePictures(nextPage);
                    } else {
                        executeDownloadTask(MODE_DOWNLOAD_REPLACE, null);
                    }
                }
            }).show();
        }
    };

    /**
     * New thread to download picture from @param url to external storage and show a progress.
     * When download succeed send a { @link #MSG_DOWNLOAD_PICTURE_SUCCEED } message.
     * 新建一个线程下载 @param url 中的图片到手机扩展储存，并现实一个进度条。
     * 新建一个线程下载 url(传入参数) 中的图片到手机扩展储存，并出现一个进度条。
     * 当下载成功时，发送一个 { @link #MSG_DOWNLOAD_PICTURE_SUCCEED } message 。
     * @param url the url of picture when it was clicked. 被点击的图片的url
     */
    private void downloadImage(final String url) {
        progress = new ProgressDialog(FetchPictureActivity.this);
        progress.setMessage("Saving picture");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    StorageUtils.saveImage(FetchPictureActivity.this,
                            mSaveName + ".jpg", url);
                    msg.what = MSG_DOWNLOAD_PICTURE_SUCCEED;
                } catch (Exception e) {
                    msg.what = MSG_DOWNLOAD_PICTURE_FAIL;
                } finally {
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    protected static final int MODE_DOWNLOAD_REPLACE = 0;
    protected static final int MODE_DOWNLOAD_ADD = 1;

    // ===== views and widgets =====
    // ----- Root CoordinatorLayout -----
    private CoordinatorLayout mCoordinatorLayout;

    // ----- RecyclerView -----
    private RecyclerView mRecyclerView;
    // ----- RecyclerAdapter -----
    private FPAdapter adapter;

    // ----- SwipeRefreshLayout -----
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // ----- Toolbar -----
    private Toolbar mToolbar;

    private int nextPage = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_swipe_refresh);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.fetch_picture_root_view);

        if (mQueryKeyword == null) {
            mQueryKeyword = getIntent().getStringExtra(EXTRA_QUERY_KEYWORD);
            mSaveName = getIntent().getStringExtra(EXTRA_SAVE_NAME);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeDownloadTask(MODE_DOWNLOAD_REPLACE, null);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.air_accent_color);

        // task to download a array list of image links
        executeDownloadTask(MODE_DOWNLOAD_ADD, null);

        // ===== setup toolbar =====
        mToolbar = (Toolbar) findViewById(R.id.collapsing_toolbar);

        // ----- toolbar background color -----
        mToolbar.setBackgroundColor(getResources().getColor(R.color.air_dark_primary_color));
        // ----- toolbar elevation -----
        if (Build.VERSION.SDK_INT >= 21) mToolbar.setElevation(19);
        // ----- toolbar title -----
        mToolbar.setTitle(mQueryKeyword);


        // ----- toolbar background color -----
        mToolbar.setBackgroundColor(getResources().getColor(R.color.air_dark_primary_color));

        // ----- toolbar elevation -----
        if (Build.VERSION.SDK_INT >= 21) mToolbar.setElevation(19);

        // ----- toolbar title -----
        mToolbar.setTitle(mQueryKeyword);


        // ----- toolbar navigation button -----
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // ----- toolbar menu search action -----
        mToolbar.inflateMenu(R.menu.menu_search);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
                    SearchView searchView = (SearchView) item.getActionView();
                    searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
                    ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                            .setHintTextColor(getResources().getColor(R.color.air_text_and_icon));
                    return true;
                }
                return false;
            }
        });


        // ===== RecyclerView =====
        // ===== setup RecyclerView =====
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final GridLayoutManager manager = new GridLayoutManager(this, 2);

        // ----- LayoutManager -----
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? manager.getSpanCount() : 1;
            }
        });
        mRecyclerView.setLayoutManager(manager);
        setupAdapter();
        // ----- ScrollListener -----
        mRecyclerView.addOnScrollListener(new EasyRecyclerViewListener() {
            @Override
            public void onScrollToBottom() {
                onFetchMorePictures(nextPage);
                nextPage++;
            }
        });
    }



    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            mQueryKeyword = intent.getStringExtra(SearchManager.QUERY);
            mToolbar.setTitle(mQueryKeyword);
        }
        executeDownloadTask(MODE_DOWNLOAD_REPLACE, null);
    }

    /**
     * Adapter of RecyclerView has a head padding, two columns and every item is a { @link #SimpleDraweeView }
     * RecyclerView 的 adapter 头部有一部分空白填充，两列每一项都是一个 { @link #SimpleDraweeView }
     */
    private class FPAdapter extends AirAdapter {

        public FPAdapter(Context context, List<?> list) {
            super(context, list);
        }

        @Override
        public AirItemViewHolder onCreateItemViewHolder(ViewGroup parent) {
            return new ResponseItemViewHolder(getLayoutInflater()
                    .inflate(R.layout.recycler_item_response, parent, false));
        }

        @Override
        public void onBindItemViewHolder(AirItemViewHolder itemHolder, int position) {
            if (itemHolder instanceof ResponseItemViewHolder) {
                ResponseItemViewHolder item = (ResponseItemViewHolder) itemHolder;
                item.image.setImageURI(Uri.parse(((Picture)getList().get(position - 1)).getThumbUrl()));
            }
        }

        private class ResponseItemViewHolder extends AirItemViewHolder {

            SimpleDraweeView image;

            public ResponseItemViewHolder(View itemView) {
                super(itemView);
                image = (SimpleDraweeView) itemView.findViewById(R.id.simple_drawee_view_item);
            }
        }
    }

    protected void executeDownloadTask(final int downloadMod, String otherParam) {
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        DownloadURLTask task = new DownloadURLTask() {
            @Override
            public String getUrl() {
                return getSearchLink();
            }

            @Override
            public ArrayList<Picture> decodeJson(String response) {
                return onDecodeJson(response);
            }

            @Override
            public void onError(Exception e) {
                sendErrorMessage(MSG_DOWNLOAD_IMAGE_URL_FAIL);
            }

            @Override
            public void onFinish(ArrayList<Picture> list) {
                if (list == null) {
                    sendErrorMessage(MSG_NO_RESULT_FOUND_OR_DECODE_FAIL);
                    return;
                }
                switch (downloadMod) {
                    case MODE_DOWNLOAD_ADD:
                        for (Picture p : list) {
                            mPictureList.add(p);
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    case MODE_DOWNLOAD_REPLACE:
                        mPictureList = list;
                        setupAdapter();
                        nextPage = 2;
                        break;
                    default:
                        break;
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        };
        task.execute(StringUtils.encodeKeyword(mQueryKeyword), otherParam);
    }

    /**
     * A convenient method to setup or update data of adapter of RecyclerView for calling more than once.
     * 一个封装好的简易方法来配置 RecyclerView adapter，以便多次调用
     */
    private void setupAdapter() {
        adapter = new FPAdapter(FetchPictureActivity.this, mPictureList);
        adapter.setOnItemClickListener(new AirAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, final int position) {
                ReplacePicDialogFragment dialog = new ReplacePicDialogFragment() {
                    @Override
                    public void onOkClick(View view) {
                        downloadImage(mPictureList.get(position - 1).getObjUrl());
                        dismiss();
                    }
                };
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                dialog.show(getSupportFragmentManager(), null);
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * package method to send error message for calling more than once
     * 封装发送错误信息的方法以便多次调用
     */
    private void sendErrorMessage(int what) {
        Message msg = new Message();
        msg.what = what;
        handler.sendMessage(msg);
    }

    public abstract String getSearchLink();
    public abstract ArrayList<Picture> onDecodeJson(String response);
    public abstract void onFetchMorePictures(int nextPage);
}
