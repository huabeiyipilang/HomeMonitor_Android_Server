package com.penghaonan.homemonitor.server.messenger;

import android.text.TextUtils;

/**
 * Created by carl on 2/27/16.
 */
public abstract class AMessengerAdapter {
    public interface MessageListener{
        void onMessageReceive(AMessage message);
    }

    public interface MessageSendCallback{
        int STATE_PRE_SEND = 1;
        int STATE_SENDING = 2;
        int STATE_SEND_SUCCESS = 3;
        int STATE_SEND_FAILED = 4;

        void onStateChanged(int state, Object info);
    }

    private MessageListener mMessageListener;

    abstract public void onAppStart();

    abstract public void sendMessage(AMessage msg, MessageSendCallback callback);

    public void sendTextMessage(Client client, String msg){
        if (client == null || TextUtils.isEmpty(msg)){
            return;
        }
        TextMessage errorMessage = new TextMessage(msg);
        errorMessage.mClient = client;
        sendMessage(errorMessage, null);
    }

    public void setMessageListener(MessageListener listener){
        mMessageListener = listener;
    }
}
