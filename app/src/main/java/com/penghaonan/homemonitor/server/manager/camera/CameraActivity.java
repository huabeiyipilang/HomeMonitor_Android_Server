package com.penghaonan.homemonitor.server.manager.camera;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.penghaonan.homemonitor.server.App;
import com.penghaonan.homemonitor.server.R;
import com.penghaonan.homemonitor.server.base.BaseActivity;

public class CameraActivity extends BaseActivity {
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private static CameraActivity sActivity;
    private static Runnable sRunnable;

    public static void finishActivity() {
        if (sActivity != null) {
            sActivity.finish();
        }
    }

    public static void startActivity(Runnable runnable){
        Intent intent = new Intent(App.getContext(), CameraActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(intent);
        sRunnable = runnable;
    }

    public static CameraActivity getInstance(){
        return sActivity;
    }

    public SurfaceView getSurfaceView(){
        return mSurfaceView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sActivity = this;
        setContentView(R.layout.activity_camera);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(sRunnable, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sActivity = null;
        sRunnable = null;
        CameraManager.getInstance().releaseCamera();
    }
}
