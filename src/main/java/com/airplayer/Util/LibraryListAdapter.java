package com.airplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.airplayer.R;
import com.airplayer.model.Music;

import java.util.List;

/**
 * Created by ZiyiTsang on 15/6/7.
 */
public class LibraryListAdapter extends ArrayAdapter<Music> {

    public static final int ARTIST_LIST = 0;
    public static final int ALBUM_LIST = 1;
    public static final int SONG_LIST = 2;

    private int resourceId;
    private List<Music> list;
    private int listType;
    private ViewHolder viewHolder;

    public LibraryListAdapter(Context context, int resourceId, List<Music> objects) {
        super(context, resourceId, objects);
        this.resourceId = resourceId;
        list = objects;
    }

    public LibraryListAdapter(Context context, int resourceId, List<Music> objects, int listType) {
        this(context, resourceId, objects);
        this.listType = listType;
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

        setListItemText(viewHolder, position);

        new SetArtTask().execute(position);

        return view;
    }

    class ViewHolder {
        public ImageView holdImage;
        public TextView holdText;
    }

    class SetArtTask extends AsyncTask<Integer, Void, Bitmap> {
        @Override
        protected synchronized Bitmap doInBackground(Integer... params) {
            switch (listType) {
                case ARTIST_LIST:
                    return ImageUtils.getListItemThumbnail(list.get(params[0]).getArtistImage());
                case ALBUM_LIST:
                    return ImageUtils.getListItemThumbnail(list.get(params[0]).getAlbumArt());
                case SONG_LIST:
                    return ImageUtils.getListItemThumbnail(list.get(params[0]).getAlbumArt());
                default:
                    return null;
            }
        }

        @Override
        protected synchronized void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                viewHolder.holdImage.setImageBitmap(bitmap);
            }
        }
    }

    private void setListItemText(ViewHolder viewHolder, int position) {
        String itemText;
        switch (listType) {
            case ARTIST_LIST:
                itemText = list.get(position).getArtist();
                break;
            case ALBUM_LIST:
                itemText = list.get(position).getAlbum();
                break;
            case SONG_LIST:
                itemText = list.get(position).getTitle();
                break;
            default:
                itemText = "";
        }
        viewHolder.holdText.setText(itemText);
    }
}

