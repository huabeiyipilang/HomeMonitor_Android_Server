package com.penghaonan.homemonitor.server.messenger;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.penghaonan.appframework.AppDelegate;
import com.penghaonan.appframework.utils.Logger;
import com.penghaonan.homemonitor.server.manager.CommandManager;
import com.penghaonan.homemonitor.server.transfer.CmdRequest;
import com.penghaonan.homemonitor.server.transfer.CmdResponse;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by carl on 2/27/16.
 * Messenger适配器
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

    /**
     * 发送文本Response
     */
    public void sendTextResponse(CmdRequest request, int code, String msg) {
        sendTextResponse(request, code, msg, null);
    }

    /**
     * 发送文本Response
     */
    public void sendTextResponse(CmdRequest request, int code, String msg, MessageSendCallback callback) {
        if (request != null && request.isValid() && !TextUtils.isEmpty(msg)) {
            //封装CmdResponse
            CmdResponse response = new CmdResponse();
            response.id = request.id;
            response.code = code;
            response.msg = msg;

            //封装成TextMessage发送
            TextMessage message = new TextMessage();
            message.mClient = request.client;
            message.mMessage = JSON.toJSONString(response);
            sendMessage(message, callback);
        } else {
            Logger.e("Response send failed! " + msg);
        }
    }

    /**
     * 发送图片Response
     */
    public void sendImageResponse(CmdRequest request, String path, MessageSendCallback callback) {
        if (request != null && request.isValid()) {
            AMessengerAdapter messenger = CommandManager.getInstance().getMessengerAdapter();
            ImageMessage imgMessage = new ImageMessage();
            imgMessage.mClient = request.client;
            imgMessage.setImagePath(path);
            messenger.sendMessage(imgMessage, callback);
        }
    }

    /**
     * 发送视频Response
     */
    public void sendVideoCallResponse(CmdRequest request, MessageSendCallback callback) {
    }

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
