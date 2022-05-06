package com.vungn.mymusicplayer2;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import java.io.Serializable;

public class MusicHelper implements Serializable {
    private MediaPlayer mMediaPlayer;
    private final Song mSong;
    private int mAction;
    private boolean isPlaying;
    private final Context context;

    public MusicHelper(Context context, Song mSong) {
        this.context = context;
        this.mSong = mSong;
    }

    public Song getSong() {
        return mSong;
    }

    public void setAction(int mAction) {
        this.mAction = mAction;
    }

    public int getAction() {
        return mAction;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void handleAction() {
        switch (mAction) {
            case Actions.PLAY_SONG:
                onPlayMusic();
                break;
            case Actions.PAUSE_SONG:
                onPauseMusic();
                break;
            case Actions.RESUME_SONG:
                onResumeMusic();
                break;
            case Actions.CLEAR_SONG:
                onClearMusic();
                break;
        }
    }

    private void onPlayMusic() {
        mMediaPlayer = MediaPlayer.create(context, mSong.getResource());
        mMediaPlayer.start();
        isPlaying = true;
    }

    private void onPauseMusic() {
        if (mMediaPlayer != null && isPlaying) {
            mMediaPlayer.pause();
            isPlaying = false;
        }
    }

    private void onResumeMusic() {
        if (mMediaPlayer != null && !isPlaying) {
            mMediaPlayer.start();
            isPlaying = true;
        }
    }

    private void onClearMusic() {
        context.stopService(new Intent(context, MusicService.class));
        destroyMusic();
    }

    public void destroyMusic() {
        mMediaPlayer.release();
        mMediaPlayer = null;
    }
}
