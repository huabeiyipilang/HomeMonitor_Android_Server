package com.penghaonan.homemonitor.server.command;

/**
 * Created by carl on 3/2/16.
 */
public class TakePic extends ACommand {
    private final static String CMD = "takepic";

    public static boolean match(String cmdStr) {
        boolean res = CMD.equals(cmdStr);
        return res;
    }

    @Override
    public boolean isCommandValid() {
        if (mMessage == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isSupport() {
        return true;
    }

    @Override
    public void execute() {

    }
}
