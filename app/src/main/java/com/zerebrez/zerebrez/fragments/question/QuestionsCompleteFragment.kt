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

package com.zerebrez.zerebrez.fragments.question

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.ui.activities.LoginActivity
import com.zerebrez.zerebrez.ui.activities.QuestionActivity

private const val TAG : String = "QuestionsCompleteFragment"

/**
 * Created by Jorge Zepeda Tinoco on 02/05/18.
 * jorzet.94@gmail.com
 */

class QuestionsCompleteFragment : BaseContentFragment() {

    /*
     * Tags
     */
    private val SHOW_START : String = "show_start"

    /*
     * UI accessors
     */
    private lateinit var mQuestionTypeText : TextView
    private lateinit var mHitsNumber : TextView
    private lateinit var mMissesNumber : TextView
    private lateinit var mBePremiumButton : View
    private lateinit var mSuperButton : View
    private lateinit var mBePremiumContainer : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.questions_complete_fragment, container, false)

        mQuestionTypeText = rootView.findViewById(R.id.tv_question_type_text)
        mHitsNumber = rootView.findViewById(R.id.tv_hits_number)
        mMissesNumber = rootView.findViewById(R.id.tv_misses_number)
        mBePremiumButton = rootView.findViewById(R.id.btn_be_premium)
        mSuperButton = rootView.findViewById(R.id.btn_super)
        mBePremiumContainer = rootView.findViewById(R.id.rl_be_premium_container)

        val isAfterExam = (activity as QuestionActivity).areExamsAndQuestionsSaved()
        val isAfterModules = (activity as QuestionActivity).areModulesAndQuestionsSaved()
        val isAfterWrongQuestions = (activity as QuestionActivity).areWrongQuestionsSaved()

        mHitsNumber.text = (activity as QuestionActivity).getCorrectQuestions().toString()
        mMissesNumber.text = (activity as QuestionActivity).getIncorrectQuestion().toString()

        if (isAfterModules) {
            val moduleId = (activity as QuestionActivity).getModuleId()
            mQuestionTypeText.text = "Módulo ${moduleId}"
        } else if (isAfterExam) {
            val examId = (activity as QuestionActivity).getExamId()
            mQuestionTypeText.text = "Examen ${examId}"
        } else if (isAfterWrongQuestions) {
            mQuestionTypeText.text = "Incorrectas"
        }

        val user = getUser()
        if (user != null) {
            if (user.isPremiumUser() || (activity as QuestionActivity).isAnonymousUser()) {
                mBePremiumContainer.visibility = View.GONE
            }
        }

        mBePremiumButton.setOnClickListener(mBePremiumButtonListener)
        mSuperButton.setOnClickListener(mSuperButtonListener)

        return rootView
    }

    private val mBePremiumButtonListener = View.OnClickListener {
        (activity as QuestionActivity).showPaymentFragment(true)
        activity!!.onBackPressed()
    }

    private val mSuperButtonListener = View.OnClickListener {
        val isAnonymousUser = (activity as QuestionActivity).isAnonymousUser()
        if (isAnonymousUser) {
            goLogInActivity()
        } else {
            activity!!.onBackPressed()
        }
    }

    /*
     * This method is only used after StartFragment when user responds the first module
     * the app is going to redirect to LoginActivity to show SingUpFragment
     */
    private fun goLogInActivity() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.putExtra(SHOW_START, false)
        this.startActivity(intent)
        activity!!.finish()
    }

}