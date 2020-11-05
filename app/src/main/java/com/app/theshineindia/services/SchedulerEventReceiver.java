package com.app.theshineindia.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.utils.SP;

public class SchedulerEventReceiver extends BroadcastReceiver {
    public static final String ACTION_ALARM_RECEIVER = "ACTION_ALARM_RECEIVER";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.d("1111", "======================= SchedulerEventReceiver ========================");
        manageServices(context);
    }

    private void manageServices(Context context) {
        if (SharedMethods.isSingleDetectionEnabled(context)) {
            SharedMethods.startMyService(context, SingleService.class);

            if (SP.getBooleanPreference(context, SP.is_intruder_selfie_on)) {
                SharedMethods.initIntruderSelfie(context);
            }

        } else {
            SharedMethods.stopAlarmManager(context);
            SharedMethods.stopServices(context, SingleService.class);
        }
    }

}
