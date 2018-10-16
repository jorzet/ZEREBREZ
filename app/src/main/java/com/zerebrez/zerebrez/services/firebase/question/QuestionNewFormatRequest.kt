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
import com.google.gson.Gson
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.QuestionNewFormat
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.services.firebase.Engagement
import org.json.JSONObject
import java.text.Normalizer
import java.util.ArrayList

private const val TAG: String = "QuestionsRequest"

class QuestionNewFormatRequest(activity: Activity) : Engagement(activity) {

    private val COMIPEMS_QUESTIONS_NEW_FORMAT_REFERENCE : String = "questions/newFormat/comipems"
    private val MODULES_REFERENCE : String = "modules/comipems"
    private var SUBJECT_REFERENCE : String = "questionsInSubjects/comipems"
    private val EXAMS_REFERENCE : String = "exams/comipems"
    private val ANSWERED_QUESTION_REFERENCE : String = "answeredQuestions"
    private val USERS_REFERENCE : String = "users"

    private val IS_PREMIUM_KEY : String = "isPremium"
    private val TIMESTAMP_KEY : String = "timeStamp"
    private val PREMIUM_KEY : String = "premium"
    private val IS_CORRECT_KEY : String = "isCorrect"
    private val SUBJECT_KEY : String = "subject"
    private val CHOOSEN_OPTION_KEY : String = "chosenOption"

    private lateinit var mQuestions : List<QuestionNewFormat>
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

    fun requestGetQuestionsNewFormatByModuleId(moduleId : Int) {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(MODULES_REFERENCE + "/m" + moduleId)
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as List<String>)

                    Log.d(TAG, post.toString())

                    /*
                     * mapping map to question array object
                     */

                    val questions = arrayListOf<QuestionNewFormat>()

                    // get question id from response
                    for (q in map) {
                        val question = QuestionNewFormat()
                        question.questionId = q
                        questions.add(question)
                    }

                    if (questions.isNotEmpty()) {
                        mQuestionSize = questions.size
                        mQuestions = questions
                        requestQuestionsNewFormat()
                    } else {
                        val error = GenericError()
                        onRequestLietenerFailed.onFailed(error)
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

    fun requestGetQuestionNewFormatBySubject(subject: String) {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(SUBJECT_REFERENCE + "/" + subject)
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as List<String>)

                    Log.d(TAG, post.toString())

                    /*
                 * mapping map to module object
                 */
                    val questions = arrayListOf<QuestionNewFormat>()

                    // get question id from response
                    for (q in map) {
                        val question = QuestionNewFormat()
                        question.questionId = q
                        questions.add(question)
                    }

                    if (questions.isNotEmpty()) {
                        mQuestionSize = questions.size
                        mQuestions = questions
                        requestQuestionsNewFormat()
                    } else {
                        val error = GenericError()
                        onRequestLietenerFailed.onFailed(error)
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

    fun requestGetQuestionsNewFormatByExamId(examId : Int) {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(EXAMS_REFERENCE + "/e" + examId)
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as List<String>)

                    Log.d(TAG, post.toString())

                    /*
                 * mapping map to module object
                 */
                    val questions = arrayListOf<QuestionNewFormat>()

                    // get question id from response
                    for (q in map) {
                        val question = QuestionNewFormat()
                        question.questionId = q
                        questions.add(question)
                    }

                    if (questions.isNotEmpty()) {
                        mQuestionSize = questions.size
                        mQuestions = questions
                        requestQuestionsNewFormat()
                    } else {
                        val error = GenericError()
                        onRequestLietenerFailed.onFailed(error)
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

    fun requestGetSubjectQuestionsNewFormatBySubjectQuestionId(subjectQuestionNewFormatIds : List<QuestionNewFormat>) {
        if (subjectQuestionNewFormatIds.isNotEmpty()) {
            mQuestionSize = subjectQuestionNewFormatIds.size
            mQuestions = subjectQuestionNewFormatIds
            requestQuestionsNewFormat()
        } else {
            val error = GenericError()
            onRequestLietenerFailed.onFailed(error)
        }
    }

    fun requestGetWrongQuestionsNewFormatByQuestionId(wrongQuestionNewFormatIds : List<QuestionNewFormat>) {

        if (wrongQuestionNewFormatIds.isNotEmpty()) {
            mQuestionSize = wrongQuestionNewFormatIds.size
            mQuestions = wrongQuestionNewFormatIds
            requestQuestionsNewFormat()
        } else {
            val error = GenericError()
            onRequestLietenerFailed.onFailed(error)
        }
    }

    fun requestQuestionsNewFormat() {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(COMIPEMS_QUESTIONS_NEW_FORMAT_REFERENCE)
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as java.util.HashMap<*, *>)
                    Log.d(TAG, "user data ------ " + map.size)
                    val mQuestions = ArrayList<QuestionNewFormat>()
                    for (key in map.keys) {
                        val questionMap = map.get(key) as HashMap<*, *>
                        val question = Gson().fromJson(JSONObject(questionMap).toString(), QuestionNewFormat::class.java)
                        question.questionId = key.toString()
                        if (questionMap.containsKey("subject")) {
                            val subject = limpiarTexto(questionMap.get("subject") as String)
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
                                limpiarTexto(SubjectType.FCE2.value) -> {
                                    question.subject = SubjectType.FCE2
                                }
                            }
                        }
                        mQuestions.add(question)
                    }

                    getQuestionsNewFormat(mQuestions)
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

    fun getQuestionsNewFormat(questions: List<QuestionNewFormat>) {
        val updatedQuestions = arrayListOf<QuestionNewFormat>()

        for (i in 0 .. mQuestions.size - 1) {
            for (question in questions) {
                if (mQuestions.get(i).questionId.equals(question.questionId)) {
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