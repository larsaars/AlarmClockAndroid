/*
 *  Created by Lars Specht
 *  Copyright (c) 2021. All rights reserved.
 *  last modified by me on 20.12.21, 13:49
 *  project Alarm Clock in module Alarm_Clock.app
 */

package com.larsaars.alarmclock.app.activity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.danielstone.materialaboutlibrary.util.OpenSourceLicense;
import com.larsaars.alarmclock.R;

import java.util.Objects;

public class AboutActivity extends MaterialAboutActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    @NonNull
    @Override
    protected MaterialAboutList getMaterialAboutList(@NonNull final Context c) {
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder()
                // Add items to card
                .addItem(new MaterialAboutTitleItem.Builder()
                        .text(R.string.app_name)
                        .icon(R.drawable.ic_launcher)
                        .desc(R.string.app_description)
                        .build())
                .addItem(ConvenienceBuilder.createVersionActionItem(c,
                        ContextCompat.getDrawable(c, R.drawable.info),
                        getString(R.string.version_title),
                        true))
                .addItem(ConvenienceBuilder.createRateActionItem(c,
                        ContextCompat.getDrawable(c, R.drawable.rate_star),
                        getString(R.string.rate_title),
                        null))
                .addItem(ConvenienceBuilder.createEmailItem(c,
                        ContextCompat.getDrawable(c, R.drawable.mail),
                        getString(R.string.send_email_title),
                        true,
                        getString(R.string.dev_mail),
                        getString(R.string.question_concerning_title) + " " + getString(R.string.app_name)))
                .addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                        ContextCompat.getDrawable(c, R.drawable.privacy_policy),
                        getString(R.string.privacy_policy_title),
                        true,
                        Uri.parse(getString(R.string.privacy_policy_url))))
                .addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                        ContextCompat.getDrawable(c, R.drawable.terms_conditions),
                        getString(R.string.terms_and_conditions_title),
                        true,
                        Uri.parse(getString(R.string.terms_conditions_url))));
                /*.addItem(ConvenienceBuilder.createWebsiteActionItem(
                        this,
                        ContextCompat.getDrawable(this, R.drawable.bug),
                        getString(R.string.report_bug_github),
                        false,
                        Uri.parse(getString(R.string.github_bug_report_url)))); */


        MaterialAboutCard.Builder resources = new MaterialAboutCard.Builder()
                .title(R.string.resources_title)
                .addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                        null,
                        "Icons by freepik",
                        true,
                        Uri.parse("https://www.flaticon.com/authors/freepik")))
                .addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                        null,
                        "Icons by Pixel perfect",
                        true,
                        Uri.parse("https://www.flaticon.com/authors/pixel-perfect")))
                .addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                        null,
                        "Icons by KP Arts",
                        true,
                        Uri.parse("https://www.flaticon.com/de/autoren/kp-arts")))
                .addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                        null,
                        "Firebase Analytics by Google",
                        true,
                        Uri.parse("https://firebase.google.com/terms/analytics")))

                .addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                        null,
                        "Firebase Crashlytics and Firebase App by Google",
                        true,
                        Uri.parse("https://firebase.google.com/terms/crashlytics")))
                .addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                        null,
                        "Sounds from freesound.org",
                        true,
                        Uri.parse("freesound.org")));


        return new MaterialAboutList(appCardBuilder.build(),
                resources.build(),
                // this source
                ConvenienceBuilder.createLicenseCard(
                        this,
                        null,
                        getString(R.string.app_name),
                        "2021",
                        "Lars Specht (larsaars or Lurzapps)",
                        OpenSourceLicense.APACHE_2
                ),
                // the libraries
                ConvenienceBuilder.createLicenseCard(c,
                        null,
                        "Smart App Rate",
                        "2018",
                        "codemybrainsout",
                        OpenSourceLicense.APACHE_2),
                ConvenienceBuilder.createLicenseCard(c,
                        null,
                        "material-about-library",
                        "2020",
                        "Daniel Stone",
                        OpenSourceLicense.APACHE_2),
                ConvenienceBuilder.createLicenseCard(c,
                        null,
                        "prettytime",
                        "2020",
                        "OCPsoft",
                        OpenSourceLicense.APACHE_2),
                ConvenienceBuilder.createLicenseCard(c,
                        null,
                        "KeyboardVisibilityEvent",
                        "2020",
                        "Yasuhiro Shimizu",
                        OpenSourceLicense.APACHE_2),
                ConvenienceBuilder.createLicenseCard(c,
                        null,
                        "AndroidViewAnimations",
                        "2020",
                        "代码家",
                        OpenSourceLicense.MIT),
                ConvenienceBuilder.createLicenseCard(c,
                        null,
                        "TransformationLayout",
                        "2021",
                        "Jaewoong Eum",
                        OpenSourceLicense.APACHE_2),
                ConvenienceBuilder.createLicenseCard(c,
                        null,
                        "Calendar-Day-View",
                        "2017",
                        "khacpham",
                        OpenSourceLicense.MIT),
                ConvenienceBuilder.createLicenseCard(c,
                        null,
                        "Fancy ShowCaseView",
                        "2021",
                        " Faruk Toptaş",
                        OpenSourceLicense.APACHE_2)
        );
    }


    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.about);
    }
}