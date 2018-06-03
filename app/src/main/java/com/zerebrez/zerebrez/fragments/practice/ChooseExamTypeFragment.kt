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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment

/**
 * Created by Jorge Zepeda Tinoco on 03/05/18.
 * jorzet.94@gmail.com
 */

class ChooseExamTypeFragment : BaseContentFragment() {

    private lateinit var mChocolateExam : View
    private lateinit var mRealExam : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.choose_exam_type_fragment, container, false)!!

        mChocolateExam = rootView.findViewById(R.id.rl_chocolate_exam)
        mRealExam = rootView.findViewById(R.id.rl_real_exam)

        mChocolateExam.setOnClickListener(mChocolateExamListener)
        mRealExam.setOnClickListener(mRealExamListener)

        return rootView
    }

    private val mChocolateExamListener = View.OnClickListener {
        goExamFragment()
    }

    private val mRealExamListener = View.OnClickListener {
        goExamFragment()
    }

    private fun goExamFragment() {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.do_exam_fragment_container, ExamFragment())
        transaction.commit()
    }
}