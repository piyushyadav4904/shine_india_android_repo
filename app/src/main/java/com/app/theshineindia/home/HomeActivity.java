package com.app.theshineindia.home;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.device_scan.AppListActivity;
import com.app.theshineindia.app_locker.activities.main.SplashActivity;
import com.app.theshineindia.sos.SOSActivity;
import com.app.theshineindia.utils.Animator;
import com.app.theshineindia.utils.AppData;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.theshineindia.R;
import com.app.theshineindia.intruder_selfie.IntruderSelfieActivity;
import com.app.theshineindia.login.LoginActivity;
import com.app.theshineindia.phone_info.PhoneInfoActivity;
import com.app.theshineindia.profile.ProfileActivity;
import com.app.theshineindia.sim_tracker.SimTrackerActivity;
import com.app.theshineindia.theft_detection.TheftDetectionActivity;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.IntentController;
import com.app.theshineindia.utils.SP;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import locationprovider.davidserrano.com.LocationProvider;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private DrawerLayout drawer;
    private ImageView hamburger_menu;
    private NavigationView mNavigationView;

    ConstraintLayout layoutBottomSheet;
    TextView btn_devicescan;
    BottomSheetBehavior sheetBehavior;
    TextView tv_malware_scan, tv_folder_scan, tv_full_scan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initNavdrawer();

        setToolBar();

        initUI();

        initBottomSheet();

        phonepermission();

        //SharedMethods.startAlarmManagerAndService(this);
    }

    private void phonepermission() {

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
    private boolean hasPermissions(HomeActivity homeMainActivity, String[] permissions) {

        if (homeMainActivity != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(homeMainActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();

        SharedMethods.isLocationEnable(this);
    }

    private void initUI() {
        layoutBottomSheet = findViewById(R.id.devicescan_bottom_sheet);
        btn_devicescan = findViewById(R.id.home_tv_devicescan);
    }


    private void setToolBar() {
        AppCompatTextView tv_tool_title = findViewById(R.id.tv_app);
        tv_tool_title.setVisibility(View.VISIBLE);
        tv_tool_title.setText(getString(R.string.title_home));

        findViewById(R.id.ib_back).setVisibility(View.GONE);
    }


    private void initNavdrawer() {
        drawer = findViewById(R.id.drawer_layout);
        hamburger_menu = findViewById(R.id.ib_menu);
        // home_toolcard = findViewById(R.id.home_toolcard);
        mNavigationView = findViewById(R.id.nav_view);

        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        //   params.setMargins(0, 0, 0, 0);
        //  home_toolcard.setLayoutParams(params);

        hamburger_menu.setImageResource(R.mipmap.menu);
        hamburger_menu.setVisibility(View.VISIBLE);
        hamburger_menu.setOnClickListener(this);

        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(this);

        mNavigationView.inflateHeaderView(R.layout.nav_header_home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            IntentController.sendIntent(HomeActivity.this, HomeActivity.class);

        } else if (id == R.id.nav_myaccount) {
            IntentController.sendIntent(HomeActivity.this, ProfileActivity.class);

        } else if (id == R.id.nav_logout) {
            logoutPopUp();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ib_menu) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        }
    }


    private void logoutPopUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.logout))
                .setMessage(getString(R.string.logout_text))
                .setCancelable(false);
        builder.setPositiveButton(getString(R.string.logout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (SP.logoutFunction(HomeActivity.this)) {
                        //stop all services

                        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    Alert.showError(HomeActivity.this, e.getMessage());
                }

            }
        });
        builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }


    public void onIntruderSelfieClick(View view) {
        Animator.buttonAnim(this, view);
        Intent intruder = new Intent(this, IntruderSelfieActivity.class);
        startActivity(intruder);
    }

    public void onTheftDetectionClick(View view) {
        Animator.buttonAnim(this, view);
        Intent intruder = new Intent(this, TheftDetectionActivity.class);
        startActivity(intruder);
    }

    public void onSimTrackerClick(View view) {
        Animator.buttonAnim(this, view);
        Intent intruder = new Intent(this, SimTrackerActivity.class);
        startActivity(intruder);
    }

    public void onAppLockerClick(View view) {
        Animator.buttonAnim(this, view);
        Intent intruder = new Intent(this, SplashActivity.class);
        startActivity(intruder);
    }

    public void onSOSClick(View view) {
        Animator.buttonAnim(this, view);
        Intent intruder = new Intent(this, SOSActivity.class);
        startActivity(intruder);
    }

    public void onPhoneInfoClick(View view) {
        Animator.buttonAnim(this, view);
        Intent intruder = new Intent(this, PhoneInfoActivity.class);
        //Intent intruder = new Intent(this, AppHideActivity.class);
        startActivity(intruder);
    }

    public void onDeviceScanClick(View view) {
        //Animator.buttonAnim(this, findViewById(R.id.imageView11));
        Animator.buttonAnim(this, findViewById(R.id.imageView14));
        //Animator.buttonAnim(this, findViewById(R.id.home_tv_devicescan));

        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    private void initBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        tv_malware_scan = findViewById(R.id.tv_malware_scan);
        tv_folder_scan = findViewById(R.id.tv_folder_scan);
        tv_full_scan = findViewById(R.id.tv_full_scan);

        tv_malware_scan.setOnClickListener(v -> {
            IntentController.sendIntent(this, AppListActivity.class);
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });

        tv_folder_scan.setOnClickListener(v -> {
            IntentController.sendIntent(this, AppListActivity.class);
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });

        tv_full_scan.setOnClickListener(v -> {
            IntentController.sendIntent(this, AppListActivity.class);
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
    }
}
