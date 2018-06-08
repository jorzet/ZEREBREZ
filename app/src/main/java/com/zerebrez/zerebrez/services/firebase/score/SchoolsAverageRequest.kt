package com.zerebrez.zerebrez.services.firebase.score

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.models.School
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.enums.ErrorType
import com.zerebrez.zerebrez.services.firebase.Engagement
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager

private const val TAG: String = "SchoolsAverageRequest"

class SchoolsAverageRequest(activity: Activity) : Engagement(activity) {

    private val USERS_REFERENCE : String = "users"
    private val PROFILE_REFERENCE : String = "profile"
    private val INSTITUTES_REFERENCE : String = "schools/comipems"

    private val PROFILE_KEY : String = "profile"
    private val IS_PREMIUM_KEY : String = "isPremium"
    private val TIMESTAMP_KEY : String = "timeStamp"
    private val PREMIUM_KEY : String = "premium"
    private val COURSE_KEY : String = "course"
    private val SELECTED_SCHOOLS_KEY : String = "selectedSchools"
    private val INSTITUTE_ID_KEY : String = "institutionId"
    private val SCHOOL_ID_KEY : String = "schoolId"
    private val ANSWERED_EXAM_KEY : String = "answeredExams"
    private val CORRECT_KEY : String = "correct"
    private val INCORRECT_KEY : String = "incorrect"

    private val INSTITUTE_TAG : String = "institute"
    private val SCHOOL_TAG : String = "school"

    private lateinit var mUserSchools : List<School>
    private var mSchools = arrayListOf<School>()
    private var mCurrentSchool : Int = 0
    private var mUserSchoolsSize : Int = 0
    private val EXAM_128_QUESTIONS : Int = 128

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

    fun requestGetUserSelectedSchoolsRefactor() {
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

                    if (user.getSelectedSchools().isNotEmpty()) {
                        requestGetUserSchools(user.getSelectedSchools())
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



    private fun requestGetUserSchools(schools: List<School>) {
        if (schools.isNotEmpty()) {
            mUserSchoolsSize = schools.size
            mUserSchools = schools
            requestSchool(schools.get(mCurrentSchool)) // request the first school
        }
    }

    private fun requestSchool(school: School) {
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

    fun requestGetScoreLast128QuestionsExam() {
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

                    if (user.getAnsweredExams().isNotEmpty()) {
                        checkIfUserHas128Questionexams(user.getAnsweredExams())
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


    private fun checkIfUserHas128Questionexams(exams : List<Exam>) {

        var index = -1

        for (i in 0 .. exams.size - 1) {
            val totalQuestions = (exams.get(i).getHits() + exams.get(i).getMisses())
            if (totalQuestions.equals(EXAM_128_QUESTIONS)) {
                index = i
            }
        }

        if (!index.equals(-1)) {
            Log.d(TAG, "exam 128 questions ------ " + exams.get(index).getExamId())
            onRequestListenerSucces.onSuccess(exams.get(index).getHits())
        } else {
            val error = GenericError()
            onRequestLietenerFailed.onFailed(error)
        }

    }
}