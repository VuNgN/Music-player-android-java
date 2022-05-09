package com.vungn.mymusicplayer2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MusicService extends Service {
    public static final String KEY_MUSIC = "com.vungn.keyMusic";
    public static final String KEY_MUSIC_ACTION = "com.vungn.keyAction";
    public static final String KEY_MUSIC_PLAYING_STATUS = "com.vungn.keyMusicStatus";
    public static final String ACTION_GET_MUSIC_ON_START_UP = "com.vungn.keyGetMusic";
    private static final String CHANNEL_ID = "com.vungn.notificationChannelId";
    private static final CharSequence CHANNEL_NAME = "com.vungn.notificationChannelName";

    private Song mSong;
    private MusicHelper mMusicHelper;
    private final MusicBroadcastReceiver mBroadcastReceiver = new MusicBroadcastReceiver() {
        @Override
        public void onNotificationBroadcastReceiver(Song song, int action) {
            super.onNotificationBroadcastReceiver(song, action);
            handleMediaAction(action);
            startForegroundService();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMusicHelper = new MusicHelper(this);
        mBroadcastReceiver.registerNotificationReceiver(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            if (intent.getAction() == ACTION_GET_MUSIC_ON_START_UP) {
                if (mSong != null)
                sendSongInfo();
            } else {
                getSong(intent);
                int action = getMediaAction(intent);
                handleMediaAction(action);
                startForegroundService();
            }
        return START_NOT_STICKY;
    }

    private int getMediaAction(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            return bundle.getInt(KEY_MUSIC_ACTION, Actions.PLAY_SONG);
        }
        return Actions.PLAY_SONG;
    }

    private void handleMediaAction(int action) {
        mMusicHelper.setAction(action);
        mMusicHelper.handleAction();
    }

    private void startForegroundService() {
        Notification notification = getNotification();
        startForeground(1, notification);
    }

    private Notification getNotification() {
        NotificationChannel channel = null;
        NotificationManager manager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setSound(null, null);
            manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mSong.getImage());
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, getString(R.string.notification_tag));
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntentMainActivity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_library_music_24)
                .setSubText(getString(R.string.subtext))
                .setContentTitle(mSong.getTitle())
                .setContentText(mSong.getAuthor())
                .setContentIntent(pendingIntentMainActivity)
                .setLargeIcon(bitmap)
                .addAction(R.drawable.ic_baseline_fast_rewind_24, getString(R.string.previous), null)
                .addAction(
                        mMusicHelper.isPlaying() ? R.drawable.ic_baseline_pause_circle_outline_24
                                : R.drawable.ic_baseline_play_circle_outline_24,
                        getString(R.string.pause),
                        getPendingIntent(this, mMusicHelper.isPlaying() ? Actions.PAUSE_SONG
                                : Actions.RESUME_SONG))
                .addAction(R.drawable.ic_baseline_fast_forward_24, getString(R.string.next), null)
                .addAction(R.drawable.ic_baseline_cancel_24, getString(R.string.exit), getPendingIntent(this, Actions.CLEAR_SONG))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1, 3)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .build();
    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent();
        intent.setAction(MusicBroadcastReceiver.ACTION_NOTIFICATION);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_MUSIC_ACTION, action);
        bundle.putSerializable(KEY_MUSIC, mSong);
        intent.putExtras(bundle);
        return PendingIntent.getBroadcast(getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void getSong(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Song song = (Song) bundle.get(KEY_MUSIC);
            if (song != null) {
                mSong = song;
                mMusicHelper.setSong(song);
            }
        }
    }

    private void sendSongInfo() {
        Intent intentMainActivity = new Intent();
        intentMainActivity.setAction(MusicBroadcastReceiver.ACTION_MUSIC);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MUSIC, mSong);
        bundle.putBoolean(KEY_MUSIC_PLAYING_STATUS, mMusicHelper.isPlaying());
        intentMainActivity.putExtras(bundle);
        sendBroadcast(intentMainActivity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBroadcastReceiver.unregisterReceiver(this);
    }
}
