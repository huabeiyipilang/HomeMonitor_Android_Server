package com.penghaonan.homemonitor.server.manager.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.SurfaceView;

import com.penghaonan.appframework.utils.BitmapUtils;
import com.penghaonan.appframework.utils.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

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
//                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
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
                            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
                            android.hardware.Camera.getCameraInfo(0, info);
                            Bitmap bitmap = BitmapUtils.rotate(bm, info.orientation, Bitmap.Config.RGB_565);

                            String picname = "sdcard/1234566.jpg";//要保存在哪里，路径你自己设
                            File myCaptureFile = new File(picname);
                            int res = 0;
                            String msg = "";
                            try {
                                BufferedOutputStream bos = new BufferedOutputStream
                                        (new FileOutputStream(myCaptureFile));
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
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
        } else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
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
                        if (listener != null) {
                            listener.onActionCallback(0, "Camera open success!");
                        }
                    } catch (Exception e) {
                        Logger.e(e);
                        if (mCamera != null) {
                            try {
                                mCamera.release();
                            } catch (Exception ignored) {
                            }
                        }
                        if (listener != null) {
                            listener.onActionCallback(1, "Camera open failed!");
                        }
                    }
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
        } else {
            if (listener != null) {
                listener.onActionCallback(0, "Torch off!");
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
