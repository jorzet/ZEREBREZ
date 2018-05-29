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
import android.widget.AdapterView
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.NonScrollListView
import com.zerebrez.zerebrez.adapters.OptionEquationAdapter
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.Question
import com.zerebrez.zerebrez.ui.activities.QuestionActivity

/**
 * Created by Jorge Zepeda Tinoco on 01/05/18.
 * jorzet.94@gmail.com
 */

class QuestionEquationFragment : BaseContentFragment(){

    /*
     * UI accessors
     */
    private lateinit var mQuestion : TextView
    private lateinit var mEquationList : NonScrollListView
    private lateinit var mTextOptionOne : TextView
    private lateinit var mTextOptionTwo : TextView
    private lateinit var mTextOptionThree : TextView
    private lateinit var mTextOptionFour : TextView
    private lateinit var mOptionOne : View
    private lateinit var mOptionTwo : View
    private lateinit var mOptionThree : View
    private lateinit var mOptionFour : View

    /*
     * adapter
     */
    private lateinit var optionEquationAdapter : OptionEquationAdapter

    /*
     * current question
     */
    private var question : Question? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater?.inflate(R.layout.question_equation_fragment, container, false)!!

        mQuestion = rootView.findViewById(R.id.tv_question)
        mEquationList = rootView.findViewById(R.id.nslv_equation_container)
        mTextOptionOne = rootView.findViewById(R.id.tv_option_a)
        mTextOptionTwo = rootView.findViewById(R.id.tv_option_b)
        mTextOptionThree = rootView.findViewById(R.id.tv_option_c)
        mTextOptionFour = rootView.findViewById(R.id.tv_option_d)
        mOptionOne = rootView.findViewById(R.id.rl_option_a)
        mOptionTwo = rootView.findViewById(R.id.rl_option_b)
        mOptionThree = rootView.findViewById(R.id.rl_option_c)
        mOptionFour = rootView.findViewById(R.id.rl_option_d)

        question = (activity as QuestionActivity).getQuestion()
        if (question != null) {
            val optionEquation = question!!.getText()
            optionEquationAdapter = OptionEquationAdapter(optionEquation, context!!)
            setOptions()
            setAnswers()
        }

        return rootView
    }

    /*
     * item listener
     */
    private val mEquationListListener = object : AdapterView.OnItemClickListener {
        override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        }
    }

    /*
     *
     */
    private fun setOptions() {
        mQuestion.setText("Resuelve la siguiente ecuación:")
        mEquationList.adapter = optionEquationAdapter
        mEquationList.setOnItemClickListener(mEquationListListener)
    }

    /*
     *
     */
    private fun setAnswers() {
        mTextOptionOne.setText(question!!.getOptionOne())
        mTextOptionTwo.setText(question!!.getOptionTwo())
        mTextOptionThree.setText(question!!.getOptionThree())
        mTextOptionFour.setText(question!!.getOptionFour())
    }
}