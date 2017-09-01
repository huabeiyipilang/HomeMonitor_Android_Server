package com.penghaonan.homemonitor.connectionservice.messenger.easemob;

import android.hardware.Camera;
import android.util.Log;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.hyphenate.exceptions.HyphenateException;
import com.penghaonan.appframework.AppDelegate;
import com.penghaonan.appframework.utils.CommonUtils;
import com.penghaonan.appframework.utils.Logger;
import com.penghaonan.homemonitor.connectionservice.messenger.AMessage;
import com.penghaonan.homemonitor.connectionservice.messenger.AMessengerAdapter;
import com.penghaonan.homemonitor.connectionservice.messenger.Client;

import java.util.List;

import static com.penghaonan.homemonitor.connectionservice.messenger.AMessengerAdapter.MessageSendCallback.STATE_SEND_FAILED;

/**
 * 环信
 * Created by carl on 2/29/16.
 */
public class EasemobMessengerAdapter extends AMessengerAdapter {
    private final static String TAG = EasemobMessengerAdapter.class.getSimpleName();

    @Override
    public void onAppStart() {
        if (!CommonUtils.isMainProcess()) {
            return;
        }
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(true);
        EMClient.getInstance().init(AppDelegate.getApp(), options);
        EMClient.getInstance().setDebugMode(AppDelegate.isDebug());
        Log.i(TAG, "EMChat init！");

        //TODO
//        Intent intent = new Intent(AppDelegate.getApp(), LoginActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        AppDelegate.startActivity(intent);
    }

    public void onLogin() {
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                if (mMessageListener != null) {
                    for (EMMessage message : messages) {
                        AMessage msg = MessageConvert.convert(message);
                        mMessageListener.onMessageReceive(msg);
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {

            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }
        });
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {
                notifyMessengerStateChanged(true);
            }

            @Override
            public void onDisconnected(int errorCode) {
                notifyMessengerStateChanged(false);
            }
        });
    }

    @Override
    public void sendMessage(AMessage msg, final MessageSendCallback callback) {
        EMMessage message = MessageConvert.convert(msg);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    @Override
    public void sendVideoCall(Client client, MessageSendCallback callback) {
        super.sendVideoCall(client, callback);
        try {
            try {
                EMClient.getInstance().callManager().setCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK);
            } catch (HyphenateException e) {
                Logger.e(e);
            }
            EMClient.getInstance().callManager().makeVideoCall(client.getUserName());
        } catch (EMServiceNotReadyException e) {
            Logger.e(e);
            if (callback != null) {
//                callback.onStateChanged(STATE_SEND_FAILED, AppDelegate.getString(R.string.video_call_send_call_failed));
            }
        }
    }

    @Override
    public String getServerId() {
        return EMClient.getInstance().getCurrentUser();
    }
}
