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
import com.zerebrez.zerebrez.models.Question
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.ui.activities.BaseActivityLifeCycle
import com.zerebrez.zerebrez.ui.activities.ContentActivity
import com.zerebrez.zerebrez.ui.activities.QuestionActivity

/**
 * Created by Jorge Zepeda Tinoco on 01/05/18.
 * jorzet.94@gmail.com
 */

class StudyWrongQuestionFragment : BaseContentFragment() {

    /*
     * Tags
     */
    private val TAG : String = "StudyWrongQuestionF"
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
    private var mQuestionList = arrayListOf<Question>()
    private var mUpdatedQuestions = arrayListOf<Question>()
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

        requestGetWrongQuestionsAndProfileRefactor()

        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode.equals(BaseActivityLifeCycle.SHOW_QUESTION_RESULT_CODE)) {
            if (resultCode.equals(BaseActivityLifeCycle.UPDATE_WRONG_QUESTIONS_RESULT_CODE)) {
                requestGetWrongQuestionsAndProfileRefactor()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requestGetWrongQuestionsAndProfileRefactor()
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

    private fun updateQuestionList(questions : List<Question>) {
        var row = 1
        for (i in 0 .. questions.size - 1) {
            when (row) {
                1 -> {
                    mQuestionList.add(questions.get(i))
                    val nothing = Question()
                    nothing.setQuestionId(Integer(-1))
                    mQuestionList.add(nothing)
                    val padding = Question()
                    padding.setQuestionId(Integer(-2))
                    mQuestionList.add(padding)
                }
                2 -> {
                    val nothing = Question()
                    nothing.setQuestionId(Integer(-1))
                    mQuestionList.add(nothing)
                    mQuestionList.add(questions.get(i))
                    val padding = Question()
                    padding.setQuestionId(Integer(-1))
                    mQuestionList.add(padding)
                }
                3 -> {
                    val nothing = Question()
                    nothing.setQuestionId(Integer(-2))
                    mQuestionList.add(nothing)
                    val padding = Question()
                    padding.setQuestionId(Integer(-1))
                    mQuestionList.add(padding)
                    mQuestionList.add(questions.get(i))
                }
                4 -> {
                    val nothing = Question()
                    nothing.setQuestionId(Integer(-1))
                    mQuestionList.add(nothing)
                    mQuestionList.add(questions.get(i))
                    val padding = Question()
                    padding.setQuestionId(Integer(-1))
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

            val number = mQuestionList.get(i).getQuestionId().toString()

            // params for module
            val param = GridLayout.LayoutParams()

            if (number.equals("-1")) {
                view.background = resources.getDrawable(R.drawable.empty_square)
                image.visibility = View.GONE
                param.height = resources.getDimension(R.dimen.height_square).toInt()
                param.width = resources.getDimension(R.dimen.width_square).toInt()
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
                when (currentQuestion.getSubjectType()) {
                    SubjectType.MATHEMATICS -> {
                        image.background = resources.getDrawable(R.drawable.mat_1_subject_icon_white)
                    }
                    SubjectType.SPANISH -> {
                        image.background = resources.getDrawable(R.drawable.esp_subject_icon_white)
                    }
                    SubjectType.VERBAL_HABILITY -> {
                        image.background = resources.getDrawable(R.drawable.hab_ver_subject_icon_white)
                    }
                    SubjectType.MATHEMATICAL_HABILITY -> {
                        image.background = resources.getDrawable(R.drawable.hab_mat_subject_icon_white)
                    }
                    SubjectType.BIOLOGY -> {
                        image.background = resources.getDrawable(R.drawable.bio_subject_icon_white)
                    }
                    SubjectType.CHEMISTRY -> {
                        image.background = resources.getDrawable(R.drawable.quim_subject_icon_white)
                    }
                    SubjectType.PHYSICS -> {
                        image.background = resources.getDrawable(R.drawable.fis_subject_icon_white)
                    }
                    SubjectType.GEOGRAPHY -> {
                        image.background = resources.getDrawable(R.drawable.geo_subject_icon_white)
                    }
                    SubjectType.MEXICO_HISTORY -> {
                        image.background = resources.getDrawable(R.drawable.his_mex_subject_icon_white)
                    }
                    SubjectType.UNIVERSAL_HISTORY -> {
                        image.background = resources.getDrawable(R.drawable.his_subject_icon_white)
                    }
                    SubjectType.FCE -> {
                        image.background = resources.getDrawable(R.drawable.civ_et_subject_icon_white)
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
                mWrongQuestionsId.add(currentQuestion.getQuestionId().toInt())
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
        this.startActivityForResult(intent, BaseActivityLifeCycle.SHOW_QUESTION_RESULT_CODE)
    }

    override fun onGetWrongQuestionsAndProfileRefactorSuccess(user: User) {
        super.onGetWrongQuestionsAndProfileRefactorSuccess(user)
        try {
            if (context != null) {
                mUser = user
                saveUser(user)
                val answeredQuestion = user.getAnsweredQuestion()

                resetValues()
                for (i in 0..answeredQuestion.size - 1) {
                    if (!answeredQuestion.get(i).getWasOK()) {
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
}