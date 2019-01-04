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
        var examDescription: String,
        var examId: String = "",
        var userScore : Integer = Integer(0),
        var totalNumberOfQuestions : Integer = Integer(0)
)