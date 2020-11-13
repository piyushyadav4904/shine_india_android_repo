package com.app.theshineindia.intruder_selfie;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.hiddencamera.CameraConfig;
import com.app.theshineindia.hiddencamera.CameraError;
import com.app.theshineindia.hiddencamera.HiddenCameraService;
import com.app.theshineindia.hiddencamera.HiddenCameraUtils;
import com.app.theshineindia.hiddencamera.config.CameraFacing;
import com.app.theshineindia.hiddencamera.config.CameraImageFormat;
import com.app.theshineindia.hiddencamera.config.CameraResolution;
import com.app.theshineindia.utils.AppData;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class InvisibleCameraService extends HiddenCameraService {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            if (HiddenCameraUtils.canOverDrawOtherApps(this)) {
                File imagesFolder = new File(Environment.getExternalStorageDirectory(), AppData.folder_name);
                if (!imagesFolder.exists()) {
                    imagesFolder.mkdirs();
                }

                File imageFile = new File(imagesFolder, System.currentTimeMillis() + ".jpg");

                CameraConfig cameraConfig = new CameraConfig()
                        .getBuilder(this)
                        .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                        .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                        .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                        .setImageFile(imageFile)
                        .build();

                startCamera(cameraConfig);

                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        takePicture();
                    }
                }.sendEmptyMessageDelayed(0, 500);

                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        takePicture();
                    }
                }, 2000L);*/
            } else {

                //Open settings to grant permission for "Draw other apps".
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
            }
        } else {

            //TODO Ask your parent activity for providing runtime permission
            //Toast.makeText(this, "Camera permission not available", Toast.LENGTH_SHORT).show();
            Log.e("CAMERA", "Permission not available");
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        //Toast.makeText(this, "Captured image size is : " + imageFile.length(), Toast.LENGTH_SHORT).show();
        Log.e("CAMERA", "CAPTURED");

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));

        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        bitmap = SharedMethods.RotateBitmap(bitmap, -90);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);

        String strImage = SharedMethods.convertToString(bitmap);
        if (strImage != null)
            new IntruderSelfiePresenter(getApplicationContext()).requestUploadSelfie2(strImage, imageFile);

        stopSelf();
    }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                //Toast.makeText(this, R.string.error_cannot_open, Toast.LENGTH_LONG).show();
                Log.e("CAMERA", "ERROR_CAMERA_OPEN_FAILED");
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
                //Toast.makeText(this, R.string.error_cannot_write, Toast.LENGTH_LONG).show();
                Log.e("CAMERA", "ERROR_IMAGE_WRITE_FAILED");
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camera permission before initializing it.
                //Toast.makeText(this, R.string.error_cannot_get_permission, Toast.LENGTH_LONG).show();
                Log.e("CAMERA", "ERROR_CAMERA_PERMISSION_NOT_AVAILABLE");
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                //HiddenCameraUtils.openDrawOverPermissionSetting(this);
                Log.e("CAMERA", "ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION");
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                //Toast.makeText(this, R.string.error_not_having_camera, Toast.LENGTH_LONG).show();
                Log.e("CAMERA", "ERROR_DOES_NOT_HAVE_FRONT_CAMERA");
                break;
        }

        stopSelf();
    }
}
