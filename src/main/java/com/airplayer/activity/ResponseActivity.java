package com.airplayer.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.airplayer.DownloadURLTask;
import com.airplayer.R;
import com.airplayer.adapter.AirAdapter;
import com.airplayer.adapter.HeadPadAdapter;
import com.airplayer.fragment.dialog.ReplacePicDialogFragment;
import com.airplayer.listener.SimpleAirScrollListener;
import com.airplayer.model.Album;
import com.airplayer.model.Artist;
import com.airplayer.model.PictureGettable;
import com.airplayer.util.StorageUtils;
import com.airplayer.util.StringUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZiyiTsang on 15/7/12.
 */
public class ResponseActivity extends AppCompatActivity {

    String SEARCH_URL_ALBUM_ART = "https://api.douban.com/v2/music/search?q=";
    String SEARCH_URL_ARTIST_PICTURE = "http://image.baidu.com/i?tn=baiduimagejson&word=";

    // query target album, key and value
    // 查询的目标专辑对象，键和值
    public static final String QUERY_TARGET = "query_target";

    private ArrayList<String> mImageLinks;

    private PictureGettable mItem;

    private RecyclerView mRecyclerView;

    /* handle download image task */
    private static final int MSG_WHAT_FILE = 1;
    private static final int MSG_WHAT_ERROR = 2;

    private ProgressDialog progress;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_WHAT_FILE) {
                progress.dismiss();
                setResult(RESULT_OK, null);
                onBackPressed();
            } else if (msg.what == MSG_WHAT_ERROR) {
                Toast.makeText(ResponseActivity.this,
                        "download fail, please check out network connection",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void downloadImage(final String url) {
        progress = new ProgressDialog(ResponseActivity.this);
        progress.setMessage("Saving picture");
        progress.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = StorageUtils.saveImage(ResponseActivity.this,
                        mItem.getSaveName() + ".jpg", url);
                Message msg = new Message();
                msg.what = MSG_WHAT_FILE;
                msg.obj = file;
                handler.sendMessage(msg);
            }
        }).start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_suppressible_toolbar);

        mItem = (PictureGettable) getIntent().getSerializableExtra(QUERY_TARGET);

        // download task
        DownloadURLTask task = new DownloadURLTask(new DownloadURLTask.HttpCallbackListener() {
            @Override
            public void onFinish(ArrayList<String> list) {
                mImageLinks = list;
                setupAdapter();
            }

            @Override
            public void onError(Exception e) {
                Message msg = new Message();
                msg.what = MSG_WHAT_ERROR;
                handler.sendMessage(msg);
            }
        });
        String urlSpec = (mItem instanceof Album ? SEARCH_URL_ALBUM_ART : SEARCH_URL_ARTIST_PICTURE)
                + StringUtils.encodeKeyword(mItem.getSearchKeyword());
        String choice = (mItem instanceof Album ? "album" : "artist");
        Log.d("TAG", urlSpec + "   " + choice);
        task.execute(urlSpec, choice);

        // setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.suppressible_toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.air_dark_primary_color));
        if (Build.VERSION.SDK_INT >= 21) toolbar.setElevation(19);
        toolbar.setTitle(mItem.getSearchKeyword());
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                (getResources().getInteger(R.integer.padding_action_bar), toolbar));
        setupAdapter();
    }

    private class ResponseAdapter extends HeadPadAdapter {

        public ResponseAdapter(Context context, List<?> list, int paddingHeight) {
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
                item.image.setImageURI(Uri.parse(getList().get(position - 1).toString()));
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

    /**
     * a convenient method to setup or update data of adapter of RecyclerView
     */
    private void setupAdapter() {
        ResponseAdapter adapter = new ResponseAdapter(ResponseActivity.this,
                mImageLinks, getResources().getInteger(R.integer.padding_action_bar));
        adapter.setOnItemClickListener(new AirAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, final int position) {
                ReplacePicDialogFragment dialog = new ReplacePicDialogFragment() {
                    @Override
                    public void onOkClick(View view) {
                        downloadImage(mImageLinks.get(position - 1));
                        dismiss();
                    }
                };
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                dialog.show(getSupportFragmentManager(), null);
            }
        });
        mRecyclerView.setAdapter(adapter);
    }
}
