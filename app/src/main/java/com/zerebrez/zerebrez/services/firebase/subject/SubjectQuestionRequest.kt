package com.zerebrez.zerebrez.services.firebase.subject

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.Module
import com.zerebrez.zerebrez.models.QuestionNewFormat
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.services.firebase.Engagement
import java.util.*

private const val TAG: String = "SubjectQuestionRequest"

class SubjectQuestionRequest(activity: Activity) : Engagement(activity) {

    private val COURSE_LABEL : String = "course_label"
    private val USERS_REFERENCE : String = "users"
    private val FREE_SUBJECTS_QUESTION_REFERENCE : String = "freeUser/course_label/subjects/numberOfQuestionsEnabeled"
    private val PROFILE_KEY : String = "profile"
    private val IS_PREMIUM_KEY : String = "isPremium"
    private val TIMESTAMP_KEY : String = "timeStamp"
    private val PREMIUM_KEY : String = "premium"
    private val COURSE_KEY : String = "course"
    private val ANSWERED_MODULED_REFERENCE : String = "answeredModules"

    private val mActivity: Activity = activity
    private lateinit var mFirebaseDatabase: DatabaseReference
    private var mFirebaseInstance: FirebaseDatabase

    init {
        mFirebaseInstance = FirebaseDatabase.getInstance()
    }
    fun requestGetFreeSubjectsQuestionsRefactor(course: String) {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(FREE_SUBJECTS_QUESTION_REFERENCE.replace(COURSE_LABEL, course))
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val numOfQuestions = post as Long

                    onRequestListenerSucces.onSuccess(numOfQuestions)
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