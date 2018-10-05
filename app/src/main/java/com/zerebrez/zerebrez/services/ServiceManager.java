package com.zerebrez.zerebrez.services;

import android.content.Context;

import com.zerebrez.zerebrez.models.User;

public class ServiceManager {

    private static ServiceManager instance;

    private User mUser;

    private static synchronized ServiceManager getInstance(Context context) {
        if (instance == null) {
            instance = new ServiceManager(context);
        }
        return instance;
    }

    /**
     * Constructor of ServiceManager
     *
     * @param context Context
     */
    private ServiceManager(Context context) {

    }

    public void setUser(User user) {
        this.mUser = user;
    }

    public User getUser() {
        return this.mUser;
    }


}