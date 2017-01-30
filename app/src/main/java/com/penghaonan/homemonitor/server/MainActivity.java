package com.penghaonan.homemonitor.server;

import android.Manifest;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.penghaonan.appframework.utils.CommonUtils;
import com.penghaonan.homemonitor.server.manager.CommandManager;
import com.penghaonan.homemonitor.server.messenger.local.LocalMessengerAdapter;

import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LocalMessengerAdapter.LocalMessageListener {

    private EditText mCmdInputView;
    private TextView mOutputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCmdInputView = (EditText) findViewById(R.id.cmd_input);
        mOutputView = (TextView) findViewById(R.id.tv_output);
        CommandManager.getInstance().requestPermissions(this);
        checkPermissions();
    }

    private void checkPermissions() {
        List<String> permissons = new LinkedList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissons.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        permissons.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissons.add(Manifest.permission.CAMERA);
        CommonUtils.checkPermission(this, permissons);
    }

    @Override
    public void onMessageReceived(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mOutputView.append(msg);
                mOutputView.append("\n");
            }
        });
    }
}
