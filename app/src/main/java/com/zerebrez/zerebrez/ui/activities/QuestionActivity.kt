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

package com.zerebrez.zerebrez.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.question.QuestionEquationFragment
import com.zerebrez.zerebrez.fragments.question.QuestionFragmentRefactor
import com.zerebrez.zerebrez.fragments.question.QuestionImageFragment
import com.zerebrez.zerebrez.fragments.question.QuestionTextFragment
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.models.Module
import com.zerebrez.zerebrez.models.Question
import com.zerebrez.zerebrez.models.enums.QuestionType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.utils.NetworkUtil

/**
 * This class manage the UI questions showing
 *
 * Created by Jorge Zepeda Tinoco on 29/04/18.
 * jorzet.94@gmail.com
 */

class QuestionActivity : BaseActivityLifeCycle() {

    /*
     * Tags
     */
    private val TAG : String = "QuestionActivity"
    private val MODULE_ID : String = "module_id"
    private val QUESTION_ID : String = "question_id"
    private val EXAM_ID : String = "exam_id"
    private val ANONYMOUS_USER : String = "anonymous_user"
    private val FROM_WRONG_QUESTION : String = "from_wrong_question"
    private val FROM_EXAM_FRAGMENT : String = "from_exam_fragment"
    private val SHOW_START : String = "show_start"

    /*
     * UI accessors
     */
    private lateinit var mNextQuestion : Button
    private lateinit var mShowAnswer : Button

    /*
     * Variables
     */
    private var mModuleId : Int = 0
    private var mQuestionId : Int = 0
    private var mExamId : Int = 0
    private var mCurrentQuestion : Int = 0
    private var mCorrectQuestions : Int = 0
    private var mIncorrectQiestions : Int = 0
    private var isAnonymous : Boolean = false
    private var isFromWrongQuestionFragment : Boolean = false
    private var isFromExamFragment : Boolean = false

