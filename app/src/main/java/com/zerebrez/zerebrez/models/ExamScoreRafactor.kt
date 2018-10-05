package com.zerebrez.zerebrez.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Jorge Zepeda Tinoco on 19/09/18.
 * jorzet.94@gmail.com
 */

data class ExamScoreRafactor (
        // Serialized attributes
        @SerializedName("average")
        @Expose
        var average: Double = 0.0,
        @SerializedName("best")
        @Expose
        var best: Double = 0.0,
        // this is to identify exam exmp. e1, e2, e3
        var examId: String = "",
        var userScore : Integer = Integer(0),
        var totalNumberOfQuestions : Integer = Integer(0)
)