package com.zerebrez.zerebrez.services.firebase.score

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.models.ExamScore
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.UserScoreExam
import com.zerebrez.zerebrez.services.firebase.Engagement
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager

private const val TAG: String = "ExamsScoreRequest"

class ExamsScoreRequest(activity: Activity) : Engagement(activity) {

    private val EXAM_SCORES_REFERENCE : String = "scores/exams/comipems"
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
        if (!SharedPreferencesManager(mActivity).isPersistanceData()) {
            mFirebaseInstance.setPersistenceEnabled(true)
            SharedPreferencesManager(mActivity).setPersistanceDataEnable(true)
        }
    }

    fun requestGetExamScores() {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(EXAM_SCORES_REFERENCE)
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                val map = (post as HashMap<String, HashMap<Any, Any>>)
                val mExamScores = arrayListOf<ExamScore>()

                Log.d(TAG, post.toString())

                for (key in map.keys) {
                    println(key)
                    val obj = map.get(key) as HashMap<String, Any>
                    val examScore = ExamScore()
                    examScore.setExamScoreId(Integer(key.replace("e","")))
                    val mUserScoreExams = arrayListOf<UserScoreExam>()
                    for (key2 in obj.keys) {
                        val userScoreExam = UserScoreExam()
                        userScoreExam.setUserUUDI(key2)
                        userScoreExam.setScore(Integer(obj.get(key2).toString()))
                        mUserScoreExams.add(userScoreExam)
                    }
                    examScore.setOtherUsersScoreExam(mUserScoreExams)
                    mExamScores.add(examScore)
                }

                onRequestListenerSucces.onSuccess(mExamScores)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                onRequestLietenerFailed.onFailed(databaseError.toException())
            }
        })
    }

    fun requestGetAnsweredExamRefactor() {
        // Get a reference to our posts
        val user = getCurrentUser()
        if (user != null) {
            mFirebaseDatabase = mFirebaseInstance.getReference(USERS_REFERENCE + "/" + user.uid)
            mFirebaseDatabase.keepSynced(true)

            // Attach a listener to read the data at our posts reference
            mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val post = dataSnapshot.getValue()
                    val map = (post as java.util.HashMap<String, String>)
                    Log.d(TAG, "user data ------ " + map.size)

                    val user = User()
                    for ( key in map.keys) {
                        println(key)
                        if (key.equals(PROFILE_REFERENCE)) {
                            val profile = map.get(key) as java.util.HashMap<String, String>
                            val premiumHash = profile.get(PREMIUM_KEY) as java.util.HashMap<String, String>
                            for (key4 in profile.keys) {
                                if (key4.equals(IS_PREMIUM_KEY)) {
                                    val isPremium = premiumHash.get(key4) as Boolean
                                    user.setPremiumUser(isPremium)
                                } else if (key4.equals(TIMESTAMP_KEY)) {
                                    val timeStamp = premiumHash.get(key4) as String
                                    user.setTimeStamp(timeStamp)
                                }
                            }

                        } else if (key.equals(ANSWERED_EXAM_KEY)) {
                            val answeredExams = map.get(key) as java.util.HashMap<String, String>
                            val exams = arrayListOf<Exam>()
                            for (key2 in answeredExams.keys) {
                                val examAnswered = answeredExams.get(key2) as java.util.HashMap<String, String>
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