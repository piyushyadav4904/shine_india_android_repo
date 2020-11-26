package com.app.theshineindia.sos;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.theshineindia.R;
import com.app.theshineindia.app_presenter.MessagePresenter;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.Animator;
import com.app.theshineindia.utils.SP;
import com.app.theshineindia.utils.SendMessageUtils;
import com.app.theshineindia.utils.Validator;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import locationprovider.davidserrano.com.LocationProvider;

public class SOSActivity extends AppCompatActivity implements SosClickListener {
    RecyclerView mEmergencyRecyclerView;
    SOSAdapter mAdapter;
    ArrayList<Contact> emergencyList = new ArrayList<>();
    ArrayList<String> active_contact_list = new ArrayList<>();
    ConstraintLayout layoutBottomSheet;
    ImageView add_contact;
    BottomSheetBehavior sheetBehavior;
    SosPresenter presenter;
    EditText et_name, et_mobile;
    Button btn_save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        presenter = new SosPresenter(this);

        setToolbar();

        initUI();

        setupRecyclerView();

        bottomSheetListener();

        getCurrentLocation();


        if (SP.getContactArrayListForSimTracker(getApplicationContext())!=null && SP.getContactArrayListForSimTracker(getApplicationContext()).size()>0){
            String temp = "Sim card has been removed, be alert!!! \n";
            /*if (getPhone().size()>0){
                if (getPhone().get(0)!=null && !TextUtils.isEmpty(getPhone().get(0).trim())){
                    temp+="IMEI " + getPhone().get(0)+"\n";
                }
                *//*if (getPhone().get(1)!=null && !TextUtils.isEmpty(getPhone().get(1).trim())){
                    temp+="Mobile " + getPhone().get(1)+"\n";
                }*//*
                if (getPhone().get(2)!=null && !TextUtils.isEmpty(getPhone().get(2).trim())){
                    temp+="Serial " + getPhone().get(2)+"\n";
                }
                if (getPhone().get(3)!=null && !TextUtils.isEmpty(getPhone().get(3).trim())){
                    temp+="Operator " + getPhone().get(3)+"\n";
                }
                if (getPhone().get(4)!=null && !TextUtils.isEmpty(getPhone().get(4).trim())){
                    temp+="COUNTRY " + getPhone().get(4)+"\n";
                }
            }*/
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
            ArrayList<String> _lst = getPhone();
            if (_lst.size()>0 ){
                for (int i=0;i<_lst.size();i++){
                    temp+=_lst.get(i)+"\n";
                }
            }
            for (int i=0;i<SP.getContactArrayListForSimTracker(getApplicationContext()).size();i++){
                Contact contact = SP.getContactArrayListForSimTracker(getApplicationContext()).get(i);
                if (contact.getNum()!=null && !TextUtils.isEmpty(contact.getNum().trim())) {
                    SendMessageUtils.SendMessage(contact.getNum(), temp);
                }
            }
        }



