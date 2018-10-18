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

package com.zerebrez.zerebrez.fragments.practice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.QuestionNewFormat
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.ui.activities.BaseActivityLifeCycle
import com.zerebrez.zerebrez.ui.activities.ContentActivity
import com.zerebrez.zerebrez.ui.activities.QuestionActivity
import java.text.Normalizer

/**
 * Created by Jorge Zepeda Tinoco on 01/05/18.
 * jorzet.94@gmail.com
 */

class StudyWrongQuestionFragment : BaseContentFragment() {

    /*
     * Tags
     */
    private val TAG : String = "StudyWrongQuestionF"
    private var CURRENT_COURSE : String = "current_course"
    private val MODULE_ID : String = "module_id"
    private val QUESTION_ID : String = "question_id"
    private val ANONYMOUS_USER : String = "anonymous_user"
    private val FROM_WRONG_QUESTION : String = "from_wrong_question"
    private val WRONG_QUESTIONS_LIST = "wrong_questions_list"

    /*
     * UI accessors
     */
    private lateinit var mLeftTableLayout : GridLayout
    private lateinit var mCenterTableLayout : GridLayout
    private lateinit var mRightTableLayout : GridLayout
    private lateinit var mNotWrongQuestionsCurrently : TextView
    private lateinit var mMainContainer : View

    /*
     * Data accessor
     */
    private lateinit var mDataHelper : DataHelper

    /*
     * Variables
     */
    private var mTotalQuestions : Int = 0

