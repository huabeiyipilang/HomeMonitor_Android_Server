/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.penghaonan.homemonitor.connectionservice.command.terminal;

import android.text.TextUtils;

import com.penghaonan.homemonitor.connectionservice.command.ACommand;
import com.penghaonan.homemonitor.connectionservice.messenger.TextMessage;

import java.io.File;
import java.util.List;

public class Terminal extends ACommand {
    private static final String ACT_CD = "cd";
    private static final String ACT_LS = "ls";
    private static final String ACT_PWD = "pwd";
    private static final String ACT_GET = "get";
    private String mAction;
    private String[] mParts;
    private TerminalSession mSession = TerminalSession.getInstance();

    @Override
    public boolean isCommandValid() {
        if (mMessage == null) {
            return false;
        }
        String message = ((TextMessage) mMessage).getMessage();
        if (TextUtils.isEmpty(message)) {
            return false;
        }

        String[] parts = message.split(" ");
        if (parts.length < 2) {
            return false;
        }

        mParts = parts;
        mAction = parts[1];

        return true;
    }

    @Override
    public boolean isSupport() {
        return true;
    }

    @Override
    public void execute() {
        if (ACT_CD.equals(mAction)) {
            String res = mSession.cd(mParts[2]);
            sendTextMessage(res);
        } else if (ACT_LS.equals(mAction)) {
            List<String> res = mSession.ls();
            StringBuilder sb = new StringBuilder();
            sb.append("Current:").append(mSession.getCurrentPath().getPath()).append("\n");
            for (String item : res) {
                sb.append(item).append("\n");
            }
            sendTextMessage(sb.toString());
        } else if (ACT_PWD.equals(mAction)) {
            sendTextMessage(mSession.pwd());
        } else if (ACT_GET.equals(mAction)) {
            File file = mSession.get(mParts[2]);
            if (file == null || !file.exists()) {
                sendTextMessage("No such file!");
            } else {
                if (file.getPath().toLowerCase().endsWith(".jpg") || file.getPath().toLowerCase().endsWith(".png")) {
                    sendTextMessage("Sendding...");
                    getMessenger().sendImageMessage(getClient(), file.getPath(), null);
                }
            }
        } else {
            sendTextMessage("No such command!");
        }
    }
}
