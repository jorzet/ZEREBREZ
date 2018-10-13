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
import com.zerebrez.zerebrez.components.NonScrollListView
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
import android.content.Context
import com.zerebrez.zerebrez.services.database.DataHelper
import java.util.*
import com.zerebrez.zerebrez.services.notification.NotificationScheduler
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.zerebrez.zerebrez.models.Error.FirebaseError
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.models.enums.ErrorType
import com.zerebrez.zerebrez.services.notification.NotificationAlarmReciver
import com.zerebrez.zerebrez.ui.activities.BaseActivityLifeCycle
import com.zerebrez.zerebrez.ui.activities.ContentActivity
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.utils.FontUtil

/**
 * Created by Jorge Zepeda Tinoco on 20/03/18.
 * jorzet.94@gmail.com
 */

private const val TAG : String = "ProfileFragment"

class ProfileFragment : BaseContentFragment(), ErrorDialog.OnErrorDialogListener {

    /*
     * tags
     */
    private val SHOW_START = "show_start"
    private val SHOW_CONTINUE_BUTTON : String = "show_continue_button"
    private val SHOW_BACK_BUTTON : String = "show_back_button"

    /*
     * UI accessors
     */
    private lateinit var mProfileTextView: TextView
    private lateinit var mCourseTextView: TextView
    private lateinit var mCourse : TextView
    private lateinit var mEmail : EditText
    private lateinit var mPassword : EditText
    private lateinit var mSelectedSchoolsList : NonScrollListView
    private lateinit var mLogOut : TextView
    private lateinit var mSendEmail : TextView
    private lateinit var mNotification : TextView
    private lateinit var mTimeNotification : TextView
    private lateinit var mTermsAndPrivacy : View
    private lateinit var mEditSchoolsButton : View
    private lateinit var mEditSchoolsTextView : TextView
    private lateinit var mChangePasswordButton : View
    private lateinit var mChangePasswordText : TextView
    private lateinit var mNotSelectedSchools : TextView
    private lateinit var mAllowMobileDataSwitch : Switch
    private lateinit var mAllowNotificationsSwitch : Switch
    private lateinit var mLinkWithFacebookButton : View
    private lateinit var mLinkWithGoogleButton : View
    private lateinit var mIsLoggedInWithFacebookImage : ImageView
    private lateinit var mIsLoggedInWithGoogleImage : ImageView
    private lateinit var mLinktYourAccountsTextView: TextView
    private lateinit var mTimetoNotifyTextView: TextView
    private lateinit var mTimeTextView: TextView
    private lateinit var mMobileDataTextView: TextView
    private lateinit var mTermsAndPrivacyTextView: TextView

    /*
     * Adapters
     */
    private lateinit var mSchoolsListAdapter : SchoolListAdapter

