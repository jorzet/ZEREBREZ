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

package com.zerebrez.zerebrez.fragments.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.models.Module
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import com.zerebrez.zerebrez.ui.activities.PaywayActivityRefactor
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.utils.FontUtil

/**
 * Created by Jorge Zepeda Tinoco on 20/03/18.
 * jorzet.94@gmail.com
 */

private const val TAG : String = "PaymentFragment"

/*
* request code
*/
private val PAYWAY_FLOW: Int = 0x234

class PaymentFragment : BaseContentFragment(), ErrorDialog.OnErrorDialogListener {

    /*
     * UI accessors
     */
    private lateinit var mIWantToBePremiumButton : View
    private lateinit var mGetFreeQuestionsExamsButton : View
    private lateinit var mIWantToBePremiumButtonTextView: TextView
    private lateinit var mGetFreeQuestionsExamsButtonTextView: TextView
    private lateinit var mExamsModulesNomberTextView: TextView
    private lateinit var mPromoCourseWithPriceTextView: TextView
    private lateinit var mBecomePremiumTextView: TextView

    /*
     * Attributes
     */
    private var mTotalExamsAndModules = ""
    private var mPromoCourseWithPrice = "Y obtén gratis todos los módulos y examenes que se agreguen ¡por solo \$"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.payment_fragment, container, false)!!

        mIWantToBePremiumButton = rootView.findViewById(R.id.btn_be_premium)
        mGetFreeQuestionsExamsButton = rootView.findViewById(R.id.btn_get_free_exams_questions)
        mIWantToBePremiumButtonTextView = rootView.findViewById(R.id.btn_be_premium_text)
        mGetFreeQuestionsExamsButtonTextView = rootView.findViewById(R.id.btn_get_free_exams_questions_text)
        mExamsModulesNomberTextView = rootView.findViewById(R.id.tv_exams_number)
        mPromoCourseWithPriceTextView = rootView.findViewById(R.id.tv_get_free_exams_questions)
        mBecomePremiumTextView = rootView.findViewById(R.id.tv_become_premium)

        mIWantToBePremiumButtonTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mGetFreeQuestionsExamsButtonTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mExamsModulesNomberTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mPromoCourseWithPriceTextView.typeface = FontUtil.getNunitoRegular(context!!)
        mBecomePremiumTextView.typeface = FontUtil.getNunitoSemiBold(context!!)

        mIWantToBePremiumButton.setOnClickListener(mIWantToBePremiumListener)
        mGetFreeQuestionsExamsButton.setOnClickListener(mGetFreeQuestionsExamsListener)

        val user = getUser()
        if (user != null && !user.getCourse().equals("")) {
            requestGetExamsRefactor(user.getCourse())
            requestGetCoursePrice(user.getCourse())
        }

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
        val intent = Intent(activity, PaywayActivityRefactor::class.java)
        startActivityForResult(intent, PAYWAY_FLOW)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PAYWAY_FLOW){
            if(resultCode == Activity.RESULT_OK && activity != null){
                val intent = activity!!.intent
                startActivity(intent)
                activity!!.finish()
            }
        }
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

    override fun onGetExamsRefactorSuccess(exams: List<Exam>) {
        super.onGetExamsRefactorSuccess(exams)
        if (context != null) {
            if (exams != null && exams.isNotEmpty()) {
                mTotalExamsAndModules = exams.size.toString() + " examenes y "
            } else {
                mTotalExamsAndModules = "0 examenes y "
            }
            val user = getUser()
            if (user != null && !user.getCourse().equals("")) {
                requestGetModulesRefactor(user.getCourse())
            }
        }
    }

    override fun onGetExamsRefactorFail(throwable: Throwable) {
        super.onGetExamsRefactorFail(throwable)
        if (context != null) {
            mExamsModulesNomberTextView.text = "8 examenes y 18"
            mExamsModulesNomberTextView.text = mTotalExamsAndModules
        }
    }


    override fun onGetModulesRefactorSuccess(modules: List<Module>) {
        super.onGetModulesRefactorSuccess(modules)
        if (context != null) {
            if (modules != null && modules.isNotEmpty()) {
                mTotalExamsAndModules = mTotalExamsAndModules + modules.size.toString()
                mExamsModulesNomberTextView.text = mTotalExamsAndModules
            } else {
                mTotalExamsAndModules = mTotalExamsAndModules + "0"
                mExamsModulesNomberTextView.text = mTotalExamsAndModules
            }
        }
    }

    override fun onGetModulesRefactorFail(throwable: Throwable) {
        super.onGetModulesRefactorFail(throwable)
        if (context != null) {
            mTotalExamsAndModules = mTotalExamsAndModules + "0"
            mExamsModulesNomberTextView.text = mTotalExamsAndModules
        }
    }

    override fun onGetCoursePriceSuccess(coursePrice: String) {
        super.onGetCoursePriceSuccess(coursePrice)
        if (context != null) {
            if (coursePrice != null && !coursePrice.equals("")) {
                SharedPreferencesManager(context!!).saveCoursePrice(coursePrice)
                mPromoCourseWithPriceTextView.text = mPromoCourseWithPrice + coursePrice + "!"
            }
        }
    }

    override fun onGetCoursePriceFail(throwable: Throwable) {
        super.onGetCoursePriceFail(throwable)
        if (context != null) {
            SharedPreferencesManager(context!!).saveCoursePrice("99")
            mPromoCourseWithPriceTextView.text = mPromoCourseWithPrice + "99!"
        }
    }

}