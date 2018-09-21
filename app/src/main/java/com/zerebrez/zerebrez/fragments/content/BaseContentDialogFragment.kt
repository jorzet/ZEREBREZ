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

abstract class BaseContentDialogFragment : BaseDialogFragment() {

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




    /*
    ********************************** FIREBASE REQUEST REFACTOR ***********************************
     */

    fun requestGetFreeModulesRefactor() {
        mRequestManager.requestGetFreeModulesRefactor(object : RequestManager.OnGetFreeModulesRefactorListener {
            override fun onGetFreeModulesRefactorLoaded(freeModules: List<Module>) {
                onGetFreeModulesRefactorSuccess(freeModules)
            }

            override fun onGetFreeModulesRefactorError(throwable: Throwable) {
               onGetFreeModulesRefactorFail(throwable)
            }
        })
    }

    fun requestGetModulesRefactor() {
        mRequestManager.requestGetModulesRefactor(object : RequestManager.OnGetModulesRefactorListener {
            override fun onGetModulesRefactorLoaded(modules: List<Module>) {
                onGetModulesRefactorSuccess(modules)
            }

            override fun onGetModulesRefactorError(throwable: Throwable) {
                onGetModulesRefactorFail(throwable)
            }
        })
    }

    fun requestGetAnsweredModulesAndProfileRefactor(course: String) {
        mRequestManager.requestGetAnsweredModulesAndProfileRefactor(course, object : RequestManager.OnGetAnsweredModulesAndProfileRefactorListener {
            override fun onGetAnsweredModulesAndProfileRefactorLoaded(user: User) {
                onGetAnsweredModulesAndProfileRefactorSuccess(user)
            }

            override fun onGetAnsweredModulesAndProfileRefactorError(throwable: Throwable) {
                onGetAnsweredModulesAndProfileRefactorFail(throwable)
            }
        })
    }

    open fun onGetFreeModulesRefactorSuccess(freeModules : List<Module>) {}
    open fun onGetFreeModulesRefactorFail(throwable: Throwable) {}
    open fun onGetModulesRefactorSuccess(modules : List<Module>) {}
    open fun onGetModulesRefactorFail(throwable: Throwable) {}
    open fun onGetAnsweredModulesAndProfileRefactorSuccess(user : User) {}
    open fun onGetAnsweredModulesAndProfileRefactorFail(throwable: Throwable) {}

    fun requestGetWrongQuestionsAndProfileRefactor(course: String) {
        mRequestManager.requestGetWrongQuestionsAndProfileRefactor(course, object : RequestManager.OnGetWrongQuestionAndProfileListener {
            override fun onGetWrongQuestionsAndProfileLoaded(user: User) {
                onGetWrongQuestionsAndProfileRefactorSuccess(user)
            }

            override fun onGetWrongQuestionsAndProfileError(throwable: Throwable) {
                onGetWrongQuestionsAndProfileRefactorFail(throwable)
            }
        })
    }

    open fun onGetWrongQuestionsAndProfileRefactorSuccess(user : User) {}
    open fun onGetWrongQuestionsAndProfileRefactorFail(throwable: Throwable) {}



    fun requestGetFreeExamsRefactor() {
        mRequestManager.requestGetFreeExamsRefactor(object : RequestManager.OnGetFreeExamsRefactorListener {
            override fun onGetFreeExamsRefactorLoaded(freeExams: List<Exam>) {
                onGetFreeExamsRefactorSuccess(freeExams)
            }
            override fun onGetFreeExamsRefactorError(throwable: Throwable) {
                onGetFreeExamsRefactorFail(throwable)
            }
        })
    }

    fun requestGetExamsRefactor() {
        mRequestManager.requestGetExamsRefactor(object : RequestManager.OnGetExamsRefactorListener {
            override fun onGetExamsRefactorLoaded(exams: List<Exam>) {
                onGetExamsRefactorSuccess(exams)
            }

            override fun onGetExamsRefactorError(throwable: Throwable) {
                onGetExamsRefactorFail(throwable)
            }
        })
    }

    fun requestGetAnsweredExamsAndProfileRefactor() {
        mRequestManager.requestGetAnsweredExamsAndProfileRefactor(object : RequestManager.OnGetAnsweredExamsAndProfileRefactorListener {
            override fun onGetAnsweredExamsAndProfileRefactorLoaded(user: User) {
                onGetAnsweredExamsAndProfileRefactorSuccess(user)
            }

            override fun onGetAnsweredExamsAndProfileRefactorError(throwable: Throwable) {
                onGetAnsweredExamsAndProfileRefactorFail(throwable)
            }
        })
    }

