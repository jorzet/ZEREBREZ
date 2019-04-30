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

package com.zerebrez.zerebrez.models

/**
 * Created by Jorge Zepeda Tinoco on 06/05/18.
 * jorzet.94@gmail.com
 */

class ExamScore {

    private var mExamScoreId : Integer = Integer(0)
    private var mUserScore : Integer = Integer(0)
    private var mTotalNumberOfQuestions : Integer = Integer(0)
    private var mOtherUsersScoreExam : List<UserScoreExam> = arrayListOf()

    fun setExamScoreId(examScoreId : Integer) {
        this.mExamScoreId = examScoreId
    }

    fun getExamScoreId() : Integer {
        return this.mExamScoreId
    }

    fun setUserScore(userScore : Integer) {
        this.mUserScore = userScore
    }

    fun getUserScore() : Integer {
        return this.mUserScore
    }

    fun setTotalNumberOfQuestion(totalNumberOfQuestions : Integer) {
        this.mTotalNumberOfQuestions = totalNumberOfQuestions
    }

    fun getTotalNumberOfQuestions() : Integer {
        return this.mTotalNumberOfQuestions
    }

    fun setOtherUsersScoreExam(userScoreExam : List<UserScoreExam>) {
        this.mOtherUsersScoreExam = userScoreExam
    }

    fun getOtherUsersScoreExam() : List<UserScoreExam> {
        return this.mOtherUsersScoreExam
    }

}