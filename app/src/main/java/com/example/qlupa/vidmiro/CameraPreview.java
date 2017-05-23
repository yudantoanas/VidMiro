package com.example.qlupa.vidmiro;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

import static android.content.ContentValues.TAG;
import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by yudan on 19/05/2017.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    public static int orientation;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context mContext;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mContext = context;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        setCameraRotation();

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void setCameraRotation() {
        try {

            Camera.CameraInfo camInfo = new Camera.CameraInfo();

            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, camInfo);

            int cameraRotationOffset = camInfo.orientation;

            Camera.Parameters parameters = mCamera.getParameters();

            int rotation = ((Activity)mContext).getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break; // Natural orientation
                case Surface.ROTATION_90:
                    degrees = 90;
                    break; // Landscape left
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;// Upside down
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;// Landscape right
            }
            int displayRotation;
            displayRotation = (cameraRotationOffset + degrees) % 360;
            displayRotation = (360 - displayRotation) % 360; // compensate the mirror

            mCamera.setDisplayOrientation(displayRotation);

            orientation = (360 + cameraRotationOffset + degrees) % 360;
//            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//            {
//                parameters.set("orientation", "portrait");
//                parameters.setRotation(orientation);
//
//            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
//            {
//                parameters.set("orientation", "landscape");
//                parameters.setRotation(orientation);
//
//            }
            parameters.set("orientation", "portrait");
            parameters.setRotation(orientation);
            mCamera.setParameters(parameters);

        } catch (Exception e) {

        }
    }
}