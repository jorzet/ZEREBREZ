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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.NonScrollListView
import com.zerebrez.zerebrez.adapters.SchoolListAdapter
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.School
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import com.zerebrez.zerebrez.ui.activities.ChooseSchoolsActivity
import com.zerebrez.zerebrez.ui.activities.LoginActivity
import com.zerebrez.zerebrez.utils.MyNetworkUtil
import com.zerebrez.zerebrez.utils.NetworkUtil
import android.os.Build
import android.app.TimePickerDialog
import com.zerebrez.zerebrez.services.database.DataHelper
import java.util.*
import com.zerebrez.zerebrez.services.notification.NotificationScheduler
import android.util.Log
import com.zerebrez.zerebrez.services.notification.NotificationAlarmReciver


/**
 * Created by Jorge Zepeda Tinoco on 20/03/18.
 * jorzet.94@gmail.com
 */

private const val TAG : String = "ProfileFragment"

class ProfileFragment : BaseContentFragment() {

    /*
     * tags
     */
    private val SHOW_START = "show_start"
    private val SHOW_CONTINUE_BUTTON : String = "show_continue_button"

    /*
     * UI accessors
     */
    private lateinit var mCourse : TextView
    private lateinit var mEmail : EditText
    private lateinit var mPassword : EditText
    private lateinit var mSelectedSchoolsList : NonScrollListView
    private lateinit var mLogOut : TextView
    private lateinit var mSendEmail : TextView
    private lateinit var mNotification : TextView
    private lateinit var mTimeNotification : TextView
    private lateinit var mTermsAndPrivacy : View
    private lateinit var mEditSchoolsButton : Button
    private lateinit var mLinkEmailButton : Button
    private lateinit var mNotSelectedSchools : TextView
    private lateinit var mAllowMobileDataSwitch : Switch
    private lateinit var mAllowNotificationsSwitch : Switch

    /*
     * Adapters
     */
    private lateinit var mSchoolsListAdapter : SchoolListAdapter

