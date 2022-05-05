package com.vungn.mymusicplayer2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MusicBoardcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getExtras().getInt(Common.ACTION_PUT_KEY, Common.PLAY_SONG);

        Intent intentMusicService = new Intent(context, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Common.ACTION_PUT_KEY , action);
        intentMusicService.putExtras(bundle);
        context.startService(intentMusicService);
    }
}
