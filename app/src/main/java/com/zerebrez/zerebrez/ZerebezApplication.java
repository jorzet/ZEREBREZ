package com.zerebrez.zerebrez;

import android.support.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;

public class ZerebezApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
