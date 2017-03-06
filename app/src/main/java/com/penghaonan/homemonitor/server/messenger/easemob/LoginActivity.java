package com.penghaonan.homemonitor.server.messenger.easemob;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.penghaonan.appframework.AppDelegate;
import com.penghaonan.appframework.utils.GlobalConfig;
import com.penghaonan.appframework.utils.Logger;
import com.penghaonan.appframework.utils.ToastUtils;
import com.penghaonan.homemonitor.server.App;
import com.penghaonan.homemonitor.server.MainActivity;
import com.penghaonan.homemonitor.server.R;
import com.penghaonan.homemonitor.server.base.BaseActivity;
import com.penghaonan.homemonitor.server.manager.CommandManager;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 自动注册登陆界面
 * Created by carl on 3/8/16.
 */
public class LoginActivity extends BaseActivity {

    private final static String PREF_KEY = "easemob_id";

    @OnClick(R.id.tv_tips)
    void onRetryClick() {
        init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAccount() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                final boolean res = tryCreateAccount(2);
                AppDelegate.post(new Runnable() {
                    @Override
                    public void run() {
                        if (res) {
                            login();
                        } else {
                            ToastUtils.showToast("Create account failed!");
                        }
                    }
                });
            }
        }.start();
    }

    private boolean tryCreateAccount(int count) {
        if (count < 0) {
            return false;
        } else {
            long time = System.currentTimeMillis();
            String sid = Long.toHexString(time);
            try {
                EMClient.getInstance().createAccount(sid, sid);
                GlobalConfig.put(PREF_KEY, time);
                return true;
            } catch (HyphenateException e) {
                Logger.e(e);
                return tryCreateAccount(--count);
            }
        }
    }

    private void init() {
        String sid = getServerId();
        if (TextUtils.isEmpty(sid)) {
            createAccount();
        } else {
            login();
        }
    }

    private void login() {
        final String sid = getServerId();
        if (TextUtils.isEmpty(sid)) {
            return;
        }
        EMClient.getInstance().login(sid, sid, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                showToast("登陆成功:" + sid);

                EasemobMessengerAdapter adapter = (EasemobMessengerAdapter) CommandManager.getInstance().getMessengerAdapter();
                adapter.onLogin();
                finish();
                Logger.i("Login success:" + sid);
                AppDelegate.startActivity(MainActivity.class, null);
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                showToast("登陆失败");
                Logger.i("Login failed:" + code + ", " + message);
            }
        });
    }

    private String getServerId() {
        long sidl = GlobalConfig.getLong(PREF_KEY, 0);
        if (sidl == 0) {
            return null;
        } else {
            return Long.toHexString(sidl);
        }
    }
}
