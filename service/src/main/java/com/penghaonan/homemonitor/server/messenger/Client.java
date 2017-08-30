package com.penghaonan.homemonitor.server.messenger;

/**
 * Created by carl on 2/27/16.
 */
public class Client {
    String mUserName;

    public Client(String username){
        mUserName = username;
    }

    public String getUserName(){
        return mUserName;
    }
}
