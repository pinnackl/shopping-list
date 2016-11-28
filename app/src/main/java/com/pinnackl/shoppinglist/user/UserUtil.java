package com.pinnackl.shoppinglist.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by htutuaku on 28/11/2016.
 */

public class UserUtil {
    public void save(JSONObject oUser, User user, Context context) throws JSONException {
        String lastname = oUser.getString("lastname");
        String firstname = oUser.getString("firstname");
        String email = oUser.getString("email");
        String token = oUser.getString("token");

        user.setLastname(lastname);
        user.setFirstname(firstname);
        user.setEmail(email);
        user.setToken(token);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("firstname", firstname);
        editor.putString("lastname", lastname);
        editor.putString("token", token);
        editor.putString("email", email);
        editor.commit();
    }
}
