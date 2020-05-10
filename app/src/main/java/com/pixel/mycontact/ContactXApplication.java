package com.pixel.mycontact;

import android.app.Application;
import android.content.Context;

import io.realm.Realm;

/**
 * @author Carl Su
 * @date 2020/5/10
 */
public class ContactXApplication  extends Application {
    private static Realm realmInstance;
    private static Context appContext;

    public static Realm getRealmInstance() {
        return realmInstance;
    }

    public static Context getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        Realm.init(appContext);
        realmInstance = Realm.getDefaultInstance();
    }
}
