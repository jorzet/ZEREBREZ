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
 * Created by Jorge Zepeda Tinoco on 06/05/18.
 * jorzet.94@gmail.com
 */

class AverageExams {
    private var myExamId : Int = 0
    private var myAverage : Int = 0
    private var beastAverage : Int = 0
    private var usersAverage : Int = 0

    fun setMyExamId(myExamId : Int) {
        this.myExamId = myExamId
    }

    fun getMyExamId() : Int {
        return this.myExamId
    }

    fun setMyAverage(myAverage : Int) {
        this.myAverage = myAverage
    }

    fun getMyAverage() : Int {
        return this.myAverage
    }

    fun setBeastAverage(beastAverage : Int) {
        this.beastAverage = beastAverage
    }

    fun getBeastAverage() : Int {
        return this.beastAverage
    }

    fun setUsersAverage(usersAverage : Int) {
        this.usersAverage = usersAverage
    }

    fun getUsersAverage() : Int {
        return this.usersAverage
    }
}