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

package com.zerebrez.zerebrez.fragments.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentDialogFragment
import com.zerebrez.zerebrez.models.OrderResponse

import com.zerebrez.zerebrez.models.Provider
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.enums.ComproPagoStatus
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.services.compropago.ComproPagoManager
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import com.zerebrez.zerebrez.ui.activities.BaseActivityLifeCycle
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.utils.NetworkUtil
import retrofit2.Response

/**
 * Created by Jesus Campos on 05/09/18.
 * jcampos.jc38@gmail.com
 */

class ConfirmOrderFragment: BaseContentDialogFragment(),  ErrorDialog.OnErrorDialogListener{

    private val TAG = "ProvidersFragment"
    private var PRICE = 99.0f
    private var ORDER_GENERATED = false

    private lateinit var mNameEditText: EditText
    private lateinit var mLastNameEditText: EditText
    private lateinit var mEmailEditText: EditText
    private lateinit var mPriceTextView: TextView
    private lateinit var mProvierImageView: ImageView
    private lateinit var mComissionTextView: TextView
    private lateinit var mConfirmOrderButton: Button
    private lateinit var mScrollView: ScrollView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mCloseContainer: RelativeLayout
    private lateinit var mCourseDescriptionTextView: TextView

    private var mProvider: Provider? = null
    private lateinit var mComproPagoManager: ComproPagoManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, R.style.AppTheme)
        //Recover provider information
        mProvider = arguments!!.getSerializable("Provider") as Provider?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.confirm_order_fragment, container, false)!!

        mNameEditText = rootView.findViewById(R.id.et_order_name)
        mLastNameEditText = rootView.findViewById(R.id.et_order_last_name)
        mEmailEditText = rootView.findViewById(R.id.et_order_mail)
        mPriceTextView = rootView.findViewById(R.id.tv_suscription_price)
        mProvierImageView = rootView.findViewById(R.id.iv_order_provider_icon)
        mComissionTextView = rootView.findViewById(R.id.tv_order_provider_comision)
        mConfirmOrderButton = rootView.findViewById(R.id.btn_order_confirm)
        mScrollView = rootView.findViewById(R.id.sv_confirm_order)
        mProgressBar = rootView.findViewById(R.id.pb_confirm_order)
        mCloseContainer = rootView.findViewById(R.id.rl_close_confirm_order)
        mCourseDescriptionTextView = rootView.findViewById(R.id.tv_suscription_details)

        PRICE = SharedPreferencesManager(context!!).getCoursePrice().toFloat()
        val user = getUser()

        if (user != null && !user.getCourse().equals("")) {
            val course = DataHelper(context!!).getCourseFromUserCourse(user.getCourse())
            if (course != null) {
                mCourseDescriptionTextView.text = course.comproPagoDescription
            }
        }

        mPriceTextView.text = "$${PRICE}"

        mConfirmOrderButton.setOnClickListener {
            //OnFakeGenerateOrderSuccess()
            GenerateOrder()
        }
        mCloseContainer.setOnClickListener {
            if (activity != null) {
                activity!!.onBackPressed()
            }
        }

        mComproPagoManager = ComproPagoManager()

        //Show provider information
        SetEmailIfUser()
        ShowProviderInformation()

        return rootView
    }

    private fun GenerateOrder() {
        val name = mNameEditText.text.toString()
        val lastName = mLastNameEditText.text.toString()
        val email = mEmailEditText.text.toString()
        if(!name.equals("") && !lastName.equals("") && !email.equals("") && email.contains("@") && activity != null){
            if (NetworkUtil.isConnected(this.activity!!)) {
                setWaitScreen(true)

                if (context != null) {
                    val user = getUser()

                    if (user != null && !user.getCourse().equals("")) {
                        val course = DataHelper(context!!).getCourseFromUserCourse(user.getCourse())
                        if (course != null) {
                            mComproPagoManager.GenerateOrder(course, "$name $lastName", email, mProvider!!.internal_name, PRICE, object : ComproPagoManager.OnGenerateOrderListener {
                                override fun onGenerateOrderResponse(response: Response<OrderResponse>?) {
                                    OnGenerateOrderSuccess(response)
                                }

                                override fun onGenerateOrderFailure(throwable: Throwable?) {
                                    OnGenerateOrderError(throwable)
                                }

                            })
                        }
                    }

                }

            } else
                SendRequestErrorMessage()
        }
        else
            Toast.makeText(activity, "Es necesario llenar todos los campos", Toast.LENGTH_SHORT).show()
    }

    fun OnFakeGenerateOrderSuccess() {
        ORDER_GENERATED=true
        setPendingPayment(true)
        setPaymentId("sdagIDSNFLDSJZBSF")

        val user = getUser()
        if (user != null && !user.getCourse().equals("")) {
            requestSendUserComproPago(user, "DSBNVIAEBSC34251KDBL", ComproPagoStatus.CHARGE_PENDING)
        }

        ErrorDialog.newInstance("Orden de pago generada", "Las instrucciones de pago llegarán al corrreo proporcionado, una vez realizado el pago obtendrás tu suscripción.",
                DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "OrderGenerated")
    }

    fun OnGenerateOrderSuccess(response: Response<OrderResponse>?){
        if(response!=null){
            if(response.code()<300 && response.code()>199){
                val orderResponse = response.body()
                if (orderResponse != null) {
                    if(orderResponse.short_id != null && !orderResponse.short_id.equals("")) {
                        ORDER_GENERATED=true
                        setPendingPayment(true)
                        setPaymentId(orderResponse.id)

                        val user = getUser()
                        if (user != null && !user.getCourse().equals("")) {
                            requestSendUserComproPago(user, orderResponse.id, ComproPagoStatus.CHARGE_PENDING)
                        }

                        ErrorDialog.newInstance("Orden de pago generada", "Las instrucciones de pago llegarán al corrreo proporcionado, una vez realizado el pago obtendrás tu suscripción.",
                                DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "OrderGenerated")
                    }else
                        SendOrderErrorMessage()
                }else
                    SendOrderErrorMessage()
            }else
                SendOrderErrorMessage()
        }else
            SendOrderErrorMessage()
    }

    fun OnGenerateOrderError(throwable: Throwable?){
        SendOrderErrorMessage()
    }

    private fun setWaitScreen(set: Boolean) {
        mScrollView.visibility = if (set) View.GONE else View.VISIBLE
        mProgressBar.visibility = if (set) View.VISIBLE else View.GONE
    }

    private fun ImageView.loadUrl(url: String) {
        Picasso.with(context).load(url).into(this)
    }

    private fun ShowProviderInformation(){
        if(mProvider!=null){
            mProvierImageView.loadUrl(mProvider!!.image_small)
            mComissionTextView.text = getString(R.string.comission_text, mProvider!!.commission.toFloat())
        }
    }


    private fun SetEmailIfUser(){
        val userFirebase = FirebaseAuth.getInstance().currentUser

        if (userFirebase != null) {
            mEmailEditText.setText(userFirebase.getEmail())
        }
        else
            requestGetProfileRefactor()
    }

    override fun onGetProfileRefactorSuccess(user: User) {
        super.onGetProfileRefactorSuccess(user)
        mEmailEditText.setText(user.getEmail())
    }


    fun SendRequestErrorMessage(){
        Log.i(TAG, "onErrorOrderRequest() Failed to enqueue")
        ErrorDialog.newInstance("Algo salió mal...", "La orden de pago no pudo ser generada. Asegurate de tener una conexión a internet.",
                DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
    }

    fun SendOrderErrorMessage(){
        ErrorDialog.newInstance("Algo salió mal...", "La orden de pago no pudo ser generada. Asegurate de haber proporcionado un correo válido",
                DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "OrderError")
    }

    override fun onConfirmationCancel() {

    }

    override fun onConfirmationNeutral() {
        if (ORDER_GENERATED) {
            if (activity != null) {
                val data = Intent()
                data.putExtra(BaseActivityLifeCycle.REFRESH_FRAGMENT, true)
                activity!!.setResult(AppCompatActivity.RESULT_OK, data)
                activity!!.finish()
            }
        } else
            setWaitScreen(false)
    }

    override fun onConfirmationAccept() {
        if (ORDER_GENERATED) {

        }
    }
}