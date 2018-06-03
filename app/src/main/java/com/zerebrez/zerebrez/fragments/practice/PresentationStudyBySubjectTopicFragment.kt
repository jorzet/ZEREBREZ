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
import android.widget.Button
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment

/**
 * Created by Jorge Zepeda Tinoco on 25/04/18.
 * jorzet.94@gmail.com
 */

class PresentationStudyBySubjectTopicFragment : BaseContentFragment() {

    /*
     * UI accessors
     */
    private lateinit var mItIsUnderstandButton : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.presentation_study_by_subject_and_topic_fragment, container, false)!!

        mItIsUnderstandButton = rootView.findViewById(R.id.btn_it_is_understand)

        mItIsUnderstandButton.setOnClickListener(mItIsUnderstandedListener)

        return rootView
    }

    private val mItIsUnderstandedListener = View.OnClickListener(){
        setStudySubjectFragmentOK()
        goStudySubjectFragment()
    }

    private fun goStudySubjectFragment() {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.study_subject_fragment_container, StudySubjectFragment())
        transaction.commit()
    }
}