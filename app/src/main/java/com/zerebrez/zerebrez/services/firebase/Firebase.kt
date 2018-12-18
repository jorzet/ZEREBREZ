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

package com.zerebrez.zerebrez.services.firebase

import android.app.Activity
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.enums.ErrorType
import com.zerebrez.zerebrez.services.database.DataHelper
import kotlin.collections.HashMap
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.zerebrez.zerebrez.models.*
import com.zerebrez.zerebrez.models.enums.ComproPagoStatus
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.request.AbstractRequestTask
import com.zerebrez.zerebrez.request.SendQuestionRequestTask
import com.zerebrez.zerebrez.request.SendQuestionsRequestTask
import java.text.Normalizer
import kotlin.collections.ArrayList

/**
 * This class provides a support to login in Firebase and get the
 * data stored on the Firebase platform casted in objects
 *
 * Also this class calls the local DataBase class to store the
 * current data in a local Data Base and SharedPreferences
 *
 * Created by Jorge Zepeda Tinoco on 28/04/18.
 * jorzet.94@gmail.com
 */

private const val TAG: String = "Firebase"

open class Firebase(activity: Activity) : Engagement(activity) {

    private val mActivity : Activity = activity

    /*
     * Labels to be replaced
     */
    private val COURSE_LABEL : String = "course_label"

    /*
     * Node references
     */
    private val INSTITUTES_REFERENCE : String = "schools/course_label"
    private val EXAMS_REFERENCE : String = "exams/course_label"
    private val COURSES_REFERENCE : String = "freeUser"
    private val PROFILE_REFERENCE : String = "profile"
    private val IMAGES_REFERENCE : String = "images/course_label"
    private val SELECTED_SCHOOLS_REFERENCE : String = "selectedSchools"
    private val MINIMUM_VERSION_REFERENCE : String = "minimumVersion/android"

    /*
     * Json keys
     */
    private val COURSE_KEY : String = "course"
    private val PREMIUM_KEY : String = "premium"
    private val COMPROPAGO_KEY : String = "comproPago"
    private val IS_PREMIUM_KEY : String = "isPremium"
    private val DEVELOPERS_DEBUG_KEY : String = "developersDebug"
    private val METHOD_KEY : String = "method"
    private val PAYMENT_CONFIRMED_IN_KEY : String = "paymentConfirmedIn"
    private val TIMESTAMP_KEY : String = "timeStamp"
    private val ANSWERED_MODULED_REFERENCE : String = "answeredModules"
    private val ANSWERED_EXAMS : String = "answeredExams"
    private val CORRECT_REFERENCE : String = "correct"
    private val INCORRECT_REFERENCE : String = "incorrect"
    private val INSTITUTION_ID : String = "institutionId"
    private val SCHOOL_ID : String = "schoolId"
    private val EMAIL_KEY : String = "email"
    private val BILLING_ID_KEY : String = "id"
    private val STATUS_KEY : String = "status"

    /*
     * Database object
     */
    private lateinit var mFirebaseDatabase: DatabaseReference


    fun requestLogIn(user : User?) {
        requestFirebaseLogIn(user)
    }

    fun requestSendPasswordResetEmail(email: String) {
        requestFirebaseSendPasswordResetEmail(email)
    }

    fun requestUpdateUser(user : User) {
        requestFirebaseUpdateUserEmail(user)
    }

    fun requestGetCourses() {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.SETTINGS_DATABASE_REFERENCE)
                .getReference(COURSES_REFERENCE)

        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                val map = (post as HashMap<String, HashMap<Any, Any>>)
                val mCourses = arrayListOf<String>()

                Log.d(TAG, post.toString())

                val mFreeModuleList = arrayListOf<Module>()
                val mFreeExamList = arrayListOf<Exam>()

