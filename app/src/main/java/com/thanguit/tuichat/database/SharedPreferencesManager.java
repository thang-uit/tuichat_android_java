package com.thanguit.tuichat.database;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String SharedPreferencesManager = "SharedPreferencesManager";
    private final Context context;

    public SharedPreferencesManager(Context context) {
        this.context = context;
    }

    public void putBooleanValue(String key, boolean value) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(SharedPreferencesManager, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBooleanValue(String key) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(SharedPreferencesManager, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public void putStringValue(String key, String value) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(SharedPreferencesManager, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringValue(String key) {
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(SharedPreferencesManager, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }
}
