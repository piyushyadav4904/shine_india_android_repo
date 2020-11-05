package com.app.theshineindia.theft_detection;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.secret_code.SecretCodeActivity;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.Animator;
import com.app.theshineindia.utils.AppData;
import com.app.theshineindia.utils.SP;

public class TheftDetectionActivity extends AppCompatActivity {
    Switch switch_phone_shake, switch_proxity, switch_charger, switch_headphone;
    public static final int REQUEST_CODE_SHAKE = 1;
    public static final int REQUEST_CODE_PROXIMITY = 2;
    public static final int REQUEST_CODE_CHARGER = 3;
    public static final int REQUEST_CODE_HEADSET = 4;

    CountDownTimer countDownTimer;
    boolean disable_back_button = false;
    ConstraintLayout CL_timer;
    TextView tv_timer, tv_type;
    int REQUEST_CODE;


    //ChargingBR chargingBR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theft_detection);

        setToolbar();

        initUI();

        setListener();

        //reset
        AppData.proximity_sensor_value = 50;

        //chargingBR = new ChargingBR();
        //SharedMethods.startMyService(this, SingleService.class);

        checkSwitches();

        addOverlay();
    }

    boolean askedForOverlayPermission = false;
    int OVERLAY_PERMISSION_CODE = 1111;

    public void addOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                askedForOverlayPermission = true;
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
            }
        }
    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        if (chargingBR != null)
