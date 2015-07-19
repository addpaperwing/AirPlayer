package com.airplayer;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ZiyiTsang on 15/7/12.
 */
public class DownloadURLTask extends AsyncTask<String, Void, ArrayList<String>>{

    /**
     * <br>a link to search a album art, use as a param to execute { @see DownloadURLTask  }</br>
     * <br>查询专辑封面的链接，执行 { @see DownloadURLTask } 时作为传入参数</br>
     */
    private static final String SEARCH_LINK_ALBUM_ART = "https://api.douban.com/v2/music/search?q=";

    /**
     * <br>a link to search a artist picture, use as a param to execute { @see DownloadURLTask  }</br>
     * <br>查询艺人图片的链接，执行 { @see DownloadURLTask } 时作为传入参数</br>
     */
    private static final String SEARCH_LINK_ARTIST_PICTURE = "http://image.baidu.com/i?tn=baiduimagejson&word=";

    public static final int MODE_DOWNLOAD_ALBUM_ART = 9;

    public static final int MODE_DOWNLOAD_ARTIST_PICTURE = 8;

    private int mode;

    public void setMode(int mode) {
        this.mode = mode;
        if (mode == MODE_DOWNLOAD_ALBUM_ART) searchLink = SEARCH_LINK_ALBUM_ART;
        if (mode == MODE_DOWNLOAD_ARTIST_PICTURE) searchLink = SEARCH_LINK_ARTIST_PICTURE;
    }

    private String searchLink;

    private HttpCallbackListener listener;

    public DownloadURLTask(HttpCallbackListener listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(searchLink + params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            if (mode == MODE_DOWNLOAD_ALBUM_ART) {
                return decodeAlbumJSON(response.toString());
            } else if (mode == MODE_DOWNLOAD_ARTIST_PICTURE){
                return decodeArtistJSON(response.toString());
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.onError(e);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<String> list) {
        if (listener != null) {
            listener.onFinish(list);
        }
    }

    public interface HttpCallbackListener {
        void onFinish(ArrayList<String> list);
        void onError(Exception e);
    }

    private ArrayList<String> decodeAlbumJSON(String response) {
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray musicInfos = jsonObject.getJSONArray("musics");
            for (int i = 0; i < musicInfos.length(); i++) {
                JSONObject firstResultInfo = musicInfos.getJSONObject(i);
                String imageUrl = changeToBigImageUrl(firstResultInfo.getString("image"));
                list.add(imageUrl);
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ArrayList<String> decodeArtistJSON(String response) {
        ArrayList<String> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray musicArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < musicArray.length() - 1; i++) {
                JSONObject musicObject = musicArray.getJSONObject(i);
                Log.d("TAG", musicObject.toString());
                String imageUrl = musicObject.getString("objURL");
                list.add(imageUrl);
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String changeToBigImageUrl(String smallImageUrl) {
        String[] array = smallImageUrl.split("/");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i == 3) {
                builder.append("lpic/");
            } else if (i + 1 == array.length) {
                builder.append(array[i]);
            } else {
                builder.append(array[i]).append("/");
            }
        }
        return builder.toString();
    }
}
