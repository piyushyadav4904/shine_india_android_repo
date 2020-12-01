package com.app.theshineindia.sim_tracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.location.LocationTestActivity;
import com.app.theshineindia.sos.Contact;
import com.app.theshineindia.sos.SOSActivity;
import com.app.theshineindia.sos.SOSAdapter;
import com.app.theshineindia.utils.IntentController;
import com.app.theshineindia.utils.SP;
import com.app.theshineindia.utils.Validator;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

public class SimTrackerActivity extends AppCompatActivity {
    Switch switch_sim_tracker;
    ConstraintLayout layoutBottomSheet;
    BottomSheetBehavior sheetBehavior;
    EditText et_name, et_mobile;
    Button btn_save;
    ConstraintLayout constraintLayout4;
    RecyclerView emergencynum_recyclerview;
    RecyclerViewAdapter mAdapter;
    ArrayList<Contact> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim_tracker);

        btn_save = findViewById(R.id.btn_save);
        et_name = findViewById(R.id.et_name);
        et_mobile = findViewById(R.id.et_mobile);
        emergencynum_recyclerview = findViewById(R.id.emergencynum_recyclerview);
        constraintLayout4 = findViewById(R.id.constraintLayout4);
        layoutBottomSheet = findViewById(R.id.emergency_bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        contacts = SP.getContactArrayListForSimTracker(this);

        setToolbar();

        intUI();

        iniListener();

        bottomSheetListener();

        setupRecyclerView();
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
            constraintLayout4.setVisibility(View.VISIBLE);
            emergencynum_recyclerview.setVisibility(View.VISIBLE);
            SharedMethods.startAlarmManagerAndService(this);
        }else {
            constraintLayout4.setVisibility(View.GONE);
            emergencynum_recyclerview.setVisibility(View.GONE);
        }

        switch_sim_tracker.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SP.setBooleanPreference(this, SP.is_sim_tracker_on, true);
                SharedMethods.startAlarmManagerAndService(this);
                constraintLayout4.setVisibility(View.VISIBLE);
                emergencynum_recyclerview.setVisibility(View.VISIBLE);
                TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                   String sim_serial_number = phoneMgr.getSimSerialNumber();
                    SP.setStringPreference(this, SP.sim_serial_number, sim_serial_number);
                }
            } else {
                SP.setBooleanPreference(this, SP.is_sim_tracker_on, false);
                constraintLayout4.setVisibility(View.GONE);
                emergencynum_recyclerview.setVisibility(View.GONE);
                SP.deleteContactListForSimTracker(this);
                SP.setStringPreference(this, SP.sim_serial_number,null);
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







    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        emergencynum_recyclerview.setLayoutManager(linearLayoutManager);
        emergencynum_recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        setAdapter();
    }
    void setAdapter() {
        mAdapter = new RecyclerViewAdapter(new RecyclerViewAdapter.SimTrackerClickListener() {
            @Override
            public void onDeleteClicked(int position) {
                if (mAdapter.getList()!=null && mAdapter.getList().size()>0) {
                    SP.saveContactArrayListForSimTracker(SimTrackerActivity.this, mAdapter.getList());
                }else {
                    SP.deleteContactListForSimTracker(SimTrackerActivity.this);
                }
            }
        });
        emergencynum_recyclerview.setAdapter(mAdapter);
        if (contacts!=null && contacts.size()>0) {
            mAdapter.setList(contacts);
        }
    }
    private void bottomSheetListener() {
        btn_save.setOnClickListener(v -> {
            String name = et_name.getText().toString().trim();
            String mobile = et_mobile.getText().toString().trim();

            if (validateEveryField(name, mobile)) {
                Contact contact = new Contact(name, mobile, null, null);
                if (contacts==null){
                    contacts = new ArrayList<>();
                }
                contacts.add(contact);
                mAdapter.setList(contacts);
                SP.saveContactArrayListForSimTracker(this, contacts);
//                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                et_name.setText("");
                et_mobile.setText("");
            }
        });
    }

    private boolean validateEveryField(String name, String mobile) {
        if (!Validator.isNameValid(this, name)) {
            return false;

        } else if (!Validator.isMobileValid(this, mobile)) {
            return false;
        }

        return true;
    }


    public void openAddContact(View view) {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }
}
