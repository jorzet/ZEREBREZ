/*
 * Copyright [2019] [Jorge Zepeda Tinoco]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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