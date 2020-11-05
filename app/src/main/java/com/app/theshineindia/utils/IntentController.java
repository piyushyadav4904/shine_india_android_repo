package com.app.theshineindia.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.theshineindia.home.HomeActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IntentController {

    public static void sendIntent(Context context1, Class<?> cls) {
        context1.startActivity(makeIntent(context1, cls));
    }

    public static <T extends Object> Intent makeIntent(Context context1, Class<?> cls, Map<String, T> map) {
        Intent intent = new Intent(context1, cls);
        for (Map.Entry<String, T> entry : map.entrySet()) {
            try {
                intent.putExtra(entry.getKey(), Serializer.serialize(entry.getValue()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent makeIntent(Context context1, Class<?> cls) {
        Intent intent = new Intent(context1, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static <T extends Object> void sendIntent(Context context1, Class<?> cls, Map<String, T> map) {
        context1.startActivity(makeIntent(context1, cls, map));
    }

    public static Object receiveIntent(Intent intent, String key) {
        try {
            return Serializer.deserialize(intent.getExtras().getByteArray(key));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class CustomHashMap extends HashMap {

        public <T extends Object> CustomHashMap put(String key, T value) {
            super.put(key, value);
            return CustomHashMap.this;
        }
    }



   /* public static void sendIntent(Context context1, Class<?> cls, String key, String value){
        Intent intent = new Intent(context1, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(key, value);
        context1.startActivity(intent);
    }

    public static void sendIntent(Context context1, Class<?> cls, String key, byte[] byteObject){
        Intent intent = new Intent(context1, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(key, byteObject);
        context1.startActivity(intent);
    }

    public static void sendIntent(Context context1, Class<?> cls, String key, String value, byte[] byteObject){
        Intent intent = new Intent(context1, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(key, value);
        intent.putExtra(key, byteObject);
        context1.startActivity(intent);
    }

    public static void sendIntent(Context context1, Class<?> cls, String key1, int value, String key2, byte[] byteObject){
        Intent intent = new Intent(context1, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(key1, value);
        intent.putExtra(key2, byteObject);
        context1.startActivity(intent);
    }*/

    public static Intent my_intent = null;

    public static void openDuressActivity(Context context) {
        try {
            if (my_intent == null) {
                my_intent = new Intent();
                my_intent.setClassName("com.app.theshineindia", "com.app.theshineindia.duressconfsett.DuressActionActivity");
                my_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(my_intent);
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void gotToActivityNoBack(AppCompatActivity activity, Class<?> cls) {
        try {
            Intent intent = makeIntent(activity, cls);
            activity.startActivity(intent);
            activity.finish();
        } catch (Exception e) {
            Alert.showError(activity, e.getMessage());
        }
    }

    public static void gotToHomeActivityNoBack(AppCompatActivity activity) {
        try {
            Intent intent = new Intent(activity, HomeActivity.class);
            activity.startActivity(intent);
            activity.finish();
        } catch (Exception e) {
            Alert.showError(activity, e.getMessage());
        }
    }
}
