package com.penghaonan.homemonitor.server;

import android.app.Application;
import android.content.Context;
import com.penghaonan.homemonitor.server.manager.CommandManager;

/**
 * Created by carl on 2/28/16.
 */
public class App extends Application {
    private final static String TAG = App.class.getSimpleName();
    private static App sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        CommandManager.getInstance().getMessengerAdapter().onAppStart();
    }

    public static Context getContext(){
        return sInstance;
    }



}
