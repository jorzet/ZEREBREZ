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

import com.zerebrez.zerebrez.models.enums.SubjectType

/**
 * Created by Jorge Zepeda Tinoco on 28/04/18.
 * jorzet.94@gmail.com
 */

class Question {
    private var questionId : Integer = Integer(0)
    private var moduleId : Integer = Integer(0)
    private var subjectType : SubjectType = SubjectType.NONE
    private var texts : List<String> = arrayListOf()
    private var images : List<String> = arrayListOf()
    private var equations : List<String> = arrayListOf()
    private var stepByStepTexts : List<String> = arrayListOf()
    private var stepByStepImages : List<String> = arrayListOf()
    private var stepByStepEquations : List<String> = arrayListOf()
    private var optionOne : String = ""
    private var optionTwo : String = ""
    private var optionThree : String = ""
    private var optionFour : String = ""
    private var answer : String = ""
    private var year : String = ""
    private var questionType : String = ""
    private var optionChoosed : String = ""
    private var wasOK : Boolean = false

    /**
     * @param questionId
     *      Set the question id
     */
    fun setQuestionId(questionId : Integer) {
        this.questionId = questionId
    }

    /**
     * @return
     *      the question id
     */
    fun getQuestionId() : Integer {
        return this.questionId
    }

    /**
     * @param moduleId
     *      Set the module id
     */
    fun setModuleId(moduleId : Integer) {
        this.moduleId = moduleId
    }

    /**
     * @return
     *      the module id
     */
    fun getModuleId() : Integer {
        return this.moduleId
    }

    /**
     * @param subject
     *      Set the question subject
     */
    fun setSubjectType(subjectType : SubjectType) {
        this.subjectType = subjectType
    }

    /**
     * @return
     *      the question subject
     */
    fun getSubjectType() : SubjectType {
        return this.subjectType
    }

    /**
     * @param text
     *      Set the question texts
     */
    fun setText(texts : List<String>) {
        this.texts = texts
    }

    /**
     * @return
     *      the question texts
     */
    fun getText() : List<String> {
        return this.texts
    }

    /**
     * @param equations
     *      Set the question equations
     */
    fun setEquations(equations : List<String>) {
        this.equations = equations
    }

    /**
     * @return
     *      the question equations
     */
    fun getEquations() : List<String> {
        return this.equations
    }

    /**
     * @param images
     *      Set the question images
     */
    fun setImages(images : List<String>) {
        this.images = images
    }

    /**
     * @return
     *      the question images
     */
    fun getImages() : List<String> {
        return this.images
    }

    /**
     * @param text
     *      Set the step by step question text
     */
    fun setStepByStepText(stepByStepTexts : List<String>) {
        this.stepByStepTexts = stepByStepTexts
    }

    /**
     * @return
     *      the question step by step text
     */
    fun getStepByStepText() : List<String> {
        return this.stepByStepTexts
    }

    /**
     * @param equations
     *      Set the question step by step equations
     */
    fun setStepByStepEquations(stepByStepEquations : List<String>) {
        this.stepByStepEquations = stepByStepEquations
    }

    /**
     * @return
     *      the question step by step equations
     */
    fun getStepByStepEquations() : List<String> {
        return this.stepByStepEquations
    }

    /**
     * @param images
     *      Set the question step by step images
     */
    fun setStepByStepImages(stepByStepImages : List<String>) {
        this.stepByStepImages = stepByStepImages
    }

    /**
     * @return
     *      the question step by step image
     */
    fun getStepByStepImages() : List<String> {
        return this.stepByStepImages
    }

    /**
     * @param optionOne
     *      Set the option text one
     */
    fun setOptionOne(optionOne : String) {
        this.optionOne = optionOne
    }

    /**
     * @return
     *      the option text one
     */
    fun getOptionOne() : String {
        return this.optionOne
    }

    /**
     * @param optionTwo
     *      Set the option text two
     */
    fun setOptionTwo(optionTwo : String) {
        this.optionTwo = optionTwo
    }

    /**
     * @return
     *      the option text two
     */
    fun getOptionTwo() : String {
        return this.optionTwo
    }

    /**
     * @param optionThree
     *      Set the option text three
     */
    fun setOptionThree(optionThree : String) {
        this.optionThree = optionThree
    }

    /**
     * @return
     *      the option text three
     */
    fun getOptionThree() : String {
        return this.optionThree
    }

    /**
     * @param optionFour
     *      Set the option text four
     */
    fun setOptionFour(optionFour : String) {
        this.optionFour = optionFour
    }

    /**
     * @return
     *      the option text four
     */
    fun getOptionFour() : String {
        return this.optionFour
    }

    /**
     * @param answer
     *      Set the question answer
     */
    fun setAnswer(answer : String) {
        this.answer = answer
    }

    /**
     * @return
     *      the question answer
     */
    fun getAnswer() : String {
        return this.answer
    }

    /**
     * @param year
     *      Set the year
     */
    fun setYear(year : String) {
        this.year = year
    }

    /**
     * @return
     *      the year
     */
    fun getYear() : String {
        return this.year
    }

    /**
     * @param questionType
     *      Set the question type
     */
    fun setQuestionType(questionType : String) {
        this.questionType = questionType
    }

    /**
     * @return
     *      The question type
     */
    fun getQuestionType() : String {
        return this.questionType
    }


    /**
     * @param optionChoosed
     *      Set the option choosed
     */
    fun setOptionChoosed(optionChoosed : String) {
        this.optionChoosed = optionChoosed
    }

    /**
     * @return
     *      The option choosed
     */
    fun getOptionChoosed() : String {
        return this.optionChoosed
    }

    /**
     * @param wasOK
     *      if was ok the answer
     */
    fun setWasOK(wasOK : Boolean) {
        this.wasOK = wasOK
    }

    /**
     * @return
     *      if answer was ok
     */
    fun getWasOK() : Boolean {
        return this.wasOK
    }

    fun hasStepByStep() : Boolean {
        return stepByStepTexts.size > 0 || stepByStepEquations.size > 0 || stepByStepImages.size > 0
    }
}
