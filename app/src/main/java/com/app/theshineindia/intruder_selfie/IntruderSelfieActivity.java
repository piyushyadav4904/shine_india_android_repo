package com.app.theshineindia.intruder_selfie;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.SP;

public class IntruderSelfieActivity extends AppCompatActivity {
    Switch switch_intruder_selfie;
    AdminReceiver adminReceiver;
    boolean is_device_admin_enabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introder_selfie);

        adminReceiver = new AdminReceiver();

        setupToolbar();

        intUI();

        iniListener();

        startDeviceAdminPrompt();
    }

    private void startDeviceAdminPrompt() {
        ComponentName cn = new ComponentName(this, AdminReceiver.class);
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (!devicePolicyManager.isAdminActive(cn)) {
            Intent deviceAdminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            deviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
            deviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getResources().getString(R.string.device_admin_explanation));
            startActivityForResult(deviceAdminIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                is_device_admin_enabled = true;
            } else if (resultCode == Activity.RESULT_CANCELED) {
                is_device_admin_enabled = false;
                Alert.showMessage(this, "We need device admin permission to activate this feature");
            }
        }
    }

    private void initReceiver() {
        ComponentName cn = new ComponentName(this, AdminReceiver.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.device_admin_explanation));
        startActivity(intent);
    }

    private void intUI() {
        switch_intruder_selfie = findViewById(R.id.switch_intruder_selfie);
    }

    private void setupToolbar() {
        findViewById(R.id.ib_tracker_back).setOnClickListener(view -> onBackPressed());
    }


    private void iniListener() {
        switch_intruder_selfie.setChecked(SP.getBooleanPreference(this, SP.is_intruder_selfie_on));

        switch_intruder_selfie.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (is_device_admin_enabled) {
                    SP.setBooleanPreference(this, SP.is_intruder_selfie_on, true);
                    SharedMethods.initIntruderSelfie(this);
                    SharedMethods.startAlarmManagerAndService(this);
                } else {
                    switch_intruder_selfie.setChecked(false);
                    startDeviceAdminPrompt();
                }
            } else {
                SP.setBooleanPreference(this, SP.is_intruder_selfie_on, false);
            }
        });
    }

}
