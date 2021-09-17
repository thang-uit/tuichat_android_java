package com.thanguit.tuichat.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.thanguit.tuichat.database.DataLocalManager;

import java.util.Locale;

public class SettingLanguage {
    private static SettingLanguage instance;

    private SettingLanguage() {
    }

    public static SettingLanguage getInstance() {
        if (instance == null) {
            synchronized (SettingLanguage.class) {
                if (instance == null) {
                    instance = new SettingLanguage();
                }
            }
        }
        return instance;
    }

    public void changeLanguage(Context context, String code) {
        DataLocalManager.init(context);

        Locale locale = new Locale(code);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        DataLocalManager.setLanguage(code); // Set data into SharedPreferences
    }

//    private static void updateResources(Context context, String language) {
//        Locale locale = new Locale(language);
//        Locale.setDefault(locale);
//
//        Configuration configuration = context.getResources().getConfiguration();
//        configuration.setLocale(locale);
//        configuration.setLayoutDirection(locale);
//
//        Context context1 = context.createConfigurationContext(configuration);
//    }
}
