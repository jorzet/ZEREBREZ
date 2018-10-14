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

package com.zerebrez.zerebrez.services.payment

import android.app.Activity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.zerebrez.zerebrez.request.AbstractPendingRequest

/*
 * Created by Jorge Zepeda Tinoco on 28/04/18.
 * jorzet.94@gmail.com
 */

private const val TAG : String = "Payment"

class Payment(activity: Activity) : AbstractPendingRequest() {

    /*
    * request code
    */
    private val LOAD_PAYMENT_DATA_REQUEST_CODE: Int = 0x2341

    private val mActivity = activity

    /*
     * Payment
     */
    private var mPaymentsClient: PaymentsClient

    init {
        // instance payment client api
        mPaymentsClient = Wallet.getPaymentsClient(
                mActivity,
                Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                        .build())
    }

    private fun isReadyToPay() {
        val request = IsReadyToPayRequest.newBuilder()
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                .build()
        val task = mPaymentsClient.isReadyToPay(request)
        task.addOnCompleteListener(
                object : OnCompleteListener<Boolean> {
                    override fun onComplete(task: Task<Boolean>) {
                        try {
                            val result = task.getResult(ApiException::class.java)
                            if (result != null) {
                                onShowGooglePayment(result)
                            }
                        } catch (exception: ApiException) {
                        }
                    }
                })
    }

    fun onShowGooglePayment(success : Boolean) {
        if (success) {
            // Show Google as payment option.
        } else {
            // Hide Google as payment option.
        }
    }

}