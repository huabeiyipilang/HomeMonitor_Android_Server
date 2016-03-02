package com.penghaonan.homemonitor.server.command;

import com.penghaonan.homemonitor.server.CommandException;
import com.penghaonan.homemonitor.server.manager.CommandManager;
import com.penghaonan.homemonitor.server.messenger.AMessage;
import com.penghaonan.homemonitor.server.messenger.AMessengerAdapter;
import com.penghaonan.homemonitor.server.messenger.Client;

/**
 * Created by carl on 2/27/16.
 */
public abstract class ACommand {

    protected String mCommandStr;
    protected AMessage mMessage;

    /**
     * 命令匹配
     * @param cmdStr
     * @return
     */
    public static boolean match(String cmdStr) throws CommandException {
        throw new CommandException("Need to override 'match' method!");
    }

    public void setCommandStr(String cmd){
        mCommandStr = cmd;
    }

    public void setMessage(AMessage message){
        mMessage = message;
    }

    public Client getClient(){
        if (mMessage != null){
            return mMessage.mClient;
        }
        return null;
    }

    /**
     * 检查命令正确性
     * @return
     */
    abstract public boolean isCommandValid();

    /**
     * 是否支持该命令
     * @return
     */
    abstract public boolean isSupport();

    /**
     * 执行
     */
    abstract public void execute();

    protected void sendTextToClient(String text){
        AMessengerAdapter messenger = CommandManager.getInstance().getMessengerAdapter();
        messenger.sendTextMessage(getClient(), text);
    }
}