    open fun onGetFreeExamsRefactorSuccess(freeExams : List<Exam>) {}
    open fun onGetFreeExamsRefactorFail(throwable: Throwable) {}
    open fun onGetExamsRefactorSuccess(exams : List<Exam>) {}
    open fun onGetExamsRefactorFail(throwable: Throwable) {}
    open fun onGetAnsweredExamsAndProfileRefactorSuccess(user : User) {}
    open fun onGetAnsweredExamsAndProfileRefactorFail(throwable: Throwable) {}


    fun requestGetProfileRefactor() {
        mRequestManager.requestGetProfileRefactor(object : RequestManager.OnGetProfileRefactorListener {
            override fun onGetProfileRefactorLoaded(user: User) {
                onGetProfileRefactorSuccess(user)
            }

            override fun onGetProfileRefactorError(throwable: Throwable) {
                onGetProfileRefactorFail(throwable)
            }
        })
    }

    fun requestGetUserSchools(schools: List<School>) {
        mRequestManager.requestGetUserSchools(schools, object : RequestManager.OnGetUserSchoolsListener {
            override fun onGetUserSchoolsLoaded(schools: List<School>) {
                onGetUserSchoolsSuccess(schools)
            }

            override fun onGetUserSchoolsError(throwable: Throwable) {
                onGetUserSchoolsFail(throwable)
            }
        })
    }

    open fun onGetProfileRefactorSuccess(user: User) {}
    open fun onGetProfileRefactorFail(throwable: Throwable) {}
    open fun onGetUserSchoolsSuccess(schools : List<School>) {}
    open fun onGetUserSchoolsFail(throwable: Throwable) {}

    fun requestGetHitAndMissesAnsweredModulesAndExams(course: String) {
        mRequestManager.requestGetHitAndMissesAnsweredQuestionsAndExams(course, object : RequestManager.OnGetHitsAndMissesAnsweredModulesAndExamsListener {
            override fun onGetHitsAndMissesAnsweredModulesAndExamsLoaded(user: User) {
                onGetHitAndMissesAnsweredModulesAndExamsSuccess(user)
            }

            override fun onGetHitsAndMissesAnsweredModulesAndExamsError(throwable: Throwable) {
                onGetHitAndMissesAnsweredModulesAndExamsFail(throwable)
            }
        })
    }

    fun requestGetAverageSubjects() {
        mRequestManager.requestGetAverageSubjects(object : RequestManager.OnGetAverageSubjectsListener {
            override fun onGetAverageSubjectsLoaded(subjects: List<Subject>) {
                onGetAverageSubjectsSuccess(subjects)
            }

            override fun onGetAverageSubjectsError(throwable: Throwable) {
                onGetAverageSubjectsFail(throwable)
            }
        })
    }

    open fun onGetHitAndMissesAnsweredModulesAndExamsSuccess(user: User) {}
    open fun onGetHitAndMissesAnsweredModulesAndExamsFail(throwable: Throwable) {}
    open fun onGetAverageSubjectsSuccess(subjects : List<Subject>) {}
    open fun onGetAverageSubjectsFail(throwable: Throwable) {}

    fun requestGetExamScoreRefactor() {
        mRequestManager.requestGetExamScoreRefactor(object : RequestManager.OnGetExamScoreRefactorListener {
            override fun onGetExamScoreRefactorLoaded(examScores: List<ExamScoreRafactor>) {
                onGetExamScoreRefactorSuccess(examScores)
            }

            override fun onGetExamScoreRefactorError(throwable: Throwable) {
                onGetExamScoreRefactorFail(throwable)
            }
        })
    }

    fun requestGetAnsweredExamsRefactor(course: String) {
        mRequestManager.requestAnsweredExamsRefactor(course, object : RequestManager.OnGetAnsweredExamsRefactorListener {
            override fun onGetAnsweredExamsRefactorLoaded(user: User) {
                onGetAnsweredExamsRefactorSuccess(user)
            }

            override fun onGetAnsweredExamsRefactorError(throwable: Throwable) {
                onGetAnsweredExamsRefactorFail(throwable)
            }
        })
    }

    open fun onGetExamScoreRefactorSuccess(examScores: List<ExamScoreRafactor>) {}
    open fun onGetExamScoreRefactorFail(throwable: Throwable) {}
    open fun onGetAnsweredExamsRefactorSuccess(user: User) {}
    open fun onGetAnsweredExamsRefactorFail(throwable: Throwable) {}

