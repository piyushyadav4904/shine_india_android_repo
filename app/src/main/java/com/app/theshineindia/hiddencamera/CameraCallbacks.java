package com.app.theshineindia.hiddencamera;

import androidx.annotation.NonNull;

import java.io.File;

interface CameraCallbacks {

    void onImageCapture(@NonNull File imageFile);

    void onCameraError(@CameraError.CameraErrorCodes int errorCode);
}
