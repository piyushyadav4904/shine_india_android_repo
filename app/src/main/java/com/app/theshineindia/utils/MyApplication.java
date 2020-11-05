package com.app.theshineindia.utils;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    Context context;
    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        //new ChargingService().onCreate();
        //startService(new Intent(getApplicationContext(), ChargingService.class));
    }
}
