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

package com.zerebrez.zerebrez.request

import android.app.Activity
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.zerebrez.zerebrez.models.*
import com.zerebrez.zerebrez.services.firebase.Firebase

/**
 * Created by Jorge Zepeda Tinoco on 28/04/18.
 * jorzet.94@gmail.com
 */

private const val TAG : String = "RequestManager"

class RequestManager(activity : Activity) {

    private val mActivity = activity

    fun requestDoLogIn(user : User?, onDoLogInListener : OnDoLogInListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onDoLogInListener.onDoLogInLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onDoLogInListener.onDoLogInError(result)
            }
        })

        firebase.requestLogIn(user)
    }

    fun requestUpdateUser(user : User, onUpdateUserListener: OnUpdateUserListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onUpdateUserListener.onUpdateUserLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onUpdateUserListener.onUpdateUserError(result)
            }
        })

        firebase.requestUpdateUser(user)
    }

    fun requestSendUser(user: User, onSendUserListener : OnSendUserListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onSendUserListener.onSendUserLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onSendUserListener.onSendUserError(result)
            }
        })

        firebase.requestSendUser(user)
    }

    fun requestSendAnsweredQuestions(modules: List<Module>, onSendAnsweredQuestionsListener: OnSendAnsweredQuestionsListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onSendAnsweredQuestionsListener.onSendAnsweredQuestionsLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
               onSendAnsweredQuestionsListener.onSendAnsweredQuestionsError(result)
            }
        })

        firebase.requestSendAnsweredQuestions(modules)
    }

    fun requestSendAnsweredModules(modules : List<Module>, onSendAnsweredModulesListener: OnSendAnsweredModulesListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onSendAnsweredModulesListener.onSendAnsweredModulesLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onSendAnsweredModulesListener.onSendAnsweredModulesError(result)
            }
        })

        firebase.requestSendAnsweredModules(modules)
    }

    fun requestSendAnsweredExams(exams: List<Exam>, onSendAnsweredExamsListener: OnSendAnsweredExamsListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onSendAnsweredExamsListener.onSendAnsweredExamsLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onSendAnsweredExamsListener.onSendAnsweredExamsError(result)
            }
        })

        firebase.requestSendAnsweredExams(exams)
    }

    fun requestSendSelectedSchools(schools: List<School>, onSendSelectedSchoolsListener: OnSendSelectedSchoolsListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onSendSelectedSchoolsListener.onSendSelectedSchoolsLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onSendSelectedSchoolsListener.onSendSelectedSchoolsError(result)
            }
        })

        firebase.requestSendSelectedSchools(schools)
    }

    fun requestGetModules(onGetModulesListener: OnGetModulesListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetModulesListener.onGetModulesLoaded(result as List<Module>)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetModulesListener.onGetModulesError(result)
            }
        })

        firebase.requestGetModules()
    }

    fun requestGetExamScores(onGetExamScoresListener: OnGetExamScoresListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetExamScoresListener.onGetExamScoresLoaded(result as List<ExamScore>)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetExamScoresListener.onGetExamScoresError(result)
            }
        })

        firebase.requestGetExamScores()
    }

    fun requestGetCourses(onGetCoursesListener: OnGetCoursesListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetCoursesListener.onGetCoursesLoaded(result as List<String>)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetCoursesListener.onGetCoursesError(result)
            }
        })

        firebase.requestGetCourses()
    }

    fun requestGetQuestions() {

    }

    fun requestGetUserData(onGetUserDataListener: OnGetUserDataListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetUserDataListener.onGetUserDataLoaded(result as User)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetUserDataListener.onGetUserDataError(result)
            }
        })

        firebase.requestGetUserData()
    }

    fun requestGetInstitutes(onGetInstitutesListener: OnGetInstitutesListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetInstitutesListener.onGetInstitutesLoaded(result as List<Institute>)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetInstitutesListener.onGetInstitutesError(result)
            }
        })

        firebase.requestGetInstitutes()
    }

    fun requestGetExams(onGetExamsListener: OnGetExamsListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetExamsListener.onGetExamsLoaded(result as List<Exam>)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetExamsListener.onGetExamError(result)
            }
        })

        firebase.requestGetExams()
    }

    fun requestGetImagesPath(onGetImagesPathListener: OnGetImagesPathListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetImagesPathListener.onGetImagesPathLoaded(result as List<Image>)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetImagesPathListener.onGetImagesPathError(result)
            }
        })

        firebase.requestGetImagesPath()
    }


    fun requestPaymentWorkFlow(onPaymentWorkFlowListener: OnPaymentWorkFlowListener) {

    }

    /*
    * Facebook user sign in request method
    */
    fun requestSignInUserWithFacebookProvider(accessToken: AccessToken, onSignInUserWithFacebookProviderListener : OnSignInUserWithFacebookProviderListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onSignInUserWithFacebookProviderListener.onSignInUserWithFacebookProviderLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onSignInUserWithFacebookProviderListener.onSignInUserWithFacebookProviderError(result)
            }
        })

        firebase.requestSignInUserWithFacebookProvider(accessToken)
    }

    /*
    * Facebook link anonymous user request method
    */
    fun requestLinkAnonymousUserWithFacebookProvider(accessToken: AccessToken, onLinkUserFacebookProviderListener : OnLinkUserFacebookProviderListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onLinkUserFacebookProviderListener.onLinkUserFacebookProviderLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onLinkUserFacebookProviderListener.onLinkUserFacebookProviderError(result)
            }
        })

        firebase.requestLinkAnonymousUserWithFacebookProvider(accessToken)
    }

    /*
     * Google user sign in request method
     */
    fun requestSigInUserWithGoogleProvider(credential: AuthCredential, onSignInUserWithGoogleProviderListener: OnSignInUserWithGoogleProviderListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onSignInUserWithGoogleProviderListener.onSignInUserWithGoogleProviderLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onSignInUserWithGoogleProviderListener.onSignInUserWithGoogleProviderError(result)
            }
        })

        firebase.requestSigInUserWithGoogleProvider(credential)
    }

    /*
     * Google link anonymous user request method
     */
    fun requestLinkAnonymousUserWithGoogleProvider(credential: AuthCredential, onLinkUserGoogleProviderListener: OnLinkUserGoogleProviderListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onLinkUserGoogleProviderListener.onLinkUserGoogleProviderLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onLinkUserGoogleProviderListener.onLinkUserGoogleProviderError(result)
            }
        })

        firebase.requestLinkAnonymousUserWithGoogleProvider(credential)
    }

    interface OnFirebaseLogIn {
        fun onFireBaseLogIn(success : Boolean)
    }

    interface OnDoLogInListener {
        fun onDoLogInLoaded(success : Boolean)
        fun onDoLogInError(throwable: Throwable)
    }

    interface OnUpdateUserListener {
        fun onUpdateUserLoaded(success : Boolean)
        fun onUpdateUserError(throwable: Throwable)
    }

    interface OnSendUserListener {
        fun onSendUserLoaded(success: Boolean)
        fun onSendUserError(throwable: Throwable)
    }

    interface OnGetModulesListener {
        fun onGetModulesLoaded(result : List<Module>)
        fun onGetModulesError(throwable: Throwable)
    }

    interface OnGetCoursesListener {
        fun onGetCoursesLoaded(result : List<String>)
        fun onGetCoursesError(throwable: Throwable)
    }

    interface OnGetExamScoresListener {
        fun onGetExamScoresLoaded(result: List<ExamScore>)
        fun onGetExamScoresError(throwable: Throwable)
    }

    interface OnGetQuestionsListener {
        fun onDoLogInLoaded(result : String)
        fun onDoLogInError(throwable : String)
    }

    interface OnGetUserDataListener {
        fun onGetUserDataLoaded(user : User)
        fun onGetUserDataError(throwable: Throwable)
    }

    interface OnSendAnsweredQuestionsListener {
        fun onSendAnsweredQuestionsLoaded(success: Boolean)
        fun onSendAnsweredQuestionsError(throwable: Throwable)
    }

    interface OnSendAnsweredModulesListener {
        fun onSendAnsweredModulesLoaded(success: Boolean)
        fun onSendAnsweredModulesError(throwable: Throwable)
    }

    interface OnSendAnsweredExamsListener {
        fun onSendAnsweredExamsLoaded(success: Boolean)
        fun onSendAnsweredExamsError(throwable: Throwable)
    }

    interface OnGetInstitutesListener {
        fun onGetInstitutesLoaded(institutes : List<Institute>)
        fun onGetInstitutesError(throwable: Throwable)
    }

    interface OnGetExamsListener {
        fun onGetExamsLoaded(exams : List<Exam>)
        fun onGetExamError(throwable: Throwable)
    }

    interface OnGetImagesPathListener {
        fun onGetImagesPathLoaded(images : List<Image>)
        fun onGetImagesPathError(throwable: Throwable)
    }

    interface OnSendSelectedSchoolsListener {
        fun onSendSelectedSchoolsLoaded(success: Boolean)
        fun onSendSelectedSchoolsError(throwable: Throwable)
    }

    interface OnPaymentWorkFlowListener {
        fun onPaymentWorkFlowSuccess()
        fun onPaymentWorkFlowFail(throwable: Throwable)
    }

    /*
     * social interfaces
     */
    interface OnSignInUserWithFacebookProviderListener {
        fun onSignInUserWithFacebookProviderLoaded(success: Boolean)
        fun onSignInUserWithFacebookProviderError(throwable: Throwable)
    }

    interface OnSignInUserWithGoogleProviderListener {
        fun onSignInUserWithGoogleProviderLoaded(success: Boolean)
        fun onSignInUserWithGoogleProviderError(throwable: Throwable)
    }

    interface OnLinkUserFacebookProviderListener {
        fun onLinkUserFacebookProviderLoaded(success: Boolean)
        fun onLinkUserFacebookProviderError(throwable: Throwable)
    }

    interface OnLinkUserGoogleProviderListener {
        fun onLinkUserGoogleProviderLoaded(success: Boolean)
        fun onLinkUserGoogleProviderError(throwable: Throwable)
    }

    /*
     * Download images listener
     */
    interface OnDownloadImagesListener {
        fun onDownloadAllImagesLoaded(success: Boolean)
        fun onDownloadAllImagesError(throwable: Throwable)
    }
}