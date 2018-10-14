package com.zerebrez.zerebrez.fragments.practice

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.QuestionNewFormat
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.ui.activities.BaseActivityLifeCycle
import com.zerebrez.zerebrez.ui.activities.ContentActivity
import com.zerebrez.zerebrez.ui.activities.QuestionActivity

class StudySubjectQuestionsFragment : BaseContentFragment() {

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
    private lateinit var mLeftTableLayout : GridLayout
    private lateinit var mCenterTableLayout : GridLayout
    private lateinit var mRightTableLayout : GridLayout
    private lateinit var mNotSubjectQuestionsCurrently : TextView
    private lateinit var mMainContainer : View

    /*
     * Objects
     */
    private var mQuestionList = arrayListOf<QuestionNewFormat>()
    private var mUpdatedQuestions = arrayListOf<QuestionNewFormat>()
    private var mWrongQuestionsId = arrayListOf<Int>()
    private lateinit var mUser : User


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null


        val rootView = inflater.inflate(R.layout.study_subject_question_fragment, container, false)!!

        mLeftTableLayout = rootView.findViewById(R.id.table_left)
        mCenterTableLayout = rootView.findViewById(R.id.table_center)
        mRightTableLayout = rootView.findViewById(R.id.table_right)
        mNotSubjectQuestionsCurrently = rootView.findViewById(R.id.tv_not_subject_questions_currently)
        mMainContainer = rootView.findViewById(R.id.sv_main_container)

        if (activity != null) {
            val user = (activity as ContentActivity).getUserProfile()
            if (user != null && !user.getCourse().equals("")) {
                requestGetWrongQuestionsAndProfileRefactor(user.getCourse())
            }
        }

        return rootView
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