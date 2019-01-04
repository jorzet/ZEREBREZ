/*
 * Copyright [2019] [Jorge Zepeda Tinoco]
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
import android.widget.*
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.QuestionNewFormat
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.ui.activities.BaseActivityLifeCycle
import com.zerebrez.zerebrez.ui.activities.ContentActivity
import com.zerebrez.zerebrez.ui.activities.QuestionActivity
import com.zerebrez.zerebrez.utils.FontUtil
import kotlinx.android.synthetic.main.custom_subject_question.view.*
import kotlinx.android.synthetic.main.init_fragment.*
import java.text.Normalizer

/**
 * Created by Jorge Zepeda Tinoco on 04/01/19.
 * jorzet.94@gmail.com
 */

class StudySubjectQuestionsFragment : BaseContentFragment() {

    /*
     * Tags
     */
    private val TAG : String = "StudySubjectQue"
    private var CURRENT_COURSE : String = "current_course"
    private val QUESTION_ID : String = "question_id"
    private val FROM_SUBJECT_QUESTION : String = "from_subject_question"
    private val SUBJECT_QUESTIONS_LIST : String = "subject_questions_list"
    private val SELECTED_SUBJECT : String = "selected_subject"
    private val ANONYMOUS_USER : String = "anonymous_user"
    private val SUBJECT_EXTRA : String = "subject_extra"

    /*
     * UI accessors
     */
    private lateinit var mLeftTableLayout : GridLayout
    private lateinit var mCenterTableLayout : GridLayout
    private lateinit var mRightTableLayout : GridLayout
    private lateinit var mNotSubjectQuestionsCurrently : TextView
    private lateinit var mMainContainer : View
    private lateinit var mLoadingQuestionSubject : ProgressBar
    private lateinit var mShowMoreQuestionsButton: View
    private lateinit var mShowMoreQuestionsTextView: TextView

    /*
     * Variables
     */
    private var mTotalQuestionsToShow : Int = 20

    /*
     * Objects
     */
    private var mQuestionList = arrayListOf<QuestionNewFormat>()
    private var mUpdatedQuestions = arrayListOf<QuestionNewFormat>()
    private var mSubjectQuestionsId = arrayListOf<String>()
    private lateinit var mUser : User

