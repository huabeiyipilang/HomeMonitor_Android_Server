/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.penghaonan.homemonitor.connectionservice.command;

import android.os.Build;

import com.penghaonan.appframework.AppDelegate;
import com.penghaonan.appframework.utils.NetworkUtils;

public class DeviceInfo extends ACommand {
    @Override
    public boolean isCommandValid() {
        return true;
    }

    @Override
    public boolean isSupport() {
        return true;
    }

    @Override
    public void execute() {
        StringBuilder sb = new StringBuilder();
        sb.append("model:").append(Build.MODEL).append("\n");
        sb.append("sdk:").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("net type:").append(NetworkUtils.getNetype(AppDelegate.getApp())).append("\n");
        sendTextMessage(sb.toString());
    }
}
