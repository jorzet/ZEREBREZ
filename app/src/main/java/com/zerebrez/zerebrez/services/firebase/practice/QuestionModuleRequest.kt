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

package com.zerebrez.zerebrez.services.firebase.practice

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.Module
import com.zerebrez.zerebrez.models.QuestionNewFormat
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.services.firebase.Engagement
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import java.util.*
import kotlin.collections.HashMap

private const val TAG: String = "QuestionModuleRequest"

class QuestionModuleRequest(activity: Activity) : Engagement(activity) {

    private val COURSE_LABEL : String = "course_label"
    private val FREE_MODULES_REFERENCE : String = "freeUser/course_label/modules"
    private val MODULES_REFERENCE : String = "modules/course_label"
    private val USERS_REFERENCE : String = "users"
    private val PROFILE_REFERENCE : String = "profile"
    private val ANSWERED_MODULED_REFERENCE : String = "answeredModules"

    private val IS_PREMIUM_KEY : String = "isPremium"
    private val TIMESTAMP_KEY : String = "timeStamp"
    private val PREMIUM_KEY : String = "premium"
    private val COURSE_KEY : String = "course"
    private val PROFILE_KEY : String = "profile"

    private val mActivity : Activity = activity
    private lateinit var mFirebaseDatabase: DatabaseReference
    private var mFirebaseInstance: FirebaseDatabase

    init {
        mFirebaseInstance = FirebaseDatabase.getInstance()
        //if (!SharedPreferencesManager(mActivity).isPersistanceData()) {
        //    mFirebaseInstance.setPersistenceEnabled(true)
        //    SharedPreferencesManager(mActivity).setPersistanceDataEnable(true)
        //}
    }

    fun requestGetFreeModulesRefactor(course: String) {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(FREE_MODULES_REFERENCE.replace(COURSE_LABEL, course))
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val list = (post as List<String>)

                    Log.d(TAG, post.toString())

                    val mFreeModuleList = arrayListOf<Module>()

                    for (m in list) {
                        val module = Module()
                        module.setId(Integer(m.replace("m", "")))
                        mFreeModuleList.add(module)
                    }

                    onRequestListenerSucces.onSuccess(mFreeModuleList)
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

    fun requestGetModulesRefactor(course: String) {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(MODULES_REFERENCE.replace(COURSE_LABEL, course))
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as HashMap<String, String>)
                    val mModules = arrayListOf<Module>()

                    Log.d(TAG, post.toString())

                    /*
                 * mapping map to module object
                 */
                    for (key in map.keys) {
                        println(key)
                        val module = Module()
                        val questions = arrayListOf<QuestionNewFormat>()

                        // get question id from response
                        val list = map.get(key) as List<String>
                        for (q in list) {
                            try {
                                val question = QuestionNewFormat()
                                question.questionId = q
                                //question.setModuleId(Integer(key.replace("m", "")))
                                questions.add(question)
                            } catch (exception: Exception) {
                            }
                        }

                        // set module id and question id
                        module.setId(Integer(key.replace("m", "")))
                        module.setQuestionsNewFormat(questions)

                        // add module to list
                        mModules.add(module)
                    }

                    /*
                  * sort module list because service doesn't return it in order
                  */
                    Collections.sort(mModules, object : Comparator<Module> {
                        override fun compare(o1: Module, o2: Module): Int {
                            return extractInt(o1) - extractInt(o2)
                        }

                        internal fun extractInt(s: Module): Int {
                            val num = s.getId().toString()
                            // return 0 if no digits found
                            return if (num.isEmpty()) 0 else Integer.parseInt(num)
                        }
                    })

                    onRequestListenerSucces.onSuccess(mModules)
                } else {
                    val error = GenericError()
                    onRequestLietenerFailed.onFailed(error)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                //onRequestLietenerFailed.onFailed(databaseError.toException())
            }
        })
    }

    fun requestGetProfileUserRefactor(course: String) {
        // Get a reference to our posts
        val firebaseUser = getCurrentUser()
        if (firebaseUser != null) {
            mFirebaseDatabase = mFirebaseInstance.getReference(USERS_REFERENCE + "/" + firebaseUser.uid)
            mFirebaseDatabase.keepSynced(true)

            // Attach a listener to read the data at our posts reference
            mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val post = dataSnapshot.getValue()
                    if (post != null) {
                        val map = post as HashMap<*, *>
                        Log.d(TAG, "user data ------ " + map.size)

                        var course = ""
                        val user = User()
                        if (map.containsKey(PROFILE_KEY)) {

                            val profileMap = map.get(PROFILE_KEY) as kotlin.collections.HashMap<String, String>

                            course = profileMap.get(COURSE_KEY) as String

                            user.setCourse(course)
                            val courseMap = profileMap.get(course) as kotlin.collections.HashMap<*, *>

                            if (courseMap.containsKey(PREMIUM_KEY)) {
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

                        if (map.containsKey(ANSWERED_MODULED_REFERENCE)) {
                                val answeredModules = (map.get(ANSWERED_MODULED_REFERENCE) as kotlin.collections.HashMap<String, String>).get(course) as kotlin.collections.HashMap<String, String>
                                val modules = arrayListOf<Module>()

                                for (key2 in answeredModules.keys) {
                                    val moduleAnswered = answeredModules.get(key2) as HashMap<String, String>
                                    val module = Module()
                                    module.setId(Integer(key2.replace("m", "")))

                                    for (key3 in moduleAnswered.keys) {
                                        if (key3.equals("incorrect")) {
                                            val incorrectQuestions = (moduleAnswered.get(key3) as java.lang.Long).toInt()
                                            module.setIncorrectQuestions(incorrectQuestions)
                                        } else if (key3.equals("correct")) {
                                            val correctQuestions = (moduleAnswered.get(key3) as java.lang.Long).toInt()
                                            module.setCorrectQuestions(correctQuestions)
                                        }
                                    }

                                    modules.add(module)
                                }
                                user.setAnsweredModules(modules)

                        }
                        Log.d(TAG, "user data ------ " + user.getUUID())
                        onRequestListenerSucces.onSuccess(user)
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
}