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
import android.widget.ListView
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.ExamAverageListAdapterRefactor
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.ExamScore
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.utils.FontUtil

/**
 * Created by Jorge Zepeda Tinoco on 20/03/18.
 * jorzet.94@gmail.com
 */

class ExamsAverageFragment : BaseContentFragment() {

    /*
     * UI accessors
     */
    private lateinit var examsAverageListView : ListView
    private lateinit var notExamsDidIt : TextView

    /*
     * Objects
     */
    private lateinit var examsAverageListAdapter: ExamAverageListAdapterRefactor

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.exams_average_fragment, container, false)!!

        examsAverageListView = rootView.findViewById(R.id.lv_exams_average)
        notExamsDidIt = rootView.findViewById(R.id.tv_not_exams_currently)

        notExamsDidIt.typeface = FontUtil.getNunitoSemiBold(context!!)

        val dataHelper = DataHelper(context!!)
        val examScores = dataHelper.getExamScores()
        val exams = dataHelper.getExams()
        val mExamsDidIt = arrayListOf<ExamScore>()
        for (examScore in examScores) {
            for (exam in exams) {
                if (exam.getExamId() == examScore.getExamScoreId() && exam.isAnsweredExam()) {
                    examScore.setUserScore(Integer(exam.getHits()))
                    examScore.setTotalNumberOfQuestion(Integer(exam.getQuestions().size))
                    mExamsDidIt.add(examScore)
                }
            }
        }

        if (mExamsDidIt.isEmpty()) {
            examsAverageListView.visibility = View.GONE
            notExamsDidIt.visibility = View.VISIBLE
        } else {
            examsAverageListAdapter = ExamAverageListAdapterRefactor(mExamsDidIt, context!!)
            examsAverageListView.adapter = examsAverageListAdapter
        }

        return rootView
    }
}