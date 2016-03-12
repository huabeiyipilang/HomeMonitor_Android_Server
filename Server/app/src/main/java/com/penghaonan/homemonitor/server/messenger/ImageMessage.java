package com.penghaonan.homemonitor.server.messenger;

/**
 * Created by carl on 3/11/16.
 */
public class ImageMessage extends AMessage {
    private String mImagePath;

    public void setImagePath(String path){
        mImagePath = path;
    }

    public String getImagePath(){
        return mImagePath;
    }
}
