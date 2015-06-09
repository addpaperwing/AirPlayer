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


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.holdImage = (ImageView) convertView.findViewById(R.id.list_item_image);
            viewHolder.holdText = (TextView) convertView.findViewById(R.id.list_item_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        setListItemText(position);

        Music music = getItem(position);
        String path = "";
        switch (listType) {
            case 0:
                path = music.getArtistImage();
                break;
            case 1:
                path = music.getAlbumArt();
                break;
            case 2:
                path = music.getAlbumArt();
                break;
        }
        viewHolder.holdImage.setTag(path);
        viewHolder.holdImage.setImageResource(android.R.drawable.ic_menu_gallery);


        new SetArtTask().execute(path);

        return convertView;
    }


    class ViewHolder {
        public ImageView holdImage;
        public TextView holdText;
    }


    class SetArtTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected synchronized Bitmap doInBackground(String... params) {
            if (params[0].equals(viewHolder.holdImage.getTag())) {
                return ImageUtils.getListItemThumbnail(params[0]);
            }
            return null;
        }

        @Override
        protected synchronized void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                viewHolder.holdImage.setImageBitmap(bitmap);
            }
        }
    }

    private void setListItemText(int position) {
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

