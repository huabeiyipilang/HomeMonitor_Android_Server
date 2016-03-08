package com.penghaonan.homemonitor.server.manager;

import android.hardware.Camera;

/**
 * Created by carl on 2/28/16.
 */
public class CameraManager {
    private static CameraManager ourInstance = new CameraManager();

    private Camera mCamera;

    public interface CameraActionListener{
        void onActionCallback(int result, String msg);
    }

    public static CameraManager getInstance() {
        return ourInstance;
    }

    private CameraManager() {
    }

    private void openCamera(){
        if (mCamera == null){
            try {
                mCamera = Camera.open();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void torchOn(CameraActionListener listener){
        openCamera();
        if (mCamera == null){
            if (listener != null){
                listener.onActionCallback(1, "Camera open failed!");
            }
            return;
        }

        Camera.Parameters mParameters = mCamera.getParameters();
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(mParameters);
        mCamera.startPreview();

        try{
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void torchOff(CameraActionListener listener){
        if (!isTorchOn()){
            if (listener != null){
                listener.onActionCallback(1, "Torch not on!");
            }
        }

        Camera.Parameters parameter = mCamera.getParameters();

        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(parameter);
        mCamera.stopPreview();

        releaseCamera();
    }

    public boolean isTorchOn(){
        if (mCamera == null){
            return false;
        }

        Camera.Parameters parameter = mCamera.getParameters();
        String flashMode = parameter.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)){
            return false;
        }

        return true;
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
        }
        mCamera = null;
    }
}
