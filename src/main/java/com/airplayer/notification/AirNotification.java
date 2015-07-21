package com.airplayer.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.airplayer.R;
import com.airplayer.activity.AirMainActivity;
import com.airplayer.model.Song;
import com.airplayer.service.PlayMusicService;
import com.airplayer.util.BitmapUtils;
import com.airplayer.util.QueryUtils;

/**
 * Created by ZiyiTsang on 15/6/25.
 */
public class AirNotification {

    private Context mContext;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    private PendingIntent pPrevious;
    private PendingIntent pPlayPause;
    private PendingIntent pNext;
    private PendingIntent pContent;

    public AirNotification(Context context) {
        mContext = context;
        mBuilder = new NotificationCompat.Builder(context);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification = new Notification.Builder(mContext)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(songPlaying.getTitle())
                    .setContentText(songPlaying.getArtist())
                    .setSubText(songPlaying.getAlbum())
                    .setContentIntent(pContent)
                    .setWhen(0)
                    .setLargeIcon(BitmapUtils.getBitmap(mContext, songPlaying.getAlbumArtPath(mContext)))
                    .setStyle(new Notification.MediaStyle().setShowActionsInCompactView(0, 1, 2))
                    .setOngoing(true)
                    .addAction(R.drawable.ic_skip_previous_white, "previous", pPrevious)
                    .addAction(R.drawable.ic_pause_white, "pause", pPlayPause)
                    .addAction(R.drawable.ic_skip_next_white, "next", pNext)
                    .build();
        } else {
            notification = mBuilder
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(songPlaying.getTitle())
                    .setContentText(songPlaying.getArtist())
                    .setSubText(songPlaying.getAlbum())
                    .setContentIntent(pContent)
                    .setWhen(0)
                    .setLargeIcon(BitmapUtils.getBitmap(mContext, QueryUtils.getAlbumArtPath(mContext, songPlaying.getAlbum())))
                    .setOngoing(true)
                    .addAction(R.drawable.ic_skip_previous_white, "previous", pPrevious)
                    .addAction(R.drawable.ic_pause_white, "pause", pPlayPause)
                    .addAction(R.drawable.ic_skip_next_white, "next", pNext)
                    .build();
        }

        if (songPlaying.isPause()) {
            notification.actions[1].icon = R.drawable.ic_play_arrow_white;
            notification.actions[1].title = "play";
        }

        mNotificationManager.notify(1, notification);
    }

    public void cancel() {
        mNotificationManager.cancel(1);
    }
}
