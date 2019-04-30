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

package com.zerebrez.zerebrez.fragments.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.components.SchoolAverageCanvas
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.School
import com.zerebrez.zerebrez.ui.activities.ContentActivity
import com.zerebrez.zerebrez.utils.FontUtil

/**
 * Created by Jorge Zepeda Tinoco on 20/03/18.
 * jorzet.94@gmail.com
 */

class SchoolsAverageFragment : BaseContentFragment() {

    /**
     * UI accessors
     */
    private lateinit var schoolAverageCanvas : SchoolAverageCanvas
    private lateinit var mNot128ExmanQuestionDitIt : TextView
    private lateinit var mComipemsYearTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.school_average_fragment, container, false)!!

        mComipemsYearTextView = rootView.findViewById(R.id.tv_comipems_year)
        mNot128ExmanQuestionDitIt = rootView.findViewById(R.id.tv_not_128_exams_questions_currently)
        schoolAverageCanvas = rootView.findViewById(R.id.school_average)

        mComipemsYearTextView.typeface = FontUtil.getNunitoBold(context!!)
        mNot128ExmanQuestionDitIt.typeface = FontUtil.getNunitoSemiBold(context!!)

        requestGetUserSelectedSchoolsRefactor()

        return rootView
    }

    override fun onGetUserSelectedSchoolsRefactorSuccess(schools: List<School>) {
        super.onGetUserSelectedSchoolsRefactorSuccess(schools)

        if (context != null) {
            if (schools.isNotEmpty()) {
                schoolAverageCanvas.setSchools(schools)
                schoolAverageCanvas.invalidate()
                //schoolAverageCanvas.setUserHits(1)

                val user = getUser()
                if (user != null && !user.getCourse().equals("")) {
                    requestGetCourseExamMaxScore(user.getCourse())
                }
            }
        }
    }

    override fun onGetUserSelectedSchoolsRefactorFail(throwable: Throwable) {
        super.onGetUserSelectedSchoolsRefactorFail(throwable)
        if (activity != null)
            (activity as ContentActivity).showLoading(false)
    }


    override fun onGetCourseExamMaxScoreSuccess(score: String) {
        super.onGetCourseExamMaxScoreSuccess(score)

        if (context != null) {
            schoolAverageCanvas.setMaxHits(score.toInt())
            schoolAverageCanvas.invalidate()
            mNot128ExmanQuestionDitIt.text = "Aún no tienes examenes de ${score} preguntas contestados"
            requestGetScoreLast128QuestionsExam()
        }
    }

    override fun onGetCourseExamMexScoreFail(throwable: Throwable) {
        super.onGetCourseExamMexScoreFail(throwable)
        if (activity != null)
            (activity as ContentActivity).showLoading(false)
    }

    override fun onGetScoreLast128QuestionsExamSuccess(score: Int) {
        super.onGetScoreLast128QuestionsExamSuccess(score)
        if (context != null) {
            schoolAverageCanvas.setUserHits(score)
            schoolAverageCanvas.invalidate()
            mNot128ExmanQuestionDitIt.visibility = View.GONE
            if (activity != null)
                (activity as ContentActivity).showLoading(false)
        }
    }

    override fun onGetScoreLast128QuestionsExamFail(throwable: Throwable) {
        super.onGetScoreLast128QuestionsExamFail(throwable)
        schoolAverageCanvas.setUserHits(0)
        schoolAverageCanvas.invalidate()
        mNot128ExmanQuestionDitIt.visibility = View.VISIBLE
        if (activity != null)
            (activity as ContentActivity).showLoading(false)
    }

}