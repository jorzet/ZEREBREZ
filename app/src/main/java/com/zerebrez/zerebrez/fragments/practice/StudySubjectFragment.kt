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
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.SubjectListAdapter
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.Subject
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog

/**
 * Created by Jorge Zepeda Tinoco on 25/04/18.
 * jorzet.94@gmail.com
 */

class StudySubjectFragment : BaseContentFragment(), AdapterView.OnItemClickListener, ErrorDialog.OnErrorDialogListener {

    /*
     * UI accessors
     */
    private lateinit var mSubjectList: ListView
    private lateinit var mGoToBottom : ImageView
    private lateinit var mNotSubjectCurrently : TextView

    /*
     * adapter
     */
    private lateinit var subjectListAdapter : SubjectListAdapter

    /*
     * Data accessor
     */
    private lateinit var mDataHelper : DataHelper

    /*
     * Objects
     */
    var updatedsubjects = arrayListOf<Subject>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.study_subject_fragment, container, false)!!

        mSubjectList = rootView.findViewById(R.id.lv_subject_container)
        mGoToBottom = rootView.findViewById(R.id.iv_go_to_bottom)
        mNotSubjectCurrently = rootView.findViewById(R.id.tv_not_subjects_currently)

        // TODO it is hardcoded
        val subjects = arrayListOf<Subject>()
        val subject1 = Subject()
        subject1.setSubjectType(SubjectType.VERBAL_HABILITY)
        subject1.setSubjectAverage(0.0)
        subjects.add(subject1)
        val subject2 = Subject()
        subject2.setSubjectType(SubjectType.MATHEMATICAL_HABILITY)
        subject2.setSubjectAverage(0.0)
        subjects.add(subject2)

        val subject5 = Subject()
        subject5.setSubjectType(SubjectType.CHEMISTRY)
        subject5.setSubjectAverage(0.0)
        subjects.add(subject5)

        val subject3 = Subject()
        subject3.setSubjectType(SubjectType.SPANISH)
        subject3.setSubjectAverage(0.0)
        subjects.add(subject3)

        val subject8 = Subject()
        subject8.setSubjectType(SubjectType.GEOGRAPHY)
        subject8.setSubjectAverage(0.0)
        subjects.add(subject8)

        val subject10 = Subject()
        subject10.setSubjectType(SubjectType.UNIVERSAL_HISTORY)
        subject10.setSubjectAverage(0.0)
        subjects.add(subject10)

        val subject11 = Subject()
        subject11.setSubjectType(SubjectType.FCE)
        subject11.setSubjectAverage(0.0)
        subjects.add(subject11)

        val subject4 = Subject()
        subject4.setSubjectType(SubjectType.MATHEMATICS)
        subject4.setSubjectAverage(0.0)
        subjects.add(subject4)

        val subject6 = Subject()
        subject6.setSubjectType(SubjectType.PHYSICS)
        subject6.setSubjectAverage(0.0)
        subjects.add(subject6)

        val subject7 = Subject()
        subject7.setSubjectType(SubjectType.BIOLOGY)
        subject7.setSubjectAverage(0.0)
        subjects.add(subject7)

        val subject9 = Subject()
        subject9.setSubjectType(SubjectType.MEXICO_HISTORY)
        subject9.setSubjectAverage(0.0)
        subjects.add(subject9)


        if (subjects.isEmpty()) {
            mSubjectList.visibility = View.GONE
            mGoToBottom.visibility = View.GONE
            mNotSubjectCurrently.visibility = View.VISIBLE
        } else {
            subjectListAdapter = SubjectListAdapter(subjects, context!!)
            mSubjectList.adapter = subjectListAdapter
            mSubjectList.setOnItemClickListener(this)
            mGoToBottom.setOnClickListener(mGoToBottomListener)
        }

        return rootView
    }

    private val mGoToBottomListener = View.OnClickListener {
        mSubjectList.post(Runnable {
            // Select the last row so it will scroll into view...
            mSubjectList.setSelection(subjectListAdapter.getCount() - 1)
        })
    }

    /*
     * Subject listener
     */
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        ErrorDialog.newInstance("Muy pronto", DialogType.OK_DIALOG, this)!!
                .show(fragmentManager!!, "notAbleNow")
    }

    /*
     * Dialog listeners
     */
    override fun onConfirmationCancel() {

    }

    override fun onConfirmationNeutral() {

    }

    override fun onConfirmationAccept() {

    }

}