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

package com.zerebrez.zerebrez.request

/**
 * Created by Jorge Zepeda Tinoco on 06/05/18.
 * jorzet.94@gmail.com
 */

abstract class AbstractPendingRequest {
    private lateinit var mResponse : String

    protected lateinit var onRequestListenerSucces : OnRequestListenerSuccess
    protected lateinit var onRequestLietenerFailed : OnRequestListenerFailed

    interface OnRequestListenerSuccess {
        fun onSuccess(result: Any?)
    }

    interface OnRequestListenerFailed {
        fun onFailed(result: Throwable)
    }

    fun setOnRequestSuccess(onRequestSuccess: OnRequestListenerSuccess) {
        this.onRequestListenerSucces = onRequestSuccess
    }

    fun setOnRequestFailed(onRequestFailed: OnRequestListenerFailed) {
        this.onRequestLietenerFailed = onRequestFailed
    }
}