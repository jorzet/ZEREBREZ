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
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.question.*
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.models.Module
import com.zerebrez.zerebrez.models.QuestionNewFormat
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

class QuestionActivity : BaseActivityLifeCycle(), ErrorDialog.OnErrorDialogListener,
        RewardedVideoAdListener {

    /*
     * Tags
     */
    private val TAG : String = "QuestionActivity"
    private var CURRENT_COURSE : String = "current_course"
    private val MODULE_ID : String = "module_id"
    private val QUESTION_ID : String = "question_id"
    private val EXAM_ID : String = "exam_id"
    private val SELECTED_SUBJECT : String = "selected_subject"
    private val ANONYMOUS_USER : String = "anonymous_user"
    private val FROM_SUBJECT_QUESTION : String = "from_subject_question"
    private val FROM_WRONG_QUESTION : String = "from_wrong_question"
    private val FROM_EXAM_FRAGMENT : String = "from_exam_fragment"
    private val SHOW_START : String = "show_start"
    private val HITS_EXTRA = "hits_extra"
    private val MISSES_EXTRA = "misses_extra"
    private val WRONG_QUESTIONS_LIST = "wrong_questions_list"
    private val SUBJECT_QUESTIONS_LIST : String = "subject_questions_list"
    private val SUBJECT_EXTRA : String = "subject_extra"

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
    private var mSubject : String = ""
    private var mCourse : String = ""
    private var mModuleId : Int = 0
    private var mQuestionId : Int = 0
    private var mExamId : Int = 0
    private var mCurrentQuestion : Int = 0
    private var mCorrectQuestions : Int = 0
    private var mIncorrectQiestions : Int = 0
    private var isAnonymous : Boolean = false
    private var isFromSubjectQuestionFragment : Boolean = false
    private var isFromWrongQuestionFragment : Boolean = false
    private var isFromExamFragment : Boolean = false
    private var resetExpandedButton : Boolean = false
    private var mExamAnsQuestionsSaved = false
    private var mModulesAndQuestionsSaved = false
    private var mWrongQuestionsSaved = false
    private var mSubjectQuestionsSaved = false
    private var mShowPaymentFragment = false
    private var mProgressByQuestion : Float = 0.0F
    private var mCurrentProgress : Float = 0.0F

    /*
     * Animation
     */
    private lateinit var inAnimation : AlphaAnimation
    private lateinit var outAnimation : AlphaAnimation

    /*
     * Objects
     */
    //private lateinit var mQuestions : List<Question>
    private lateinit var mQuestionsNewFormat: List<QuestionNewFormat>
    private lateinit var mModuleList : List<Module>
    private lateinit var mExamList : List<Exam>
    private lateinit var currentFragment : Fragment

    /*
     * Ads Objects
     */
    private lateinit var mRewardedVideoAd: RewardedVideoAd
    private lateinit var mInterstitialAd: InterstitialAd

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

        if (intent != null) {
            mCourse = intent.getStringExtra(CURRENT_COURSE)
            mModuleId = intent.getIntExtra(MODULE_ID, -1)
            mQuestionId = intent.getIntExtra(QUESTION_ID, -1)
            mExamId = intent.getIntExtra(EXAM_ID, -1)
            isAnonymous = intent.getBooleanExtra(ANONYMOUS_USER, false)
            isFromSubjectQuestionFragment = intent.getBooleanExtra(FROM_SUBJECT_QUESTION, false)
            isFromWrongQuestionFragment = intent.getBooleanExtra(FROM_WRONG_QUESTION, false)
            isFromExamFragment = intent.getBooleanExtra(FROM_EXAM_FRAGMENT, false)

            if (isFromSubjectQuestionFragment) {
                showLoading(true)
                //val mSelectedSubject = intent.getStringExtra(SELECTED_SUBJECT)
                //requestGetQuestionsNewFormatBySubject(mSelectedSubject)
                val mSubjectQuestionIds = intent.getSerializableExtra(SUBJECT_QUESTIONS_LIST) as List<Int>
                mSubject = intent.getStringExtra(SUBJECT_EXTRA)
                val mQuestions = arrayListOf<QuestionNewFormat>()
                var mLastKnowQuestion = false
                for (subjectQuestionId in mSubjectQuestionIds) {
                    if (mLastKnowQuestion || subjectQuestionId.equals(mQuestionId)) {
                        val question = QuestionNewFormat()
                        question.questionId = "p" + subjectQuestionId
                        mQuestions.add(question)
                        mLastKnowQuestion = true
                    }
                }
                requestGetQuestionsNewFormatBySubjectQuestionId(mQuestions)
            } else if (isFromWrongQuestionFragment) {
                showLoading(true)
                val mWrongQuestionIds = intent.getSerializableExtra(WRONG_QUESTIONS_LIST) as List<Int>
                val mQuestions = arrayListOf<QuestionNewFormat>()
                var mLastKnowQuestion = false
                for (wrongQuestionId in mWrongQuestionIds) {
                    if (mLastKnowQuestion || wrongQuestionId.equals(mQuestionId)) {
                        val question = QuestionNewFormat()
                        question.questionId = "p" + wrongQuestionId
                        mQuestions.add(question)
                        mLastKnowQuestion = true
                    }
                }
                //requestGetWrongQuestionsByQuestionIdRefactor(mQuestions)
                requestGetWrongQuestionsNewFormatByQuestionIdRefactor(mQuestions)
                //mQuestions = DataHelper(baseContext).getWrongQuestionsByQuestionId(Integer(mQuestionId))
            } else if (isFromExamFragment) {
                showLoading(true)
                //requestGetQuestionsByExamIdRefactor(mExamId)
                requestGetQuestionsNewFormatByExamIdRefactor(mExamId)
            } else {
                showLoading(true)
                //requestGetQuestionsByModuleIdRefactor(mModuleId)
                requestGetQuestionsNewFormatByModuleIdRefactor(mModuleId)
            }

            // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
            MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713")
            // Use an activity context to get the rewarded video instance.
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
            mRewardedVideoAd.rewardedVideoAdListener = this
            // RequestAdd
            loadRewardedVideoAd()

            mInterstitialAd = InterstitialAd(this)
            mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
            mInterstitialAd.loadAd(AdRequest.Builder().build())
            mInterstitialAd.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    // Code to be executed when an ad request fails.
                }

                override fun onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                override fun onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                override fun onAdClosed() {
                    // Code to be executed when when the interstitial ad is closed.
                }
            }
        }
    }

    private fun loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                AdRequest.Builder().build())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode.equals(SHOW_ANSWER_RESULT_CODE)) {
            val showAnswer = data!!.getBooleanExtra(SET_CHECKED_TAG, false)
            if (showAnswer) {
                if (currentFragment is QuestionFragmentRefactor) {
                    (currentFragment as QuestionFragmentRefactor).showAnswerQuestion()
                }
            }
        } else if (resultCode.equals(SHOW_ANSWER_MESSAGE_RESULT_CODE)) {
            //DataHelper(baseContext).saveCurrentQuestion(mQuestions.get(mCurrentQuestion))
            DataHelper(baseContext).saveCurrentQuestionNewFormat(mQuestionsNewFormat.get(mCurrentQuestion))
            showAnswer()
        }
    }

    override fun onBackPressed() {
        try {
            if (isAnonymous) {
                goLogInActivityStartFragment()
            } else if (mShowPaymentFragment) {
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
        } catch (exception: Exception) {}
    }

    private val mCloseQuestionListener = View.OnClickListener {
        if (mCurrentQuestion > 0) {
            if (isFromWrongQuestionFragment || isFromSubjectQuestionFragment) {
                if (isAnonymous) {
                    goLogInActivityStartFragment()
                } else {
                    onBackPressed()
                }
            } else {
                ErrorDialog.newInstance("¿Seguro que quieres salir?",
                        "Perderás los avances.",
                        DialogType.YES_NOT_DIALOG,
                        this)!!
                        .show(supportFragmentManager, "")
            }
        } else {
            if (isAnonymous) {
                goLogInActivityStartFragment()
                //onBackPressed()
            } else {
                onBackPressed()
            }
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
        //if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size -1) {
        if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestionsNewFormat.size -1) {
            if (isFromWrongQuestionFragment) {
                //requestSendAnsweredQuestions(mQuestions, mCourse)
                requestSendAnsweredQuestionsNewFormat(mQuestionsNewFormat, mCourse)
            }
            showQuestion()
            mCurrentQuestion++
        } else if (isAnonymous) {
            saveModulesAndQuestions()
        } else {
            if (isFromWrongQuestionFragment) {
                saveWrongQuestion()
            } else if (isFromSubjectQuestionFragment) {
                saveQuestionSubject()
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
            /*if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size) {
                if (mQuestions.get(mCurrentQuestion).hasStepByStep()) {
                    DataHelper(baseContext).saveCurrentQuestion(mQuestions.get(mCurrentQuestion))
                    showAnswer()
                }
            }*/
            if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestionsNewFormat.size) {
                if (mQuestionsNewFormat.get(mCurrentQuestion).stepByStepData.isNotEmpty()) {
                    DataHelper(baseContext).saveCurrentQuestionNewFormat(mQuestionsNewFormat.get(mCurrentQuestion))
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
        mQuestionsProgress.progress = mCurrentProgress.toInt()
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
    /*fun getQuestion() : Question? {
        if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size) {
            mShowAnswer.isEnabled = mQuestions.get(mCurrentQuestion).hasStepByStep()
            if (isFromWrongQuestionFragment) {
                mQuestiontypeText.text = mQuestions.get(mCurrentQuestion).getSubjectType().value
            }
            return mQuestions.get(mCurrentQuestion)
        }

        return null
    }*/

    /**
     * @return
     *      The current question according to moduleId
     */
    fun getQuestionNewFormat() : QuestionNewFormat? {
        if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestionsNewFormat.size) {
            mShowAnswer.isEnabled = mQuestionsNewFormat.get(mCurrentQuestion).stepByStepData.isNotEmpty()
            if (isFromWrongQuestionFragment) {
                mQuestiontypeText.text = mQuestionsNewFormat.get(mCurrentQuestion).subject.value
            }
            return mQuestionsNewFormat.get(mCurrentQuestion)
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
    /*fun setQuestionAnswer(answer : String, wasOK : Boolean) {
        if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestions.size) {
            mQuestions.get(mCurrentQuestion).setOptionChoosed(answer)
            mQuestions.get(mCurrentQuestion).setWasOK(wasOK)
            if (wasOK) {
                mCorrectQuestions++
            } else {
                mIncorrectQiestions++
            }
        }
    }*/

    /**
     * @param answer
     * @param wasOK
     *      The method set answed choosed by user and set if answer was correct or not
     */
    fun setQuestionNewFormatAnswer(answer : String, wasOK : Boolean) {
        if (mCurrentQuestion >= 0 && mCurrentQuestion < mQuestionsNewFormat.size) {
            mQuestionsNewFormat.get(mCurrentQuestion).chosenOption = answer
            mQuestionsNewFormat.get(mCurrentQuestion).wasOK = wasOK
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

        //if (NetworkUtil.isConnected(baseContext)) {
        //    requestSendAnsweredModules(mModuleList)
        //} else {
        val module = Module()
        module.setId(Integer(mModuleId))
        module.setAnsweredModule(true)
        //module.setQuestions(mQuestions)
        module.setQuestionsNewFormat(mQuestionsNewFormat)
        module.setCorrectQuestions(mCorrectQuestions)
        module.setIncorrectQuestions(mIncorrectQiestions)

        requestSendAnsweredModules(module, mCourse)
        //requestSendAnsweredQuestions(mQuestions, mCourse)
        requestSendAnsweredQuestionsNewFormat(mQuestionsNewFormat, mCourse)
        mModulesAndQuestionsSaved = true

        // this is called on QuestionsCompleteFragment
        if (isAnonymous) {
            goLogInActivity()
        } else {
            showQuestionsCompleteFragment()
        }


    }

    private fun saveExamsAndQuestions() {

        // send data
        //if (NetworkUtil.isConnected(baseContext)) {
        //    requestSendAnsweredExams(mExamList)
        //} else {
        val exam = Exam()
        exam.setExamId(Integer(mExamId))
        //exam.setQuestions(mQuestions)
        exam.setQuestionsNewFormat(mQuestionsNewFormat)
        exam.setHits(mCorrectQuestions)
        exam.setMisses(mIncorrectQiestions)
        exam.setAnsweredExam(true)

            requestSendAnsweredExams(exam, mCourse)
            //requestSendAnsweredQuestions(mQuestions, mCourse)
        requestSendAnsweredQuestionsNewFormat(mQuestionsNewFormat, mCourse)
            mExamAnsQuestionsSaved = true
            showQuestionsCompleteFragment()
            // this is called on QuestionsCompleteFragment
            //onBackPressed()
        //}

    }

    private fun saveQuestionSubject() {
        requestSendAnsweredQuestionsNewFormat(mQuestionsNewFormat, mCourse)
        mSubjectQuestionsSaved = true
        showQuestionsCompleteFragment()
    }

    private fun saveWrongQuestion() {
        //requestSendAnsweredQuestions(mQuestions, mCourse)
        requestSendAnsweredQuestionsNewFormat(mQuestionsNewFormat, mCourse)
        mWrongQuestionsSaved = true
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
        transaction.replace(R.id.complete_question_fragment_container, currentFragment)
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

    fun areSubjectQuestionsSaved() : Boolean {
        return this.mSubjectQuestionsSaved
    }

    fun getSubject() : String {
        return this.mSubject
    }

    fun areWrongQuestionsSaved() : Boolean {
        return this.mWrongQuestionsSaved
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

    private fun goLogInActivityStartFragment() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(SHOW_START, true)
        this.startActivity(intent)
        this.finish()
    }

    /*
     * LISTENER REQUEST QUESTIONS OLD FORMAT
     */
    /*
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
    }*/

    /*
     * LISTENER REQUEST QUESTIONS NEW FORMAT
     */
    override fun onGetQuestionsNewFormatByModuleIdRefactorSuccess(questions: List<QuestionNewFormat>) {
        super.onGetQuestionsNewFormatByModuleIdRefactorSuccess(questions)
        mQuestionsNewFormat = questions
        mModuleNumber.text = mModuleId.toString()
        mQuestiontypeText.text = "Módulo"
        mProgressByQuestion = 100 / questions.size.toFloat()
        showQuestion()
        showLoading(false)
    }

    override fun onGetQuestionsNewFormatByModuleIdRefactorFail(throwable: Throwable) {
        super.onGetQuestionsNewFormatByModuleIdRefactorFail(throwable)
        showLoading(false)
        onBackPressed()
    }

    override fun onGetQuestionsNewFormatByExamIdRefactorSuccess(questions: List<QuestionNewFormat>) {
        super.onGetQuestionsNewFormatByExamIdRefactorSuccess(questions)
        mQuestionsNewFormat = questions
        mModuleNumber.text = mExamId.toString()
        mQuestiontypeText.text = "Examen"
        mProgressByQuestion = 100 / questions.size.toFloat()
        showQuestion()
        showLoading(false)
    }

    override fun onGetQuestionsNewFormatByExamIdRefactorFail(throwable: Throwable) {
        super.onGetQuestionsNewFormatByExamIdRefactorFail(throwable)
        showLoading(false)
        onBackPressed()
    }

    override fun onGetWrongQuestionsNewFormatByQuestionIdRefactorSuccess(questions: List<QuestionNewFormat>) {
        super.onGetWrongQuestionsNewFormatByQuestionIdRefactorSuccess(questions)
        mQuestionsNewFormat = questions
        mModuleNumber.text = ":)"
        if (mQuestionsNewFormat.isNotEmpty()) {
            mQuestiontypeText.text =  mQuestionsNewFormat.get(mCurrentQuestion).subject.value
        }
        mProgressByQuestion = 100 / questions.size.toFloat()
        showQuestion()
        showLoading(false)
    }

    override fun onGetWrongQuestionsNewFormatByQuestionIdRefactorFail(throwable: Throwable) {
        super.onGetWrongQuestionsNewFormatByQuestionIdRefactorFail(throwable)
        showLoading(false)
        ErrorDialog.newInstance("Ocurrió un problema, vuelve a intentarlo",
                DialogType.OK_DIALOG, this)!!
                .show(supportFragmentManager!!, "notAbleNow")
        onBackPressed()
    }

    override fun onGetSubjectQuestionsNewFormatBySubjectQuestionIdSuccess(questions: List<QuestionNewFormat>) {
        super.onGetSubjectQuestionsNewFormatBySubjectQuestionIdSuccess(questions)
        mQuestionsNewFormat = questions
        mModuleNumber.text = ":)"
        if (mQuestionsNewFormat.isNotEmpty()) {
            mQuestiontypeText.text =  mQuestionsNewFormat.get(mCurrentQuestion).subject.value
        }
        mProgressByQuestion = 100 / questions.size.toFloat()
        showQuestion()
        showLoading(false)
    }

    override fun onGetSubjectQuestionsNewFormatBySubjectQuestionIdFail(throwable: Throwable) {
        super.onGetSubjectQuestionsNewFormatBySubjectQuestionIdFail(throwable)
        showLoading(false)
        ErrorDialog.newInstance("Ocurrió un problema, vuelve a intentarlo",
                DialogType.OK_DIALOG, this)!!
                .show(supportFragmentManager!!, "notAbleNow")
        onBackPressed()
    }

    override fun onGetQuestionsNewFormatBySubjectSuccess(questions: List<QuestionNewFormat>) {
        super.onGetQuestionsNewFormatBySubjectSuccess(questions)
        mQuestionsNewFormat = questions
        mModuleNumber.text = ":)"
        if (mQuestionsNewFormat.isNotEmpty()) {
            mQuestiontypeText.text =  mQuestionsNewFormat.get(mCurrentQuestion).subject.value
        }
        mProgressByQuestion = 100 / questions.size.toFloat()
        showQuestion()
        showLoading(false)
    }

    override fun onGetQuestionsNewFormatBySubjectFail(throwable: Throwable) {
        super.onGetQuestionsNewFormatBySubjectFail(throwable)
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

    fun enableDisableAnswerButton(showButton: Boolean) {
        mShowAnswer.isEnabled = showButton
    }

    override fun onConfirmationCancel() {

    }

    override fun onConfirmationNeutral() {

    }

    override fun onConfirmationAccept() {
        if (isAnonymous) {
            goLogInActivityStartFragment()
        } else {
            onBackPressed()
        }
    }


    /********************************************************************************/

    fun getInterstitialAd() : InterstitialAd? {
        if (::mInterstitialAd.isInitialized) {
            return mInterstitialAd
        } else {
            return null
        }
    }

    fun getRewardedVideoAd() : RewardedVideoAd? {
        if (::mRewardedVideoAd.isInitialized) {
            return mRewardedVideoAd
        } else {
            return null
        }
    }

    override fun onRewarded(reward: RewardItem) {
        //Toast.makeText(this, "onRewarded! currency: ${reward.type} amount: ${reward.amount}", Toast.LENGTH_SHORT).show()
        // Reward the user.
    }

    override fun onRewardedVideoAdLeftApplication() {
        //Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdClosed() {
        //Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
        //Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdLoaded() {
        //Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdOpened() {
        //Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoStarted() {
        //Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoCompleted() {
        //Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show()
    }

}
