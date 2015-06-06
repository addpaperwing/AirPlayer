package com.airplayer.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Message;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.Util.ViewHolder;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/5.
 */
public class LibraryAdapter<T> extends ArrayAdapter<T> {
    private int resourceId;
    private LibraryAdapterCallbacks callbacks;
    ViewHolder viewHolder; // use a view holder to optimize list view

    public LibraryAdapter(Context context, int resourceId, List<T> objects) {
        super(context, resourceId, objects);
        this.resourceId = resourceId;
    }

    public LibraryAdapter(Context context, int resourceId, List<T> objects, LibraryAdapterCallbacks callbacks) {
        this(context, resourceId, objects);
        this.callbacks = callbacks;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.holdImage = (ImageView) view.findViewById(R.id.list_item_image);
            viewHolder.holdText = (TextView) view.findViewById(R.id.list_item_title);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.holdText.setText(callbacks.onGetTitle(position));

        new SetArtTask().execute(position);

        return view;
    }

    public interface LibraryAdapterCallbacks {
        String onGetTitle(int position);
        Bitmap onGetImage(int position);
    }

    class SetArtTask extends AsyncTask<Integer, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Integer... params) {
            return callbacks.onGetImage(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            viewHolder.holdImage.setImageBitmap(bitmap);
        }
    }
}