    /*
     * Objects
     */
    private lateinit var mQuestions : List<Question>
    private lateinit var mModuleList : List<Module>
    private lateinit var mExamList : List<Exam>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_container)

        mShowAnswer = findViewById(R.id.btn_show_answer)
        mNextQuestion = findViewById(R.id.btn_next_question)

        //mBackQuestion.setOnClickListener(mBackQuestionListener)
        mNextQuestion.setOnClickListener(mNextQuestionListener)
        mShowAnswer.setOnClickListener(mShowAnswerListener)

        mModuleId = intent.getIntExtra(MODULE_ID, -1)
        mQuestionId = intent.getIntExtra(QUESTION_ID, -1)
        mExamId = intent.getIntExtra(EXAM_ID, -1)
        isAnonymous = intent.getBooleanExtra(ANONYMOUS_USER, false)
        isFromWrongQuestionFragment = intent.getBooleanExtra(FROM_WRONG_QUESTION, false)
        isFromExamFragment = intent.getBooleanExtra(FROM_EXAM_FRAGMENT, false)

        if (isFromWrongQuestionFragment) {
            mQuestions = DataHelper(baseContext).getWrongQuestionsByQuestionId(Integer(mQuestionId))
        } else if (isFromExamFragment) {
            mQuestions = DataHelper(baseContext).getQuestionsByExamId(Integer(mExamId))
        } else {
            mQuestions = DataHelper(baseContext).getQuestionsByModuleId(Integer(mModuleId))
        }

        showQuestion()
    }

    /*
     * A listener that check current question and show back question
     */
    /*private val mBackQuestionListener = View.OnClickListener {
        if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size) {
            showQuestion()
            mCurrentQuestion--
        }
    }*/

    /*
     * A listener that check current question and show next question
     *  - If user is anonymous is neesary show LoginActivity
     *  - Else (this means that user answer all questions) save questions in current module and show
     *    QuestionModuleFragment again with current module updated
     */
    private val mNextQuestionListener = View.OnClickListener {
        if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size -1) {
            showQuestion()
            mCurrentQuestion++
        } else if (isAnonymous) {
            saveModulesAndQuestions()
        } else {
            if (isFromExamFragment) {
                saveExamsAndQuestions()
            } else {
                saveModulesAndQuestions()
            }
        }
    }

    /*
     * A listener that show answer step by step according to current question
     */
    private val mShowAnswerListener = View.OnClickListener {

    }


    /*
     * This method show the correspond fragment according to question type
     */
    private fun showQuestion() {
        val manager = getSupportFragmentManager();
        val transaction = manager.beginTransaction();
        transaction.replace(R.id.question_fragment_container, QuestionFragmentRefactor());
        transaction.commit()

        /*when (mQuestions.get(mCurrentQuestion).getQuestionType()) {
            QuestionType.EQUATION.toString() -> {
                transaction.replace(R.id.question_fragment_container, QuestionEquationFragment());
                transaction.commit()
            }
            QuestionType.TEXT.toString() -> {
                transaction.replace(R.id.question_fragment_container, QuestionTextFragment());
                transaction.commit()
            }
            QuestionType.IMAGE.toString() -> {
                transaction.replace(R.id.question_fragment_container, QuestionImageFragment());
                transaction.commit()
            }
        }*/
    }

    /**
     * @return
     *      The current question according to moduleId
     */
    fun getQuestion() : Question? {
        if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size) {
            return mQuestions.get(mCurrentQuestion)
        }

        return null
    }

    /**
     * @param answer
     * @param wasOK
     *      The method set answed choosed by user and set if answer was correct or not
     */
    fun setQuestionAnswer(answer : String, wasOK : Boolean) {
        if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size) {
            mQuestions.get(mCurrentQuestion).setOptionChoosed(answer)
            mQuestions.get(mCurrentQuestion).setWasOK(wasOK)
            if (wasOK) {
                mCorrectQuestions++
            } else {
                mIncorrectQiestions++
            }
        }
    }


    /*
     * This method save the current module and its own questions
     */
    private fun saveModulesAndQuestions() {
        mModuleList = DataHelper(baseContext).getModulesAnsQuestions()
        for (j in 0 .. mModuleList.size - 1) {
            if (mModuleList.get(j).getId().equals(Integer(mModuleId))) {
                mModuleList.get(j).setAnsweredModule(true)
                mModuleList.get(j).setQuestions(mQuestions)
                mModuleList.get(j).setCorrectQuestions(mCorrectQuestions)
                mModuleList.get(j).setIncorrectQuestions(mIncorrectQiestions)
            }
        }
        if (mModuleList.isNotEmpty()) {
            DataHelper(baseContext).saveModules(mModuleList)

            if (NetworkUtil.isConnected(baseContext)) {
                requestSendAnsweredModules(mModuleList)
            } else {
                requestSendAnsweredModules(mModuleList)
                requestSendAnsweredQuestions(mModuleList)
                if (!isAnonymous) {
                    onBackPressed()
                }
            }
        }
    }

    private fun saveExamsAndQuestions() {
        mExamList = DataHelper(baseContext).getExams()
        for (j in 0 .. mExamList.size - 1) {
            if (mExamList.get(j).getExamId().equals(Integer(mExamId))) {
                mExamList.get(j).setQuestions(mQuestions)
                mExamList.get(j).setHits(mCorrectQuestions)
                mExamList.get(j).setMisses(mIncorrectQiestions)
                mExamList.get(j).setAnsweredExam(true)
                DataHelper(baseContext).saveLastExamDidIt(mExamList.get(j))
            }
        }
        if (mExamList.isNotEmpty()) {
            // update local data
            DataHelper(baseContext).saveExams(mExamList)

            // send data
            if (NetworkUtil.isConnected(baseContext)) {
                requestSendAnsweredExams(mExamList)
            } else {
                requestSendAnsweredExams(mExamList)
                onBackPressed()
            }
        }
    }

    override fun onSendAnsweredModulesSuccess(success: Boolean) {
        super.onSendAnsweredModulesSuccess(success)
        requestSendAnsweredQuestions(mModuleList)
    }

    override fun onSendAnsweredModulesFail(throwable: Throwable) {
        super.onSendAnsweredModulesFail(throwable)
    }

    override fun onSendAnsweredQuestionsSuccess(success: Boolean) {
        super.onSendAnsweredQuestionsSuccess(success)
        if (isAnonymous) {
            goLogInActivity()
        } else {
            onBackPressed()
        }
    }

    override fun onSendAnsweredQuestionsFail(throwable: Throwable) {
        super.onSendAnsweredQuestionsFail(throwable)
    }

    override fun onSendAnsweredExamsSuccess(success: Boolean) {
        super.onSendAnsweredExamsSuccess(success)
        onBackPressed()
    }

    override fun onSendAnsweredExamsFail(throwable: Throwable) {
        super.onSendAnsweredExamsFail(throwable)
    }

    /*
     * This method is only used after StartFragment when user responds the first module
     * the app is going to redirect to LoginActivity to show SingUpFragment
     */
    private fun goLogInActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(SHOW_START, false)
        this.startActivity(intent)
        this.finish()
    }
}