//            unregisterReceiver(chargingBR);
//    }


    private void initUI() {
        switch_phone_shake = findViewById(R.id.switch_phone_shake);
        switch_proxity = findViewById(R.id.switch_proxity);
        switch_charger = findViewById(R.id.switch_charger);
        switch_headphone = findViewById(R.id.switch_headphone);
        tv_timer = findViewById(R.id.tv_timer);
        tv_type = findViewById(R.id.tv_type);
        CL_timer = findViewById(R.id.CL_timer);
    }

    private void setToolbar() {
        findViewById(R.id.ib_changepassword_back).setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        if (disable_back_button) {
            return;
        }

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            //SharedMethods.startAlarmManagerAndService(this); //for charger and headphone , pre detection

            //Hide keyboard if opened
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
            Alert.showError(this, e.getMessage());
        }
    }

    private void checkSwitches() {
        //useful after add secret code and complete auth
        switch_phone_shake.setChecked(SP.getBooleanPreference(this, SP.Sensor_Type.is_shake_detection_on));
        switch_proxity.setChecked(SP.getBooleanPreference(this, SP.Sensor_Type.is_proximity_detection_on));
        switch_charger.setChecked(SP.getBooleanPreference(this, SP.Sensor_Type.is_charger_detection_on));
        switch_headphone.setChecked(SP.getBooleanPreference(this, SP.Sensor_Type.is_headset_detection_on));
    }


    private void setListener() {
        switch_phone_shake.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!SP.getBooleanPreference(this, SP.Sensor_Type.is_shake_detection_on)) {    //already enabled
                    checkSecretCode(REQUEST_CODE_SHAKE);
                    tv_type.setText("Shake Detection \n\n Starts In");
                }
            } else {
                SP.setBooleanPreference(this, SP.Sensor_Type.is_shake_detection_on, false);
            }
        });

        switch_proxity.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!SP.getBooleanPreference(this, SP.Sensor_Type.is_proximity_detection_on)) {
                    checkSecretCode(REQUEST_CODE_PROXIMITY);
                    tv_type.setText("Proximity Detection \n\n Starts In \n\n Please put your phone in the pocket");
                }
            } else {
                SP.setBooleanPreference(this, SP.Sensor_Type.is_proximity_detection_on, false);
            }
        });

        switch_charger.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (Detector.isChargerConnected(this)) {
                    if (!SP.getBooleanPreference(this, SP.Sensor_Type.is_charger_detection_on)) {
                        tv_type.setText("Charger Detection \n\n Starts In");
                        checkSecretCode(REQUEST_CODE_CHARGER);
                    }
                } else {
                    Alert.showMessage(this, "Please plugged in your charger");
                    switch_charger.setChecked(false);
                }
            } else {
                SP.setBooleanPreference(this, SP.Sensor_Type.is_charger_detection_on, false);
                //SharedMethods.stopAlarmManagerAndService(this);
            }
        });

        switch_headphone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!Detector.isHeadsetConnected(this)) {
                    Alert.showMessage(this, "Please plugged in your headset");
                    switch_headphone.setChecked(false);
                    return;
                }

                if (!SP.getBooleanPreference(this, SP.Sensor_Type.is_headset_detection_on)) {
                    tv_type.setText("Headset Detection \n\n Starts In");
                    checkSecretCode(REQUEST_CODE_HEADSET);
                }

            } else {
                SP.setBooleanPreference(this, SP.Sensor_Type.is_headset_detection_on, false);
                //SharedMethods.stopAlarmManagerAndService(this);
            }
        });
    }

    private void checkSecretCode(int REQUEST_CODE) {
        this.REQUEST_CODE = REQUEST_CODE;
        SharedMethods.startAlarmManagerAndService(this);

        if (SP.getStringPreference(this, SP.secret_code) != null) {     //if secret code present
            startCountDown();
        } else {
            Intent my_intent = new Intent(this, SecretCodeActivity.class);
            startActivityForResult(my_intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            startCountDown();
        }

        if (requestCode == OVERLAY_PERMISSION_CODE) {
            askedForOverlayPermission = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "ACTION_MANAGE_OVERLAY_PERMISSION Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void startCountDown() {
        if (countDownTimer != null) {
            return;
        }

        CL_timer.setVisibility(View.VISIBLE);
        tv_timer.setText(AppData.theft_detection_count_down_time + "s");
        disable_back_button = true;

        countDownTimer = new CountDownTimer(AppData.theft_detection_count_down_time * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                tv_timer.setText(millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                CL_timer.setVisibility(View.GONE);

                countDownTimer.cancel();
                countDownTimer = null;

                startDetection();

                disable_back_button = false;
            }
        }.start();
    }


    private void startDetection() {
        if (REQUEST_CODE == REQUEST_CODE_SHAKE) {
            SP.setBooleanPreference(this, SP.Sensor_Type.is_shake_detection_on, true);

        } else if (REQUEST_CODE == REQUEST_CODE_PROXIMITY) {
            SP.setBooleanPreference(this, SP.Sensor_Type.is_proximity_detection_on, true);
            checkProximityStatus();

        } else if (REQUEST_CODE == REQUEST_CODE_CHARGER) {
            SP.setBooleanPreference(this, SP.Sensor_Type.is_charger_detection_on, true);
            checkChargerStatus();

        } else if (REQUEST_CODE == REQUEST_CODE_HEADSET) {
            SP.setBooleanPreference(this, SP.Sensor_Type.is_headset_detection_on, true);
            checkHeadphoneStatus();
        }
    }

    private void checkProximityStatus() {
        //bcz proximity wont work un till some object come in front of it
        //useful when user not putting phone in the pocket
        if (AppData.proximity_sensor_value > AppData.PROXIMITY_SENSOR_SENSITIVITY) {
            SharedMethods.playAlarm(this, SP.Sensor_Type.is_proximity_detection_on);
        }
    }

    private void checkChargerStatus() {
        //useful when user unplugged charger, before count down finished
        // bcz it receive broadcast after some interval,
        // this way it will in instance
        if (!Detector.isChargerConnected(this)) {
            SharedMethods.playAlarm(this, SP.Sensor_Type.is_charger_detection_on);
        }
    }

    private void checkHeadphoneStatus() {
        //useful when user unplugged headset, before count down finished
        // unlike charger it only receive broadcast for one time, when situation occurred
        if (!Detector.isHeadsetConnected(this)) {
            SharedMethods.playAlarm(this, SP.Sensor_Type.is_headset_detection_on);
        }
    }


    public void resetSecretCode(View view) {
        Animator.buttonAnim(this, view);

        if (SP.getStringPreference(this, SP.secret_code) != null) {     //if secret code present
            Intent my_intent = new Intent(this, SecretCodeActivity.class);
            my_intent.putExtra("request_for", "reset_code");
            startActivity(my_intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //SharedMethods.stopAlarmManagerAndService(this);
    }
}
