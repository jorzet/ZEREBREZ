package com.zerebrez.zerebrez.models

import com.zerebrez.zerebrez.models.enums.QuestionType

class QuestionOption {
    private var mQuestion : String = ""
    private var mQuestionType : QuestionType = QuestionType.NONE

    fun setQuestion(question : String) {
        this.mQuestion = question
    }

    fun getQuestion() : String {
        return this.mQuestion
    }

    fun setQuestionType(questionType : QuestionType) {
        this.mQuestionType = questionType
    }

    fun getQuestionType() : QuestionType {
        return this.mQuestionType
    }
}