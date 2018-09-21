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

package com.zerebrez.zerebrez.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.ExamScore
import com.zerebrez.zerebrez.models.ExamScoreRafactor
import com.zerebrez.zerebrez.services.firebase.score.ExamsScoreRequest
import com.zerebrez.zerebrez.utils.FontUtil
import kotlinx.android.synthetic.main.custom_exam_average_refactor.view.*

/**
 * Created by Jorge Zepeda Tinoco on 13/05/18.
 * jorzet.94@gmail.com
 */

private const val TAG : String = "ExamAverageListAdapterRefactor"

class ExamAverageListAdapterRefactor(averageExams : List<ExamScoreRafactor>, context : Context) : BaseAdapter() {

    private val mAverageExams : List<ExamScoreRafactor> = averageExams
    private val mContext : Context = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val currentAverageExam = getItem(position) as ExamScore

        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val examView = inflator.inflate(R.layout.custom_exam_average_refactor, null)

        examView.tv_exam_number.text = "EXAMEN " + currentAverageExam.getExamScoreId()
        examView.bc_exams_average.setExamScores(currentAverageExam.getOtherUsersScoreExam())
        examView.bc_exams_average.setUserHits(currentAverageExam.getUserScore().toInt())
        examView.bc_exams_average.setHighestScore(currentAverageExam.getTotalNumberOfQuestions().toInt())

        examView.tv_exam_number.typeface = FontUtil.getNunitoBold(mContext)

        return examView
    }

    override fun getItem(position: Int): Any {
        return this.mAverageExams.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return this.mAverageExams.size
    }

}