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
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.ui.activities.ContentActivity
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.zerebrez.zerebrez.models.*
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.utils.NetworkUtil
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.GoogleAuthProvider
import com.zerebrez.zerebrez.models.Error.FirebaseError
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.enums.ErrorType
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import com.zerebrez.zerebrez.ui.activities.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.zerebrez.zerebrez.BuildConfig
import com.zerebrez.zerebrez.services.firebase.Firebase
import com.zerebrez.zerebrez.utils.FontUtil

/**
 * Created by Jorge Zepeda Tinoco on 12/03/18.
 * jorzet.94@gmail.com
 */

private const val TAG : String = "SignInFragment"

class SignInFragment : BaseContentFragment(), ErrorDialog.OnErrorDialogListener {

    /*
     * UI accessors
     */
    private lateinit var mEmailEditText : EditText
    private lateinit var mPasswordEditText : EditText
    private lateinit var mSinginButton : Button
    private lateinit var mSinginFacebookButton : View
    private lateinit var mSinginGoogleButton : View
    private lateinit var mLogInView : View
    private lateinit var mLoginAnotherProvidersView : View
    private lateinit var mLoadingProgresBar : ProgressBar
    private lateinit var mTextLoginWith : TextView
    private lateinit var mForgotPassword : TextView
    private lateinit var mSendEmail : TextView

    /*
     * Objects
     */
    private lateinit var mUser : User

