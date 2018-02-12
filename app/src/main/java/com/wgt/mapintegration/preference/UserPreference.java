package com.wgt.mapintegration.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.wgt.mapintegration.model.UserModel;
import com.wgt.mapintegration.utils.Constant;

/**
 * Created by root on 10/2/18.
 */

public class UserPreference {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public UserPreference(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constant.PREFERENCE.PREF_USER_FILE, context.MODE_PRIVATE);
    }

    public UserModel getUser() {
        String name, email, pic;
        boolean logged_status;

        name = sharedPreferences.getString(Constant.PREFERENCE.PREF_USER_NAME, null);
        email = sharedPreferences.getString(Constant.PREFERENCE.PREF_USER_EMAIL, null);
        pic = sharedPreferences.getString(Constant.PREFERENCE.PREF_USER_PIC_LOC, null);
        logged_status = sharedPreferences.getBoolean(Constant.PREFERENCE.PREF_USER_LOGGED_STATUS, false);

        return new UserModel(email, name, pic, logged_status);
    }

    public boolean saveUser(UserModel user) {
        if (user == null || user.getEmail() == null || user.getName()== null) {
            return false;
        }

        editor = sharedPreferences.edit();
        editor.putString(Constant.PREFERENCE.PREF_USER_EMAIL, user.getEmail());
        editor.putString(Constant.PREFERENCE.PREF_USER_NAME, user.getName());
        editor.putString(Constant.PREFERENCE.PREF_USER_PIC_LOC, user.getPicLoc());
        editor.putBoolean(Constant.PREFERENCE.PREF_USER_LOGGED_STATUS, user.getLoggedStatus());

        editor.apply();

        return true;
    }

    public void logout() {
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public boolean getLoggedStatus() {
        return sharedPreferences.getBoolean(Constant.PREFERENCE.PREF_USER_LOGGED_STATUS, false);
    }
}
