package com.larsaars.alarmclock.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutActivity;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.danielstone.materialaboutlibrary.util.OpenSourceLicense;

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
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();

        // Add items to card
        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(R.string.app_name)
                .desc(R.string.copyright_lurzapps)
                .icon(R.drawable.ic_launcher)
                .build());

        appCardBuilder.addItem(ConvenienceBuilder.createVersionActionItem(c,
                ContextCompat.getDrawable(c, R.drawable.info),
                getString(R.string.version_title),
                true));

        appCardBuilder.addItem(ConvenienceBuilder.createRateActionItem(c,
                ContextCompat.getDrawable(c, R.drawable.rate_star),
                getString(R.string.rate_title),
                null
        ));

        appCardBuilder.addItem(ConvenienceBuilder.createEmailItem(c,
                ContextCompat.getDrawable(c, R.drawable.mail),
                getString(R.string.send_email_title),
                true,
                getString(R.string.dev_mail),
                getString(R.string.question_concerning_title) + getString(R.string.app_name)));

        appCardBuilder.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                ContextCompat.getDrawable(c, R.drawable.privacy_policy),
                getString(R.string.privacy_policy_title),
                true,
                Uri.parse(getString(R.string.privacy_url))));

        appCardBuilder.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                ContextCompat.getDrawable(c, R.drawable.terms_conditions),
                getString(R.string.terms_and_conditions_title),
                true,
                Uri.parse(getString(R.string.terms_url))));

        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(R.string.app_name_no_exc)
                .desc(R.string.desc_what_is_c2h5oh)
                .icon(R.drawable.continueg)
                .build());

        MaterialAboutCard.Builder resources = new MaterialAboutCard.Builder();

        resources.title(R.string.resources_title);

        resources.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                null,
                "Icons by freepik",
                true,
                Uri.parse("https://www.flaticon.com/authors/freepik")));

        resources.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                null,
                "Icons by Pixel perfect",
                true,
                Uri.parse("https://www.flaticon.com/authors/pixel-perfect")));

        resources.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                null,
                "Firebase Analytics by Google",
                true,
                Uri.parse("https://firebase.google.com/terms/analytics")));


        resources.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                null,
                "Firebase Crashlytics and Firebase App by Google",
                true,
                Uri.parse("https://firebase.google.com/terms/crashlytics")));


        resources.addItem(ConvenienceBuilder.createWebsiteActionItem(c,
                null,
                "Sounds from freesound.org",
                true,
                Uri.parse("freesound.org")));


        return new MaterialAboutList(appCardBuilder.build(),
                resources.build(),
                //the libraries
                ConvenienceBuilder.createLicenseCard(c,
                        null,
                        "Smart App Rate",
                        "2018",
                        "codemybrainsout",
                        OpenSourceLicense.APACHE_2),
                ConvenienceBuilder.createLicenseCard(c,
                        null,
                        "ShowCaseView",
                        "2020",
                        "Mohammad Reza Eram",
                        OpenSourceLicense.APACHE_2),
                ConvenienceBuilder.createLicenseCard(c,
                        null,
                        "material-about-library",
                        "2020",
                        "Daniel Stone",
                        OpenSourceLicense.APACHE_2),
                ConvenienceBuilder.createLicenseCard(c,
                        null,
                        "Fancybuttons",
                        "2019",
                        "Mehdi Sakout",
                        OpenSourceLicense.MIT),
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
                        OpenSourceLicense.APACHE_2)
        );
    }


    @Override
    protected CharSequence getActivityTitle() {
        return getString(R.string.about);
    }
}