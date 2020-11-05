package com.app.theshineindia.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.app.theshineindia.baseclasses.SharedMethods;

public class SchedulerSetupReceiver extends BroadcastReceiver {
    private static final int EXEC_INTERVAL = 60 * 1000;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("1111", "======================= SchedulerSetupReceiver =======================");

        //if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        initAlarmManager(context);
    }

    public static void initAlarmManager(Context context) {
        if (!SharedMethods.isSingleDetectionEnabled(context)) {
            Log.d("1111", "AlarmManager : did not stated : as no detection enabled");
            return;
        }

        SharedMethods.startAlarmManagerAndService(context);
        Log.d("1111", "AlarmManager : started from boot");

//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent_receiver = new Intent(context, SchedulerEventReceiver.class); // explicit intent
//        PendingIntent intentExecuted = PendingIntent.getBroadcast(context, 0, intent_receiver, PendingIntent.FLAG_CANCEL_CURRENT);
//        if (alarmManager != null && intentExecuted != null) {
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), EXEC_INTERVAL, intentExecuted);
//            Log.d("1111", "AlarmManager 2 : started from boot");
//        }
    }

}