                for ( key in map.keys) {
                    println(key)
                    mCourses.add(key)
                    val obj = map.get(key) as HashMap<String, Any>
                    for (key2 in obj.keys) {
                        if (key2.toString().equals("modules")) {
                            for (m in obj.get("modules") as List<String>) {
                                val module = Module()
                                module.setId(Integer(m.replace("m", "")))
                                mFreeModuleList.add(module)
                            }
                        }
                        if (key2.toString().equals("exams")) {
                            for (e in obj.get("exams") as List<String>) {
                                val exam = Exam()
                                exam.setExamId(Integer(e.replace("e", "")))
                                mFreeExamList.add(exam)
                            }
                        }
                    }
                }

                val dataHelper = DataHelper(mActivity)
                val modules = dataHelper.getModulesAnsQuestions()

                for (freeModule in mFreeModuleList) {
                    for (i in 0 .. modules.size - 1) {
                        if (freeModule.getId().equals(modules.get(i).getId())) {
                            modules.get(i).setFreeModule(true)
                        }
                    }
                }

                val exams = dataHelper.getExams()
                for (freeExam in mFreeExamList) {
                    for (i in 0 .. exams.size - 1) {
                        if (freeExam.getExamId().equals(exams.get(i).getExamId())) {
                            exams.get(i).setFreeExam(true)
                        }
                    }
                }

                dataHelper.saveModules(modules)
                dataHelper.saveExams(exams)
                dataHelper.saveFreeModules(mFreeModuleList)
                dataHelper.saveFreeExams(mFreeExamList)

                //SharedPreferencesManager(mActivity).storeJsonFreeModules(JsonParcer.parceObjectListToJson(mFreeModuleList))
                //SharedPreferencesManager(mActivity).storeJsonFreeExams(JsonParcer.parceObjectListToJson(mFreeExamList))

