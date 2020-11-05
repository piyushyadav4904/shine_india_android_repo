package com.app.theshineindia.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.app.theshineindia.app_presenter.MessagePresenter;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.loaders.JSONFunctions;
import com.app.theshineindia.sos.SOSActivity;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.AppData;
import com.app.theshineindia.utils.SP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import locationprovider.davidserrano.com.LocationProvider;

public class SingleService extends Service implements SensorEventListener {
    SensorManager sensorManager;
    private long lastUpdate = -1;
    private float last_x, last_y, last_z;
    private boolean isShaked = false;
    SingleReceiver singleReceiver;
    TelephonyManager telMgr;
    long prev_time = 0;
    long current_time = 0;

    Geocoder geocoder;
    List<Address> addresses;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("1111", "======================= SingleService started =====================");
        try {
            buildNotification();

            getCurrentLocation();

            if (sensorManager == null) {
                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            }

            if (sensorManager != null) {
                startShake(sensorManager);
                startProximity(sensorManager);
            } else {
                Log.d("1111", "Error getting sensor manager");
            }

            startChargeDetection();

            startSimCardDetection();

            SharedMethods.startAlarmManager(this);

        } catch (Exception e) {
            Toast.makeText(this, "SingleService: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void buildNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "The Shine India",
                    NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    private void startShake(SensorManager sensorManager) {
        boolean is_register = sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        if (!is_register) {
            sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        }
        Log.d("1111", "SHAKE detection Started ----->");
    }

    private void startProximity(SensorManager sensorManager) {
        boolean is_register = sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        if (!is_register) {
            sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
        }
        Log.d("1111", "PROXIMITY detection Started ----->");
    }

    private void startChargeDetection() {
        singleReceiver = new SingleReceiver();

        final IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);

        registerReceiver(singleReceiver, filter);
        Log.d("1111", "Charger detection Started ----->");
        Log.d("1111", "headset detection Started ----->");
    }

    private void startSimCardDetection() {
        //checkIsSimCardRemoved();
        telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Log.d("1111", "SimCard detection Started ----->");
    }


    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
            sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));

            if (singleReceiver != null)
                unregisterReceiver(singleReceiver);
        }

        Log.d("1111", "Service destroyed");
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        //================================TYPE_PROXIMITY============================================
        if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            try {
                AppData.proximity_sensor_value = sensorEvent.values[0];
                //Log.d("1111", "proximity_sensor_value Service: " + AppData.proximity_sensor_value);

                if (SP.getBooleanPreference(this, SP.Sensor_Type.is_proximity_detection_on)
                        && sensorEvent.values[0] >= AppData.PROXIMITY_SENSOR_SENSITIVITY) {

                    SharedMethods.playAlarm(this, SP.Sensor_Type.is_proximity_detection_on);
                    Toast.makeText(this, "Removed from pocket", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d("1111", "ERROR : " + e.getMessage());
            }
        }

        //================================TYPE_ACCELEROMETER========================================
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (SP.getBooleanPreference(this, SP.Sensor_Type.is_shake_detection_on)) {
                checkPhoneShake(sensorEvent);
            }

            if (SP.getBooleanPreference(this, SP.is_sim_tracker_on)) {
                checkIsSimCardRemoved();
            }
        }
    }


    private void checkPhoneShake(SensorEvent sensorEvent) {
        try {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                if (speed > AppData.SHAKE_THRESHOLD && !isShaked) {
                    // yes, this is a shake action! Do something about it!
                    isShaked = false;

                    Toast.makeText(this, "Mobile shake", Toast.LENGTH_SHORT).show();
                    SharedMethods.playAlarm(this, SP.Sensor_Type.is_shake_detection_on);
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }

        } catch (Exception e) {
            Log.d("1111", "ERROR : " + e.getMessage());
        }
    }

    private void checkIsSimCardRemoved() {
        current_time = System.currentTimeMillis();
        if (current_time - prev_time >= 25 * 1000) {
            if (SP.getStringPreference(this, SP.prev_sim_count) == null) return;
            int prev_sim_count = Integer.parseInt(SP.getStringPreference(this, SP.prev_sim_count));
            int current_sim_count = SharedMethods.getAvailableSimCount(this);

            if (prev_sim_count > current_sim_count
                    || prev_sim_count < current_sim_count
                    || SP.getBooleanPreference(this, SP.is_sim_card_changed)) { //it work even if sim removed then inserted (count same)

                SP.setBooleanPreference(this, SP.is_sim_card_changed, true);
                Log.d("1111", "Sim card removed ---->");

                if (JSONFunctions.isInternetOn(this))
                    new Handler().postDelayed(this::sendSOSMessage, 24 * 1000);
                else
                    Log.d("1111", "No internet available: ");
            }
            prev_time = current_time;
        }
    }

    public void sendSOSMessage() {
        //call api to send sms
        new MessagePresenter(this, "sim_change").requestSendSosMessage("sim");
    }

    //============================ Geo Location ============================
    private void getCurrentLocation() {
        Log.d("1111", "Location detection Started ----->");

        //create a callback
        LocationProvider.LocationCallback callback = new LocationProvider.LocationCallback() {

            @Override
            public void onNewLocationAvailable(float lat, float lon) {
                //location update
                SP.setStringPreference(SingleService.this, SP.last_latitude, String.valueOf(lat));
                SP.setStringPreference(SingleService.this, SP.last_longitude, String.valueOf(lon));

                getAddressByLatLong(lat, lon);

                Log.d("1111", "latitude: " + lat + "   longitude: " + lon);
            }

            @Override
            public void locationServicesNotEnabled() {
                //failed finding a location
                Toast.makeText(SingleService.this, "We are not able to find your current location. Please enable gps", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateLocationInBackground(float lat, float lon) {
                //if a listener returns after the main locationAvailable callback, it will go here
            }

            @Override
            public void networkListenerInitialised() {
                //when the library switched from GPS only to GPS & network
            }

            @Override
            public void locationRequestStopped() {

            }
        };

        //initialise an instance with the two required parameters
        LocationProvider locationProvider = new LocationProvider.Builder()
                .setContext(this)
                .setListener(callback)
                .create();

        //start getting location
        try {
            locationProvider.requestLocation();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getAddressByLatLong(float lat, float lon) {
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            //Log.d("1111", "addresses: " + addresses);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                SP.setStringPreference(SingleService.this, SP.last_address, address.getAddressLine(0) + ", " + address.getLocality());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}