package com.vungn.mymusicplayer2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
    private MusicHelper mMusicHelper;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleBottomView(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Common.ACTION_SENT_TO_MAIN_ACTIVITY));
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
                int action = mMusicHelper.isPlaying() ? Common.PAUSE_SONG : Common.RESUME_SONG;
                sendActionToService(action);
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendActionToService(Common.CLEAR_SONG);
            }
        });
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(this, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Common.ACTION_PUT_KEY, action);
        intent.putExtras(bundle);
        startService(intent);
    }

    private void onStopButtonClick() {
        sendActionToService(Common.CLEAR_SONG);
    }

    private void onPlayButtonClick() {
        Intent intentMusicService = new Intent(this, MusicService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Common.MUSIC_PUT_KEY, new Song
                ("Tháng mấy em nhớ anh",
                        "Hà Anh Tuấn",
                        R.drawable.thang_may_em_nho_anh,
                        R.raw.thang_may_em_nho_anh));
        intentMusicService.putExtras(bundle);
        startService(intentMusicService);
    }

    private void handleBottomView(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mMusicHelper = (MusicHelper) bundle.get(Common.MUSIC_HELPER_PUT_KEY);
            if (mMusicHelper != null && mMusicHelper.getAction() != Common.CLEAR_SONG) {
                Song song = mMusicHelper.getSong();
                bottomLayout.setVisibility(View.VISIBLE);
                songImage.setImageResource(song.getImage());
                songTitle.setText(song.getTitle());
                songAuthor.setText(song.getAuthor());
                pauseOrResumeButton.setImageResource(
                        mMusicHelper.isPlaying()
                                ? R.drawable.ic_baseline_pause_circle_outline_24
                                : R.drawable.ic_baseline_play_circle_outline_24);
                exitButton.setImageResource(R.drawable.ic_baseline_cancel_24);
            } else {
                bottomLayout.setVisibility(View.GONE);
            }
        }
    }

    private void retrieveView() {
        playButton = findViewById(R.id.play_button);
        stopButton = findViewById(R.id.stop_button);
        songImage = findViewById(R.id.song_image);
        songTitle = findViewById(R.id.name_song_textview);
        songAuthor = findViewById(R.id.author_song_textview);
        pauseOrResumeButton = findViewById(R.id.pause_resume_button);
        exitButton = findViewById(R.id.cancel_button);
        bottomLayout = findViewById(R.id.relative_layout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }
}