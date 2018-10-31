package com.zerebrez.zerebrez.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.utils.FontUtil

class ShowQuestionsAdapter(context: Context, questionIds: List<String>, currentQuestionId: String) : RecyclerView.Adapter<QuestionNumberViewHolder>() {

    private val mQuestioIds : List<String> = questionIds
    private val mQuestionSelected : String = currentQuestionId
    private val mContext: Context = context

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
            if (mQuestionSelected.equals(getQuestionIdByPosition(position))) {

            } else {

            }

            holder.mQuestionNumberText.typeface = FontUtil.getNunitoBlack(mContext)
            holder.mQuestionNumberText.text = (position + 1).toString()
        }
    }

    fun getQuestionIdByPosition(position: Int) : String {
        return mQuestioIds[position]
    }

}

class QuestionNumberViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val mQuestionNumberBackground = view.findViewById(R.id.question_number_background) as View
    val mQuestionNumberText = view.findViewById(R.id.question_number_text) as TextView
}