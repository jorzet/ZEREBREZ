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

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.zerebrez.zerebrez.models.*
import com.zerebrez.zerebrez.request.RequestManager

/**
 * Created by Jorge Zepeda Tinoco on 27/02/18.
 * jorzet.94@gmail.com
 */

abstract class BaseContentFragment : BaseFragment() {

    private lateinit var mRequestManager : RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRequestManager = RequestManager(activity as Activity)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun requestLogIn(user : User?) {
        mRequestManager.requestDoLogIn(user, object : RequestManager.OnDoLogInListener {
            override fun onDoLogInLoaded(success: Boolean) {
                onDoLogInSuccess(success)
            }

            override fun onDoLogInError(throwable: Throwable) {
                onDoLogInFail(throwable)
            }

        })
    }

    fun requestUpdateUser(user : User) {
        mRequestManager.requestUpdateUser(user, object : RequestManager.OnUpdateUserListener {
            override fun onUpdateUserLoaded(success: Boolean) {
                onUpdateUserSuccess(success)
            }

            override fun onUpdateUserError(throwable: Throwable) {
                onUpdateUserFail(throwable)
            }
        })
    }

    fun requestSendUser(user : User) {
        mRequestManager.requestSendUser(user, object : RequestManager.OnSendUserListener {
            override fun onSendUserLoaded(success: Boolean) {
                onSendUserSuccess(success)
            }

            override fun onSendUserError(throwable: Throwable) {
                onSendUserFail(throwable)
            }
        })
    }

    fun requestModules() {
        mRequestManager.requestGetModules(object : RequestManager.OnGetModulesListener {
            override fun onGetModulesLoaded(result: List<Module>) {
                onGetModulesSucces(result)
            }

            override fun onGetModulesError(throwable: Throwable) {
                onGetModulesFail(throwable)
            }
        })
    }

    fun requestCourses() {
        mRequestManager.requestGetCourses(object : RequestManager.OnGetCoursesListener {
            override fun onGetCoursesLoaded(result: List<String>) {
                onGetCoursesSuccess(result)
            }

            override fun onGetCoursesError(throwable: Throwable) {
                onGetCoursesFail(throwable)
            }
        })
    }

    fun requestGetUserData() {
        mRequestManager.requestGetUserData(object : RequestManager.OnGetUserDataListener {
            override fun onGetUserDataLoaded(user: User) {
                onGetUserDataSuccess(user)
            }

            override fun onGetUserDataError(throwable: Throwable) {
                onGetUserDataFail(throwable)
            }
        })
    }

    fun requestGetInstitutes() {
        mRequestManager.requestGetInstitutes(object : RequestManager.OnGetInstitutesListener {
            override fun onGetInstitutesLoaded(institutes: List<Institute>) {
                onGetInstitutesSuccess(institutes)
            }

            override fun onGetInstitutesError(throwable: Throwable) {
                onGetInstitutesFail(throwable)
            }
        })
    }

    fun requestGetExams() {
        mRequestManager.requestGetExams(object : RequestManager.OnGetExamsListener {
            override fun onGetExamsLoaded(exams: List<Exam>) {
                onGetExamsSuccess(exams)
            }

            override fun onGetExamError(throwable: Throwable) {
                onGetExamsFail(throwable)
            }
        })
    }

    fun requestGetImagesPath() {
        mRequestManager.requestGetImagesPath(object : RequestManager.OnGetImagesPathListener {
            override fun onGetImagesPathLoaded(images: List<Image>) {
                onGetImagesPathSuccess(images)
            }

            override fun onGetImagesPathError(throwable: Throwable) {
                onGetImagesPathFail(throwable)
            }
        })
    }

    fun requestGetExamScores() {
        mRequestManager.requestGetExamScores(object : RequestManager.OnGetExamScoresListener {
            override fun onGetExamScoresLoaded(result: List<ExamScore>) {
                onGetExamScoresSuccess(result)
            }

            override fun onGetExamScoresError(throwable: Throwable) {
                onGetExamScoresFail(throwable)
            }
        })
    }

