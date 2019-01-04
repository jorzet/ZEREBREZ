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
 * Created by Jorge Zepeda Tinoco on 28/04/18.
 * jorzet.94@gmail.com
 */

class Module {
    private var moduleId : Integer = Integer(0)
    private var moduleName : String = ""
    private var answeredModule : Boolean = false
    //private var questions : List<Question> = arrayListOf()
    private var questionsNewFormat : List<QuestionNewFormat> = arrayListOf()
    private var freeModule : Boolean = false
    private var correctQuestions : Int = 0
    private var incorrectQuestions : Int = 0

    /**
     * @param id
     *      Set the module id
     */
    fun setId(id : Integer) {
        this.moduleId = id
    }

    /**
     * @return
     *      the module id
     */
    fun getId() : Integer {
        return this.moduleId
    }

    /**
     * @param moduleName
     *      Set the module name
     */
    fun setModuleName(moduleName : String) {
        this.moduleName = moduleName
    }

    /**
     * @return
     *      the module name
     */
    fun getModuleName() : String {
        return this.moduleName
    }

    /**
     * @param answeredModule
     *      Set if module is answered
     */
    fun setAnsweredModule(answeredModule : Boolean) {
        this.answeredModule = answeredModule
    }

    /**
     * @return
     *      if moduele is answered
     */
    fun isAnsweredModule() : Boolean {
        return this.answeredModule
    }

    /**
     * @param questions
     *      Set a question list object
     */
    /*fun setQuestions(questions : List<Question>) {
        this.questions = questions
    }

    /**
     * @return
     *      A question list object
     */
    fun getQuestions() : List<Question> {
        return this.questions
    }*/

    /**
     * @param questionsNewFormat
     *      Set a question list object
     */
    fun setQuestionsNewFormat(questionsNewFormat : List<QuestionNewFormat>) {
        this.questionsNewFormat = questionsNewFormat
    }

    /**
     * @return
     *      A question new format list object
     */
    fun getQuestionsNewFormat() : List<QuestionNewFormat> {
        return this.questionsNewFormat
    }

    /**
     * @param freeModule
     *      Set if is free module
     */
    fun setFreeModule(freeModule : Boolean) {
        this.freeModule = freeModule
    }

    /**
     * @return
     *      if current module is free
     */
    fun isFreeModule() : Boolean {
        return this.freeModule
    }

    fun setCorrectQuestions(correctQuestions : Int) {
        this.correctQuestions = correctQuestions
    }

    fun getCorrectQuestions() : Int {
        return this.correctQuestions
    }

    fun setIncorrectQuestions(incorrectQuestions : Int) {
        this.incorrectQuestions = incorrectQuestions
    }

    fun getIncorrectQuestions() : Int {
        return this.incorrectQuestions
    }
}