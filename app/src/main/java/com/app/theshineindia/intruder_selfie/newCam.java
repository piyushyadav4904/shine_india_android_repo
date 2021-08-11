package com.app.theshineindia.intruder_selfie;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class newCam {

    private static final SparseIntArray orientations = new SparseIntArray();

    static {
        orientations.append(Surface.ROTATION_0, 90);
        orientations.append(Surface.ROTATION_90, 0);
        orientations.append(Surface.ROTATION_180, 270);
        orientations.append(Surface.ROTATION_270, 180);
    }

    Button button;
    TextureView textureView;
    Context context;
    CameraDevice cameraDevice;
    CameraCaptureSession cameraCaptureSession;
    CaptureRequest captureRequest;
    CaptureRequest.Builder captureRequestBuilder;
    Handler mBackgroundHandler;
    HandlerThread mBackgroundThread;
    private String cameraId;
    private Size imageDimensions;
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            try {
                Log.e("123", "opening camera");
                openCamera();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };
    private ImageReader imageReader;
    private File file;
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
            try {
                Log.e("123", "state opened opened");
                createCameraPreview();
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.e("123", "state opened" + e.getMessage());
            }

        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
            Log.e("123", "state error " + error);
        }
    };


    public newCam(Context context) {
        this.context = context;

        textureView = new TextureView(context);
        textureView.setSurfaceTextureListener(textureListener);
        if (textureView.isAvailable()) {
            Log.e("123", "calling");
            textureListener.onSurfaceTextureAvailable(textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight());
        }
        Log.e("123", "constrictor");
        /*try {
            //openCamera();
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.e("123", "cons" + e.getMessage());
        }*/

    }

    public void takePic() {
        Log.e("123", "rake pic");
        try {
            takePicture();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCameraPreview() throws CameraAccessException {
        Log.e("123", "create preivew");
        SurfaceTexture texture = textureView.getSurfaceTexture();
        texture.setDefaultBufferSize(imageDimensions.getWidth(), imageDimensions.getHeight());
        Surface surface = new Surface(texture);
        captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        captureRequestBuilder.addTarget(surface);

        cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(CameraCaptureSession session) {
                Toast.makeText(context, "On Config", Toast.LENGTH_SHORT).show();
                if (cameraDevice == null) {
                    return;
                }

                cameraCaptureSession = session;
                try {
                    updatePreview();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onConfigureFailed(CameraCaptureSession session) {
                Toast.makeText(context, "Configuration Changed", Toast.LENGTH_SHORT).show();

            }
        }, null);

        takePic();

    }

    private void updatePreview() throws CameraAccessException {
        if (cameraDevice == null) {
            return;
        }

        captureRequestBuilder.set(captureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);

    }

    private void openCamera() throws CameraAccessException {
        Log.e("123", "open camera");
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        cameraId = manager.getCameraIdList()[0];
        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        imageDimensions = map.getOutputSizes(SurfaceTexture.class)[0];

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            return;
        }


        manager.openCamera(cameraId, stateCallback, null);


    }

    private void takePicture() throws CameraAccessException {
        if (cameraDevice == null) {
            Log.e("123", "null");
            return;
        }

        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
        Size[] jpegSizes = null;
        jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);

        int width = 640;
        int height = 480;

        if (jpegSizes != null && jpegSizes.length > 0) {
            width = jpegSizes[0].getWidth();
            height = jpegSizes[0].getHeight();
        }

        ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
        List<Surface> outputSurfaces = new ArrayList<>(2);
        outputSurfaces.add(reader.getSurface());

        outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));

        final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(cameraDevice.TEMPLATE_STILL_CAPTURE);
        captureBuilder.addTarget(reader.getSurface());
        captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        captureBuilder.set(captureRequest.JPEG_ORIENTATION, orientations.get(rotation));

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        file = new File(Environment.getExternalStorageDirectory() + "/" + ts + ".jpg");

        ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Toast.makeText(context, "Image Avail", Toast.LENGTH_SHORT).show();
                Image image = null;
                image = reader.acquireLatestImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.capacity()];
                buffer.get(bytes);
                try {
                    save(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (image != null) {
                        image.close();
                    }
                }

            }
        };

        reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

        final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
            @Override
            public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                super.onCaptureCompleted(session, request, result);
                Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
                try {
                    createCameraPreview();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

            }
        };

        cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(CameraCaptureSession session) {
                Toast.makeText(context, "On Config 2", Toast.LENGTH_SHORT).show();
                try {
                    session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onConfigureFailed(CameraCaptureSession session) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();

            }
        }, mBackgroundHandler);

    }

    private void save(byte[] bytes) throws IOException {
        OutputStream outputStream = null;
        outputStream = new FileOutputStream(file);
        outputStream.write(bytes);
        outputStream.close();
        Log.e("123", "success");
    }


    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }


    protected void stopBackgroundThread() throws InterruptedException {
        mBackgroundThread.quitSafely();
        mBackgroundThread.join();
        mBackgroundThread = null;
        mBackgroundHandler = null;
    }

    public void setup() {
        Camera mCamera = getAvailableFrontCamera();     // globally declared instance of camera
        if (mCamera == null) {
            mCamera = Camera.open();    //Take rear facing camera only if no front camera available
        }
        SurfaceView sv = new SurfaceView(context);
        SurfaceTexture surfaceTexture = new SurfaceTexture(10);


        Camera.PictureCallback mCall = new Camera.PictureCallback() {

            public void onPictureTaken(byte[] data, Camera camera) {
                //decode the data obtained by the camera into a Bitmap

                FileOutputStream outStream = null;
                try {

                    // create a File object for the parent directory
                    File myDirectory = new File(Environment.getExternalStorageDirectory() + "/Test");
                    // have the object build the directory structure, if needed.
                    myDirectory.mkdirs();

                    //SDF for getting current time for unique image name
                    SimpleDateFormat curTimeFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
                    String curTime = curTimeFormat.format(new java.util.Date());

                    // create a File object for the output file
                    outStream = new FileOutputStream(myDirectory + "/user" + curTime + ".jpg");
                    outStream.write(data);
                    outStream.close();
                    Log.e("123", "wrote");
                    //mCamera.release();
                    //mCamera = null;

                    String strImagePath = Environment.getExternalStorageDirectory() + "/" + myDirectory.getName() + "/user" + curTime + ".jpg";
                    //sendEmailWithImage(strImagePath);
                    Log.d("CAMERA", "picture clicked - " + strImagePath);
                } catch (FileNotFoundException e) {
                    Log.d("CAMERA", e.getMessage());
                } catch (IOException e) {
                    Log.d("CAMERA", e.getMessage());
                }


            }
        };


        try {
            mCamera.setPreviewTexture(surfaceTexture);
            //mCamera.setPreviewDisplay(sv.getHolder());
            Camera.Parameters parameters = mCamera.getParameters();

            //set camera parameters
            mCamera.setParameters(parameters);


            //This boolean is used as app crashes while writing images to file if simultaneous calls are made to takePicture
            if (true) {
                mCamera.startPreview();
                mCamera.takePicture(null, null, mCall);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        //Get a surface
        SurfaceHolder sHolder = sv.getHolder();
        //tells Android that this surface will have its data constantly replaced
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


    }

    private Camera getAvailableFrontCamera() {

        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("CAMERA", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }
}

/*
2021-08-11 01:30:57.723 15401-15401/com.app.theshineindia E/Zygote: isWhitelistProcess - Process is Whitelisted
2021-08-11 01:30:57.728 15401-15401/com.app.theshineindia E/Zygote: accessInfo : 1
2021-08-11 01:30:57.795 15401-15401/com.app.theshineindia E/p.theshineindi: Unknown bits set in runtime_flags: 0x8000
2021-08-11 01:31:11.589 15401-15401/com.app.theshineindia E/123: reached till here
2021-08-11 01:31:15.471 15401-15401/com.app.theshineindia E/123: reached till here
2021-08-11 01:31:15.472 15401-15401/com.app.theshineindia E/123: reached here
2021-08-11 01:31:15.744 15401-15401/com.app.theshineindia E/123: null
2021-08-11 01:31:15.744 15401-15401/com.app.theshineindia E/123: success
2021-08-11 01:31:15.746 15401-15401/com.app.theshineindia E/123: state opened opened
2021-08-11 01:31:15.748 15401-15401/com.app.theshineindia E/AndroidRuntime: FATAL EXCEPTION: main
    Process: com.app.theshineindia, PID: 15401
    java.lang.NullPointerException: Attempt to invoke virtual method 'void android.graphics.SurfaceTexture.setDefaultBufferSize(int, int)' on a null object reference
        at com.app.theshineindia.intruder_selfie.newCam.createCameraPreview(newCam.java:146)
        at com.app.theshineindia.intruder_selfie.newCam.access$100(newCam.java:42)
        at com.app.theshineindia.intruder_selfie.newCam$2.onOpened(newCam.java:97)
        at android.hardware.camera2.impl.CameraDeviceImpl$1.run(CameraDeviceImpl.java:150)
        at android.os.Handler.handleCallback(Handler.java:883)
        at android.os.Handler.dispatchMessage(Handler.java:100)
        at android.os.Looper.loop(Looper.java:237)
        at android.app.ActivityThread.main(ActivityThread.java:7948)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1075)

 */