package com.manan.dev.shineymca.Utility;

import android.content.Context;
import android.content.SharedPreferences;

public class Methods {
    public static void callSharedPreference(Context context, String userEmail) {
        SharedPreferences sharedPref = context.getSharedPreferences("shared_preference_file", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", userEmail);
        editor.commit();
    }

    public static String getEmailSharedPref(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("shared_preference_file", Context.MODE_PRIVATE);
        return sharedPref.getString("email", "default");
    }
    public static void callUserIDSharedPreference(Context context, String userID) {
        SharedPreferences sharedPref = context.getSharedPreferences("shared_preference_file", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("uid", userID);
        editor.commit();
    }

    public static String getUserIDSharedPref(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences("shared_preference_file", Context.MODE_PRIVATE);
        return sharedPref.getString("uid", "default");
    }
}
