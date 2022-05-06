package com.vungn.mymusicplayer2;

public interface Actions {
    String MUSIC_PUT_KEY = "Put_music_key";
    String ACTION_PUT_KEY = "Put_action_key";
    String MUSIC_HELPER_PUT_KEY = "Put_music_helper_key";
    String SENT_BROAD_CAST = "Sent_to_main_activity";
    String IS_PLAYING_PUT_KEY = "Put_is_playing_key";

    int PLAY_SONG = 0;
    int PAUSE_SONG = 1;
    int RESUME_SONG = 2;
    int CLEAR_SONG = 3;
}
