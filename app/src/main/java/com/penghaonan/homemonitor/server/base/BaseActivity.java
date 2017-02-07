package com.penghaonan.homemonitor.server.base;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import com.penghaonan.appframework.base.BaseFrameworkActivity;

public class BaseActivity extends BaseFrameworkActivity {
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, getClass().getSimpleName());
        mWakeLock.acquire();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }
}
