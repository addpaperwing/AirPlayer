package com.airplayer;

import android.os.AsyncTask;

import com.airplayer.model.Picture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ZiyiTsang on 15/7/12.
 */
public abstract class DownloadURLTask extends AsyncTask<String, Void, ArrayList<Picture>> {

    @Override
    protected ArrayList<Picture> doInBackground(String... params) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(getUrl() + params[0]);
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
            return decodeJson(response.toString());
        } catch (IOException e) {
                onError(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Picture> list) {
        onFinish(list);
    }

    public abstract String getUrl();

    public abstract ArrayList<Picture> decodeJson(String response);

    public abstract void onError(Exception e);

    public abstract void onFinish(ArrayList<Picture> list);
}
