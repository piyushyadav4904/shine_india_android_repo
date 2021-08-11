package com.app.theshineindia.intruder_selfie;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraService;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.SharedMethods;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class DemoCam extends HiddenCameraService {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("123", "onstart");


        String CHANNEL_ID = "my_channel_02";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                "The Shine Indiaaa",
                NotificationManager.IMPORTANCE_DEFAULT);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText("input")
                .setSmallIcon(R.drawable.delete_icon)
                .build();

        startForeground(1000, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            if (HiddenCameraUtils.canOverDrawOtherApps(this)) {
                CameraConfig cameraConfig = new CameraConfig()
                        .getBuilder(this)
                        .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                        .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                        .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                        .build();

                startCamera(cameraConfig);

                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DemoCam.this,
                                "Capturing image.", Toast.LENGTH_SHORT).show();

                        takePicture();
                    }
                }, 2000L);
            } else {

                //Open settings to grant permission for "Draw other apps".
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
            }
        } else {

            //TODO Ask your parent activity for providing runtime permission
            Toast.makeText(this, "Camera permission not available", Toast.LENGTH_SHORT).show();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        Toast.makeText(this,
                "Captured image size is : " + imageFile.length(),
                Toast.LENGTH_SHORT)
                .show();
        Log.e("123", imageFile.getAbsolutePath() + "\n"
                + imageFile.getPath() + "\n");
        FileOutputStream fo;
        /*try {
            fo = new FileOutputStream(myDirectory + "/user" + curTime + ".jpg");
            fo.write(imageFile.toByteArray());
            fo.close();
            Log.e("123", "file wrote eueueuue " + myDirectory.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        bitmap = SharedMethods.RotateBitmap(bitmap, -90);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bytes);

        byte[] data = bytes.toByteArray();

        /*File myDirectory = new File(Environment.getStorageDirectory() + "/Test");
        myDirectory.mkdirs();
        //File md = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/sample-take-image");
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(myDirectory + "/user" + "asdf" + ".jpg");
            outStream.write(data);
            outStream.close();
            stopSelf();
        } catch (FileNotFoundException e) {
            Log.e("CcCCcCAMERA", e.getMessage());
        } catch (IOException e) {
            Log.e("CCcccCAMERA", e.getMessage());
        }*/

/*
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), AppData.folder_name);
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs(); // <----
        }


        File image = new File(imagesFolder, System.currentTimeMillis() + ".jpg");

        //File image = new File(imagesFolder, "intruder.jpg");

        // write the bytes in file

        Log.e("123", Arrays.toString(data));

        try {
            fo = new FileOutputStream(imagesFolder + "/user" + System.currentTimeMillis() + ".jpg");
            fo.write(data);
            fo.close();
        } catch (FileNotFoundException e) {
            Log.e("TAG", "FileNotFoundException", e);
            // TODO Auto-generated catch block
        } catch (IOException e) {
            Log.e("123", e.getMessage());
            e.printStackTrace();
        }*/


        String root = Environment.getExternalStorageDirectory().toString();

        File myDir = new File(root + "/DocVision/Pictures");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String fname = System.currentTimeMillis() + ".jpg";
        File file = new File(myDir, fname);

        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.e("123", "wrote file");
        } catch (Exception e) {
            e.printStackTrace();

        }


        stopSelf();
    }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Toast.makeText(this, R.string.error_cannot_open, Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
                Toast.makeText(this, R.string.error_cannot_write, Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camera permission before initializing it.
                Toast.makeText(this, R.string.error_cannot_get_permission, Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(this, R.string.error_not_having_camera, Toast.LENGTH_LONG).show();
                break;
        }

        stopSelf();
    }
}