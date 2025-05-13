package org.gaze.vpc.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.gaze.vpc.MyApp;


public class SharedPreferencesUtils {
    private static final SharedPreferences sharedPreferences=
            MyApp.getInstance().getSharedPreferences("gaze_tracker",0);

    public static void setInt(String key, int value){
        sharedPreferences.edit().putInt(key,value).apply();
    }
    public static void setString(String key, String value){
        sharedPreferences.edit().putString(key,value).apply();
    }
    public static void setBoolean(String key, boolean value){
        sharedPreferences.edit().putBoolean(key,value).apply();
    }
    public static void setFloat(String key, float value){
        sharedPreferences.edit().putFloat(key,value).apply();
    }
    public static void setLong(String key, long value){
        sharedPreferences.edit().putLong(key,value).apply();
    }

    public static int getInt(String key, int value){
        return  sharedPreferences.getInt(key,value);
    }
    public static String getString(String key, String value){
        return  sharedPreferences.getString(key,value);
    }
    public static boolean getBoolean(String key, boolean value){
        return sharedPreferences.getBoolean(key,value);
    }
    public static float getFloat(String key, float value){
        return sharedPreferences.getFloat(key,value);
    }
    public static long getLong(String key, long value){
        return sharedPreferences.getLong(key,value);
    }


}
