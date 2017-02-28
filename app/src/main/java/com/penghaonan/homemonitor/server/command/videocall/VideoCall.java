package com.penghaonan.homemonitor.server.command.videocall;

import com.penghaonan.homemonitor.server.command.ACommand;
import com.penghaonan.homemonitor.server.manager.CommandManager;
import com.penghaonan.homemonitor.server.messenger.easemob.EasemobMessengerAdapter;

public class VideoCall extends ACommand {

    @Override
    public boolean isCommandValid() {
        return true;
    }

    @Override
    public boolean isSupport() {
        if (CommandManager.getInstance().getMessengerAdapter() instanceof EasemobMessengerAdapter) {
            return true;
        }
        return false;
    }

    @Override
    public void execute() {
        VideoCallActivity.startVideoCall(this);
    }
}