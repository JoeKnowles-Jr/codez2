package com.jk.codez;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class Settings {
    private static SharedPreferences prefs;
    public static String LAST_CONNECTION = "LAST_CONNECTION";
    public static String DEFAULT_CONNECTION = "DEFAULT_CONNECTION";
    public static String NONE = "NONE";
    public static String LOCAL = "LOCAL";
    public static String PREFERRED_DB = "PREFERRED_DB";

    public static void load(@NonNull Activity a) {
        prefs = a.getPreferences(0);
        if (!prefs.contains(LAST_CONNECTION)) {
            prefs.edit().putString(LAST_CONNECTION, NONE).apply();
        }
        if (!prefs.contains(PREFERRED_DB)) {
            prefs.edit().putString(PREFERRED_DB, LOCAL).apply();
        }
    }

    public static String getLastConnection() {
        return prefs.getString(LAST_CONNECTION, NONE);
    }

    public static void setLastConnection(String conn) {
        prefs.edit().putString(LAST_CONNECTION, conn).apply();
    }

    public static String getDbType() {
        return prefs.getString(PREFERRED_DB, LOCAL);
    }

    public static void setDbType(String dbType) {
        prefs.edit().putString(PREFERRED_DB, dbType).apply();
    }
}