    /*
     * Facebook user sign in request method
     */
    fun requestSignInUserWithFacebookProvider(accessToken: AccessToken) {
        mRequestManager.requestSignInUserWithFacebookProvider(accessToken, object : RequestManager.OnSignInUserWithFacebookProviderListener {
            override fun onSignInUserWithFacebookProviderLoaded(success: Boolean) {
                onSignInUserWithFacebookProviderSuccess(success)
            }

            override fun onSignInUserWithFacebookProviderError(throwable: Throwable) {
                onSignInUserWithFacebookProviderFail(throwable)
            }
        })
    }

    /*
     * Facebook user sign in request method
     */
    fun requestLinkAnonymousUserWithFacebookProvider(accessToken: AccessToken) {
        mRequestManager.requestLinkAnonymousUserWithFacebookProvider(accessToken, object : RequestManager.OnLinkUserFacebookProviderListener {
            override fun onLinkUserFacebookProviderLoaded(success: Boolean) {
                onLinkAnonymousUserWithFacebookProviderSuccess(success)
            }

            override fun onLinkUserFacebookProviderError(throwable: Throwable) {
                onLinkAnonymousUserWithFacebookProviderFail(throwable)
            }
        })
    }

    /*
     * Google user sign in request method
     */
    fun requestSigInUserWithGoogleProvider(credential: AuthCredential) {
        mRequestManager.requestSigInUserWithGoogleProvider(credential, object : RequestManager.OnSignInUserWithGoogleProviderListener {
            override fun onSignInUserWithGoogleProviderLoaded(success: Boolean) {
                onSignInUserWithGoogleProviderSuccess(success)
            }

            override fun onSignInUserWithGoogleProviderError(throwable: Throwable) {
                onSignInUserWithGoogleProviderFail(throwable)
            }

        })
    }

    /*
     * Google link anonymous user request method
     */
    fun requestLinkAnonymousUserWithGoogleProvider(credential: AuthCredential) {
        mRequestManager.requestLinkAnonymousUserWithGoogleProvider(credential, object : RequestManager.OnLinkUserGoogleProviderListener {
            override fun onLinkUserGoogleProviderLoaded(success: Boolean) {
                onLinkAnonymousUserWithGoogleProviderSuccess(success)
            }

            override fun onLinkUserGoogleProviderError(throwable: Throwable) {
                onLinkAnonymousUserWithGoogleProviderFail(throwable)
            }
        })
    }

    open fun onDoLogInSuccess(success : Boolean) {
    }

    open fun onDoLogInFail(throwable: Throwable) {
    }

    open fun onUpdateUserSuccess(success: Boolean) {
    }

    open fun onUpdateUserFail(throwable: Throwable) {
    }

    open fun onSendUserSuccess(success: Boolean) {
    }

    open fun onSendUserFail(throwable: Throwable) {
    }

    open fun onSignInUserWithFacebookProviderSuccess(success: Boolean) {
    }

    open fun onSignInUserWithFacebookProviderFail(throwable: Throwable) {
    }

    open fun onSignInUserWithGoogleProviderSuccess(success: Boolean) {
    }

    open fun onSignInUserWithGoogleProviderFail(throwable: Throwable) {
    }

    open fun onLinkAnonymousUserWithFacebookProviderSuccess(success: Boolean) {
    }

    open fun onLinkAnonymousUserWithFacebookProviderFail(throwable: Throwable) {
    }

    open fun onLinkAnonymousUserWithGoogleProviderSuccess(success: Boolean) {
    }

    open fun onLinkAnonymousUserWithGoogleProviderFail(throwable: Throwable) {
    }

    open fun onGetModulesSucces(result : List<Module>) {
    }

    open fun onGetModulesFail(throwable: Throwable) {
    }

    open fun onGetCoursesSuccess(courses : List<String>) {
    }

    open fun onGetCoursesFail(throwable: Throwable) {
    }

    open fun onGetUserDataSuccess(user : User) {
    }

    open fun onGetUserDataFail(throwable: Throwable) {
    }

    open fun onGetInstitutesSuccess(institutes : List<Institute>) {
    }

    open fun onGetInstitutesFail(throwable: Throwable) {
    }

    open fun onGetExamsSuccess(exams : List<Exam>) {
    }

    open fun onGetExamsFail(throwable: Throwable) {
    }

    open fun onGetImagesPathSuccess(images : List<Image>) {
    }

    open fun onGetImagesPathFail(throwable: Throwable) {
    }

    open fun onGetExamScoresSuccess(examScores : List<ExamScore>) {
    }

    open fun onGetExamScoresFail(throwable: Throwable) {
    }
}