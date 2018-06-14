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

package com.zerebrez.zerebrez.services.firebase.profile

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.zerebrez.zerebrez.models.*
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.services.firebase.Engagement
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager

private const val TAG: String = "ProfileRequest"

class ProfileRequest(activity: Activity) : Engagement(activity) {

    private val USERS_REFERENCE : String = "users"
    private val INSTITUTES_REFERENCE : String = "schools/comipems"

    private val PROFILE_KEY : String = "profile"
    private val IS_PREMIUM_KEY : String = "isPremium"
    private val TIMESTAMP_KEY : String = "timeStamp"
    private val PREMIUM_KEY : String = "premium"
    private val COURSE_KEY : String = "course"
    private val SELECTED_SCHOOLS_KEY : String = "selectedSchools"
    private val INSTITUTE_ID_KEY : String = "institutionId"
    private val SCHOOL_ID_KEY : String = "schoolId"

    private val INSTITUTE_TAG : String = "institute"
    private val SCHOOL_TAG : String = "school"

    private val mActivity : Activity = activity
    private lateinit var mFirebaseDatabase: DatabaseReference
    private var mFirebaseInstance: FirebaseDatabase

    private lateinit var mUserSchools : List<School>
    private var mSchools = arrayListOf<School>()
    private var mCurrentSchool : Int = 0
    private var mUserSchoolsSize : Int = 0

    init {
        mFirebaseInstance = FirebaseDatabase.getInstance()
        if (!SharedPreferencesManager(mActivity).isPersistanceData()) {
            mFirebaseInstance.setPersistenceEnabled(true)
            SharedPreferencesManager(mActivity).setPersistanceDataEnable(true)
        }
    }

    fun requestGetProfileRefactor() {
        // Get a reference to our posts
        val user = getCurrentUser()
        if (user != null) {
            mFirebaseDatabase = mFirebaseInstance.getReference(USERS_REFERENCE + "/" + user.uid)
            mFirebaseDatabase.keepSynced(true)

            // Attach a listener to read the data at our posts reference
            mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val post = dataSnapshot.getValue()
                    val map = (post as HashMap<String, String>)
                    Log.d(TAG, "profile data ------ " + map.size)

                    val user = User()

                    val profile = map.get(PROFILE_KEY) as HashMap<String, String>
                    for (key2 in profile.keys) {
                        if (key2.equals(PREMIUM_KEY)) {

                            val premiumHash = profile.get(PREMIUM_KEY) as java.util.HashMap<String, String>

                            if (premiumHash.containsKey(IS_PREMIUM_KEY)) {
                                val isPremium = premiumHash.get(IS_PREMIUM_KEY) as Boolean
                                user.setPremiumUser(isPremium)
                            }

                            if (premiumHash.containsKey(TIMESTAMP_KEY)) {
                                val timeStamp = premiumHash.get(TIMESTAMP_KEY) as Long
                                user.setTimeStamp(timeStamp)
                            }

                        } else if (key2.equals(COURSE_KEY)) {
                            val course = profile.get(key2).toString()
                            user.setCourse(course)
                        } else if (key2.equals(SELECTED_SCHOOLS_KEY)) {
                            val selectedSchools = profile.get(key2) as ArrayList<Any>
                            val schools = arrayListOf<School>()
                            Log.d(TAG, "profile data ------ " + selectedSchools.size)
                            for (i in 0 .. selectedSchools.size - 1) {
                                val institute = selectedSchools.get(i) as HashMap<String ,String>
                                val school = School()
                                if (institute.containsKey(INSTITUTE_ID_KEY)) {
                                    school.setInstituteId(Integer(institute.get(INSTITUTE_ID_KEY)!!.replace(INSTITUTE_TAG,"")))
                                }

                                if (institute.containsKey(SCHOOL_ID_KEY)) {
                                    school.setSchoolId(Integer(institute.get(SCHOOL_ID_KEY)!!.replace(SCHOOL_TAG,"")))
                                }
                                schools.add(school)
                            }
                            user.setSelectedShools(schools)
                        }
                    }

                    Log.d(TAG, "profile data ------ " + user.getUUID())
                    onRequestListenerSucces.onSuccess(user)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("The read failed: " + databaseError.code)
                    onRequestLietenerFailed.onFailed(databaseError.toException())
                }
            })
        }
    }



    fun requestGetUserSchools(schools: List<School>) {
        if (schools.isNotEmpty()) {
            mUserSchoolsSize = schools.size
            mUserSchools = schools
            requestSchool(schools.get(mCurrentSchool)) // request the first school
        }
    }

    fun requestSchool(school: School) {
// Get a reference to our posts
        val ref = INSTITUTES_REFERENCE + "/institute" + school.getInstituteId().toString() + "/schoolsList/school" + school.getSchoolId()
        mFirebaseDatabase = mFirebaseInstance.getReference(ref)
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as HashMap<String, String>)

                    Log.d(TAG, post.toString())

                    mUserSchools.get(mCurrentSchool).setSchoolName(map.get("name").toString())
                    mUserSchools.get(mCurrentSchool).setHitsNumber((map.get("score") as java.lang.Long).toInt())

                    mSchools.add(mUserSchools.get(mCurrentSchool))

                    if (mCurrentSchool == (mUserSchoolsSize - 1)) {
                        onRequestListenerSucces.onSuccess(mSchools)
                    } else {
                        mCurrentSchool++
                        requestSchool(mUserSchools.get(mCurrentSchool))
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