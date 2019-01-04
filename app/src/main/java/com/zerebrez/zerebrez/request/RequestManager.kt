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

package com.zerebrez.zerebrez.request

import android.app.Activity
import com.facebook.AccessToken
import com.google.firebase.auth.AuthCredential
import com.zerebrez.zerebrez.models.*
import com.zerebrez.zerebrez.models.enums.ComproPagoStatus
import com.zerebrez.zerebrez.services.firebase.CheckUserWithProviderRequest
import com.zerebrez.zerebrez.services.firebase.Firebase
import com.zerebrez.zerebrez.services.firebase.advances.AdvancesRequest
import com.zerebrez.zerebrez.services.firebase.courses.CourseRequest
import com.zerebrez.zerebrez.services.firebase.practice.ExamsRequest
import com.zerebrez.zerebrez.services.firebase.practice.QuestionModuleRequest
import com.zerebrez.zerebrez.services.firebase.practice.WrongQuestionRequest
import com.zerebrez.zerebrez.services.firebase.profile.ProfileRequest
import com.zerebrez.zerebrez.services.firebase.profile.SchoolsRequest
import com.zerebrez.zerebrez.services.firebase.question.QuestionNewFormatRequest
import com.zerebrez.zerebrez.services.firebase.question.TipsRequest
import com.zerebrez.zerebrez.services.firebase.score.ExamsScoreRequest
import com.zerebrez.zerebrez.services.firebase.score.SchoolsAverageRequest
import com.zerebrez.zerebrez.services.firebase.subject.SubjectQuestionRequest
import com.zerebrez.zerebrez.services.firebase.subject.SubjectRequest

/**
 * Created by Jorge Zepeda Tinoco on 28/04/18.
 * jorzet.94@gmail.com
 */

private const val TAG : String = "RequestManager"

class RequestManager(activity : Activity) {

    private val mActivity = activity

