package com.penghaonan.homemonitor.connectionservice.command;

import com.penghaonan.homemonitor.connectionservice.manager.CommandProfile;

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
        String response = GetProfile.class.getSimpleName().toLowerCase() + ":" + CommandProfile.getProfile();
        getMessenger().sendTextMessage(getClient(), response, null);
        notifyFinished();
    }
}