                // send courses in onSuccess listener
                onRequestListenerSucces.onSuccess(mCourses)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                onRequestLietenerFailed.onFailed(databaseError.toException())
            }
        })
    }

    fun requestSendUser(userCache : User) {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.USERS_DATABASE_REFERENCE)
                .getReference()

        mFirebaseDatabase.keepSynced(true)

        val user = getCurrentUser()
        if (user != null) {
            val userUpdates = HashMap<String, Any>()
            if (!userCache.getCourse().equals("")) {
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + COURSE_KEY, userCache.getCourse())
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + userCache.getCourse() + "/" + PREMIUM_KEY + "/" + DEVELOPERS_DEBUG_KEY, "Suscripcion")
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + userCache.getCourse() + "/" + PREMIUM_KEY + "/" + IS_PREMIUM_KEY, userCache.isPremiumUser())
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + userCache.getCourse() + "/" + PREMIUM_KEY + "/" + METHOD_KEY, userCache.getPayGayMethod())
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + userCache.getCourse() + "/" + PREMIUM_KEY + "/" + PAYMENT_CONFIRMED_IN_KEY, "Android")
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + userCache.getCourse() + "/" + PREMIUM_KEY + "/" + TIMESTAMP_KEY, userCache.getTimestamp())
            }
            //userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + PREMIUM_KEY + "/" + IS_PREMIUM_KEY, userCache.isPremiumUser())
            //userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + PREMIUM_KEY + "/" + TIMESTAMP_KEY, userCache.getTimestamp())

            mFirebaseDatabase.updateChildren(userUpdates).addOnCompleteListener(mActivity, object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if (task.isComplete) {
                        Log.d(TAG, "complete requestSendUser")
                        onRequestListenerSucces.onSuccess(true)
                    } else {
                        Log.d(TAG, "cancelled requestSendUser")
                        val error = GenericError()
                        error.setErrorType(ErrorType.USER_NOT_SENDED)
                        onRequestLietenerFailed.onFailed(error)
                    }
                }
            })
        }
    }


    fun requestRemoveCompropagoNode(userCache : User) {
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.USERS_DATABASE_REFERENCE)
                .getReference()

        mFirebaseDatabase.keepSynced(true)

        val user = getCurrentUser()
        if (user != null) {
            mFirebaseDatabase
                    .child(user.uid + "/" + PROFILE_REFERENCE + "/" + userCache.getCourse() + "/" + COMPROPAGO_KEY)
                    .removeValue()

        }
    }

    fun requestSendUserComproPago(userCache : User, billingId: String, comproPagoStatus: ComproPagoStatus) {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.USERS_DATABASE_REFERENCE)
                .getReference()

        mFirebaseDatabase.keepSynced(true)

        val user = getCurrentUser()
        if (user != null) {
            val userUpdates = HashMap<String, Any>()
            if (!userCache.getCourse().equals("")) {
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + COURSE_KEY, userCache.getCourse())
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + userCache.getCourse() + "/" + COMPROPAGO_KEY + "/" + COURSE_KEY, userCache.getCourse())
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + userCache.getCourse() + "/" + COMPROPAGO_KEY + "/" + EMAIL_KEY, userCache.getEmail())
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + userCache.getCourse() + "/" + COMPROPAGO_KEY + "/" + BILLING_ID_KEY, billingId)
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + userCache.getCourse() + "/" + COMPROPAGO_KEY + "/" + STATUS_KEY, comproPagoStatus)
            }
            //userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + PREMIUM_KEY + "/" + IS_PREMIUM_KEY, userCache.isPremiumUser())
            //userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + PREMIUM_KEY + "/" + TIMESTAMP_KEY, userCache.getTimestamp())

            mFirebaseDatabase.updateChildren(userUpdates).addOnCompleteListener(mActivity, object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if (task.isComplete) {
                        Log.d(TAG, "complete requestSendUser")
                        onRequestListenerSucces.onSuccess(true)
                    } else {
                        Log.d(TAG, "cancelled requestSendUser")
                        val error = GenericError()
                        error.setErrorType(ErrorType.USER_NOT_SENDED)
                        onRequestLietenerFailed.onFailed(error)
                    }
                }
            })
        }
    }


    fun requestSendAnsweredQuestionsNewFormat(questions: List<QuestionNewFormat>, course: String) {
        val sendQuestionsTask = SendQuestionsRequestTask(mActivity, questions)

        sendQuestionsTask.setOnRequestSuccess(object : AbstractRequestTask.OnRequestListenerSuccess {
            override fun onSuccess(result: Any) {
                Log.d(TAG, "send questions success")
            }
        })
        sendQuestionsTask.setOnRequestFailed(object : AbstractRequestTask.OnRequestListenerFailed {
            override fun onFailed(result: Throwable) {
                Log.d(TAG, "send questions fail")
            }
        })
        sendQuestionsTask.execute(course)
    }

    fun requestSendAnsweredQuestionNewFormat(question: QuestionNewFormat, course: String) {
        val sendQuestionTask = SendQuestionRequestTask(mActivity)

        sendQuestionTask.setOnRequestSuccess(object : AbstractRequestTask.OnRequestListenerSuccess {
            override fun onSuccess(result: Any) {
                Log.d(TAG, "send question success")
            }
        })
        sendQuestionTask.setOnRequestFailed(object : AbstractRequestTask.OnRequestListenerFailed {
            override fun onFailed(result: Throwable) {
                Log.d(TAG, "send question fail")
            }
        })
         sendQuestionTask.execute(question, course)
    }

    fun requestSendAnsweredModules(module : Module, course: String) {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.USERS_DATABASE_REFERENCE)
                .getReference()

        mFirebaseDatabase.keepSynced(true)

        val user = getCurrentUser()
        if (user != null) {
            val userUpdates = HashMap<String, Any>()

            if (module.isAnsweredModule()) {
                var correct = 0
                var incorrect = 0
                for (question in module.getQuestionsNewFormat()) {
                    if (question.wasOK) {
                        correct++
                    } else {
                        incorrect++
                    }
                }

                userUpdates.put(user.uid + "/" + ANSWERED_MODULED_REFERENCE + "/" + course + "/" + "m" + module.getId() + "/" + CORRECT_REFERENCE, correct)
                userUpdates.put(user.uid + "/" + ANSWERED_MODULED_REFERENCE + "/" + course + "/" + "m" + module.getId() + "/" + INCORRECT_REFERENCE, incorrect)
            }

            mFirebaseDatabase.updateChildren(userUpdates).addOnCompleteListener(mActivity, object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if (task.isComplete) {
                        Log.d(TAG, "complete requestSendAnsweredModules")
                        onRequestListenerSucces.onSuccess(true)
                    } else {
                        Log.d(TAG, "cancelled requestSendAnsweredModules")
                        val error = GenericError()
                        error.setErrorType(ErrorType.ANSWERED_MODULES_NOT_SENDED)
                        onRequestLietenerFailed.onFailed(error)
                    }
                }
            })
        }
    }


    fun requestSendAnsweredExams(exam : Exam, course: String) {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.USERS_DATABASE_REFERENCE)
                .getReference()

        mFirebaseDatabase.keepSynced(true)

        val user = getCurrentUser()
        if (user != null) {
            val userUpdates = HashMap<String, Any>()

            if (exam.isAnsweredExam()) {
                userUpdates.put(user.uid + "/" + ANSWERED_EXAMS + "/" + course + "/" + "e" + exam.getExamId() + "/" + CORRECT_REFERENCE, exam.getHits())
                userUpdates.put(user.uid + "/" + ANSWERED_EXAMS + "/" + course + "/" + "e" + exam.getExamId()+ "/" + INCORRECT_REFERENCE, exam.getMisses())
            }

            mFirebaseDatabase.updateChildren(userUpdates).addOnCompleteListener(mActivity, object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if (task.isComplete) {
                        Log.d(TAG, "complete requestSendAnsweredExams")
                        onRequestListenerSucces.onSuccess(true)
                    } else {
                        Log.d(TAG, "cancelled requestSendAnsweredExams")
                        val error = GenericError()
                        error.setErrorType(ErrorType.ANSWERED_EXAMS_NOT_SENDED)
                        onRequestLietenerFailed.onFailed(error)
                    }
                }
            })
        }
    }

    fun requestSendSelectedSchools(userCache: User, schools : List<School>) {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.USERS_DATABASE_REFERENCE)
                .getReference()

        mFirebaseDatabase.keepSynced(true)

        val user = getCurrentUser()
        if (user != null) {
            val userUpdates = HashMap<String, Any>()
            val dbNode = mFirebaseDatabase.child(user.uid + "/" + PROFILE_REFERENCE + "/" + userCache.getCourse() + "/" + SELECTED_SCHOOLS_REFERENCE)
            dbNode.setValue(null)

            for (i in 0 .. schools.size - 1) {
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + userCache.getCourse() + "/" +  SELECTED_SCHOOLS_REFERENCE + "/" + i + "/" + INSTITUTION_ID, "institute" + schools.get(i).getInstituteId())
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + userCache.getCourse() + "/" +  SELECTED_SCHOOLS_REFERENCE + "/" + i + "/" + SCHOOL_ID, "school" + schools.get(i).getSchoolId())
            }
            mFirebaseDatabase.updateChildren(userUpdates).addOnCompleteListener(mActivity, object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if (task.isComplete) {
                        Log.d(TAG, "complete requestSendSelectedSchools")
                        onRequestListenerSucces.onSuccess(true)
                    } else {
                        Log.d(TAG, "cancelled requestSendSelectedSchools")
                        val error = GenericError()
                        error.setErrorType(ErrorType.SELECTED_SCHOOLS_NOT_SENDED)
                        onRequestLietenerFailed.onFailed(error)
                    }
                }
            })
        }
    }

    fun requestGetUserData() {
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
                    val map = (post as HashMap<String, String>)
                    Log.d(TAG, "user data ------ " + map.size)

                    val user = User()
                    for ( key in map.keys) {
                        println(key)
                        if (key.equals("profile")) {
                            val profile = map.get(key) as HashMap<String, String>
                            for (key2 in profile.keys) {
                                if (key2.equals("premium")) {
                                    val premiumHash = map.get(key) as HashMap<String, String>
                                    for (key4 in profile.keys) {
                                        if (key4.equals(IS_PREMIUM_KEY)) {
                                            val isPremium = premiumHash.get(key4) as Boolean
                                            user.setPremiumUser(isPremium)
                                        } else if (key4.equals(TIMESTAMP_KEY)) {
                                            val timeStamp = premiumHash.get(key4) as Long
                                            user.setTimeStamp(timeStamp)
                                        }
                                    }

                                } else if (key2.equals("course")) {
                                    val course = profile.get(key2).toString()
                                    user.setCourse(course)
                                } else if (key2.equals("selectedSchools")) {
                                    val selectedSchools = profile.get(key2) as ArrayList<Any>
                                    val schools = arrayListOf<School>()
                                    Log.d(TAG, "user data ------ " + selectedSchools.size)
                                    for (i in 0 .. selectedSchools.size - 1) {
                                        val institute = selectedSchools.get(i) as HashMap<String ,String>
                                        val school = School()
                                        if (institute.containsKey("institutionId")) {
                                            school.setInstituteId(Integer(institute.get("institutionId")!!.replace("institute","")))
                                        }

                                        if (institute.containsKey("schoolId")) {
                                            school.setSchoolId(Integer(institute.get("schoolId")!!.replace("school","")))
                                        }
                                        schools.add(school)
                                    }
                                    user.setSelectedShools(schools)
                                }
                            }
                        } else if (key.equals("answeredQuestions")) {
                            val answeredQuestions = map.get(key) as HashMap<String, String>
                            val questions = arrayListOf<QuestionNewFormat>()
                            for (key2 in answeredQuestions.keys) {
                                val questionAnswered = answeredQuestions.get(key2) as HashMap<String, String>
                                val question = QuestionNewFormat()
                                question.questionId = key2
                                for (key3 in questionAnswered.keys) {
                                    if (key3.equals("subject")) {
                                        val subject = limpiarTexto(questionAnswered.get(key3))
                                        when (subject) {
                                            limpiarTexto(SubjectType.VERBAL_HABILITY.value) -> {
                                                question.subject = SubjectType.VERBAL_HABILITY
                                            }
                                            limpiarTexto(SubjectType.MATHEMATICAL_HABILITY.value) -> {
                                                question.subject = SubjectType.MATHEMATICAL_HABILITY
                                            }
                                            limpiarTexto(SubjectType.MATHEMATICS.value) -> {
                                                question.subject = SubjectType.MATHEMATICS
                                            }
                                            limpiarTexto(SubjectType.SPANISH.value) -> {
                                                question.subject = SubjectType.SPANISH
                                            }
                                            limpiarTexto(SubjectType.BIOLOGY.value) -> {
                                                question.subject = SubjectType.BIOLOGY
                                            }
                                            limpiarTexto(SubjectType.CHEMISTRY.value) -> {
                                                question.subject = SubjectType.CHEMISTRY
                                            }
                                            limpiarTexto(SubjectType.PHYSICS.value) -> {
                                                question.subject = SubjectType.PHYSICS
                                            }
                                            limpiarTexto(SubjectType.GEOGRAPHY.value) -> {
                                                question.subject = SubjectType.GEOGRAPHY
                                            }
                                            limpiarTexto(SubjectType.UNIVERSAL_HISTORY.value) -> {
                                                question.subject = SubjectType.UNIVERSAL_HISTORY
                                            }
                                            limpiarTexto(SubjectType.MEXICO_HISTORY.value) -> {
                                                question.subject = SubjectType.MEXICO_HISTORY
                                            }
                                            limpiarTexto(SubjectType.FCE.value) -> {
                                                question.subject = SubjectType.FCE
                                            }
                                        }
                                    } else if (key3.equals("isCorrect")) {
                                        val isCorrect = questionAnswered.get(key3) as Boolean
                                        question.wasOK = isCorrect
                                    } else if (key3.equals("chosenOption")) {
                                        val chosenOption = questionAnswered.get(key3).toString()
                                        question.chosenOption = chosenOption
                                    }
                                }
                                questions.add(question)
                            }
                            user.setAnsweredQuestionsNewFormat(questions)
                        } else if (key.equals("answeredModules")) {
                            val answeredModules = map.get(key) as HashMap<String, String>
                            val modules = arrayListOf<Module>()

                            for (key2 in answeredModules.keys) {
                                val moduleAnswered = answeredModules.get(key2) as HashMap<String, String>
                                val module = Module()
                                module.setId(Integer(key2.replace("m","")))

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
                        } else if (key.equals("answeredExams")) {
                            val answeredExams = map.get(key) as HashMap<String, String>
                            val exams = arrayListOf<Exam>()
                            for (key2 in answeredExams.keys) {
                                val examAnswered = answeredExams.get(key2) as HashMap<String, String>
                                val exam = Exam()
                                exam.setExamId(Integer(key2.replace("e","")))

                                for (key3 in examAnswered.keys) {
                                    if (key3.equals("incorrect")) {
                                        val incorrectQuestions = (examAnswered.get(key3) as java.lang.Long).toInt()
                                        exam.setMisses(incorrectQuestions)
                                    } else if (key3.equals("correct")) {
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


    fun requestGetInstitutes(course: String) {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.SETTINGS_DATABASE_REFERENCE)
                .getReference(INSTITUTES_REFERENCE.replace(COURSE_LABEL, course))

        mFirebaseDatabase.keepSynced(true)

        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                val map = (post as HashMap<String, HashMap<Any, Any>>)
                val mInstitutes = arrayListOf<Institute>()

                Log.d(TAG, post.toString())

                for (key in map.keys) {
                    println(key)
                    val institute = Institute()
                    institute.setInstituteId(Integer(key.replace("institute","")))
                    val instituteHash = map.get(key) as HashMap<String, String>
                    for (key2 in instituteHash.keys) {
                        if (key2.equals("schoolsList")) {
                            val schools = arrayListOf<School>()
                            val schoolsHash = instituteHash.get(key2) as HashMap<String, String>
                            for (key3 in schoolsHash.keys) {
                                val school = School()
                                school.setSchoolId(Integer(key3.replace("school","")))

                                val schoolDataHash = schoolsHash.get(key3) as HashMap<String, String>
                                for (key4 in schoolDataHash.keys) {
                                    if (key4.equals("name")) {
                                        school.setSchoolName(schoolDataHash.get(key4).toString())
                                    } else if (key4.equals("score")) {
                                        school.setHitsNumber((schoolDataHash.get(key4) as java.lang.Long).toInt())
                                    }
                                }
                                schools.add(school)
                            }
                            institute.setSchools(schools)
                        } else if (key2.equals("name")) {
                            institute.setInstituteName(instituteHash.get(key2).toString())
                        }
                    }
                    mInstitutes.add(institute)
                }

                onRequestListenerSucces.onSuccess(mInstitutes)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                onRequestLietenerFailed.onFailed(databaseError.toException())
            }
        })
    }

    fun requestGetExams() {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance()
                .getReference(EXAMS_REFERENCE)

        mFirebaseDatabase.keepSynced(true)

        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                val map = (post as HashMap<String, HashMap<Any, Any>>)
                val mExams = arrayListOf<Exam>()

                Log.d(TAG, post.toString())

                /*
                 * mapping map to module object
                 */
                for ( key in map.keys) {
                    println(key)
                    val exam = Exam()
                    val questionsNewFormat = arrayListOf<QuestionNewFormat>()

                    // get question id from response
                    val list = map.get(key) as List<String>
                    for (q in list) {
                        val questionNewFormat = QuestionNewFormat()
                        questionNewFormat.questionId = q
                        questionsNewFormat.add(questionNewFormat)
                    }

                    // set module id and question id
                    exam.setExamId(Integer(key.replace("e","")))
                    exam.setQuestionsNewFormat(questionsNewFormat)

                    // add module to list
                    mExams.add(exam)
                }


                onRequestListenerSucces.onSuccess(mExams)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                onRequestLietenerFailed.onFailed(databaseError.toException())
            }
        })
    }

    fun requestGetImagesPath(course: String) {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance()
                .getReference(IMAGES_REFERENCE.replace(COURSE_LABEL, course))

        mFirebaseDatabase.keepSynced(true)

        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                val map = (post as HashMap<String, HashMap<Any, Any>>)
                val mImage = arrayListOf<Image>()

                Log.d(TAG, post.toString())

                /*
                 * mapping map to module object
                 */
                for ( key in map.keys) {
                    println(key)
                    val image = Image()

                    // set module id and question id
                    image.setImageId(Integer(key.replace("i","")))
                    val values = map.get(key) as HashMap<String, String>

                    for (key2 in values.keys) {
                        if (key2.equals("download")) {
                            image.setIsDownloadable(values.get(key2) as Boolean)
                        }

                        if (key2.equals("nameInStorage")) {
                            image.setNameInStorage(values.get(key2) as String)
                        }
                    }
                    // add module to list
                    mImage.add(image)
                }


                onRequestListenerSucces.onSuccess(mImage)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                onRequestLietenerFailed.onFailed(databaseError.toException())
            }
        })
    }

    fun requestSignInUserWithFacebookProvider(accessToken: AccessToken) {
        requestSignInWithFacebookProvider(accessToken)
    }

    fun requestSigInUserWithGoogleProvider(credential: AuthCredential) {
        requestSignInWithGoogleProvider(credential)
    }

    fun requestLinkAnonymousUserWithFacebookProvider(accessToken: AccessToken) {
        requestLinkWithFacebookProvider(accessToken)
    }

    fun requestLinkAnonymousUserWithGoogleProvider(credential: AuthCredential) {
        requestLinkWithGoogleProvider(credential)
    }

    override fun onEmailUpdatedSuccess(user : User) {
        super.onEmailUpdatedSuccess(user)
        requestFirebaseUpdateUserPassword(user)
    }

    override fun onEmailUpdatedFail(throwable: Throwable) {
        super.onEmailUpdatedFail(throwable)
        onRequestLietenerFailed.onFailed(throwable)
    }

    override fun onPasswordUpdatedSuccess(success: Boolean) {
        super.onPasswordUpdatedSuccess(success)
        onRequestListenerSucces.onSuccess(success)
    }

    override fun onPasswordUpdatedFail(throwable: Throwable) {
        super.onPasswordUpdatedFail(throwable)
        onRequestLietenerFailed.onFailed(throwable)
    }


    fun requestUpdateUserPassword(user : User) {
        requestChangeUserPassword(user)
    }

    fun requestGetMinimumVersion() {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.SETTINGS_DATABASE_REFERENCE)
                .getReference(MINIMUM_VERSION_REFERENCE)

        mFirebaseDatabase.keepSynced(true)

        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val minimumVersion = post as String
                    Log.d(TAG, "minimum version ------ $minimumVersion" )
                    onRequestListenerSucces.onSuccess(minimumVersion)
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

    fun limpiarTexto(cadena: String?): String? {
        var limpio: String? = null
        if (cadena != null) {
            var valor: String = cadena
            valor = valor.toUpperCase()
            // Normalizar texto para eliminar acentos, dieresis, cedillas y tildes
            limpio = Normalizer.normalize(valor, Normalizer.Form.NFD)
            // Quitar caracteres no ASCII excepto la enie, interrogacion que abre, exclamacion que abre, grados, U con dieresis.
            limpio = limpio!!.replace("[^\\p{ASCII}(N\u0303)(n\u0303)(\u00A1)(\u00BF)(\u00B0)(U\u0308)(u\u0308)]".toRegex(), "")
            // Regresar a la forma compuesta, para poder comparar la enie con la tabla de valores
            limpio = Normalizer.normalize(limpio, Normalizer.Form.NFC).replace(" ","").toLowerCase()
        }
        return limpio
    }

}
