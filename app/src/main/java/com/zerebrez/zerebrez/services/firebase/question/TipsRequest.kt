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

package com.zerebrez.zerebrez.services.firebase.question

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.services.firebase.Engagement

/**
 * Created by Jorge Zepeda Tinoco on 03/06/18.
 * jorzet.94@gmail.com
 */

private const val TAG: String = "TipsRequest"

class TipsRequest(activity: Activity) : Engagement(activity) {

    /*
     * Labels to be replaced
     */
    private val COURSE_LABEL : String = "course_label"
    private val TIPS_REFERENCE : String = "tips/course_label"

    /*
     * Json keys
     */
    private val PROFILE_KEY : String = "profile"
    private val COURSE_KEY : String = "course"
    private val IS_PREMIUM_KEY : String = "isPremium"
    private val TIMESTAMP_KEY : String = "timeStamp"
    private val PREMIUM_KEY : String = "premium"

    /*
     * Database object
     */
    private lateinit var mFirebaseDatabase: DatabaseReference

    fun requestGetUserTips() {
        // Get a reference to our posts
        val user = getCurrentUser()
        if (user != null) {
            mFirebaseDatabase = FirebaseDatabase
                    .getInstance(Engagement.USERS_DATABASE_REFERENCE)
                    .getReference(user.uid)

            mFirebaseDatabase.keepSynced(true)

            // Attach a listener to read the data at our posts reference
            mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val post = dataSnapshot.getValue()
                    if (post != null) {
                        val map = (post as HashMap<String, String>)
                        Log.d(TAG, "profile data ------ " + map.size)

                        if (map.containsKey(PROFILE_KEY)) {
                            val user = User()
                            val profileMap = map.get(PROFILE_KEY) as kotlin.collections.HashMap<String, String>

                            if (profileMap.containsKey(COURSE_KEY)) {
                                val course = profileMap.get(COURSE_KEY) as String

                                user.setCourse(course)
                                val courseMap = profileMap.get(course) as kotlin.collections.HashMap<*, *>

                                for (key2 in courseMap.keys) {
                                    if (key2.equals(PREMIUM_KEY)) {

                                        val premiumHash = courseMap.get(PREMIUM_KEY) as kotlin.collections.HashMap<String, String>

                                        if (premiumHash.containsKey(IS_PREMIUM_KEY)) {
                                            val isPremium = premiumHash.get(IS_PREMIUM_KEY) as Boolean
                                            user.setPremiumUser(isPremium)
                                        }

                                        if (premiumHash.containsKey(TIMESTAMP_KEY)) {
                                            val timeStamp = premiumHash.get(TIMESTAMP_KEY) as Long
                                            user.setTimeStamp(timeStamp)
                                        }
                                    }
                                }


                                Log.d(TAG, "profile data ------ " + user.getUUID())
                                onRequestListenerSucces.onSuccess(user)
                            } else {
                                val error = GenericError()
                                onRequestLietenerFailed.onFailed(error)
                            }

                        } else {
                            val error = GenericError()
                            onRequestLietenerFailed.onFailed(error)
                        }
                    } else {
                        val error = GenericError()
                        onRequestLietenerFailed.onFailed(error)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("The read failed: " + databaseError.code)
                    onRequestLietenerFailed.onFailed(databaseError.toException())
                }
            })
        }
    }

    fun requestGetTips(course : String) {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.SETTINGS_DATABASE_REFERENCE)
                .getReference(TIPS_REFERENCE.replace(COURSE_LABEL, course))

        mFirebaseDatabase.keepSynced(true)

        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                val tips = (post as List<String>)

                onRequestListenerSucces.onSuccess(tips)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                onRequestLietenerFailed.onFailed(databaseError.toException())
            }
        })
    }
}