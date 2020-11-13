package com.app.theshineindia.intruder_selfie

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.app.theshineindia.baseclasses.SharedMethods
import com.app.theshineindia.utils.AppData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FrontPictureLiveData(val context: Context) :
        MutableLiveData<FrontPictureState>() {

    private enum class State {
        IDLE, TAKING, TAKEN, ERROR
    }

    private var camera: Camera? = null

    private var state: State = State.IDLE

    override fun onInactive() {
        super.onInactive()
        stopCamera()
    }

    fun takePicture() {
        if (state == State.TAKEN || state == State.TAKING) {
            return
        }

        state = State.TAKING
        startCamera()
        camera?.takePicture(
                null,
                null,
                Camera.PictureCallback { data, camera -> savePicture(data) })
    }

    private fun startCamera() {
        val dummy = SurfaceTexture(0)

        try {
            val cameraId = getFrontCameraId()
            if (cameraId == NO_CAMERA_ID) {
                value = FrontPictureState.Error(IllegalStateException("No front camera found"))
                state = State.ERROR
                return
            }
            camera = Camera.open(cameraId).also {
                it.setPreviewTexture(dummy)
                it.startPreview()
            }
            value = FrontPictureState.Started()
        } catch (e: RuntimeException) {
            value = FrontPictureState.Error(e)
            state = State.ERROR
        }
    }

    private fun stopCamera() {
        camera?.stopPreview()
        camera?.release()
        camera = null
        value = FrontPictureState.Destroyed()
    }

    private fun getFrontCameraId(): Int {
        var camId =
                NO_CAMERA_ID
        val numberOfCameras = Camera.getNumberOfCameras()
        val ci = Camera.CameraInfo()

        for (i in 0 until numberOfCameras) {
            Camera.getCameraInfo(i, ci)
            if (ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                camId = i
            }
        }

        return camId
    }

    @SuppressLint("CheckResult")
    private fun savePicture(data: ByteArray) {
        Single
                .create<String> { emitter ->
                    try {
                        val pictureFile = getOutputMediaFile()
                        pictureFile?.let {
                            val fos = FileOutputStream(pictureFile)
                            fos.write(data)
                            fos.close()
                            emitter.onSuccess(it.absolutePath)
                        }
                    } catch (e: Exception) {
                        emitter.onError(e)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { filePath ->
                            state = State.TAKEN
                            value = FrontPictureState.Taken(filePath)

                            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(filePath))))

                            var bitmap = BitmapFactory.decodeFile(File(filePath).absolutePath)
                            bitmap = SharedMethods.RotateBitmap(bitmap, -90f)

                            val bytes = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

                            val strImage = SharedMethods.convertToString(bitmap)
                            if (strImage != null) IntruderSelfiePresenter(context).requestUploadSelfie2(strImage, File(filePath))

                            stopCamera()
                        },
                        {
                            state = State.ERROR
                            value = FrontPictureState.Error(it)
                        })
    }

    private fun getOutputMediaFile(): File? {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        val mediaStorageDir = File(Environment.getExternalStorageDirectory(), AppData.folder_name)

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val mediaFile: File
        mediaFile = File(mediaStorageDir.path + File.separator + "IMG_" + timeStamp + ".jpg")
        return mediaFile
    }

    companion object {
        private const val NO_CAMERA_ID = -1
    }

}