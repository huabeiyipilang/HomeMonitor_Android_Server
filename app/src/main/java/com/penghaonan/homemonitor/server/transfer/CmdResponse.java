package com.penghaonan.homemonitor.server.transfer;

public class CmdResponse {

    public final static int CODE_SUCCESS = 0;
    public final static int CODE_FAILED = 1;
    public final static int CODE_PROGRESS = 2;

    /**
     * 请求id
     */
    public long id;
    /**
     * 返回结果类型
     */
    public int code;
    /**
     * 消息
     */
    public String msg;
}
