package com.app.theshineindia.intruder_selfie;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Size;

import com.app.theshineindia.utils.AppData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class CameraHelpers {

    private static Size getBiggestPictureSize(Camera.Parameters parameters) {
        Size result = null;
        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result = new Size(size.width, size.height);
            } else {
                int resultArea = result.getWidth() * result.getHeight();
                int newArea = size.width * size.height;

                if (newArea > resultArea) {
                    result = new Size(size.width, size.height);
                }
            }
        }
        return (result);
    }

    public static Bitmap decodeBitmap(byte[] data) {

        Bitmap bitmap = null;
        if (data != null)
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bitmap;
    }

    static public boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY);
    }

    @SuppressLint("UnsupportedChromeOsCameraSystemFeature")
    static public boolean checkRearCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    static public boolean checkFrontCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT);
    }

    public static Uri saveBitmapToFile(String fileName, Bitmap bitmap, ContentResolver resolver) throws IOException {
        OutputStream fos;
        Uri imageUri;

        File MEDIA_DIRECTORY = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));

            try {
                saveBitmapToStream(fos, bitmap);
                Objects.requireNonNull(fos, "Can't create fileoutputstream!");
            } finally {
                fos.close();
            }
        } else {
            if (!(MEDIA_DIRECTORY.exists() || MEDIA_DIRECTORY.mkdirs())) {
                throw new IOException("Can not create media directory.");
            }

            File file = new File(MEDIA_DIRECTORY, fileName);
            OutputStream outputStream = new FileOutputStream(file);

            try {
                saveBitmapToStream(outputStream, bitmap);
            } finally {
                outputStream.close();
            }

            imageUri = Uri.fromFile(file);
        }

        return imageUri;
    }

    private static void saveBitmapToStream(OutputStream outputStream, Bitmap bitmap) throws IOException {
        if (bitmap == null || bitmap.isRecycled()) {
            throw new IllegalArgumentException("Bitmap is invalid");
        }
        if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
            throw new IOException("Can not write png to stream.");
        }
    }

    public static File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, AppData.folder_name);
    }


}
