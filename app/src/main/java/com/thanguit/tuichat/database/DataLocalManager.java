package com.thanguit.tuichat.database;

import android.content.Context;

public class DataLocalManager {
    private static DataLocalManager instance;
    private SharedPreferencesManager sharedPreferencesManager;

    private static final String THEME = "THEME";
    private static final String LANGUAGE = "LANGUAGE";

    public static void init(Context context) {
        instance = new DataLocalManager();
        instance.sharedPreferencesManager = new SharedPreferencesManager(context);
    }

    public static DataLocalManager getInstance() {
        if (instance == null) {
            synchronized (DataLocalManager.class) {
                if (instance == null) {
                    instance = new DataLocalManager();
                }
            }
        }
        return instance;
    }

    public static void setTheme(boolean theme) {
        DataLocalManager.getInstance().sharedPreferencesManager.putBooleanValue(THEME.trim(), theme);
    }

    public static boolean getTheme() {
        return DataLocalManager.getInstance().sharedPreferencesManager.getBooleanValue(THEME.trim());
    }
}
