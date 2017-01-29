package com.penghaonan.homemonitor.server.messenger;

import android.text.TextUtils;

import java.util.StringTokenizer;

/**
 * Created by carl on 2/27/16.
 */
public class TextMessage extends AMessage {
    private String mMessage;

    public TextMessage(String msg){
        mMessage = msg;
    }

    /**
     * 获取所有信息
     * @return
     */
    public String getMessage(){
        return mMessage;
    }

    /**
     * 获取命令
     * @return
     */
    public String getCommand(){
        if (TextUtils.isEmpty(mMessage)){
            return null;
        }
        return new StringTokenizer(mMessage).nextToken();
    }
}
