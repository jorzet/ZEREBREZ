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

package com.zerebrez.zerebrez.fragments.content

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.zerebrez.zerebrez.models.*
import com.zerebrez.zerebrez.models.enums.ComproPagoStatus
import com.zerebrez.zerebrez.request.RequestManager

/**
 * Created by Jorge Zepeda Tinoco on 27/02/18.
 * jorzet.94@gmail.com
 */

abstract class BaseContentDialogFragment : BaseDialogFragment() {

    private lateinit var mRequestManager : RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRequestManager = RequestManager(activity as Activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun requestGetProfileRefactor() {
        mRequestManager.requestGetProfileRefactor(object : RequestManager.OnGetProfileRefactorListener {
            override fun onGetProfileRefactorLoaded(user: User) {
                onGetProfileRefactorSuccess(user)
            }

            override fun onGetProfileRefactorError(throwable: Throwable) {
                onGetProfileRefactorFail(throwable)
            }
        })
    }


    fun requestSendUserComproPago(user : User, billingId: String, comproPagoStatus: ComproPagoStatus) {
        mRequestManager.requestSendUserComproPago(user, billingId, comproPagoStatus, object : RequestManager.OnSendUserComproPagoListener {
            override fun onSendUserComproPagoLoaded(success: Boolean) {
                onSendUserComproPagoSuccess(success)
            }

            override fun onSendUserComproPagoError(throwable: Throwable) {
                onSendUserComproPagoFail(throwable)
            }
        })
    }

    open fun onGetProfileRefactorSuccess(user: User) {}
    open fun onGetProfileRefactorFail(throwable: Throwable) {}
    open fun onSendUserComproPagoSuccess(success: Boolean) {}
    open fun onSendUserComproPagoFail(throwable: Throwable) {}

}