package com.app.theshineindia.sim_tracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Toast;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.location.LocationTestActivity;
import com.app.theshineindia.sos.SOSActivity;
import com.app.theshineindia.utils.IntentController;
import com.app.theshineindia.utils.SP;

import java.util.ArrayList;

public class SimTrackerActivity extends AppCompatActivity {
    Switch switch_sim_tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim_tracker);

        setToolbar();

        intUI();

        iniListener();
    }

    private void setToolbar() {
        findViewById(R.id.ib_tracker_back).setOnClickListener(view -> onBackPressed());
    }

    private void intUI() {
        switch_sim_tracker = findViewById(R.id.switch_sim_tracker);
    }


    private void iniListener() {
        if (SP.getBooleanPreference(this, SP.is_sim_tracker_on)) {
            switch_sim_tracker.setChecked(true);
            SharedMethods.startAlarmManagerAndService(this);
        }

        switch_sim_tracker.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SP.setBooleanPreference(this, SP.is_sim_tracker_on, true);
                SharedMethods.startAlarmManagerAndService(this);
            } else {
                SP.setBooleanPreference(this, SP.is_sim_tracker_on, false);
            }
        });

//        switch_sim_tracker.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                ArrayList<String> active_contact_list = SP.getArrayList(this, SP.active_sos_contact_list);
//                if (active_contact_list == null || active_contact_list.size() == 0) {
//                    Toast.makeText(this, "No active contact found in Local Database. Please go to SOS and activate some contact if not activated", Toast.LENGTH_LONG).show();
//                    switch_sim_tracker.setChecked(false);
//                } else {
//                    SP.setBooleanPreference(this, SP.is_sim_tracker_on, true);
//                    SharedMethods.startAlarmManagerAndService(this);
//                }
//            } else {
//                SP.setBooleanPreference(this, SP.is_sim_tracker_on, false);
//                //SharedMethods.stopAlarmManagerAndService(this);
//            }
//        });


        findViewById(R.id.imageView16).setOnLongClickListener(v -> {
            startActivity(new Intent(this, LocationTestActivity.class));
            return true;
        });
    }

}
