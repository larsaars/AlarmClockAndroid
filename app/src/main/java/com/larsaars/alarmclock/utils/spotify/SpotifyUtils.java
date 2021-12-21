/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 21.12.21, 02:50
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.utils.spotify;

public class SpotifyUtils {
    /*
     * convert link from this form (uri):
     * https://open.spotify.com/playlist/5ifMsPihlkYEGCakqfqj17
     * to:
     * spotify:playlist:5ifMsPihlkYEGCakqfqj17
     */
    public static String spotifyStyleLinkFromURI(String uri) throws StringIndexOutOfBoundsException {
        final String openSpotifyCom = "open.spotify.com";
        String result = uri.substring(
                uri.indexOf(openSpotifyCom) + openSpotifyCom.length(), uri.lastIndexOf('?'))
                .replace('/', ':');

        return "spotify" + result;
    }

}
