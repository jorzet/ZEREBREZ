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

package com.zerebrez.zerebrez.services.firebase.question

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.Module
import com.zerebrez.zerebrez.models.Question
import com.zerebrez.zerebrez.models.enums.QuestionType
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.services.firebase.Engagement
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import org.json.JSONArray
import org.json.JSONObject

private const val TAG: String = "QuestionsRequest"

class QuestionsRequest(activity: Activity) : Engagement(activity) {

    private val COMIPEMS_QUESTIONS_REFERENCE : String = "questions/comipems"
    private val MODULES_REFERENCE : String = "modules/comipems"
    private val EXAMS_REFERENCE : String = "exams/comipems"
    private val ANSWERED_QUESTION_REFERENCE : String = "answeredQuestions"
    private val USERS_REFERENCE : String = "users"

    private val IS_PREMIUM_KEY : String = "isPremium"
    private val TIMESTAMP_KEY : String = "timeStamp"
    private val PREMIUM_KEY : String = "premium"
    private val IS_CORRECT_KEY : String = "isCorrect"
    private val SUBJECT_KEY : String = "subject"
    private val CHOOSEN_OPTION_KEY : String = "chosenOption"

    private lateinit var mQuestions : List<Question>
    private var mGotQuestions = arrayListOf<Question>()
    private var mCurrentQuestion : Int = 0
    private var mQuestionSize : Int = 0
    private var mLastWrongQuestion : Boolean = false

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

    fun requestGetQuestionsByModuleId(moduleId : Int) {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(MODULES_REFERENCE + "/m" + moduleId)
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                val map = (post as List<String>)

                Log.d(TAG, post.toString())

                /*
                 * mapping map to module object
                 */

                val questions = arrayListOf<Question>()

                // get question id from response
                for (q in map) {
                    val question = Question()
                    question.setQuestionId(Integer(q.replace("p","")))
                    questions.add(question)
                }

                if (questions.isNotEmpty()) {
                    mQuestionSize = questions.size
                    mQuestions = questions
                    requestGetQuestion()
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

    fun requestGetQuestionsByExamId(examId : Int) {
// Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(EXAMS_REFERENCE + "/e" + examId)
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                val map = (post as List<String>)

                Log.d(TAG, post.toString())

                /*
                 * mapping map to module object
                 */
                val questions = arrayListOf<Question>()

                // get question id from response
                for (q in map) {
                    val question = Question()
                    question.setQuestionId(Integer(q.replace("p","")))
                    questions.add(question)
                }

                if (questions.isNotEmpty()) {
                    mQuestionSize = questions.size
                    mQuestions = questions
                    requestGetQuestion()
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

    fun requestGetWrongQuestionsByQuestionId(wrongQuestionIds : List<Question>) {

        if (wrongQuestionIds.isNotEmpty()) {
            mQuestionSize = wrongQuestionIds.size
            mQuestions = wrongQuestionIds
            requestGetQuestion()
        } else {
            val error = GenericError()
            onRequestLietenerFailed.onFailed(error)
        }
    }

    private fun requestGetQuestion() {
// Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(COMIPEMS_QUESTIONS_REFERENCE)
        mFirebaseDatabase.keepSynced(true)
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

                getQuestions(mQuestions)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                onRequestLietenerFailed.onFailed(databaseError.toException())
            }
        })
    }

    fun getQuestions(questions: List<Question>) {
        val updatedQuestions = arrayListOf<Question>()

        for (i in 0 .. mQuestions.size - 1) {
            for (question in questions) {
                if (mQuestions.get(i).getQuestionId().equals(question.getQuestionId())) {
                    updatedQuestions.add(question)
                }
            }
        }

        if (updatedQuestions.isNotEmpty()) {
            onRequestListenerSucces.onSuccess(updatedQuestions)
        } else {
            val error = GenericError()
            onRequestLietenerFailed.onFailed(error)
        }
    }

}