    fun requestGetQuestionsByModuleIdRefactor(moduleId : Int) {
        mRequestManager.requestGetQuestionsByModuleIdRefactor(moduleId, object : RequestManager.OnGetQuestionsByModuleIdRefactorListener {
            override fun onGetQuestionsByModuleIdRefactorLoaded(questions: List<Question>) {
                onGetQuestionsByModuleIdRefactorSuccess(questions)
            }

            override fun onGetQuestionsByModuleIdRefactorError(throwable: Throwable) {
                onGetQuestionsByModuleIdRefactorFail(throwable)
            }
        })
    }

    open fun onGetQuestionsByModuleIdRefactorSuccess(questions : List<Question>) {}
    open fun onGetQuestionsByModuleIdRefactorFail(throwable: Throwable) {}

    fun requestGetUserSelectedSchoolsRefactor() {
        mRequestManager.requestGetUserSelectedSchoolsRefactor(object : RequestManager.OnGetUserSelectedSchoolsRefactorListener {
            override fun onGetUserSelectedSchoolsRefactorLoaded(schools: List<School>) {
                onGetUserSelectedSchoolsRefactorSuccess(schools)
            }

            override fun onGetUserSelectedSchoolsRefactorError(throwable: Throwable) {
                onGetUserSelectedSchoolsRefactorFail(throwable)
            }
        })
    }

    fun requestGetScoreLast128QuestionsExam() {
        mRequestManager.requestGetScoreLast128QuestionsExam(object : RequestManager.OnGetScoreLast128QuestionsExamListener {
            override fun onGetScoreLast128QuestionsExamLoaded(score : Int) {
                onGetScoreLast128QuestionsExamSuccess(score)
            }

            override fun onGetScoreLast128QuestionsExamError(throwable: Throwable) {
                onGetScoreLast128QuestionsExamFail(throwable)
            }
        })
    }

    open fun onGetUserSelectedSchoolsRefactorSuccess(schools: List<School>) {}
    open fun onGetUserSelectedSchoolsRefactorFail(throwable: Throwable) {}
    open fun onGetScoreLast128QuestionsExamSuccess(score : Int) {}
    open fun onGetScoreLast128QuestionsExamFail(throwable: Throwable) {}

    fun requestGetUserTips() {
        mRequestManager.requestGetUserTips(object : RequestManager.OnGetUserTipsListener {
            override fun onGetUserTipsLoaded(user: User) {
                onGetUserTipsSuccess(user)
            }

            override fun onGetUserTipsError(throwable: Throwable) {
                onGetUserTipdFail(throwable)
            }
        })
    }

    fun requestGetTips() {
        mRequestManager.requestGetTips(object : RequestManager.OnGetTipsListener {
            override fun onGetTipsLoaded(tips: List<String>) {
                onGetTipsSuccess(tips)
            }

            override fun onGetTipsError(throwable: Throwable) {
                ongetTipsFail(throwable)
            }
        })
    }


    open fun onGetUserTipsSuccess(user: User) {}
    open fun onGetUserTipdFail(throwable: Throwable) {}
    open fun onGetTipsSuccess(tips: List<String>) {}
    open fun ongetTipsFail(throwable: Throwable) {}

    fun requestUpdateUserPassword(user : User) {
        mRequestManager.requestUpdateUserPassword(user, object : RequestManager.OnUpdateUserPasswordListener {
            override fun onUpdateUserPasswordLoaded(success: Boolean) {
                onUpdateUserPasswordSuccess(success)
            }

            override fun onUpdateUserPasswordError(throwable: Throwable) {
                onUpdateUserPasswordFail(throwable)
            }
        })
    }

    open fun onUpdateUserPasswordSuccess(success: Boolean) {}
    open fun onUpdateUserPasswordFail(throwable: Throwable) {}

    fun requestGetUserWithFacebook() {
        mRequestManager.requestGetUserWithFacebook(object : RequestManager.OnGetUserWithFacebookListener {
            override fun onGetUserWithFacebookLoaded(user: User) {
                onGetUserWithFacebookSuccess(user)
            }

            override fun onGetUserWithFacebookError(throwable: Throwable) {
                onGetUserWithFacebookFail(throwable)
            }
        })
    }

    open fun onGetUserWithFacebookSuccess(user: User) {}
    open fun onGetUserWithFacebookFail(throwable: Throwable) {}

}