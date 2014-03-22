package com.cameracornet.outsidegpl.swiftp;

import android.app.Application;

import be.ppareit.swiftp.FsApp;

/**
 * Created by adminsag on 3/22/14.
 */
public class ExtendsApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FsApp.setContext(getApplicationContext());
    }
}