        /*if (SP.getContactArrayListForSimTracker(getApplicationContext())!=null && SP.getContactArrayListForSimTracker(getApplicationContext()).size()>0){
            String temp = "Sim card has been removed, be alert!!! ";
            ArrayList<String> _lst = getPhone();
            if (_lst.size()>0 ){
                for (int i=0;i<_lst.size();i++){
                    temp+=_lst.get(i)+"\n";
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
                    String finalTemp = temp;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SendMessageUtils.SendMessage(contact.getNum(), finalTemp);
                        }
                    }, 1500);
                }
            }
        }*/

//        getPhone();
    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.requestContacts();
    }

    private void initUI() {
        mEmergencyRecyclerView = findViewById(R.id.emergencynum_recyclerview);
        layoutBottomSheet = findViewById(R.id.emergency_bottom_sheet);
        add_contact = findViewById(R.id.emergencynum_add_contact);

        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        btn_save = findViewById(R.id.btn_save);
        et_name = findViewById(R.id.et_name);
        et_mobile = findViewById(R.id.et_mobile);
    }

    private void setToolbar() {
        findViewById(R.id.ib_phoneinfo_back).setOnClickListener(view -> onBackPressed());
    }


    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mEmergencyRecyclerView.setLayoutManager(linearLayoutManager);
        mEmergencyRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    void setAdapter() {
        mAdapter = new SOSAdapter(emergencyList, this);
        mEmergencyRecyclerView.setAdapter(mAdapter);
    }

    private void bottomSheetListener() {
        btn_save.setOnClickListener(v -> {
            String name = et_name.getText().toString().trim();
            String mobile = et_mobile.getText().toString().trim();

            if (validateEveryField(name, mobile)) {
                presenter.requestAddContacts(name, mobile);
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

    public void sendSOSMessage(View view) {
        Animator.buttonAnim(this, view);

        if (emergencyList.size() == 0 || active_contact_list.size() == 0) {
            Alert.showError(this, "No active contact found. Please add & activate a number");
            return;
        }

        //call api to send sms
        new MessagePresenter(this, "sos").requestSendSosMessage("sos");
    }

//    public static boolean isSimCardAvailable(Context context) {
//        try {
//            TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//            if (telMgr.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
//                Log.d("1111", "No sim card available");
//                return false;
//            }
//        } catch (Exception e) {
//            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//        return true;
//    }

    @Override
    public void onStatusClicked(String sos_id, String status) {
        presenter.requestChangeStatus(sos_id, status);
    }

    @Override
    public void onDeleteClicked(String sos_id) {
        presenter.requestDeleteContact(sos_id);
    }


    private void getCurrentLocation() {
        //create a callback
        LocationProvider.LocationCallback callback = new LocationProvider.LocationCallback() {

            @Override
            public void onNewLocationAvailable(float lat, float lon) {
                //location update
                SP.setStringPreference(SOSActivity.this, SP.last_latitude, String.valueOf(lat));
                SP.setStringPreference(SOSActivity.this, SP.last_longitude, String.valueOf(lon));

                getAddressByLatLong(lat, lon);

                Log.d("1111", "latitude: " + lat + "   longitude: " + lon);
            }

            @Override
            public void locationServicesNotEnabled() {
                //failed finding a location
                gpsPopUp();
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
            Alert.showError(this, e.getMessage());
        }

    }

    private void getAddressByLatLong(float lat, float lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            //Log.d("1111", "addresses: " + addresses);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                SP.setStringPreference(SOSActivity.this, SP.last_address, address.getAddressLine(0) + ", " + address.getLocality());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gpsPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS")
                .setMessage("We are not able to find your current location. Please enable gps and continue")
                .setIcon(R.mipmap.trackerthree)
                .setCancelable(false);

        builder.setPositiveButton("Continue", (dialog, which) -> {
            getCurrentLocation();
        });
        builder.setNegativeButton("Exit", (dialog, which) -> {
            finish();
        });

        builder.create();
        builder.show();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private ArrayList<String> getPhone() {
        ArrayList<String> _lst =new ArrayList<>();
        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//            _lst.add(String.valueOf(phoneMgr.getCallState()));
            _lst.add("IMEI No.-"+phoneMgr.getImei());
//            _lst.add("Number :-"+phoneMgr.getLine1Number());
            _lst.add("Serial No.-"+phoneMgr.getSimSerialNumber());
//            _lst.add("Operator :-"+phoneMgr.getSimOperatorName());
//            _lst.add("Subscriber id :-"+phoneMgr.getSubscriptionId());
//            _lst.add("MEI NUMBER :-"+phoneMgr.getMeid());
//            _lst.add("SIM STATE :-"+String.valueOf(phoneMgr.getSimState()));
//            _lst.add("ISO :-"+phoneMgr.getSimCountryIso());
        }
        Log.d("Sim Tracker", "getPhone: "+_lst);

        return _lst;
    }
}
