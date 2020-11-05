package com.app.theshineindia.sos;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.theshineindia.R;
import com.app.theshineindia.app_presenter.MessagePresenter;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.Animator;
import com.app.theshineindia.utils.SP;
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
}
