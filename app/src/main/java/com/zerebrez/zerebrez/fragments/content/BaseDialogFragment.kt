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

package com.zerebrez.zerebrez.fragments.content

import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.services.sharedpreferences.JsonParcer
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager

/**
 * Created by Jorge Zepeda Tinoco on 27/02/18.
 * jorzet.94@gmail.com
 */

open class BaseDialogFragment : DialogFragment() {

    open fun saveUser(user : User) {
        val json = JsonParcer.parceObjectToJson(user)
        SharedPreferencesManager(context!!).storeJsonUser(json)
    }

    open fun getUser() : User? {
        if (context != null) {
            val json = SharedPreferencesManager(context!!).getJsonUser()
            if (json != null) {
                return JsonParcer.getObjectFromJson(json, User::class.java) as User
            } else {
                return null
            }
        } else {
            return null
        }
    }

    open fun setQuestionModuleFragmentOK() {
        SharedPreferencesManager(context!!).setQuestionModuleFragmentOK()
    }

    open fun setStudySubjectFragmentOK() {
        SharedPreferencesManager(context!!).setStudySubjectFragmentOK()
    }

    open fun setStudyWrongQuestionFragmentOK() {
        SharedPreferencesManager(context!!).setStudyWrongQuestionFragmentOK()
    }

    open fun setExamFragmentOK() {
        SharedPreferencesManager(context!!).setExamFragmentOK()
    }

    open fun setAdvancesFragmentOK() {
        SharedPreferencesManager(context!!).setAdvancesFragmentOK()
    }

    open fun setSchoolAverageFragmentOK() {
        SharedPreferencesManager(context!!).setSchoolAverageFragmentOK()
    }

    open fun setExamsAverageFragmentOK() {
        SharedPreferencesManager(context!!).setExamsAverageFragmentOK()
    }

    open fun setPendingPayment(hasPendingPayment : Boolean) {
        SharedPreferencesManager(context!!).storePendingPayment(hasPendingPayment)
    }

    open fun setPaymentId(paymentId : String) {
        SharedPreferencesManager(context!!).storePaymentId(paymentId)
    }
}