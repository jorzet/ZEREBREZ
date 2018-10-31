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

package com.zerebrez.zerebrez.services.firebase.subject

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.google.gson.Gson
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.SubjectRefactor
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.services.firebase.Engagement
import org.json.JSONObject
import java.text.Normalizer
import java.util.*

/**
 * Created by Jorge Zepeda Tinoco on 20/09/18.
 * jorzet.94@gmail.com
 */

class SubjectRequest(activity: Activity) : Engagement(activity) {

    private val TAG: String = "SubjectRequest"
    private val COURSE_LABEL : String = "course_label"
    private val SUBJECT_REFERENCE: String = "subjects/course_label"

    private val mActivity: Activity = activity
    private lateinit var mFirebaseDatabase: DatabaseReference
    private var mFirebaseInstance: FirebaseDatabase

    init {
        mFirebaseInstance = FirebaseDatabase.getInstance()
    }

    fun requestGetSubjects(course: String) {
        mFirebaseDatabase = mFirebaseInstance.getReference(SUBJECT_REFERENCE.replace(COURSE_LABEL, course))
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as HashMap<*, *>)
                    Log.d(TAG, "user data ------ " + map.size)
                    val subjects = ArrayList<SubjectRefactor>()
                    for (key in map.keys) {
                        val subjectMap = map.get(key) as HashMap<*, *>
                        val subject = Gson().fromJson(JSONObject(subjectMap).toString(), SubjectRefactor::class.java)
                        subject.subjectId = key.toString()

                        if (subjectMap.containsKey("nameToDisplay")) {
                            val subjectClean = limpiarTexto(subjectMap.get("nameToDisplay") as String)
                            when (subjectClean) {
                                limpiarTexto(SubjectType.VERBAL_HABILITY.value) -> {
                                    subject.subjectType = SubjectType.VERBAL_HABILITY
                                }
                                limpiarTexto(SubjectType.MATHEMATICAL_HABILITY.value) -> {
                                    subject.subjectType = SubjectType.MATHEMATICAL_HABILITY
                                }
                                limpiarTexto(SubjectType.MATHEMATICS.value) -> {
                                    subject.subjectType = SubjectType.MATHEMATICS
                                }
                                limpiarTexto(SubjectType.SPANISH.value) -> {
                                    subject.subjectType = SubjectType.SPANISH
                                }
                                limpiarTexto(SubjectType.BIOLOGY.value) -> {
                                    subject.subjectType = SubjectType.BIOLOGY
                                }
                                limpiarTexto(SubjectType.CHEMISTRY.value) -> {
                                    subject.subjectType = SubjectType.CHEMISTRY
                                }
                                limpiarTexto(SubjectType.PHYSICS.value) -> {
                                    subject.subjectType = SubjectType.PHYSICS
                                }
                                limpiarTexto(SubjectType.GEOGRAPHY.value) -> {
                                    subject.subjectType = SubjectType.GEOGRAPHY
                                }
                                limpiarTexto(SubjectType.UNIVERSAL_HISTORY.value) -> {
                                    subject.subjectType = SubjectType.UNIVERSAL_HISTORY
                                }
                                limpiarTexto(SubjectType.MEXICO_HISTORY.value) -> {
                                    subject.subjectType = SubjectType.MEXICO_HISTORY
                                }
                                limpiarTexto(SubjectType.FCE.value) -> {
                                    subject.subjectType = SubjectType.FCE
                                }
                                limpiarTexto(SubjectType.FCE2.value) -> {
                                    subject.subjectType = SubjectType.FCE2
                                }
                            }
                        }
                        subjects.add(subject)
                    }

                    Collections.sort(subjects, object : Comparator<SubjectRefactor> {
                        override fun compare(o1: SubjectRefactor, o2: SubjectRefactor): Int {
                            return extractInt(o1) - extractInt(o2)
                        }

                        fun extractInt(s: SubjectRefactor): Int {
                            val num = s.subjectId.replace("s", "").replace("S", "")
                            // return 0 if no digits found
                            return if (num.isEmpty()) 0 else Integer.parseInt(num)
                        }
                    })

                    Log.d(TAG, "subject data ------ ")
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

