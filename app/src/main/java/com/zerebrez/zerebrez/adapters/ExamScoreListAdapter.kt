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
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.utils.ColorUtils
import com.zerebrez.zerebrez.utils.FontUtil
import kotlinx.android.synthetic.main.custom_exam.view.*

/**
 * Created by Jorge Zepeda Tinoco on 12/05/18.
 * jorzet.94@gmail.com
 */

class ExamScoreListAdapter (exams : List<Exam>, context : Context) : BaseAdapter() {

    private val mExams: List<Exam> = exams
    private val mContext: Context = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val currentExam = getItem(position) as Exam

        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val examView = inflator.inflate(R.layout.custom_exam, null)

        examView.tv_exam.text = currentExam.getDescription()
        if (currentExam.getHits().equals(1)) {
            examView.tv_hits_number.text = currentExam.getHits().toString() + " acierto de " + (currentExam.getHits() + currentExam.getMisses())
        } else {
            examView.tv_hits_number.text = currentExam.getHits().toString() + " aciertos de " + (currentExam.getHits() + currentExam.getMisses())
        }

        examView.tv_exam.typeface = FontUtil.getNunitoSemiBold(mContext)
        examView.tv_hits_number.typeface = FontUtil.getNunitoSemiBold(mContext)

        // generate random color
        //val color = ColorGenerator.MATERIAL.getColor(getItem(position))

        val background = mContext.resources.getDrawable(ColorUtils.mExamColors[position%ColorUtils.mExamColors.size])
        examView.rl_background.background = background

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