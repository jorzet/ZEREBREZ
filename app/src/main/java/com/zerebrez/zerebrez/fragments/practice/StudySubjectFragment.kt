/*
 * Copyright [2019] [Jorge Zepeda Tinoco]
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

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
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
import com.zerebrez.zerebrez.models.SubjectRefactor
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.ui.activities.BaseActivityLifeCycle
import com.zerebrez.zerebrez.ui.activities.ContentActivity
import com.zerebrez.zerebrez.ui.activities.QuestionActivity
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.utils.FontUtil

/**
 * Created by Jorge Zepeda Tinoco on 25/04/18.
 * jorzet.94@gmail.com
 */

class StudySubjectFragment : BaseContentFragment(), AdapterView.OnItemClickListener, ErrorDialog.OnErrorDialogListener {

    /*
     * Tags
     */
    private var CURRENT_COURSE : String = "current_course"
    private val FROM_SUBJECT_QUESTION : String = "from_subject_question"
    private val SELECTED_SUBJECT : String = "selected_subject"
    private val ANONYMOUS_USER : String = "anonymous_user"

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
    var updatedsubjects : List<SubjectRefactor> = arrayListOf()

    private lateinit var studyQuestionFragment : Fragment


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.study_subject_fragment, container, false)!!

        mSubjectList = rootView.findViewById(R.id.lv_subject_container)
        mGoToBottom = rootView.findViewById(R.id.iv_go_to_bottom)
        mNotSubjectCurrently = rootView.findViewById(R.id.tv_not_subjects_currently)

        mNotSubjectCurrently.typeface = FontUtil.getNunitoSemiBold(context!!)


        val user = getUser()
        if (user != null && !user.getCourse().equals("")) {
            requestGetSubjects(user.getCourse())
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

        if (updatedsubjects.isNotEmpty()) {
            goStudySubjectQuestionFragment(updatedsubjects[position].internalName, updatedsubjects[position].subjectType)
            //goQuestionActivity(updatedsubjects[position].internalName)
        } else {
            ErrorDialog.newInstance("Ocurrió un problema, vuelve a intentarlo", DialogType.OK_DIALOG, this)!!
                    .show(fragmentManager!!, "notAbleNow")
        }
    }

    override fun onGetSubjectsSuccess(subjects: List<SubjectRefactor>) {
        super.onGetSubjectsSuccess(subjects)
        if (context != null) {
            if (subjects.isEmpty()) {
                mSubjectList.visibility = View.GONE
                mGoToBottom.visibility = View.GONE
                mNotSubjectCurrently.visibility = View.VISIBLE
            } else {
                updatedsubjects = subjects
                subjectListAdapter = SubjectListAdapter(subjects, context!!)
                mSubjectList.adapter = subjectListAdapter
                mSubjectList.setOnItemClickListener(this)
                mGoToBottom.setOnClickListener(mGoToBottomListener)
            }
            if (activity != null) {
                (activity as ContentActivity).showLoading(false)
            }
        }
    }

    override fun onGetSubjectsFail(throwable: Throwable) {
        super.onGetSubjectsFail(throwable)
        if (activity != null) {
            (activity as ContentActivity).showLoading(false)
        }
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

    private fun goStudySubjectQuestionFragment(selectedSubject : String, selectedSubjectType: SubjectType) {
        try {
            studyQuestionFragment = StudySubjectQuestionsFragment()
            (studyQuestionFragment as StudySubjectQuestionsFragment).setSelectedSubject(selectedSubject)
            (studyQuestionFragment as StudySubjectQuestionsFragment).setSelectedSubjectType(selectedSubjectType)
            val transaction = fragmentManager!!.beginTransaction()
            transaction.replace(R.id.study_questions_subject_fragment_container, studyQuestionFragment)
            transaction.commit()
            transaction.addToBackStack("studyQuestionFragment")
        } catch (exception : Exception) {
            val a = 0
        }
    }

    fun getStudySubjectQuestionFragment() : Fragment? {
        if (::studyQuestionFragment.isInitialized) {
            return this.studyQuestionFragment
        } else {
            return  null
        }
    }

    private fun goQuestionActivity(selectedSubject : String) {
        val intent = Intent(activity, QuestionActivity::class.java)
        intent.putExtra(SELECTED_SUBJECT, selectedSubject)
        intent.putExtra(ANONYMOUS_USER, false)
        intent.putExtra(FROM_SUBJECT_QUESTION, true)
        if (activity != null) {
            val user = (activity as ContentActivity).getUserProfile()
            if (user != null && !user.getCourse().equals("")) {
                intent.putExtra(CURRENT_COURSE, user.getCourse())
            }
        }
        this.startActivityForResult(intent, BaseActivityLifeCycle.SHOW_QUESTION_RESULT_CODE)
    }

}