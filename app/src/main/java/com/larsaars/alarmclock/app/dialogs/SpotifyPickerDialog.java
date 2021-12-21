/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 21.12.21, 02:41
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;

import com.larsaars.alarmclock.R;
import com.larsaars.alarmclock.app.activity.SpotifyActivity;
import com.larsaars.alarmclock.ui.etc.CDialog;
import com.larsaars.alarmclock.ui.etc.RootActivity;
import com.larsaars.alarmclock.ui.view.ToastMaker;
import com.larsaars.alarmclock.utils.Constants;
import com.larsaars.alarmclock.utils.Executable;
import com.larsaars.alarmclock.utils.spotify.SpotifyUtils;

import java.util.Objects;

public class SpotifyPickerDialog {

    private static boolean dialogShowing = false;

    /*
     * show edit text dialog in which you can enter a spotify link
     * and verifies it
     * returns a valid spotify link
     */
    public static void show(RootActivity context, @NonNull Executable<String> result) {
        if (dialogShowing)
            return;

        // get spotify package name
        String spotifyPackageName = context.getString(R.string.spotify_package_name);

        // inflate view
        View rootView = context.getLayoutInflater().inflate(R.layout.dialog_spotify_picker, null);

        AppCompatEditText linkEt = rootView.findViewById(R.id.spotifyPickerEditTextLink);

        CDialog.alertDialog(context)
                .setView(rootView)
                .setOnCancelListener(dialog -> dialogShowing = false)
                .setOnDismissListener(dialog -> dialogShowing = false)
                .setTitle(R.string.dialog_spotify_picker_title)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    // get link as spotify object
                    try {
                        String link = SpotifyUtils.spotifyStyleLinkFromURI(Objects.requireNonNull(linkEt.getText()).toString());
                        // test the link
                        Intent spotifyLinkIntent = new Intent(context, SpotifyActivity.class);
                        spotifyLinkIntent.putExtra(Constants.EXTRA_SPOTIFY_LINK, link);
                        // start for result
                        context.activityLauncher.launch(spotifyLinkIntent, testResult -> {
                            if (testResult.getResultCode() == Activity.RESULT_OK)
                                result.run(link);
                        });
                    } catch (IndexOutOfBoundsException e) {
                        ToastMaker.make(context, R.string.no_valid_spotify_link);
                    }
                }).setNeutralButton(R.string.open_spotify, (dialog, which) ->
                // start spotify with intent
                context.startActivity(context.getPackageManager().getLaunchIntentForPackage(spotifyPackageName)))
                .show();

        dialogShowing = true;
    }
}
