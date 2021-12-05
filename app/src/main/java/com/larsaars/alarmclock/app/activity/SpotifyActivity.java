/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 05.12.21, 15:31
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.activity;

import android.os.Bundle;
import android.util.Log;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.protocol.types.Track;

public class SpotifyActivity extends RootActivity {

    private static final String REDIRECT_URI = "com.larsaars.alarmclock://callback";

    private SpotifyAppRemote spotifyAppRemote;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(getString(R.string.spotify_client_id))
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        SpotifyActivity.this.spotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                        if (throwable instanceof CouldNotFindSpotifyApp) {
                            ToastMaker.make(getApplicationContext(), R.string.spotify_error_not_installed);
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(spotifyAppRemote);
    }

    private void connected() {
        // Play a playlist
        // spotify:playlist:37i9dQZF1DX2sUQwD7tbmL
        spotifyAppRemote.getPlayerApi().play("https://open.spotify.com/track/2zJ0u78baAFxX9SoSpeyoi?si=CAah6HcFSGOgGaMUVzmijQ&utm_source=copy-link");

        // Subscribe to PlayerState
        spotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                });
    }
}
