package com.example.phakneath.ckcc_homework;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.phakneath.ckcc_homework.Entity.person;

public class Preferences {

    private static final String USERNAME = "USERNAME";
    private static final String EMAIL = "EMAIL";
    private static final String PASSWORD = "PASSWORD";

    private static final String PREFERENCE = "PREFERENCE";

    private static SharedPreferences getPreference(Context context)
    {
        return  context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
    }

    public static void save(Context context, person per)
    {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putString(USERNAME, per.getUsername());
        editor.putString(EMAIL, per.getEmail());
        editor.putString(PASSWORD, per.getPassword());
        editor.commit();
    }

    public static person getPerson(Context context)
    {
        SharedPreferences preferences = getPreference(context);
        person per = new person();
        per.setUsername(preferences.getString(USERNAME,null));
        per.setEmail(preferences.getString(EMAIL,null));
        per.setPassword(preferences.getString(PASSWORD, null));
        return  per;
    }

    public static void remove(Context context)
    {
        SharedPreferences.Editor editor = getPreference(context).edit();
        editor.putString(USERNAME,null);
        editor.putString(EMAIL, null);
        editor.putString(PASSWORD,null);
        editor.commit();
    }

    public static boolean isLogin(Context context)
    {
        SharedPreferences preferences = getPreference(context);
        if (preferences.getString(USERNAME,null) == null)
        {
            return  false;
        }
        else return  true;
    }
}
