package com.airplayer.activity.fetchpicture;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.airplayer.model.Picture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ZiyiTsang on 15/7/21.
 */
public class FetchArtistPictureActivity extends FetchPictureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getSearchLink() {
        return SEARCH_LINK_ARTIST_PICTURE;
    }

    @Override
    public ArrayList<Picture> onDecodeJson(String response) {
        ArrayList<Picture> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray musicArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < musicArray.length() - 1; i++) {
                JSONObject musicObject = musicArray.getJSONObject(i);
                String objUrl = musicObject.getString("objURL");
                String thumbUrl = musicObject.getString("thumbURL");
                Picture picture = new Picture(thumbUrl, objUrl);
                list.add(picture);
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onFetchMorePictures(int nextPage) {
        executeDownloadTask(MODE_DOWNLOAD_ADD, "&pn=" + nextPage * 20);
    }
}
