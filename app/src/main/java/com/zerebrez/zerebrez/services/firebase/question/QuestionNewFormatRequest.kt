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

/**
 * Created by Jorge Zepeda Tinoco on 03/06/18.
 * jorzet.94@gmail.com
 */

private const val TAG: String = "QuestionsRequest"

class QuestionNewFormatRequest(activity: Activity) : Engagement(activity) {

    /*
     * Labels to be replaced
     */
    private val COURSE_LABEL : String = "course_label"
    private val SUBJECT_LABEL : String = "subject_label"
    private val QUESTION_ID : String = "question_id"

    /*
     * Node references
     */
    private val COMIPEMS_QUESTION_NEW_FORMAT_REFERENCE : String = "course_label/question_id"
    private val COMIPEMS_QUESTIONS_NEW_FORMAT_REFERENCE : String = "course_label"
    private val MODULES_REFERENCE : String = "modules/course_label"
    private var SUBJECT_REFERENCE : String = "questionsInSubjects/course_label/subject_label"
    private val EXAMS_REFERENCE : String = "exams/course_label"

    /*
     * Variables
     */
    private lateinit var mQuestions : List<QuestionNewFormat>
    private var mQuestionSize : Int = 0

    /*
     * Database object
     */
    private lateinit var mFirebaseDatabase: DatabaseReference

    /*******************************************************************************************/
    /**************************   This section is to get all questions  ************************/
    /*******************************************************************************************/

    fun requestGetQuestionsNewFormatByModuleId(moduleId : Int, course: String) {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance()
                .getReference(MODULES_REFERENCE.replace(COURSE_LABEL, course) + "/m" + moduleId)

        mFirebaseDatabase.keepSynced(true)

        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as List<String>)

                    val questionsId = arrayListOf<String>()
                    // get question id from response
                    for (q in map) {
                        questionsId.add(q)
                    }

                    if (questionsId.isNotEmpty()) {
                        onRequestListenerSucces.onSuccess(questionsId)
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

    fun requestGetQuestionsNewFormatByExamId(examId : Int, course: String) {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance()
                .getReference(EXAMS_REFERENCE.replace(COURSE_LABEL, course) + "/e" + examId)

        mFirebaseDatabase.keepSynced(true)

        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as List<String>)

                    val questionsId = arrayListOf<String>()

                    // get question id from response
                    for (q in map) {
                        questionsId.add(q)
                    }

                    if (questionsId.isNotEmpty()) {
                        onRequestListenerSucces.onSuccess(questionsId)
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

    fun requestGetQuestionNewFormatBySubject(subject: String, course: String) {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance()
                .getReference(SUBJECT_REFERENCE.replace(COURSE_LABEL, course).replace(SUBJECT_LABEL, subject))

        mFirebaseDatabase.keepSynced(true)

        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as List<String>)

                    Log.d(TAG, post.toString())

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
                        onRequestListenerSucces.onSuccess(questions)
                        //requestQuestionsNewFormat(course)
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

    fun requestGetSubjectQuestionsNewFormatBySubjectQuestionId(subjectQuestionNewFormatIds : List<QuestionNewFormat>, course: String) {
        if (subjectQuestionNewFormatIds.isNotEmpty()) {
            mQuestionSize = subjectQuestionNewFormatIds.size
            mQuestions = subjectQuestionNewFormatIds
            requestQuestionsNewFormat(course)
        } else {
            val error = GenericError()
            onRequestLietenerFailed.onFailed(error)
        }
    }

    fun requestGetWrongQuestionsNewFormatByQuestionId(wrongQuestionNewFormatIds : List<QuestionNewFormat>, course: String) {

        if (wrongQuestionNewFormatIds.isNotEmpty()) {
            mQuestionSize = wrongQuestionNewFormatIds.size
            mQuestions = wrongQuestionNewFormatIds
            requestQuestionsNewFormat(course)
        } else {
            val error = GenericError()
            onRequestLietenerFailed.onFailed(error)
        }
    }

    fun requestQuestionsNewFormat(course: String) {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.QUESTIONS_DATABASE_REFERENCE)
                .getReference(COMIPEMS_QUESTIONS_NEW_FORMAT_REFERENCE.replace(COURSE_LABEL, course))
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

    /*******************************************************************************************/
    /**********************   This section is to get question by question   ********************/
    /*******************************************************************************************/

    fun requestQuestionNewFormat(questionId: String, course: String) {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.QUESTIONS_DATABASE_REFERENCE)
                .getReference(COMIPEMS_QUESTION_NEW_FORMAT_REFERENCE.replace(COURSE_LABEL, course).replace(QUESTION_ID, questionId))

        mFirebaseDatabase.keepSynced(true)

        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val questionMap = (post as java.util.HashMap<*, *>)
                    Log.d(TAG, "user data ------ " + questionMap.size)

                    val question = Gson().fromJson(JSONObject(questionMap).toString(), QuestionNewFormat::class.java)
                    question.questionId = questionId
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

                    onRequestListenerSucces.onSuccess(question)
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

    /*
     * This method clear non ascii chars
     */
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