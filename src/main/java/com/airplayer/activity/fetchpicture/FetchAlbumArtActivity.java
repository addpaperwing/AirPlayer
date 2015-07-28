package com.airplayer.activity.fetchpicture;

import android.os.Bundle;
import android.view.View;

import com.airplayer.model.Picture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ZiyiTsang on 15/7/21.
 */
public class FetchAlbumArtActivity extends FetchPictureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getSearchLink() {
        return SEARCH_LINK_ALBUM_ART;
    }

    @Override
    public ArrayList<Picture> onDecodeJson(String response) {
        ArrayList<Picture> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray musicInfoArray = jsonObject.getJSONArray("musics");
            for (int i = 0; i < musicInfoArray.length(); i++) {
                JSONObject firstResultInfo = musicInfoArray.getJSONObject(i);
                String objUrl = changeToBigImageUrl(firstResultInfo.getString("image"));
                Picture picture = new Picture(objUrl, objUrl);
                list.add(picture);
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

    @Override
    public void onMoreButtonClick(View v, int nextPage) {
        executeDownloadTask(MODE_DOWNLOAD_ADD, "&start=" + (nextPage - 1) * 20);
    }
}
