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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.utils.FontUtil
import java.util.*

/**
 * Created by Jorge Zepeda Tinoco on 20/03/18.
 * jorzet.94@gmail.com
 */

class YouArePremiumFragment : BaseContentFragment() {

    private lateinit var mYouArePremiumTextView: TextView
    private lateinit var mUserCourseTextView: TextView
    private lateinit var mExpirationDate1TextView: TextView
    private lateinit var mExpirationDate2TextView: TextView
    private lateinit var mYouArePremiumContainer: View
    private lateinit var mNotSuscriptionInfoTextView: TextView
    private lateinit var mLoadingSuscriptionInfo: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.you_are_premium_fragment, container, false)!!

        mYouArePremiumTextView = rootView.findViewById(R.id.tv_you_are_premium)
        mUserCourseTextView = rootView.findViewById(R.id.tv_user_course)
        mExpirationDate1TextView = rootView.findViewById(R.id.tv_expiration_date_1)
        mExpirationDate2TextView = rootView.findViewById(R.id.tv_expiration_date_2)
        mYouArePremiumContainer = rootView.findViewById(R.id.rl_you_are_premium_container)
        mNotSuscriptionInfoTextView = rootView.findViewById(R.id.tv_not_suscription_info)
        mLoadingSuscriptionInfo = rootView.findViewById(R.id.pb_loading)

        mYouArePremiumTextView.typeface = FontUtil.getNunitoBold(context!!)
        mUserCourseTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mExpirationDate1TextView.typeface = FontUtil.getNunitoRegular(context!!)
        mExpirationDate2TextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mNotSuscriptionInfoTextView.typeface = FontUtil.getNunitoSemiBold(context!!)

        mYouArePremiumContainer.visibility = View.GONE
        mLoadingSuscriptionInfo.visibility = View.VISIBLE
        requestGetProfileRefactor()

        return rootView
    }

    @SuppressLint("SetTextI18n")
    override fun onGetProfileRefactorSuccess(user: User) {
        super.onGetProfileRefactorSuccess(user)

        if (context != null) {

            if (user != null) {
                mYouArePremiumContainer.visibility = View.VISIBLE
                mLoadingSuscriptionInfo.visibility = View.GONE

                if (!user.getCourse().equals("")) {
                    mUserCourseTextView.text =
                            resources.getString(R.string.user_course_text) + " " + user.getCourse()
                } else {
                    mUserCourseTextView.text =
                            resources.getString(R.string.user_course_text) + " " +
                            resources.getString(R.string.user_without_course_text)
                }

                if (!user.getTimestamp().equals(0)) {
                    val calendar = Calendar.getInstance()
                    calendar.setTimeInMillis(user.getTimestamp())

                    val mYear = calendar.get(Calendar.YEAR)
                    var mMonth = calendar.get(Calendar.MONTH).toString()
                    var mDay = calendar.get(Calendar.DAY_OF_MONTH).toString()

                    if (mMonth.length == 1) {
                        mMonth = "0$mMonth"
                    }

                    if (mDay.length == 1) {
                        mDay = "0$mDay"
                    }

                    mExpirationDate2TextView.text = mDay + "/" + mMonth + "/" + (mYear + 1).toString()
                }
            } else {
                mYouArePremiumContainer.visibility = View.GONE
                mLoadingSuscriptionInfo.visibility = View.GONE
                mNotSuscriptionInfoTextView.visibility = View.VISIBLE
            }
        }
    }

    override fun onGetProfileRefactorFail(throwable: Throwable) {
        super.onGetProfileRefactorFail(throwable)

        if (context != null) {
            mYouArePremiumContainer.visibility = View.GONE
            mLoadingSuscriptionInfo.visibility = View.GONE
            mNotSuscriptionInfoTextView.visibility = View.VISIBLE
        }
    }
}