    /*
     * Objects
     */
    private var mQuestionList = arrayListOf<QuestionNewFormat>()
    private var mUpdatedQuestions = arrayListOf<QuestionNewFormat>()
    private var mWrongQuestionsId = arrayListOf<Int>()
    private lateinit var mUser : User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.study_wrong_question_fragment, container, false)!!

        mLeftTableLayout = rootView.findViewById(R.id.table_left)
        mCenterTableLayout = rootView.findViewById(R.id.table_center)
        mRightTableLayout = rootView.findViewById(R.id.table_right)
        mNotWrongQuestionsCurrently = rootView.findViewById(R.id.tv_not_wrong_questions_currently)
        mMainContainer = rootView.findViewById(R.id.sv_main_container)

        if (activity != null) {
            val user = (activity as ContentActivity).getUserProfile()
            if (user != null && !user.getCourse().equals("")) {
                requestGetWrongQuestionsAndProfileRefactor(user.getCourse())
            }
        }

        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode.equals(BaseActivityLifeCycle.SHOW_QUESTION_RESULT_CODE)) {
            if (resultCode.equals(BaseActivityLifeCycle.UPDATE_WRONG_QUESTIONS_RESULT_CODE)) {
                if (activity != null) {
                    val user = (activity as ContentActivity).getUserProfile()
                    if (user != null && !user.getCourse().equals("")) {
                        requestGetWrongQuestionsAndProfileRefactor(user.getCourse())
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (activity != null) {
            val user = (activity as ContentActivity).getUserProfile()
            if (user != null && !user.getCourse().equals("")) {
                requestGetWrongQuestionsAndProfileRefactor(user.getCourse())
            }
        }
    }

    private fun resetValues() {
        mWrongQuestionsId.clear()
        mWrongQuestionsId = arrayListOf()

        mUpdatedQuestions.clear()
        mUpdatedQuestions = arrayListOf()
        mQuestionList.clear()
        mQuestionList = arrayListOf()
        mLeftTableLayout.removeAllViews()
        mCenterTableLayout.removeAllViews()
        mRightTableLayout.removeAllViews()
    }

    private fun updateQuestionList(questions : List<QuestionNewFormat>) {
        var row = 1
        for (i in 0 .. questions.size - 1) {
            when (row) {
                1 -> {
                    mQuestionList.add(questions.get(i))
                    val nothing = QuestionNewFormat()
                    nothing.questionId = "-1"
                    mQuestionList.add(nothing)
                    val padding = QuestionNewFormat()
                    padding.questionId = "-2"
                    mQuestionList.add(padding)
                }
                2 -> {
                    val nothing = QuestionNewFormat()
                    nothing.questionId = "-1"
                    mQuestionList.add(nothing)
                    mQuestionList.add(questions.get(i))
                    val padding = QuestionNewFormat()
                    padding.questionId = "-1"
                    mQuestionList.add(padding)
                }
                3 -> {
                    val nothing = QuestionNewFormat()
                    nothing.questionId = "-2"
                    mQuestionList.add(nothing)
                    val padding = QuestionNewFormat()
                    padding.questionId = "-1"
                    mQuestionList.add(padding)
                    mQuestionList.add(questions.get(i))
                }
                4 -> {
                    val nothing = QuestionNewFormat()
                    nothing.questionId = "-1"
                    mQuestionList.add(nothing)
                    mQuestionList.add(questions.get(i))
                    val padding = QuestionNewFormat()
                    padding.questionId = "-1"
                    mQuestionList.add(padding)
                }
            }
            row++
            if(row == 5)
                row = 1
        }

    }

    private fun drawQuestions() {

        var cnt : Int = 0

        for (i in 0 .. mQuestionList.size - 1) {

            val view = LayoutInflater.from(context).inflate(R.layout.custom_wrong_question, null, false)
            val image : ImageView = view.findViewById(R.id.image)

            val number = mQuestionList.get(i).questionId.replace("p","")

            // params for module
            val param = GridLayout.LayoutParams()

            if (number.equals("-1")) {
                view.background = resources.getDrawable(R.drawable.empty_square)
                image.visibility = View.GONE
                param.height = resources.getDimension(R.dimen.height_empty_square).toInt()
                param.width = resources.getDimension(R.dimen.width_empty_square).toInt()
                param.bottomMargin = 2
                param.rightMargin = 2
                param.leftMargin = 2
                param.topMargin = 2
                param.setGravity(Gravity.CENTER)
            } else if(number.equals("-2")) {
                val randomNumber = Math.random()
                var rand = 0
                if (randomNumber > 0.5) {
                    rand = 1
                }
                if (rand.equals(0)) {
                    view.background = resources.getDrawable(R.drawable.square_first_module_background)
                } else {
                    view.background = resources.getDrawable(R.drawable.square_second_module_background)
                }
                image.visibility = View.GONE
                param.height = resources.getDimension(R.dimen.height_square).toInt()
                param.width = resources.getDimension(R.dimen.width_square).toInt()
                param.bottomMargin = 2
                param.rightMargin = 2
                param.leftMargin = 2
                param.topMargin = 2
                param.setGravity(Gravity.CENTER)
            } else {
                val currentQuestion = mQuestionList.get(i)
                val subject = limpiarTexto(currentQuestion.subject.value)
                when (subject) {
                    limpiarTexto(SubjectType.MATHEMATICS.value) -> {
                        image.background = resources.getDrawable(R.drawable.mat_1_subject_icon_white)
                    }
                    limpiarTexto(SubjectType.SPANISH.value) -> {
                        image.background = resources.getDrawable(R.drawable.esp_subject_icon_white)
                    }
                    limpiarTexto(SubjectType.VERBAL_HABILITY.value) -> {
                        image.background = resources.getDrawable(R.drawable.hab_ver_subject_icon_white)
                    }
                    limpiarTexto(SubjectType.MATHEMATICAL_HABILITY.value) -> {
                        image.background = resources.getDrawable(R.drawable.hab_mat_subject_icon_white)
                    }
                    limpiarTexto(SubjectType.BIOLOGY.value) -> {
                        image.background = resources.getDrawable(R.drawable.bio_subject_icon_white)
                    }
                    limpiarTexto(SubjectType.CHEMISTRY.value) -> {
                        image.background = resources.getDrawable(R.drawable.quim_subject_icon_white)
                    }
                    limpiarTexto(SubjectType.PHYSICS.value) -> {
                        image.background = resources.getDrawable(R.drawable.fis_subject_icon_white)
                    }
                    limpiarTexto(SubjectType.GEOGRAPHY.value) -> {
                        image.background = resources.getDrawable(R.drawable.geo_subject_icon_white)
                    }
                    limpiarTexto(SubjectType.MEXICO_HISTORY.value) -> {
                        image.background = resources.getDrawable(R.drawable.his_mex_subject_icon_white)
                    }
                    limpiarTexto(SubjectType.UNIVERSAL_HISTORY.value) -> {
                        image.background = resources.getDrawable(R.drawable.his_subject_icon_white)
                    }
                    limpiarTexto(SubjectType.FCE.value) -> {
                        image.background = resources.getDrawable(R.drawable.civ_et_subject_icon_white)
                    }
                    limpiarTexto(SubjectType.FCE2.value) -> {
                        image.background = resources.getDrawable(R.drawable.civ_et_subject_icon_white)
                    }
                    limpiarTexto(SubjectType.NONE.value) -> {
                        //image.background = resources.getDrawable(R.drawable.main_icon)
                    }
                }

                param.height = resources.getDimension(R.dimen.height_square).toInt()
                param.width = resources.getDimension(R.dimen.width_square).toInt()
                param.bottomMargin = 2
                param.rightMargin = 2
                param.leftMargin = 2
                param.topMargin = 2
                param.setGravity(Gravity.CENTER)
                mWrongQuestionsId.add(Integer.parseInt(currentQuestion.questionId.replace("p","")))
                view.setOnClickListener(View.OnClickListener {
                    Log.d(TAG, "onClick: number --- " + number)
                    goQuestionActivity(Integer.parseInt(number))
                })
            }



            when (cnt) {
                0 -> {
                    mLeftTableLayout.addView(view)
                    view.setLayoutParams(param)
                }
                1 -> {
                    mCenterTableLayout.addView(view)
                    view.setLayoutParams(param)
                }
                2 -> {
                    mRightTableLayout.addView(view)
                    view.setLayoutParams(param)
                }
            }

            cnt++
            if (cnt==3) { cnt = 0 }
        }

    }

    private fun goQuestionActivity(questionId : Int) {
        val intent = Intent(activity, QuestionActivity::class.java)
        intent.putExtra(QUESTION_ID, questionId)
        intent.putExtra(ANONYMOUS_USER, false)
        intent.putExtra(FROM_WRONG_QUESTION, true)
        intent.putExtra(WRONG_QUESTIONS_LIST, mWrongQuestionsId)
        if (activity != null) {
            val user = (activity as ContentActivity).getUserProfile()
            if (user != null && !user.getCourse().equals("")) {
                intent.putExtra(CURRENT_COURSE, user.getCourse())
            }
        }
        this.startActivityForResult(intent, BaseActivityLifeCycle.SHOW_QUESTION_RESULT_CODE)
    }

    override fun onGetWrongQuestionsAndProfileRefactorSuccess(user: User) {
        super.onGetWrongQuestionsAndProfileRefactorSuccess(user)
        try {
            if (context != null) {
                mUser = user
                saveUser(user)
                val answeredQuestion = user.getAnsweredQuestionNewFormat()

                resetValues()
                for (i in 0..answeredQuestion.size - 1) {
                    if (!answeredQuestion.get(i).wasOK) {
                        mUpdatedQuestions.add(answeredQuestion.get(i))
                    }
                }

                updateQuestionList(mUpdatedQuestions)
                drawQuestions()
            }
            if (activity != null)
                (activity as ContentActivity).showLoading(false)
        } catch (exception : Exception) {}
    }

    override fun onGetWrongQuestionsAndProfileRefactorFail(throwable: Throwable) {
        super.onGetWrongQuestionsAndProfileRefactorFail(throwable)
        if (activity != null)
            (activity as ContentActivity).showLoading(false)
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