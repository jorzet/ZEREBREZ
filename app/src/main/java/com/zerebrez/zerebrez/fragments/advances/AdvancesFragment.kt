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

package com.zerebrez.zerebrez.fragments.advances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.AverageSubjectListAdapter
import com.zerebrez.zerebrez.adapters.ExamScoreListAdapter
import com.zerebrez.zerebrez.adapters.NonScrollListView
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.models.Subject
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.utils.FontUtil

/**
 * Created by Jorge Zepeda Tinoco on 01/05/18.
 * jorzet.94@gmail.com
 */

class AdvancesFragment : BaseContentFragment() {

    /*
     * UI accessors
     */
    private lateinit var mExamList : NonScrollListView
    private lateinit var mAverageSubjectList : NonScrollListView
    private lateinit var mTotalQuestionTextView : TextView
    private lateinit var mHitsNumberTextView : TextView
    private lateinit var mMissesNumberTextView : TextView
    private lateinit var mNotExamsDidIt : TextView
    private lateinit var mNotAbleNow : TextView
    private lateinit var mQuestionTextView: TextView
    private lateinit var mAnsweredQuestionTextView: TextView
    private lateinit var mExamsTextView: TextView
    private lateinit var mAverageBySubjectTextView: TextView

    /*
     * adapters
     */
    private lateinit var examScoreListAdapter : ExamScoreListAdapter
    private lateinit var averageSubjectListAdapter: AverageSubjectListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.advances_fragment, container, false)!!

        mExamList = rootView.findViewById(R.id.nslv_exams)
        mAverageSubjectList = rootView.findViewById(R.id.nslv_average_by_subject)
        mTotalQuestionTextView = rootView.findViewById(R.id.tv_num_answered_questions)
        mHitsNumberTextView = rootView.findViewById(R.id.tv_hits_number)
        mMissesNumberTextView = rootView.findViewById(R.id.tv_misses_number)
        mNotExamsDidIt = rootView.findViewById(R.id.tv_not_exams_currently)
        mNotAbleNow = rootView.findViewById(R.id.tv_not_able_now)
        mQuestionTextView = rootView.findViewById(R.id.question_text)
        mAnsweredQuestionTextView = rootView.findViewById(R.id.answered_questions_text)
        mExamsTextView = rootView.findViewById(R.id.tv_exams)
        mAverageBySubjectTextView = rootView.findViewById(R.id.tv_average_by_subject)

        mQuestionTextView.typeface = FontUtil.getNunitoBold(context!!)
        mTotalQuestionTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mAnsweredQuestionTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mHitsNumberTextView.typeface = FontUtil.getNunitoBold(context!!)
        mMissesNumberTextView.typeface = FontUtil.getNunitoBold(context!!)
        mExamsTextView.typeface = FontUtil.getNunitoBold(context!!)
        mNotAbleNow.typeface = FontUtil.getNunitoSemiBold(context!!)
        mAverageBySubjectTextView.typeface = FontUtil.getNunitoBold(context!!)


        val user = getUser()
        if (user != null && context != null) {
            val modules = DataHelper(context!!).getModulesAnsQuestions()
            var hits = 0
            var misses = 0
            for (module in modules) {
                hits += module.getCorrectQuestions()
                misses += module.getIncorrectQuestions()
            }
            val total = hits + misses
            mTotalQuestionTextView.text = total.toString()
            mHitsNumberTextView.text = hits.toString()
            mMissesNumberTextView.text = misses.toString()

            val exams = DataHelper(context!!).getExams()
            val answeredExams = arrayListOf<Exam>()
            for (exam in exams) {
                if (exam.isAnsweredExam()) {
                    answeredExams.add(exam)
                }
            }

            if (answeredExams.isEmpty()) {
                mExamList.visibility = View.GONE
                mNotExamsDidIt.visibility = View.VISIBLE
            } else {

                examScoreListAdapter = ExamScoreListAdapter(answeredExams, context!!)
                mExamList.adapter = examScoreListAdapter
            }
        }

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

        val subject3 = Subject()
        subject3.setSubjectType(SubjectType.SPANISH)
        subject3.setSubjectAverage(0.0)
        subjects.add(subject3)

        val subject4 = Subject()
        subject4.setSubjectType(SubjectType.MATHEMATICS)
        subject4.setSubjectAverage(0.0)
        subjects.add(subject4)

        val subject5 = Subject()
        subject5.setSubjectType(SubjectType.CHEMISTRY)
        subject5.setSubjectAverage(0.0)
        subjects.add(subject5)


        val subject6 = Subject()
        subject6.setSubjectType(SubjectType.PHYSICS)
        subject6.setSubjectAverage(0.0)
        subjects.add(subject6)

        val subject7 = Subject()
        subject7.setSubjectType(SubjectType.BIOLOGY)
        subject7.setSubjectAverage(0.0)
        subjects.add(subject7)

        val subject8 = Subject()
        subject8.setSubjectType(SubjectType.GEOGRAPHY)
        subject8.setSubjectAverage(0.0)
        subjects.add(subject8)

        val subject9 = Subject()
        subject9.setSubjectType(SubjectType.MEXICO_HISTORY)
        subject9.setSubjectAverage(0.0)
        subjects.add(subject9)

        val subject10 = Subject()
        subject10.setSubjectType(SubjectType.UNIVERSAL_HISTORY)
        subject10.setSubjectAverage(0.0)
        subjects.add(subject10)

        val subject11 = Subject()
        subject11.setSubjectType(SubjectType.FCE)
        subject11.setSubjectAverage(0.0)
        subjects.add(subject11)

        if (subjects.isEmpty()) {
            mAverageSubjectList.visibility = View.GONE
            mNotAbleNow.visibility = View.VISIBLE
        } else {
            averageSubjectListAdapter = AverageSubjectListAdapter(subjects, context!!)
            mAverageSubjectList.adapter = averageSubjectListAdapter
        }

        return rootView
    }
}