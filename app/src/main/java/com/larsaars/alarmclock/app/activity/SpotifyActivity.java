/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 14.12.21, 19:01
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.activity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Executable;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;

public class SpotifyActivity extends RootActivity {

    private static final String REDIRECT_URI = "com.larsaars.alarmclock://callback";

    private String spotifyLink;
    // format:
    // spotify:playlist:37i9dQZF1DX2sUQwD7tbmL

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get spotify link as intent extra
        spotifyLink = getIntent().hasExtra(Constants.EXTRA_SPOTIFY_LINK) ?
                getIntent().getStringExtra(Constants.EXTRA_SPOTIFY_LINK) : null;
    }

    /*
     * connect to spotify api and play
     * returns with listener if result was good or a failure
     */
    public static void connectAndPlay(Context context, boolean showAuthView, String spotifyLink, Executable<Integer> result) {
        // init audio manager for volume control later on
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);

        // connect with params
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(context.getString(R.string.spotify_client_id))
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(showAuthView)
                        .build();

        // connect and execute
        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        if (spotifyLink != null) {
                            // set media channel volume to alarm channel volume to be sure that
                            float percentageOfAlarmVolume = ((float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) /
                                    ((float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                            audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    Math.round(percentageOfAlarmVolume * ((float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC))),
                                    0
                            );
                            // play link if exists
                            spotifyAppRemote.getPlayerApi().play(spotifyLink);
                            // switch to the current device
                            spotifyAppRemote.getConnectApi().connectSwitchToLocalDevice();
                        }

                        // disconnect spotify link again
                        SpotifyAppRemote.disconnect(spotifyAppRemote);

                        // and exit with positive result
                        result.run(RESULT_OK);
                    }

                    public void onFailure(Throwable throwable) {
                        // Something went wrong when attempting to connect! Handle errors here
                        if (throwable instanceof CouldNotFindSpotifyApp) {
                            ToastMaker.make(context, R.string.spotify_error_not_installed);
                        } else {
                            ToastMaker.make(context, "Error: " + throwable.getMessage());
                        }

                        // exit with bad result
                        result.run(RESULT_CANCELED);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // execute show and play activity with activity result
        connectAndPlay(this, true, spotifyLink, result -> {
            // set result and exit
            setResult(result);
            finish();
        });
    }
}
