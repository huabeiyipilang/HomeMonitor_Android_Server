package com.penghaonan.homemonitor.server.command;

import com.penghaonan.appframework.AppDelegate;
import com.penghaonan.homemonitor.server.manager.CommandManager;
import com.penghaonan.homemonitor.server.messenger.AMessage;
import com.penghaonan.homemonitor.server.messenger.AMessengerAdapter;
import com.penghaonan.homemonitor.server.messenger.Client;
import com.penghaonan.homemonitor.server.transfer.CmdRequest;

/**
 * 所有命令的父类
 * Created by carl on 2/27/16.
 */
public abstract class ACommand {

    protected CmdRequest mRequest;
    protected AMessage mMessage;
    private CommandListener mListener;

    public interface CommandListener {
        void onFinished(ACommand command);
    }

    public void setRequest(CmdRequest request) {
        mRequest = request;
    }

    public CmdRequest getRequest() {
        return mRequest;
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

    public void notifyFinished() {
        AppDelegate.post(new Runnable() {
            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onFinished(ACommand.this);
                }
            }
        });
    }

    /**
     * 检查命令正确性
     */
    abstract public boolean isCommandValid();

    /**
     * 是否支持该命令
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
