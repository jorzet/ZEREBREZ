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
import android.media.RemoteController
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.utils.FontUtil

/**
 * Created by Jorge Zepeda Tinoco on 31/10/18.
 * jorzet.94@gmail.com
 */

class ShowQuestionsAdapter(context: Context, questionIds: List<String>, currentQuestionId: String,
                           onQuestionSelectedListener : OnQuestionSelectedListener) :
        RecyclerView.Adapter<QuestionNumberViewHolder>() {

    private val mQuestioIds : List<String> = questionIds
    private val mQuestionSelected : String = currentQuestionId
    private val mContext: Context = context
    private val mOnQuestionSelectedListener : OnQuestionSelectedListener = onQuestionSelectedListener

    interface OnQuestionSelectedListener {
        fun onQuestionSelected(questionId : String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionNumberViewHolder {
        val view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_question_number, parent, false)
        return QuestionNumberViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (mQuestioIds != null && mQuestioIds.isNotEmpty()) {
            return mQuestioIds.size
        } else {
            return 0
        }
    }

    override fun onBindViewHolder(holder: QuestionNumberViewHolder, position: Int) {
        if (holder != null) {
            val questionId = getQuestionIdByPosition(position)
            if (questionId != null) {
                if (mQuestionSelected.equals(questionId)) {
                    holder.mQuestionNumberBackground.background = mContext.resources.getDrawable(R.drawable.selected_question_background)
                } else {
                    holder.mQuestionNumberBackground.background = mContext.resources.getDrawable(R.drawable.unselected_question_background)
                }

                holder.mQuestionNumberText.typeface = FontUtil.getNunitoBlack(mContext)
                holder.mQuestionNumberText.text = (position + 1).toString()

                holder.mQuestionNumberBackground.setOnClickListener(object: View.OnClickListener {
                    override fun onClick(v: View?) {
                        if (mOnQuestionSelectedListener != null) {
                            mOnQuestionSelectedListener.onQuestionSelected(questionId)
                        }
                    }
                })
            }
        }
    }

    fun getQuestionIdByPosition(position: Int) : String? {
        if (mQuestioIds != null && mQuestioIds.isNotEmpty()) {
            return mQuestioIds[position]
        } else {
            return null
        }
    }

}

class QuestionNumberViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val mQuestionNumberBackground = view.findViewById(R.id.question_number_background) as View
    val mQuestionNumberText = view.findViewById(R.id.question_number_text) as TextView
}