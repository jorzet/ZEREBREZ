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

package com.zerebrez.zerebrez.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import com.google.android.gms.wallet.*

import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import java.util.*

/*
* Created by Jorge Zepeda Tinoco on 27/02/18.
* jorzet.94@gmail.com
*/

private const val TAG : String = "PaywayActivity"

class PaywayActivity : BaseActivityLifeCycle(), ErrorDialog.OnErrorDialogListener {

    /*
     * request code
     */
    private val LOAD_PAYMENT_DATA_REQUEST_CODE: Int = 0x2341

    /*
     * UI accessors
     */
    private lateinit var mToolBar : Toolbar
    private lateinit var mGooglePayView : View
    private lateinit var mMercadoPagoView : View

    /*
     * Payment
     */
    private lateinit var mPaymentsClient: PaymentsClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        mToolBar = findViewById(R.id.toolbar)
        mGooglePayView = findViewById(R.id.rl_google_pay_container)
        mMercadoPagoView = findViewById(R.id.rl_mercado_pago_container)

        setSupportActionBar(mToolBar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true);

        mGooglePayView.setOnClickListener(mGooglePayViewListener)
        mMercadoPagoView.setOnClickListener(mMercadoPagoViewListener)

        // instance payment client api
        mPaymentsClient = Wallet.getPaymentsClient(
                this,
                Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                        .build())

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> when (resultCode) {
                Activity.RESULT_OK -> {
                    val paymentData = PaymentData.getFromIntent(data!!)
                    val token = paymentData!!.paymentMethodToken!!.token
                    Log.d(TAG, token.toString())
                    val user = User()
                    user.setPremiumUser(true)
                    user.setTimeStamp(System.currentTimeMillis().toString())
                    saveUser(user)
                    requestSendUser(user)

                }
                Activity.RESULT_CANCELED -> {
                    // Do nothing.
                }
                AutoResolveHelper.RESULT_ERROR -> {
                    val status = AutoResolveHelper.getStatusFromIntent(data)
                    Log.d(TAG, status.toString())
                    // Log the status for debugging.
                    // Generally, there is no need to show an error to
                    // the user as the Google Pay API will do that.
                }
                else -> {
                    // Do nothing.
                }
            }
            else -> {
                // Do nothing.
            }

        }

        /*val user = User()
        user.setPremiumUser(true)
        user.setTimeStamp(System.currentTimeMillis().toString())
        saveUser(user)
        requestSendUser(user)*/

    }

    private fun createPaymentDataRequest(): PaymentDataRequest? {
        val request = PaymentDataRequest.newBuilder()
                .setTransactionInfo(
                        TransactionInfo.newBuilder()
                                .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                                .setTotalPrice("99.00")
                                .setCurrencyCode("MXN")
                                .build())
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                .setCardRequirements(
                        CardRequirements.newBuilder()
                                .addAllowedCardNetworks(
                                        Arrays.asList(
                                                WalletConstants.CARD_NETWORK_AMEX,
                                                WalletConstants.CARD_NETWORK_DISCOVER,
                                                WalletConstants.CARD_NETWORK_VISA,
                                                WalletConstants.CARD_NETWORK_MASTERCARD))
                                .build())

        val params = PaymentMethodTokenizationParameters.newBuilder()
                .setPaymentMethodTokenizationType(
                        WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
                .addParameter("gateway", "example")
                .addParameter("gatewayMerchantId", "exampleGatewayMerchantId")
                .build()

        request.setPaymentMethodTokenizationParameters(params)
        return request.build()
    }

    private val mGooglePayViewListener = View.OnClickListener {

        //isReadyToPay()
        val request = createPaymentDataRequest()
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    mPaymentsClient.loadPaymentData(request),
                    this,
                    // LOAD_PAYMENT_DATA_REQUEST_CODE is a constant value
                    // you define.
                    LOAD_PAYMENT_DATA_REQUEST_CODE)
        }
    }

    private val mMercadoPagoViewListener = View.OnClickListener {
        ErrorDialog.newInstance("Metodo de pago no disponible",
                DialogType.OK_DIALOG ,this)!!
                .show(supportFragmentManager, "paywayNotAllow")
    }

    override fun onConfirmationAccept() {

    }

    override fun onConfirmationCancel() {

    }

    override fun onConfirmationNeutral() {

    }

    override fun onSendUserSuccess(success: Boolean) {
        super.onSendUserSuccess(success)
    }

    override fun onSendUserFail(throwable: Throwable) {
        super.onSendUserFail(throwable)
    }

}