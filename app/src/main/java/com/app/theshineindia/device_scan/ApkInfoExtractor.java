package com.app.theshineindia.device_scan;


import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.app.theshineindia.R;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.SP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApkInfoExtractor {

    private Context context1;
    private JSONArray harmful_app_ja;
    private ArrayList<String> hidden_app_list;

    public ApkInfoExtractor(Context context2) {
        context1 = context2;
        hidden_app_list = SP.getArrayList(context2, SP.hidden_app_list);
    }

    public List<App> GetAllInstalledApkInfo() {
        List<App> ApkPackageName = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        List<ResolveInfo> resolveInfoList = context1.getPackageManager().queryIntentActivities(intent, 0);

        getHarmfullApplist();

        for (ResolveInfo resolveInfo : resolveInfoList) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;

            App app = new App();
            String package_name = activityInfo.applicationInfo.packageName;
            String app_name = GetAppName(package_name);
            app.setName(app_name);
            app.setPackage_name(package_name);
            app.setIcon(getAppIconByPackageName(package_name));

            if (isSystemPackage(resolveInfo)) {
                app.setStatus("System app");
            } else {
                app.setStatus(checkAppStatus(app_name, package_name));
                //Log.d("1111", "app_name: " + app_name);
                //Log.d("1111", "checkAppStatus: " + checkAppStatus(app_name, package_name));
            }

            app.setHidden(checkHiddenStatus(package_name));

            ApkPackageName.add(app);
        }

        return ApkPackageName;

    }

    private void getHarmfullApplist() {
        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAsset(context1));
            harmful_app_ja = jsonObject.getJSONArray("harmful_app");
            //Log.d("1111", "harmful_app_ja: " + harmful_app_ja);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("harmful_app.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    private boolean checkHiddenStatus(String package_name) {
        if (hidden_app_list == null) return false;

        try {
            for (String str : hidden_app_list) {
                if (package_name.equalsIgnoreCase(str)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isSystemPackage(ResolveInfo resolveInfo) {
        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private Drawable getAppIconByPackageName(String ApkTempPackageName) {
        Drawable drawable;

        try {
            drawable = context1.getPackageManager().getApplicationIcon(ApkTempPackageName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            drawable = ContextCompat.getDrawable(context1, R.drawable.launcher);
        }
        return drawable;
    }
//            Intent i = new Intent(context, CameraView.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);

    //            Intent front_translucent = new Intent(context, CameraService.class);
//            front_translucent.putExtra("Front_Request", true);
//            front_translucent.putExtra("Quality_Mode", 50);
//            context.startService(front_translucent);


    private String GetAppName(String ApkPackageName) {
        String Name = "";
        ApplicationInfo applicationInfo;
        PackageManager packageManager = context1.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0);
            if (applicationInfo != null) {
                Name = (String) packageManager.getApplicationLabel(applicationInfo);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Name;
    }


    private String checkAppStatus(String app_name, String package_name) {
        String status = "Secure";

        if (harmful_app_ja == null) {
            return status;
        }

        try {
            for (int i = 0; i < harmful_app_ja.length(); i++) {
                JSONObject jsonObject = harmful_app_ja.getJSONObject(i);

                if (!app_name.isEmpty() && jsonObject.getString("name").contains(app_name)) {
                    status = "High risk";
                }

                if (!package_name.isEmpty() && package_name.equalsIgnoreCase(jsonObject.getString("package_name"))) {
                    status = "High risk";
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }
}
