package com.airplayer;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ZiyiTsang on 15/7/12.
 */
public class DownloadURLTask extends AsyncTask<String, Void, ArrayList<String>>{

    private HttpCallbackListener listener;

    public DownloadURLTask(HttpCallbackListener listener) {
        this.listener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(String... params) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(params[0]);
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
            if (params[1].equals("album")) {
                return decodeAlbumJSON(response.toString());
            } else if (params[1].equals("artist")) {
                return decodeArtistJSON(response.toString());
            }
        } catch (Exception e) {
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
