package com.penghaonan.homemonitor.server.manager.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.SurfaceView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by carl on 2/28/16.
 */
public class CameraManager {
    private static CameraManager ourInstance = new CameraManager();

    private Camera mCamera;

    public interface CameraActionListener {
        void onActionCallback(int result, String msg);
    }

    public static CameraManager getInstance() {
        return ourInstance;
    }


    private CameraManager() {
    }

    private boolean isInUse() {
        return CameraActivity.getInstance() != null;
    }

    //for Commands
    public void takePic(final CameraActionListener listener) {
        if (isInUse()) {
            listener.onActionCallback(1, "Camera is using by other command!");
        } else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    SurfaceView surfaceView = CameraActivity.getInstance().getSurfaceView();
                    if (surfaceView == null) {
                        return;
                    }
                    openCamera();
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setPictureFormat(ImageFormat.JPEG);
                    List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
                    Camera.Size size = pictureSizes.get(11);
                    parameters.setPreviewSize(size.width, size.height);
                    parameters.setPictureSize(size.width, size.height);
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(parameters);
                    try {
                        mCamera.setPreviewDisplay(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();
                    mCamera.takePicture(null, null, new Camera.PictureCallback() {
                        public void onPictureTaken(byte[] _data, Camera _camera) {
                            Bitmap bm = BitmapFactory.decodeByteArray(_data, 0, _data.length);

                            String picname = "sdcard/1234566.jpg";//要保存在哪里，路径你自己设
                            File myCaptureFile = new File(picname);
                            int res = 0;
                            String msg = "";
                            try {
                                BufferedOutputStream bos = new BufferedOutputStream
                                        (new FileOutputStream(myCaptureFile));
                                bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                                bos.flush();
                                bos.close();
                                releaseCamera();
                                msg = picname;
                            } catch (Exception e) {
                                e.printStackTrace();
                                res = 1;
                                msg = e.getMessage();
                            }
                            if (listener != null) {
                                listener.onActionCallback(res, msg);
                            }
                            CameraActivity.finishActivity();
                        }
                    });
                }
            };
            CameraActivity.startActivity(runnable);
        }
    }

    public void torchOn(final CameraActionListener listener) {
        if (isInUse()) {
            listener.onActionCallback(1, "Camera is using by other command!");
        }else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    SurfaceView surfaceView = CameraActivity.getInstance().getSurfaceView();
                    if (surfaceView == null) {
                        return;
                    }
                    openCamera();

                    if (mCamera == null) {
                        if (listener != null) {
                            listener.onActionCallback(1, "Camera open failed!");
                        }
                        return;
                    }

                    Camera.Parameters parameters = mCamera.getParameters();
                    List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
                    Camera.Size size = pictureSizes.get(11);
                    parameters.setPreviewSize(size.width, size.height);
                    parameters.setPictureSize(size.width, size.height);
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(parameters);
                    try {
                        mCamera.setPreviewDisplay(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCamera.startPreview();
                }
            };
            CameraActivity.startActivity(runnable);

        }

    }

    public void torchOff(CameraActionListener listener) {
        if (!isTorchOn()) {
            if (listener != null) {
                listener.onActionCallback(1, "Torch not on!");
            }
        }
        CameraActivity.finishActivity();
    }

    private void openCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isTorchOn() {
        if (!isInUse() || mCamera == null) {
            return false;
        }

        Camera.Parameters parameter = mCamera.getParameters();
        String flashMode = parameter.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            return false;
        }

        return true;
    }

    void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        mCamera = null;
    }

}
