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
import android.support.v4.app.Fragment
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.question.*
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.models.Module
import com.zerebrez.zerebrez.models.Question
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.utils.FontUtil


/**
 * This class manage the UI questions showing
 *
 * Created by Jorge Zepeda Tinoco on 29/04/18.
 * jorzet.94@gmail.com
 */

class QuestionActivity : BaseActivityLifeCycle(), ErrorDialog.OnErrorDialogListener {

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
    private val HITS_EXTRA = "hits_extra"
    private val MISSES_EXTRA = "misses_extra"
    private val WRONG_QUESTIONS_LIST = "wrong_questions_list"

    /*
     * UI accessors
     */
    private lateinit var mModuleNumber : TextView
    private lateinit var mQuestiontypeText : TextView
    private lateinit var mCloseQuestion : View
    private lateinit var mNextQuestion : View
    private lateinit var mNextQuestionText : TextView
    private lateinit var mShowExpandedQuestion : View
    private lateinit var mShowAnswer : View
    private lateinit var mShowAnswerText : TextView
    private lateinit var mCompleteQuestionsFragmentContainer : FrameLayout
    private lateinit var mControlsBar : View
    private lateinit var progressBarHolder : FrameLayout
    private lateinit var mQuestionsProgress : ProgressBar

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
    private var resetExpandedButton : Boolean = false
    private var mExamAnsQuestionsSaved = false
    private var mModulesAndQuestionsSaved = false
    private var mWrongQuestionsSaver = false
    private var mShowPaymentFragment = false
    private var mProgressByQuestion : Int = 0
    private var mCurrentProgress : Int = 0

    /*
     * Animation
     */
    private lateinit var inAnimation : AlphaAnimation
    private lateinit var outAnimation : AlphaAnimation

