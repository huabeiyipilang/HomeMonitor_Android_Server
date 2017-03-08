package com.penghaonan.homemonitor.server.manager;

import android.text.TextUtils;

import com.penghaonan.appframework.utils.Logger;
import com.penghaonan.homemonitor.server.BuildConfig;
import com.penghaonan.homemonitor.server.command.ACommand;
import com.penghaonan.homemonitor.server.command.GetProfile;
import com.penghaonan.homemonitor.server.messenger.AMessage;
import com.penghaonan.homemonitor.server.messenger.AMessengerAdapter;
import com.penghaonan.homemonitor.server.messenger.TextMessage;
import com.penghaonan.homemonitor.server.messenger.easemob.EasemobMessengerAdapter;

import java.util.LinkedList;
import java.util.Queue;


/**
 * Command处理，管理
 * Created by carl on 2/27/16.
 */
public class CommandManager implements AMessengerAdapter.MessageListener {

    private static CommandManager ourInstance = new CommandManager();

    private Queue<ACommand> mCacheCommands = new LinkedList<>();
    private ACommand mRunningCommand;

    private AMessengerAdapter mMessengerAdapter;
    private ACommand.CommandListener mCommandListener = new ACommand.CommandListener() {

        @Override
        public void onFinished() {
            if (mRunningCommand != null) {
                mRunningCommand.setCommandListener(null);
                mRunningCommand = null;
            }
            checkCommand();
        }
    };

    private CommandManager() {
//        mMessengerAdapter = new LocalMessengerAdapter();
        mMessengerAdapter = new EasemobMessengerAdapter();
        mMessengerAdapter.setMessageListener(this);
    }

    public static CommandManager getInstance() {
        return ourInstance;
    }

    @Override
    public void onMessageReceive(AMessage message) {
        if (message instanceof TextMessage) {
            handleTextMessage((TextMessage) message);
        } else {
            mMessengerAdapter.sendTextMessage(message.mClient, "Unknown message type!", null);
        }
    }

    public AMessengerAdapter getMessengerAdapter() {
        return mMessengerAdapter;
    }

    private void handleTextMessage(TextMessage msg) {
        String cmd = msg.getCommand();
        if (TextUtils.isEmpty(cmd)) {
            mMessengerAdapter.sendTextMessage(msg.mClient, "Error command!", null);
            return;
        }

        ACommand command = createCommand(cmd);
        if (command == null) {
            mMessengerAdapter.sendTextMessage(msg.mClient, "No such command!", null);
            return;
        }
        command.setCommandStr(msg.getMessage());
        command.setMessage(msg);
        if (!command.isSupport()) {
            mMessengerAdapter.sendTextMessage(command.getClient(), "This command is not support!", null);
            return;
        }
        if (!command.isCommandValid()) {
            mMessengerAdapter.sendTextMessage(command.getClient(), "Command invalid!", null);
            return;
        }

        if (command instanceof GetProfile) {
            command.execute();
        } else {
            postCommand(command);
        }

    }

    /**
     * 根据cmd创建ACommand实例
     */
    private ACommand createCommand(String cmd) {
        ACommand command = null;
        for (Class<? extends ACommand> cls : CommandProfile.getCommandClassList()) {
            if (cls.getSimpleName().toLowerCase().equals(cmd)) {
                try {
                    command = cls.newInstance();
                } catch (Exception e) {
                    Logger.e(e);
                }
            }
        }
        return command;
    }

    private void postCommand(ACommand command) {
        mCacheCommands.add(command);
        if (mRunningCommand != null) {
            mMessengerAdapter.sendTextMessage(command.getClient(), "There has running command. Add this command in queue.", null);
            if (BuildConfig.DEBUG) {
                mMessengerAdapter.sendTextMessage(command.getClient(), "Running command:" + mRunningCommand.getCommandStr(), null);
            }
        }
        checkCommand();
    }

    private void checkCommand() {
        if (mRunningCommand == null) {
            mRunningCommand = mCacheCommands.poll();
            if (mRunningCommand != null) {
                mRunningCommand.setCommandListener(mCommandListener);
                mRunningCommand.execute();
            }
        } else {
            Logger.i("checkCommand", "Has running command:" + mRunningCommand.toString());
        }
    }

}