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

package com.zerebrez.zerebrez.models.enums

/**
 * Created by Jorge Zepeda Tinoco on 20/03/18.
 * jorzet.94@gmail.com
 */

enum class ErrorType {
    ERROR_NOT_USER,
    EMAIL_NOT_UPDATED,
    PASSWORD_NOT_UPDATED,
    USER_NOT_SENDED,
    SELECTED_SCHOOLS_NOT_SENDED,
    ANSWERED_EXAMS_NOT_SENDED,
    ANSWERED_MODULES_NOT_SENDED,
    ANSWERED_QUESTIONS_NOT_SENDED,
    FACEBOOK_NOT_LINKED,
    FACEBOOK_NOT_SIGNED_IN,
    GOOGLE_NOT_LINKED,
    GOOGLE_NOT_SIGNED_IN,
    NETWORK_ERROR,
    QUESTIONS_ERROR,
    NULL_RESPONSE,
    CANNOT_LOGIN,
    CANNOT_DOWNLOAD_CONTENT
}