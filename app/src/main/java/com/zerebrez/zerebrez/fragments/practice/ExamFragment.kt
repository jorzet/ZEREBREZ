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

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.ExamListAdapter
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.ui.activities.BaseActivityLifeCycle
import com.zerebrez.zerebrez.ui.activities.ContentActivity
import com.zerebrez.zerebrez.ui.activities.QuestionActivity
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog

/**
 * Created by Jorge Zepeda Tinoco on 03/05/18.
 * jorzet.94@gmail.com
 */

class ExamFragment : BaseContentFragment(), AdapterView.OnItemClickListener, ErrorDialog.OnErrorDialogListener {

    /*
     * Tags
     */
    private val TAG : String = "ExamFragment"
    private val FROM_EXAM_FRAGMENT : String = "from_exam_fragment"
    private val ANONYMOUS_USER : String = "anonymous_user"
    private val EXAM_ID : String = "exam_id"

    /*
     * UI accessors
     */
    private lateinit var mExamList : ListView
    private lateinit var mNotExamsCurrentlyTextView : TextView

    /*
     * adapter
     */
    private lateinit var examListAdapter : ExamListAdapter

    /*
     * Data accessor
     */
    private lateinit var mDataHelper : DataHelper

    /*
     * Objects
     */
    private var mUpdatedExams : List<Exam> = arrayListOf()
    private lateinit var mUser : User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.exam_fragment, container, false)!!

        mExamList = rootView.findViewById(R.id.lv_exam_container)
        mNotExamsCurrentlyTextView = rootView.findViewById(R.id.tv_not_exams_currently)

        requestGetExamsRefactor()

        return rootView
    }

    override fun onResume() {
        super.onResume()

        requestGetExamsRefactor()

    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, position: Int, p3: Long) {
        Log.d(TAG, "item clicked--- position: " + position)
        if (mUpdatedExams.isNotEmpty()) {
            
            if (mUser.isPremiumUser() || mUpdatedExams.get(position).isFreeExam()) {
                val examId = mUpdatedExams.get(position).getExamId()
                goQuestionActivity(examId.toInt())
            } else {
                (activity as ContentActivity).goPaymentFragment()
                /*ErrorDialog.newInstance("Vuelvete premium para desbloquear mas módulos",
                        DialogType.OK_DIALOG, this)!!
                        .show(fragmentManager!!, "")*/
            }
        }
    }

    private fun goQuestionActivity(examId : Int) {
        val intent = Intent(activity, QuestionActivity::class.java)
        intent.putExtra(EXAM_ID, examId)
        intent.putExtra(ANONYMOUS_USER, false)
        intent.putExtra(FROM_EXAM_FRAGMENT, true)
        this.startActivityForResult(intent, BaseActivityLifeCycle.SHOW_QUESTION_RESULT_CODE)
    }

    /*
     * Dialog listeners
     */

    override fun onConfirmationAccept() {
    }

    override fun onConfirmationNeutral() {
        (activity as ContentActivity).goPaymentFragment()
    }

    override fun onConfirmationCancel() {
    }

    /*
     * Firebase listeners
     */
    override fun onGetExamsRefactorSuccess(exams: List<Exam>) {
        super.onGetExamsRefactorSuccess(exams)
        mUpdatedExams = exams
        requestGetFreeExamsRefactor()
    }

    override fun onGetExamsRefactorFail(throwable: Throwable) {
        super.onGetExamsRefactorFail(throwable)
    }

    override fun onGetFreeExamsRefactorSuccess(freeExams: List<Exam>) {
        super.onGetFreeExamsRefactorSuccess(freeExams)


        for (i in 0 .. mUpdatedExams.size - 1) {
            for (freeModule in freeExams) {
                if (mUpdatedExams.get(i).getExamId().equals(freeModule.getExamId())) {
                    mUpdatedExams.get(i).setFreeExam(true)
                }
            }
        }

        requestGetAnsweredExamsAndProfileRefactor()
    }

    override fun onGetFreeExamsRefactorFail(throwable: Throwable) {
        super.onGetFreeExamsRefactorFail(throwable)
        if (activity != null)
            (activity as ContentActivity).showLoading(false)
    }

    override fun onGetAnsweredExamsAndProfileRefactorSuccess(user: User) {
        super.onGetAnsweredExamsAndProfileRefactorSuccess(user)

        if (context != null) {
            mUser = user
            saveUser(user)
            val answeredExams = user.getAnsweredExams()

            for (i in 0..mUpdatedExams.size - 1) {
                for (answeredExam in answeredExams) {
                    if (mUpdatedExams.get(i).getExamId().equals(answeredExam.getExamId())) {
                        mUpdatedExams.get(i).setAnsweredExam(true)
                        mUpdatedExams.get(i).setHits(answeredExam.getHits())
                        mUpdatedExams.get(i).setMisses(answeredExam.getMisses())
                    }
                }
            }

            if (mUpdatedExams.isNotEmpty()) {
                examListAdapter = ExamListAdapter(mUser, mUpdatedExams, context!!)
                mExamList.adapter = examListAdapter
                mExamList.setOnItemClickListener(this)
            } else {
                mNotExamsCurrentlyTextView.visibility = View.VISIBLE
                mExamList.visibility = View.GONE

            }
        }
        if (activity != null)
            (activity as ContentActivity).showLoading(false)
    }

    override fun onGetAnsweredExamsAndProfileRefactorFail(throwable: Throwable) {
        super.onGetAnsweredExamsAndProfileRefactorFail(throwable)
        if (activity != null)
            (activity as ContentActivity).showLoading(false)
    }

}