    /*
     * Variables
     */
    private var mNotLogedIn : Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.signin_fragment, container, false)!!

        mEmailEditText = rootView.findViewById(R.id.et_email)
        mPasswordEditText = rootView.findViewById(R.id.et_password)
        mSinginButton = rootView.findViewById(R.id.btn_login)
        mSinginFacebookButton = rootView.findViewById(R.id.btn_facebook_login)
        mSinginGoogleButton = rootView.findViewById(R.id.btn_google_login)
        mLogInView = rootView.findViewById(R.id.rl_login_inputs)
        mLoginAnotherProvidersView = rootView.findViewById(R.id.rl_login_other_provider)
        mLoadingProgresBar = rootView.findViewById(R.id.pb_loading)
        mTextLoginWith = rootView.findViewById(R.id.tv_login_with)
        mForgotPassword = rootView.findViewById(R.id.tv_i_forgot_my_password)
        mSendEmail = rootView.findViewById(R.id.tv_support_email)

        mSinginButton.typeface = FontUtil.getNunitoSemiBold(context!!)
        mTextLoginWith.typeface = FontUtil.getNunitoBold(context!!)
        mForgotPassword.typeface = FontUtil.getNunitoSemiBold(context!!)
        mSendEmail.typeface = FontUtil.getNunitoSemiBold(context!!)


        mSinginButton.setOnClickListener(mSinginButtonListener)
        mSinginFacebookButton.setOnClickListener(mSignInFacebookButtonListener)
        mSinginGoogleButton.setOnClickListener(mSinginGoogleButtonListener)
        mPasswordEditText.setOnEditorActionListener(onSendFormListener)
        mForgotPassword.setOnClickListener(mForgotPasswordListener)
        mSendEmail.setOnClickListener(mSendEmailListener)

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

    private fun goContentActivity() {
        if (activity != null) {
            val intent = Intent(activity, ContentActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = (activity as LoginActivity).getGoogleSignInClient().signInIntent
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
                    mSinginButton.performClick()
                    action = true
                } catch (exception : Exception) {

                }
            }
            return action
        }
    }

    private val mForgotPasswordListener = View.OnClickListener {

    }

    private val mSendEmailListener = View.OnClickListener {
        goSendEmailActivity()
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
        val userFirebase = FirebaseAuth.getInstance().currentUser
        var userUUID = ""
        val versionName = BuildConfig.VERSION_NAME
        if (userFirebase != null) {
            userUUID = userFirebase.uid
        }
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Sistema Operativo: " + getAndroidVersion() +
                "\n\n\n Versión app: " + versionName +
                "\n\n\n Cuenta: " + userUUID +
                "\n Correo: " + "" +
                "\n\n\n Aquí escribe tu mensaje" + "" +
                "\n\n\n (Para un mejor soporte no borres el sistema operativo ni la cuenta)")
        startActivity(Intent.createChooser(emailIntent, "Enviando email..."))
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
     * email password button listener
     */
    private val mSinginButtonListener : View.OnClickListener = View.OnClickListener {

        val email = mEmailEditText.text.toString()
        val password = mPasswordEditText.text.toString()

        if (!email.equals("") && !password.equals("")) {
            mUser = User(email, password)

            if (NetworkUtil.isConnected(context!!) && activity != null) {

                mLogInView.visibility = View.GONE
                mLoginAnotherProvidersView.visibility = View.GONE
                mLoadingProgresBar.visibility = View.VISIBLE

                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view!!.windowToken, 0)

                requestLogIn(mUser)
            } else {
                ErrorDialog.newInstance("Error", "Necesitas tener conexión a intenet para poderte conectar",
                        DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
            }
        } else {
            ErrorDialog.newInstance("Error", "Necesitas ingresar el correo y contraseña",
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "loginError")
        }

    }

    /*
     * facebook button listener
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
                    /*val request = GraphRequest.newMeRequest(loginResult.getAccessToken()) { json, response ->
                        if (json != null) {
                            val email = json.getString("email")
                            val birthday = json.getString("birthday")
                            mUser = User(email, birthday)

                            //saveUser(user)
                            requestLogIn(mUser)
                            //requestSignInUserWithFacebookProvider(loginResult.accessToken)
                        }else {
                            mLogInView.visibility = View.VISIBLE
                            mLoginAnotherProvidersView.visibility = View.VISIBLE
                            mLoadingProgresBar.visibility = View.GONE
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,email,gender,birthday")
                    request.setParameters(parameters)
                    request.executeAsync()*/

                    requestSignInUserWithFacebookProvider(loginResult.accessToken)
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
     * google button listener
     */
    private val mSinginGoogleButtonListener = View.OnClickListener {
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
     * log in listeners
     */
    override fun onDoLogInSuccess(success: Boolean) {
        super.onDoLogInSuccess(success)
        //saveUser(mUser)
        //if (mUser != null && !mUser.getCourse().equals("")) {
        //    requestGetImagesPath(mUser.getCourse())
        //}
        requestGetUserWithProvider()
        //goContentActivity()
        //requestModules()
    }

    override fun onDoLogInFail(throwable: Throwable) {
        super.onDoLogInFail(throwable)
        val dataHelper = DataHelper(context!!)
        dataHelper.saveSessionData(false)
        mLogInView.visibility = View.VISIBLE
        mLoginAnotherProvidersView.visibility = View.VISIBLE
        mLoadingProgresBar.visibility = View.GONE

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
     * Listeners to send user data
     */
    override fun onSendUserSuccess(success: Boolean) {
        super.onSendUserSuccess(success)
        val user = getUser()
        if (user != null && !user.getCourse().equals("")) {
            requestGetImagesPath(user.getCourse())
        }
    }

    override fun onSendUserFail(throwable: Throwable) {
        super.onSendUserFail(throwable)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(false)
            mLogInView.visibility = View.VISIBLE
            mLoginAnotherProvidersView.visibility = View.VISIBLE
            mLoadingProgresBar.visibility = View.GONE

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
    }

    /*
     * get modules listeners
     */
    override fun onGetModulesSucces(result: List<Module>) {
        super.onGetModulesSucces(result)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveModules(result)
        }
        requestGetExams()
    }

    override fun onGetModulesFail(throwable: Throwable) {
        super.onGetModulesFail(throwable)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(false)
        }
        mLogInView.visibility = View.VISIBLE
        mLoginAnotherProvidersView.visibility = View.VISIBLE
        mLoadingProgresBar.visibility = View.GONE
    }

    /*
     * Get exams listeners
     */
    override fun onGetExamsSuccess(exams: List<Exam>) {
        super.onGetExamsSuccess(exams)

        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveExams(exams)
        }
        requestGetUserData()
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
     * Get user data listeners
     */
    override fun onGetUserDataSuccess(user: User) {
        super.onGetUserDataSuccess(user)

        val mUser = getUser()
        if (mUser != null) {
            val dataHelper = DataHelper(context!!)
            val modules = dataHelper.getModulesAnsQuestions()
            val exams = dataHelper.getExams()

            mUser.setCourse(user.getCourse())
            mUser.setPremiumUser(user.isPremiumUser())

            if (user.getSelectedSchools().isNotEmpty()) {
                mUser.setSelectedShools(user.getSelectedSchools())
            }

            if (user.getAnsweredModule().isNotEmpty()) {
                for (i in 0 .. modules.size - 1) {
                    for (module in user.getAnsweredModule()) {
                        if (modules.get(i).getId().equals(module.getId())){
                            modules.get(i).setAnsweredModule(true)
                            modules.get(i).setCorrectQuestions(module.getCorrectQuestions())
                            modules.get(i).setIncorrectQuestions(module.getIncorrectQuestions())
                        }
                    }
                }
            }

            if (user.getAnsweredQuestionNewFormat().isNotEmpty()) {
                for (i in 0 .. modules.size - 1) {
                    for (j in 0 .. modules.get(i).getQuestionsNewFormat().size - 1) {
                        for (question2 in user.getAnsweredQuestionNewFormat()) {
                            if (modules.get(i).getQuestionsNewFormat().get(j).questionId.equals(question2.questionId)) {
                                modules.get(i).getQuestionsNewFormat().get(j).subject = question2.subject
                                modules.get(i).getQuestionsNewFormat().get(j).wasOK = question2.wasOK
                                modules.get(i).getQuestionsNewFormat().get(j).chosenOption = question2.chosenOption
                            }
                        }
                    }
                }
            }

            if (user.getAnsweredExams().isNotEmpty()) {
                for (i in 0 .. exams.size - 1) {
                    for (exam in user.getAnsweredExams()) {
                        if (exams.get(i).getExamId().equals(exam.getExamId())) {
                            exams.get(i).setAnsweredExam(true)
                            exams.get(i).setMisses(exam.getMisses())
                            exams.get(i).setHits(exam.getHits())
                        }
                    }
                }
            }

            mUser.setSelectedShools(user.getSelectedSchools())

            if (context != null) {
                Log.d(TAG, "save modules")
                dataHelper.saveModules(modules)
                dataHelper.saveExams(exams)
                saveUser(mUser)
            }
        } else {
            val mUser2 = User()
            if (context != null) {
                val dataHelper = DataHelper(context!!)
                val modules = dataHelper.getModulesAnsQuestions()
                val exams = dataHelper.getExams()

                mUser2.setEmail(user.getEmail())
                mUser2.setPassword(user.getPassword())
                mUser2.setCourse(user.getCourse())
                mUser2.setPremiumUser(user.isPremiumUser())

                if (user.getSelectedSchools().isNotEmpty()) {
                    mUser2.setSelectedShools(user.getSelectedSchools())
                }

                if (user.getAnsweredModule().isNotEmpty()) {
                    for (i in 0..modules.size - 1) {
                        for (module in user.getAnsweredModule()) {
                            if (modules.get(i).getId().equals(module.getId())) {
                                modules.get(i).setAnsweredModule(true)
                                modules.get(i).setCorrectQuestions(module.getCorrectQuestions())
                                modules.get(i).setIncorrectQuestions(module.getIncorrectQuestions())
                            }
                        }
                    }
                }

                if (user.getAnsweredQuestionNewFormat().isNotEmpty()) {
                    for (i in 0..modules.size - 1) {
                        for (j in 0..modules.get(i).getQuestionsNewFormat().size - 1) {
                            for (question2 in user.getAnsweredQuestionNewFormat()) {
                                if (modules.get(i).getQuestionsNewFormat().get(j).questionId.equals(question2.questionId)) {
                                    modules.get(i).getQuestionsNewFormat().get(j).subject = question2.subject
                                    modules.get(i).getQuestionsNewFormat().get(j).wasOK = question2.wasOK
                                    modules.get(i).getQuestionsNewFormat().get(j).chosenOption = question2.chosenOption
                                }
                            }
                        }
                    }
                }

                if (user.getAnsweredExams().isNotEmpty()) {
                    for (i in 0 .. exams.size - 1) {
                        for (exam in user.getAnsweredExams()) {
                            if (exams.get(i).getExamId().equals(exam.getExamId())) {
                                exams.get(i).setAnsweredExam(true)
                                exams.get(i).setMisses(exam.getMisses())
                                exams.get(i).setHits(exam.getHits())
                            }
                        }
                    }
                }

                mUser2.setSelectedShools(user.getSelectedSchools())

                if (context != null) {
                    Log.d(TAG, "save modules")
                    dataHelper.saveModules(modules)
                    dataHelper.saveExams(exams)
                    saveUser(mUser2)
                }
            }
        }
        requestCourses()
    }

    override fun onGetUserDataFail(throwable: Throwable) {
        super.onGetUserDataFail(throwable)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(false)
        }
        mLogInView.visibility = View.VISIBLE
        mLoginAnotherProvidersView.visibility = View.VISIBLE
        mLoadingProgresBar.visibility = View.GONE
    }

    /*
     * get courses listeners
     */
    override fun onGetCoursesSuccess(courses: List<String>) {
        super.onGetCoursesSuccess(courses)
        val user = getUser()
        if (user != null && !user.getCourse().equals("")) {
            requestGetInstitutes(user.getCourse())
        }
    }

    override fun onGetCoursesFail(throwable: Throwable) {
        super.onGetCoursesFail(throwable)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(false)
        }
        mLogInView.visibility = View.VISIBLE
        mLoginAnotherProvidersView.visibility = View.VISIBLE
        mLoadingProgresBar.visibility = View.GONE
    }

    /*
     * Get institutes listeners
     */
    override fun onGetInstitutesSuccess(institutes: List<Institute>) {
        super.onGetInstitutesSuccess(institutes)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveInstitutes(institutes)

            // update user selected institutes
            val user = getUser()
            if (user != null) {
                val schools = arrayListOf<School>()
                for (institute in institutes) {
                    for (school in institute.getSchools()) {
                        for (userSchool in user.getSelectedSchools()) {
                            if (school.getSchoolId().equals(userSchool.getSchoolId()) &&
                                    institute.getInstituteId().equals(userSchool.getInstituteId())) {
                                schools.add(school)
                            }
                        }
                    }
                }
                user.setSelectedShools(schools)
                saveUser(user)
            }
        }
        val user = getUser()
        if (user != null && !user.getCourse().equals("")) {
            requestGetImagesPath(user.getCourse())
        }
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
     * Get images path listeners
     */
    override fun onGetImagesPathSuccess(images: List<Image>) {
        super.onGetImagesPathSuccess(images)

        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(true)
            dataHelper.saveImagesPath(images)
        }

        if (activity != null) {
            goContentActivity()
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
     * LogInActivity listeners to know google response
     */
    fun onGoogleResultSuccess(account: GoogleSignInAccount) {
        val idToken = account.getIdToken()
        val credential = GoogleAuthProvider.getCredential(idToken, null);
        requestSigInUserWithGoogleProvider(credential)
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

    override fun onGetUserWithProviderSuccess(user: User) {
        super.onGetUserWithProviderSuccess(user)
        if (context != null) {
            if (user != null && !user.getCourse().equals("")) {
                saveUser(user)
                requestGetImagesPath(user.getCourse())
            }
            mNotLogedIn = false
        }
    }

    override fun onGetUserWithProviderFail(throwable: Throwable) {
        super.onGetUserWithProviderFail(throwable)
        if (context != null) {
            mUser = User()
            mUser.setCourse("comipems")
            val userFirebase = FirebaseAuth.getInstance().currentUser
            if (userFirebase != null) {
                mUser.setUUID(userFirebase.uid)
            }
            mUser.setPremiumUser(false)
            saveUser(mUser)
            // save uuid user
            requestSendUser(mUser)
        }
    }

    /*
     * Facebook Sign in listeners
     */
    override fun onSignInUserWithFacebookProviderSuccess(success: Boolean) {
        super.onSignInUserWithFacebookProviderSuccess(success)
        if (context != null) {
            Log.d(TAG, "login with facebook success")
            requestGetUserWithProvider()
            //requestGetImagesPath()
            //goContentActivity()
            //requestModules()
        }
    }

    override fun onSignInUserWithFacebookProviderFail(throwable: Throwable) {
        super.onSignInUserWithFacebookProviderFail(throwable)
        if (context != null) {
            LoginManager.getInstance().logOut()
            Log.d(TAG, "login with facebook fail")
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(false)
            mLogInView.visibility = View.VISIBLE
            mLoginAnotherProvidersView.visibility = View.VISIBLE
            mLoadingProgresBar.visibility = View.GONE

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
    }

    /*
     * Google Sign in listeners
     */
    override fun onSignInUserWithGoogleProviderSuccess(success: Boolean) {
        super.onSignInUserWithGoogleProviderSuccess(success)
        if (context != null) {
            requestGetUserWithProvider()
            //goContentActivity()
            //requestModules()
        }
    }

    override fun onSignInUserWithGoogleProviderFail(throwable: Throwable) {
        super.onSignInUserWithGoogleProviderFail(throwable)
        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(false)
            mLogInView.visibility = View.VISIBLE
            mLoginAnotherProvidersView.visibility = View.VISIBLE
            mLoadingProgresBar.visibility = View.GONE

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
    }

}