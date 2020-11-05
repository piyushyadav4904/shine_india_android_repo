package com.app.theshineindia.intruder_selfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.app.theshineindia.R;
import com.app.theshineindia.baseclasses.BasePresenter2;
import com.app.theshineindia.baseclasses.SharedMethods;
import com.app.theshineindia.baseclasses.WebServices;
import com.app.theshineindia.intruder_selfie.image_uploader.MyWorker;
import com.app.theshineindia.loaders.JSONFunctions;
import com.app.theshineindia.utils.AppData;
import com.app.theshineindia.utils.SP;

import java.io.File;
import java.util.HashMap;

public class IntruderSelfiePresenter extends BasePresenter2 {
    private Context context;

    public IntruderSelfiePresenter(Context context) {
        this.context = context;
    }


//    void prepareWorkManagerForSelfie() {
//        //creating constraints
//        Constraints constraints = new Constraints.Builder()
//                .setRequiresCharging(true) // you can add as many constraints as you want
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build();
//
//        final OneTimeWorkRequest workRequest =
//                new OneTimeWorkRequest.Builder(MyWorker.class)
//                        .setConstraints(constraints)
//                        .addTag("uploading_intruder_selfie")
//                        .build();
//
//        WorkManager.getInstance(context).enqueue(workRequest);
//
//        /*Listening to the work status*/
//        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workRequest.getId())
//                .observe(context, workInfo -> {
//                    //Displaying the status
//                    Log.d("1111", "onChanged: " + workInfo.getState().name() + "\n");
//                });
//    }

    int file_count = 0;
    String current_file_name = "";

    public void checkFileExistence() {
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), AppData.folder_name);
        if (!imagesFolder.exists()) {
            Log.d("1111", "File not Exist");
            return;
        }
       /* File file_image = new File(imagesFolder, "intruder.jpg");
        Bitmap bmp = BitmapFactory.decodeFile(file_image.getAbsolutePath());
        String image_str = SharedMethods.convertToString(bmp);
        if (image_str != null)
            requestUploadSelfie(image_str);*/


        String path = Environment.getExternalStorageDirectory().toString() + AppData.folder_name;
        Log.d("1111 Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("1111 Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            Log.d("1111 Files", "FileName:" + files[i].getName());
        }

        if (files.length >= file_count) {
            current_file_name = files[file_count].getName();
            File file_image = new File(imagesFolder, current_file_name);
            Bitmap bmp = BitmapFactory.decodeFile(file_image.getAbsolutePath());
            String image_str = SharedMethods.convertToString(bmp);
            if (image_str != null)
                requestUploadSelfie(image_str);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void requestUploadSelfie(String image_str) {
        //SharedMethods.writeToFile("Shine_" + System.currentTimeMillis(), image_str);

        if (JSONFunctions.isInternetOn(context)) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("user_id", SP.getStringPreference(context, SP.user_id));
            hashMap.put("lat", SP.getStringPreference(context, SP.last_latitude));
            hashMap.put("long", SP.getStringPreference(context, SP.last_longitude));
            hashMap.put("address", SP.getStringPreference(context, SP.last_address));
            hashMap.put("image", image_str);

            getJfns().makeHttpRequest(WebServices.upload_selfi, "POST", hashMap, false, WebServices.request_url_no_1);
        } else {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    void requestUploadSelfie2(String image_str, File file) {
        //SharedMethods.writeToFile("Shine_" + System.currentTimeMillis(), image_str);

        if (JSONFunctions.isInternetOn(context)) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("user_id", SP.getStringPreference(context, SP.user_id));
            hashMap.put("lat", SP.getStringPreference(context, SP.last_latitude));
            hashMap.put("long", SP.getStringPreference(context, SP.last_longitude));
            hashMap.put("address", SP.getStringPreference(context, SP.last_address));
            hashMap.put("image", image_str);
            HashMap<String, File> hashMap2 = new HashMap<>();
            hashMap2.put("image", file);
            getJfns().makeHttpRequest(WebServices.upload_selfi, "POST", hashMap, hashMap2, WebServices.request_url_no_1);
        } else {
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getJSONResponseResult(String result, int url_no) {
        if (url_no == WebServices.request_url_no_1) {
            SharedMethods.isSuccess(result, context);

//            if (SharedMethods.isSuccess(result, context)) {
//                deleteTheFile(current_file_name);
//            } else {
//                prepareWorkManagerForSelfie();
//            }
        }
    }

    private void deleteTheFile(String file_name) {
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), AppData.folder_name);
        File file_delete = new File(imagesFolder, "intruder.jpg");
        if (file_delete.exists()) {
            if (file_delete.delete()) {
                System.out.println("file Deleted :" + file_delete.getPath());
            } else {
                System.out.println("file not Deleted :" + file_delete.getPath());
            }
        }
    }

}
