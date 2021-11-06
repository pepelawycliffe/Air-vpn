package com.softmaster.airvpn.adapter;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PrefManager {

    public static final String PRF_APP_DATA = "app_data";
    public static final String KEY_RATE_INDEX = "rate_index";

    public static final String MODE_WRITE = "edit";
    public static final String MODE_READ = "read";
    public static final String MODE_DELETE = "delete";
    private SharedPreferences.Editor editor;

    public PrefManager(Context ctx, String PrefName, String mode){
        switch (mode) {
            case MODE_READ:
                SharedPreferences prefs = ctx.getSharedPreferences(PrefName, MODE_PRIVATE);
                break;
            case MODE_WRITE:
                editor = ctx.getSharedPreferences(PrefName, MODE_PRIVATE).edit();
                break;
            case MODE_DELETE:
                editor = ctx.getSharedPreferences(PrefName, MODE_PRIVATE).edit();
                editor.clear().apply();
                break;
        }
    }

    public void SaveIntData(String key, int value){
        editor.putInt(key, value);
        editor.apply();
    }


}
