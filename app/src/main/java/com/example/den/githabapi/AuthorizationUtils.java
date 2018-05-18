package com.example.den.githabapi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AuthorizationUtils {
    private final String PREFERENCES_AUTHORIZED_KEY = "isAuthorized";
    private final String LOGIN_PREFERENCES = "LoginData";
    public String access_token;
    /**
     * This method makes the user authorized
     *
     * @param context current context
     */

    public  void setAuthorized(Context context) {
        context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREFERENCES_AUTHORIZED_KEY, true)
                .apply();
    }

    /**
     * This method makes the user unauthorized
     *
     * @param context current context
     */

    public void logoutPref(Context context) {
        context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREFERENCES_AUTHORIZED_KEY, false)
                .apply();
    }


     // This method checks if the user is authorized
    public boolean isAuthorized(Context context) {
        return context.getSharedPreferences(LOGIN_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(PREFERENCES_AUTHORIZED_KEY, false);
    }

    //	If user is not authorized we finish the main activity
    public void onLogout(Context context) {
        Intent login = new Intent(context, LoginActivity.class);
        login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(login);
    }

    public void save(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("access_token", access_token);
        editor.apply();
    }

    public String restore(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        access_token = prefs.getString("access_token", null);
        return access_token;
    }
}
