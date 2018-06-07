package com.zerebrez.zerebrez.services.firebase.advances

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.models.Module
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.services.firebase.Engagement
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import java.util.HashMap

private const val TAG: String = "AdvancesRequest"

class AdvancesRequest(activity: Activity) : Engagement(activity) {

    private val USERS_REFERENCE : String = "users"
    private val PROFILE_REFERENCE : String = "profile"
    private val ANSWERED_QUESTION_REFERENCE : String = "answeredQuestions"

    private val IS_PREMIUM_KEY : String = "isPremium"
    private val TIMESTAMP_KEY : String = "timeStamp"
    private val PREMIUM_KEY : String = "premium"
    private val IS_CORRECT_KEY : String = "isCorrect"
    private val INCORRECT_KEY : String = "incorrect"
    private val SUBJECT_KEY : String = "subject"
    private val CHOOSEN_OPTION_KEY : String = "chosenOption"
    private val ANSWERED_EXAM_KEY : String = "answeredExams"
    private val ANSWERED_MODULE_KEY : String = "answeredModules"
    private val CORRECT_KEY : String = "correct"

    private val mActivity : Activity = activity
    private lateinit var mFirebaseDatabase: DatabaseReference
    private var mFirebaseInstance: FirebaseDatabase

    init {
        mFirebaseInstance = FirebaseDatabase.getInstance()
        if (!SharedPreferencesManager(mActivity).isPersistanceData()) {
            mFirebaseInstance.setPersistenceEnabled(true)
            SharedPreferencesManager(mActivity).setPersistanceDataEnable(true)
        }
    }


    fun requestGetHitAndMissesAnsweredModulesAndExams() {
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
                    Log.d(TAG, "user data ------ " + map.size)

                    val user = User()
                    for ( key in map.keys) {
                        println(key)
                        if (key.equals(PROFILE_REFERENCE)) {
                            val profile = map.get(key) as HashMap<String, String>
                            val premiumHash = profile.get(PREMIUM_KEY) as HashMap<String, String>
                            for (key4 in profile.keys) {
                                if (key4.equals(IS_PREMIUM_KEY)) {
                                    val isPremium = premiumHash.get(key4) as Boolean
                                    user.setPremiumUser(isPremium)
                                } else if (key4.equals(TIMESTAMP_KEY)) {
                                    val timeStamp = premiumHash.get(key4) as String
                                    user.setTimeStamp(timeStamp)
                                }
                            }

                        }  else if (key.equals(ANSWERED_MODULE_KEY)) {
                            val answeredModules = map.get(key) as HashMap<String, String>
                            val modules = arrayListOf<Module>()

                            for (key2 in answeredModules.keys) {
                                val moduleAnswered = answeredModules.get(key2) as HashMap<String, String>
                                val module = Module()
                                module.setId(Integer(key2.replace("m","")))

                                for (key3 in moduleAnswered.keys) {
                                    if (key3.equals(INCORRECT_KEY)) {
                                        val incorrectQuestions = (moduleAnswered.get(key3) as java.lang.Long).toInt()
                                        module.setIncorrectQuestions(incorrectQuestions)
                                    } else if (key3.equals(CORRECT_KEY)) {
                                        val correctQuestions = (moduleAnswered.get(key3) as java.lang.Long).toInt()
                                        module.setCorrectQuestions(correctQuestions)
                                    }
                                }

                                modules.add(module)
                            }
                            user.setAnsweredModules(modules)
                        } else if (key.equals(ANSWERED_EXAM_KEY)) {
                            val answeredExams = map.get(key) as HashMap<String, String>
                            val exams = arrayListOf<Exam>()
                            for (key2 in answeredExams.keys) {
                                val examAnswered = answeredExams.get(key2) as HashMap<String, String>
                                val exam = Exam()
                                exam.setExamId(Integer(key2.replace("e","")))

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
                    }
                    Log.d(TAG, "user data ------ " + user.getUUID())
                    onRequestListenerSucces.onSuccess(user)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("The read failed: " + databaseError.code)
                    onRequestLietenerFailed.onFailed(databaseError.toException())
                }
            })
        }
    }

}