    /*
     * Objects
     */
    private var mSchools = arrayListOf<School>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.profile_fragment, container, false)!!

        mCourse = rootView.findViewById(R.id.tv_course)
        mEmail = rootView.findViewById(R.id.et_email)
        mPassword = rootView.findViewById(R.id.et_password)
        mSelectedSchoolsList = rootView.findViewById(R.id.nslv_schools_selected)
        mLogOut = rootView.findViewById(R.id.tv_log_out)
        mSendEmail = rootView.findViewById(R.id.tv_support_email)
        mTermsAndPrivacy = rootView.findViewById(R.id.rl_terms_and_privacy_container)
        mEditSchoolsButton = rootView.findViewById(R.id.btn_change_schools)
        mLinkEmailButton = rootView.findViewById(R.id.btn_link_email)
        mNotSelectedSchools = rootView.findViewById(R.id.tv_not_selected_schools)
        mAllowMobileDataSwitch = rootView.findViewById(R.id.sw_allow_mobile_data)

        mNotification = rootView.findViewById(R.id.tv_notification)
        mTimeNotification = rootView.findViewById(R.id.tv_time)
        mAllowNotificationsSwitch = rootView.findViewById(R.id.sw_allow_notification)

        mEditSchoolsButton.setOnClickListener(mEditSchoolsListener)
        mLinkEmailButton.setOnClickListener(mLinkEmailButtonListener)

        mAllowMobileDataSwitch.setOnCheckedChangeListener(mAllowMobileNetworkSwitchListener)
        mAllowNotificationsSwitch.setOnCheckedChangeListener(mAllowNotificationsSwitchListener)

        // set notification
        mAllowNotificationsSwitch.setChecked(DataHelper(context!!).getReminderStatus());

        mLogOut.setOnClickListener(mLogOutListener)
        mTermsAndPrivacy.setOnClickListener(mTermsAndPrivacyListener)
        mSendEmail.setOnClickListener(mSendEmailListener)
        mNotification.setOnClickListener(mNotificationListener)

        val dataHelper = DataHelper(context!!)
        mTimeNotification.text = dataHelper.getNotificationTime()

        checkEmailAndPassword()
        checkMobileDataSate()

        val user = getUser()
        if (user != null) {
            val mSchools = user.getSelectedSchools()

            if (mSchools.isNotEmpty()) {
                mSchoolsListAdapter = SchoolListAdapter(mSchools, context!!)
                mSelectedSchoolsList.adapter = mSchoolsListAdapter
                mEditSchoolsButton.visibility = View.VISIBLE
            } else {
                mSelectedSchoolsList.visibility = View.GONE
                mNotSelectedSchools.visibility = View.VISIBLE
            }
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()

        val user = getUser()
        if (user != null) {
            val mSchools = user.getSelectedSchools()

            if (mSchools.isNotEmpty()) {
                mSchoolsListAdapter = SchoolListAdapter(mSchools, context!!)
                mSelectedSchoolsList.adapter = mSchoolsListAdapter
            } else {
                mSelectedSchoolsList.visibility = View.GONE
                mNotSelectedSchools.visibility = View.VISIBLE
            }
        }
    }

    private val mAllowMobileNetworkSwitchListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(button: CompoundButton?, checked: Boolean) {
            setMobileDataChange(checked)
        }
    }

    private val mAllowNotificationsSwitchListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, isChecked: Boolean) {
            DataHelper(context!!).setReminderStatus(isChecked)
            if (isChecked) {
                Log.d(TAG, "onCheckedChanged: true")
                val dataHelper = DataHelper(context!!)
                val time = dataHelper.getNotificationTime()
                val times = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val hour = Integer.parseInt(times[0])
                val minute = Integer.parseInt(times[1])
                NotificationScheduler.setReminder(activity, NotificationAlarmReciver::class.java, hour, minute)
                //ll_set_time.setAlpha(1f)
            } else {
                Log.d(TAG, "onCheckedChanged: false")
                NotificationScheduler.cancelReminder(activity, NotificationAlarmReciver::class.java)
                //ll_set_time.setAlpha(0.4f)
            }
        }
    }

    private val mLogOutListener = View.OnClickListener {
        FirebaseAuth.getInstance().signOut()
        SharedPreferencesManager(context!!).removeSessionData()
        SharedPreferencesManager(context!!).setPersistanceDataEnable(true)
        LoginManager.getInstance().logOut()
        goLogInActivity()
    }

    private val mTermsAndPrivacyListener = View.OnClickListener {
        goTermsAndPrivacyActivity()
    }

    private val mSendEmailListener = View.OnClickListener {
        goSendEmailActivity()
    }

    private val mNotificationListener = View.OnClickListener {
        // TODO Auto-generated method stub
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)
        val mTimePicker: TimePickerDialog
        mTimePicker = TimePickerDialog(activity,
                TimePickerDialog.OnTimeSetListener { timePicker, selectedHour, selectedMinute ->
                    val notificationTime = selectedHour.toString() + ":" + selectedMinute
                    mTimeNotification.setText(notificationTime)
                    DataHelper(context!!).saveNotificationTime(notificationTime)

                    NotificationScheduler.cancelReminder(activity, NotificationAlarmReciver::class.java)

                    val dataHelper = DataHelper(context!!)
                    val time = dataHelper.getNotificationTime()
                    val times = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val hour = Integer.parseInt(times[0])
                    val minute = Integer.parseInt(times[1])
                    NotificationScheduler.setReminder(activity, NotificationAlarmReciver::class.java, hour, minute)


                }, hour, minute, false)//Yes 24 hour time
        mTimePicker.setTitle("Select Time")
        mTimePicker.show()
    }

    private val mLinkEmailButtonListener = View.OnClickListener {
        val user = getUser()
        if (user != null) {
            //check if has account changes
            var hasChangesAccount = false
            if (!mEmail.text.toString().equals(user.getEmail())) {
                user.setEmail(mEmail.text.toString())
                hasChangesAccount = true
            }
            if (!mPassword.text.toString().equals(user.getPassword())) {
                user.setPassword(mPassword.text.toString())
                hasChangesAccount = true
            }

            // send request just if has email or password changes
            if (hasChangesAccount) {
                requestUpdateUser(user)
            }

            // check if has profile changes
            /*var hasProfileChanges = false
            if (mCourse.text.toString().equals(user.getCourse())) {
                user.setCourse(mCourse.text.toString())
                hasProfileChanges = true
            }

            // send request just if has profile changes
            if (hasProfileChanges) {
                requestSendUser(user)
            }*/

            // check has account changes or profile changes
            if (hasChangesAccount) {
                saveUser(user)
            }
        }
    }

    private val mEditSchoolsListener = View.OnClickListener {
        goChooseSchoolsActivity()
    }

    private fun checkEmailAndPassword() {
        val userFirebase = FirebaseAuth.getInstance().currentUser
        val userCache = getUser()
        if (userFirebase != null && userCache != null) {
            val course = userCache.getCourse()
            if (!course.equals("")) {
                mCourse.text = course.toUpperCase()
            }

            val email = userCache.getEmail()
            if (!email.equals("")) {
                mEmail.setText(email)
            }

            val pass = userCache.getPassword()
            if (!pass.equals("")) {
                var password = ""
                for (i in 0..pass.length) {
                    password = password + "*"
                }
                mPassword.setText(password)
            }
        }
    }

    private fun checkMobileDataSate() {
        if (NetworkUtil.isMobileNetworkConnected(context!!)) {
            mAllowMobileDataSwitch.isChecked = true
        } else {
            mAllowMobileDataSwitch.isChecked = false
        }
    }

    private fun setMobileDataChange(checked: Boolean) {
        if (checked) {
            MyNetworkUtil.getInstance().setMobileDataEnabled(context!!, true)
            MyNetworkUtil.getInstance().setWifiEnable(context!!, false)
            mAllowMobileDataSwitch.isChecked = true

        } else {
            MyNetworkUtil.getInstance().setMobileDataEnabled(context!!, false)
            MyNetworkUtil.getInstance().setWifiEnable(context!!, true)
            mAllowMobileDataSwitch.isChecked = false
        }
    }

    private fun goLogInActivity() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.putExtra(SHOW_START, true)
        activity!!.startActivity(intent)
        activity!!.finish()
    }

    private fun goSendEmailActivity() {
        //val intent = Intent(activity, SendEmailActivity::class.java)
        //activity!!.startActivity(intent)
        val emailIntent = Intent(Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", resources.getString(R.string.support_email_text), null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Sistema Operativo: " + getAndroidVersion() +
                "\n\n\n Aquí escribe tu mensaje" + "" +
                "\n\n\n (Para un mejor soporte no borres el sistema operativo ni la cuenta)")
        startActivity(Intent.createChooser(emailIntent, "Enviando email..."))
    }

    fun getAndroidVersion(): String {
        val release = Build.VERSION.RELEASE
        val sdkVersion = Build.VERSION.SDK_INT
        return "Android SDK: $sdkVersion ($release)"
    }

    private fun goTermsAndPrivacyActivity() {
        val url = resources.getString(R.string.url_terms_and_privacy)
        val intent = Intent(Intent.ACTION_VIEW)

        intent.data = Uri.parse(url)
        // this allow the smatphone find chrome browser application
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.android.chrome")

        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            // Chrome browser may be is not installed so allow user to choose instead
            intent.setPackage(null)
            startActivity(intent)
        }

    }

    private fun goChooseSchoolsActivity() {
        val intent = Intent(activity, ChooseSchoolsActivity::class.java)
        intent.putExtra(SHOW_CONTINUE_BUTTON, false)
        startActivity(intent)
    }




}
