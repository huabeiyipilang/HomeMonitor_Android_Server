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

    public void torchOn(CameraActionListener listener){
        try {
            mCamera = Camera.open();
        }catch (Exception e){
            if (listener != null){
                listener.onActionCallback(1, "Camera open failed!");
            }
        }

        mCamera.startPreview();
        Camera.Parameters parameter = mCamera.getParameters();
        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(parameter);

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
