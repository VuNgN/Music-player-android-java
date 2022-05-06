package com.vungn.mymusicplayer2;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MusicService extends Service {
    public static final String KEY_MUSIC = "com.vungn.keyMusic";
    public static final String KEY_ACTION = "com.vungn.keyAction";
    public static final String KEY_BROAD_CAST_NOTIFICATION = "com.vungn.keyBroadCastNotification";
    public static final String KEY_BROAD_CAST_MUSIC = "com.vungn.keyBroadCastMusic";
    public static final String KEY_MUSIC_STATUS = "com.vungn.keyMusicStatus";

    private Song mSong;
    private MusicHelper mMusicHelper;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleAction(intent);
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
        registerReceiver(mBroadcastReceiver, new IntentFilter(KEY_BROAD_CAST_NOTIFICATION));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() == MainActivity.KEY_GET_MUSIC) {
            if (mSong != null)
            sendSongInfo();
        } else {
            getSong(intent);
            handleAction(intent);
            startForegroundService();
        }
        return START_NOT_STICKY;
    }

    private void handleAction(Intent intent) {
        int action = getAction(intent);
        mMusicHelper.setAction(action);
        mMusicHelper.handleAction();
    }

    private void startForegroundService() {
        Notification notification = getNotification();
        startForeground(1, notification);
    }

    private int getAction(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        return bundle.getInt(KEY_ACTION, Actions.PLAY_SONG);
        return Actions.PLAY_SONG;
    }

    private Notification getNotification() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mSong.getImage());
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, getString(R.string.notification_tag));
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntentMainActivity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(this, NotifyApplication.CHANNEL_ID)
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
        intent.setAction(KEY_BROAD_CAST_NOTIFICATION);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ACTION, action);
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
        intentMainActivity.setAction(KEY_BROAD_CAST_MUSIC);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MUSIC, mSong);
        bundle.putBoolean(KEY_MUSIC_STATUS, mMusicHelper.isPlaying());
        intentMainActivity.putExtras(bundle);
        sendBroadcast(intentMainActivity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
