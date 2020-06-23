package com.nickotonglo.streamingapp.ui.main;

import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.nickotonglo.streamingapp.R;

import java.io.IOException;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private Uri mp4VideoUri;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private ProgressBar mProgressBar;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel

//        mp4VideoUri = Uri.parse("http://mirrors.standaloneinstaller.com/video-sample/star_trails.mp4");
        mp4VideoUri = Uri.parse("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4");
    }

    private void initialisePlayer(){
        playerView = getView().findViewById(R.id.video_view);
        mProgressBar = getView().findViewById(R.id.exo_buffering);
        mProgressBar.setVisibility(View.VISIBLE);

        player = new SimpleExoPlayer.Builder(getActivity()).build();
        playerView.setPlayer(player);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(), Util.getUserAgent(getActivity(), this.getString(R.string.app_name)));
        // This is the MediaSource representing the media to be played.
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);
        // Prepare the player with the source.
        player.prepare(mediaSource, false, false);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);

//        player.addListener(new Player.EventListener() {
//            @Override
//            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//
//            }
//
//            @Override
//            public void onPlayerError(ExoPlaybackException error) {
//                Log.d("LOG_initialisePlayer()",error.getMessage());
//            }
//        });

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady){
                    Log.d("LOG_initialisePlayer()","playWhenReady: true, state: "+playbackState);
                } else {
                    Log.d("LOG_initialisePlayer()","playWhenReady: false, state: "+playbackState);
                }

                switch(playbackState){
                    /*
                    * Player.STATE_IDLE = 1
                    * Player.STATE_BUFFERING = 2
                    * Player.STATE_READY = 3
                    * Player.STATE_ENDED = 4
                    * */
                    case Player.STATE_IDLE:
                    case Player.STATE_ENDED:
                    case Player.STATE_BUFFERING:
                        playerView.setKeepScreenOn(false);
                        break;
                    case Player.STATE_READY:
                        playerView.setKeepScreenOn(true);
                        break;
                }


            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                    IOException cause = error.getSourceException();
                    if (cause instanceof HttpDataSource.HttpDataSourceException) {
                        /*Log.d("LOG_initialisePlayer()","onPlayerError(): cause - "+cause);*/
                        Toast.makeText(getActivity(), R.string.error_player_unable_to_connect, Toast.LENGTH_SHORT).show();
                        HttpDataSource.HttpDataSourceException httpError = (HttpDataSource.HttpDataSourceException) cause;
                        // This is the request for which the error occurred.
                        DataSpec requestDataSpec = httpError.dataSpec;
                        // It's possible to find out more about the error both by casting and by
                        // querying the cause.
                        /*Log.d("LOG_initialisePlayer()","onPlayerError(): requestDataSpec - "+requestDataSpec);*/
                        if (httpError instanceof HttpDataSource.InvalidResponseCodeException) {
                            // Cast to InvalidResponseCodeException and retrieve the response code,
                            // message and headers.
                            /*Log.d("LOG_initialisePlayer()","onPlayerError(): httpError1 - "+((HttpDataSource.InvalidResponseCodeException) cause).responseMessage);*/
                        } else {
                            // Try calling httpError.getCause() to retrieve the underlying cause,
                            // although note that it may be null.
                            /*Log.d("LOG_initialisePlayer()","onPlayerError(): httpError2 - "+httpError.getCause());*/
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initialisePlayer();
            Log.d("LOG_onStart()","initialisePlayer() started");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initialisePlayer();
            Log.d("LOG_onResume()","initialisePlayer() started");
        }
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

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(getView().SYSTEM_UI_FLAG_LOW_PROFILE
                | getView().SYSTEM_UI_FLAG_FULLSCREEN
                | getView().SYSTEM_UI_FLAG_LAYOUT_STABLE
                | getView().SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | getView().SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | getView().SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }
}