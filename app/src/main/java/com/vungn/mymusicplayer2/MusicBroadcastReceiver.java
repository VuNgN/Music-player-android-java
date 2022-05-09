package com.vungn.mymusicplayer2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class MusicBroadcastReceiver extends BroadcastReceiver{
    public static final String KEY_BROAD_CAST_NOTIFICATION = "com.vungn.keyBroadCastNotification";
    public static final String KEY_BROAD_CAST_MUSIC = "com.vungn.keyBroadCastMusic";
    private final IntentFilter musicIntentFilter = new IntentFilter(KEY_BROAD_CAST_MUSIC);
    private final IntentFilter notifyIntentFilter = new IntentFilter(KEY_BROAD_CAST_NOTIFICATION);

    public void getMusicBroadcastReceiver(Song song, int action, boolean isPlaying) {}

    public void getNotifyBroadcastReceiver(Song song, int action) {}

    public IntentFilter getMusicIntentFilter() {
        return this.musicIntentFilter;
    }

    public IntentFilter getNotifyIntentFilter() {
        return this.notifyIntentFilter;
    }

    private void notifyBroadcast(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int action = bundle.getInt(MusicService.KEY_ACTION, Actions.PLAY_SONG);
            Song song = (Song) bundle.get(MusicService.KEY_MUSIC);
            getNotifyBroadcastReceiver(song, action);
        }
    }

    private void musicBroadcast (Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int action = bundle.getInt(MusicService.KEY_ACTION, Actions.PLAY_SONG);
            Song song = (Song) bundle.get(MusicService.KEY_MUSIC);
            boolean isPlaying = bundle.getBoolean(MusicService.KEY_MUSIC_STATUS);
            getMusicBroadcastReceiver(song, action, isPlaying);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case KEY_BROAD_CAST_MUSIC:
                musicBroadcast(intent);
                break;
            case KEY_BROAD_CAST_NOTIFICATION:
                notifyBroadcast(intent);
                break;
        }
    }
}