    /*
     * Objects
     */
    private lateinit var mQuestions : List<Question>
    private lateinit var mModuleList : List<Module>
    private lateinit var mExamList : List<Exam>
    private lateinit var currentFragment : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_container)

        mModuleNumber = findViewById(R.id.tv_module_number)
        mQuestiontypeText = findViewById(R.id.tv_question_type_text)
        mCloseQuestion = findViewById(R.id.iv_close_question)
        mShowAnswer = findViewById(R.id.btn_show_answer)
        mShowAnswerText = findViewById(R.id.btn_show_answer_text)
        mShowExpandedQuestion = findViewById(R.id.iv_show_expanded_question)
        mNextQuestion = findViewById(R.id.btn_next_question)
        mNextQuestionText = findViewById(R.id.btn_next_question_text)
        mCompleteQuestionsFragmentContainer = findViewById(R.id.complete_question_fragment_container)
        mControlsBar = findViewById(R.id.bottom_bar)
        progressBarHolder = findViewById(R.id.progressBarHolder)
        mQuestionsProgress = findViewById(R.id.pb_questions_progress)

        //mBackQuestion.setOnClickListener(mBackQuestionListener)
        mCloseQuestion.setOnClickListener(mCloseQuestionListener)
        mNextQuestion.setOnClickListener(mNextQuestionListener)
        mShowExpandedQuestion.setOnClickListener(mShowExpandedQuestionListener)
        mShowAnswer.setOnClickListener(mShowAnswerListener)

        mShowAnswer.isEnabled = false
        setNextQuestionEnable(false)
        mShowExpandedQuestion.visibility = View.GONE

        mModuleNumber.typeface = FontUtil.getNunitoBold(baseContext)
        mNextQuestionText.typeface = FontUtil.getNunitoBlack(baseContext)
        mShowAnswerText.typeface = FontUtil.getNunitoBlack(baseContext)

        inAnimation = AlphaAnimation(0f, 1f)
        inAnimation.duration = 200
        outAnimation = AlphaAnimation(1f, 0f)
        outAnimation.duration = 200

        mModuleId = intent.getIntExtra(MODULE_ID, -1)
        mQuestionId = intent.getIntExtra(QUESTION_ID, -1)
        mExamId = intent.getIntExtra(EXAM_ID, -1)
        isAnonymous = intent.getBooleanExtra(ANONYMOUS_USER, false)
        isFromWrongQuestionFragment = intent.getBooleanExtra(FROM_WRONG_QUESTION, false)
        isFromExamFragment = intent.getBooleanExtra(FROM_EXAM_FRAGMENT, false)

        if (isFromWrongQuestionFragment) {
            showLoading(true)
            val mWrongQuestionIds = intent.getSerializableExtra(WRONG_QUESTIONS_LIST) as List<Int>
            val mQuestions = arrayListOf<Question>()
            var mLastKnowQuestion = false
            for (wrongQuestionId in mWrongQuestionIds) {
                if (mLastKnowQuestion || wrongQuestionId.equals(mQuestionId)) {
                    val question = Question()
                    question.setQuestionId(Integer(wrongQuestionId))
                    mQuestions.add(question)
                    mLastKnowQuestion = true
                }
            }
            requestGetWrongQuestionsByQuestionIdRefactor(mQuestions)
            //mQuestions = DataHelper(baseContext).getWrongQuestionsByQuestionId(Integer(mQuestionId))
        } else if (isFromExamFragment) {
            showLoading(true)
            requestGetQuestionsByExamIdRefactor(mExamId)
        } else {
            showLoading(true)
            requestGetQuestionsByModuleIdRefactor(mModuleId)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode.equals(SHOW_ANSWER_RESULT_CODE)) {
            val showAnswer = data!!.getBooleanExtra(SET_CHECKED_TAG, false)
            if (showAnswer) {
                if (currentFragment is QuestionFragmentRefactor)
                    (currentFragment as QuestionFragmentRefactor).showAnswerQuestion()
            }
        } else if (resultCode.equals(SHOW_ANSWER_MESSAGE_RESULT_CODE)) {
            DataHelper(baseContext).saveCurrentQuestion(mQuestions.get(mCurrentQuestion))
            showAnswer()
        }
    }

    override fun onBackPressed() {
        if (mShowPaymentFragment) {
            val intent = Intent()
            intent.putExtra(SHOW_PAYMENT_FRAGMENT, true)
            setResult(SHOW_ANSWER_MESSAGE_RESULT_CODE, intent)
            finish()
        } else if (isFromWrongQuestionFragment) {
            val intent = Intent()
            intent.putExtra(UPDATE_WRONG_QUESTIONS, true)
            setResult(UPDATE_WRONG_QUESTIONS_RESULT_CODE, intent)
            finish()
        }
        super.onBackPressed()
    }

    private val mCloseQuestionListener = View.OnClickListener {
        if (mCurrentQuestion > 0) {
            ErrorDialog.newInstance("¿Seguro que quieres salir?",
                    "Perderás los avances.",
                    DialogType.YES_NOT_DIALOG,
                    this)!!
                    .show(supportFragmentManager, "")
        } else {
            onBackPressed()
        }
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
        setNextQuestionEnable(false)
        if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size -1) {
            showQuestion()
            mCurrentQuestion++
        } else if (isAnonymous) {
            saveModulesAndQuestions()
        } else {
            if (isFromWrongQuestionFragment) {
                saveWrongQuestion()
            } else if (isFromExamFragment) {
                saveExamsAndQuestions()
            } else {
                saveModulesAndQuestions()
            }
        }
    }

    /*
     * A listener that expands the question to read it beater
     */
    private val mShowExpandedQuestionListener = View.OnClickListener {
        if (resetExpandedButton) {
            mShowExpandedQuestion.background = resources.getDrawable(R.drawable.finger_unselected_icon)
            resetExpandedButton = false
        } else {
            mShowExpandedQuestion.background = resources.getDrawable(R.drawable.finger_selected_icon)
            resetExpandedButton = true
        }

        if (currentFragment is QuestionFragmentRefactor) {
            (currentFragment as QuestionFragmentRefactor).showExpandedQuestion(resetExpandedButton)
        }
    }

    /*
     * A listener that show answer step by step according to current question
     */
    private val mShowAnswerListener = View.OnClickListener {
        //showAnswerMessage()

        if (SharedPreferencesManager(baseContext).isShowAnswerMessageOK()) {
            if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size) {
                if (mQuestions.get(mCurrentQuestion).hasStepByStep()) {
                    DataHelper(baseContext).saveCurrentQuestion(mQuestions.get(mCurrentQuestion))
                    showAnswer()
                }
            }
        } else {
            showAnswerMessage()
        }

    }


    /*
     * This method show the correspond fragment according to question type
     */
    private fun showQuestion() {
        mCurrentProgress += mProgressByQuestion
        mQuestionsProgress.progress = mCurrentProgress
        currentFragment = QuestionFragmentRefactor()
        val manager = getSupportFragmentManager();
        val transaction = manager.beginTransaction();
        transaction.replace(R.id.question_fragment_container, currentFragment);
        transaction.commitAllowingStateLoss()
    }

    /**
     * @return
     *      The current question according to moduleId
     */
    fun getQuestion() : Question? {
        if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size) {
            mShowAnswer.isEnabled = mQuestions.get(mCurrentQuestion).hasStepByStep()
            if (isFromWrongQuestionFragment) {
                mQuestiontypeText.text = mQuestions.get(mCurrentQuestion).getSubjectType().value
            }
            return mQuestions.get(mCurrentQuestion)
        }

        return null
    }

    fun getCorrectQuestions() : Int {
        return this.mCorrectQuestions
    }

    fun getIncorrectQuestion() : Int {
        return this.mIncorrectQiestions
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

            //if (NetworkUtil.isConnected(baseContext)) {
            //    requestSendAnsweredModules(mModuleList)
            //} else {
                requestSendAnsweredModules(mModuleList)
                requestSendAnsweredQuestions(mQuestions)
                mModulesAndQuestionsSaved = true

                // this is called on QuestionsCompleteFragment
                if (isAnonymous) {
                    goLogInActivity()
                } else {
                    showQuestionsCompleteFragment()
                }
            //}
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
            //if (NetworkUtil.isConnected(baseContext)) {
            //    requestSendAnsweredExams(mExamList)
            //} else {
                requestSendAnsweredExams(mExamList)
                mExamAnsQuestionsSaved = true
                showQuestionsCompleteFragment()
                // this is called on QuestionsCompleteFragment
                //onBackPressed()
            //}
        }
    }

    private fun saveWrongQuestion() {
        requestSendAnsweredQuestions(mQuestions)
        mWrongQuestionsSaver = true
        showQuestionsCompleteFragment()
    }

    override fun onSendAnsweredModulesSuccess(success: Boolean) {
        super.onSendAnsweredModulesSuccess(success)
        //requestSendAnsweredQuestions(mModuleList)
    }

    override fun onSendAnsweredModulesFail(throwable: Throwable) {
        super.onSendAnsweredModulesFail(throwable)
    }

    override fun onSendAnsweredQuestionsSuccess(success: Boolean) {
        super.onSendAnsweredQuestionsSuccess(success)
        /*if (isAnonymous) {
            goLogInActivity()
        } else {
            onBackPressed()
        }*/
    }

    override fun onSendAnsweredQuestionsFail(throwable: Throwable) {
        super.onSendAnsweredQuestionsFail(throwable)
    }

    override fun onSendAnsweredExamsSuccess(success: Boolean) {
        super.onSendAnsweredExamsSuccess(success)
        //onBackPressed()
    }

    override fun onSendAnsweredExamsFail(throwable: Throwable) {
        super.onSendAnsweredExamsFail(throwable)
    }

    private fun showAnswerMessage() {
        val intent = Intent(this, ShowAnswerMessageActivity::class.java)
        this.startActivityForResult(intent, SHOW_QUESTION_RESULT_CODE)
    }

    private fun showAnswer() {
        val intent = Intent(this, ShowAnswerActivity::class.java)
        this.startActivityForResult(intent, SHOW_QUESTION_RESULT_CODE)
    }

    private fun showQuestionsCompleteFragment() {
        mCompleteQuestionsFragmentContainer.visibility = View.VISIBLE
        showHideControlBar(false)

        currentFragment = QuestionsCompleteFragment()
        val manager = getSupportFragmentManager();
        val transaction = manager.beginTransaction();
        transaction.replace(R.id.complete_question_fragment_container, currentFragment);
        transaction.commit()
    }

    fun setNextQuestionEnable(isEnable : Boolean) {
        mNextQuestion.isEnabled = isEnable
    }

    fun showHideExpandedQuestionButton(showButton : Boolean) {
        if (showButton)
            mShowExpandedQuestion.visibility = View.VISIBLE
        else
            mShowExpandedQuestion.visibility = View.GONE
    }

    fun showHideControlBar(showControls : Boolean) {
        if (showControls) {
            mControlsBar.visibility = View.VISIBLE
        } else {
            mControlsBar.visibility = View.GONE
        }
    }

    /*
     * Those method are called on QuestionsCompletefragment
     */
    fun areModulesAndQuestionsSaved() : Boolean {
        return this.mModulesAndQuestionsSaved
    }

    fun areExamsAndQuestionsSaved() : Boolean {
        return this.mExamAnsQuestionsSaved
    }

    fun areWrongQuestionsSaved() : Boolean {
        return this.mWrongQuestionsSaver
    }

    fun isAnonymousUser() : Boolean {
        return this.isAnonymous
    }

    fun getModuleId() : Int {
        return this.mModuleId
    }

    fun getExamId() : Int {
        return this.mExamId
    }

    fun showPaymentFragment(showPaymentFragment : Boolean) {
        this.mShowPaymentFragment = showPaymentFragment
    }

    /*
     * This method is only used after StartFragment when user responds the first module
     * the app is going to redirect to LoginActivity to show SingUpFragment
     */
    private fun goLogInActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(SHOW_START, false)
        intent.putExtra(HITS_EXTRA, getCorrectQuestions())
        intent.putExtra(MISSES_EXTRA, getIncorrectQuestion())
        this.startActivity(intent)
        this.finish()
    }

    override fun onGetQuestionsByModuleIdRefactorSuccess(questions: List<Question>) {
        super.onGetQuestionsByModuleIdRefactorSuccess(questions)
        mQuestions = questions
        mModuleNumber.text = mModuleId.toString()
        mQuestiontypeText.text = "Módulo"
        mProgressByQuestion = 100 / questions.size
        showQuestion()
        showLoading(false)
    }

    override fun onGetQuestionsByModuleIdRefactorFail(throwable: Throwable) {
        super.onGetQuestionsByModuleIdRefactorFail(throwable)
        showLoading(false)
        onBackPressed()
    }

    override fun onGetQuestionsByExamIdRefactorSuccess(questions: List<Question>) {
        super.onGetQuestionsByExamIdRefactorSuccess(questions)
        mQuestions = questions
        mModuleNumber.text = mExamId.toString()
        mQuestiontypeText.text = "Examen"
        mProgressByQuestion = 100 / questions.size
        showQuestion()
        showLoading(false)
    }

    override fun onGetQuestionsByExamIdRefactorFail(throwable: Throwable) {
        super.onGetQuestionsByExamIdRefactorFail(throwable)
        showLoading(false)
        onBackPressed()
    }

    override fun onGetWrongQuestionsByQuestionIdRefactorSuccess(questions: List<Question>) {
        super.onGetWrongQuestionsByQuestionIdRefactorSuccess(questions)
        mQuestions = questions
        mModuleNumber.text = ":)"
        if (mQuestions.isNotEmpty()) {
            mQuestiontypeText.text =  mQuestions.get(mCurrentQuestion).getSubjectType().value
        }
        mProgressByQuestion = 100 / questions.size
        showQuestion()
        showLoading(false)
    }

    override fun onGetWrongQuestionsByQuestionIdRefactorFail(throwable: Throwable) {
        super.onGetWrongQuestionsByQuestionIdRefactorFail(throwable)
        showLoading(false)
        onBackPressed()
    }

    fun showLoading(showLoading : Boolean) {
        if (showLoading) {
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        } else {
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
        }
    }

    override fun onConfirmationCancel() {

    }

    override fun onConfirmationNeutral() {

    }

    override fun onConfirmationAccept() {
        onBackPressed()
    }

}
