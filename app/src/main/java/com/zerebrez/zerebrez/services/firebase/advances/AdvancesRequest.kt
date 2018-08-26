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

package com.zerebrez.zerebrez.services.firebase.advances

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.zerebrez.zerebrez.models.*
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.services.firebase.Engagement
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import java.util.HashMap
import java.text.Normalizer


private const val TAG: String = "AdvancesRequest"

class AdvancesRequest(activity: Activity) : Engagement(activity) {

    private val USERS_REFERENCE : String = "users"
    private val PROFILE_REFERENCE : String = "profile"
    private val ANSWERED_QUESTION_REFERENCE : String = "answeredQuestions"

    private val IS_PREMIUM_KEY : String = "isPremium"
    private val TIMESTAMP_KEY : String = "timeStamp"
    private val PREMIUM_KEY : String = "premium"
    private val IS_CORRECT_KEY : String = "isCorrect"
    private val INCORRECT_KEY : String = "incorrect"
    private val SUBJECT_KEY : String = "subject"
    private val CHOOSEN_OPTION_KEY : String = "chosenOption"
    private val ANSWERED_EXAM_KEY : String = "answeredExams"
    private val ANSWERED_MODULE_KEY : String = "answeredModules"
    private val CORRECT_KEY : String = "correct"

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


