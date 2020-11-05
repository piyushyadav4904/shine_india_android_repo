package com.app.theshineindia.app_locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.app.theshineindia.app_locker.base.AppConstants;
import com.app.theshineindia.app_locker.services.BackgroundManager;
import com.app.theshineindia.app_locker.services.LoadAppListService;
import com.app.theshineindia.app_locker.services.LockService;
import com.app.theshineindia.app_locker.utils.LogUtil;
import com.app.theshineindia.app_locker.utils.SpUtil;


public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        LogUtil.i("Boot service....");
        //TODO: pie compatable done
       // context.startService(new Intent(context, LoadAppListService.class));
        BackgroundManager.getInstance().init(context).startService(LoadAppListService.class);
        if (SpUtil.getInstance().getBoolean(AppConstants.LOCK_STATE, false)) {
            BackgroundManager.getInstance().init(context).startService(LockService.class);
            BackgroundManager.getInstance().init(context).startAlarmManager();
        }
    }
}
