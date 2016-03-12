package com.penghaonan.homemonitor.server.manager.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.view.SurfaceView;

import com.penghaonan.homemonitor.server.App;
import com.penghaonan.homemonitor.server.messenger.easemob.LoginActivity;

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

    private void openCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void torchOn(CameraActionListener listener) {
        openCamera();
        if (mCamera == null) {
            if (listener != null) {
                listener.onActionCallback(1, "Camera open failed!");
            }
            return;
        }

        Camera.Parameters mParameters = mCamera.getParameters();
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(mParameters);
        mCamera.startPreview();

        try {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void torchOff(CameraActionListener listener) {
        if (!isTorchOn()) {
            if (listener != null) {
                listener.onActionCallback(1, "Torch not on!");
            }
        }

        Camera.Parameters parameter = mCamera.getParameters();

        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(parameter);

        releaseCamera();
    }

    public boolean isTorchOn() {
        if (mCamera == null) {
            return false;
        }

        Camera.Parameters parameter = mCamera.getParameters();
        String flashMode = parameter.getFlashMode();
        if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {
            return false;
        }

        return true;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        mCamera = null;
    }

    private CameraActionListener mListener;

    public void takePic(CameraActionListener listener) {
        mListener = listener;
        Intent intent = new Intent(App.getContext(), CameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(intent);

    }

    void takePic(SurfaceView surfaceView, final CameraActionListener listener) {
        openCamera();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        Camera.Size size = pictureSizes.get(11);
        parameters.setPreviewSize(size.width, size.height);
        parameters.setPictureSize(size.width, size.height);
        mCamera.setParameters(parameters);
        try {
            mCamera.setPreviewDisplay(surfaceView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            public void onPictureTaken(byte[] _data, Camera _camera) {
                /* onPictureTaken传入的第一个参数即为相片的byte */
                Bitmap bm = BitmapFactory.decodeByteArray(_data, 0, _data.length);

                /* 创建新文件 */
                String picname = "sdcard/1234566.jpg";//要保存在哪里，路径你自己设
                File myCaptureFile = new File(picname);
                int res = 0;
                String msg = "";
                try {
                    BufferedOutputStream bos = new BufferedOutputStream
                            (new FileOutputStream(myCaptureFile));

                    /* 采用压缩转档方法 */
                    bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);

                    /* 调用flush()方法，更新BufferStream */
                    bos.flush();

                    /* 结束OutputStream */
                    bos.close();

                    /* 将拍照下来且存储完毕的图文件，显示出来 */
                    //mImageView01.setImageBitmap(bm);

                    /* 显示完图文件，立即重置相机，并关闭预览 */
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
                if (mListener != null){
                    mListener.onActionCallback(res, msg);
                }
                releaseCamera();
            }
        });
    }
}
