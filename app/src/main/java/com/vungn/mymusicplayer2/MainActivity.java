package com.vungn.mymusicplayer2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Button playButton;
    private Button stopButton;
    private ImageView songImage;
    private TextView songTitle;
    private TextView songAuthor;
    private ImageButton pauseOrResumeButton;
    private ImageButton exitButton;
    private RelativeLayout bottomLayout;
    private boolean isPlaying;
    private final MusicBroadcastReceiver mBroadcastReceiver = new MusicBroadcastReceiver() {
        @Override
        public void onMusicBroadcastReceiver(Song song, int action, boolean isPlaying) {
            super.onMusicBroadcastReceiver(song, action, isPlaying);
            MainActivity.this.isPlaying = isPlaying;
            handleBottomView(song, action);
        }

        @Override
        public void onNotificationBroadcastReceiver(Song song, int action) {
            super.onNotificationBroadcastReceiver(song, action);
            handleBottomView(song, action);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getMusic();
        mBroadcastReceiver.registerMusicReceiver(this);
        mBroadcastReceiver.registerNotificationReceiver(this);
        retrieveView();
        handleClickListener();
    }

    private void getMusic() {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction(MusicService.ACTION_GET_MUSIC_ON_START_UP);
        startService(intent);
    }

    private void handleClickListener() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPlayButtonClick();
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStopButtonClick();
            }
        });
        pauseOrResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPauseOrResumeButtonClick();
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStopButtonClick();
            }
        });
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(this, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(MusicService.KEY_MUSIC_ACTION, action);
        intent.putExtras(bundle);
        startService(intent);
    }

    private void onPauseOrResumeButtonClick() {
        int action = isPlaying ? Actions.PAUSE_SONG : Actions.RESUME_SONG;
        isPlaying = !isPlaying;
        sendActionToService(action);
        handlePauseOrResumeButton();
    }

    private void onStopButtonClick() {
        bottomLayout.setVisibility(View.GONE);
        sendActionToService(Actions.CLEAR_SONG);
    }

    private void onPlayButtonClick() {
        Intent intentMusicService = new Intent(this, MusicService.class);
        Bundle bundle = new Bundle();
        Song song = new Song("Th??ng m???y em nh??? anh",
                            "H?? Anh Tu???n",
                            R.drawable.thang_may_em_nho_anh,
                            R.raw.thang_may_em_nho_anh);
        bundle.putSerializable(MusicService.KEY_MUSIC, song);
        intentMusicService.putExtras(bundle);
        startService(intentMusicService);
        handleBottomView(song, Actions.PLAY_SONG);
    }

    private void handleBottomView(Song song, int action) {
            if (action != Actions.CLEAR_SONG) {
                isPlaying = action != Actions.PAUSE_SONG;
                bottomLayout.setVisibility(View.VISIBLE);
                songImage.setImageResource(song.getImage());
                songTitle.setText(song.getTitle());
                songAuthor.setText(song.getAuthor());
                exitButton.setImageResource(R.drawable.ic_baseline_cancel_24);
                handlePauseOrResumeButton();
            } else {
                bottomLayout.setVisibility(View.GONE);
            }
    }

    private void handlePauseOrResumeButton() {
        pauseOrResumeButton.setImageResource(
                isPlaying
                        ? R.drawable.ic_baseline_pause_circle_outline_24
                        : R.drawable.ic_baseline_play_circle_outline_24);
    }

    private void retrieveView() {
        songTitle = findViewById(R.id.name_song_textview);
        songImage = findViewById(R.id.song_image);
        songAuthor = findViewById(R.id.author_song_textview);
        playButton = findViewById(R.id.play_button);
        stopButton = findViewById(R.id.stop_button);
        exitButton = findViewById(R.id.cancel_button);
        bottomLayout = findViewById(R.id.relative_layout);
        pauseOrResumeButton = findViewById(R.id.pause_resume_button);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBroadcastReceiver.unregisterReceiver(this);
    }
}