package com.penghaonan.homemonitor.connectionservice.messenger.local;

import android.util.Log;

import com.penghaonan.homemonitor.connectionservice.messenger.AMessage;
import com.penghaonan.homemonitor.connectionservice.messenger.AMessengerAdapter;
import com.penghaonan.homemonitor.connectionservice.messenger.TextMessage;

/**
 * Created by carl on 2/27/16.
 */
public class LocalMessengerAdapter extends AMessengerAdapter{
    private final static String TAG = LocalMessengerAdapter.class.getSimpleName();
    private LocalMessageListener mListener;
    public interface LocalMessageListener{
        void onMessageReceived(String msg);
    }

    public void setLocalMessageListener(LocalMessageListener listener){
        mListener = listener;
    }

    @Override
    public void onAppStart() {

    }

    @Override
    public void sendMessage(AMessage message, MessageSendCallback callback) {
        if (message instanceof TextMessage){
            String msg = ((TextMessage) message).getMessage();
            Log.i(TAG, msg);
            if (mListener != null){
                mListener.onMessageReceived(msg);
            }
            if (callback != null){
                callback.onStateChanged(MessageSendCallback.STATE_SEND_SUCCESS, null);
            }
        }
    }
}