    fun requestGetHitAndMissesAnsweredModulesAndExams() {
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
                            } else if (key.equals(ANSWERED_EXAM_KEY)) {
                                val answeredExams = map.get(key) as HashMap<String, String>
                                val exams = arrayListOf<Exam>()
                                for (key2 in answeredExams.keys) {
                                    val examAnswered = answeredExams.get(key2) as HashMap<String, String>
                                    val exam = Exam()
                                    exam.setExamId(Integer(key2.replace("e", "")))

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

    fun requestGetAverageSubjects() {
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

                        val subjects = arrayListOf<Subject>()

                        val verbalHability = Subject()
                        verbalHability.setSubjectType(SubjectType.VERBAL_HABILITY)
                        var verbalHabylityOK = 0
                        var verbalHabylityTotal = 0

                        val mathematicalHability = Subject()
                        mathematicalHability.setSubjectType(SubjectType.MATHEMATICAL_HABILITY)
                        var mathematicalHabilityOK = 0
                        var mathematicalHabilityTotal = 0

                        val spanish = Subject()
                        spanish.setSubjectType(SubjectType.SPANISH)
                        var spanishOK = 0
                        var spanishTotal = 0

                        val mathematics = Subject()
                        mathematics.setSubjectType(SubjectType.MATHEMATICS)
                        var mathematicsOK = 0
                        var mathematicsTotal = 0

                        val chemistry = Subject()
                        chemistry.setSubjectType(SubjectType.CHEMISTRY)
                        var chemistryOK = 0
                        var chemistryTotal = 0

                        val physics = Subject()
                        physics.setSubjectType(SubjectType.PHYSICS)
                        var physicsOK = 0
                        var physicsTotal = 0

                        val biology = Subject()
                        biology.setSubjectType(SubjectType.BIOLOGY)
                        var biologyOK = 0
                        var biologyTotal = 0

                        val geography = Subject()
                        geography.setSubjectType(SubjectType.GEOGRAPHY)
                        var geographyOK = 0
                        var geographyTotal = 0

                        val mexicoHistory = Subject()
                        mexicoHistory.setSubjectType(SubjectType.MEXICO_HISTORY)
                        var mexicoHistoryOK = 0
                        var mexicoHistoryTotal = 0

                        val universalHistory = Subject()
                        universalHistory.setSubjectType(SubjectType.UNIVERSAL_HISTORY)
                        var universalHistoryOK = 0
                        var universalHistoryTotal = 0

                        val FCE = Subject()
                        FCE.setSubjectType(SubjectType.FCE)
                        var FCEOK = 0
                        var FCETotal = 0

                        if (map.containsKey(ANSWERED_QUESTION_REFERENCE)) {
                            val answeredQuestions = map.get(ANSWERED_QUESTION_REFERENCE) as HashMap<String, String>
                            val questions = arrayListOf<Question>()
                            for (key2 in answeredQuestions.keys) {
                                val questionAnswered = answeredQuestions.get(key2) as HashMap<String, String>

                                if (questionAnswered.containsKey(SUBJECT_KEY)) {
                                    val subjectType = limpiarTexto(questionAnswered.get(SUBJECT_KEY))
                                    when (subjectType) {
                                        limpiarTexto(SubjectType.VERBAL_HABILITY.value) -> {
                                            if (questionAnswered.containsKey(IS_CORRECT_KEY)) {
                                                val isCorrect = questionAnswered.get(IS_CORRECT_KEY) as Boolean
                                                verbalHabylityTotal++
                                                if (isCorrect) verbalHabylityOK++
                                            }
                                        }
                                        limpiarTexto(SubjectType.MATHEMATICAL_HABILITY.value) -> {
                                            if (questionAnswered.containsKey(IS_CORRECT_KEY)) {
                                                val isCorrect = questionAnswered.get(IS_CORRECT_KEY) as Boolean
                                                mathematicalHabilityTotal++
                                                if (isCorrect) mathematicalHabilityOK++
                                            }
                                        }
                                        limpiarTexto(SubjectType.MATHEMATICS.value) -> {
                                            if (questionAnswered.containsKey(IS_CORRECT_KEY)) {
                                                val isCorrect = questionAnswered.get(IS_CORRECT_KEY) as Boolean
                                                mathematicsTotal++
                                                if (isCorrect) mathematicsOK++
                                            }
                                        }
                                        limpiarTexto(SubjectType.SPANISH.value) -> {
                                            if (questionAnswered.containsKey(IS_CORRECT_KEY)) {
                                                val isCorrect = questionAnswered.get(IS_CORRECT_KEY) as Boolean
                                                spanishTotal++
                                                if (isCorrect) spanishOK++
                                            }
                                        }
                                        limpiarTexto(SubjectType.BIOLOGY.value) -> {
                                            if (questionAnswered.containsKey(IS_CORRECT_KEY)) {
                                                val isCorrect = questionAnswered.get(IS_CORRECT_KEY) as Boolean
                                                biologyTotal++
                                                if (isCorrect) biologyOK++
                                            }
                                        }
                                        limpiarTexto(SubjectType.CHEMISTRY.value) -> {
                                            if (questionAnswered.containsKey(IS_CORRECT_KEY)) {
                                                val isCorrect = questionAnswered.get(IS_CORRECT_KEY) as Boolean
                                                chemistryTotal++
                                                if (isCorrect) chemistryOK++
                                            }
                                        }
                                        limpiarTexto(SubjectType.PHYSICS.value) -> {
                                            if (questionAnswered.containsKey(IS_CORRECT_KEY)) {
                                                val isCorrect = questionAnswered.get(IS_CORRECT_KEY) as Boolean
                                                physicsTotal++
                                                if (isCorrect) physicsOK++
                                            }
                                        }
                                        limpiarTexto(SubjectType.GEOGRAPHY.value) -> {
                                            if (questionAnswered.containsKey(IS_CORRECT_KEY)) {
                                                val isCorrect = questionAnswered.get(IS_CORRECT_KEY) as Boolean
                                                geographyTotal++
                                                if (isCorrect) geographyOK++
                                            }
                                        }
                                        limpiarTexto(SubjectType.UNIVERSAL_HISTORY.value) -> {
                                            if (questionAnswered.containsKey(IS_CORRECT_KEY)) {
                                                val isCorrect = questionAnswered.get(IS_CORRECT_KEY) as Boolean
                                                universalHistoryTotal++
                                                if (isCorrect) universalHistoryOK++
                                            }
                                        }
                                        limpiarTexto(SubjectType.MEXICO_HISTORY.value) -> {
                                            if (questionAnswered.containsKey(IS_CORRECT_KEY)) {
                                                val isCorrect = questionAnswered.get(IS_CORRECT_KEY) as Boolean
                                                mexicoHistoryTotal++
                                                if (isCorrect) mexicoHistoryOK++
                                            }
                                        }
                                        limpiarTexto(SubjectType.FCE.value) -> {
                                            if (questionAnswered.containsKey(IS_CORRECT_KEY)) {
                                                val isCorrect = questionAnswered.get(IS_CORRECT_KEY) as Boolean
                                                FCETotal++
                                                if (isCorrect) FCEOK++
                                            }
                                        }
                                    }
                                }


                            }
                        }

                        if (!verbalHabylityTotal.equals(0) && verbalHabylityTotal != 0)
                            verbalHability.setSubjectAverage(((verbalHabylityOK*10)/verbalHabylityTotal).toDouble())
                        else
                            verbalHability.setSubjectAverage(0.0)
                        subjects.add(verbalHability)

                        if (!mathematicalHabilityTotal.equals(0) && mathematicalHabilityTotal != 0)
                            mathematicalHability.setSubjectAverage(((mathematicalHabilityOK*10)/mathematicalHabilityTotal).toDouble())
                        else
                            mathematicalHability.setSubjectAverage(0.0)
                        subjects.add(mathematicalHability)

                        if (!spanishTotal.equals(0) && spanishTotal != 0)
                            spanish.setSubjectAverage(((spanishOK*10)/spanishTotal).toDouble())
                        else
                            spanish.setSubjectAverage(0.0)
                        subjects.add(spanish)

                        if (!mathematicsTotal.equals(0) && mathematicsTotal != 0)
                            mathematics.setSubjectAverage(((mathematicsOK*10)/mathematicsTotal).toDouble())
                        else
                            mathematics.setSubjectAverage(0.0)
                        subjects.add(mathematics)

                        if (!chemistryTotal.equals(0) && chemistryTotal != 0)
                            chemistry.setSubjectAverage(((chemistryOK*10)/chemistryTotal).toDouble())
                        else
                            chemistry.setSubjectAverage(0.0)
                        subjects.add(chemistry)

                        if (!physicsTotal.equals(0) && physicsTotal != 0)
                            physics.setSubjectAverage(((physicsOK*10)/physicsTotal).toDouble())
                        else
                            physics.setSubjectAverage(0.0)
                        subjects.add(physics)

                        if (!biologyTotal.equals(0) && biologyTotal != 0)
                            biology.setSubjectAverage(((biologyOK*10)/biologyTotal).toDouble())
                        else
                            biology.setSubjectAverage(0.0)
                        subjects.add(biology)

                        if (!geographyTotal.equals(0) && geographyTotal != 0)
                            geography.setSubjectAverage(((geographyOK*10)/geographyTotal).toDouble())
                        else
                            geography.setSubjectAverage(0.0)
                        subjects.add(geography)

                        if (!mexicoHistoryTotal.equals(0) && mexicoHistoryTotal != 0)
                            mexicoHistory.setSubjectAverage(((mexicoHistoryOK*10)/mexicoHistoryTotal).toDouble())
                        else
                            mexicoHistory.setSubjectAverage(0.0)
                        subjects.add(mexicoHistory)

                        if (!universalHistoryTotal.equals(0) && universalHistoryTotal != 0)
                            universalHistory.setSubjectAverage(((universalHistoryOK*10)/universalHistoryTotal).toDouble())
                        else
                            universalHistory.setSubjectAverage(0.0)
                        subjects.add(universalHistory)

                        if (!FCETotal.equals(0) && FCETotal != 0)
                            FCE.setSubjectAverage(((FCEOK*10)/FCETotal).toDouble())
                        else
                            FCE.setSubjectAverage(0.0)
                        subjects.add(FCE)

                        onRequestListenerSucces.onSuccess(subjects)
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