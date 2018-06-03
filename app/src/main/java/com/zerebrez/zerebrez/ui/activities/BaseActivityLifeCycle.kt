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

package com.zerebrez.zerebrez.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zerebrez.zerebrez.models.*
import com.zerebrez.zerebrez.request.RequestManager
import com.zerebrez.zerebrez.services.sharedpreferences.JsonParcer
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager

/**
 * Created by Jorge Zepeda Tinoco on 27/02/18.
 * jorzet.94@gmail.com
 */

open class BaseActivityLifeCycle : AppCompatActivity() {

    companion object {
        val SET_CHECKED_TAG : String = "set_checked_tag"
        val SHOW_PAYMENT_FRAGMENT : String = "show_payment_fragment"
        val SHOW_QUESTION_RESULT_CODE : Int = 1
        val SHOW_ANSWER_RESULT_CODE : Int = 2
        val SHOW_ANSWER_MESSAGE_RESULT_CODE : Int = 3
        val SHOW_PAYMENT_FRAGMENT_RESULT_CODE : Int = 4
    }

    private lateinit var mRequestManager : RequestManager

    override fun onStart() {
        super.onStart()
        mRequestManager = RequestManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRequestManager = RequestManager(this)
    }

    override fun onStop() {
        super.onStop()
    }

    open fun saveUser(user : User) {
        val json = JsonParcer.parceObjectToJson(user)
        SharedPreferencesManager(baseContext).storeJsonUser(json)
    }

    open fun getUser() : User? {
        val json = SharedPreferencesManager(baseContext).getJsonUser()
        if (json != null) {
            return JsonParcer.getObjectFromJson(json, User::class.java) as User
        } else {
            return null
        }
    }

    open fun setLogInData(hasLogInData : Boolean) {
        SharedPreferencesManager(baseContext).storeLogInData(hasLogInData)
    }

    open fun hasLogInData() : Boolean {
        val isLogedIn = SharedPreferencesManager(baseContext).getLogInData()
        return isLogedIn
    }

    open fun getTermsAndPrivacy() : String {
        val termsAndPrivacy = SharedPreferencesManager(baseContext).getTermsAndPrivacy()
        return termsAndPrivacy
    }

    fun requestSendAnsweredQuestions(modules : List<Module>) {
        mRequestManager.requestSendAnsweredQuestions(modules, object : RequestManager.OnSendAnsweredQuestionsListener {
            override fun onSendAnsweredQuestionsLoaded(success: Boolean) {
                onSendAnsweredQuestionsSuccess(success)
            }

            override fun onSendAnsweredQuestionsError(throwable: Throwable) {
                onSendAnsweredQuestionsFail(throwable)
            }
        })
    }

    fun requestSendAnsweredModules(modules : List<Module>) {
        mRequestManager.requestSendAnsweredModules(modules, object : RequestManager.OnSendAnsweredModulesListener {
            override fun onSendAnsweredModulesLoaded(success: Boolean) {
                onSendAnsweredModulesSuccess(success)
            }

            override fun onSendAnsweredModulesError(throwable: Throwable) {
                onSendAnsweredModulesFail(throwable)
            }
        })
    }

    fun requestSendAnsweredExams(exams : List<Exam>) {
        mRequestManager.requestSendAnsweredExams(exams, object : RequestManager.OnSendAnsweredExamsListener {
            override fun onSendAnsweredExamsLoaded(success: Boolean) {
                onSendAnsweredExamsSuccess(success)
            }

            override fun onSendAnsweredExamsError(throwable: Throwable) {
                onSendAnsweredExamsFail(throwable)
            }
        })
    }

    fun requestSendSelectedSchools(schools : List<School>) {
        mRequestManager.requestSendSelectedSchools(schools, object : RequestManager.OnSendSelectedSchoolsListener {
            override fun onSendSelectedSchoolsLoaded(success: Boolean) {
                onSendSelectedSchoolsSuccess(success)
            }

            override fun onSendSelectedSchoolsError(throwable: Throwable) {
                onSendSelectedSchoolsFail(throwable)
            }
        })
    }

    fun requestGetExamScores() {
        mRequestManager.requestGetExamScores(object : RequestManager.OnGetExamScoresListener {
            override fun onGetExamScoresLoaded(result: List<ExamScore>) {
                onGetExamScoresSuccess(result)
            }

            override fun onGetExamScoresError(throwable: Throwable) {
                onGetExamScoresFail(throwable)
            }
        })
    }

    open fun onSendAnsweredQuestionsSuccess(success : Boolean) {
    }

    open fun onSendAnsweredQuestionsFail(throwable: Throwable) {
    }

    open fun onSendAnsweredModulesSuccess(success: Boolean) {
    }

    open fun onSendAnsweredModulesFail(throwable: Throwable) {
    }

    open fun onSendAnsweredExamsSuccess(success: Boolean) {
    }

    open fun onSendAnsweredExamsFail(throwable: Throwable) {
    }

    open fun onSendSelectedSchoolsSuccess(success: Boolean) {
    }

    open fun onSendSelectedSchoolsFail(throwable: Throwable) {
    }

    open fun onGetExamScoresSuccess(examScores : List<ExamScore>) {
    }

    open fun onGetExamScoresFail(throwable: Throwable) {
    }

}