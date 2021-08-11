package com.app.theshineindia.intruder_selfie;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.hardware.Camera;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.app.theshineindia.R;
import com.app.theshineindia.app_locker.activities.main.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class CamService extends Service {

    Camera.PictureCallback mCall = new Camera.PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            //decode the data obtained by the camera into a Bitmap
            Log.e("123", Arrays.toString(data));
            File myDirectory = new File(Environment.getExternalStorageDirectory() + "/Test");
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
            }

        }
    };
    private SurfaceHolder sHolder;
    //a variable to control the camera
    private Camera mCamera;
    //the camera parameters
    private Camera.Parameters parameters;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, "10000")
                .setContentTitle("Foreground Service")
                .setContentText("input")
                .setSmallIcon(R.drawable.delete_icon)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1000, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA);

        mCamera = Camera.open();
        SurfaceView sv = new SurfaceView(getApplicationContext());

        try {
            mCamera.setPreviewDisplay(sv.getHolder());
            parameters = mCamera.getParameters();

            //set camera parameters
            mCamera.setParameters(parameters);
            mCamera.startPreview();
            mCamera.takePicture(null, null, mCall);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        //Get a surface
        sHolder = sv.getHolder();
        //tells Android that this surface will have its data constantly replaced
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
