package com.penghaonan.homemonitor.server.messenger;

import android.text.TextUtils;

import com.penghaonan.homemonitor.server.manager.CommandManager;

/**
 * Created by carl on 2/27/16.
 */
public abstract class AMessengerAdapter {
    public interface MessageListener {
        void onMessageReceive(AMessage message);
    }

    public interface MessageSendCallback {
        int STATE_PRE_SEND = 1;
        int STATE_SENDING = 2;
        int STATE_SEND_SUCCESS = 3;
        int STATE_SEND_FAILED = 4;

        void onStateChanged(int state, Object info);
    }

    private MessageListener mMessageListener;

    abstract public void onAppStart();

    abstract public void sendMessage(AMessage msg, MessageSendCallback callback);

    public String getServerId() {
        return null;
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

    public void setMessageListener(MessageListener listener) {
        mMessageListener = listener;
    }
}
