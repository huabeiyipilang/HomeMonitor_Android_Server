package com.penghaonan.homemonitor.server.messenger.easemob;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.penghaonan.homemonitor.server.App;
import com.penghaonan.homemonitor.server.manager.CommandManager;
import com.penghaonan.homemonitor.server.messenger.AMessage;
import com.penghaonan.homemonitor.server.messenger.AMessengerAdapter;
import com.penghaonan.homemonitor.server.messenger.Client;
import com.penghaonan.homemonitor.server.messenger.ImageMessage;
import com.penghaonan.homemonitor.server.messenger.TextMessage;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by carl on 2/29/16.
 */
public class EasemobMessengerAdapter extends AMessengerAdapter {
    private final static String TAG = EasemobMessengerAdapter.class.getSimpleName();

    @Override
    public void onAppStart() {
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        if (processAppName == null || !processAppName.equalsIgnoreCase(App.getContext().getPackageName())) {
            return;
        }

        EMChat.getInstance().init(App.getContext());
        Log.i(TAG, "EMChat init！");

        Intent intent = new Intent(App.getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(intent);
    }

    void onLogin() {
        NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        App.getContext().registerReceiver(msgReceiver, intentFilter);
    }

    @Override
    public void sendMessage(AMessage msg, final MessageSendCallback callback) {
        if (msg instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) msg;
            EMConversation conversation = EMChatManager.getInstance().getConversation(msg.mClient.getUserName());
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            TextMessageBody txtBody = new TextMessageBody(textMessage.getMessage());
            message.addBody(txtBody);
            message.setReceipt(msg.mClient.getUserName());
            conversation.addMessage(message);
            EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        } else if (msg instanceof ImageMessage) {
            ImageMessage imageMessage = (ImageMessage) msg;
            EMConversation conversation = EMChatManager.getInstance().getConversation(msg.mClient.getUserName());
            EMMessage message = EMMessage.createSendMessage(EMMessage.Type.IMAGE);
            File file = new File(imageMessage.getImagePath());
            ImageMessageBody imgBody = new ImageMessageBody(file);
            message.addBody(imgBody);
            message.setReceipt(msg.mClient.getUserName());
            conversation.addMessage(message);
            EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(int i, String s) {

                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        }
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) App.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = App.getContext().getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
    }

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 注销广播
            abortBroadcast();

            // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
            String msgId = intent.getStringExtra("msgid");
            //发送方
            String username = intent.getStringExtra("from");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            EMMessage.Type type = message.getType();
            switch (type) {
                case TXT:
                    TextMessageBody body = (TextMessageBody) message.getBody();
                    String cmd = body.getMessage();
                    TextMessage tm = new TextMessage(cmd);
                    tm.mClient = new Client(message.getUserName());
                    CommandManager.getInstance().onMessageReceive(tm);
                    break;
                case IMAGE:
                    break;
                case VIDEO:
                    break;
                case LOCATION:
                    break;
                case VOICE:
                    break;
                case FILE:
                    break;
                case CMD:
                    break;
            }
        }
    }
}