    /*
     * Objects
     */
    private lateinit var mUser : User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.profile_fragment, container, false)!!

        mProfileTextView = rootView.findViewById(R.id.tv_my_profile)
        mCourseTextView = rootView.findViewById(R.id.tv_course_text)
        mCourse = rootView.findViewById(R.id.tv_course)
        mEmail = rootView.findViewById(R.id.et_email)
        mPassword = rootView.findViewById(R.id.et_password)
        mSelectedSchoolsList = rootView.findViewById(R.id.nslv_schools_selected)
        mLogOut = rootView.findViewById(R.id.tv_log_out)
        mSendEmail = rootView.findViewById(R.id.tv_support_email)
        mTermsAndPrivacy = rootView.findViewById(R.id.rl_terms_and_privacy_container)
        mEditSchoolsButton = rootView.findViewById(R.id.btn_change_schools)
        mEditSchoolsTextView = rootView.findViewById(R.id.edit_schools_text)
        mNotSelectedSchools = rootView.findViewById(R.id.tv_not_selected_schools)
        //mAllowMobileDataSwitch = rootView.findViewById(R.id.sw_allow_mobile_data)
        mNotification = rootView.findViewById(R.id.tv_notification)
        mTimeNotification = rootView.findViewById(R.id.tv_time)
        mAllowNotificationsSwitch = rootView.findViewById(R.id.sw_allow_notification)
        mLinkWithFacebookButton = rootView.findViewById(R.id.btn_facebook_login)
        mLinkWithGoogleButton = rootView.findViewById(R.id.btn_google_login)
        mChangePasswordButton = rootView.findViewById(R.id.btn_change_password)
        mChangePasswordText = rootView.findViewById(R.id.tv_change_password)
        mIsLoggedInWithFacebookImage = rootView.findViewById(R.id.iv_is_loggedin_with_facebook)
        mIsLoggedInWithGoogleImage = rootView.findViewById(R.id.iv_is_loggedin_with_google)
        mLinktYourAccountsTextView = rootView.findViewById(R.id.tv_link_accounts_text)
        mTimetoNotifyTextView = rootView.findViewById(R.id.tv_time_to_notify)
        mTimeTextView = rootView.findViewById(R.id.tv_time)
        //mMobileDataTextView = rootView.findViewById(R.id.tv_mobile_data)
        mTermsAndPrivacyTextView = rootView.findViewById(R.id.terms_and_privacy_container_text)

        mProfileTextView.typeface = FontUtil.getNunitoBold(context!!)
        mCourseTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mCourse.typeface = FontUtil.getNunitoSemiBold(context!!)
        mNotSelectedSchools.typeface = FontUtil.getNunitoSemiBold(context!!)
        mEditSchoolsTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mLinktYourAccountsTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mChangePasswordText.typeface = FontUtil.getNunitoSemiBold(context!!)
        mLogOut.typeface = FontUtil.getNunitoSemiBold(context!!)
        mSendEmail.typeface = FontUtil.getNunitoSemiBold(context!!)
        mNotification.typeface = FontUtil.getNunitoSemiBold(context!!)
        mTimetoNotifyTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mTimeTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        //mMobileDataTextView.typeface = FontUtil.getNunitoSemiBold(context!!)
        mTermsAndPrivacyTextView.typeface = FontUtil.getNunitoBold(context!!)


        // set listeners
        mEditSchoolsButton.setOnClickListener(mEditSchoolsListener)
        mChangePasswordButton.setOnClickListener(mLinkEmailButtonListener)
        mLinkWithFacebookButton.setOnClickListener(mLinkWithFacebookButtonListener)
        mLinkWithGoogleButton.setOnClickListener(mLinkWithGoogleButtonListener)
        mLogOut.setOnClickListener(mLogOutListener)
        mTermsAndPrivacy.setOnClickListener(mTermsAndPrivacyListener)
        mSendEmail.setOnClickListener(mSendEmailListener)
        mNotification.setOnClickListener(mNotificationListener)

        //mAllowMobileDataSwitch.setOnCheckedChangeListener(mAllowMobileNetworkSwitchListener)
        mAllowNotificationsSwitch.setOnCheckedChangeListener(mAllowNotificationsSwitchListener)
        //mChangePasswordButton.setOnEditorActionListener(onSendFormListener)

        // set notification
        val dataHelper = DataHelper(context!!)

        mAllowNotificationsSwitch.setChecked(dataHelper.getReminderStatus());

        val time = dataHelper.getNotificationTime()
        if (time.equals("")) {
            mTimeNotification.text = "16:00"
            dataHelper.saveNotificationTime("16:00")
        } else {
            mTimeNotification.text = time
        }



        requestGetProfileRefactor()

        checkProviders()
        //checkMobileDataSate()

        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == LoginActivity.RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {

                // Google Sign In was successful, save Token and a state then authenticate with Firebase
                val account = result.signInAccount

                val idToken = account!!.idToken

                SharedPreferencesManager(context!!).saveGoogleToken(idToken!!)
                onGoogleResultSuccess(account)
                //val credential = GoogleAuthProvider.getCredential(idToken, null)
                //requestSigInUserWithGoogleProvider(credential)
            } else {
                if (activity != null)
                    (activity as ContentActivity).showLoading(false)
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Login Unsuccessful. ")
                val error = GenericError()
                error.setErrorType(ErrorType.NULL_RESPONSE)
                error.setErrorMessage("Respuesta sin datos")
                Toast.makeText(activity, "Login Unsuccessful", Toast.LENGTH_SHORT).show()
                onGoogleResultFaild(error)
            }
        } else if (requestCode.equals(BaseActivityLifeCycle.RC_CHOOSE_SCHOOL)) {
            if (resultCode.equals(BaseActivityLifeCycle.UPDATE_USER_SCHOOLS_RESULT_CODE)) {
                onResume()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        requestGetProfileRefactor()

        checkProviders()
        //checkMobileDataSate()
    }

    private val onSendFormListener = object : TextView.OnEditorActionListener {
        override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            var action = false
            if (actionId.equals(EditorInfo.IME_ACTION_SEND)) {
                // hide keyboard
                try {
                    val inputMethodManager = textView!!.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0)
                    mChangePasswordButton.performClick()
                    action = true
                } catch (exception : Exception) {

                }
            }
            return action
        }
    }

    private val mAllowMobileNetworkSwitchListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(button: CompoundButton?, checked: Boolean) {
            setMobileDataChange(checked)
        }
    }

    private val mAllowNotificationsSwitchListener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(compoundButton: CompoundButton?, isChecked: Boolean) {
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

        var ok1 = false
        var ok2 = false
        var ok3 = false
        var ok4 = false
        var ok5 = false
        var ok6 = false
        var ok7 = false

        if (SharedPreferencesManager(context!!).isQuestionModuleFragmentOK()) {
            ok1 = true
        }
        if (SharedPreferencesManager(context!!).isStudySubjectFragmentOK()) {
            ok2 = true
        }
        if (SharedPreferencesManager(context!!).isStudyWrongQuestionFragmentOK()) {
            ok3 = true
        }
        if (SharedPreferencesManager(context!!).isExamFragmentOK()) {
            ok4 = true
        }
        if (SharedPreferencesManager(context!!).isAdvancesfragmentOK()) {
            ok5 = true
        }
        if (SharedPreferencesManager(context!!).isAdvancesfragmentOK()) {
            ok6 = true
        }
        if (SharedPreferencesManager(context!!).isExamsAverageFragmentOK()) {
            ok7 = true
        }

        FirebaseAuth.getInstance().signOut()
        SharedPreferencesManager(context!!).removeSessionData()
        SharedPreferencesManager(context!!).setPersistanceDataEnable(true)
        LoginManager.getInstance().logOut()

        if (ok1) {
            setQuestionModuleFragmentOK()
        }
        if (ok2) {
            setStudySubjectFragmentOK()
        }
        if (ok3) {
            setStudyWrongQuestionFragmentOK()
        }
        if (ok4) {
            setExamFragmentOK()
        }
        if (ok5) {
            setAdvancesFragmentOK()
        }
        if (ok6) {
            setSchoolAverageFragmentOK()
        }
        if (ok7) {
            setExamsAverageFragmentOK()
        }

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
            if (!mPassword.text.toString().equals("")) {
                user.setPassword(mPassword.text.toString())

                if (activity != null)
                    (activity as ContentActivity).showLoading(true)

                requestUpdateUserPassword(user)

                saveUser(user)
            }

        }
    }

    private val mLinkWithFacebookButtonListener = View.OnClickListener {

        if (NetworkUtil.isConnected(context!!)) {
            (activity as ContentActivity).showLoading(true)

            val mFacebookSignInButton = LoginButton(context)
            mFacebookSignInButton.setReadPermissions("email", "public_profile", "user_birthday", "user_friends");
            mFacebookSignInButton.registerCallback((activity as ContentActivity).getCallBackManager(), object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d(TAG, "facebook:onSuccess:$loginResult")
                    requestLinkAnonymousUserWithFacebookProvider(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d(TAG, "facebook:onCancel")
                    (activity as ContentActivity).showLoading(false)
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "facebook:onError", error)
                    (activity as ContentActivity).showLoading(false)
                }
            })
            mFacebookSignInButton.performClick()
        } else {
            ErrorDialog.newInstance("Error", "Necesitas tener conexión a intenet para poderte conectar",
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        }
    }

    override fun onLinkAnonymousUserWithFacebookProviderSuccess(success: Boolean) {
        super.onLinkAnonymousUserWithFacebookProviderSuccess(success)
        Log.d(TAG, "link with facebook success")
        val user = getUser()
        if (user != null) {
            user.setFacebookLogIn(true)
            saveUser(user)
        }
        // hide loading
        (activity as ContentActivity).showLoading(false)
        // paint again view
        onResume()
    }

    override fun onLinkAnonymousUserWithFacebookProviderFail(throwable: Throwable) {
        super.onLinkAnonymousUserWithFacebookProviderFail(throwable)
        Log.d(TAG, "link with facebook fail")
        val error = throwable
        if (error is FirebaseError) {
            val firebaseError = error as FirebaseError
            ErrorDialog.newInstance("Error", firebaseError.getErrorType().value,
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        } else {
            ErrorDialog.newInstance("Error", "No se pudo iniciar sesión",
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        }
        // facebook log out
        LoginManager.getInstance().logOut()
        // hide loading
        (activity as ContentActivity).showLoading(false)

    }

    override fun onLinkAnonymousUserWithGoogleProviderSuccess(success: Boolean) {
        super.onLinkAnonymousUserWithGoogleProviderSuccess(success)
        Log.d(TAG, "link with facebook success")
        val user = getUser()
        if (user != null) {
            user.setFacebookLogIn(true)
            saveUser(user)
        }
        // hide loading
        (activity as ContentActivity).showLoading(false)
        // paint again view
        onResume()
    }

    override fun onLinkAnonymousUserWithGoogleProviderFail(throwable: Throwable) {
        super.onLinkAnonymousUserWithGoogleProviderFail(throwable)
        Log.d(TAG, "link with facebook fail")

        val error = throwable
        if (error is FirebaseError) {
            val firebaseError = error as FirebaseError
            ErrorDialog.newInstance("Error", firebaseError.getErrorType().value,
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        } else {
            ErrorDialog.newInstance("Error", "No se pudo iniciar sesión",
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        }

        // google logout
        (activity as ContentActivity).getGoogleSignInClient().revokeAccess().addOnCompleteListener(object : OnCompleteListener<Void> {
            override fun onComplete(task: Task<Void>) {
                if (task.isSuccessful) {
                    Log.d(TAG, "logout success")
                } else {
                    Log.d(TAG, "logout not success")
                }
            }
        })

        // hide loading
        (activity as ContentActivity).showLoading(false)
    }

    private val mLinkWithGoogleButtonListener = View.OnClickListener {
        if (NetworkUtil.isConnected(context!!)) {
            (activity as ContentActivity).showLoading(true)
            signInWithGoogle()
        } else {
            ErrorDialog.newInstance("Error", "Necesitas tener conexión a intenet para poderte conectar",
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        }
    }

    private val mEditSchoolsListener = View.OnClickListener {
        goChooseSchoolsActivity()
    }

    private fun checkProviders() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            for (userInfo in firebaseUser.getProviderData()) {
                if (userInfo.getProviderId() == "facebook.com") {
                    Log.d("TAG", "User is signed in with Facebook")
                    mIsLoggedInWithFacebookImage.visibility = View.VISIBLE
                    mLinkWithFacebookButton.setOnClickListener(null)
                } else if (userInfo.getProviderId() == "google.com") {
                    Log.d("TAG", "User is signed in with Google")
                    mIsLoggedInWithGoogleImage.visibility = View.VISIBLE
                    mLinkWithGoogleButton.setOnClickListener(null)
                }
            }
        }
    }

    private fun checkMobileDataSate() {
        if (NetworkUtil.isMobileNetworkConnected(context!!)) {
            //mAllowMobileDataSwitch.isChecked = true
        } else {
            //mAllowMobileDataSwitch.isChecked = false
        }
    }

    private fun setMobileDataChange(checked: Boolean) {
        if (checked) {
            MyNetworkUtil.getInstance().setMobileDataEnabled(context!!, true)
            MyNetworkUtil.getInstance().setWifiEnable(context!!, false)
            //mAllowMobileDataSwitch.isChecked = true

        } else {
            MyNetworkUtil.getInstance().setMobileDataEnabled(context!!, false)
            MyNetworkUtil.getInstance().setWifiEnable(context!!, true)
            //mAllowMobileDataSwitch.isChecked = false
        }
    }

    private fun goLogInActivity() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.putExtra(SHOW_START, true)
        activity!!.startActivity(intent)
        activity!!.finish()
    }

    private fun signInWithGoogle() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent((activity as ContentActivity).getGoogleApiClient())
        startActivityForResult(signInIntent, LoginActivity.RC_SIGN_IN)
    }

    /*
     * This method returns the devices current API version
     */
    fun getAndroidVersion(): String {
        val release = Build.VERSION.RELEASE
        val sdkVersion = Build.VERSION.SDK_INT
        return "Android SDK: $sdkVersion ($release)"
    }

    /*
     * This method open google chrome to show terms and conditions web page
     */
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

    /*
     * This method open the native mail app to send an email to soporte@zerebrez.com
     */
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

    /*
     * This method change starts an activity to choose the schools
     */
    private fun goChooseSchoolsActivity() {
        val intent = Intent(activity, ChooseSchoolsActivity::class.java)
        intent.putExtra(SHOW_CONTINUE_BUTTON, false)
        intent.putExtra(SHOW_BACK_BUTTON, true)
        startActivityForResult(intent, BaseActivityLifeCycle.RC_CHOOSE_SCHOOL)
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

    /*
    * LogInActivity listeners to know google response
    */
    fun onGoogleResultSuccess(account: GoogleSignInAccount) {
        val idToken = account.getIdToken()
        val credential = GoogleAuthProvider.getCredential(idToken, null);
        requestLinkAnonymousUserWithGoogleProvider(credential)
    }

    fun onGoogleResultFaild(throwable: Throwable) {
        val error = throwable as GenericError
        if (error.getErrorType().equals(ErrorType.NULL_RESPONSE)) {
            Log.d(TAG, "response is null")
        } else if (error.getErrorType().equals(ErrorType.CANNOT_LOGIN)) {
            Log.d(TAG, "cannot do google login")
        }
        (activity as ContentActivity).getGoogleSignInClient().revokeAccess().addOnCompleteListener(object : OnCompleteListener<Void> {
            override fun onComplete(task: Task<Void>) {
                if (task.isSuccessful) {
                    Log.d(TAG, "logout success")
                } else {
                    Log.d(TAG, "logout not success")
                }
            }
        })
    }


    override fun onGetProfileRefactorSuccess(user: User) {
        super.onGetProfileRefactorSuccess(user)

        if (context != null) {
            saveUser(user)
            val mSchools = user.getSelectedSchools()

            requestGetUserSchools(mSchools)

            val course = user.getCourse()
            if (!course.equals("")) {
                mCourse.text = course.toUpperCase()
            }

            val userFirebase = FirebaseAuth.getInstance().currentUser
            if (userFirebase != null) {
                mEmail.setText(userFirebase.getEmail())
            }
        }
    }

    override fun onGetProfileRefactorFail(throwable: Throwable) {
        super.onGetProfileRefactorFail(throwable)
        if (activity != null)
            (activity as ContentActivity).showLoading(false)
    }

    override fun onGetUserSchoolsSuccess(schools: List<School>) {
        super.onGetUserSchoolsSuccess(schools)
        if (schools.isNotEmpty() && context != null) {
            // save user chools to get it in next view

            val user = getUser()
            if (user != null) {
                user.setSelectedShools(schools)
                saveUser(user)
                val updatedSchools = arrayListOf<School>()

                if (schools.isEmpty()) {
                    val school1 = School()
                    val school2 = School()
                    val school3 = School()
                    school1.setSchoolName("Sin opción")
                    updatedSchools.add(school1)
                    school2.setSchoolName("Sin opción")
                    updatedSchools.add(school2)
                    school3.setSchoolName("Sin opción")
                    updatedSchools.add(school3)
                } else if (schools.size == 1) {
                    updatedSchools.add(schools.get(0))
                    val school1 = School()
                    val school2 = School()
                    school1.setSchoolName("Sin opción")
                    updatedSchools.add(school1)
                    school2.setSchoolName("Sin opción")
                    updatedSchools.add(school2)
                } else if (schools.size == 2) {
                    updatedSchools.add(schools.get(0))
                    updatedSchools.add(schools.get(1))
                    val school1 = School()
                    school1.setSchoolName("Sin opción")
                    updatedSchools.add(school1)
                } else {
                    updatedSchools.addAll(schools)
                }

                mSchoolsListAdapter = SchoolListAdapter(updatedSchools, activity!!.applicationContext)
                mSelectedSchoolsList.adapter = mSchoolsListAdapter
                mEditSchoolsButton.visibility = View.VISIBLE
            }
        } else {
            mEditSchoolsButton.visibility = View.VISIBLE
            mEditSchoolsTextView.text = "Escoger"
            mSelectedSchoolsList.visibility = View.GONE
            mNotSelectedSchools.visibility = View.VISIBLE
        }
        if (activity != null)
            (activity as ContentActivity).showLoading(false)
    }

    override fun onGetUserSchoolsFail(throwable: Throwable) {
        super.onGetUserSchoolsFail(throwable)
        if (context != null) {
            val updatedSchools = arrayListOf<School>()
            val school1 = School()
            val school2 = School()
            val school3 = School()
            school1.setSchoolName("Sin opción")
            updatedSchools.add(school1)
            school2.setSchoolName("Sin opción")
            updatedSchools.add(school2)
            school3.setSchoolName("Sin opción")
            updatedSchools.add(school3)

            mSchoolsListAdapter = SchoolListAdapter(updatedSchools, activity!!.applicationContext)
            mSelectedSchoolsList.adapter = mSchoolsListAdapter
        }

        mEditSchoolsButton.visibility = View.VISIBLE
        mEditSchoolsTextView.text = "Escoger"
        mSelectedSchoolsList.visibility = View.GONE
        mNotSelectedSchools.visibility = View.VISIBLE
        if (activity != null)
            (activity as ContentActivity).showLoading(false)
    }


    override fun onUpdateUserPasswordSuccess(success: Boolean) {
        if (context != null) {
            super.onUpdateUserPasswordSuccess(success)
            if (activity != null)
                (activity as ContentActivity).showLoading(false)

            mPassword.setText("")

            ErrorDialog.newInstance("Tu contraseña fue cambiada",
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        }
    }

    override fun onUpdateUserPasswordFail(throwable: Throwable) {
        super.onUpdateUserPasswordFail(throwable)

        if (context != null) {
            val error = throwable
            if (error is FirebaseError) {
                val firebaseError = error as FirebaseError
                ErrorDialog.newInstance("Error", firebaseError.getErrorType().value,
                        DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
            } else {
                ErrorDialog.newInstance("Error", "No se pudo cambiar la contraseña",
                        DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
            }
            if (activity != null)
                (activity as ContentActivity).showLoading(false)
        }
    }

}
