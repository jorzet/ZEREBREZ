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

package com.zerebrez.zerebrez.services.firebase.practice

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.QuestionNewFormat
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.services.firebase.Engagement
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import java.text.Normalizer
import java.util.HashMap

/**
 * Created by Jorge Zepeda Tinoco on 03/06/18.
 * jorzet.94@gmail.com
 */

private const val TAG: String = "WrongQuestionRequest"

class WrongQuestionRequest(activity: Activity) : Engagement(activity) {

    /*
     * Node references
     */
    private val ANSWERED_QUESTION_REFERENCE : String = "answeredQuestions"

    /*
     * Json keys
     */
    private val IS_PREMIUM_KEY : String = "isPremium"
    private val TIMESTAMP_KEY : String = "timeStamp"
    private val PREMIUM_KEY : String = "premium"
    private val IS_CORRECT_KEY : String = "isCorrect"
    private val SUBJECT_KEY : String = "subject"
    private val CHOOSEN_OPTION_KEY : String = "chosenOption"
    private val COURSE_KEY : String = "course"
    private val PROFILE_KEY : String = "profile"

    /*
     * Database object
     */
    private lateinit var mFirebaseDatabase: DatabaseReference


    fun requestGetWrontQuestionsRefactor(course: String) {
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
                    if (post != null) {
                        val map = (post as HashMap<String, String>)
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

                        if (map.containsKey(ANSWERED_QUESTION_REFERENCE)) {
                            val answeredQuestions = (map.get(ANSWERED_QUESTION_REFERENCE) as kotlin.collections.HashMap<String, String>).get(course) as kotlin.collections.HashMap<String, String>
                            val questions = arrayListOf<QuestionNewFormat>()
                            for (key2 in answeredQuestions.keys) {
                                val questionAnswered = answeredQuestions.get(key2) as HashMap<String, String>
                                val question = QuestionNewFormat()
                                question.questionId = key2
                                for (key3 in questionAnswered.keys) {
                                    if (key3.equals(SUBJECT_KEY)) {
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
                                            limpiarTexto(SubjectType.FCE2.value) -> {
                                                question.subject = SubjectType.FCE2
                                            }
                                        }
                                    } else if (key3.equals(IS_CORRECT_KEY)) {
                                        val isCorrect = questionAnswered.get(key3) as Boolean
                                        question.wasOK = isCorrect
                                    } else if (key3.equals(CHOOSEN_OPTION_KEY)) {
                                        val chosenOption = questionAnswered.get(key3).toString()
                                        question.chosenOption = chosenOption
                                    }
                                }
                                questions.add(question)
                            }
                            user.setAnsweredQuestionsNewFormat(questions)
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