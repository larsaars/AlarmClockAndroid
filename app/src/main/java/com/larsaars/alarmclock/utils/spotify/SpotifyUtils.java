/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 07.12.21, 18:04
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils.spotify;

import java.util.Locale;

public class SpotifyUtils {
    /*
     * convert link from this form (uri):
     * https://open.spotify.com/playlist/5ifMsPihlkYEGCakqfqj17
     * to:
     * spotify:playlist:5ifMsPihlkYEGCakqfqj17
     */
    public static String spotifyStyleLinkFromURI(String uri) {
        final String openSpotifyCom = "open.spotify.com";
        String result = uri.substring(
                uri.indexOf(openSpotifyCom) + openSpotifyCom.length(), uri.lastIndexOf('?'))
                .replace('/', ':');

        return "spotify" + result;
    }

}
