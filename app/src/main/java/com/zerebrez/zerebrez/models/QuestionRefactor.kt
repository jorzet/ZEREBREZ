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

data class QuestionRefactor (
        // the serialized attributes are got from Firebase
        @SerializedName("topic")
        @Expose
        var topic: String = "",
        @SerializedName("subtopic")
        @Expose
        var subtopic: String = "",
        @SerializedName("subject")
        @Expose
        var subject: String = "",
        @SerializedName("answer")
        @Expose
        var answer: String = "",
        @SerializedName("stepByStepTypes")
        @Expose
        var stepByStepTypes: List<String> = ArrayList(),
        @SerializedName("stepByStepData")
        @Expose
        var stepByStepData: List<String> = ArrayList(),
        @SerializedName("questionTypes")
        @Expose
        var questionTypes: List<String> = ArrayList(),
        @SerializedName("questionData")
        @Expose
        var questionData: List<String> = ArrayList(),
        @SerializedName("optionsTypes")
        @Expose
        var optionsTypes: List<String> = ArrayList(),
        @SerializedName("optionsData")
        @Expose
        var optionsData: List<String> = ArrayList(),

        // those attributes changes in app
        var questionId: String = "", // this is to identify the question, examp. q1, q2, q3
        var optionChoosed : String = "",
        var wasOK : Boolean = false
        )