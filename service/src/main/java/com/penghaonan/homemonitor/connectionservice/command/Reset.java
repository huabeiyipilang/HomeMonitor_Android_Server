package com.penghaonan.homemonitor.connectionservice.command;

import android.content.Intent;

import com.penghaonan.appframework.AppDelegate;
import com.penghaonan.homemonitor.connectionservice.Constants;
import com.penghaonan.homemonitor.connectionservice.R;
import com.penghaonan.homemonitor.connectionservice.manager.CommandManager;

public class Reset extends ACommand {
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
        CommandManager.getInstance().reset();
        AppDelegate.getApp().sendBroadcast(new Intent(Constants.ACTION_RESET));
        getMessenger().sendTextMessage(getClient(), AppDelegate.getString(R.string.reseting_try_later), null);
    }
}
