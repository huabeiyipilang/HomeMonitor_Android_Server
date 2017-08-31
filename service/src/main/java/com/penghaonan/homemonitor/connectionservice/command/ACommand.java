package com.penghaonan.homemonitor.connectionservice.command;

import com.penghaonan.appframework.AppDelegate;
import com.penghaonan.homemonitor.connectionservice.manager.CommandManager;
import com.penghaonan.homemonitor.connectionservice.messenger.AMessage;
import com.penghaonan.homemonitor.connectionservice.messenger.AMessengerAdapter;
import com.penghaonan.homemonitor.connectionservice.messenger.Client;

/**
 * 所有命令的父类
 * Created by carl on 2/27/16.
 */
public abstract class ACommand {

    protected String mCommandStr;
    protected AMessage mMessage;
    private CommandListener mListener;
    private boolean mCancel;

    public String getCommandStr() {
        return mCommandStr;
    }

    public void setCommandStr(String cmd) {
        mCommandStr = cmd;
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

    public boolean isCancel() {
        return mCancel;
    }

    public void cancel() {
        mCancel = true;
    }

    public void setCommandListener(CommandListener listener) {
        mListener = listener;
    }

    protected void notifyFinished() {
        if (isCancel()) {
            return;
        }
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

    /**
     * 命令队列
     */
    public String getQueueId() {
        return CommandManager.QUEUE_NONE;
    }

    protected AMessengerAdapter getMessenger() {
        return CommandManager.getInstance().getMessengerAdapter();
    }

    @Override
    public String toString() {
        return mCommandStr;
    }

    public interface CommandListener {
        void onFinished(ACommand command);
    }
}
