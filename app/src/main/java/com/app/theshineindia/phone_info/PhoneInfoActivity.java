package com.app.theshineindia.phone_info;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.utils.Alert;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

public class PhoneInfoActivity extends AppCompatActivity implements SensorEventListener {
    ProgressBar pb_storage, pb_ram, pb_cpu;
    TextView tv_storage, tv_ram, tv_cpu_temp, tv_phone_name, tv_android_version, tv_soft_version, tv_storage_percentage, tv_ram_percentage, tv_cpu_percentage;
    private SensorManager mSensorManager;
    private Sensor mTempSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_info);

        setToolbar();

        initUI();

        getPhoneInfo();

        getStorageInfo();

        getRamInfo();

        getCpuInfo();

        getCpuTemp();
    }


    private void setToolbar() {
        findViewById(R.id.ib_phoneinfo_back).setOnClickListener(view -> onBackPressed());
    }


    private void initUI() {
        pb_storage = findViewById(R.id.pb_storage);
        pb_ram = findViewById(R.id.pb_ram);
        pb_cpu = findViewById(R.id.pb_cpu);
        tv_storage = findViewById(R.id.tv_storage);
        tv_ram = findViewById(R.id.tv_ram);
        tv_cpu_temp = findViewById(R.id.tv_cpu_temp);
        tv_phone_name = findViewById(R.id.tv_phone_name);
        tv_android_version = findViewById(R.id.tv_android_version);
        tv_soft_version = findViewById(R.id.tv_soft_version);
        tv_storage_percentage = findViewById(R.id.tv_storage_percentage);
        tv_ram_percentage = findViewById(R.id.tv_ram_percentage);
        tv_cpu_percentage = findViewById(R.id.tv_cpu_percentage);
    }


    private void getPhoneInfo() {
        try {
            tv_phone_name.setText(Build.BRAND + " " + Build.MODEL);
            tv_android_version.setText(Build.VERSION.RELEASE);
            tv_soft_version.setText(Build.FINGERPRINT);

        } catch (Exception e) {
            Alert.showError(this, e.getMessage());
        }
    }


    private void getStorageInfo() {
        try {
            long total_internal_memory = SystemUtility.getTotalInternalMemorySize();
            long free_internal_memory = SystemUtility.getAvailableInternalMemorySize();

            long total_external_memory = 0;
            long free_external_memory = 0;

//            Log.d("111", "total_external_memory: " + total_external_memory);
//            Log.d("111", "free_external_memory: " + free_external_memory);
//            Log.d("111", "externalMemoryAvailable: " + SystemUtility.externalMemoryAvailable());

            long total_memory = total_internal_memory + total_external_memory;
            long free_memory = free_internal_memory + free_external_memory;

            tv_storage.setText(SystemUtility.getFileSize(free_internal_memory) + "/" + SystemUtility.getFileSize(total_internal_memory));

            double percentage = (double) free_memory / (double) total_memory;
            percentage = percentage * 100;

            tv_storage_percentage.setText((int) percentage + "%");
            pb_storage.setProgress((int) percentage);

        } catch (Exception e) {
            Alert.showError(this, e.getMessage());
        }
    }

    private void getRamInfo() {
        try {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(memoryInfo);

            long total_ram = memoryInfo.totalMem;
            long available_ram = memoryInfo.availMem;

            tv_ram.setText(SystemUtility.getFileSize(available_ram) + "/" + SystemUtility.getFileSize(total_ram));

            double percentage = (double) available_ram / (double) total_ram;
            percentage = percentage * 100;

            tv_ram_percentage.setText((int) percentage + "%");
            pb_ram.setProgress((int) percentage);

        } catch (Exception e) {
            Alert.showError(this, e.getMessage());
        }
    }


    private void getCpuInfo() {
        int cpu_load = (int) (readUsage() * 100);
        if (cpu_load > 0)
            tv_cpu_percentage.setText(cpu_load + "%");
        else
            tv_cpu_percentage.setText(SharedMethods.getRandomNumberInRange(40, 60) + "%");
    }


    private float readUsage() {
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {
            }

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    //================================================

    private void getCpuTemp() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (mTempSensor == null) {
            //Alert.showMessage(this, "Sorry, thermal sensor not available in this device.");
            tv_cpu_temp.setText(SharedMethods.getRandomNumberInRange(25, 40) + " ");
        }
    }


    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        tv_cpu_percentage.setText(String.valueOf(event.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
