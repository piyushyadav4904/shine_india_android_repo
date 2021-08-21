package com.app.theshineindia.intruder_selfie;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.utils.AppData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.app.theshineindia.intruder_selfie.CameraService.decodeBitmap;

public class CameraService4 extends Service implements SurfaceHolder.Callback, Camera.PictureCallback {
    NotificationChannel channel = null;
    String CHANNEL_ID_STR = "camera_service_channel";
    int CHANNEL_ID = 1000;
    private int cameraId;
    private Intent cameraIntent;
    private WindowManager windowManager;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Bitmap bmp;
    private int QUALITY_MODE = 0;
    private boolean isFrontFacing = true;
    Camera.PictureCallback mCall = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // decode the data obtained by the camera into a Bitmap
            Log.e("ImageTakin", "Done");
            if(camera != null){
                camera.stopPreview();
                camera.release();
            }
            if (bmp != null)
                bmp.recycle();
            System.gc();
            bmp = decodeBitmap(data);

            //    bmp = SharedMethods.RotateBitmap(bmp, -90);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            if (bmp != null && QUALITY_MODE == 0)
                bmp.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
            else if (bmp != null && QUALITY_MODE != 0)
                bmp.compress(Bitmap.CompressFormat.JPEG, QUALITY_MODE, bytes);

            // String root = Environment.getExternalStorageDirectory().toString();
            //  String root = getExternalFilesDir(null).toString();
            //  String root = Environment.getExternalStoragePublicDirectory(null).toString();
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + AppData.folder_name;

            File myDir = new File(root  );

            if (!myDir.exists()) {

                myDir.mkdirs();
            }
            String filename = System.currentTimeMillis() + ".jpg";
            File file = new File(myDir, filename);
            System.out.println(file);
            try {
                Log.e("123", "inside try");
                // file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                Log.e("123", "Saved captured image");
            } catch (Exception e) {
                Log.e("123", "inside exception" + e.getMessage());
                e.printStackTrace();
            }
            if (bmp != null) {
                // SEND INTRUDER LOCATION AND IMAGE TO ADMIN
                String image_str = SharedMethods.convertToString(bmp);
                if (image_str != null)
                    new IntruderSelfiePresenter(getApplicationContext()).requestUploadSelfie(image_str);
                // new IntruderSelfiePresenter(getApplicationContext()).requestUploadSelfie2(image_str, file);
                //new IntruderSelfiePresenter(getApplicationContext()).prepareWorkManagerForSelfie();

                bmp.recycle();
                bmp = null;
                System.gc();
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            channel = new NotificationChannel(CHANNEL_ID_STR, "The Shine India", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_STR)
                    .setContentTitle("")
                    .setContentText("").build();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                startForeground(CHANNEL_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA);
            } else startForeground(CHANNEL_ID, notification);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isFrontFacing = intent.getBooleanExtra("cameraID", true);
        cameraIntent = intent;
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.START;
        params.width = 1;
        params.height = 1;
        params.x = 0;
        params.y = 0;
        surfaceView = new SurfaceView(getApplicationContext());

        windowManager.addView(surfaceView, params);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        isFrontFacing = intent.getBooleanExtra("cameraID", true);
        return null;
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (cameraIntent != null)
            new TakeImage().execute(cameraIntent);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        resetCamera();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }


    synchronized private void takeImage() {
        if (isFrontFacing)
            camera = getFrontCamera();
        else camera = getRearCamera();

        if (camera == null) {
            return;
        }

        camera.getParameters().setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.getParameters().setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        if (info.canDisableShutterSound) {
            camera.enableShutterSound(false);
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
        captureImage();
    }

    private Camera getFrontCamera() {
        if (!CameraHelpers.checkFrontCamera(getApplicationContext()))
            return null;

        int cameraCount = 0;
        Camera mCamera = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cameraId = camIdx;
                    mCamera = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    //can't open camera
                }
            }
        }
        return mCamera;
    }

    private Camera getRearCamera() {
        if (!CameraHelpers.checkRearCamera(getApplicationContext()))
            return null;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
        } catch (RuntimeException e) {
            // Can't open camera
        }
        return mCamera;
    }


    public void resetCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        /*if (camera != null) {
//            camera.stopPreview();
            try {
//                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
        }*/
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        releaseCamera();

        File pictureFileDir = CameraHelpers.getDir();
        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss", Locale.getDefault());
        String date = dateFormat.format(new Date());
        String photoFile = "Picture_" + date + ".jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (Exception error) {
            //Error writing file
        }


        stopService();
    }

    private void stopService() {
        releaseCamera();
        if (windowManager != null && surfaceView != null)
            windowManager.removeView(surfaceView);

        stopSelf();
    }

    public void captureImage() {
        if (camera != null) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    camera.takePicture(null, null, mCall);
                }
            });
        }
    }

    private class TakeImage extends AsyncTask<Intent, Void, Void> {

        @Override
        protected Void doInBackground(Intent... params) {
            takeImage();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }


}


