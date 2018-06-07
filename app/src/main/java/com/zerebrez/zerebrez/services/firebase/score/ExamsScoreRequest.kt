package com.zerebrez.zerebrez.services.firebase.score

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.zerebrez.zerebrez.models.ExamScore
import com.zerebrez.zerebrez.models.UserScoreExam
import com.zerebrez.zerebrez.services.firebase.Engagement
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager

private const val TAG: String = "ExamsScoreRequest"

class ExamsScoreRequest(activity: Activity) : Engagement(activity) {

    private val EXAM_SCORES_REFERENCE : String = "scores/exams/comipems"

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

}