    /*
     * attibutes
     */
    private var mSubject : String = ""
    private lateinit var mSelectedSubject : String
    private lateinit var mSelectedSubjectType : SubjectType
    private var mNumberOfFreeQuestionSubject : Long = 0
    private var isRequesting : Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.study_subject_question_fragment, container, false)!!

        mLeftTableLayout = rootView.findViewById(R.id.table_left)
        mCenterTableLayout = rootView.findViewById(R.id.table_center)
        mRightTableLayout = rootView.findViewById(R.id.table_right)
        mNotSubjectQuestionsCurrently = rootView.findViewById(R.id.tv_not_subject_questions_currently)
        mMainContainer = rootView.findViewById(R.id.sv_main_container)
        mLoadingQuestionSubject = rootView.findViewById(R.id.pb_loading_question_subjects)
        mShowMoreQuestionsButton = rootView.findViewById(R.id.rl_show_more_button)
        mShowMoreQuestionsTextView = rootView.findViewById(R.id.tv_show_more)

        mShowMoreQuestionsTextView.typeface = FontUtil.getNunitoSemiBold(context!!)

        mShowMoreQuestionsButton.setOnClickListener(mShowMoreQuestionsListener)

        if (::mSelectedSubject.isInitialized && mSelectedSubject != null && !mSelectedSubject.equals("")) {
            mLoadingQuestionSubject.visibility = View.VISIBLE
            /*if (activity != null)
                (activity as ContentActivity).showLoading(true)*/
            val user = getUser()
            if (user != null && !user.getCourse().equals("")) {
                isRequesting = true
                requestGetQuestionsNewFormatBySubject(mSelectedSubject, user.getCourse())
            }
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()
        if (::mSelectedSubject.isInitialized && mSelectedSubject != null && !mSelectedSubject.equals("")) {
            mLoadingQuestionSubject.visibility = View.VISIBLE
            /*if (activity != null)
                (activity as ContentActivity).showLoading(true)*/
            if (!isRequesting) {
                isRequesting = true
                val user = getUser()
                if (user != null && !user.getCourse().equals("")) {
                    requestGetQuestionsNewFormatBySubject(mSelectedSubject, user.getCourse())
                }
            }
        }
    }

    fun setSelectedSubject(selectedSubject: String) {
        this.mSelectedSubject = selectedSubject
    }

    fun setSelectedSubjectType(selectedSubjectType: SubjectType) {
        this.mSelectedSubjectType = selectedSubjectType
    }

    private fun resetValues() {
        mSubjectQuestionsId.clear()
        mSubjectQuestionsId = arrayListOf()

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
        var count : Int = 0

        if (mTotalQuestionsToShow < mUpdatedQuestions.size) {
            mShowMoreQuestionsButton.visibility = View.VISIBLE
        } else {
            mShowMoreQuestionsButton.visibility = View.GONE
        }

        for (i in 0 .. mQuestionList.size - 1) {

            if (count < mTotalQuestionsToShow) {

                val view = LayoutInflater.from(context).inflate(R.layout.custom_subject_question, null, false)
                val image: ImageView = view.findViewById(R.id.image)
                val number = mQuestionList.get(i).questionId.replace("p", "")

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
                } else if (number.equals("-2")) {
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
                    view.text.visibility = View.VISIBLE
                    view.text.typeface = FontUtil.getNunitoSemiBold(context!!)
                    image.visibility = View.GONE
                    param.height = resources.getDimension(R.dimen.height_square).toInt()
                    param.width = resources.getDimension(R.dimen.width_square).toInt()
                    param.bottomMargin = 2
                    param.rightMargin = 2
                    param.leftMargin = 2
                    param.topMargin = 2
                    param.setGravity(Gravity.CENTER)
                    view.setOnClickListener(View.OnClickListener {
                        Log.d(TAG, "onClick: number --- " + number)
                        fragmentManager!!.popBackStack()
                        fragmentManager!!.popBackStack()
                    })

                } else {
                    val currentQuestion = mQuestionList.get(i)
                    val subject = mSelectedSubjectType
                    if (count >= mNumberOfFreeQuestionSubject.toInt() && !mUser.isPremiumUser()) {
                        view.background = resources.getDrawable(R.drawable.not_premium_module_background)
                    } else if (currentQuestion.wasOK) {
                        view.background = resources.getDrawable(R.drawable.checked_module_background)
                    } else {
                        view.background = resources.getDrawable(R.drawable.unchecked_module_background)
                    }
                    when (subject) {
                        SubjectType.MATHEMATICS -> {
                            mSubject = SubjectType.MATHEMATICS.value
                            image.background = resources.getDrawable(R.drawable.mat_1_subject_icon_white)
                        }
                        SubjectType.SPANISH -> {
                            mSubject = SubjectType.SPANISH.value
                            image.background = resources.getDrawable(R.drawable.esp_subject_icon_white)
                        }
                        SubjectType.SPANISH2 -> {
                            mSubject = SubjectType.SPANISH.value
                            image.background = resources.getDrawable(R.drawable.esp_subject_icon_white)
                        }
                        SubjectType.VERBAL_HABILITY -> {
                            mSubject = SubjectType.VERBAL_HABILITY.value
                            image.background = resources.getDrawable(R.drawable.hab_ver_subject_icon_white)
                        }
                        SubjectType.MATHEMATICAL_HABILITY -> {
                            mSubject = SubjectType.MATHEMATICAL_HABILITY.value
                            image.background = resources.getDrawable(R.drawable.hab_mat_subject_icon_white)
                        }
                        SubjectType.BIOLOGY -> {
                            mSubject = SubjectType.BIOLOGY.value
                            image.background = resources.getDrawable(R.drawable.bio_subject_icon_white)
                        }
                        SubjectType.CHEMISTRY -> {
                            mSubject = SubjectType.CHEMISTRY.value
                            image.background = resources.getDrawable(R.drawable.quim_subject_icon_white)
                        }
                        SubjectType.PHYSICS -> {
                            mSubject = SubjectType.PHYSICS.value
                            image.background = resources.getDrawable(R.drawable.fis_subject_icon_white)
                        }
                        SubjectType.GEOGRAPHY -> {
                            mSubject = SubjectType.GEOGRAPHY.value
                            image.background = resources.getDrawable(R.drawable.geo_subject_icon_white)
                        }
                        SubjectType.MEXICO_HISTORY -> {
                            mSubject = SubjectType.MEXICO_HISTORY.value
                            image.background = resources.getDrawable(R.drawable.his_mex_subject_icon_white)
                        }
                        SubjectType.UNIVERSAL_HISTORY -> {
                            mSubject = SubjectType.UNIVERSAL_HISTORY.value
                            image.background = resources.getDrawable(R.drawable.his_subject_icon_white)
                        }
                        SubjectType.FCE -> {
                            mSubject = SubjectType.FCE.value
                            image.background = resources.getDrawable(R.drawable.civ_et_subject_icon_white)
                        }
                        SubjectType.FCE2 -> {
                            mSubject = SubjectType.FCE2.value
                            image.background = resources.getDrawable(R.drawable.civ_et_subject_icon_white)
                        }
                        SubjectType.PHILOSOPHY_AREA -> {
                            mSubject = SubjectType.PHILOSOPHY_AREA.value
                            image.background = resources.getDrawable(R.drawable.filo_subject_icon_white)
                        }
                        SubjectType.PHILOSOPHY_AREA_4 -> {
                            mSubject = SubjectType.PHILOSOPHY_AREA_4.value
                            image.background = resources.getDrawable(R.drawable.filo_subject_icon_white)
                        }
                        SubjectType.PHILOSOPHY -> {
                            mSubject = SubjectType.PHILOSOPHY.value
                            image.background = resources.getDrawable(R.drawable.filo_subject_icon_white)
                        }
                        SubjectType.LITERATURE -> {
                            mSubject = SubjectType.LITERATURE.value
                            image.background = resources.getDrawable(R.drawable.hab_ver_subject_icon_white)
                        }
                        SubjectType.CHEMISTRY_AREA -> {
                            mSubject = SubjectType.CHEMISTRY_AREA.value
                            image.background = resources.getDrawable(R.drawable.quim_plus_subject_icon_white)
                        }
                        SubjectType.CHEMISTRY_AREA_2 -> {
                            mSubject = SubjectType.CHEMISTRY_AREA_2.value
                            image.background = resources.getDrawable(R.drawable.quim_plus_subject_icon_white)
                        }
                        SubjectType.MATEMATICS_AREA -> {
                            mSubject = SubjectType.MATEMATICS_AREA.value
                            image.background = resources.getDrawable(R.drawable.mat_plus_subject_icon_white)
                        }
                        SubjectType.MATEMATICS_AREA_1_2 -> {
                            mSubject = SubjectType.MATEMATICS_AREA_1_2.value
                            image.background = resources.getDrawable(R.drawable.mat_plus_subject_icon_white)
                        }
                        SubjectType.NONE -> {
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
                    val c = count

                    view.setOnClickListener(View.OnClickListener {

                        if (mUser.isPremiumUser() || c < mNumberOfFreeQuestionSubject.toInt()) {
                            Log.d(TAG, "onClick: number --- " + number)
                            goQuestionActivity(Integer.parseInt(number))
                        } else {
                            (activity as ContentActivity).goPaymentFragment()
                        }
                    })
                    count++
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
                if (cnt == 3) {
                    cnt = 0
                }
            }
        }

    }

    override fun onGetQuestionsNewFormatBySubjectSuccess(questions: List<QuestionNewFormat>) {
        super.onGetQuestionsNewFormatBySubjectSuccess(questions)
        isRequesting = false
        try {
            resetValues()
            mUpdatedQuestions.addAll(questions)
            val user = getUser()
            if (user != null && !user.getCourse().equals("")) {
                requestGetFreeSubjectsQuestionsRefactor(user.getCourse())
            }
        } catch (e : Exception) { }
    }

    override fun onGetQuestionsNewFormatBySubjectFail(throwable: Throwable) {
        super.onGetQuestionsNewFormatBySubjectFail(throwable)
        isRequesting = false
        mLoadingQuestionSubject.visibility = View.GONE
        if (activity != null) {
            //(activity as ContentActivity).showLoading(false)
        }
    }

    override fun onGetFreeSubjectsQuestionsSuccess(numberOfFreeQuestionSubjects: Long) {
        super.onGetFreeSubjectsQuestionsSuccess(numberOfFreeQuestionSubjects)
        mNumberOfFreeQuestionSubject = numberOfFreeQuestionSubjects

        if (activity != null) {
            val user = (activity as ContentActivity).getUserProfile()
            if (user != null && !user.getCourse().equals("")) {
                requestGetWrongQuestionsAndProfileRefactor(user.getCourse())
            }
        }
    }

    override fun onGetFreeSubjectsQuestionsFail(throwable: Throwable) {
        super.onGetFreeSubjectsQuestionsFail(throwable)
        mNumberOfFreeQuestionSubject = 0
        mLoadingQuestionSubject.visibility = View.GONE
        if (activity != null) {
            //(activity as ContentActivity).showLoading(false)
        }
    }

    override fun onGetWrongQuestionsAndProfileRefactorSuccess(user: User) {
        super.onGetWrongQuestionsAndProfileRefactorSuccess(user)
        try {
            if (context != null) {
                mLoadingQuestionSubject.visibility = View.GONE
                mUser = user
                saveUser(user)
                val answeredQuestions = user.getAnsweredQuestionNewFormat()

                for (i in 0.. mUpdatedQuestions.size -1) {
                    for (answeredQuestion in answeredQuestions) {
                        if (answeredQuestion.questionId.equals(mUpdatedQuestions[i].questionId) && answeredQuestion.wasOK) {
                            mUpdatedQuestions[i].wasOK = answeredQuestion.wasOK
                        }
                    }
                    if (mUser.isPremiumUser()) {
                        mSubjectQuestionsId.add(mUpdatedQuestions[i].questionId)
                    } else if (!mUser.isPremiumUser() && i < mNumberOfFreeQuestionSubject.toInt()) {
                        mSubjectQuestionsId.add(mUpdatedQuestions[i].questionId)
                    }
                }

                updateQuestionList(mUpdatedQuestions)
                drawQuestions()
            }
            /*if (activity != null)
                (activity as ContentActivity).showLoading(false)*/
        } catch (exception : Exception) {}
    }

    override fun onGetWrongQuestionsAndProfileRefactorFail(throwable: Throwable) {
        super.onGetWrongQuestionsAndProfileRefactorFail(throwable)
        mLoadingQuestionSubject.visibility = View.GONE
        /*if (activity != null)
            (activity as ContentActivity).showLoading(false)
            */
    }

    private fun goQuestionActivity(questionId : Int) {
        val intent = Intent(activity, QuestionActivity::class.java)
        intent.putExtra(QUESTION_ID, questionId)
        intent.putExtra(ANONYMOUS_USER, false)
        intent.putExtra(FROM_SUBJECT_QUESTION, true)
        intent.putExtra(SUBJECT_QUESTIONS_LIST, mSubjectQuestionsId)
        intent.putExtra(SUBJECT_EXTRA, mSubject)
        if (activity != null) {
            val user = (activity as ContentActivity).getUserProfile()
            if (user != null && !user.getCourse().equals("")) {
                intent.putExtra(CURRENT_COURSE, user.getCourse())
            }
        }
        this.startActivityForResult(intent, BaseActivityLifeCycle.SHOW_QUESTION_RESULT_CODE)
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

    private val mShowMoreQuestionsListener = View.OnClickListener {
        mTotalQuestionsToShow += 20
        mLeftTableLayout.removeAllViews()
        mCenterTableLayout.removeAllViews()
        mRightTableLayout.removeAllViews()
        drawQuestions()
    }
}