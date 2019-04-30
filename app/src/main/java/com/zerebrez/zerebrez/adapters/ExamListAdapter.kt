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

package com.zerebrez.zerebrez.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.utils.FontUtil
import kotlinx.android.synthetic.main.custom_option_exam.view.*

/**
 * Created by Jorge Zepeda Tinoco on 05/05/18.
 * jorzet.94@gmail.com
 */

class ExamListAdapter (user : User, exams : List<Exam>, context : Context) : BaseAdapter() {
    private val mExams: List<Exam> = exams
    private val mContext: Context = context
    private val mUser : User = user

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val currentExam = getItem(position) as Exam

        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val examView = inflator.inflate(R.layout.custom_option_exam, null)

        examView.tv_exam.text = currentExam.getDescription()
        if (currentExam.isAnsweredExam()){
            examView.rl_correct_incorrect_container.visibility = View.VISIBLE
            examView.rl_exam_not_done_container.visibility = View.GONE
            examView.iv_exam.setImageDrawable(mContext.resources.getDrawable(R.drawable.exam_done_icon))
            examView.tv_exam.setTextColor(mContext.resources.getColor((R.color.exam_done_text_color)))
            examView.tv_correct.text = currentExam.getHits().toString()
            examView.tv_incorrect.text = currentExam.getMisses().toString()
            examView.tv_exam.typeface = FontUtil.getNunitoRegular(mContext)
            examView.tv_correct.typeface = FontUtil.getNunitoRegular(mContext)
            examView.tv_incorrect.typeface = FontUtil.getNunitoRegular(mContext)

        } else {
            examView.rl_correct_incorrect_container.visibility = View.GONE
            examView.rl_exam_not_done_container.visibility = View.VISIBLE
            examView.iv_exam.setImageDrawable(mContext.resources.getDrawable(R.drawable.exam_icon))
            examView.tv_exam.setTextColor(mContext.resources.getColor((R.color.exam_not_done_text_color)))
            examView.tv_question_number.text = currentExam.getQuestionsNewFormat().size.toString()
            examView.tv_exam.typeface = FontUtil.getNunitoRegular(mContext)
            examView.tv_question_number.typeface = FontUtil.getNunitoRegular(mContext)

            if (!mUser.isPremiumUser() && !currentExam.isFreeExam()) {
                examView.tv_question_number.setTextColor(mContext.resources.getColor(R.color.exam_not_done_text_color))
                examView.question_text.setTextColor(mContext.resources.getColor(R.color.exam_not_done_text_color))
            }
        }

        return examView
    }

    override fun getItem(position: Int): Any {
        return this.mExams.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return this.mExams.size
    }
}
