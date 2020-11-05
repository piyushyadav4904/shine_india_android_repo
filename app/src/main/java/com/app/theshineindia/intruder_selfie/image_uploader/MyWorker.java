package com.app.theshineindia.intruder_selfie.image_uploader;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.theshineindia.intruder_selfie.IntruderSelfiePresenter;
import com.app.theshineindia.utils.SP;


public class MyWorker extends Worker {
    private Context context;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        //new IntruderSelfiePresenter(getApplicationContext()).checkFileExistence();
        return Result.success();
    }
}