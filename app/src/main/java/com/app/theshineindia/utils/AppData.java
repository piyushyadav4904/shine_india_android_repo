package com.app.theshineindia.utils;


public class AppData {
    public static final String TAG = "MVP";
    public static final String folder_name = "TheShineIndia";

    public static final int theft_detection_count_down_time = 15;
    public static final int SHAKE_THRESHOLD = 100;
    public static final float PROXIMITY_SENSOR_SENSITIVITY = 4;

    public static final int max_wrong_pass_attempts = 2;
    public static int wrong_paas_count = 0;


    public static class Sensor_Type {
        public static boolean is_headset_plugged_in = false;
        public static boolean is_charger_plugged_in = false;
    }

    public static float proximity_sensor_value = 50;

    public static class Sensor_Name {
        public static String shake = "shake";
        public static String proximity = "proximity";
        public static String charger = "charger";
        public static String headset = "headset";
    }

    public static final String authenticate = "authenticate";


}

