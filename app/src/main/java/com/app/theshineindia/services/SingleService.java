package com.app.theshineindia.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.app.theshineindia.app_presenter.MessagePresenter;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.loaders.JSONFunctions;
import com.app.theshineindia.sos.Contact;
import com.app.theshineindia.sos.SOSActivity;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.AppData;
import com.app.theshineindia.utils.DualSimManager;
import com.app.theshineindia.utils.SP;
import com.app.theshineindia.utils.SendMessageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import locationprovider.davidserrano.com.LocationProvider;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class SingleService extends Service implements SensorEventListener {
    private static final String TAG = "SingleService";

    private int countForMessageSendWhenSimTrackerOn = 0;
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

//            if (SP.getBooleanPreference(this, SP.is_sim_tracker_on) && countForMessageSendWhenSimTrackerOn < 1 &&
//                    SP.getContactArrayListForSimTracker(this) != null && SP.getContactArrayListForSimTracker(this).size() > 0) {
//                countForMessageSendWhenSimTrackerOn++;
//                checkIsSimCardRemoved();
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        countForMessageSendWhenSimTrackerOn = 0;
//                    }
//                }, 180000);
//            }

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
        //Log.d("1111", "SHAKE detection Started ----->");
    }

    private void startProximity(SensorManager sensorManager) {
        boolean is_register = sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        if (!is_register) {
            sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
        }
        //Log.d("1111", "PROXIMITY detection Started ----->");
    }

    private void startChargeDetection() {
        singleReceiver = new SingleReceiver();

        final IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);

        registerReceiver(singleReceiver, filter);
        //Log.d("1111", "Charger detection Started ----->");
        //Log.d("1111", "headset detection Started ----->");
    }

    private void startSimCardDetection() {
        //checkIsSimCardRemoved();
        telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Log.d("1111", "SimCard detection Started ----->");
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

            //Log.d(TAG, "onSensorChanged: true");
            if (SP.getBooleanPreference(this, SP.is_sim_tracker_on) && countForMessageSendWhenSimTrackerOn < 1 &&
                    SP.getContactArrayListForSimTracker(this) != null && SP.getContactArrayListForSimTracker(this).size() > 0) {
                countForMessageSendWhenSimTrackerOn++;
                checkIsSimCardRemoved();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        countForMessageSendWhenSimTrackerOn = 0;
                    }
                }, 180000);
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

    @SuppressLint("HardwareIds")
    private void checkIsSimCardRemoved() {
//        if (SP.getStringPreference(this, SP.prev_sim_count) == null) return;
//        int prev_sim_count = Integer.parseInt(SP.getStringPreference(this, SP.prev_sim_count));
//        int current_sim_count = SharedMethods.getAvailableSimCount(this);


        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

//        Log.d(TAG, "checkIsSimCardRemoved_prev_sim_count: " + prev_sim_count);
//        Log.d(TAG, "checkIsSimCardRemoved_current_sim_count: " + current_sim_count);
        Log.d(TAG, "checkIsSimCardRemoved_serial_number_prev: " + SP.getStringPreference(this, SP.sim_serial_number));
//        Log.d(TAG, "checkIsSimCardRemoved_serial_number_sim1: " + phoneMgr.getSimSerialNumber());
        /*if (DualSimManager.getSimSerialNumbersICCID(this).containsKey(DualSimManager.KEY_FOR_SIM_2)){
            Log.d(TAG, "checkIsSimCardRemoved_serial_number_sim2: " + DualSimManager.getSimSerialNumbersICCID(this).get(DualSimManager.KEY_FOR_SIM_2));
        }*/



        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            String sim_1_serial_number = null;
            String sim_2_serial_number = null;


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager subsManager = (SubscriptionManager) this.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

                List<SubscriptionInfo> subsList = subsManager.getActiveSubscriptionInfoList();

                if (subsList!=null) {

                    for (int i = 0; i < subsList.size(); i++) {
                        if (i==0){
                            if (subsList.get(i) != null) {
                                sim_1_serial_number  = subsList.get(i).getIccId();
                            }
                        }
                        else if (i==1){
                            if (subsList.get(i) != null) {
                                sim_2_serial_number  = subsList.get(i).getIccId();
                            }
                        }
                    }

                }
            } else {

                if (phoneMgr != null && phoneMgr.getSimOperatorName() != null) {
                    sim_1_serial_number = phoneMgr.getSimSerialNumber();
                }
                if (DualSimManager.getSimSimOperatorName(this).containsKey(DualSimManager.KEY_FOR_SIM_2)) {
                    sim_2_serial_number = DualSimManager.getSimSerialNumbersICCID(this).get(DualSimManager.KEY_FOR_SIM_2);
                }

            }

            Log.d(TAG, "sim_serial_numbers: "+sim_1_serial_number);
            System.out.println("sim_SERIAL_NO ---- >>"+sim_1_serial_number);


            String temp = "";
            if (sim_1_serial_number!=null){
                temp = temp + sim_1_serial_number + ",";
            }
            if (sim_2_serial_number!=null){
                temp = temp + sim_2_serial_number;
            }
            Log.d(TAG, "sim_serial_numbers: "+temp);

             /// check logical by remove !
            if (SP.getStringPreference(this, SP.sim_serial_number) != null &&
                    !SP.getStringPreference(this, SP.sim_serial_number).contains(temp)) { //it work even if sim removed then inserted (count same)

                SP.setBooleanPreference(this, SP.is_sim_card_changed, true);
                Log.d("1111", "Sim card removed ---->");

                if (JSONFunctions.isInternetOn(this)) {
                    String finalTemp = temp;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendSOSMessage(finalTemp);
                        }
                    }, 20 * 1000);
