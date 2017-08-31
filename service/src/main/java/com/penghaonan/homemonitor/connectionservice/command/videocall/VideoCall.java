package com.penghaonan.homemonitor.connectionservice.command.videocall;

import com.penghaonan.homemonitor.connectionservice.command.ACommand;
import com.penghaonan.homemonitor.connectionservice.manager.CommandManager;
import com.penghaonan.homemonitor.connectionservice.messenger.easemob.EasemobMessengerAdapter;

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
    public String getQueueId() {
        return CommandManager.QUEUE_CAMERA;
    }

    @Override
    public void execute() {
        VideoCallActivity.startVideoCall(this);
    }

    @Override
    public void notifyFinished() {
        super.notifyFinished();
    }
}