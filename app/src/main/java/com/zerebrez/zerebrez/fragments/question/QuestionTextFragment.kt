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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.NonScrollListView
import com.zerebrez.zerebrez.adapters.OptionTextAdapter
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.Question
import com.zerebrez.zerebrez.ui.activities.QuestionActivity

/**
 * Created by Jorge Zepeda Tinoco on 01/05/18.
 * jorzet.94@gmail.com
 */

class QuestionTextFragment : BaseContentFragment(), View.OnClickListener {

    /*
     * UI accessors
     */
    private lateinit var mQuestion : TextView
    private lateinit var mOptionList : NonScrollListView
    private lateinit var mTextAnswerOne : TextView
    private lateinit var mTextAnswerTwo : TextView
    private lateinit var mTextAnswerThree : TextView
    private lateinit var mTextAnswerFour : TextView
    private lateinit var mOptionA : View
    private lateinit var mOptionB : View
    private lateinit var mOptionC : View
    private lateinit var mOptionD : View

    /*
     * tags
     */
    private val OPTION_A : String = "a"
    private val OPTION_B : String = "b"
    private val OPTION_C : String = "c"
    private val OPTION_D : String = "d"

    private lateinit var optionTextAdapter: OptionTextAdapter

    private var question : Question? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.question_text_fragment, container, false)!!

        mQuestion = rootView.findViewById(R.id.tv_question)
        mOptionList = rootView.findViewById(R.id.nslv_option_container)
        mTextAnswerOne = rootView.findViewById(R.id.answer_a)
        mTextAnswerTwo = rootView.findViewById(R.id.answer_b)
        mTextAnswerThree = rootView.findViewById(R.id.answer_c)
        mTextAnswerFour = rootView.findViewById(R.id.answer_d)
        mOptionA = rootView.findViewById(R.id.option_a)
        mOptionB = rootView.findViewById(R.id.option_b)
        mOptionC = rootView.findViewById(R.id.option_c)
        mOptionD = rootView.findViewById(R.id.option_d)

        question = (activity as QuestionActivity).getQuestion()
        if (question != null) {
            val optionText = question!!.getText()
            optionTextAdapter = OptionTextAdapter(optionText, context!!)

            setOptions()
            setAnswers()
        }

        return rootView
    }


    private fun setOptions() {
        mQuestion.setText("Lee atentamente")
        mOptionList.adapter = optionTextAdapter
    }

    private fun setAnswers() {
        mTextAnswerOne.setText(question!!.getOptionOne())
        mTextAnswerTwo.setText(question!!.getOptionTwo())
        mTextAnswerThree.setText(question!!.getOptionThree())
        mTextAnswerFour.setText(question!!.getOptionFour())
        mOptionA.setOnClickListener(this)
        mOptionB.setOnClickListener(this)
        mOptionC.setOnClickListener(this)
        mOptionD.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        val answer = question!!.getAnswer()

        when (view!!.id) {
            R.id.option_a -> {
                when (answer) {
                    OPTION_A -> {
                        mOptionA.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("a", true)
                    }
                    OPTION_B -> {
                        mOptionA.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionB.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("a", false)
                    }
                    OPTION_C -> {
                        mOptionA.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionC.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("a", false)
                    }
                    OPTION_D -> {
                        mOptionA.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionD.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("a", false)
                    }
                }
            }
            R.id.option_b -> {
                when (answer) {
                    OPTION_A -> {
                        mOptionB.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionA.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("b", false)
                    }
                    OPTION_B -> {
                        mOptionB.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("b", true)
                    }
                    OPTION_C -> {
                        mOptionB.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionC.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("b", false)
                    }
                    OPTION_D -> {
                        mOptionC.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionD.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("b", false)
                    }
                }
            }
            R.id.option_c -> {
                when (answer) {
                    OPTION_A -> {
                        mOptionC.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionA.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("c", false)
                    }
                    OPTION_B -> {
                        mOptionC.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionB.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("c", false)
                    }
                    OPTION_C -> {
                        mOptionC.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("c", true)
                    }
                    OPTION_D -> {
                        mOptionC.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionD.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("c", false)
                    }
                }
            }
            R.id.option_d -> {
                when (answer) {
                    OPTION_A -> {
                        mOptionD.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionA.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("d", false)
                    }
                    OPTION_B -> {
                        mOptionD.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionB.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("d", false)
                    }
                    OPTION_C -> {
                        mOptionD.background = resources.getDrawable(R.drawable.answer_wrong_option_background)
                        mOptionC.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("d", false)
                    }
                    OPTION_D -> {
                        mOptionD.background = resources.getDrawable(R.drawable.answer_correct_option_background)
                        (activity as QuestionActivity).setQuestionAnswer("d", true)
                    }
                }
            }
        }


    }

}