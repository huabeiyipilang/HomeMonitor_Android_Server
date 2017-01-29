package com.penghaonan.homemonitor.server.messenger.easemob;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.penghaonan.homemonitor.server.App;
import com.penghaonan.homemonitor.server.R;
import com.penghaonan.homemonitor.server.manager.CommandManager;

/**
 * Created by carl on 3/8/16.
 */
public class LoginActivity extends AppCompatActivity {
    private final static String TAG = LoginActivity.class.getSimpleName();
    private EditText mAppKeyView, mUserNameView, mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAppKeyView = (EditText) findViewById(R.id.et_appkey);
        mUserNameView = (EditText) findViewById(R.id.et_username);
        mPasswordView = (EditText) findViewById(R.id.et_password);

        mAppKeyView.setText("carl#homemonitor");
        mUserNameView.setText("home_server1");
        mPasswordView.setText("wowangle");

        findViewById(R.id.bt_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        login();
    }

    private void login() {
        String appKey = mAppKeyView.getText().toString();
        String userName = mUserNameView.getText().toString();
        String password = mPasswordView.getText().toString();

        EMChat.getInstance().setAppkey(appKey);
        EMChatManager.getInstance().login(userName, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
                showToast("登陆成功");

                EasemobMessengerAdapter adapter = (EasemobMessengerAdapter) CommandManager.getInstance().getMessengerAdapter();
                adapter.onLogin();
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                showToast("登陆失败");
            }
        });

    }

    private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
