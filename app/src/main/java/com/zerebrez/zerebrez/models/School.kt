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
 * Created by Jorge Zepeda Tinoco on 06/05/18.
 * jorzet.94@gmail.com
 */

class School {
    private var instituteId : Integer = Integer(0)
    private var schoolId : Integer = Integer(0)
    private var schoolName : String = ""
    private var hitsNumber : Int = 0

    fun setInstituteId(instituteId : Integer) {
        this.instituteId = instituteId
    }

    fun getInstituteId() : Integer {
        return this.instituteId
    }

    fun setSchoolId(schoolId : Integer) {
        this.schoolId = schoolId
    }

    fun getSchoolId() : Integer {
        return this.schoolId
    }

    fun setSchoolName(schoolName : String) {
        this.schoolName = schoolName
    }

    fun getSchoolName() : String {
        return this.schoolName
    }

    fun setHitsNumber(hitsNumber : Int) {
        this.hitsNumber = hitsNumber
    }

    fun getHitsNumber() : Int {
        return this.hitsNumber
    }
}