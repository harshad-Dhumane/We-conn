package com.mohitsprojects.instaclone.AndroidConstants;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference
{
    static final String PREF_USER_NAME= "username";
    static final String NAME= "name";
    static final String USER_NAME= "username";
    static final String BIO= "bio";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, String userName)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_NAME, userName);
        editor.commit();
    }

    public static String getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "");
    }

    public static void setName(Context ctx, String name)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(NAME, name);
        editor.commit();
    }

    public static String getName(Context ctx)
    {
        return getSharedPreferences(ctx).getString(NAME, "");
    }

    public static void setUser(Context ctx, String username)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(USER_NAME, username);
        editor.commit();
    }

    public static String getUser(Context ctx)
    {
        return getSharedPreferences(ctx).getString(USER_NAME, "");
    }

    public static void setBio(Context ctx, String bio)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(BIO, bio);
        editor.commit();
    }

    public static String getBio(Context ctx)
    {
        return getSharedPreferences(ctx).getString(BIO, "");
    }


}