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
import com.zerebrez.zerebrez.models.enums.QuestionType
import com.zerebrez.zerebrez.models.enums.RequestType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.services.sharedpreferences.JsonParcer
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import java.util.*
import kotlin.collections.HashMap
import org.json.JSONObject
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.zerebrez.zerebrez.models.*
import com.zerebrez.zerebrez.models.enums.SubjectType
import org.json.JSONArray
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

    private val MODULES_REFERENCE : String = "modules/comipems"
    private val COMIPEMS_QUESTIONS_REFERENCE : String = "questions/comipems"
    private val INSTITUTES_REFERENCE : String = "schools/comipems"
    private val EXAMS_REFERENCE : String = "exams/comipems"
    private val COURSES_REFERENCE : String = "freeUser"
    private val USERS_REFERENCE : String = "users"
    private val PROFILE_REFERENCE : String = "profile"
    private val EXAM_SCORES_REFERENCE : String = "scores/exams/comipems"
    private val IMAGES_REFERENCE : String = "images/comipems"
    private val SELECTED_SCHOOLS_REFERENCE : String = "selectedSchools"
    private val COURSE_KEY : String = "course"
    private val PREMIUM_KEY : String = "isPremium"
    private val ANSWERED_MODULED_REFERENCE : String = "answeredModules"
    private val ANSWERED_QUESTION_MODULE : String = "answeredQuestions"
    private val ANSWERED_EXAMS : String = "answeredExams"
    private val IS_CORRECT_REFERENCE : String = "isCorrect"
    private val SUBJECT_REFERENCE : String = "subject"
    private val CORRECT_REFERENCE : String = "correct"
    private val INCORRECT_REFERENCE : String = "incorrect"
    private val CHOSEN_OPTION_REFERENCE : String = "chosenOption"
    private val INSTITUTION_ID : String = "institutionId"
    private val SCHOOL_ID : String = "schoolId"


    private lateinit var mRequestType : RequestType

    private lateinit var mFirebaseDatabase: DatabaseReference
    private var mFirebaseInstance: FirebaseDatabase

    init {
        mFirebaseInstance = FirebaseDatabase.getInstance()
        if (!SharedPreferencesManager(mActivity).isPersistanceData()) {
            mFirebaseInstance.setPersistenceEnabled(true)
            SharedPreferencesManager(mActivity).setPersistanceDataEnable(true)
        }
    }

    /*
     * This is an override method that returns a boolean this value is to
     * check if the Firebase login was success of not and according to the
     * request type it going to call the correspond method to get the data
     */
    /*override fun onFirebaseLogIn(success: Boolean) {
        super.onFirebaseLogIn(success)
        if (success) {
            when (mRequestType) {
                RequestType.USER_LOGIN -> {

                }
                RequestType.MODULES -> {
                    //getModules()
                }
                RequestType.QUESTIONS -> {
                    // TODO
                    //getQuestions()
                }
            }

        } else {
            Log.d(TAG, "error firebase log in");
        }
    }*/

    fun requestLogIn(user : User?) {
        requestFirebaseLogIn(user)
    }

    fun requestUpdateUser(user : User) {
        requestFirebaseUpdateUserEmail(user)
    }

    /*
     * This method set the request type and calls requestFirebaseLogIn()
     * then the override method called onFirebaseLogIn() gets the Firebase
     * login response
     */
    /*fun requestGetModules() {
        mRequestType = RequestType.MODULES
        requestFirebaseLogIn()
    }*/

    /*
     *
     */
    fun requestGetQuestions() {
        mRequestType = RequestType.QUESTIONS
        //requestFirebaseLogIn()
    }


    /*
     * This method init an inistance of Firebase and gets the module reference
     * then cast the response in module object list and set a response on listener
     */
    fun requestGetModules() {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(MODULES_REFERENCE)

        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                val map = (post as HashMap<String, String>)
                val mModules = arrayListOf<Module>()

                Log.d(TAG, post.toString())

                /*
                 * mapping map to module object
                 */
                for ( key in map.keys) {
                    println(key)
                    val module = Module()
                    val questions = arrayListOf<Question>()

                    // get question id from response
                    val list = map.get(key) as List<String>
                    for (q in list) {
                        val question = Question()
                        question.setQuestionId(Integer(q.replace("p","")))
                        question.setModuleId(Integer(key.replace("m","")))
                        questions.add(question)
                    }

                    // set module id and question id
                    module.setId(Integer(key.replace("m","")))
                    module.setQuestions(questions)

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

                getQuestions(mModules)
                //val dataHelper = DataHelper(mActivity)
                //dataHelper.insertModules(mModules)
                //dataHelper.getModules()
                //onRequestListenerSucces.onSuccess(mModules)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                //onRequestLietenerFailed.onFailed(databaseError.toException())
            }
        })
    }

    /*
     * This method gets all questions from Firebase and add it in its correspond module
     */
    fun getQuestions(modules : List<Module>) {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(COMIPEMS_QUESTIONS_REFERENCE)

        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                val map = (post as HashMap<String, HashMap<Any, Any>>)
                val mQuestions = arrayListOf<Question>()

                Log.d(TAG, post.toString())

                /*
                 * mapping map to module object
                 */
                for ( key in map.keys) {
                    println(key)

                    val question = Question()
                    // getting question id
                    val questionId = Integer(key.replace("p",""))
                    question.setQuestionId(questionId)

                    //getting values
                    // get text value
                    val json = JSONObject(map.get(key))
                    if (json.has("text")) {
                        Log.d(TAG,"json has text value ------")
                        val texts = json.getJSONArray("text")
                        val text = arrayListOf<String>()
                        for (i in 0 .. texts.length() - 1) {
                            text.add(texts.get(i).toString())
                        }

                        question.setText(text)
                    } else {
                        Log.d(TAG,"json has not text value ******  " + json.toString())
                    }

                    if (json.has("equation")) {
                        Log.d(TAG,"json has text value ------")
                        val equations = json.getJSONArray("equation")
                        val equation = arrayListOf<String>()
                        for (i in 0 .. equations.length() - 1) {
                            equation.add(equations.get(i).toString())
                        }

                        question.setEquations(equation)
                    } else {
                        Log.d(TAG,"json has not equation value ******  " + json.toString())
                    }

                    if (json.has("image")) {
                        Log.d(TAG,"json has text value ------")
                        val images = json.getJSONArray("image")
                        val image = arrayListOf<String>()
                        for (i in 0 .. images.length() - 1) {
                            image.add(images.get(i).toString())
                        }

                        question.setImages(image)
                    } else {
                        Log.d(TAG,"json has not image value ******  " + json.toString())
                    }

                    /*
                     * here it is get the step by step
                     */
                    if (json.has("stepByStepText")) {
                        Log.d(TAG,"json has text value ------")
                        val texts = json.getJSONArray("stepByStepText")
                        val stepByStepText = arrayListOf<String>()
                        for (i in 0 .. texts.length() - 1) {
                            stepByStepText.add(texts.get(i).toString())
                        }

                        question.setStepByStepText(stepByStepText)
                    } else {
                        Log.d(TAG,"json has not text value ******  " + json.toString())
                    }

                    if (json.has("stepByStepEquation")) {
                        Log.d(TAG,"json has text value ------")
                        val equations = json.getJSONArray("stepByStepEquation")
                        val stepByStepEquation = arrayListOf<String>()
                        for (i in 0 .. equations.length() - 1) {
                            stepByStepEquation.add(equations.get(i).toString())
                        }

                        question.setStepByStepEquations(stepByStepEquation)
                    } else {
                        Log.d(TAG,"json has not equation value ******  " + json.toString())
                    }

                    if (json.has("stepByStepImage")) {
                        Log.d(TAG,"json has text value ------")
                        val images = json.getJSONArray("stepByStepImage")
                        val stepByStepImage = arrayListOf<String>()
                        for (i in 0 .. images.length() - 1) {
                            stepByStepImage.add(images.get(i).toString())
                        }

                        question.setStepByStepImages(stepByStepImage)
                    } else {
                        Log.d(TAG,"json has not image value ******  " + json.toString())
                    }


                    /*
                     * Here it is get the subjects
                     */
                    if (json.has("subject")) {
                        val subject = json.getString("subject")
                        when (subject) {
                            SubjectType.VERBAL_HABILITY.value -> {
                                question.setSubjectType(SubjectType.VERBAL_HABILITY)
                            }
                            SubjectType.MATHEMATICAL_HABILITY.value -> {
                                question.setSubjectType(SubjectType.MATHEMATICAL_HABILITY)
                            }
                            SubjectType.MATHEMATICS.value -> {
                                question.setSubjectType(SubjectType.MATHEMATICS)
                            }
                            SubjectType.SPANISH.value -> {
                                question.setSubjectType(SubjectType.SPANISH)
                            }
                            SubjectType.BIOLOGY.value -> {
                                question.setSubjectType(SubjectType.BIOLOGY)
                            }
                            SubjectType.CHEMISTRY.value -> {
                                question.setSubjectType(SubjectType.CHEMISTRY)
                            }
                            SubjectType.PHYSICS.value -> {
                                question.setSubjectType(SubjectType.PHYSICS)
                            }
                            SubjectType.GEOGRAPHY.value -> {
                                question.setSubjectType(SubjectType.GEOGRAPHY)
                            }
                            SubjectType.UNIVERSAL_HISTORY.value -> {
                                question.setSubjectType(SubjectType.UNIVERSAL_HISTORY)
                            }
                            SubjectType.MEXICO_HISTORY.value -> {
                                question.setSubjectType(SubjectType.MEXICO_HISTORY)
                            }
                            SubjectType.FCE.value -> {
                                question.setSubjectType(SubjectType.FCE)
                            }
                        }
                    }

                    if (json.has("answer")) {
                        val answer = json.getString("answer")
                        question.setAnswer(answer)
                    }

                    val mapOptionText = map.get(key)
                    var optionText = JSONArray()

                    if (json.has("optionsEquation")) {
                        optionText = json.getJSONArray("optionsEquation")
                        question.setQuestionType(QuestionType.EQUATION.toString())
                    } else if(json.has("optionsImage")) {
                        optionText = json.getJSONArray("optionsImage")
                        question.setQuestionType(QuestionType.IMAGE.toString())
                    } else if (json.has("optionsText")){
                        optionText = json.getJSONArray("optionsText")
                        question.setQuestionType(QuestionType.TEXT.toString())
                    }

                    if (optionText.length() > 0)
                        question.setOptionOne(optionText.get(0).toString())
                    if (optionText.length() > 1)
                        question.setOptionTwo(optionText.get(1).toString())
                    if (optionText.length() > 2)
                        question.setOptionThree(optionText.get(2).toString())
                    if (optionText.length() > 3)
                        question.setOptionFour(optionText.get(3).toString())


                    if (json.has("year")) {
                        val year = json.getString("year")
                        question.setYear(year)
                    }

                    mQuestions.add(question)

                }

                val updatedModules = arrayListOf<Module>()
                for (module in modules) {
                    val updateQuestions = arrayListOf<Question>()
                    for (question in mQuestions) {
                        for (currentQuestion in module.getQuestions()) {
                            if (currentQuestion.getQuestionId().equals(question.getQuestionId())
                            && module.getId().equals(currentQuestion.getModuleId())) {
                                //question.setModuleId(module.getId())
                                updateQuestions.add(question)
                            }
                        }
                    }
                    if (!updateQuestions.isEmpty()) {
                        module.setQuestions(updateQuestions)
                    }
                    updatedModules.add(module)
                }

                val dataHelper = DataHelper(mActivity)
                dataHelper.saveModules(updatedModules)
                dataHelper.saveQuestions(mQuestions)


                // send modules in onSuccess listener
                onRequestListenerSucces.onSuccess(updatedModules)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                onRequestLietenerFailed.onFailed(databaseError.toException())
            }
        })
    }

    fun requestGetCourses() {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(COURSES_REFERENCE)

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
        mFirebaseDatabase = mFirebaseInstance.getReference(USERS_REFERENCE)
        val user = getCurrentUser()
        if (user != null) {
            val userUpdates = HashMap<String, Any>()
            userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + COURSE_KEY, userCache.getCourse())
            userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + PREMIUM_KEY, userCache.isPremiumUser())
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

    fun requestGetExamScores() {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(EXAM_SCORES_REFERENCE)

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

    fun requestGetSchoolScores() {

    }

    fun requestSendAnsweredQuestions(modules: List<Module>) {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(USERS_REFERENCE)
        val user = getCurrentUser()
        if (user != null) {
            val userUpdates = HashMap<String, Any>()
            for (module in modules) {
                for (question in module.getQuestions()) {
                    if (!question.getOptionChoosed().equals("")) {
                        userUpdates.put(user.uid + "/" + ANSWERED_QUESTION_MODULE + "/" + "p" + question.getQuestionId() + "/" + IS_CORRECT_REFERENCE, question.getWasOK())
                        userUpdates.put(user.uid + "/" + ANSWERED_QUESTION_MODULE + "/" + "p" + question.getQuestionId() + "/" + SUBJECT_REFERENCE, question.getSubjectType().value)
                        userUpdates.put(user.uid + "/" + ANSWERED_QUESTION_MODULE + "/" + "p" + question.getQuestionId() + "/" + CHOSEN_OPTION_REFERENCE, question.getOptionChoosed())
                    }
                }
            }
            mFirebaseDatabase.updateChildren(userUpdates).addOnCompleteListener(mActivity, object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if (task.isComplete) {
                        Log.d(TAG, "complete requestSendAnsweredQuestions")
                        onRequestListenerSucces.onSuccess(true)
                    } else {
                        Log.d(TAG, "cancelled requestSendAnsweredQuestions")
                        val error = GenericError()
                        error.setErrorType(ErrorType.ANSWERED_QUESTIONS_NOT_SENDED)
                        onRequestLietenerFailed.onFailed(error)
                    }
                }
            })
        }
    }

    fun requestSendAnsweredModules(modules : List<Module>) {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(USERS_REFERENCE)
        val user = getCurrentUser()
        if (user != null) {
            val userUpdates = HashMap<String, Any>()
            for (module in modules) {
                if (module.isAnsweredModule()) {
                    var correct = 0
                    var incorrect = 0
                    for (question in module.getQuestions()) {
                        if (question.getWasOK()) {
                            correct++
                        } else {
                            incorrect++
                        }
                    }

                    userUpdates.put(user.uid + "/" + ANSWERED_MODULED_REFERENCE + "/" + "m" + module.getId() + "/" + CORRECT_REFERENCE, correct)
                    userUpdates.put(user.uid + "/" + ANSWERED_MODULED_REFERENCE + "/" + "m" + module.getId() + "/" + INCORRECT_REFERENCE, incorrect)
                }
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


    fun requestSendAnsweredExams(exams : List<Exam>) {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(USERS_REFERENCE)
        val user = getCurrentUser()
        if (user != null) {
            val userUpdates = HashMap<String, Any>()
            for (exam in exams) {
                if (exam.isAnsweredExam()) {
                    userUpdates.put(user.uid + "/" + ANSWERED_EXAMS + "/" + "e" + exam.getExamId() + "/" + CORRECT_REFERENCE, exam.getHits())
                    userUpdates.put(user.uid + "/" + ANSWERED_EXAMS + "/" + "e" + exam.getExamId()+ "/" + INCORRECT_REFERENCE, exam.getMisses())
                }
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

    fun requestSendSelectedSchools(schools : List<School>) {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(USERS_REFERENCE)
        val user = getCurrentUser()
        if (user != null) {
            val userUpdates = HashMap<String, Any>()
            for (i in 0 .. schools.size - 1) {
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + SELECTED_SCHOOLS_REFERENCE + "/" + i + "/" + INSTITUTION_ID, "institute" + schools.get(i).getInstituteId())
                userUpdates.put(user.uid + "/" + PROFILE_REFERENCE + "/" + SELECTED_SCHOOLS_REFERENCE + "/" + i + "/" + SCHOOL_ID, "school" + schools.get(i).getSchoolId())
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
            mFirebaseDatabase = mFirebaseInstance.getReference(USERS_REFERENCE + "/" + user.uid)

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
                                if (key2.equals("isPremium")) {
                                    val isPremium = profile.get(key2) as Boolean
                                    user.setPremiumUser(isPremium)
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
                            val questions = arrayListOf<Question>()
                            for (key2 in answeredQuestions.keys) {
                                val questionAnswered = answeredQuestions.get(key2) as HashMap<String, String>
                                val question = Question()
                                question.setQuestionId(Integer(key2.replace("p","").replace("q","")))
                                for (key3 in questionAnswered.keys) {
                                    if (key3.equals("subject")) {
                                        val subject = questionAnswered.get(key3)
                                        when (subject) {
                                            SubjectType.VERBAL_HABILITY.value -> {
                                                question.setSubjectType(SubjectType.VERBAL_HABILITY)
                                            }
                                            SubjectType.MATHEMATICAL_HABILITY.value -> {
                                                question.setSubjectType(SubjectType.MATHEMATICAL_HABILITY)
                                            }
                                            SubjectType.MATHEMATICS.value -> {
                                                question.setSubjectType(SubjectType.MATHEMATICS)
                                            }
                                            SubjectType.SPANISH.value -> {
                                                question.setSubjectType(SubjectType.SPANISH)
                                            }
                                            SubjectType.BIOLOGY.value -> {
                                                question.setSubjectType(SubjectType.BIOLOGY)
                                            }
                                            SubjectType.CHEMISTRY.value -> {
                                                question.setSubjectType(SubjectType.CHEMISTRY)
                                            }
                                            SubjectType.PHYSICS.value -> {
                                                question.setSubjectType(SubjectType.PHYSICS)
                                            }
                                            SubjectType.GEOGRAPHY.value -> {
                                                question.setSubjectType(SubjectType.GEOGRAPHY)
                                            }
                                            SubjectType.UNIVERSAL_HISTORY.value -> {
                                                question.setSubjectType(SubjectType.UNIVERSAL_HISTORY)
                                            }
                                            SubjectType.MEXICO_HISTORY.value -> {
                                                question.setSubjectType(SubjectType.MEXICO_HISTORY)
                                            }
                                            SubjectType.FCE.value -> {
                                                question.setSubjectType(SubjectType.FCE)
                                            }
                                        }
                                    } else if (key3.equals("isCorrect")) {
                                        val isCorrect = questionAnswered.get(key3) as Boolean
                                        question.setWasOK(isCorrect)
                                    } else if (key3.equals("chosenOption")) {
                                        val chosenOption = questionAnswered.get(key3).toString()
                                        question.setOptionChoosed(chosenOption)
                                    }
                                }
                                questions.add(question)
                            }
                            user.setAnsweredQuestions(questions)
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


    fun requestGetInstitutes() {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(INSTITUTES_REFERENCE)

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
        mFirebaseDatabase = mFirebaseInstance.getReference(EXAMS_REFERENCE)

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
                    val questions = arrayListOf<Question>()

                    // get question id from response
                    val list = map.get(key) as List<String>
                    for (q in list) {
                        val question = Question()
                        question.setQuestionId(Integer(q.replace("p","")))
                        questions.add(question)
                    }

                    // set module id and question id
                    exam.setExamId(Integer(key.replace("e","")))
                    exam.setQuestions(questions)

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

    fun requestGetImagesPath() {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(IMAGES_REFERENCE)

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
}
