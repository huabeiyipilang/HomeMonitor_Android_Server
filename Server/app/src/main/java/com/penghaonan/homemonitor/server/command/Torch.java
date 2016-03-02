package com.penghaonan.homemonitor.server.command;

import android.hardware.Camera;
import android.text.TextUtils;

import com.penghaonan.homemonitor.server.manager.CameraManager;
import com.penghaonan.homemonitor.server.messenger.TextMessage;

/**
 * Created by carl on 2/27/16.
 */
public class Torch extends ACommand {

    private final static String CMD = "torch";
    private final static String ARG_ON = "on";
    private final static String ARG_OFF = "off";
    private final static String ARG_INFO = "info";

    private String mAction;

    public static boolean match(String cmdStr) {
        boolean res = CMD.equals(cmdStr);
        return res;
    }

    @Override
    public boolean isCommandValid() {
        if (mMessage == null) {
            return false;
        }
        String message = ((TextMessage) mMessage).getMessage();
        if (TextUtils.isEmpty(message)) {
            return false;
        }

        String[] parts = message.split(" ");
        if (parts.length != 2) {
            return false;
        }

        mAction = parts[1];
        if (!ARG_INFO.equals(mAction) && !ARG_ON.equals(mAction) && !ARG_OFF.equals(mAction)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isSupport() {
        //TODO 需要判断设备是否支持设备
        return true;
    }

    @Override
    public void execute() {
        if (ARG_ON.equals(mAction)) {
            CameraManager.getInstance().torchOn(new CameraManager.CameraActionListener() {
                @Override
                public void onActionCallback(int result, String msg) {
                    if (result != 0){
                        sendTextToClient(msg);
                    }
                }
            });
        } else if (ARG_OFF.equals(mAction)) {
            CameraManager.getInstance().torchOff(new CameraManager.CameraActionListener() {
                @Override
                public void onActionCallback(int result, String msg) {
                    if (result != 0) {
                        sendTextToClient(msg);
                    }
                }
            });
        } else if ((ARG_INFO.equals(mAction))) {
            boolean isTorchOn = CameraManager.getInstance().isTorchOn();
            sendTextToClient("Torch is " + (isTorchOn ? "ON" : "OFF"));
        }
    }
}
