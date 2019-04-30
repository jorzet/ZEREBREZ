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

import com.zerebrez.zerebrez.models.enums.SubjectType

/**
 * Created by Jorge Zepeda Tinoco on 06/05/18.
 * jorzet.94@gmail.com
 */

class Subject {
    private var subjectType : SubjectType = SubjectType.NONE
    private var subjectAverage : Double = 0.0

    fun setSubjectType(subjectType: SubjectType) {
        this.subjectType = subjectType
    }

    fun getsubjectType() : SubjectType {
        return this.subjectType
    }

    fun setSubjectAverage(subjectAverage : Double) {
        this.subjectAverage = subjectAverage
    }

    fun getSubjectAverage() : Double {
        return this.subjectAverage
    }

}