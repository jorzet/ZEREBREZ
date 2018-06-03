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

package com.zerebrez.zerebrez.fragments.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.Error.FirebaseError
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.models.Image
import com.zerebrez.zerebrez.models.Institute
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.models.enums.ErrorType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import com.zerebrez.zerebrez.ui.activities.ChooseSchoolsActivity
import com.zerebrez.zerebrez.ui.activities.LoginActivity
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.utils.NetworkUtil

/**
 * Created by Jorge Zepeda Tinoco on 12/03/18.
 * jorzet.94@gmail.com
 */

private const val TAG : String = "SignUpFragment"

class SignUpFragment : BaseContentFragment(), ErrorDialog.OnErrorDialogListener {

    /*
     * tags
     */
    private val SHOW_CONTINUE_BUTTON : String = "show_continue_button"

    /*
     * UI accessors
     */
    private lateinit var mEmailEditText : EditText
    private lateinit var mPasswordEditText : EditText
    private lateinit var mSigninButton : Button
    private lateinit var mSigninFacebookButton : Button
    private lateinit var mSigninGoogleButton : View
    private lateinit var mLogInView : View
    private lateinit var mLoginAnotherProvidersView : View
    private lateinit var mLoadingProgresBar : ProgressBar

    /*
     * objects
     */
    private lateinit var mUser : User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.signup_fragment, container, false)!!

        mEmailEditText = rootView.findViewById(R.id.et_email)
        mPasswordEditText = rootView.findViewById(R.id.et_password)
        mSigninButton = rootView.findViewById(R.id.btn_login)
        mSigninFacebookButton = rootView.findViewById(R.id.btn_facebook_login)
        mSigninGoogleButton = rootView.findViewById(R.id.btn_google_login)
        mLogInView = rootView.findViewById(R.id.rl_login_inputs)
        mLoginAnotherProvidersView = rootView.findViewById(R.id.rl_login_other_provider)
        mLoadingProgresBar = rootView.findViewById(R.id.pb_loading)

        mSigninButton.setOnClickListener(mSigninButtonListener)
        mSigninFacebookButton.setOnClickListener(mSignInFacebookButtonListener)
        mSigninGoogleButton.setOnClickListener(mSignInGoogleButtonListener)
        mPasswordEditText.setOnEditorActionListener(onSendFormListener)

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
                mLogInView.visibility = View.VISIBLE
                mLoginAnotherProvidersView.visibility = View.VISIBLE
                mLoadingProgresBar.visibility = View.GONE
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Login Unsuccessful. ")
                val error = GenericError()
                error.setErrorType(ErrorType.NULL_RESPONSE)
                error.setErrorMessage("Respuesta sin datos")
                Toast.makeText(activity, "Login Unsuccessful", Toast.LENGTH_SHORT).show()
                onGoogleResultFaild(error)
            }
        }
    }

    private fun goChooseSchoolActivity() {
        val intent = Intent(activity, ChooseSchoolsActivity::class.java)
        intent.putExtra(SHOW_CONTINUE_BUTTON, true)
        startActivity(intent)
        activity!!.finish()
    }

    private fun signInWithGoogle() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent((activity as LoginActivity).getGoogleApiClient())
        startActivityForResult(signInIntent, LoginActivity.RC_SIGN_IN)
    }

    private val onSendFormListener = object : TextView.OnEditorActionListener {
        override fun onEditorAction(textView: TextView?, actionId: Int, event: KeyEvent?): Boolean {
            var action = false
            if (actionId.equals(EditorInfo.IME_ACTION_SEND)) {
                // hide keyboard
                try {
                    val inputMethodManager = textView!!.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(textView.getWindowToken(), 0)
                    mSigninButton.performClick()
                    action = true
                } catch (exception : Exception) {

                }
            }
            return action
        }
    }

    /*
     * Login button listener
     */
    private val mSigninButtonListener = View.OnClickListener {
        val email = mEmailEditText.text.toString()
        val password = mPasswordEditText.text.toString()

        if (!email.equals("") && !password.equals("")) {
            mUser = User(email, password)

            mLogInView.visibility = View.GONE
            mLoginAnotherProvidersView.visibility = View.GONE
            mLoadingProgresBar.visibility = View.VISIBLE

            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)

            requestUpdateUser(mUser)
        } else {
            Log.d(TAG, "error set email and password")
        }
    }

    /*
     * Login with facebook button listener
     */
    private val mSignInFacebookButtonListener = View.OnClickListener {
        if (NetworkUtil.isConnected(context!!)) {
            mLogInView.visibility = View.GONE
            mLoginAnotherProvidersView.visibility = View.GONE
            mLoadingProgresBar.visibility = View.VISIBLE

            val mFacebookSignInButton = LoginButton(context)
            mFacebookSignInButton.setReadPermissions("email", "public_profile", "user_birthday", "user_friends");
            mFacebookSignInButton.registerCallback((activity as LoginActivity).getCallBackManager(), object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d(TAG, "facebook:onSuccess:$loginResult")

                    val request = GraphRequest.newMeRequest(loginResult.getAccessToken()) { json, response ->
                        if (json != null) {
                            val email = json.getString("email")
                            val birthday = json.getString("birthday")
                            mUser = User()
                            mUser.setEmail(email)
                            mUser.setPassword(birthday)
                            saveUser(mUser)
                            requestLinkAnonymousUserWithFacebookProvider(loginResult.accessToken)
                        } else {
                            mLogInView.visibility = View.VISIBLE
                            mLoginAnotherProvidersView.visibility = View.VISIBLE
                            mLoadingProgresBar.visibility = View.GONE
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,gender,birthday")
                    request.setParameters(parameters)
                    request.executeAsync()
                }

                override fun onCancel() {
                    Log.d(TAG, "facebook:onCancel")
                    mLogInView.visibility = View.VISIBLE
                    mLoginAnotherProvidersView.visibility = View.VISIBLE
                    mLoadingProgresBar.visibility = View.GONE
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "facebook:onError", error)
                    mLogInView.visibility = View.VISIBLE
                    mLoginAnotherProvidersView.visibility = View.VISIBLE
                    mLoadingProgresBar.visibility = View.GONE
                }
            })
            mFacebookSignInButton.performClick()
        } else {
            ErrorDialog.newInstance("Error", "Necesitas tener conexión a intenet para poderte conectar",
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        }
    }

    /*
     * Login with google button listener
     */
    private val mSignInGoogleButtonListener = View.OnClickListener {
        if (NetworkUtil.isConnected(context!!)) {
            mLogInView.visibility = View.VISIBLE
            mLoginAnotherProvidersView.visibility = View.VISIBLE
            mLoadingProgresBar.visibility = View.GONE
            signInWithGoogle()
        } else {
            ErrorDialog.newInstance("Error", "Necesitas tener conexión a intenet para poderte conectar",
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        }
    }

    /*
     * Listeners when request update user data
     */
    override fun onUpdateUserSuccess(success: Boolean) {
        super.onUpdateUserSuccess(success)
        val user = getUser()
        if (user != null) {
            user.setEmail(mUser.getEmail())
            user.setPassword(mUser.getPassword())
            user.setPremiumUser(false)
            saveUser(user)

            // save uuid user
            requestSendUser(user)
        }
    }

    override fun onUpdateUserFail(throwable: Throwable) {
        super.onUpdateUserFail(throwable)
        val error = throwable
        if (error is FirebaseError) {
            val firebaseError = error as FirebaseError
            ErrorDialog.newInstance("Error", firebaseError.getErrorType().value,
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        } else {
            ErrorDialog.newInstance("Error", "No se pudo iniciar sesión",
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        }
        val dataHelper = DataHelper(context!!)
        dataHelper.saveSessionData(false)
    }

    /*
     * Listeners to send user data
     */
    override fun onSendUserSuccess(success: Boolean) {
        super.onSendUserSuccess(success)
        requestGetInstitutes()
    }

    override fun onSendUserFail(throwable: Throwable) {
        super.onSendUserFail(throwable)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(false)
        }
        mLogInView.visibility = View.VISIBLE
        mLoginAnotherProvidersView.visibility = View.VISIBLE
        mLoadingProgresBar.visibility = View.GONE
    }

    /*
     * Listeners to send institutes choosen
     */
    override fun onGetInstitutesSuccess(institutes: List<Institute>) {
        super.onGetInstitutesSuccess(institutes)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveInstitutes(institutes)
        }
        requestGetExams()
    }

    override fun onGetInstitutesFail(throwable: Throwable) {
        super.onGetInstitutesFail(throwable)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(false)
        }
        mLogInView.visibility = View.VISIBLE
        mLoginAnotherProvidersView.visibility = View.VISIBLE
        mLoadingProgresBar.visibility = View.GONE
    }

    /*
     * Listeners to gets exams
     */
    override fun onGetExamsSuccess(exams: List<Exam>) {
        super.onGetExamsSuccess(exams)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveExams(exams)
        }

        requestGetImagesPath()
    }

    override fun onGetExamsFail(throwable: Throwable) {
        super.onGetExamsFail(throwable)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(false)
        }
        mLogInView.visibility = View.VISIBLE
        mLoginAnotherProvidersView.visibility = View.VISIBLE
        mLoadingProgresBar.visibility = View.GONE
    }

    /*
     * Listenes to get images path
     */
    override fun onGetImagesPathSuccess(images: List<Image>) {
        super.onGetImagesPathSuccess(images)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(true)
            dataHelper.saveImagesPath(images)
        }

        if (activity != null) {
            goChooseSchoolActivity()
        }
    }

    override fun onGetImagesPathFail(throwable: Throwable) {
        super.onGetImagesPathFail(throwable)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(false)
        }
        mLogInView.visibility = View.VISIBLE
        mLoginAnotherProvidersView.visibility = View.VISIBLE
        mLoadingProgresBar.visibility = View.GONE
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
     * Facebook link listeners
     */
    override fun onLinkAnonymousUserWithFacebookProviderSuccess(success: Boolean) {
        super.onLinkAnonymousUserWithFacebookProviderSuccess(success)
        val user = getUser()
        if (user != null) {
            val profile = Profile.getCurrentProfile()
            Log.d(TAG, "" + profile)

            user.setPremiumUser(false)
            user.setFacebookLogIn(true)
            saveUser(user)

            requestUpdateUser(user)
        }
    }

    override fun onLinkAnonymousUserWithFacebookProviderFail(throwable: Throwable) {
        super.onLinkAnonymousUserWithFacebookProviderFail(throwable)
        LoginManager.getInstance().logOut()
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(false)
            mLogInView.visibility = View.VISIBLE
            mLoginAnotherProvidersView.visibility = View.VISIBLE
            mLoadingProgresBar.visibility = View.GONE
        }

        val error = throwable
        if (error is FirebaseError) {
            val firebaseError = error as FirebaseError
            ErrorDialog.newInstance("Error", firebaseError.getErrorType().value,
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        } else {
            ErrorDialog.newInstance("Error", "No se pudo iniciar sesión",
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        }
    }

    /*
     * Google link listeners
     */
    override fun onLinkAnonymousUserWithGoogleProviderSuccess(success: Boolean) {
        super.onLinkAnonymousUserWithGoogleProviderSuccess(success)
        val user = getUser()
        if (user != null) {
            val profile = Profile.getCurrentProfile()
            Log.d(TAG, "" + profile)

            user.setPremiumUser(false)
            user.setGoogleLogIn(true)
            saveUser(user)

            requestUpdateUser(user)
        }
    }

    override fun onLinkAnonymousUserWithGoogleProviderFail(throwable: Throwable) {
        super.onLinkAnonymousUserWithGoogleProviderFail(throwable)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(false)
            mLogInView.visibility = View.VISIBLE
            mLoginAnotherProvidersView.visibility = View.VISIBLE
            mLoadingProgresBar.visibility = View.GONE
        }

        val error = throwable
        if (error is FirebaseError) {
            val firebaseError = error as FirebaseError
            ErrorDialog.newInstance("Error", firebaseError.getErrorType().value,
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        } else {
            ErrorDialog.newInstance("Error", "No se pudo iniciar sesión",
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        }

        (activity as LoginActivity).getGoogleSignInClient().revokeAccess().addOnCompleteListener(object : OnCompleteListener<Void> {
            override fun onComplete(task: Task<Void>) {
                if (task.isSuccessful) {
                    Log.d(TAG, "logout success")
                } else {
                    Log.d(TAG, "logout not success")
                }
            }
        })
    }


    /*
    * LogInActivity listeners to know google response
    */
    fun onGoogleResultSuccess(account: GoogleSignInAccount) {
        val idToken = account.getIdToken()
        val credential = GoogleAuthProvider.getCredential(idToken, null);
        val email = account.getEmail()
        val randomPass = account.getId()
        mUser = User()
        mUser.setEmail(email!!)
        mUser.setPassword(randomPass!!)
        saveUser(mUser)
        requestLinkAnonymousUserWithGoogleProvider(credential)
    }

    fun onGoogleResultFaild(throwable: Throwable) {
        val error = throwable as GenericError
        if (error.getErrorType().equals(ErrorType.NULL_RESPONSE)) {
            Log.d(TAG, "response is null")
        } else if (error.getErrorType().equals(ErrorType.CANNOT_LOGIN)) {
            Log.d(TAG, "cannot do google login")
        }
        (activity as LoginActivity).getGoogleSignInClient().revokeAccess().addOnCompleteListener(object : OnCompleteListener<Void> {
            override fun onComplete(task: Task<Void>) {
                if (task.isSuccessful) {
                    Log.d(TAG, "logout success")
                } else {
                    Log.d(TAG, "logout not success")
                }
            }
        })
    }

}