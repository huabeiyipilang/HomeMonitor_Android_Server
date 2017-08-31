package com.penghaonan.homemonitor.connectionservice.command.videocall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.media.EMLocalSurfaceView;
import com.hyphenate.media.EMOppositeSurfaceView;
import com.penghaonan.appframework.AppDelegate;
import com.penghaonan.homemonitor.connectionservice.Constants;
import com.penghaonan.homemonitor.connectionservice.R;
import com.penghaonan.homemonitor.connectionservice.base.BaseActivity;
import com.penghaonan.homemonitor.connectionservice.manager.CommandManager;
import com.penghaonan.homemonitor.connectionservice.messenger.AMessengerAdapter;

public class VideoCallActivity extends BaseActivity {

    private static VideoCall sCmd;
    private EMOppositeSurfaceView mRemoteView;
    private EMLocalSurfaceView mLocalView;
    private TextView mTipsView;

    private ActionReceiver mReceiver = new ActionReceiver();

    private Runnable mTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            if (EMClient.getInstance().callManager().getCallState() != EMCallStateChangeListener.CallState.CONNECTED) {
                sendMessage(R.string.video_call_time_out);
                finishCall();
            }
        }
    };
    private EMCallStateChangeListener mCallStateChangeListener = new EMCallStateChangeListener() {
        @Override
        public void onCallStateChanged(CallState callState, CallError error) {
            switch (callState) {
                case CONNECTING: // 正在连接对方
                    showTips("正在连接");
                    break;
                case CONNECTED: // 双方已经建立连接
                    showTips("连接已建立");
                    break;

                case ACCEPTED: // 电话接通成功
                    showTips("已接通");
                    break;
                case DISCONNECTED: // 电话断了
                    showTips("通话结束");
                    sendMessage(R.string.video_call_end);
                    finishCall();
                    break;
                case NETWORK_UNSTABLE: //网络不稳定
                    if (error == CallError.ERROR_NO_DATA) {
                        //无通话数据
                    } else {
                    }
                    showTips("网络不稳定");
                    break;
                case NETWORK_NORMAL: //网络恢复正常
                    showTips("网络恢复正常");
                    break;
                default:
                    break;
            }

        }
    };

    public static void startVideoCall(VideoCall cmd) {
        sCmd = cmd;
        AppDelegate.startActivity(VideoCallActivity.class, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        mRemoteView = (EMOppositeSurfaceView) findViewById(R.id.sv_remote);
        mLocalView = (EMLocalSurfaceView) findViewById(R.id.sv_local);
        mTipsView = (TextView) findViewById(R.id.tv_tips);

        EMClient.getInstance().callManager().setSurfaceView(mLocalView, mRemoteView);
        EMClient.getInstance().callManager().addCallStateChangeListener(mCallStateChangeListener);
        CommandManager.getInstance().getMessengerAdapter().sendVideoCall(sCmd.getClient(), new AMessengerAdapter.MessageSendCallback() {
            @Override
            public void onStateChanged(int state, Object info) {
                if (state == AMessengerAdapter.MessageSendCallback.STATE_SEND_FAILED) {
                    if (info != null && info instanceof String) {
                        sendMessage((String) info);
                    }
                    finish();
                }
            }
        });
        AppDelegate.postDelayed(mTimeOutRunnable, 30 * 1000);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_RESET);
        registerReceiver(mReceiver, filter);
    }

    private void finishCall() {
        AppDelegate.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);
    }

    private void showTips(final String tips) {
        AppDelegate.post(new Runnable() {
            @Override
            public void run() {
                mTipsView.setText(tips);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().callManager().removeCallStateChangeListener(mCallStateChangeListener);
        AppDelegate.removeCallbacks(mTimeOutRunnable);
        sCmd.notifyFinished();
        sCmd = null;
        unregisterReceiver(mReceiver);
    }

    private void sendMessage(String msg) {
        if (sCmd != null) {
            CommandManager.getInstance().getMessengerAdapter().sendTextMessage(sCmd.getClient(), msg, null);
        }
    }

    private void sendMessage(int msgId) {
        if (sCmd != null) {
            CommandManager.getInstance().getMessengerAdapter().sendTextMessage(sCmd.getClient(), msgId, null);
        }
    }

    private class ActionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.ACTION_RESET.equals(intent.getAction())) {
                finish();
            }
        }
    }
}
