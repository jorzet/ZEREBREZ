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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import com.zerebrez.zerebrez.ui.activities.ContentActivity
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.utils.FontUtil

/**
 * Created by Jorge Zepeda Tinoco on 04/01/19.
 * jorzet.94@gmail.com
 */

class PendingPaymentFragment : BaseContentFragment(), ErrorDialog.OnErrorDialogListener {

    private lateinit var mPendingPaymentTextView : TextView
    private lateinit var mInstructionsSendTo: TextView
    private lateinit var mEmailToSend: TextView
    private lateinit var mChangePaymentMethod: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.pending_payment_fragment, container, false)

        mPendingPaymentTextView = rootView.findViewById(R.id.tv_pending_payment)
        mInstructionsSendTo = rootView.findViewById(R.id.tv_instruction_send_to)
        mEmailToSend = rootView.findViewById(R.id.tv_email_to_send)
        mChangePaymentMethod = rootView.findViewById(R.id.btn_change_payment_method)

        val userFirebase = FirebaseAuth.getInstance().currentUser
        if (userFirebase != null) {
            mEmailToSend.text = userFirebase.getEmail()
        }

        mPendingPaymentTextView.typeface = FontUtil.getNunitoBold(context!!)
        mInstructionsSendTo.typeface = FontUtil.getNunitoRegular(context!!)
        mEmailToSend.typeface = FontUtil.getNunitoRegular(context!!)
        mChangePaymentMethod.typeface = FontUtil.getNunitoRegular(context!!)

        mChangePaymentMethod.setOnClickListener(mChangePaymentMethodListener)

        return rootView
    }

    private val mChangePaymentMethodListener = View.OnClickListener {
        val user = getUser()
        if (user != null) {
            requestRemoveCompropagoNode(user)
            SharedPreferencesManager(context!!).storePendingPayment(false)
            SharedPreferencesManager(context!!).storePaymentId("")
        }
    }

    override fun onRemoveCompropagoNodeSuccess(success: Boolean) {
        super.onRemoveCompropagoNodeSuccess(success)
        if (activity != null) {
            (activity as ContentActivity).changePendingPaymentMethodFragmentToPaywayFragment()
        }
    }

    override fun onRemoveCompropagoNodeFail(throwable: Throwable) {
        super.onRemoveCompropagoNodeFail(throwable)
        ErrorDialog.newInstance("Error",
                "No se pudo cambiar el método de pago, intentalo más tarde",
                DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "OrderGenerated")
    }

    override fun onConfirmationCancel() {

    }

    override fun onConfirmationNeutral() {

    }

    override fun onConfirmationAccept() {

    }

}