package com.penghaonan.homemonitor.server;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.penghaonan.homemonitor.server.command.Torch;
import com.penghaonan.homemonitor.server.manager.CommandManager;
import com.penghaonan.homemonitor.server.messenger.AMessage;
import com.penghaonan.homemonitor.server.messenger.Client;
import com.penghaonan.homemonitor.server.messenger.TextMessage;
import com.penghaonan.homemonitor.server.messenger.local.LocalMessengerAdapter;


public class MainActivity extends AppCompatActivity implements LocalMessengerAdapter.LocalMessageListener {

    private EditText mCmdInputView;
    private TextView mOutputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCmdInputView = (EditText)findViewById(R.id.cmd_input);
        mOutputView = (TextView)findViewById(R.id.tv_output);
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
