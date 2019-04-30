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
import com.zerebrez.zerebrez.models.enums.SubjectType

/**
 * Created by Jorge Zepeda Tinoco on 20/09/18.
 * jorzet.94@gmail.com
 */

data class SubjectRefactor (

        @SerializedName("internalName")
        @Expose
        var internalName: String = "",
        @SerializedName("nameToDisplay")
        @Expose
        var nameToDisplay: String = "",
        // this is to identify course exmp. s1, s2, s3
        var subjectId: String = "",
        var subjectType : SubjectType = SubjectType.NONE,
        var subjectAverage : Double = 0.0
)