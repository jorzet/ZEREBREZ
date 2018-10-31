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

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.ShowQuestionsAdapter
import com.zerebrez.zerebrez.utils.FontUtil
import android.support.v7.widget.GridLayoutManager



/**
 * Created by Jorge Zepeda Tinoco on 31/10/18.
 * jorzet.94@gmail.com
 */

class ShowQuestionsActivity : BaseActivityLifeCycle() {

    /*
     * Tags
     */
    private val FROM_SUBJECT_QUESTION : String = "from_subject_question"
    private val FROM_WRONG_QUESTION : String = "from_wrong_question"
    private val FROM_EXAM_FRAGMENT : String = "from_exam_fragment"
    private val QUESTION_IDS_LIST : String = "questions_ids_list"
    private val CURRENT_QUESTION_ID : String = "current_question_id"

    /*
     * UI accessors
     */
    private lateinit var mReturnToQuestionButton: View
    private lateinit var mReturnToQuestionText: TextView
    private lateinit var mQuestionsGrid: RecyclerView

    /*
     * Adapters
     */
    private lateinit var mShowQuestionsAdapter : ShowQuestionsAdapter

    /*
     * Objects
     */
    private lateinit var mQuestionsId : List<String>

    /*
     * Variables
     */
    private lateinit var mCurrentQuestionId : String
    private var isFromSubjectQuestionFragment : Boolean = false
    private var isFromWrongQuestionFragment : Boolean = false
    private var isFromExamFragment : Boolean = false
    private var mIsRequestiong : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_show_questions)

        mReturnToQuestionButton = findViewById(R.id.rl_return_to_answer)
        mReturnToQuestionText = findViewById(R.id.tv_return_to_answer)
        mQuestionsGrid = findViewById(R.id.rv_questions)

        mReturnToQuestionText.typeface = FontUtil.getNunitoBlack(baseContext)

        mReturnToQuestionButton.setOnClickListener(mReturnToQuestionListener)

        isFromSubjectQuestionFragment = intent.getBooleanExtra(FROM_SUBJECT_QUESTION, false)
        isFromWrongQuestionFragment = intent.getBooleanExtra(FROM_WRONG_QUESTION, false)
        isFromExamFragment = intent.getBooleanExtra(FROM_EXAM_FRAGMENT, false)
        mQuestionsId = intent.getSerializableExtra(QUESTION_IDS_LIST) as List<String>
        mCurrentQuestionId = intent.getStringExtra(CURRENT_QUESTION_ID)

        if (isFromSubjectQuestionFragment) {

        } else if (isFromWrongQuestionFragment) {

        } else if (isFromExamFragment) {

        } else {

        }


        val user = getUser()
        if (user != null && !user.getCourse().equals("")) {

            mIsRequestiong = true
        }

        val mGridLayoutManager = GridLayoutManager(this, 6)
        mShowQuestionsAdapter = ShowQuestionsAdapter(baseContext, mQuestionsId, mCurrentQuestionId)
        mQuestionsGrid.adapter = mShowQuestionsAdapter
        mQuestionsGrid.layoutManager = mGridLayoutManager


    }

    override fun onResume() {
        super.onResume()

        if (!mIsRequestiong) {
            val user = getUser()
            if (user != null && !user.getCourse().equals("")) {

            }
        }

        val mGridLayoutManager = GridLayoutManager(this, 6)
        mShowQuestionsAdapter = ShowQuestionsAdapter(baseContext, mQuestionsId, mCurrentQuestionId)
        mQuestionsGrid.adapter = mShowQuestionsAdapter
        mQuestionsGrid.layoutManager = mGridLayoutManager
    }

    private val mReturnToQuestionListener = View.OnClickListener {
        onBackPressed()
    }

}