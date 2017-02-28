package com.penghaonan.homemonitor.server.command;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.penghaonan.homemonitor.server.manager.camera.CameraManager;
import com.penghaonan.homemonitor.server.transfer.CmdResponse;

/**
 * 手电筒命令
 * Created by carl on 2/27/16.
 */
public class Torch extends ACommand {

    private final static String ARG_ON = "on";
    private final static String ARG_OFF = "off";
    private final static String ARG_INFO = "info";

    private String mAction;

    @Override
    public boolean isCommandValid() {
        if (mMessage == null) {
            return false;
        }

        JSONObject jsonObject = JSON.parseObject(getRequest().data);

        mAction = jsonObject.getString("action");

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
                    getMessenger().sendTextResponse(getRequest(), result != 0 ? CmdResponse.CODE_FAILED : CmdResponse.CODE_SUCCESS, getResponseString(msg));
                    notifyFinished();
                }
            });
        } else if (ARG_OFF.equals(mAction)) {
            CameraManager.getInstance().torchOff(new CameraManager.CameraActionListener() {
                @Override
                public void onActionCallback(int result, String msg) {
                    getMessenger().sendTextResponse(getRequest(), result != 0 ? CmdResponse.CODE_FAILED : CmdResponse.CODE_SUCCESS, getResponseString(msg));
                    notifyFinished();
                }
            });
        } else if ((ARG_INFO.equals(mAction))) {
            getMessenger().sendTextResponse(getRequest(), CmdResponse.CODE_SUCCESS, getResponseString(null));
            notifyFinished();
        }
    }

    private String getResponseString(String msg) {
        boolean isTorchOn = CameraManager.getInstance().isTorchOn();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", isTorchOn);
        if (!TextUtils.isEmpty(msg)) {
            jsonObject.put("msg", msg);
        }
        return jsonObject.toJSONString();
    }
}
