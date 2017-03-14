package com.penghaonan.homemonitor.server.manager;

import android.text.TextUtils;

import com.penghaonan.appframework.utils.CollectionUtils;
import com.penghaonan.appframework.utils.Logger;
import com.penghaonan.homemonitor.server.BuildConfig;
import com.penghaonan.homemonitor.server.command.ACommand;
import com.penghaonan.homemonitor.server.messenger.AMessage;
import com.penghaonan.homemonitor.server.messenger.AMessengerAdapter;
import com.penghaonan.homemonitor.server.messenger.TextMessage;
import com.penghaonan.homemonitor.server.messenger.easemob.EasemobMessengerAdapter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


/**
 * Command处理，管理
 * Created by carl on 2/27/16.
 */
public class CommandManager implements AMessengerAdapter.MessageListener {

    public final static String QUEUE_CAMERA = "camera";
    public final static String QUEUE_NONE = "";
    private static CommandManager ourInstance = new CommandManager();
    private Map<String, Queue<ACommand>> mCmdQueueMap = new HashMap<>();
    private Map<String, List<ACommand>> mRunningCmdMap = new HashMap<>();

    private AMessengerAdapter mMessengerAdapter;
    private ACommand.CommandListener mCommandListener = new ACommand.CommandListener() {

        @Override
        public void onFinished(ACommand command) {
            onCommandFinished(command);
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
        postCommand(command);
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
        if (CommandManager.QUEUE_NONE.equals(command.getQueueId())) {
            //无需排队
            runCommand(command);
        } else {
            //有队列
            List<ACommand> runningList = mRunningCmdMap.get(command.getQueueId());
            if (CollectionUtils.isEmpty(runningList)) {
                //如果改队列号中没有正在执行的，则直接执行
                runCommand(command);
            } else {
                ACommand runningCmd = runningList.get(0);
                //如果有正在执行的，扔进队列
                mMessengerAdapter.sendTextMessage(command.getClient(), "There has running command. Add this command in queue.", null);
                if (BuildConfig.DEBUG) {
                    mMessengerAdapter.sendTextMessage(command.getClient(), "Queue Id : " + runningCmd.getQueueId() + "Running command:" + runningCmd.getCommandStr(), null);
                }
                Queue<ACommand> cmdQueue = mCmdQueueMap.get(command.getQueueId());
                if (cmdQueue == null) {
                    cmdQueue = new LinkedList<>();
                    mCmdQueueMap.put(command.getQueueId(), cmdQueue);
                }
                cmdQueue.add(command);
                Logger.i("Command put in queue:" + command);
            }
        }
    }

    private void runCommand(ACommand command) {
        List<ACommand> cmdList = mRunningCmdMap.get(command.getQueueId());
        if (cmdList == null) {
            cmdList = new LinkedList<>();
            mRunningCmdMap.put(command.getQueueId(), cmdList);
        }
        cmdList.add(command);
        command.setCommandListener(mCommandListener);
        Logger.i("runCommand:" + command);
        command.execute();
    }

    private void onCommandFinished(ACommand command) {
        if (command == null) {
            return;
        }
        Logger.i("onCommandFinished:" + command);
        List<ACommand> commandList = mRunningCmdMap.get(command.getQueueId());
        if (CollectionUtils.isEmpty(commandList)) {
            return;
        }
        command.setCommandListener(null);
        commandList.remove(command);
    }

    public void reset() {
        for (Map.Entry<String, List<ACommand>> entry : mRunningCmdMap.entrySet()) {
            for (ACommand command : entry.getValue()) {
                command.cancel();
            }
        }
        mRunningCmdMap.clear();
        mCmdQueueMap.clear();
    }
}