//                    new Handler().postDelayed(this::sendSOSMessage, 24 * 1000);
                } else
                    Log.d("1111", "No internet available: ");
            }

        }




       /* if (phoneMgr != null && phoneMgr.getSimOperatorName() != null) { // getting ICCID of sime 1  equivalent to  DualSimManager.getSimSerialNumbersICCID(this).containsKey(DualSimManager.KEY_FOR_SIM_1)
            if (SP.getStringPreference(this, SP.sim_serial_number) != null &&
                    !SP.getStringPreference(this, SP.sim_serial_number).contains(phoneMgr.getSimOperatorName())) { //it work even if sim removed then inserted (count same)

                SP.setBooleanPreference(this, SP.is_sim_card_changed, true);
                Log.d("1111", "Sim card removed ---->");

                if (JSONFunctions.isInternetOn(this)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendSOSMessage(phoneMgr.getSimOperatorName());
                        }
                    }, 20 * 1000);
//                    new Handler().postDelayed(this::sendSOSMessage, 24 * 1000);
                } else
                    Log.d("1111", "No internet available: ");
            }
        } else if (DualSimManager.getSimSimOperatorName(this).containsKey(DualSimManager.KEY_FOR_SIM_2)) {
            if (SP.getStringPreference(this, SP.sim_serial_number) != null &&
                    !SP.getStringPreference(this, SP.sim_serial_number).
                            contains(DualSimManager.getSimSimOperatorName(this).get(DualSimManager.KEY_FOR_SIM_2))) { //it work even if sim removed then inserted (count same)

                SP.setBooleanPreference(this, SP.is_sim_card_changed, true);
                Log.d("1111", "Sim card removed ---->");
                if (JSONFunctions.isInternetOn(this)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendSOSMessage(DualSimManager.getSimSimOperatorName(getApplicationContext()).get(DualSimManager.KEY_FOR_SIM_2));
                        }
                    }, 20 * 1000);
//                    new Handler().postDelayed(this::sendSOSMessage, 24 * 1000);
                } else
                    Log.d("1111", "No internet available: ");
                *//*if (JSONFunctions.isInternetOn(this))
                    new Handler().postDelayed(this::sendSOSMessage, 24 * 1000);
                else
                Log.d("1111", "No internet available: ");*//*
            }
        }*/


        /*if (phoneMgr != null && phoneMgr.getSimSerialNumber() != null) { // getting ICCID of sime 1  equivalent to  DualSimManager.getSimSerialNumbersICCID(this).containsKey(DualSimManager.KEY_FOR_SIM_1)
            if (SP.getStringPreference(this, SP.sim_serial_number) != null &&
                    !SP.getStringPreference(this, SP.sim_serial_number).contains(phoneMgr.getSimSerialNumber())) { //it work even if sim removed then inserted (count same)

                SP.setBooleanPreference(this, SP.is_sim_card_changed, true);
                Log.d("1111", "Sim card removed ---->");

                if (JSONFunctions.isInternetOn(this)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendSOSMessage(phoneMgr.getSimSerialNumber());
                        }
                    }, 20 * 1000);
//                    new Handler().postDelayed(this::sendSOSMessage, 24 * 1000);
                } else
                    Log.d("1111", "No internet available: ");
            }
        } else if (DualSimManager.getSimSerialNumbersICCID(this).containsKey(DualSimManager.KEY_FOR_SIM_2)) {
            if (SP.getStringPreference(this, SP.sim_serial_number) != null &&
                    !SP.getStringPreference(this, SP.sim_serial_number).
                            contains(DualSimManager.getSimSerialNumbersICCID(this).get(DualSimManager.KEY_FOR_SIM_2))) { //it work even if sim removed then inserted (count same)

                SP.setBooleanPreference(this, SP.is_sim_card_changed, true);
                Log.d("1111", "Sim card removed ---->");
                if (JSONFunctions.isInternetOn(this)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendSOSMessage(DualSimManager.getSimSerialNumbersICCID(getApplicationContext()).get(DualSimManager.KEY_FOR_SIM_2));
                        }
                    }, 20 * 1000);
//                    new Handler().postDelayed(this::sendSOSMessage, 24 * 1000);
                } else
                    Log.d("1111", "No internet available: ");
                *//*if (JSONFunctions.isInternetOn(this))
                    new Handler().postDelayed(this::sendSOSMessage, 24 * 1000);
                else
                Log.d("1111", "No internet available: ");*//*
            }
        }*/

        /*current_time = System.currentTimeMillis();
        if (current_time - prev_time >= 25 * 1000) {
            if (SP.getStringPreference(this, SP.prev_sim_count) == null) return;
            int prev_sim_count = Integer.parseInt(SP.getStringPreference(this, SP.prev_sim_count));
            int current_sim_count = SharedMethods.getAvailableSimCount(this);

            TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (prev_sim_count > current_sim_count
                    || prev_sim_count < current_sim_count
                    || SP.getBooleanPreference(this, SP.is_sim_card_changed)
                    || (SP.getStringPreference(this, SP.sim_serial_number) != null &&
                    SP.getStringPreference(this, SP.sim_serial_number).equals(phoneMgr.getSimSerialNumber()))) { //it work even if sim removed then inserted (count same)

                SP.setBooleanPreference(this, SP.is_sim_card_changed, true);
                Log.d("1111", "Sim card removed ---->");

                if (JSONFunctions.isInternetOn(this))
                    new Handler().postDelayed(this::sendSOSMessage, 24 * 1000);
                else
                    Log.d("1111", "No internet available: ");
            }
            prev_time = current_time;
        }*/
    }

    public void sendSOSMessage(String simSerialNumberToSend) {
        //call api to send sms
        new MessagePresenter(this, "sim_change").requestSendSosMessage("sim");

//        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        if (SP.getStringPreference(this, SP.sim_serial_number) != null &&
//                !SP.getStringPreference(this, SP.sim_serial_number).equals(phoneMgr.getSimSerialNumber())) {
//
//        }
        if (SP.getContactArrayListForSimTracker(getApplicationContext()) != null && SP.getContactArrayListForSimTracker(getApplicationContext()).size() > 0) {
            // String temp = "Sim card has been removed, be alert!!! \n";
            String temp = "EMARGENCY !\n\n";
            if (SP.getStringPreference(getApplicationContext(), SP.mobile) != null &&
                    !TextUtils.isEmpty(SP.getStringPreference(getApplicationContext(), SP.mobile).trim())) {
                // temp += "Old Phone Number- " + SP.getStringPreference(getApplicationContext(), SP.mobile) + "\n";

                temp += "";

//                temp += getPhone();
//                System.out.println("new--phone--number : "+temp);

            }
            if (SP.getStringPreference(getApplicationContext(), SP.name) != null &&
                    !TextUtils.isEmpty(SP.getStringPreference(getApplicationContext(), SP.name).trim())) {
                //temp += "User Name- " + SP.getStringPreference(getApplicationContext(), SP.name) + "\n";
                temp += "";
            }
            if (SP.getStringPreference(getApplicationContext(), SP.email) != null &&
                    !TextUtils.isEmpty(SP.getStringPreference(getApplicationContext(), SP.email).trim())) {
                //temp += "Email- " + SP.getStringPreference(getApplicationContext(), SP.email) + "\n";
                temp += "";
            }
//            ArrayList<String> _lst = getPhone(simSerialNumberToSend);
//            if (_lst.size() > 0) {
//                for (int i = 0; i < _lst.size(); i++) {
//                    temp += _lst.get(i) + "\n";
//                }
//            }
            if (SP.getStringPreference(getApplicationContext(), SP.last_address) != null &&
                    !TextUtils.isEmpty(SP.getStringPreference(getApplicationContext(), SP.last_address).trim())) {
                //temp += "Address- " + SP.getStringPreference(getApplicationContext(), SP.last_address) + "\n";
                temp += "";
            }
            if (SP.getStringPreference(getApplicationContext(), SP.last_latitude) != null &&
                    !TextUtils.isEmpty(SP.getStringPreference(getApplicationContext(), SP.last_latitude).trim()) &&
                    SP.getStringPreference(getApplicationContext(), SP.last_longitude) != null &&
                    !TextUtils.isEmpty(SP.getStringPreference(getApplicationContext(), SP.last_longitude).trim())) {
//                temp += "LatLong- " + SP.getStringPreference(getApplicationContext(), SP.last_latitude) + ", " + SP.getStringPreference(getApplicationContext(), SP.last_longitude) + "\n";
                temp += "https://maps.google.com/?q=" + SP.getStringPreference(getApplicationContext(), SP.last_latitude) + "," + SP.getStringPreference(getApplicationContext(), SP.last_longitude);
            }

            for (int i = 0; i < SP.getContactArrayListForSimTracker(getApplicationContext()).size(); i++) {
                Contact contact = SP.getContactArrayListForSimTracker(getApplicationContext()).get(i);

                // SEND SMS FROM PHONE DISABLE AS PER CLIENT REQUERMENT..

//                if (contact.getNum() != null && !TextUtils.isEmpty(contact.getNum().trim())) {
//                    SendMessageUtils.SendMessage(contact.getNum(), temp);
//                }
            }
        }

        /*if (SP.getContactArrayListForSimTracker(getApplicationContext())!=null && SP.getContactArrayListForSimTracker(getApplicationContext()).size()>0){
            String temp = "Sim card has been removed, be alert!!! ";
            if (getPhone().size()>0){
                if (getPhone().get(0)!=null && !TextUtils.isEmpty(getPhone().get(0).trim())){
                    temp+=getPhone().get(0)+"\n";
                }
                if (getPhone().get(1)!=null && !TextUtils.isEmpty(getPhone().get(1).trim())){
                    temp+=getPhone().get(1)+"\n";
                }
                if (getPhone().get(2)!=null && !TextUtils.isEmpty(getPhone().get(2).trim())){
                    temp+=getPhone().get(2)+"\n";
                }
                if (getPhone().get(3)!=null && !TextUtils.isEmpty(getPhone().get(3).trim())){
                    temp+= getPhone().get(3)+"\n";
                }
                if (getPhone().get(4)!=null && !TextUtils.isEmpty(getPhone().get(4).trim())){
                    temp+= getPhone().get(4)+"\n";
                }
            }
            if (SP.getStringPreference(getApplicationContext(), SP.mobile)!=null  &&
                    !TextUtils.isEmpty(SP.getStringPreference(getApplicationContext(), SP.mobile).trim())){
                temp+="Old Phone Number- " + SP.getStringPreference(getApplicationContext(), SP.mobile)+"\n";
            }
            if (SP.getStringPreference(getApplicationContext(), SP.name)!=null  &&
                    !TextUtils.isEmpty(SP.getStringPreference(getApplicationContext(), SP.name).trim())){
                temp+="User Name- " + SP.getStringPreference(getApplicationContext(), SP.name)+"\n";
            }
            if (SP.getStringPreference(getApplicationContext(), SP.email)!=null  &&
                    !TextUtils.isEmpty(SP.getStringPreference(getApplicationContext(), SP.email).trim())){
                temp+="Email- " + SP.getStringPreference(getApplicationContext(), SP.email)+"\n";
            }

            for (int i=0;i<SP.getContactArrayListForSimTracker(getApplicationContext()).size();i++){
                Contact contact = SP.getContactArrayListForSimTracker(getApplicationContext()).get(i);
                if (contact.getNum()!=null && !TextUtils.isEmpty(contact.getNum().trim())) {
                    SendMessageUtils.SendMessage(contact.getNum(), temp);
                }
            }
        }*/
    }

    private String getPhone() {

        String newPhoneNumber = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                List<SubscriptionInfo> subscription = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
                for (int i = 0; i < subscription.size(); i++) {
                    SubscriptionInfo info = subscription.get(i);
                    Log.d(TAG, "number " + info.getNumber());
                    Log.d(TAG, "network name : " + info.getCarrierName());
                    Log.d(TAG, "country iso " + info.getCountryIso());
                    newPhoneNumber = info.getNumber();
                }
                return newPhoneNumber;
            }
        }else {
            TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            newPhoneNumber = phoneMgr.getLine1Number();
            return newPhoneNumber;
        }

        return newPhoneNumber;

    }

    @TargetApi(Build.VERSION_CODES.O)
    private ArrayList<String> getPhone(String simSerialNumberToSend) {
        ArrayList<String> _lst = new ArrayList<>();
        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//            _lst.add(String.valueOf(phoneMgr.getCallState()));
            _lst.add("IMEI No.-" + phoneMgr.getImei());
//            _lst.add("Number :-"+phoneMgr.getLine1Number());
//            _lst.add("Serial No.-" + phoneMgr.getSimSerialNumber());
            _lst.add("Serial No.-" + simSerialNumberToSend);
//            _lst.add("Operator :-"+phoneMgr.getSimOperatorName());
//            _lst.add("Subscriber id :-"+phoneMgr.getSubscriptionId());
//            _lst.add("MEI NUMBER :-"+phoneMgr.getMeid());
//            _lst.add("SIM STATE :-"+String.valueOf(phoneMgr.getSimState()));
//            _lst.add("ISO :-"+phoneMgr.getSimCountryIso());
        }
        Log.d("Sim Tracker", "getPhone: " + _lst);

        return _lst;
    }

    /*@TargetApi(Build.VERSION_CODES.O)
    private ArrayList<String> getPhone() {
        ArrayList<String> _lst =new ArrayList<>();
        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            _lst.add(String.valueOf(phoneMgr.getCallState()));
            _lst.add("IMEI NUMBER :-"+phoneMgr.getImei());
            _lst.add("MOBILE NUMBER :-"+phoneMgr.getLine1Number());
            _lst.add("SERIAL NUMBER :-"+phoneMgr.getSimSerialNumber());
            _lst.add("SIM OPERATOR NAME :-"+phoneMgr.getSimOperatorName());
//            _lst.add("MEI NUMBER :-"+phoneMgr.getMeid());
//            _lst.add("SIM STATE :-"+String.valueOf(phoneMgr.getSimState()));
            _lst.add("COUNTRY ISO :-"+phoneMgr.getSimCountryIso());
        }
        Log.d("Sim Tracker", "getPhone: "+_lst);

        return _lst;
    }*/

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