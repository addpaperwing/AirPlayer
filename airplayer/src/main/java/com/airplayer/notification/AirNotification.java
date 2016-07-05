package com.airplayer.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Action;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;
import com.airplayer.util.BitmapUtils;

/**
 * Created by ZiyiTsang on 15/6/25.
 */
public class AirNotification {

    private Context mContext;
    private NotificationManager mNotificationManager;

    private PendingIntent pPrevious;
    private PendingIntent pPlayPause;
    private PendingIntent pNext;
    private PendingIntent pContent;

    public AirNotification(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent onNotificationClick = new Intent(context, AirMainActivity.class);
        pContent = PendingIntent.getActivity(context, 8, onNotificationClick, 0);

        Intent previous = new Intent(PlayMusicService.NOTIFICATION_OPERATION);
        previous.putExtra(PlayMusicService.NOTIFICATION_OPERATION_KEY, PlayMusicService.NOTIFICATION_OPERATION_PREVIOUS);
        pPrevious = PendingIntent.getBroadcast(context, 5, previous, 0);

        Intent play = new Intent(PlayMusicService.NOTIFICATION_OPERATION);
        play.putExtra(PlayMusicService.NOTIFICATION_OPERATION_KEY, PlayMusicService.NOTIFICATION_OPERATION_PLAY_PAUSE);
        pPlayPause = PendingIntent.getBroadcast(context, 1, play, 0);

        Intent next = new Intent(PlayMusicService.NOTIFICATION_OPERATION);
        next.putExtra(PlayMusicService.NOTIFICATION_OPERATION_KEY, PlayMusicService.NOTIFICATION_OPERATION_NEXT);
        pNext = PendingIntent.getBroadcast(context, 2, next, 0);
    }

    @SuppressLint("NewApi")
    public void push(Song songPlaying) {
        Notification notification;
        if (isLollipop()) {
            Notification.Builder builder = new Notification.Builder(mContext)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(songPlaying.getTitle())
                    .setContentText(songPlaying.getAlbum().getArtist().getName())
                    .setSubText(songPlaying.getAlbum().getTitle())
                    .setContentIntent(pContent)
                    .setWhen(0)
                    .setLargeIcon(BitmapUtils.getBitmap(mContext, songPlaying.getAlbum().getAlbumArtPath()))
                    .setStyle(new Notification.MediaStyle().setShowActionsInCompactView(0, 1, 2))
                    .setOngoing(true);

            addNotificationActions(builder, songPlaying);
            notification = builder.build();
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(songPlaying.getTitle())
                    .setContentText(songPlaying.getAlbum().getArtist().getName())
                    .setSubText(songPlaying.getAlbum().getTitle())
                    .setContentIntent(pContent)
                    .setWhen(0)
                    .setLargeIcon(BitmapUtils.getBitmap(mContext, songPlaying.getAlbum().getAlbumArtPath()))
                    .setOngoing(true);

            addNotificationActions(builder, songPlaying);
            notification = builder.build();
        }

        mNotificationManager.notify(1, notification);
    }

    @SuppressLint("NewApi")
    private Action newAction(int iconResId, String title, PendingIntent pi) {
        Icon icon = Icon.createWithResource(mContext, iconResId);
        return new Notification.Action.Builder(icon, title, pi).build();
    }

    @SuppressLint("NewApi")
    private void addNotificationActions(Notification.Builder builder, Song songPlaying) {
        if (isMarshmallow()) {
            builder.addAction(newAction(R.drawable.ic_skip_previous_white, "previous", pPrevious));
            if (songPlaying.isPause()) {
                builder.addAction(newAction(R.drawable.ic_play_arrow_white, "play", pPlayPause));
            } else {
                builder.addAction(newAction(R.drawable.ic_pause_white, "pause", pPlayPause));
            }
            builder.addAction(newAction(R.drawable.ic_skip_next_white, "next", pNext));
        } else {
            builder.addAction(R.drawable.ic_skip_previous_white, "previous", pPrevious);
            if (songPlaying.isPause()) {
                builder.addAction(R.drawable.ic_play_arrow_white, "play", pPlayPause);
            } else {
                builder.addAction(R.drawable.ic_pause_white, "pause", pPlayPause);
            }
            builder.addAction(R.drawable.ic_skip_next_white, "next", pNext);
        }
    }

    private void addNotificationActions(NotificationCompat.Builder builder, Song songPlaying) {
        builder.addAction(R.drawable.ic_skip_previous_white, "previous", pPrevious);
        if (songPlaying.isPause()) {
            builder.addAction(R.drawable.ic_play_arrow_white, "play", pPlayPause);
        } else {
            builder.addAction(R.drawable.ic_pause_white, "pause", pPlayPause);
        }
        builder.addAction(R.drawable.ic_skip_next_white, "next", pNext);
    }

    private boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public void cancel() {
        mNotificationManager.cancel(1);
        mNotificationManager.cancelAll();
    }
}
