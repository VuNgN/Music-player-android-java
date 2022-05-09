package com.vungn.mymusicplayer2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class MusicBroadcastReceiver extends BroadcastReceiver{
    public static final String ACTION_NOTIFICATION = "com.vungn.keyBroadCastNotification";
    public static final String ACTION_MUSIC = "com.vungn.keyBroadCastMusic";
    private final IntentFilter musicIntentFilter = new IntentFilter(ACTION_MUSIC);
    private final IntentFilter notificationIntentFilter = new IntentFilter(ACTION_NOTIFICATION);

    public void onMusicBroadcastReceiver(Song song, int action, boolean isPlaying) {}

    public void onNotificationBroadcastReceiver(Song song, int action) {}

    private void notifyBroadcast(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int action = bundle.getInt(MusicService.KEY_MUSIC_ACTION, Actions.PLAY_SONG);
            Song song = (Song) bundle.get(MusicService.KEY_MUSIC);
            onNotificationBroadcastReceiver(song, action);
        }
    }

    private void musicBroadcast (Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int action = bundle.getInt(MusicService.KEY_MUSIC_ACTION, Actions.PLAY_SONG);
            Song song = (Song) bundle.get(MusicService.KEY_MUSIC);
            boolean isPlaying = bundle.getBoolean(MusicService.KEY_MUSIC_PLAYING_STATUS);
            onMusicBroadcastReceiver(song, action, isPlaying);
        }
    }

    public void registerNotificationReceiver(Context context) {
        context.registerReceiver(this, notificationIntentFilter);
    }

    public void registerMusicReceiver(Context context) {
        context.registerReceiver(this, musicIntentFilter);
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_MUSIC:
                musicBroadcast(intent);
                break;
            case ACTION_NOTIFICATION:
                notifyBroadcast(intent);
                break;
        }
    }
}
