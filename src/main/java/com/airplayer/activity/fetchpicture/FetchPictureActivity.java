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
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airplayer.util.DownloadURLTask;
import com.airplayer.R;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.HeadPadAdapter;
import com.airplayer.fragment.dialog.ReplacePicDialogFragment;
import com.airplayer.listener.SimpleAirScrollListener;
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
     * <br>Instance of { @see PictureGettable }, pass when start { @see FetchPictureActivity }</br>
     * <br>{ @see PictureGettable } 的实例, 启动{ @see FetchPictureActivity } 时传入</br>
     */

    /**
     * <br>a link to search a album art, use as a param to execute { @see DownloadURLTask  }</br>
     * <br>查询专辑封面的链接，执行 { @see DownloadURLTask } 时作为传入参数</br>
     */
    public static final String SEARCH_LINK_ALBUM_ART = "https://api.douban.com/v2/music/search?q=";

    /**
     * <br>a link to search a artist picture, use as a param to execute { @see DownloadURLTask  }</br>
     * <br>查询艺人图片的链接，执行 { @see DownloadURLTask } 时作为传入参数</br>
     */
    public static final String SEARCH_LINK_ARTIST_PICTURE = "http://image.baidu.com/i?tn=baiduimagejson&word=";

    /**
     * <br>Image url array list fetch from { @see DownloadURLTask }</br>
     * <br>图片 url 的数组列表，从 { @see DownloadURLTask } 获取</br>
     */
    private ArrayList<Picture> mPictureList = new ArrayList<>();

    /* handle download image task */
    /**
     * <br>What value of message that will be sent when { @see downloadImage } download succeed.</br>
     * <br>在 { @see downloadImage } 方法下载成功时发送的 message 的 what 值</br>
     */
    private static final int MSG_DOWNLOAD_PICTURE_SUCCEED = 1;
    private static final int MSG_DOWNLOAD_PICTURE_FAIL = 2;

    /**
     * <br>What value of message that will be sent when { @see DownloadURLTask } download fail</br>
     * <br>在 { @see DownloadURLTask } 下载失败时发送的 message 的 what 值</br>
     */
    private static final int MSG_DOWNLOAD_IMAGE_URL_FAIL = 3;

    private ProgressDialog progress;

    /**
     * <br>If msg is { @see MSG_DOWNLOAD_PICTURE_SUCCEED } dismiss progress and set resultCode to RESULT_OK.</br>
     * <br>If msg is { @see MSG_DOWNLOAD_IMAGE_URL_FAIL } make a toast to tell user.</br>
     * <br>如果 msg 是 { @see MSG_DOWNLOAD_PICTURE_SUCCEED } 撤销进度条并设置 resultCode 为 RESULT_OK。</br>
     * <br>如果 msg 是 { @see MSG_DOWNLOAD_IMAGE_URL_FAIL } 发出一条 toast 告诉用户下载失败。</br>
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (progress != null) {
                progress.dismiss();
            }
            switch (msg.what) {
                case MSG_DOWNLOAD_PICTURE_SUCCEED:
                    setResult(RESULT_OK, null);
                    onBackPressed();
                    break;
                case MSG_DOWNLOAD_PICTURE_FAIL:
                    mSwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(FetchPictureActivity.this,
                            "download picture fail, picture source might not exist",
                            Toast.LENGTH_SHORT).show();
                    break;
                case MSG_DOWNLOAD_IMAGE_URL_FAIL:
                    Toast.makeText(FetchPictureActivity.this,
                            "download fail, please check out network connection",
                            Toast.LENGTH_SHORT).show();

            }
        }
    };

    /**
     * <br>New thread to download picture from @param url to external storage and show a progress.
     * When download succeed send a { @see MSG_DOWNLOAD_PICTURE_SUCCEED } message.</br>
     * <br>新建一个线程下载 @param url 中的图片到手机扩展储存，并现实一个进度条。
     * 当下载成功时，发送一个 { @see MSG_DOWNLOAD_PICTURE_SUCCEED } message 。</br>
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

    private static final int MODE_DOWNLOAD_REPLACE = 0;
    private static final int MODE_DOWNLOAD_ADD = 1;

    private RecyclerView mRecyclerView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Toolbar mToolbar;

    private FPAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_swipe_refresh);

        if (mQueryKeyword == null) {
            mQueryKeyword = getIntent().getStringExtra(EXTRA_QUERY_KEYWORD);
            mSaveName = getIntent().getStringExtra(EXTRA_SAVE_NAME);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("TAG", "onRefresh");
                executeDownloadTask(MODE_DOWNLOAD_REPLACE);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(R.color.air_accent_color);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getInteger(R.integer.padding_action_bar) + 20);

        // task to download a array list of image links
        executeDownloadTask(MODE_DOWNLOAD_ADD);

        // setup toolbar
        mToolbar = (Toolbar) findViewById(R.id.suppressible_toolbar);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.air_dark_primary_color));
        if (Build.VERSION.SDK_INT >= 21) mToolbar.setElevation(19);
        mToolbar.setTitle(mQueryKeyword);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolbar.inflateMenu(R.menu.menu_search);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    Bundle appSearchData = new Bundle();
                    appSearchData.putString(EXTRA_SAVE_NAME, mSaveName);
                    startSearch(mQueryKeyword, false, appSearchData, false);
                    return true;
                }
                return false;
            }
        });

        // setup RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final GridLayoutManager manager = new GridLayoutManager(this, 2);

        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? manager.getSpanCount() : 1;
            }
        });
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setOnScrollListener(new SimpleAirScrollListener
                (getResources().getInteger(R.integer.padding_action_bar), mToolbar));
        setupAdapter();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            mQueryKeyword = intent.getStringExtra(SearchManager.QUERY);
            mSaveName = intent.getBundleExtra(SearchManager.APP_DATA).getString(EXTRA_SAVE_NAME);
            mToolbar.setTitle(mQueryKeyword);
        }
        executeDownloadTask(MODE_DOWNLOAD_REPLACE);
    }

    /**
     * <br>Adapter of RecyclerView has a head padding, two columns and every item is a { @see SimpleDraweeView }</br>
     * <br>RecyclerView 的 adapter 头部有一部分空白填充，两列每一项都是一个 { @SimpleDraweeView }</br>
     */
    private class FPAdapter extends HeadPadAdapter {

        public FPAdapter(Context context, List<?> list, int paddingHeight) {
            super(context, list, paddingHeight);
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

    private void executeDownloadTask(final int downloadMod) {
        mSwipeRefreshLayout.setRefreshing(true);
        Log.d("TAG", "onExecuteTask");
        new DownloadURLTask() {
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
                Message msg = new Message();
                msg.what = MSG_DOWNLOAD_IMAGE_URL_FAIL;
                handler.sendMessage(msg);
            }

            @Override
            public void onFinish(ArrayList<Picture> list) {
                switch (downloadMod) {
                    case MODE_DOWNLOAD_ADD:
                        for (Picture p : list) {
                            mPictureList.add(p);
                        }
                        adapter.notifyItemRangeInserted(mPictureList.size(), list.size());
                        break;
                    case MODE_DOWNLOAD_REPLACE:
                        mPictureList = list;
                        setupAdapter();
                        break;
                    default:
                        break;
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }.execute(StringUtils.encodeKeyword(mQueryKeyword));
    }

    /**
     * <br>A convenient method to setup or update data of adapter of RecyclerView.</br>
     * <br>一个封装好的简易方法来配置 RecyclerView adapter</br>
     * <br></br>
     * <br>This method will be called twice, first is when { @see FetchPictureActivity } create and the
     * other one is when { @DownloadURLTask } finish</br>
     * <br>该方法将会被调用两次，第一次是当 { @see FetchPictureActivity } 创建时，
     * 另一次时当 { @see DownloadURLTask } 完成时</br>
     */
    private void setupAdapter() {
        adapter = new FPAdapter(FetchPictureActivity.this,
                mPictureList, getResources().getInteger(R.integer.padding_action_bar));
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

    public abstract String getSearchLink();
    public abstract ArrayList<Picture> onDecodeJson(String response);
}
