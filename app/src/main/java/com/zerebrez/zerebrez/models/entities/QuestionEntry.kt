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

package com.zerebrez.zerebrez.models.entities

import android.provider.BaseColumns

/**
 * Created by Jorge Zepeda Tinoco on 29/04/18.
 * jorzet.94@gmail.com
 */

object QuestionEntry : BaseColumns {
    const val TABLE_NAME = "question"
    const val COLUMN_ID = "id"
    const val COLUMN_QUESTION_ID = "question_id"
    const val COLUMN_MODULE_ID = "module_id"
    const val COLUMN_SUBJECT = "subject"
    const val COLUMN_TEXT = "text"
    const val COLUMN_OPTION_ONE = "option_one"
    const val COLUMN_OPTION_TWO = "option_two"
    const val COLUMN_OPTION_THREE = "option_three"
    const val COLUMN_OPTION_FOUR = "option_four"
    const val COLUMN_ANSWER = "answer"
    const val COLUMN_YEAR = "year"
    const val COLUMN_TYPE = "type"
}