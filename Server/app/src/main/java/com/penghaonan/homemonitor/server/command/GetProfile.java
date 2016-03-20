package com.penghaonan.homemonitor.server.command;

import com.penghaonan.homemonitor.server.manager.CommandProfile;

/**
 * Created by carl on 3/20/16.
 */
public class GetProfile extends ACommand{

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
        getMessenger().sendTextMessage(getClient(), CommandProfile.getProfile(), null);
    }
}
