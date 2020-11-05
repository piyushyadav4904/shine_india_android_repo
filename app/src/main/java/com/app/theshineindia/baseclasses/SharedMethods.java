package com.app.theshineindia.baseclasses;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.theshineindia.R;
import com.app.theshineindia.intruder_selfie.AdminReceiver;
import com.app.theshineindia.secret_code.SecretCodeActivity;
import com.app.theshineindia.services.SchedulerEventReceiver;
import com.app.theshineindia.services.SingleService;
import com.app.theshineindia.utils.Alert;
import com.app.theshineindia.utils.AppData;
import com.app.theshineindia.utils.SP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

//use this class to write methods, which are used in many other classes
public class SharedMethods {

    public static boolean isSuccess(String response, Context mContext) {
        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getInt(WebServices.result) == 1) {
                    return true;
                } else {
                    Toast.makeText(mContext, jsonObject.getString(WebServices.message), Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "No response from server. Please try again", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public static JSONArray getDataArray(String response, AppCompatActivity activity) {
        JSONArray jsonArray = null;
        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.getInt(WebServices.result) == 1) {
                    jsonArray = jsonObject.getJSONArray(WebServices.data);

                    if (jsonArray.length() > 0) {
                        return jsonArray;
                    } else {
                        //showEmptyView(activity);
                        Alert.showError(activity, "Data not available");
                    }

                } else {
                    //showEmptyView(activity);
                    Alert.showError(activity, jsonObject.getString("message"));
                }
            } catch (JSONException e) {
                Alert.showError(activity, "Error 1 : " + e.getMessage());
            }
        } else {
            Alert.showError(activity, "No response from server. Please try again");
        }
        return jsonArray;
    }

    public static JSONArray getResponseArray(AppCompatActivity activity, String response, String key) {
        JSONArray jsonArray = null;
        if (response != null && key != null) {
            try {
                Log.d(activity.getClass().getSimpleName(), "response: " + response);
                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.getInt(WebServices.result) == 1) {
                    jsonArray = jsonObject.getJSONArray(key);

                    if (jsonArray.length() > 0) {
                        return jsonArray;
                    } else {
                        //showEmptyView(activity);
                        Alert.showError(activity, "Data not available");
                    }

                } else {
                    //showEmptyView(activity);
                    Alert.showError(activity, jsonObject.getString("message"));
                }
            } catch (JSONException e) {
                Alert.showError(activity, "Error 1 : " + e.getMessage());
            }
        } else {
            Alert.showError(activity, "No response from server. Please try again");
        }
        return jsonArray;
    }

    public static JSONArray getResponseArray(String response, AppCompatActivity activity, String key, RecyclerView recyclerView) {
        JSONArray jsonArray = null;
        if (recyclerView != null) {
            recyclerView.setBackgroundResource(android.R.color.transparent);
        }

        if (response != null) {
            try {
                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.getInt(WebServices.result) == 1) {
                    jsonArray = jsonObject.getJSONArray(key);

                    if (jsonArray.length() > 0) {
                        return jsonArray;
                    } else {
                        recyclerView.setBackgroundResource(R.drawable.no_data);
                    }

                } else {
                    recyclerView.setBackgroundResource(R.drawable.no_data);
                    Alert.showError(activity, jsonObject.getString("message"));
                }
            } catch (JSONException e) {
                Alert.showError(activity, "Error 1 : " + e.getMessage());
            }
        } else {
            Alert.showError(activity, "No response from server. Please try again");
            recyclerView.removeAllViews();
            recyclerView.setBackgroundResource(R.drawable.no_data);
        }
        return jsonArray;
    }

    public static void showEmptyView(final AppCompatActivity activity) {
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.empty_view);
        dialog.show();

        dialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity.finish();
                dialog.dismiss();
            }
        });
    }

    public static String convertToString(Bitmap bm) {
        String encImage = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 10, baos);
            byte[] b = baos.toByteArray();
            encImage = Base64.encodeToString(b, Base64.DEFAULT);

        } catch (Exception e) {
            Log.d("1111", "encodeImage: " + e.getMessage());
        }
        return encImage;
    }


    private static AlarmManager alarmManager;


    public static void startAlarmManager(Context context) {
        try {
            if (isAlarmManagerRunning(context)) return;
            //if (!isSingleDetectionEnabled(context)) return; //detection not started yet

            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent_receiver = new Intent(context, SchedulerEventReceiver.class); // explicit intent
            intent_receiver.setAction(SchedulerEventReceiver.ACTION_ALARM_RECEIVER);//my custom string action name
            PendingIntent intentExecuted = PendingIntent.getBroadcast(context, 1001, intent_receiver, PendingIntent.FLAG_CANCEL_CURRENT);

            if (alarmManager != null && intentExecuted != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60 * 1000, intentExecuted);
                Log.d("1111", "AlarmManager started ----->");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopAlarmManager(Context context) {
        try {
            if (!isAlarmManagerRunning(context)) return;
            //if (isSingleDetectionEnabled(context)) return;

            Intent intent_receiver = new Intent(context, SchedulerEventReceiver.class); // explicit intent
            intent_receiver.setAction(SchedulerEventReceiver.ACTION_ALARM_RECEIVER);//the same as up
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1001, intent_receiver, PendingIntent.FLAG_CANCEL_CURRENT);

            if (alarmManager != null && pendingIntent != null) {
                alarmManager.cancel(pendingIntent);//important
                pendingIntent.cancel();//important
                //AppData.is_alarm_manager_activated = false;
                Log.d("1111", "AlarmManager : stopped");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isAlarmManagerRunning(Context context) {
        //checking if alarm is working with pendingIntent
        boolean isRunning = false;
        try {
            Intent intent_receiver = new Intent(context, SchedulerEventReceiver.class); // explicit intent
            intent_receiver.setAction(SchedulerEventReceiver.ACTION_ALARM_RECEIVER);//the same as up
            isRunning = (PendingIntent.getBroadcast(context, 1001, intent_receiver, PendingIntent.FLAG_NO_CREATE) != null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("1111", "AlarmManager running : " + isRunning);
        return isRunning;
    }


    public static void startAlarmManagerAndService(Context context) {
        startAlarmManager(context);
        startMyService(context, SingleService.class);
    }

    public static void stopAlarmManagerAndService(Context context) {
        stopAlarmManager(context);

        if (!isSingleDetectionEnabled(context)) {
            SharedMethods.stopServices(context, SingleService.class);
        }
    }

    public static boolean isSingleDetectionEnabled(Context context) {
        boolean isSingleDetectionEnabled = false;
        try {
            if (SP.getBooleanPreference(context, SP.Sensor_Type.is_shake_detection_on)
                    || SP.getBooleanPreference(context, SP.Sensor_Type.is_proximity_detection_on)
                    || SP.getBooleanPreference(context, SP.Sensor_Type.is_charger_detection_on)
                    || SP.getBooleanPreference(context, SP.Sensor_Type.is_headset_detection_on)
                    || SP.getBooleanPreference(context, SP.is_sim_tracker_on)
                    || SP.getBooleanPreference(context, SP.is_intruder_selfie_on)) {

                isSingleDetectionEnabled = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("1111", "Is Single Detection Enabled : " + isSingleDetectionEnabled);
        return isSingleDetectionEnabled;
    }

    public static void startMyService(Context context, Class<?> serviceClass) {
        try {
            if (SharedMethods.isMyServiceRunning(serviceClass, context)) {
                return;
            }

            Intent myService = new Intent(context, serviceClass);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(myService);
            } else {
                context.startService(myService);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopServices(Context context, Class<?> serviceClass) {
        try {
            if (SharedMethods.isMyServiceRunning(serviceClass, context))
                context.stopService(new Intent(context, serviceClass));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        boolean isServiceRunning = false;
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    isServiceRunning = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("1111", serviceClass.getSimpleName() + " Running : " + isServiceRunning);
        return isServiceRunning;
    }


    public static void initIntruderSelfie(Context context) {
        try {
            ComponentName cn = new ComponentName(context, AdminReceiver.class);
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, context.getString(R.string.device_admin_explanation));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            Log.d("1111", "Intruder Selfie : started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static MediaPlayer mediaPlayer;

    public static MediaPlayer getMediaPlayer(Context context) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
                mediaPlayer.setLooping(true);
            }
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return mediaPlayer;
    }

    public static void playAlarm(Context context, String request_for) {
        try {
            getMediaPlayer(context).start();

            Intent my_intent = new Intent(context, SecretCodeActivity.class);
            my_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //my_intent.putExtra("request_for", request_for);
            my_intent.putExtra("request_for", AppData.authenticate);
            context.startActivity(my_intent);

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static String getCurrentDateTime() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }


    public static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    @SuppressLint("MissingPermission")
    public static int getAvailableSimCount(Context context) {
        int sim_count = 0;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                SubscriptionManager sManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
                SubscriptionInfo infoSim1 = sManager.getActiveSubscriptionInfoForSimSlotIndex(0);
                if (infoSim1 != null) {
                    sim_count += 1;
                }
                SubscriptionInfo infoSim2 = sManager.getActiveSubscriptionInfoForSimSlotIndex(1);
                if (infoSim2 != null) {
                    sim_count += 1;
                }
            } else {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager.getSimSerialNumber() != null) {
                    sim_count += 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sim_count;
    }


    public static void writeToFile(String file_name, String data) {
        if (data != null) {
            String path = Environment.getExternalStorageDirectory() + File.separator + "AlevantReqRes";
            // Create the folder.
            File folder = new File(path);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Create the file.
            File file = new File(folder, file_name + ".txt");

            // Save your stream, don't forget to flush() it before closing it.

            try {
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileOutputStream fOut = new FileOutputStream(file, true);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(data);

                myOutWriter.close();

                fOut.flush();
                fOut.close();
                //Toast.makeText(this, "File created " + file.getPath(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
                //Toast.makeText(this, "File error " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static void isLocationEnable(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setMessage("GPS network not enabled")
                    .setPositiveButton("Enable", (paramDialogInterface, paramInt) -> context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    //.setNegativeButton("Cancel", null)
                    .show();
        }
    }
}
