package com.penghaonan.homemonitor.server.command;

import com.alibaba.fastjson.JSONObject;
import com.penghaonan.homemonitor.server.manager.CommandProfile;
import com.penghaonan.homemonitor.server.manager.camera.CameraManager;
import com.penghaonan.homemonitor.server.transfer.CmdResponse;

/**
 * 获取本服务端可支持的命令
 * Created by carl on 3/20/16.
 */
public class GetProfile extends ACommand {

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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("torch_status", CameraManager.getInstance().isTorchOn());
        jsonObject.put("cmd_profile", CommandProfile.getProfile());
        getMessenger().sendTextResponse(getRequest(), CmdResponse.CODE_SUCCESS, jsonObject.toJSONString());
        notifyFinished();
    }
}
