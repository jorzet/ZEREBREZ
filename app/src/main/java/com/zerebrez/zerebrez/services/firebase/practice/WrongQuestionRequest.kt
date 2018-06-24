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
import com.zerebrez.zerebrez.models.Question
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.services.firebase.Engagement
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import java.text.Normalizer
import java.util.HashMap

private const val TAG: String = "WrongQuestionRequest"

class WrongQuestionRequest(activity: Activity) : Engagement(activity) {

    private val USERS_REFERENCE : String = "users"
    private val PROFILE_REFERENCE : String = "profile"
    private val ANSWERED_QUESTION_REFERENCE : String = "answeredQuestions"

    private val IS_PREMIUM_KEY : String = "isPremium"
    private val TIMESTAMP_KEY : String = "timeStamp"
    private val PREMIUM_KEY : String = "premium"
    private val IS_CORRECT_KEY : String = "isCorrect"
    private val SUBJECT_KEY : String = "subject"
    private val CHOOSEN_OPTION_KEY : String = "chosenOption"

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

    fun requestGetWrontQuestionsRefactor() {
        // Get a reference to our posts
        val user = getCurrentUser()
        if (user != null) {
            mFirebaseDatabase = mFirebaseInstance.getReference(USERS_REFERENCE + "/" + user.uid)
            mFirebaseDatabase.keepSynced(true)

            // Attach a listener to read the data at our posts reference
            mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val post = dataSnapshot.getValue()
                    if (post != null) {
                        val map = (post as HashMap<String, String>)
                        Log.d(TAG, "user data ------ " + map.size)

                        val user = User()
                        for (key in map.keys) {
                            println(key)
                            if (key.equals(PROFILE_REFERENCE)) {
                                val profile = map.get(key) as HashMap<String, String>
                                if (profile.containsKey(PREMIUM_KEY)) {
                                    val premiumHash = profile.get(PREMIUM_KEY) as java.util.HashMap<String, String>

                                    if (premiumHash.containsKey(IS_PREMIUM_KEY)) {
                                        val isPremium = premiumHash.get(IS_PREMIUM_KEY) as Boolean
                                        user.setPremiumUser(isPremium)
                                    }

                                    if (premiumHash.containsKey(TIMESTAMP_KEY)) {
                                        val timeStamp = premiumHash.get(TIMESTAMP_KEY) as Long
                                        user.setTimeStamp(timeStamp)
                                    }
                                }

                            } else if (key.equals(ANSWERED_QUESTION_REFERENCE)) {
                                val answeredQuestions = map.get(key) as HashMap<String, String>
                                val questions = arrayListOf<Question>()
                                for (key2 in answeredQuestions.keys) {
                                    val questionAnswered = answeredQuestions.get(key2) as HashMap<String, String>
                                    val question = Question()
                                    question.setQuestionId(Integer(key2.replace("p", "").replace("q", "")))
                                    for (key3 in questionAnswered.keys) {
                                        if (key3.equals(SUBJECT_KEY)) {
                                            val subject = limpiarTexto(questionAnswered.get(key3))
                                            when (subject) {
                                                limpiarTexto(SubjectType.VERBAL_HABILITY.value) -> {
                                                    question.setSubjectType(SubjectType.VERBAL_HABILITY)
                                                }
                                                limpiarTexto(SubjectType.MATHEMATICAL_HABILITY.value) -> {
                                                    question.setSubjectType(SubjectType.MATHEMATICAL_HABILITY)
                                                }
                                                limpiarTexto(SubjectType.MATHEMATICS.value) -> {
                                                    question.setSubjectType(SubjectType.MATHEMATICS)
                                                }
                                                limpiarTexto(SubjectType.SPANISH.value) -> {
                                                    question.setSubjectType(SubjectType.SPANISH)
                                                }
                                                limpiarTexto(SubjectType.BIOLOGY.value) -> {
                                                    question.setSubjectType(SubjectType.BIOLOGY)
                                                }
                                                limpiarTexto(SubjectType.CHEMISTRY.value) -> {
                                                    question.setSubjectType(SubjectType.CHEMISTRY)
                                                }
                                                limpiarTexto(SubjectType.PHYSICS.value) -> {
                                                    question.setSubjectType(SubjectType.PHYSICS)
                                                }
                                                limpiarTexto(SubjectType.GEOGRAPHY.value) -> {
                                                    question.setSubjectType(SubjectType.GEOGRAPHY)
                                                }
                                                limpiarTexto(SubjectType.UNIVERSAL_HISTORY.value) -> {
                                                    question.setSubjectType(SubjectType.UNIVERSAL_HISTORY)
                                                }
                                                limpiarTexto(SubjectType.MEXICO_HISTORY.value) -> {
                                                    question.setSubjectType(SubjectType.MEXICO_HISTORY)
                                                }
                                                limpiarTexto(SubjectType.FCE.value) -> {
                                                    question.setSubjectType(SubjectType.FCE)
                                                }
                                                limpiarTexto(SubjectType.FCE2.value) -> {
                                                    question.setSubjectType(SubjectType.FCE2)
                                                }
                                            }
                                        } else if (key3.equals(IS_CORRECT_KEY)) {
                                            val isCorrect = questionAnswered.get(key3) as Boolean
                                            question.setWasOK(isCorrect)
                                        } else if (key3.equals(CHOOSEN_OPTION_KEY)) {
                                            val chosenOption = questionAnswered.get(key3).toString()
                                            question.setOptionChoosed(chosenOption)
                                        }
                                    }
                                    questions.add(question)
                                }
                                user.setAnsweredQuestions(questions)
                            }
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