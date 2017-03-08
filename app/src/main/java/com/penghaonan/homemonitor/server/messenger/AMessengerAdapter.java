package com.penghaonan.homemonitor.server.messenger;

import android.text.TextUtils;

import com.penghaonan.appframework.AppDelegate;
import com.penghaonan.homemonitor.server.manager.CommandManager;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by carl on 2/27/16.
 */
public abstract class AMessengerAdapter {
    public interface MessageListener {
        void onMessageReceive(AMessage message);
    }

    public interface MessengerStateListener {
        void onMessengerEnable(boolean enable);
    }

    public interface MessageSendCallback {
        int STATE_PRE_SEND = 1;
        int STATE_SENDING = 2;
        int STATE_SEND_SUCCESS = 3;
        int STATE_SEND_FAILED = 4;

        void onStateChanged(int state, Object info);
    }

    private MessageListener mMessageListener;
    private final List<MessengerStateListener> mMessengerStateListeners = new LinkedList<>();

    abstract public void onAppStart();

    abstract public void sendMessage(AMessage msg, MessageSendCallback callback);

    public String getServerId() {
        return null;
    }

    public void sendTextMessage(Client client, int msgId, MessageSendCallback callback) {
        String msg = AppDelegate.getString(msgId);
        sendTextMessage(client, msg, callback);
    }

    public void sendTextMessage(Client client, String msg, MessageSendCallback callback) {
        if (client == null || TextUtils.isEmpty(msg)) {
            return;
        }
        TextMessage message = new TextMessage(msg);
        message.mClient = client;
        sendMessage(message, callback);
    }

    public void sendImageMessage(Client client, String path, MessageSendCallback callback) {
        AMessengerAdapter messenger = CommandManager.getInstance().getMessengerAdapter();
        ImageMessage imgMessage = new ImageMessage();
        imgMessage.mClient = client;
        imgMessage.setImagePath(path);
        messenger.sendMessage(imgMessage, callback);
    }

    public void sendVideoCall(Client client, MessageSendCallback callback) {}

    public void setMessageListener(MessageListener listener) {
        mMessageListener = listener;
    }

    public void addMessengerStateListener(MessengerStateListener listener) {
        synchronized (mMessengerStateListeners) {
            if (!mMessengerStateListeners.contains(listener)) {
                mMessengerStateListeners.add(listener);
            }
        }
    }

    public void removeMessengerStateListener(MessengerStateListener listener) {
        synchronized (mMessengerStateListeners) {
            if (mMessengerStateListeners.contains(listener)) {
                mMessengerStateListeners.remove(listener);
            }
        }
    }

    protected void notifyMessengerStateChanged(final boolean enable) {
        synchronized (mMessengerStateListeners) {
            for (final MessengerStateListener listener : mMessengerStateListeners) {
                AppDelegate.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onMessengerEnable(enable);
                    }
                });
            }
        }
    }
}
