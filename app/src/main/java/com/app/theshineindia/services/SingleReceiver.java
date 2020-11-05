package com.app.theshineindia.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.utils.AppData;
import com.app.theshineindia.utils.SP;


public class SingleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        //========================= CHARGER DETECTION ====================
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        if (plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS) {
            //AppData.Sensor_Type.is_charger_plugged_in = true;

        } else if (SP.getBooleanPreference(context, SP.Sensor_Type.is_charger_detection_on)) {
            SP.setBooleanPreference(context, SP.Sensor_Type.is_charger_detection_on, false);    //to detect only one time
            //AppData.Sensor_Type.is_charger_plugged_in = false;

            Toast.makeText(context, "Charger disconnected", Toast.LENGTH_SHORT).show();
            SharedMethods.playAlarm(context, SP.Sensor_Type.is_charger_detection_on);
        }


        //========================= HEADSET DETECTION ====================
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    Log.d("1111", "Headset is unplugged");
                    //AppData.Sensor_Type.is_headset_plugged_in = false;
                    if (SP.getBooleanPreference(context, SP.Sensor_Type.is_headset_detection_on)) {
                        SP.setBooleanPreference(context, SP.Sensor_Type.is_headset_detection_on, false);    //to detect only one time

                        Toast.makeText(context, "Headset unplugged", Toast.LENGTH_SHORT).show();
                        SharedMethods.playAlarm(context, SP.Sensor_Type.is_headset_detection_on);
                    }
                    break;

                case 1:
                    //AppData.Sensor_Type.is_headset_plugged_in = true;
                    Log.d("1111", "Headset is plugged");
                    break;

                default:
                    //AppData.Sensor_Type.is_headset_plugged_in = false;
                    Log.d("1111", "I have no idea what the headset state is");
            }
        }
    }

}
