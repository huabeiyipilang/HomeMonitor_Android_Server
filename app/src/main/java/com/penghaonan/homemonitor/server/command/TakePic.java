package com.penghaonan.homemonitor.server.command;

import com.penghaonan.homemonitor.server.manager.camera.CameraManager;

/**
 * 拍照命令
 * Created by carl on 3/2/16.
 */
public class TakePic extends ACommand {

    @Override
    public boolean isCommandValid() {
        if (mMessage == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isSupport() {
        return true;
    }

    @Override
    public void execute() {
        CameraManager.getInstance().takePic(new CameraManager.CameraActionListener() {
            @Override
            public void onActionCallback(int result, String msg) {
                getMessenger().sendImageMessage(getClient(), msg, null);
                notifyFinished();
            }
        });
    }
}
