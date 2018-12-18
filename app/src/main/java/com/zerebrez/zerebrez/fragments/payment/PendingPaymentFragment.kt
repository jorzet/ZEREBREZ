package com.zerebrez.zerebrez.fragments.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.utils.FontUtil

class PendingPaymentFragment : BaseContentFragment() {

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

        val user = getUser()
        if (user != null) {
            mEmailToSend.text = user.getEmail()
        }

        mPendingPaymentTextView.typeface = FontUtil.getNunitoBold(context!!)
        mInstructionsSendTo.typeface = FontUtil.getNunitoRegular(context!!)
        mEmailToSend.typeface = FontUtil.getNunitoRegular(context!!)
        mChangePaymentMethod.typeface = FontUtil.getNunitoRegular(context!!)

        mChangePaymentMethod.setOnClickListener(mChangePaymentMethodListener)

        return rootView
    }

    private val mChangePaymentMethodListener = View.OnClickListener {

    }

}