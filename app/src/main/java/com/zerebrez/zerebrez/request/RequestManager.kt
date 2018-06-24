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
import com.zerebrez.zerebrez.services.firebase.CheckUserWithFacebookRequest
import com.zerebrez.zerebrez.services.firebase.Firebase
import com.zerebrez.zerebrez.services.firebase.advances.AdvancesRequest
import com.zerebrez.zerebrez.services.firebase.practice.ExamsRequest
import com.zerebrez.zerebrez.services.firebase.practice.QuestionModuleRequest
import com.zerebrez.zerebrez.services.firebase.practice.WrongQuestionRequest
import com.zerebrez.zerebrez.services.firebase.profile.ProfileRequest
import com.zerebrez.zerebrez.services.firebase.profile.SchoolsRequest
import com.zerebrez.zerebrez.services.firebase.question.QuestionsRequest
import com.zerebrez.zerebrez.services.firebase.question.TipsRequest
import com.zerebrez.zerebrez.services.firebase.score.ExamsScoreRequest
import com.zerebrez.zerebrez.services.firebase.score.SchoolsAverageRequest

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

    fun requestSendAnsweredQuestions(questions: List<Question>, onSendAnsweredQuestionsListener: OnSendAnsweredQuestionsListener) {
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

        firebase.requestSendAnsweredQuestions(questions)
    }

    fun requestSendAnsweredModules(module : Module, onSendAnsweredModulesListener: OnSendAnsweredModulesListener) {
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

        firebase.requestSendAnsweredModules(module)
    }

    fun requestSendAnsweredExams(exam: Exam, onSendAnsweredExamsListener: OnSendAnsweredExamsListener) {
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

        firebase.requestSendAnsweredExams(exam)
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

    interface OnUpdateUserPasswordListener {
        fun onUpdateUserPasswordLoaded(success: Boolean)
        fun onUpdateUserPasswordError(throwable: Throwable)
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


    /*
    ********************************** FIREBASE REQUEST REFACTOR ***********************************
     */



    fun requestGetFreeModulesRefactor(onRequestFreeModulesRefactorListener: OnGetFreeModulesRefactorListener) {
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

        questionModuleRequest.requestGetFreeModulesRefactor()
    }

    fun requestGetModulesRefactor(onGetModulesRefactorListener: OnGetModulesRefactorListener) {
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

        questionModuleRequest.requestGetModulesRefactor()
    }

    fun requestGetAnsweredModulesAndProfileRefactor(onGetAnsweredModulesAndProfileRefactorListener: OnGetAnsweredModulesAndProfileRefactorListener) {
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

        questionModuleRequest.requestGetProfileUserRefactor()
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


    fun requestGetWrongQuestionsAndProfileRefactor(onGetWrongQuestionAndProfileListener: OnGetWrongQuestionAndProfileListener) {
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

        wrongQuestionRequest.requestGetWrontQuestionsRefactor()
    }


    /*
     * Listener wrong questions fragment
     */
    interface OnGetWrongQuestionAndProfileListener {
        fun onGetWrongQuestionsAndProfileLoaded(user : User)
        fun onGetWrongQuestionsAndProfileError(throwable: Throwable)
    }




    fun requestGetFreeExamsRefactor(onRequestFreeExamsRefactorListener: OnGetFreeExamsRefactorListener) {
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

        questionModuleRequest.requestGetFreeExamsRefactor()
    }

    fun requestGetExamsRefactor(onGetExamsRefactorListener: OnGetExamsRefactorListener) {
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

        questionExamsRequest.requestGetExamsRefactor()
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

    fun requestGetUserSchools(schools: List<School> ,onGetUserSchoolsListener: OnGetUserSchoolsListener) {
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

        profileRequest.requestGetUserSchools(schools)
    }

    interface OnGetProfileRefactorListener {
        fun onGetProfileRefactorLoaded(user: User)
        fun onGetProfileRefactorError(throwable: Throwable)
    }

    interface OnGetUserSchoolsListener {
        fun onGetUserSchoolsLoaded(schools: List<School>)
        fun onGetUserSchoolsError(throwable: Throwable)
    }


    fun requestGetHitAndMissesAnsweredQuestionsAndExams(onGetHitsAndMissesAnsweredModulesAndExamsListener: OnGetHitsAndMissesAnsweredModulesAndExamsListener) {
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

        advancesRequest.requestGetHitAndMissesAnsweredModulesAndExams()
    }

    fun requestGetAverageSubjects(onGetAverageSubjectsListener: OnGetAverageSubjectsListener) {
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

        advancesRequest.requestGetAverageSubjects()
    }

    interface OnGetHitsAndMissesAnsweredModulesAndExamsListener {
        fun onGetHitsAndMissesAnsweredModulesAndExamsLoaded(user : User)
        fun onGetHitsAndMissesAnsweredModulesAndExamsError(throwable: Throwable)
    }

    interface OnGetAverageSubjectsListener {
        fun onGetAverageSubjectsLoaded(subjects : List<Subject>)
        fun onGetAverageSubjectsError(throwable: Throwable)
    }


    fun requestGetExamScoreRefactor(onGetExamScoreRefactorListener : OnGetExamScoreRefactorListener) {
        val examScoreRequest = ExamsScoreRequest(mActivity)

        examScoreRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetExamScoreRefactorListener.onGetExamScoreRefactorLoaded(result as List<ExamScore>)
            }
        })

        examScoreRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetExamScoreRefactorListener.onGetExamScoreRefactorError(result)
            }
        })

        examScoreRequest.requestGetExamScores()
    }

    fun requestAnsweredExamsRefactor(onGetAnsweredExamsRefactorListener : OnGetAnsweredExamsRefactorListener) {
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

        examScoreRequest.requestGetAnsweredExamRefactor()
    }

    interface OnGetExamScoreRefactorListener {
        fun onGetExamScoreRefactorLoaded(examScores : List<ExamScore>)
        fun onGetExamScoreRefactorError(throwable: Throwable)
    }

    interface OnGetAnsweredExamsRefactorListener {
        fun onGetAnsweredExamsRefactorLoaded(user: User)
        fun onGetAnsweredExamsRefactorError(throwable: Throwable)
    }


    fun requestGetQuestionsByModuleIdRefactor(moduleId : Int, onGetQuestionsByModuleIdRefactorListener : OnGetQuestionsByModuleIdRefactorListener) {
        val questionsRequest = QuestionsRequest(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetQuestionsByModuleIdRefactorListener.onGetQuestionsByModuleIdRefactorLoaded(result as List<Question>)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetQuestionsByModuleIdRefactorListener.onGetQuestionsByModuleIdRefactorError(result)
            }
        })

        questionsRequest.requestGetQuestionsByModuleId(moduleId)
    }

    fun requestGetQuestionsByExamIdRefactor(examId : Int, onGetQuestionsByExamIdRefactorListener : OnGetQuestionsByExamIdRefactorListener) {
        val questionsRequest = QuestionsRequest(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetQuestionsByExamIdRefactorListener.onGetQuestionsByExamIdRefactorLoaded(result as List<Question>)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetQuestionsByExamIdRefactorListener.onGetQuestionsByExamIdRefactorError(result)
            }
        })

        questionsRequest.requestGetQuestionsByExamId(examId)
    }

    fun requestGetWrongQuestionsByQuestionIdRefactor(wrongQuestions : List<Question>, onGetWrongQuestionsByQuestionIdRefactorListener : OnGetWrongQuestionsByQuestionIdRefactorListener) {
        val questionsRequest = QuestionsRequest(mActivity)

        questionsRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetWrongQuestionsByQuestionIdRefactorListener.onGetWrongQuestionsByQuestionIdRefactorLoaded(result as List<Question>)
            }
        })

        questionsRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetWrongQuestionsByQuestionIdRefactorListener.onGetWrongQuestionsByQuestionIdRefactorError(result)
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

    interface OnGetUserSelectedSchoolsRefactorListener {
        fun onGetUserSelectedSchoolsRefactorLoaded(schools: List<School>)
        fun onGetUserSelectedSchoolsRefactorError(throwable: Throwable)
    }

    interface OnGetScoreLast128QuestionsExamListener {
        fun onGetScoreLast128QuestionsExamLoaded(score: Int)
        fun onGetScoreLast128QuestionsExamError(throwable: Throwable)
    }

    fun requestGetSchools(onGetSchoolsListener : OnGetSchoolsListener) {
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

        schoolsRequest.requestGetSchools()
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

    fun requestGetTips(OnGetTipsListener : OnGetTipsListener) {
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

        tipsRequest.requestGetTips()
    }

    interface OnGetUserTipsListener {
        fun onGetUserTipsLoaded(user: User)
        fun onGetUserTipsError(throwable: Throwable)
    }

    interface OnGetTipsListener {
        fun onGetTipsLoaded(tips : List<String>)
        fun onGetTipsError(throwable: Throwable)
    }

    fun requestGetUserWithFacebook(onGetUserWithFacebookListener : OnGetUserWithFacebookListener) {
        val checkUserWithFacebookRequest = CheckUserWithFacebookRequest(mActivity)

        checkUserWithFacebookRequest.setOnRequestSuccess(object : AbstractPendingRequest.OnRequestListenerSuccess{
            override fun onSuccess(result: Any?) {
                onGetUserWithFacebookListener.onGetUserWithFacebookLoaded(result as User)
            }
        })

        checkUserWithFacebookRequest.setOnRequestFailed(object : AbstractPendingRequest.OnRequestListenerFailed{
            override fun onFailed(result: Throwable) {
                onGetUserWithFacebookListener.onGetUserWithFacebookError(result)
            }
        })

        checkUserWithFacebookRequest.requestGetUserWithFacebook()
    }

    interface OnGetUserWithFacebookListener {
        fun onGetUserWithFacebookLoaded(user: User)
        fun onGetUserWithFacebookError(throwable: Throwable)
    }

}