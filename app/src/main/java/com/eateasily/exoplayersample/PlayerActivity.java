package com.eateasily.exoplayersample;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {

    protected StyledPlayerView playerView;
    private SimpleExoPlayer player;
    private ProgressBar progressBar;
    private ImageView imgFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.progress_bar);
        imgFullScreen = findViewById(R.id.img_full_screen);

        initUi();
    }

    /**
     * Initialize Ui
     */
    private void initUi() {
        PlayerErrorMessageProvider playerErrorMessageProvider = new PlayerErrorMessageProvider(this);

        playerView.setErrorMessageProvider(playerErrorMessageProvider);
        playerView.requestFocus();

        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else if (state == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        imgFullScreen.setOnClickListener(view -> {
            if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                imgFullScreen.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.exo_controls_fullscreen_exit));
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                imgFullScreen.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.exo_controls_fullscreen_enter));
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
    }

    /**
     * Initialize Player and play.
     */
    private void initializePlayer() {

        //Create media item list and add to player
        List<MediaItem> mMediaItems = new ArrayList<>();
        mMediaItems.add(MediaItem.fromUri(Uri.parse(getString(R.string.dash_video))));
        mMediaItems.add(MediaItem.fromUri(Uri.parse(getString(R.string.hls_video))));
        player.addMediaItems(mMediaItems);

        //Prepare player and play
        player.prepare();
        player.play();
    }

    /**
     * Stops the player
     */
    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideSystemUi();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    /**
     * Hide system Ui
     */
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }
}

