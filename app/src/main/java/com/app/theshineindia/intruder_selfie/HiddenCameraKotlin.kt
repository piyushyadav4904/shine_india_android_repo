package com.app.theshineindia.intruder_selfie
//
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.os.Environment
//import android.util.Log
//import com.app.theshineindia.baseclasses.SharedMethods
//import com.app.theshineindia.utils.AppData
//import com.cottacush.android.hiddencam.CameraType
//import com.cottacush.android.hiddencam.HiddenCam
//import com.cottacush.android.hiddencam.OnImageCapturedListener
//import java.io.ByteArrayOutputStream
//import java.io.File
//
//class HiddenCameraKotlin(private var context: Context) {
//    private var hiddenCam: HiddenCam? = null
//    fun initializeCamera() {
//        hiddenCam = HiddenCam(context = context, baseFileDirectory = outputMediaFile!!, imageCapturedListener = onImageCapturedListener, cameraType = CameraType.FRONT_CAMERA)
//        hiddenCam!!.start()
//    }
//
//    fun captureCamera() {
//        hiddenCam!!.captureImage()
//    }
//
//    private val onImageCapturedListener: OnImageCapturedListener = object : OnImageCapturedListener {
//        override fun onImageCaptured(file: File) {
//            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
//            var bitmap = BitmapFactory.decodeFile(file.absolutePath)
//            bitmap = SharedMethods.RotateBitmap(bitmap, -90f)
//            val bytes = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bytes)
//            val strImage = SharedMethods.convertToString(bitmap)
//            if (strImage != null) IntruderSelfiePresenter(context).requestUploadSelfie2(strImage, file)
//            hiddenCam!!.stop()
//            hiddenCam!!.destroy()
//        }
//
//        override fun onImageCaptureError(throwable: Throwable?) {
//            Log.e("onImageCaptureError", throwable.toString())
//        }
//    }
//
//    // To be safe, you should check that the SDCard is mounted
//    // using Environment.getExternalStorageState() before doing this.
//    private val outputMediaFile: File
//
//    // This location works best if you want the created images to be shared
//    // between applications and persist after your app has been uninstalled.
//
//    // Create the storage directory if it does not exist
//
//    /*// Create a media file name
//       String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//
//       File mediaFile;
//       mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");*/
//    ?
//        private get() {
//            // To be safe, you should check that the SDCard is mounted
//            // using Environment.getExternalStorageState() before doing this.
//            val mediaStorageDir = File(Environment.getExternalStorageDirectory(), AppData.folder_name)
//
//            // This location works best if you want the created images to be shared
//            // between applications and persist after your app has been uninstalled.
//
//            // Create the storage directory if it does not exist
//            if (!mediaStorageDir.exists()) {
//                if (!mediaStorageDir.mkdirs()) {
//                    return null
//                }
//            }
//
//            /*// Create a media file name
//               String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//
//               File mediaFile;
//               mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");*/return mediaStorageDir
//        }
//}