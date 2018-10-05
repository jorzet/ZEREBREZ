package com.zerebrez.zerebrez.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.zerebrez.zerebrez.models.enums.SubjectType

data class QuestionNewFormat (
        @SerializedName("answer")
        @Expose
        var answer : String = "",
        @SerializedName("optionsData")
        @Expose
        var optionsData : List<String> = arrayListOf(),
        @SerializedName("optionsTypes")
        @Expose
        var optionsTypes : List<String> = arrayListOf(),
        @SerializedName("questionData")
        @Expose
        var questionData : List<String> = arrayListOf(),
        @SerializedName("questionTypes")
        @Expose
        var questionTypes : List<String> = arrayListOf(),
        @SerializedName("stepByStepData")
        @Expose
        var stepByStepData : List<String> = arrayListOf(),
        @SerializedName("stepByStepTypes")
        @Expose
        var stepByStepTypes : List<String> = arrayListOf(),
        @SerializedName("subject")
        @Expose
        var subject : SubjectType = SubjectType.NONE,
        @SerializedName("subtopic")
        @Expose
        var subtopic : String = "",
        @SerializedName("topic")
        @Expose
        var topic : String = "",
        @SerializedName("year")
        @Expose
        var year : String = "",

        var questionId : String = "",
        var chosenOption : String = "",
        var wasOK : Boolean = false
)