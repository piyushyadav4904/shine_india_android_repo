
package com.app.theshineindia.app_locker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.utils.Alert;

import java.util.ArrayList;
import java.util.List;

public class AppLockerActivity extends AppCompatActivity {

    ArrayList<String> blocked_app_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_locker);

        setToolbar();

    }


    //get a list of installed apps.
    private void getInstalledAppList() {
        try {
            final PackageManager pm = getPackageManager();
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            blocked_app_list.clear();

            for (ApplicationInfo packageInfo : packages) {
                Log.d("1111", "Installed package :" + packageInfo.packageName);
                Log.d("1111", "Source dir : " + packageInfo.sourceDir);
                Log.d("1111", "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));

                blocked_app_list.add(packageInfo.packageName);
            }
        } catch (Exception e) {
            Alert.showError(this, e.getMessage());
        }
    }

    private void setToolbar() {
        findViewById(R.id.ib_phoneinfo_back).setOnClickListener(view -> onBackPressed());
    }
}
