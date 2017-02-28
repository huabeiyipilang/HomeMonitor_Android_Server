package com.penghaonan.homemonitor.server.manager;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.penghaonan.appframework.AppDelegate;
import com.penghaonan.appframework.utils.CollectionUtils;
import com.penghaonan.appframework.utils.Logger;
import com.penghaonan.homemonitor.server.R;
import com.penghaonan.homemonitor.server.command.ACommand;
import com.penghaonan.homemonitor.server.messenger.AMessage;
import com.penghaonan.homemonitor.server.messenger.AMessengerAdapter;
import com.penghaonan.homemonitor.server.messenger.TextMessage;
import com.penghaonan.homemonitor.server.messenger.easemob.EasemobMessengerAdapter;
import com.penghaonan.homemonitor.server.transfer.CmdRequest;
import com.penghaonan.homemonitor.server.transfer.CmdResponse;

import java.util.LinkedList;
import java.util.Queue;


/**
 * Command处理，管理
 * Created by carl on 2/27/16.
 */
public class CommandManager implements AMessengerAdapter.MessageListener {

    private static CommandManager ourInstance = new CommandManager();

    private final static int MAX_RUNNING_COMMAND_COUNT = 5;
    private Queue<ACommand> mCacheCommands = new LinkedList<>();
    private Queue<ACommand> mRunningCommands = new LinkedList<>();

    private AMessengerAdapter mMessengerAdapter;

    public static CommandManager getInstance() {
        return ourInstance;
    }

    private CommandManager() {
//        mMessengerAdapter = new LocalMessengerAdapter();
        mMessengerAdapter = new EasemobMessengerAdapter();
        mMessengerAdapter.setMessageListener(this);
    }

    @Override
    public void onMessageReceive(AMessage message) {
        if (message instanceof TextMessage) {
            handleTextMessage((TextMessage) message);
        } else {
            Logger.e("Unknown mssage type");
        }
    }

    public AMessengerAdapter getMessengerAdapter() {
        return mMessengerAdapter;
    }

    /**
     * 处理文本请求
     */
    private void handleTextMessage(TextMessage msg) {
        String cmdStr = msg.mMessage;
        if (TextUtils.isEmpty(cmdStr)) {
            Logger.e("Command is null!");
            return;
        }

        CmdRequest request = JSON.parseObject(cmdStr, CmdRequest.class);
        if (request == null) {
            Logger.e("Command format error");
            return;
        }

        request.client = msg.mClient;
        if (!request.isValid()) {
            Logger.e("Command invalid");
            return;
        }

        ACommand command = createCommand(request);
        if (command == null) {
            mMessengerAdapter.sendTextResponse(request, CmdResponse.CODE_FAILED, AppDelegate.getString(R.string.request_failed_cmd_not_support));
            return;
        }
        command.setMessage(msg);
        if (!command.isSupport()) {
            mMessengerAdapter.sendTextResponse(request, CmdResponse.CODE_FAILED, AppDelegate.getString(R.string.request_failed_cmd_not_support));
            return;
        }
        if (!command.isCommandValid()) {
            mMessengerAdapter.sendTextResponse(request, CmdResponse.CODE_FAILED, AppDelegate.getString(R.string.request_failed_cmd_invalid));
            return;
        }

        postCommand(command);
    }

    /**
     * 根据cmd创建ACommand实例
     */
    private ACommand createCommand(CmdRequest request) {
        ACommand command = null;
        for (Class<? extends ACommand> cls : CommandProfile.getCommandClassList()) {
            if (cls.getSimpleName().toLowerCase().equals(request.cmd)) {
                try {
                    command = cls.newInstance();
                    command.setRequest(request);
                } catch (Exception e) {
                    Logger.e(e);
                }
            }
        }
        return command;
    }

    private void postCommand(ACommand command) {
        mCacheCommands.add(command);
        checkCommand();
    }

    private void checkCommand() {
        if (CollectionUtils.size(mRunningCommands) < MAX_RUNNING_COMMAND_COUNT) {
            if (CollectionUtils.isEmpty(mCacheCommands)) {
                return;
            }
            ACommand command = mCacheCommands.poll();
            command.setCommandListener(mCommandListener);
            mRunningCommands.add(command);
            command.execute();
        }
    }

    private ACommand.CommandListener mCommandListener = new ACommand.CommandListener() {

        @Override
        public void onFinished(ACommand command) {
            command.setCommandListener(null);
            mRunningCommands.remove(command);
            checkCommand();
        }
    };

}