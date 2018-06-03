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

package com.zerebrez.zerebrez.services.firebase

import android.app.Activity
import android.util.Log
import com.facebook.AccessToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.models.enums.ErrorType
import com.zerebrez.zerebrez.request.AbstractPendingRequest
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.zerebrez.zerebrez.models.Error.FirebaseError
import com.zerebrez.zerebrez.models.enums.LoginErrorType


/**
 * Created by Jorge Zepeda Tinoco on 28/04/18.
 * jorzet.94@gmail.com
 */

private const val TAG : String = "Engagement"

abstract class Engagement constructor(activity: Activity) : AbstractPendingRequest(){

    private val mActivity : Activity = activity
    private lateinit var mAuth : FirebaseAuth

    protected fun requestFirebaseLogIn(user : User?) {
        mAuth = FirebaseAuth.getInstance()

        if (user != null) {
            mAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(mActivity, object : OnCompleteListener<AuthResult> {
                        override fun onComplete(task: Task<AuthResult>) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful)
                            if (task.isSuccessful) {
                                onFirebaseLogIn(task.isSuccessful)
                                onRequestListenerSucces.onSuccess(task.isSuccessful)
                            } else {
                                Log.d(TAG, "LoginError.");
                                if (task.exception is FirebaseAuthUserCollisionException) {
                                    val exception = task.exception as FirebaseAuthUserCollisionException
                                    val error = FirebaseError()
                                    if (exception.errorCode.equals("ERROR_INVALID_EMAIL")){
                                        error.setErrorType(LoginErrorType.INVALID_EMAIL)
                                    } else if (exception.errorCode.equals("ERROR_INVALID_CREDENTIAL")){
                                        error.setErrorType(LoginErrorType.INVALID_CREDENTIAL)
                                    } else if (exception.errorCode.equals("ERROR_WRONG_PASSWORD")){
                                        error.setErrorType(LoginErrorType.WRONG_PASSWORD)
                                    } else if (exception.errorCode.equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL")) {
                                        error.setErrorType(LoginErrorType.ACCOUNT_EXIST_WITH_DIFFERENT_CREDENTIAL)
                                    } else if (exception.errorCode.equals("ERROR_USER_DISABLED")){
                                        error.setErrorType(LoginErrorType.USER_DISABLED)
                                    } else if (exception.errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")){
                                        error.setErrorType(LoginErrorType.EMAIL_ADLREADY_IN_USE)
                                    } else if (exception.errorCode.equals("ERROR_WEAK_PASSWORD")){
                                        error.setErrorType(LoginErrorType.WEAK_PASSWORD)
                                    } else if (exception.errorCode.equals("ERROR_USER_NOT_FOUND")){
                                        error.setErrorType(LoginErrorType.USER_NOT_FOUND)
                                    } else if (exception.errorCode.equals("ERROR_CREDENTIAL_ALREADY_IN_USE")){
                                        error.setErrorType(LoginErrorType.ERROR_CREDENTIAL_ALREADY_IN_USE)
                                    } else {
                                        error.setErrorType(LoginErrorType.DEFAULT)
                                    }

                                    onRequestLietenerFailed.onFailed(error)
                                } else {
                                    val error = GenericError()
                                    error.setErrorType(ErrorType.CANNOT_LOGIN)
                                    onRequestLietenerFailed.onFailed(error)
                                }
                            }
                        }
                    })
        } else {
            mAuth.signInAnonymously().addOnCompleteListener(mActivity, object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful)
                    if (task.isSuccessful) {
                        onFirebaseLogIn(task.isSuccessful)
                        onRequestListenerSucces.onSuccess(task.isSuccessful)
                    } else {
                        Log.d(TAG, "LoginError");
                        if (task.exception != null) {
                            val error = task.exception
                            onRequestLietenerFailed.onFailed(error!!)
                        } else {
                            val error = GenericError()
                            error.setErrorType(ErrorType.CANNOT_LOGIN)
                            onRequestLietenerFailed.onFailed(error)
                        }
                    }
                }
            })
        }
    }

    protected fun requestFirebaseUpdateUserEmail(user : User) {
        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            mAuth.currentUser!!.updateEmail(user.getEmail()).addOnCompleteListener(mActivity, object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User email address updated.");
                        onEmailUpdatedSuccess(user)
                    } else {
                        Log.d(TAG, "User email address not updated.");
                        val error = GenericError()
                        error.setErrorType(ErrorType.EMAIL_NOT_UPDATED)
                        onEmailUpdatedFail(error)
                    }
                }
            })
        } else {

        }
    }

    protected fun requestFirebaseUpdateUserPassword(user : User) {
        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            mAuth.currentUser!!.updatePassword(user.getPassword()).addOnCompleteListener(mActivity, object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User password address updated.");
                        onPasswordUpdatedSuccess(task.isSuccessful())
                    } else {
                        Log.d(TAG, "User email address not updated.");
                        val error = GenericError()
                        error.setErrorType(ErrorType.PASSWORD_NOT_UPDATED)
                        onPasswordUpdatedFail(error)
                    }
                }
            })
        } else {

        }
    }

    fun requestSignInWithFacebookProvider(token : AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        mAuth = FirebaseAuth.getInstance()
        val credential = FacebookAuthProvider.getCredential(token.getToken())
        mAuth.signInWithCredential(credential).addOnCompleteListener(mActivity, OnCompleteListener<AuthResult> { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success")
                val user = mAuth.currentUser
                onRequestListenerSucces.onSuccess(task.isSuccessful)

            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                if (task.exception is FirebaseAuthUserCollisionException) {
                    val exception = task.exception as FirebaseAuthUserCollisionException
                    val error = FirebaseError()
                    if (exception.errorCode.equals("ERROR_INVALID_EMAIL")){
                        error.setErrorType(LoginErrorType.INVALID_EMAIL)
                    } else if (exception.errorCode.equals("ERROR_INVALID_CREDENTIAL")){
                        error.setErrorType(LoginErrorType.INVALID_CREDENTIAL)
                    } else if (exception.errorCode.equals("ERROR_WRONG_PASSWORD")){
                        error.setErrorType(LoginErrorType.WRONG_PASSWORD)
                    } else if (exception.errorCode.equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL")) {
                        error.setErrorType(LoginErrorType.ACCOUNT_EXIST_WITH_DIFFERENT_CREDENTIAL)
                    } else if (exception.errorCode.equals("ERROR_USER_DISABLED")){
                        error.setErrorType(LoginErrorType.USER_DISABLED)
                    } else if (exception.errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")){
                        error.setErrorType(LoginErrorType.EMAIL_ADLREADY_IN_USE)
                    } else if (exception.errorCode.equals("ERROR_WEAK_PASSWORD")){
                        error.setErrorType(LoginErrorType.WEAK_PASSWORD)
                    } else if (exception.errorCode.equals("ERROR_USER_NOT_FOUND")){
                        error.setErrorType(LoginErrorType.USER_NOT_FOUND)
                    } else if (exception.errorCode.equals("ERROR_CREDENTIAL_ALREADY_IN_USE")){
                        error.setErrorType(LoginErrorType.ERROR_CREDENTIAL_ALREADY_IN_USE)
                    } else {
                        error.setErrorType(LoginErrorType.DEFAULT)
                    }

                    onRequestLietenerFailed.onFailed(error)
                } else {
                    val error = GenericError()
                    error.setErrorType(ErrorType.FACEBOOK_NOT_SIGNED_IN)
                    onRequestLietenerFailed.onFailed(error)
                }
            }
        })

    }

    fun requestLinkWithFacebookProvider(token : AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            val credential = FacebookAuthProvider.getCredential(token.getToken())
            mAuth.currentUser!!.linkWithCredential(credential).addOnCompleteListener(mActivity, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    // link success, update UI with the signed-in user's information
                    Log.d(TAG, "linkWithCredential:success")
                    val user = mAuth.currentUser
                    onRequestListenerSucces.onSuccess(task.isSuccessful)

                } else {
                    // If link fails, display a message to the user.
                    Log.w(TAG, "linkWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        val exception = task.exception as FirebaseAuthUserCollisionException
                        val error = FirebaseError()
                        if (exception.errorCode.equals("ERROR_INVALID_EMAIL")){
                            error.setErrorType(LoginErrorType.INVALID_EMAIL)
                        } else if (exception.errorCode.equals("ERROR_INVALID_CREDENTIAL")){
                            error.setErrorType(LoginErrorType.INVALID_CREDENTIAL)
                        } else if (exception.errorCode.equals("ERROR_WRONG_PASSWORD")){
                            error.setErrorType(LoginErrorType.WRONG_PASSWORD)
                        } else if (exception.errorCode.equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL")) {
                            error.setErrorType(LoginErrorType.ACCOUNT_EXIST_WITH_DIFFERENT_CREDENTIAL)
                        } else if (exception.errorCode.equals("ERROR_USER_DISABLED")){
                            error.setErrorType(LoginErrorType.USER_DISABLED)
                        } else if (exception.errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")){
                            error.setErrorType(LoginErrorType.EMAIL_ADLREADY_IN_USE)
                        } else if (exception.errorCode.equals("ERROR_WEAK_PASSWORD")){
                            error.setErrorType(LoginErrorType.WEAK_PASSWORD)
                        } else if (exception.errorCode.equals("ERROR_USER_NOT_FOUND")){
                            error.setErrorType(LoginErrorType.USER_NOT_FOUND)
                        } else if (exception.errorCode.equals("ERROR_CREDENTIAL_ALREADY_IN_USE")){
                            error.setErrorType(LoginErrorType.ERROR_CREDENTIAL_ALREADY_IN_USE)
                        } else {
                            error.setErrorType(LoginErrorType.DEFAULT)
                        }

                        onRequestLietenerFailed.onFailed(error)
                    } else {
                        val error = GenericError()
                        error.setErrorType(ErrorType.FACEBOOK_NOT_LINKED)
                        onRequestLietenerFailed.onFailed(error)
                    }
                }
            })
        }
    }

    fun requestSignInWithGoogleProvider(credential : AuthCredential) {
        Log.d(TAG, "handleFacebookAccessToken: ${credential.provider}")

        mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential).addOnCompleteListener(mActivity, OnCompleteListener<AuthResult> { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success")
                val user = mAuth.currentUser
                onRequestListenerSucces.onSuccess(task.isSuccessful)

            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure", task.exception)
                if (task.exception is FirebaseAuthUserCollisionException) {
                    val exception = task.exception as FirebaseAuthUserCollisionException
                    val error = FirebaseError()
                    if (exception.errorCode.equals("ERROR_INVALID_EMAIL")){
                        error.setErrorType(LoginErrorType.INVALID_EMAIL)
                    } else if (exception.errorCode.equals("ERROR_INVALID_CREDENTIAL")){
                        error.setErrorType(LoginErrorType.INVALID_CREDENTIAL)
                    } else if (exception.errorCode.equals("ERROR_WRONG_PASSWORD")){
                        error.setErrorType(LoginErrorType.WRONG_PASSWORD)
                    } else if (exception.errorCode.equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL")) {
                        error.setErrorType(LoginErrorType.ACCOUNT_EXIST_WITH_DIFFERENT_CREDENTIAL)
                    } else if (exception.errorCode.equals("ERROR_USER_DISABLED")){
                        error.setErrorType(LoginErrorType.USER_DISABLED)
                    } else if (exception.errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")){
                        error.setErrorType(LoginErrorType.EMAIL_ADLREADY_IN_USE)
                    } else if (exception.errorCode.equals("ERROR_WEAK_PASSWORD")){
                        error.setErrorType(LoginErrorType.WEAK_PASSWORD)
                    } else if (exception.errorCode.equals("ERROR_USER_NOT_FOUND")){
                        error.setErrorType(LoginErrorType.USER_NOT_FOUND)
                    } else if (exception.errorCode.equals("ERROR_CREDENTIAL_ALREADY_IN_USE")){
                        error.setErrorType(LoginErrorType.ERROR_CREDENTIAL_ALREADY_IN_USE)
                    } else {
                        error.setErrorType(LoginErrorType.DEFAULT)
                    }

                    onRequestLietenerFailed.onFailed(error)
                } else {
                    val error = GenericError()
                    error.setErrorType(ErrorType.GOOGLE_NOT_SIGNED_IN)
                    onRequestLietenerFailed.onFailed(error)
                }
            }
        })

    }

    fun requestLinkWithGoogleProvider(credential : AuthCredential) {
        Log.d(TAG, "handleFacebookAccessToken: ${credential.provider}")

        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser != null) {
            mAuth.currentUser!!.linkWithCredential(credential).addOnCompleteListener(mActivity, OnCompleteListener<AuthResult> { task ->
                if (task.isSuccessful) {
                    // link success, update UI with the signed-in user's information
                    Log.d(TAG, "linkWithCredential:success")
                    val user = mAuth.currentUser
                    onRequestListenerSucces.onSuccess(task.isSuccessful)

                } else {
                    // If link fails, display a message to the user.
                    Log.w(TAG, "linkWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        val exception = task.exception as FirebaseAuthUserCollisionException
                        val error = FirebaseError()
                        if (exception.errorCode.equals("ERROR_INVALID_EMAIL")){
                            error.setErrorType(LoginErrorType.INVALID_EMAIL)
                        } else if (exception.errorCode.equals("ERROR_INVALID_CREDENTIAL")){
                            error.setErrorType(LoginErrorType.INVALID_CREDENTIAL)
                        } else if (exception.errorCode.equals("ERROR_WRONG_PASSWORD")){
                            error.setErrorType(LoginErrorType.WRONG_PASSWORD)
                        } else if (exception.errorCode.equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL")) {
                            error.setErrorType(LoginErrorType.ACCOUNT_EXIST_WITH_DIFFERENT_CREDENTIAL)
                        } else if (exception.errorCode.equals("ERROR_USER_DISABLED")){
                            error.setErrorType(LoginErrorType.USER_DISABLED)
                        } else if (exception.errorCode.equals("ERROR_EMAIL_ALREADY_IN_USE")){
                            error.setErrorType(LoginErrorType.EMAIL_ADLREADY_IN_USE)
                        } else if (exception.errorCode.equals("ERROR_WEAK_PASSWORD")){
                            error.setErrorType(LoginErrorType.WEAK_PASSWORD)
                        } else if (exception.errorCode.equals("ERROR_USER_NOT_FOUND")){
                            error.setErrorType(LoginErrorType.USER_NOT_FOUND)
                        } else if (exception.errorCode.equals("ERROR_CREDENTIAL_ALREADY_IN_USE")){
                            error.setErrorType(LoginErrorType.ERROR_CREDENTIAL_ALREADY_IN_USE)
                        } else {
                            error.setErrorType(LoginErrorType.DEFAULT)
                        }

                        onRequestLietenerFailed.onFailed(error)
                    } else {
                        val error = GenericError()
                        error.setErrorType(ErrorType.GOOGLE_NOT_LINKED)
                        onRequestLietenerFailed.onFailed(error)
                    }
                }
            })
        }
    }

    protected fun getCurrentUser() : FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    open fun onFirebaseLogIn(success: Boolean) {
    }

    open fun onEmailUpdatedSuccess(user : User) {
    }

    open fun onEmailUpdatedFail(throwable: Throwable) {
    }

    open fun onPasswordUpdatedSuccess(success: Boolean) {
    }

    open fun onPasswordUpdatedFail(throwable: Throwable) {
    }

}