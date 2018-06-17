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

package com.zerebrez.zerebrez.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.ui.activities.PaywayActivity
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.utils.FontUtil

/**
 * Created by Jorge Zepeda Tinoco on 20/03/18.
 * jorzet.94@gmail.com
 */

private const val TAG : String = "PaymentFragment"

class PaymentFragment : BaseContentFragment(), ErrorDialog.OnErrorDialogListener {

    private lateinit var mIWantToBePremiumButton : View
    private lateinit var mGetFreeQuestionsExamsButton : View
    private lateinit var mIWantToBePremiumButtonTextView: TextView
    private lateinit var mGetFreeQuestionsExamsButtonTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.payment_fragment, container, false)!!

        mIWantToBePremiumButton = rootView.findViewById(R.id.btn_be_premium)
        mGetFreeQuestionsExamsButton = rootView.findViewById(R.id.btn_get_free_exams_questions)
        mIWantToBePremiumButtonTextView = rootView.findViewById(R.id.btn_be_premium_text)
        mGetFreeQuestionsExamsButtonTextView = rootView.findViewById(R.id.btn_get_free_exams_questions_text)

        mIWantToBePremiumButtonTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mGetFreeQuestionsExamsButtonTextView.typeface = FontUtil.getNunitoSemiBold(context!!)

        mIWantToBePremiumButton.setOnClickListener(mIWantToBePremiumListener)
        mGetFreeQuestionsExamsButton.setOnClickListener(mGetFreeQuestionsExamsListener)


        return rootView
    }

    private val mIWantToBePremiumListener = View.OnClickListener {
        goPaywayActivity()
    }

    private val mGetFreeQuestionsExamsListener = View.OnClickListener {
        ErrorDialog.newInstance("Muy pronto",
                DialogType.OK_DIALOG ,this)!!
                .show(fragmentManager!!, "paywayNotAllow")
    }

    private fun goPaywayActivity() {
        val intent = Intent(activity, PaywayActivity::class.java)
        startActivity(intent)
    }

    /*
     * Dialog listeners
     */
    override fun onConfirmationCancel() {

    }

    override fun onConfirmationNeutral() {

    }

    override fun onConfirmationAccept() {

    }
}