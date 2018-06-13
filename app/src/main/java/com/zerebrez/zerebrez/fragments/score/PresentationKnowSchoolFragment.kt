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

package com.zerebrez.zerebrez.fragments.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.ui.activities.ContentActivity
import com.zerebrez.zerebrez.utils.FontUtil

/**
 * Created by Jorge Zepeda Tinoco on 25/04/18.
 * jorzet.94@gmail.com
 */

class PresentationKnowSchoolFragment : BaseContentFragment() {

    /*
     * UI accessors
     */
    private lateinit var mItIsUnderstandButton : View
    private lateinit var mPracticeTextView: TextView
    private lateinit var mNeedOneReamExamTextView: TextView
    private lateinit var mItIsUnderstandTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.presentation_know_the_school, container, false)!!

        mItIsUnderstandButton = rootView.findViewById(R.id.btn_it_is_understand)
        mPracticeTextView = rootView.findViewById(R.id.text_practice_questions)
        mNeedOneReamExamTextView = rootView.findViewById(R.id.text_need_to_have_one_exam)
        mItIsUnderstandTextView = rootView.findViewById(R.id.btn_it_is_understand_text)

        mPracticeTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mNeedOneReamExamTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mItIsUnderstandTextView.typeface = FontUtil.getNunitoSemiBold(context!!)

        mItIsUnderstandButton.setOnClickListener(mItIsUnderstandListener)

        return rootView
    }

    private val mItIsUnderstandListener = View.OnClickListener(){
        if (activity != null)
            (activity as ContentActivity).showLoading(true)
        setSchoolAverageFragmentOK()
        goSchoolAverageFragment()
    }

    fun goSchoolAverageFragment() {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.know_school_fragment_container, SchoolsAverageFragment())
        transaction.commit()
    }
}