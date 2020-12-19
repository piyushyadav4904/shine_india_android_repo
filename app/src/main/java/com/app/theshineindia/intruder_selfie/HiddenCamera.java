package com.app.theshineindia.intruder_selfie;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.utils.AppData;
import com.cottacush.android.hiddencam.HiddenCam;
import com.cottacush.android.hiddencam.OnImageCapturedListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HiddenCamera {

    private static final String TAG = "HiddenCamera";
    private Context context;
    private HiddenCam hiddenCam;

    public HiddenCamera(Context context) {
        this.context = context;
    }

    public void initializeCamera() {
        hiddenCam = new HiddenCam(context, getOutputMediaFile(), onImageCapturedListener);

        hiddenCam.start();
    }

    public void captureCamera() {
        hiddenCam.captureImage();
    }

    private final OnImageCapturedListener onImageCapturedListener = new OnImageCapturedListener() {
        @Override
        public void onImageCaptured(@NotNull File file) {
            Log.d(TAG, "onImageCaptured: "+ Uri.fromFile(file));
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            bitmap = SharedMethods.RotateBitmap(bitmap, -90);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bytes);

            String strImage = SharedMethods.convertToString(bitmap);
            if (strImage != null)
                new IntruderSelfiePresenter(context).requestUploadSelfie2(strImage, file);

            hiddenCam.stop();
            hiddenCam.destroy();
        }

        @Override
        public void onImageCaptureError(@Nullable Throwable throwable) {
            Log.e("onImageCaptureError", throwable.toString());
        }
    };

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), AppData.folder_name);

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        /*// Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");*/

        return mediaStorageDir;
    }
}
