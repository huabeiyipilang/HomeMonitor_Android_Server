package com.penghaonan.homemonitor.server.manager;

import android.content.Context;
import android.text.TextUtils;

import com.penghaonan.homemonitor.server.App;
import com.penghaonan.homemonitor.server.command.ACommand;
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
    private static List<Class<? extends ACommand>> sCommandCls;

    static {
        sCommandCls = new LinkedList<>();
        sCommandCls.add(Torch.class);
    }

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

    @Override
    public void onMessageReceive(AMessage message) {
        if (message instanceof TextMessage) {
            handleTextMessage((TextMessage) message);
        } else {
            mMessengerAdapter.sendTextMessage(message.mClient, "Unknown message type!");
        }
    }

    public AMessengerAdapter getMessengerAdapter(){
        return mMessengerAdapter;
    }

    private void handleTextMessage(TextMessage msg) {
        String cmd = msg.getCommand();
        if (TextUtils.isEmpty(cmd)) {
            mMessengerAdapter.sendTextMessage(msg.mClient, "Error command!");
            return;
        }

        ACommand command = createCommand(cmd);
        if (command == null){
            mMessengerAdapter.sendTextMessage(msg.mClient, "No such command!");
            return;
        }
        command.setCommandStr(msg.getMessage());
        command.setMessage(msg);

        executeCommand(command);
    }

    /**
     * 根据cmd创建ACommand实例
     * @param cmd
     * @return
     */
    private ACommand createCommand(String cmd){
        ACommand command = null;
        for (Class<? extends ACommand> cls : sCommandCls) {
            Method matchMethod;
            try {
                matchMethod = cls.getMethod("match", String.class);
                boolean match = (boolean) matchMethod.invoke(cls, cmd);
                if (match){
                    command = cls.newInstance();
                    break;
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return command;
    }

    /**
     * 执行命令
     * @param command
     */
    private void executeCommand(ACommand command) {
        if (!command.isSupport()) {
            mMessengerAdapter.sendTextMessage(command.getClient(), "This command is not support!");
        } else if (!command.isCommandValid()) {
            mMessengerAdapter.sendTextMessage(command.getClient(), "Command invalid!");
        } else {
            command.execute();
        }
    }

}
