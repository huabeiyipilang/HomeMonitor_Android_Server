package com.penghaonan.homemonitor.server.command;

import com.penghaonan.appframework.AppDelegate;
import com.penghaonan.homemonitor.server.manager.CommandManager;
import com.penghaonan.homemonitor.server.messenger.AMessage;
import com.penghaonan.homemonitor.server.messenger.AMessengerAdapter;
import com.penghaonan.homemonitor.server.messenger.Client;

/**
 * 所有命令的父类
 * Created by carl on 2/27/16.
 */
public abstract class ACommand {

    protected String mCommandStr;
    protected AMessage mMessage;
    private CommandListener mListener;

    public interface CommandListener {
        void onFinished();
    }

    public void setCommandStr(String cmd) {
        mCommandStr = cmd;
    }

    public String getCommandStr() {
        return mCommandStr;
    }

    public void setMessage(AMessage message) {
        mMessage = message;
    }

    public Client getClient() {
        if (mMessage != null) {
            return mMessage.mClient;
        }
        return null;
    }

    public void setCommandListener(CommandListener listener) {
        mListener = listener;
    }

    protected void notifyFinished() {
        AppDelegate.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onFinished();
                }
            }
        });
    }

    /**
     * 检查命令正确性
     *
     * @return
     */
    abstract public boolean isCommandValid();

    /**
     * 是否支持该命令
     *
     * @return
     */
    abstract public boolean isSupport();

    /**
     * 执行
     */
    abstract public void execute();

    protected AMessengerAdapter getMessenger() {
        return CommandManager.getInstance().getMessengerAdapter();
    }
}
