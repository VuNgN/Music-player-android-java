package com.vungn.mymusicplayer2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
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
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int action = bundle.getInt(Actions.ACTION_PUT_KEY, Actions.PLAY_SONG);
                Song song = (Song) bundle.get(Actions.MUSIC_PUT_KEY);
                handleBottomView(song, action);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(mBroadcastReceiver, new IntentFilter(Actions.SENT_BROAD_CAST));
        retrieveView();
        handleClickListener();
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
        bundle.putInt(Actions.ACTION_PUT_KEY, action);
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
        Song song = new Song("Tháng mấy em nhớ anh",
                            "Hà Anh Tuấn",
                            R.drawable.thang_may_em_nho_anh,
                            R.raw.thang_may_em_nho_anh);
        bundle.putSerializable(Actions.MUSIC_PUT_KEY, song);
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
        unregisterReceiver(mBroadcastReceiver);
    }
}