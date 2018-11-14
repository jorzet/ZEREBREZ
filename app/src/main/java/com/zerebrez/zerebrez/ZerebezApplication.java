/*
 * Copyright [2018] [Jorge Zepeda Tinoco]
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

package com.zerebrez.zerebrez;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.zerebrez.zerebrez.services.firebase.Engagement;

/**
 * Created by Jorge Zepeda Tinoco on 03/06/18.
 * jorzet.94@gmail.com
 */

public class ZerebezApplication extends MultiDexApplication {

    private static final String TAG = "ZerebezApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FirebaseDatabase
                    .getInstance(Engagement.Companion.getQUESTIONS_DATABASE_REFERENCE())
                    .setPersistenceEnabled(true);
            FirebaseDatabase
                    .getInstance(Engagement.Companion.getUSERS_DATABASE_REFERENCE())
                    .setPersistenceEnabled(true);
            FirebaseDatabase
                    .getInstance(Engagement.Companion.getSETTINGS_DATABASE_REFERENCE())
                    .setPersistenceEnabled(true);
        } catch (Exception e) {
            Log.d(TAG, "cannot set persistence database");
        }
    }

}
