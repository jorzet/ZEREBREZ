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

package com.zerebrez.zerebrez.models

/**
 * Created by Jorge Zepeda Tinoco on 05/05/18.
 * jorzet.94@gmail.com
 */

class Exam {
    private var examId : Integer = Integer(0)
    private var examImage : String = ""
    private var examText : String = ""
    private var hits : Int = 0
    private var misses : Int = 0
    private var questions : List<Question> = arrayListOf()
    private var answeredExam : Boolean = false

    fun setExamId(examId : Integer) {
        this.examId = examId
    }

    fun getExamId() : Integer {
        return this.examId
    }

    fun setExamImage(examImage : String) {
        this.examImage = examImage
    }

    fun getExamImage() : String {
        return this.examImage
    }

    fun setExamText(examText : String) {
        this.examText = examText
    }

    fun getExamText() : String {
        return this.examText
    }

    fun setHits(hits : Int) {
        this.hits = hits
    }

    fun getHits() : Int {
        return this.hits
    }

    fun setMisses(misses : Int) {
        this.misses = misses
    }

    fun getMisses() : Int {
        return this.misses
    }

    /**
     * @param questions
     *      Set a question list object
     */
    fun setQuestions(questions : List<Question>) {
        this.questions = questions
    }

    /**
     * @return
     *      A question list object
     */
    fun getQuestions() : List<Question> {
        return this.questions
    }

    fun setAnsweredExam(answeredExam : Boolean) {
        this.answeredExam = answeredExam
    }

    fun isAnsweredExam() : Boolean {
        return this.answeredExam
    }
}