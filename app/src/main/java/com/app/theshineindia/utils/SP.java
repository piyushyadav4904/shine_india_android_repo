package com.app.theshineindia.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.app.theshineindia.sos.Contact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Save data in local by Shared preference
 */
public class SP {
    private static SharedPreferences.Editor editor;

    //keys
    public static final String contact_list_sim_tracker = "contact_list_sim_tracker";
    public static final String fcm_token = "fcm_token";
    public static final String login = "login";
    public static final String sim_serial_number = "sim_serial_number";
    public static final String user_id = "user_id";
    public static final String name = "name";
    public static final String email = "email";
    public static final String mobile = "mobile";
    public static final String password = "password";
    public static final String user_data = "user_data";
    public static final String secret_code = "secret_code";
    public static final String is_intruder_selfie_on = "is_intruder_selfie_on";
    public static final String is_sim_tracker_on = "is_sim_tracker_on";
    public static final String is_sim_card_changed = "is_sim_card_changed";
    public static final String prev_sim_count = "prev_sim_count";
    public static final String sos_response = "sos_response";
    public static final String active_sos_contact_list = "active_sos_contact_list";
    public static final String hidden_app_list = "hidden_app_list";
    public static final String last_latitude = "last_latitude";
    public static final String last_longitude = "last_longitude";
    public static final String last_address = "last_address";
    public static final String is_last_intruder_selfie_uploaded = "is_last_intruder_selfie_uploaded";

    public static class Sensor_Type {
        public static String is_shake_detection_on = "is_shake_detection_on";
        public static String is_proximity_detection_on = "is_proximity_detection_on";
        public static String is_charger_detection_on = "is_charger_detection_on";
        public static String is_headset_detection_on = "is_headset_detection_on";
    }


    public static boolean setStringPreference(Context context, String key, String value) {
        if (context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences != null && !TextUtils.isEmpty(key)) {
                if (value == null) {
                    return true;
                }
                editor = preferences.edit();
                editor.putString(key, value);
                return editor.commit();
            }
        }

        return false;
    }

    public static String getStringPreference(Context context, String key) {
        String value = null;
        if (context != null) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences != null) {
                value = preferences.getString(key, null);
            }
        }

        return value;
    }

    public static boolean removeStringPreference(Context context, String key) {
        if (context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences != null && !TextUtils.isEmpty(key)) {
                editor = preferences.edit();
                editor.remove(key);
                return editor.commit();
            }
        }

        return false;
    }

    public static boolean setBooleanPreference(Context context, String key, boolean value) {
        if (context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences != null && !TextUtils.isEmpty(key)) {
                editor = preferences.edit();
                editor.putBoolean(key, value);
                return editor.commit();
            }
        }

        return false;
    }

    public static boolean getBooleanPreference(Context context, String key) {
        if (context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences != null) {
                return preferences.getBoolean(key, false);
            }
        }

        return false;
    }

    public static void saveArrayList(Context context, String key, ArrayList<String> list) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();

    }

    public static ArrayList<String> getArrayList(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static boolean logoutFunction(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        editor.clear().commit();

        if (getStringPreference(context, user_id) == null) {
            return true;
        } else {
            return false;
        }
    }





    public static void saveContactArrayListForSimTracker(Context context, ArrayList<Contact> list) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(contact_list_sim_tracker, json);
        editor.apply();

    }

    public static ArrayList<Contact> getContactArrayListForSimTracker(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = prefs.getString(contact_list_sim_tracker, null);
        Type type = new TypeToken<ArrayList<Contact>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static void deleteContactListForSimTracker(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(contact_list_sim_tracker);
        editor.apply();
    }
}