    fun requestSendPasswordResetEmail(email: String, onSendPasswordResetEmailListener: OnSendPasswordResetEmailListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onSendPasswordResetEmailListener.onSendPasswordResetEmailLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onSendPasswordResetEmailListener.onSendPasswordResetEmailFail(result)
            }
        })

        firebase.requestSendPasswordResetEmail(email)
    }

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

    fun requestUpdateUserPassword(user : User, onUpdateUserPasswordListener : OnUpdateUserPasswordListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onUpdateUserPasswordListener.onUpdateUserPasswordLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onUpdateUserPasswordListener.onUpdateUserPasswordError(result)
            }
        })

        firebase.requestUpdateUserPassword(user)
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


    fun requestSendUserComproPago(user: User, billingId: String, comproPagoStatus: ComproPagoStatus, onSendUserComproPagoListener: OnSendUserComproPagoListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onSendUserComproPagoListener.onSendUserComproPagoLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onSendUserComproPagoListener.onSendUserComproPagoError(result)
            }
        })

        firebase.requestSendUserComproPago(user, billingId, comproPagoStatus)
    }

    fun requestRemoveCompropagoNode(user: User, onRemoveComproPagoNodeListener: OnRemoveComproPagoNodeListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onRemoveComproPagoNodeListener.onRemoveComproPagoNodeLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onRemoveComproPagoNodeListener.onRemoveComproPagoNodeError(result)
            }
        })

        firebase.requestRemoveCompropagoNode(user)
    }

    /*fun requestSendAnsweredQuestions(questions: List<Question>, course: String, onSendAnsweredQuestionsListener: OnSendAnsweredQuestionsListener) {
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

        firebase.requestSendAnsweredQuestions(questions, course)
    }*/

    fun requestSendAnsweredQuestionsNewFormat(questions: List<QuestionNewFormat>, course: String, onSendAnsweredQuestionsNewFormatListener: OnSendAnsweredQuestionsNewFormatListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onSendAnsweredQuestionsNewFormatListener.onSendAnsweredQuestionsNewFormatLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onSendAnsweredQuestionsNewFormatListener.onSendAnsweredQuestionsNewFormatError(result)
            }
        })

        firebase.requestSendAnsweredQuestionsNewFormat(questions, course)
    }

    fun requestSendAnsweredQuestionNewFormat(question: QuestionNewFormat, course: String, onSendAnsweredQuestionNewFormatListener: OnSendAnsweredQuestionNewFormatListener) {
        val firebase = Firebase(mActivity)

        firebase.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onSendAnsweredQuestionNewFormatListener.onSendAnsweredQuestionNewFormatLoaded(result as Boolean)
            }
        })

        firebase.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onSendAnsweredQuestionNewFormatListener.onSendAnsweredQuestionNewFormatError(result)
            }
        })

        firebase.requestSendAnsweredQuestionNewFormat(question, course)
    }

    fun requestSendAnsweredModules(module : Module, course: String, onSendAnsweredModulesListener: OnSendAnsweredModulesListener) {
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

        firebase.requestSendAnsweredModules(module, course)
    }

    fun requestSendAnsweredExams(exam: Exam, course: String, onSendAnsweredExamsListener: OnSendAnsweredExamsListener) {
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

        firebase.requestSendAnsweredExams(exam, course)
    }

    fun requestSendSelectedSchools(user: User, schools: List<School>, onSendSelectedSchoolsListener: OnSendSelectedSchoolsListener) {
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

        firebase.requestSendSelectedSchools(user, schools)
    }


    /*fun requestGetModules(onGetModulesListener: OnGetModulesListener) {
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
    }*/


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

    fun requestGetInstitutes(course: String, onGetInstitutesListener: OnGetInstitutesListener) {
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

        firebase.requestGetInstitutes(course)
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

    fun requestGetImagesPath(course: String, onGetImagesPathListener: OnGetImagesPathListener) {
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

        firebase.requestGetImagesPath(course)
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

    interface OnSendPasswordResetEmailListener {
        fun onSendPasswordResetEmailLoaded(success : Boolean)
        fun onSendPasswordResetEmailFail(throwable: Throwable)
    }

    interface OnDoLogInListener {
        fun onDoLogInLoaded(success : Boolean)
        fun onDoLogInError(throwable: Throwable)
    }

    interface OnUpdateUserListener {
        fun onUpdateUserLoaded(success : Boolean)
        fun onUpdateUserError(throwable: Throwable)
    }

    interface OnUpdateUserPasswordListener {
        fun onUpdateUserPasswordLoaded(success: Boolean)
        fun onUpdateUserPasswordError(throwable: Throwable)
    }

    interface OnSendUserListener {
        fun onSendUserLoaded(success: Boolean)
        fun onSendUserError(throwable: Throwable)
    }

    interface OnSendUserComproPagoListener {
        fun onSendUserComproPagoLoaded(success: Boolean)
        fun onSendUserComproPagoError(throwable: Throwable)
    }

    interface OnRemoveComproPagoNodeListener {
        fun onRemoveComproPagoNodeLoaded(success: Boolean)
        fun onRemoveComproPagoNodeError(throwable: Throwable)
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

    interface OnSendAnsweredQuestionsNewFormatListener {
        fun onSendAnsweredQuestionsNewFormatLoaded(success: Boolean)
        fun onSendAnsweredQuestionsNewFormatError(throwable: Throwable)
    }

    interface OnSendAnsweredQuestionNewFormatListener {
        fun onSendAnsweredQuestionNewFormatLoaded(success: Boolean)
        fun onSendAnsweredQuestionNewFormatError(throwable: Throwable)
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


    /*
    ********************************** FIREBASE REQUEST REFACTOR ***********************************
     */



    fun requestGetFreeModulesRefactor(course: String, onRequestFreeModulesRefactorListener: OnGetFreeModulesRefactorListener) {
        val questionModuleRequest = QuestionModuleRequest(mActivity)

        questionModuleRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onRequestFreeModulesRefactorListener.onGetFreeModulesRefactorLoaded(result as List<Module>)
            }
        })

        questionModuleRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onRequestFreeModulesRefactorListener.onGetFreeModulesRefactorError(result)
            }
        })

        questionModuleRequest.requestGetFreeModulesRefactor(course)
    }

    fun requestGetModulesRefactor(course: String, onGetModulesRefactorListener: OnGetModulesRefactorListener) {
        val questionModuleRequest = QuestionModuleRequest(mActivity)

        questionModuleRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetModulesRefactorListener.onGetModulesRefactorLoaded(result as List<Module>)
            }
        })

        questionModuleRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetModulesRefactorListener.onGetModulesRefactorError(result)
            }
        })

        questionModuleRequest.requestGetModulesRefactor(course)
    }

    fun requestGetAnsweredModulesAndProfileRefactor(course: String, onGetAnsweredModulesAndProfileRefactorListener: OnGetAnsweredModulesAndProfileRefactorListener) {
        val questionModuleRequest = QuestionModuleRequest(mActivity)

        questionModuleRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetAnsweredModulesAndProfileRefactorListener.onGetAnsweredModulesAndProfileRefactorLoaded(result as User)
            }
        })

        questionModuleRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetAnsweredModulesAndProfileRefactorListener.onGetAnsweredModulesAndProfileRefactorError(result)
            }
        })

        questionModuleRequest.requestGetProfileUserRefactor(course)
    }

    /*
     * Listeners question module fragment
     */
    interface OnGetFreeModulesRefactorListener {
        fun onGetFreeModulesRefactorLoaded(freeModules : List<Module>)
        fun onGetFreeModulesRefactorError(throwable: Throwable)
    }

    interface OnGetModulesRefactorListener {
        fun onGetModulesRefactorLoaded(modules : List<Module>)
        fun onGetModulesRefactorError(throwable: Throwable)
    }

    interface OnGetAnsweredModulesAndProfileRefactorListener {
        fun onGetAnsweredModulesAndProfileRefactorLoaded(user : User)
        fun onGetAnsweredModulesAndProfileRefactorError(throwable: Throwable)
    }


    fun requestGetWrongQuestionsAndProfileRefactor(course: String, onGetWrongQuestionAndProfileListener: OnGetWrongQuestionAndProfileListener) {
        val wrongQuestionRequest = WrongQuestionRequest(mActivity)

        wrongQuestionRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetWrongQuestionAndProfileListener.onGetWrongQuestionsAndProfileLoaded(result as User)
            }
        })

        wrongQuestionRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetWrongQuestionAndProfileListener.onGetWrongQuestionsAndProfileError(result)
            }
        })

        wrongQuestionRequest.requestGetWrontQuestionsRefactor(course)
    }


    /*
     * Listener wrong questions fragment
     */
    interface OnGetWrongQuestionAndProfileListener {
        fun onGetWrongQuestionsAndProfileLoaded(user : User)
        fun onGetWrongQuestionsAndProfileError(throwable: Throwable)
    }




    fun requestGetFreeExamsRefactor(course : String, onRequestFreeExamsRefactorListener: OnGetFreeExamsRefactorListener) {
        val questionModuleRequest = ExamsRequest(mActivity)

        questionModuleRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onRequestFreeExamsRefactorListener.onGetFreeExamsRefactorLoaded(result as List<Exam>)
            }
        })

        questionModuleRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onRequestFreeExamsRefactorListener.onGetFreeExamsRefactorError(result)
            }
        })

        questionModuleRequest.requestGetFreeExamsRefactor(course)
    }

    fun requestGetExamsRefactor(course: String, onGetExamsRefactorListener: OnGetExamsRefactorListener) {
        val questionExamsRequest = ExamsRequest(mActivity)

        questionExamsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetExamsRefactorListener.onGetExamsRefactorLoaded(result as List<Exam>)
            }
        })

        questionExamsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetExamsRefactorListener.onGetExamsRefactorError(result)
            }
        })

        questionExamsRequest.requestGetExamsRefactor(course)
    }

    fun requestGetAnsweredExamsAndProfileRefactor(onGetAnsweredExamsAndProfileRefactorListener: OnGetAnsweredExamsAndProfileRefactorListener) {
        val examsRequest = ExamsRequest(mActivity)

        examsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetAnsweredExamsAndProfileRefactorListener.onGetAnsweredExamsAndProfileRefactorLoaded(result as User)
            }
        })

        examsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetAnsweredExamsAndProfileRefactorListener.onGetAnsweredExamsAndProfileRefactorError(result)
            }
        })

        examsRequest.requestGetProfileUserRefactor()
    }

    /*
     * Listeners exams fragment
     */
    interface OnGetFreeExamsRefactorListener {
        fun onGetFreeExamsRefactorLoaded(freeExams : List<Exam>)
        fun onGetFreeExamsRefactorError(throwable: Throwable)
    }

    interface OnGetExamsRefactorListener {
        fun onGetExamsRefactorLoaded(exams : List<Exam>)
        fun onGetExamsRefactorError(throwable: Throwable)
    }

    interface OnGetAnsweredExamsAndProfileRefactorListener {
        fun onGetAnsweredExamsAndProfileRefactorLoaded(user : User)
        fun onGetAnsweredExamsAndProfileRefactorError(throwable: Throwable)
    }


    fun requestGetProfileRefactor(onGetProfileRefactorListener: OnGetProfileRefactorListener) {
        val profileRequest = ProfileRequest(mActivity)

        profileRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetProfileRefactorListener.onGetProfileRefactorLoaded(result as User)
            }
        })

        profileRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetProfileRefactorListener.onGetProfileRefactorError(result)
            }
        })

        profileRequest.requestGetProfileRefactor()
    }

    fun requestGetUserSchools(schools: List<School>, course: String ,onGetUserSchoolsListener: OnGetUserSchoolsListener) {
        val profileRequest = ProfileRequest(mActivity)

        profileRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetUserSchoolsListener.onGetUserSchoolsLoaded(result as List<School>)
            }
        })

        profileRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetUserSchoolsListener.onGetUserSchoolsError(result)
            }
        })

        profileRequest.requestGetUserSchools(schools, course)
    }

    interface OnGetProfileRefactorListener {
        fun onGetProfileRefactorLoaded(user: User)
        fun onGetProfileRefactorError(throwable: Throwable)
    }

    interface OnGetUserSchoolsListener {
        fun onGetUserSchoolsLoaded(schools: List<School>)
        fun onGetUserSchoolsError(throwable: Throwable)
    }


    fun requestGetHitAndMissesAnsweredQuestionsAndExams(course: String, onGetHitsAndMissesAnsweredModulesAndExamsListener: OnGetHitsAndMissesAnsweredModulesAndExamsListener) {
        val advancesRequest = AdvancesRequest(mActivity)

        advancesRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetHitsAndMissesAnsweredModulesAndExamsListener.onGetHitsAndMissesAnsweredModulesAndExamsLoaded(result as User)
            }
        })

        advancesRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetHitsAndMissesAnsweredModulesAndExamsListener.onGetHitsAndMissesAnsweredModulesAndExamsError(result)
            }
        })

        advancesRequest.requestGetHitAndMissesAnsweredModulesAndExams(course)
    }

    fun requestGetAverageSubjects(course : String, onGetAverageSubjectsListener: OnGetAverageSubjectsListener) {
        val advancesRequest = AdvancesRequest(mActivity)

        advancesRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetAverageSubjectsListener.onGetAverageSubjectsLoaded(result as List<Subject>)
            }
        })

        advancesRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetAverageSubjectsListener.onGetAverageSubjectsError(result)
            }
        })

        advancesRequest.requestGetAverageSubjects(course)
    }

    interface OnGetHitsAndMissesAnsweredModulesAndExamsListener {
        fun onGetHitsAndMissesAnsweredModulesAndExamsLoaded(user : User)
        fun onGetHitsAndMissesAnsweredModulesAndExamsError(throwable: Throwable)
    }

    interface OnGetAverageSubjectsListener {
        fun onGetAverageSubjectsLoaded(subjects : List<Subject>)
        fun onGetAverageSubjectsError(throwable: Throwable)
    }


    fun requestGetExamScoreRefactor(course: String, onGetExamScoreRefactorListener : OnGetExamScoreRefactorListener) {
        val examScoreRequest = ExamsScoreRequest(mActivity)

        examScoreRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetExamScoreRefactorListener.onGetExamScoreRefactorLoaded(result as List<ExamScoreRafactor>)
            }
        })

        examScoreRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetExamScoreRefactorListener.onGetExamScoreRefactorError(result)
            }
        })

        examScoreRequest.requestGetExamScores(course)
    }

    fun requestAnsweredExamsRefactor(course: String, onGetAnsweredExamsRefactorListener : OnGetAnsweredExamsRefactorListener) {
        val examScoreRequest = ExamsScoreRequest(mActivity)

        examScoreRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetAnsweredExamsRefactorListener.onGetAnsweredExamsRefactorLoaded(result as User)
            }
        })

        examScoreRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetAnsweredExamsRefactorListener.onGetAnsweredExamsRefactorError(result)
            }
        })

        examScoreRequest.requestGetAnsweredExamRefactor(course)
    }

    interface OnGetExamScoreRefactorListener {
        fun onGetExamScoreRefactorLoaded(examScores : List<ExamScoreRafactor>)
        fun onGetExamScoreRefactorError(throwable: Throwable)
    }

    interface OnGetAnsweredExamsRefactorListener {
        fun onGetAnsweredExamsRefactorLoaded(user: User)
        fun onGetAnsweredExamsRefactorError(throwable: Throwable)
    }


    /*
     * REQUEST QUESTIONS OLD FORMAT
     */
    /*
    fun requestGetQuestionsByModuleIdRefactor(moduleId : Int,
                                              onGetQuestionsByModuleIdRefactorListener : OnGetQuestionsByModuleIdRefactorListener) {
        val questionsRequest = QuestionsRequest(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetQuestionsByModuleIdRefactorListener
                        .onGetQuestionsByModuleIdRefactorLoaded(result as List<Question>)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetQuestionsByModuleIdRefactorListener
                        .onGetQuestionsByModuleIdRefactorError(result)
            }
        })

        questionsRequest.requestGetQuestionsByModuleId(moduleId)
    }

    fun requestGetQuestionsByExamIdRefactor(examId : Int,
                                            onGetQuestionsByExamIdRefactorListener : OnGetQuestionsByExamIdRefactorListener) {
        val questionsRequest = QuestionsRequest(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetQuestionsByExamIdRefactorListener
                        .onGetQuestionsByExamIdRefactorLoaded(result as List<Question>)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetQuestionsByExamIdRefactorListener
                        .onGetQuestionsByExamIdRefactorError(result)
            }
        })

        questionsRequest.requestGetQuestionsByExamId(examId)
    }

    fun requestGetWrongQuestionsByQuestionIdRefactor(wrongQuestions : List<Question>,
                                                     onGetWrongQuestionsByQuestionIdRefactorListener : OnGetWrongQuestionsByQuestionIdRefactorListener) {
        val questionsRequest = QuestionsRequest(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetWrongQuestionsByQuestionIdRefactorListener
                        .onGetWrongQuestionsByQuestionIdRefactorLoaded(result as List<Question>)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetWrongQuestionsByQuestionIdRefactorListener
                        .onGetWrongQuestionsByQuestionIdRefactorError(result)
            }
        })

        questionsRequest.requestGetWrongQuestionsByQuestionId(wrongQuestions)
    }

    interface OnGetQuestionsByModuleIdRefactorListener {
        fun onGetQuestionsByModuleIdRefactorLoaded(questions : List<Question>)
        fun onGetQuestionsByModuleIdRefactorError(throwable: Throwable)
    }

    interface OnGetQuestionsByExamIdRefactorListener {
        fun onGetQuestionsByExamIdRefactorLoaded(questions : List<Question>)
        fun onGetQuestionsByExamIdRefactorError(throwable: Throwable)
    }

    interface OnGetWrongQuestionsByQuestionIdRefactorListener {
        fun onGetWrongQuestionsByQuestionIdRefactorLoaded(questions : List<Question>)
        fun onGetWrongQuestionsByQuestionIdRefactorError(throwable: Throwable)
    }*/


    /*
     * REQUEST QUESTION NEW FORMAT
     */

    fun requestGetQuestionsNewFormatByModuleIdRefactor(moduleId : Int, course: String,
                                                       onGetQuestionsNewFormatByModuleIdRefactorListener : OnGetQuestionsNewFormatByModuleIdRefactorListener) {
        val questionsRequest = QuestionNewFormatRequest(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetQuestionsNewFormatByModuleIdRefactorListener
                        .onGetQuestionsNewFormatByModuleIdRefactorLoaded(result as List<QuestionNewFormat>)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetQuestionsNewFormatByModuleIdRefactorListener
                        .onGetQuestionsNewFormatByModuleIdRefactorError(result)
            }
        })

        questionsRequest.requestGetQuestionsNewFormatByModuleId(moduleId, course)
    }

    fun requestGetQuestionsNewFormatByExamIdRefactor(examId : Int, course: String,
                                                     onGetQuestionsNewFormatByExamIdRefactorListener : OnGetQuestionsNewFormatByExamIdRefactorListener) {
        val questionsRequest = QuestionNewFormatRequest(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetQuestionsNewFormatByExamIdRefactorListener
                        .onGetQuestionsNewFormatByExamIdRefactorLoaded(result as List<QuestionNewFormat>)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetQuestionsNewFormatByExamIdRefactorListener
                        .onGetQuestionsNewFormatByExamIdRefactorError(result)
            }
        })

        questionsRequest.requestGetQuestionsNewFormatByExamId(examId, course)
    }

    fun requestGetWrongQuestionsNewFormatByQuestionIdRefactor(wrongQuestionsNewFormat : List<QuestionNewFormat>, course: String,
                                                              onGetWrongQuestionsNewFormatByQuestionIdRefactorListener : OnGetWrongQuestionsNewFormatByQuestionIdRefactorListener) {
        val questionsRequest = QuestionNewFormatRequest(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetWrongQuestionsNewFormatByQuestionIdRefactorListener
                        .onGetWrongQuestionsNewFormatByQuestionIdRefactorLoaded(result as List<QuestionNewFormat>)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetWrongQuestionsNewFormatByQuestionIdRefactorListener
                        .onGetWrongQuestionsNewFormatByQuestionIdRefactorError(result)
            }
        })

        questionsRequest.requestGetWrongQuestionsNewFormatByQuestionId(wrongQuestionsNewFormat, course)
    }

    fun requestGetQuestionsNewFormatBySubject(subject : String, course: String,
                                                              onGetQuestionsNewFormatBySubjectListener: OnGetQuestionsNewFormatBySubjectListener) {
        val questionsRequest = QuestionNewFormatRequest(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetQuestionsNewFormatBySubjectListener
                        .onGetQuestionsNewFormatBySubjectLoaded(result as List<QuestionNewFormat>)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetQuestionsNewFormatBySubjectListener
                        .onGetQuestionsNewFormatBySubjectError(result)
            }
        })

        questionsRequest.requestGetQuestionNewFormatBySubject(subject, course)
    }

    interface OnGetQuestionsNewFormatByModuleIdRefactorListener {
        fun onGetQuestionsNewFormatByModuleIdRefactorLoaded(questions : List<QuestionNewFormat>)
        fun onGetQuestionsNewFormatByModuleIdRefactorError(throwable: Throwable)
    }

    interface OnGetQuestionsNewFormatByExamIdRefactorListener {
        fun onGetQuestionsNewFormatByExamIdRefactorLoaded(questions : List<QuestionNewFormat>)
        fun onGetQuestionsNewFormatByExamIdRefactorError(throwable: Throwable)
    }

    interface OnGetWrongQuestionsNewFormatByQuestionIdRefactorListener {
        fun onGetWrongQuestionsNewFormatByQuestionIdRefactorLoaded(questions : List<QuestionNewFormat>)
        fun onGetWrongQuestionsNewFormatByQuestionIdRefactorError(throwable: Throwable)
    }

    interface OnGetQuestionsNewFormatBySubjectListener {
        fun onGetQuestionsNewFormatBySubjectLoaded(questions : List<QuestionNewFormat>)
        fun onGetQuestionsNewFormatBySubjectError(throwable: Throwable)
    }

    fun requestGetUserSelectedSchoolsRefactor(onGetUserSelectedSchoolsRefactorListener : OnGetUserSelectedSchoolsRefactorListener) {
        val schoolsAverageRequest = SchoolsAverageRequest(mActivity)

        schoolsAverageRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetUserSelectedSchoolsRefactorListener.onGetUserSelectedSchoolsRefactorLoaded(result as List<School>)
            }
        })

        schoolsAverageRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetUserSelectedSchoolsRefactorListener.onGetUserSelectedSchoolsRefactorError(result)
            }
        })

        schoolsAverageRequest.requestGetUserSelectedSchoolsRefactor()
    }

    fun requestGetScoreLast128QuestionsExam(onGetScoreLast128QuestionsExamListener : OnGetScoreLast128QuestionsExamListener) {
        val schoolsAverageRequest = SchoolsAverageRequest(mActivity)

        schoolsAverageRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetScoreLast128QuestionsExamListener.onGetScoreLast128QuestionsExamLoaded(result as Int)
            }
        })

        schoolsAverageRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetScoreLast128QuestionsExamListener.onGetScoreLast128QuestionsExamError(result)
            }
        })

        schoolsAverageRequest.requestGetScoreLast128QuestionsExam()
    }

    fun requestGetCourseExamMaxScore(course: String, onGetCourseExamMaxScore: OnGetCourseExamMaxScore) {
        val schoolsAverageRequest = SchoolsAverageRequest(mActivity)

        schoolsAverageRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetCourseExamMaxScore.onGetCourseExamMaxScoreLoaded(result as String)
            }
        })

        schoolsAverageRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetCourseExamMaxScore.onGetCourseExamMaxScoreError(result)
            }
        })

        schoolsAverageRequest.requestGetCourseExamMaxScore(course)
    }

    interface OnGetUserSelectedSchoolsRefactorListener {
        fun onGetUserSelectedSchoolsRefactorLoaded(schools: List<School>)
        fun onGetUserSelectedSchoolsRefactorError(throwable: Throwable)
    }

    interface OnGetScoreLast128QuestionsExamListener {
        fun onGetScoreLast128QuestionsExamLoaded(score: Int)
        fun onGetScoreLast128QuestionsExamError(throwable: Throwable)
    }

    interface  OnGetCourseExamMaxScore {
        fun onGetCourseExamMaxScoreLoaded(score: String)
        fun onGetCourseExamMaxScoreError(throwable: Throwable)
    }



    fun requestGetSchools(course: String, onGetSchoolsListener : OnGetSchoolsListener) {
        val schoolsRequest = SchoolsRequest(mActivity)

        schoolsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetSchoolsListener.onGetSchoolsLoaded(result as List<Institute>)
            }
        })

        schoolsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetSchoolsListener.onGetSchoolsError(result)
            }
        })

        schoolsRequest.requestGetSchools(course)
    }

    interface OnGetSchoolsListener {
        fun onGetSchoolsLoaded(institutes : List<Institute>)
        fun onGetSchoolsError(throwable: Throwable)
    }



    fun requestGetUserTips(OnGetUserTipsListener : OnGetUserTipsListener) {
        val tipsRequest = TipsRequest(mActivity)

        tipsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                OnGetUserTipsListener.onGetUserTipsLoaded(result as User)
            }
        })

        tipsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                OnGetUserTipsListener.onGetUserTipsError(result)
            }
        })

        tipsRequest.requestGetUserTips()
    }

    fun requestGetTips(course: String, OnGetTipsListener : OnGetTipsListener) {
        val tipsRequest = TipsRequest(mActivity)

        tipsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                OnGetTipsListener.onGetTipsLoaded(result as List<String>)
            }
        })

        tipsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                OnGetTipsListener.onGetTipsError(result)
            }
        })

        tipsRequest.requestGetTips(course)
    }

    interface OnGetUserTipsListener {
        fun onGetUserTipsLoaded(user: User)
        fun onGetUserTipsError(throwable: Throwable)
    }

    interface OnGetTipsListener {
        fun onGetTipsLoaded(tips : List<String>)
        fun onGetTipsError(throwable: Throwable)
    }

    fun requestGetUserWithProvider(onGetUserWithProviderListener : OnGetUserWithProviderListener) {
        val checkUserWithProviderRequest = CheckUserWithProviderRequest(mActivity)

        checkUserWithProviderRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetUserWithProviderListener.onGetUserWithProviderLoaded(result as User)
            }
        })

        checkUserWithProviderRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetUserWithProviderListener.onGetUserWithProviderError(result)
            }
        })

        checkUserWithProviderRequest.requestGetUserWithProvider()
    }

    interface OnGetUserWithProviderListener {
        fun onGetUserWithProviderLoaded(user: User)
        fun onGetUserWithProviderError(throwable: Throwable)
    }

    fun requestGetCoursesrefactor(onGetCourseRefactorListener: OnGetCourseRefactorListener) {
        val courseRequest = CourseRequest(mActivity)

        courseRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetCourseRefactorListener.onGetCoursesRefactorLoaded(result as List<Course>)
            }
        })

        courseRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetCourseRefactorListener.onGetCoursesRefactorError(result)
            }
        })

        courseRequest.requestGetCourses()
    }

    fun requestGetCoursePrice(course: String, onGetCoursePriceListener: OnGetCoursePriceListener) {
        val courseRequest = CourseRequest(mActivity)

        courseRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetCoursePriceListener.onGetCoursePriceLoaded(result as String)
            }
        })

        courseRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetCoursePriceListener.onGetCoursePriceError(result)
            }
        })

        courseRequest.requestGetCoursePrice(course)
    }


    interface OnGetCourseRefactorListener {
        fun onGetCoursesRefactorLoaded(courses: List<Course>)
        fun onGetCoursesRefactorError(throwable: Throwable)
    }

    interface OnGetCoursePriceListener {
        fun onGetCoursePriceLoaded(coursePrice : String)
        fun onGetCoursePriceError(throwable: Throwable)
    }

    fun requestGetSubjects(course: String, onGetSubjectsListener: OnGetSubjectsListener) {
        val courseRequest = SubjectRequest(mActivity)

        courseRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetSubjectsListener.onGetSubjectsLoaded(result as List<SubjectRefactor>)
            }
        })

        courseRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetSubjectsListener.onGetSubjectsError(result)
            }
        })

        courseRequest.requestGetSubjects(course)
    }

    interface OnGetSubjectsListener {
        fun onGetSubjectsLoaded(subjects: List<SubjectRefactor>)
        fun onGetSubjectsError(throwable: Throwable)
    }

    fun requestGetFreeSubjectsQuestionsRefactor(course: String, onGetFreeSubjectsQuestionsListener: OnGetFreeSubjectsQuestionsListener) {
        val courseRequest = SubjectQuestionRequest(mActivity)

        courseRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetFreeSubjectsQuestionsListener.onGetFreeSubjectsQuestionsLoaded(result as Long)
            }
        })

        courseRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetFreeSubjectsQuestionsListener.onGetFreeSubjectsQuestionsError(result)
            }
        })

        courseRequest.requestGetFreeSubjectsQuestionsRefactor(course)
    }

    interface OnGetFreeSubjectsQuestionsListener {
        fun onGetFreeSubjectsQuestionsLoaded(numberOfFreeQuestionSubjects: Long)
        fun onGetFreeSubjectsQuestionsError(throwable: Throwable)
    }

    fun requestGetQuestionsNewFormatBySubjectQuestionId(subjectQuestionsNewFormat: List<QuestionNewFormat>, course: String, onGetSubjectQuestionsNewFormatBySubjectQuestionIdListener: OnGetSubjectQuestionsNewFormatBySubjectQuestionIdListener) {
        val questionsRequest = QuestionNewFormatRequest(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetSubjectQuestionsNewFormatBySubjectQuestionIdListener
                        .OnGetSubjectQuestionsNewFormatBySubjectQuestionIdLoaded(result as List<QuestionNewFormat>)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetSubjectQuestionsNewFormatBySubjectQuestionIdListener
                        .OnGetSubjectQuestionsNewFormatBySubjectQuestionIdError(result)
            }
        })

        questionsRequest.requestGetSubjectQuestionsNewFormatBySubjectQuestionId(subjectQuestionsNewFormat, course)
    }

    interface OnGetSubjectQuestionsNewFormatBySubjectQuestionIdListener {
        fun OnGetSubjectQuestionsNewFormatBySubjectQuestionIdLoaded(questions: List<QuestionNewFormat>)
        fun OnGetSubjectQuestionsNewFormatBySubjectQuestionIdError(throwable: Throwable)
    }

    fun requestGetQuestionNewFormat(questionId: String, course: String,
                                 onGetQuestionNewFormatListener: OnGetQuestionNewFormatListener) {
        val questionsRequest = QuestionNewFormatRequest(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetQuestionNewFormatListener
                        .onGetQuestionNewFormatLoaded(result as QuestionNewFormat)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetQuestionNewFormatListener
                        .onGetQuestionNewFormatError(result)
            }
        })

        questionsRequest.requestQuestionNewFormat(questionId, course)
    }

    interface OnGetQuestionNewFormatListener {
        fun onGetQuestionNewFormatLoaded(question: QuestionNewFormat)
        fun onGetQuestionNewFormatError(throwable: Throwable)
    }


    fun requestGetQuestionsIdByModuleId(moduleId : Int, course: String, onGetQuestionsIdListener: OnGetQuestionsIdListener) {
        val questionsRequest = QuestionNewFormatRequest(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetQuestionsIdListener
                        .onGetQuestionsIdLoaded(result as List<String>)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetQuestionsIdListener
                        .onGetQuestionsIdError(result)
            }
        })

        questionsRequest.requestGetQuestionsNewFormatByModuleId(moduleId, course)
    }

    fun requestGetQuestionsIdByExamId(examId: Int, course: String, onGetQuestionsIdListener: OnGetQuestionsIdListener) {
        val questionsRequest = QuestionNewFormatRequest(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetQuestionsIdListener
                        .onGetQuestionsIdLoaded(result as List<String>)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetQuestionsIdListener
                        .onGetQuestionsIdError(result)
            }
        })

        questionsRequest.requestGetQuestionsNewFormatByExamId(examId, course)
    }

    interface OnGetQuestionsIdListener {
        fun onGetQuestionsIdLoaded(questionsId: List<String>)
        fun onGetQuestionsIdError(throwable: Throwable)
    }

    fun requestGetMinimumVersion(onGetMinimumVersionListener: OnGetMinimumVersionListener) {
        val questionsRequest = Firebase(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetMinimumVersionListener
                        .onGetMinimumVersionLoaded(result as String)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetMinimumVersionListener
                        .onGetMinimumVersionError(result)
            }
        })

        questionsRequest.requestGetMinimumVersion()

    }

    interface OnGetMinimumVersionListener {
        fun onGetMinimumVersionLoaded(minimumVersion: String)
        fun onGetMinimumVersionError(throwable: Throwable)
    }

}