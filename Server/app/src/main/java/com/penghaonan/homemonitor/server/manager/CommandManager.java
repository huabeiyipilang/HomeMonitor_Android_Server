package com.penghaonan.homemonitor.server.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.penghaonan.homemonitor.server.App;
import com.penghaonan.homemonitor.server.command.ACommand;
import com.penghaonan.homemonitor.server.command.TakePic;
import com.penghaonan.homemonitor.server.command.Torch;
import com.penghaonan.homemonitor.server.messenger.AMessage;
import com.penghaonan.homemonitor.server.messenger.AMessengerAdapter;
import com.penghaonan.homemonitor.server.messenger.TextMessage;
import com.penghaonan.homemonitor.server.messenger.easemob.EasemobMessengerAdapter;
import com.penghaonan.homemonitor.server.messenger.local.LocalMessengerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by carl on 2/27/16.
 */
public class CommandManager implements AMessengerAdapter.MessageListener {

    private static CommandManager ourInstance = new CommandManager();
    private static Context sContext;

    private LinkedList<ACommand> mCacheCommands = new LinkedList<>();
    private LinkedList<ACommand> mRunningCommands = new LinkedList<>();

    private AMessengerAdapter mMessengerAdapter;

    public static CommandManager getInstance() {
        if (sContext == null) {
            sContext = App.getContext();
        }
        return ourInstance;
    }

    private CommandManager() {
//        mMessengerAdapter = new LocalMessengerAdapter();
        mMessengerAdapter = new EasemobMessengerAdapter();
        mMessengerAdapter.setMessageListener(this);
    }

    public void requestPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
                    0);
        }
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

        executeCommand(command);
    }

    /**
     * 根据cmd创建ACommand实例
     *
     * @param cmd
     * @return
     */
    private ACommand createCommand(String cmd) {
        ACommand command = null;
        for (Class<? extends ACommand> cls : CommandProfile.getCommandClassList()) {
            if (cls.getSimpleName().toLowerCase().equals(cmd)) {
                try {
                    command = cls.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return command;
    }

    /**
     * 执行命令
     *
     * @param command
     */
    private void executeCommand(ACommand command) {
        if (!command.isSupport()) {
            mMessengerAdapter.sendTextMessage(command.getClient(), "This command is not support!", null);
        } else if (!command.isCommandValid()) {
            mMessengerAdapter.sendTextMessage(command.getClient(), "Command invalid!", null);
        } else {
            command.execute();
        }
    }

}
