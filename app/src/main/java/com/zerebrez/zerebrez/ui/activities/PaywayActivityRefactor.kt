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
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.android.billingclient.api.BillingClient
import com.google.firebase.auth.FirebaseAuth

import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.payment.ProvidersFragment
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.services.billing.BillingManager
import com.zerebrez.zerebrez.utils.NetworkUtil


/*
* Created by Jorge Zepeda Tinoco on 27/02/18.
* jorzet.94@gmail.com
*/

private const val TAG : String = "PaywayActivityRefacor"

class PaywayActivityRefactor : BaseActivityLifeCycle(), ErrorDialog.OnErrorDialogListener, BillingManager.OnBillingResponseListener {

    /*
     * UI accessors
     */
    private lateinit var mGooglePayView : View
    private lateinit var mCashView : View
    private lateinit var mCloseContainer: RelativeLayout
    private lateinit var mPaywayContainer: RelativeLayout
    private lateinit var mProgressContainer: RelativeLayout

    /*
     * Payment
     */
    private lateinit var mBillingManager: BillingManager
    private var purchaseOk: Boolean = false
    private var updatingUser : Boolean = false

    // ProductID
    private val productID = "comipems"

    /*
     * Fragments
     */
    private var mProvidersFragment: ProvidersFragment? = null

    // Tag for a dialog
    private val DIALOG_TAG = "dialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        mGooglePayView = findViewById(R.id.rl_google_pay_container)
        mCashView = findViewById(R.id.rl_mercado_pago_container)
        mCloseContainer = findViewById(R.id.rl_close_payway_activity)
        mProgressContainer = findViewById(R.id.rl_progress_container)
        mPaywayContainer = findViewById(R.id.rl_activity_payway_container)

        mGooglePayView.setOnClickListener(mGooglePayViewListener)
        mCashView.setOnClickListener(mCashViewListener)
        mCloseContainer.setOnClickListener{onBackPressed()}

        mBillingManager = BillingManager(this)

    }


    override fun onBackPressed() {
        if(!updatingUser){
            super.onBackPressed()
            setResult(Activity.RESULT_CANCELED, getIntent())
            finish()
        }else
            Toast.makeText(this,"Espere a que termine la actualización.",Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        // Unbind Service
        super.onDestroy()
        mBillingManager.destroy()
    }

    private val mGooglePayViewListener = View.OnClickListener {
        mBillingManager.startPurchaseFlow(productID, BillingClient.SkuType.INAPP)
    }

    private val mCashViewListener = View.OnClickListener {
        if (NetworkUtil.isConnected(this)) {
            if (mProvidersFragment==null) {
                mProvidersFragment = ProvidersFragment()
            }

            if (!isProvidersFragmentShown()) {
                mProvidersFragment!!.show(supportFragmentManager, DIALOG_TAG)
            }
        }
        else
            ErrorDialog.newInstance("Error", "Necesitas tener conexión a intenet para poder continuar",
                    DialogType.OK_DIALOG, this)!!.show(supportFragmentManager, "networkError")
    }

    fun isProvidersFragmentShown(): Boolean {
        return mProvidersFragment != null && mProvidersFragment!!.isVisible()
    }

    override fun onBillingResponseOk() {
        UpdateUser()
    }

    override fun onBillingResponseUserCanceled() {
        DisplayMessage("Pago cancelado", "Puedes consultar nuestros métodos de pago en efectivo.")
    }

    override fun onBillingResponseServiceUnavailable() {
        DisplayMessage("Error", "No hay conexión a internet. Conéctate a una red y vuelve a intentarlo")
    }

    override fun onBillingResponseBillingUnavailable() {
        DisplayMessage("Lo sentimos", "Los servicios de facturación de Google no son compatibles con tu versión de Google Play services")
    }

    override fun onBillingResponseItemUnavailable() {
        DisplayMessage("Suscripción no disponible", "Algo salió mal con la suscripción que intentas adquirir, vuelve a intentarlo más tarde")
    }

    override fun onBillingResponseDeveloperError() {
        DisplayMessage("Error interno", "Algo salió mal con la compra de tu suscripción. Por favor, ponte en contacto con nosotros: soporte@zerebrez.com")
    }

    override fun onBillingResponseError() {
        DisplayMessage("Error", "Algo salió muy mal, vuelve a intentarlo más tarde.")
    }

    override fun onBillingResponseItemAlreadyOwned() {
        DisplayMessage("Ya eres premium", "No hay necesidad de volver a comprar la suscripción. Tú ya eres un usuario premium")
    }

    override fun onBillingResponseItemNotOwned() {
        DisplayMessage("¡Oh, oh...!", "Algo salió mal en el último momento y tu suscripción no fue adquirida, vuelve a intentarlo más tarde")
    }

    fun DisplayMessage(title: String, message: String){
        try {

            ErrorDialog.newInstance(title, message,
                    DialogType.OK_DIALOG, this)!!.show(supportFragmentManager, DIALOG_TAG)
        } catch (e : java.lang.Exception) {
        } catch (e : kotlin.Exception) {
        }
    }

    override fun onConfirmationNeutral() {
        if(purchaseOk){
            setResult(Activity.RESULT_OK, getIntent())
            finish()
        }
    }

    override fun onConfirmationAccept() {
    }

    override fun onConfirmationCancel() {

    }

    fun UpdateUser(){
        val user = getUser()
        if(user != null){
            val userFirebase = FirebaseAuth.getInstance().currentUser
            if (userFirebase != null) {
                user.setEmail(userFirebase.email!!)
            }
            user.setPremiumUser(true)
            user.setTimeStamp(System.currentTimeMillis())
            user.setPayGayMethod("GooglePay")
            saveUser(user)
            updatingUser = true
            setWaitScreen(true)
            requestSendUser(user)
        }
    }

    private fun setWaitScreen(set: Boolean) {
        mPaywayContainer.setVisibility(if (set) View.GONE else View.VISIBLE)
        mProgressContainer.setVisibility(if (set) View.VISIBLE else View.GONE)
    }

    override fun onSendUserSuccess(success: Boolean) {
        purchaseOk=true
        updatingUser = false
        setWaitScreen(false)
        DisplayMessage("¡¡ Felicidades !!","Ya eres premium, disfruta de todo el contenido exclusivo para ti.")
    }
}