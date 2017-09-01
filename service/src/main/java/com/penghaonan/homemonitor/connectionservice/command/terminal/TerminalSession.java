/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.penghaonan.homemonitor.connectionservice.command.terminal;

import android.os.Environment;

import com.penghaonan.appframework.utils.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

class TerminalSession {
    private static final TerminalSession ourInstance = new TerminalSession();

    private File mCurrentPath = Environment.getExternalStorageDirectory();

    private TerminalSession() {
    }

    static TerminalSession getInstance() {
        return ourInstance;
    }

    public File getCurrentPath() {
        return mCurrentPath;
    }

    public List<String> ls() {
        List<String> files = new ArrayList<>();
        if (!checkCurrentPathExist()) {
            files.add("Current path not exists. " + mCurrentPath.getPath());
            return files;
        }
        if (mCurrentPath.list() == null) {
            files.add("No files!");
            return files;
        }
        String[] children = mCurrentPath.list();
        Arrays.sort(children);
        for (String item : children) {
            if (new File(mCurrentPath, item).isDirectory()) {
                files.add(formatDir(item));
            } else {
                files.add(item);
            }
        }
        return files;
    }

    public String cd(String path) {
        if ("..".equals(path)) {
            if (!checkCurrentPathExist()) {
                return "Current path not exists. " + mCurrentPath.getPath();
            }
            mCurrentPath = mCurrentPath.getParentFile();
        }else if (new File(path).exists()) {
            mCurrentPath = new File(path);
        }else if (new File(mCurrentPath, path).exists()) {
            mCurrentPath = new File(mCurrentPath, path);
        }
        return mCurrentPath.getPath();
    }

    public String pwd() {
        return mCurrentPath.getPath();
    }

    public File get(String param) {
        File file = null;
        if (new File(param).exists()) {
            file = new File(param);
        }else if (new File(mCurrentPath, param).exists()) {
            file = new File(mCurrentPath, param);
        }
        return file;
    }

    private boolean checkCurrentPathExist() {
        return mCurrentPath.exists();
    }

    private String formatDir(String item) {
        return "[" + item + "]";
    }
}
