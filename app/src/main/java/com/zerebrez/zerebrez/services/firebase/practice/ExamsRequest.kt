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
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.models.QuestionNewFormat
import com.zerebrez.zerebrez.models.School
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.services.firebase.Engagement
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import java.util.*

private const val TAG: String = "ExamsRequest"

class ExamsRequest(activity: Activity) : Engagement(activity) {

    private val FREE_EXAMS_REFERENCE : String = "freeUser/comipems/exams"
    private val EXAMS_REFERENCE : String = "exams/comipems"
    private val USERS_REFERENCE : String = "users"
    private val PROFILE_REFERENCE : String = "profile"
    private val ANSWERED_EXAMS_REFERENCE : String = "answeredExams"
    private val COURSE_KEY : String = "course"
    private val PROFILE_KEY : String = "profile"

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

    fun requestGetFreeExamsRefactor() {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(FREE_EXAMS_REFERENCE)
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val list = (post as List<String>)

                    Log.d(TAG, post.toString())

                    val mFreeExamList = arrayListOf<Exam>()

                    for (m in list) {
                        val exam = Exam()
                        exam.setExamId(Integer(m.replace("e", "")))
                        mFreeExamList.add(exam)
                    }

                    onRequestListenerSucces.onSuccess(mFreeExamList)
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

    fun requestGetExamsRefactor() {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(EXAMS_REFERENCE)
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as HashMap<String, HashMap<Any, Any>>)
                    val mExams = arrayListOf<Exam>()

                    Log.d(TAG, post.toString())

                    /*
                 * mapping map to module object
                 */
                    for (key in map.keys) {
                        println(key)
                        val exam = Exam()
                        val questions = arrayListOf<QuestionNewFormat>()

                        // get question id from response
                        val list = map.get(key) as List<String>
                        for (q in list) {
                            val questionNewFormat = QuestionNewFormat()
                            questionNewFormat.questionId = q
                            questions.add(questionNewFormat)
                        }

                        // set module id and question id
                        exam.setExamId(Integer(key.replace("e", "")))
                        exam.setQuestionsNewFormat(questions)

                        // add module to list
                        mExams.add(exam)
                    }

                    /*
                  * sort module list because service doesn't return it in order
                  */
                    Collections.sort(mExams, object : Comparator<Exam> {
                        override fun compare(o1: Exam, o2: Exam): Int {
                            return extractInt(o1) - extractInt(o2)
                        }

                        internal fun extractInt(s: Exam): Int {
                            val num = s.getExamId().toString()
                            // return 0 if no digits found
                            return if (num.isEmpty()) 0 else Integer.parseInt(num)
                        }
                    })

                    onRequestListenerSucces.onSuccess(mExams)
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

    fun requestGetProfileUserRefactor() {
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
                        val map = (post as HashMap<String, String>)
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

                        if (map.containsKey(ANSWERED_EXAM_KEY)) {
                            val answeredExams = (map.get(ANSWERED_EXAM_KEY) as kotlin.collections.HashMap<String, String>).get(course) as kotlin.collections.HashMap<String, String>
                            val exams = arrayListOf<Exam>()
                            for (key2 in answeredExams.keys) {
                                val examAnswered = answeredExams.get(key2) as HashMap<String, String>
                                val exam = Exam()
                                exam.setExamId(Integer(key2.replace("e", "")))

                                for (key3 in examAnswered.keys) {
                                    if (key3.equals(INCORRECT_KEY)) {
                                        val incorrectQuestions = (examAnswered.get(key3) as java.lang.Long).toInt()
                                        exam.setMisses(incorrectQuestions)
                                    } else if (key3.equals(CORRECT_KEY)) {
                                        val correctQuestions = (examAnswered.get(key3) as java.lang.Long).toInt()
                                        exam.setHits(correctQuestions)
                                    }
                                }

                                exams.add(exam)
                            }
                            user.setAnsweredExams(exams)
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