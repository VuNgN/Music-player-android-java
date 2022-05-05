package com.vungn.mymusicplayer2;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MusicService extends Service {
    private Song mSong;
    private MusicHelper mMusicHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getSong(intent);
        int action = getAction(intent);
        mMusicHelper.setAction(action);
        mMusicHelper.handleAction();
        sendActionToActivity();
        Notification notification = getNotification();
        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    private void sendActionToActivity() {
        Intent intent = new Intent(Common.ACTION_SENT_TO_MAIN_ACTIVITY);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Common.MUSIC_HELPER_PUT_KEY, mMusicHelper);
        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private int getAction(Intent intent) {
        Bundle bundle = intent.getExtras();
        return bundle.getInt(Common.ACTION_PUT_KEY, Common.PLAY_SONG);
    }

    private Notification getNotification() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mSong.getImage());
        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, getString(R.string.notification_tag));

        return new NotificationCompat.Builder(this, NotifyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_library_music_24)
                .setSubText(getString(R.string.subtext))
                .setContentTitle(mSong.getTitle())
                .setContentText(mSong.getAuthor())
                .setLargeIcon(bitmap)
                .addAction(R.drawable.ic_baseline_fast_rewind_24, getString(R.string.previous), null)
                .addAction(
                        mMusicHelper.isPlaying() ? R.drawable.ic_baseline_pause_circle_outline_24
                                : R.drawable.ic_baseline_play_circle_outline_24,
                        getString(R.string.pause),
                        getPendingIntent(this, mMusicHelper.isPlaying() ? Common.PAUSE_SONG
                                : Common.RESUME_SONG))
                .addAction(R.drawable.ic_baseline_fast_forward_24, getString(R.string.next), null)
                .addAction(R.drawable.ic_baseline_cancel_24, getString(R.string.exit), getPendingIntent(this, Common.CLEAR_SONG))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1, 3)
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .build();
    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, MusicBoardcastReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Common.ACTION_PUT_KEY, action);
        intent.putExtras(bundle);
        return PendingIntent.getBroadcast(getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void getSong(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Song song = (Song) bundle.get(Common.MUSIC_PUT_KEY);
            if (song != null) {
                mSong = song;
                mMusicHelper = new MusicHelper(this, song);
            }
        }
    }
}
