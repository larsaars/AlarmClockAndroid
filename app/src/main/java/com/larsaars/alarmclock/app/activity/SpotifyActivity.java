/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 06.12.21, 16:51
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.activity;

import android.os.Bundle;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.larsaars.alarmclock.utils.Constants;
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

        spotifyLink = getIntent().hasExtra(Constants.EXTRA_SPOTIFY_LINK) ?
                getIntent().getStringExtra(Constants.EXTRA_SPOTIFY_LINK) : null;
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
                        // play link if exists
                        if (spotifyLink != null)
                            spotifyAppRemote.getPlayerApi().play(spotifyLink);

                        // disconnect spotify link
                        SpotifyAppRemote.disconnect(spotifyAppRemote);

                        // and exit with positive result
                        setResult(RESULT_OK);
                        finish();
                    }

                    public void onFailure(Throwable throwable) {
                        // Something went wrong when attempting to connect! Handle errors here
                        if (throwable instanceof CouldNotFindSpotifyApp) {
                            ToastMaker.make(getApplicationContext(), R.string.spotify_error_not_installed);
                        } else {
                            ToastMaker.make(getApplicationContext(), "Error: " + throwable.getMessage());
                        }

                        // exit with bad result
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
    }
}
