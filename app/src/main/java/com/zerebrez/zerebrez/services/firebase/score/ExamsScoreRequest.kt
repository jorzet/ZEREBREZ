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

package com.zerebrez.zerebrez.services.firebase.score

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.google.gson.Gson
import com.zerebrez.zerebrez.models.*
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.services.firebase.Engagement
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

private const val TAG: String = "ExamsScoreRequest"

class ExamsScoreRequest(activity: Activity) : Engagement(activity) {

    private val COURSE_LABEL : String = "course_label"
    private val EXAM_SCORES_REFERENCE : String = "scores/exams/course_label/processedData"
    private val USERS_REFERENCE : String = "users"
    private val PROFILE_REFERENCE : String = "profile"

    private val IS_PREMIUM_KEY : String = "isPremium"
    private val TIMESTAMP_KEY : String = "timeStamp"
    private val PREMIUM_KEY : String = "premium"
    private val ANSWERED_EXAM_KEY : String = "answeredExams"
    private val CORRECT_KEY : String = "correct"
    private val INCORRECT_KEY : String = "incorrect"

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

    fun requestGetExamScores(course: String) {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(EXAM_SCORES_REFERENCE.replace(COURSE_LABEL, course))
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as HashMap<*, *>)
                    Log.d(TAG, "user data ------ " + map.size)
                    val examScores = ArrayList<ExamScoreRafactor>()
                    for (key in map.keys) {
                        val examScoreMap = map.get(key) as HashMap<*, *>
                        val examScore = Gson().fromJson(JSONObject(examScoreMap).toString(), ExamScoreRafactor::class.java)
                        examScore.examId = key.toString()
                        examScores.add(examScore)
                    }

                    onRequestListenerSucces.onSuccess(examScores)
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

    fun requestGetAnsweredExamRefactor(course: String) {
        // Get a reference to our posts
        val user = getCurrentUser()
        if (user != null) {
            mFirebaseDatabase = mFirebaseInstance.getReference(USERS_REFERENCE + "/" + user.uid)
            mFirebaseDatabase.keepSynced(true)

            // Attach a listener to read the data at our posts reference
            mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val post = dataSnapshot.getValue()
                    if (post != null) {
                        val map = (post as kotlin.collections.HashMap<*, *>)
                        Log.d(TAG, "user data ------ " + map.size)

                        val user = User()
                        for (key in map.keys) {
                            println(key)
                            if (key.equals(PROFILE_REFERENCE)) {
                                val profile = map.get(key) as kotlin.collections.HashMap<String, String>
                                if (profile.containsKey(PREMIUM_KEY)) {
                                    val premiumHash = profile.get(PREMIUM_KEY) as kotlin.collections.HashMap<String, String>

                                    if (premiumHash.containsKey(IS_PREMIUM_KEY)) {
                                        val isPremium = premiumHash.get(IS_PREMIUM_KEY) as Boolean
                                        user.setPremiumUser(isPremium)
                                    }

                                    if (premiumHash.containsKey(TIMESTAMP_KEY)) {
                                        val timeStamp = premiumHash.get(TIMESTAMP_KEY) as Long
                                        user.setTimeStamp(timeStamp)
                                    }
                                }

                            } else if (key.equals(ANSWERED_EXAM_KEY)) {
                                val answeredExams = (map.get(key) as kotlin.collections.HashMap<*, *>).get(course) as kotlin.collections.HashMap<String, String>
                                val exams = arrayListOf<Exam>()
                                for (key2 in answeredExams.keys) {
                                    val examAnswered = answeredExams.get(key2) as java.util.HashMap<String, String>
                                    val exam = Exam()
                                    exam.setExamId(Integer(key2.replace("e", "")))

                                    for (key3 in examAnswered.keys) {
                                        if (key3.equals(INCORRECT_KEY)) {
                                            val incorrectQuestions = (examAnswered.get(key3) as kotlin.Long).toInt()
                                            exam.setMisses(incorrectQuestions)
                                        } else if (key3.equals(CORRECT_KEY)) {
                                            val correctQuestions = (examAnswered.get(key3) as kotlin.Long).toInt()
                                            exam.setHits(correctQuestions)
                                        }
                                    }

                                    exams.add(exam)
                                }
                                user.setAnsweredExams(exams)
                            }
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