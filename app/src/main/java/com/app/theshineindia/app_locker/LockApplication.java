package com.app.theshineindia.app_locker;

import android.content.Context;
import android.os.PowerManager;

import com.app.theshineindia.app_locker.activities.lock.GestureUnlockActivity;
import com.app.theshineindia.app_locker.base.BaseActivity;
import com.app.theshineindia.app_locker.utils.SpUtil;

import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.List;


public class LockApplication extends LitePalApplication {

    private static LockApplication application;
    private static List<BaseActivity> activityList;
    private PowerManager.WakeLock wakeLock;

    public static LockApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        //Crash reporter utility
        //CrashReporter.initialize(this, getCacheDir().getPath());

        SpUtil.getInstance().init(application);
        activityList = new ArrayList<>();
    }

    public void doForCreate(BaseActivity activity) {
        activityList.add(activity);
    }

    public void doForFinish(BaseActivity activity) {
        activityList.remove(activity);
    }

    public void clearAllActivity() {
        try {
            for (BaseActivity activity : activityList) {
                if (activity != null && !clearAllWhiteList(activity))
                    activity.clear();
            }
            activityList.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean clearAllWhiteList(BaseActivity activity) {
        return activity instanceof GestureUnlockActivity;
    }


    public PowerManager.WakeLock getWakeLock() {
        if (wakeLock == null) {
            // lazy loading: first call, create wakeLock via PowerManager.
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, ":wakeup");
        }
        return wakeLock;
    }
}
