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

package com.zerebrez.zerebrez.services.sharedpreferences

import android.content.Context
import com.zerebrez.zerebrez.models.Question

/**
 * Created by Jorge Zepeda Tinoco on 29/04/18.
 * jorzet.94@gmail.com
 */

class SharedPreferencesManager(context: Context) {

    /*
     * tags to save data
     */
    private val SHARED_PREFERENCES_NAME : String = "shared_preferences_name"
    private val JSON_MODULES : String = "json_modules"
    private val JSON_USER : String = "json_user"
    private val JSON_FREE_MODULES : String = "json_free_modules"
    private val JSON_FREE_EXAMS : String = "json_free_exams"
    private val JSON_PREMIUM_MODULES : String = "json_premium_modules"
    private val JSON_PREMIUM_EXAMS : String = "json_premium_exams"
    private val PERSISTANCE_DATA : String = "persistance_data"
    private val IMAGES_DOWNLOADED : String = "images_downloaded"
    private val IS_LOGGED_IN : String = "is_logged_in"
    private val JSON_INSTITUTES : String = "json_institutes"
    private val JSON_EXAMS : String = "json_exams"
    private val JSON_QUESTIONS : String = "json_questions"
    private val JSON_TERMS_AND_PRIVACY : String = "json_terms_and_privacy"
    private val JSON_SELECTED_SCHOOLS : String = "json_seelected_schools"
    private val JSON_EXAM_SCORES : String = "json_exam_scores"
    private val JSON_LAST_EXAM_DIT_IT : String = "json_last_exam_did_it"
    private val JSON_IMAGES_PATH : String = "json_images_path"
    private val JSON_CURRENT_QUESTION : String = "json_current_question"
    private val NOTIFICATION_TIME : String = "notification_time"
    private val REMINDER_SATUS : String = "reminder_status"
    private val HAS_PENDING_PAYMENT : String = "has_pending_payment"
    private val PAYMENT_ID : String = "payment_id"

    /*
     * fragment tags
     */
    private val QUESTION_MODULE_FRAGMENT : String = "question_module_fragment"
    private val STUDY_SUBJECT_FRAGMENT : String = "study_subject_fragment"
    private val STUDY_WRONG_QUESTION_FRAGMENT : String = "study_wrong_question_fragment"
    private val EXAM_FRAGMENT : String = "exam_fragment"
    private val ADVANCES_FRAGMENT : String = "advances_fragment"
    private val SCHOOL_AVERAGE_FRAGMENT : String = "school_average_fragment"
    private val EXAMS_AVERAGE_FRAGMENT : String = "exams_average_fragemnt"

    /*
     * messages tags
     */
    private val SHOW_ANSWER_MESSAGE : String = "show_answer_message"

    /*
     * Objects
     */
    private val mContext : Context = context

    /**
     * This method removes all sharedPreferences session data
     */
    fun removeSessionData() {
        mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun storeJsonUser(json : String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_USER, json)
        editor.apply()
    }

    /**
     * @return
     *      A json string that contains user object
     */
    fun getJsonUser() : String? {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_USER, null)
    }

    fun storeJSONModules(json: String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_MODULES, json)
        editor.apply()
    }

    /**
     * @return
     *
     */
    fun getJsonModules(): String? {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_MODULES, null)
    }

    fun setPersistanceDataEnable(enable : Boolean) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(PERSISTANCE_DATA, enable)
        editor.apply()
    }

    /**
     * @return
     *      A json array string that contains object list
     */
    fun isPersistanceData() : Boolean {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(PERSISTANCE_DATA, false)
    }

    fun storeJsonFreeModules(json : String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_FREE_MODULES, json)
        editor.apply()
    }

    /**
     * @return
     *      A json array in string that contains a list of free modules ids
     */
    fun getJsonFreeModules() : String {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_FREE_MODULES, "")
    }

    fun storeJsonFreeExams(json : String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_FREE_EXAMS, json)
        editor.apply()
    }

    /**
     * @return
     *      A json array in string that contains a list of free exam ids
     */
    fun getJsonFreeExams() : String {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_FREE_EXAMS, "")
    }


    fun storeJsonCurrentQuestion(json : String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_CURRENT_QUESTION, json)
        editor.apply()
    }

    fun getJsonCurrentQuestion() : String? {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_CURRENT_QUESTION, null)
    }


    fun storeJsonInstitutes(json : String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_INSTITUTES, json)
        editor.apply()
    }

    fun getJsonInstitutes() : String {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_INSTITUTES, "")
    }

    fun storeJsonExams(json : String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_EXAMS, json)
        editor.apply()
    }

    fun getJsonExams() : String {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_EXAMS, "")
    }

    fun storeJsonQuestions(json : String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_QUESTIONS, json)
        editor.apply()
    }

    fun getJsonQuestions() : String {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_QUESTIONS, "")
    }


    fun storeLogInData(isLoggedIn : Boolean) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    fun getLogInData() : Boolean {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    fun storeTermsAndPrivacy(json : String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_TERMS_AND_PRIVACY, json)
        editor.apply()
    }

    fun getTermsAndPrivacy() : String {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_TERMS_AND_PRIVACY, "")
    }

    fun storeJsonSelectedSchools(json: String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_SELECTED_SCHOOLS, json)
        editor.apply()
    }

    fun getJsonSelectedSchools() : String {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_SELECTED_SCHOOLS, "")
    }

    fun storeJsonExamScores(json: String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_EXAM_SCORES, json)
        editor.apply()
    }

    fun getJsonExamScores() : String {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_EXAM_SCORES, "")
    }

    fun storeLastExamDidIt(json : String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_LAST_EXAM_DIT_IT, json)
        editor.apply()
    }

    fun getJsonLastExamDidIt() : String {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_LAST_EXAM_DIT_IT, "")
    }

    fun storeJsonImagesPath(json : String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_IMAGES_PATH, json)
        editor.apply()
    }

    fun getJsonImagesPath() : String {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_IMAGES_PATH, "")
    }

    fun setImagesDownloaded(areDownloaded : Boolean) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(IMAGES_DOWNLOADED, areDownloaded)
        editor.apply()
    }

    fun areImagesDownloaded() : Boolean {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(IMAGES_DOWNLOADED, false)
    }

    fun storeNotificationTime(notificationTime : String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(NOTIFICATION_TIME, notificationTime)
        editor.apply()
    }

    fun getNotificationTime() : String {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(NOTIFICATION_TIME, "")
    }

    fun storeReminderStatus(remaind : Boolean) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(REMINDER_SATUS, remaind)
        editor.apply()
    }

    fun getReminderStatus() : Boolean {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(REMINDER_SATUS, false)
    }

    /*
     * Methods to know if presentation fragment is ok or not
     */
    fun setQuestionModuleFragmentOK() {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(QUESTION_MODULE_FRAGMENT, true)
        editor.apply()
    }

    fun isQuestionModuleFragmentOK() : Boolean {
        try {
            val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(QUESTION_MODULE_FRAGMENT, false)
        } catch (exception : Exception) {
            return false
        }
    }

    fun setStudySubjectFragmentOK() {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(STUDY_SUBJECT_FRAGMENT, true)
        editor.apply()
    }

    fun isStudySubjectFragmentOK() : Boolean {
        try {
            val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(STUDY_SUBJECT_FRAGMENT, false)
        } catch (exception : Exception) {
            return false
        }
    }

    fun setStudyWrongQuestionFragmentOK() {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(STUDY_WRONG_QUESTION_FRAGMENT, true)
        editor.apply()
    }

    fun isStudyWrongQuestionFragmentOK() : Boolean {
        try {
            val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(STUDY_WRONG_QUESTION_FRAGMENT, false)
        } catch (exception : Exception) {
            return false
        }
    }

    fun setExamFragmentOK() {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(EXAM_FRAGMENT, true)
        editor.apply()
    }

    fun isExamFragmentOK() : Boolean {
        try {
            val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(EXAM_FRAGMENT, false)
        } catch (exception : Exception) {
            return false
        }
    }

    fun setAdvancesFragmentOK() {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(ADVANCES_FRAGMENT, true)
        editor.apply()
    }

    fun isAdvancesfragmentOK() : Boolean {
        try {
            val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(ADVANCES_FRAGMENT, false)
        } catch (exception : Exception) {
            return false
        }
    }

    fun setSchoolAverageFragmentOK() {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(SCHOOL_AVERAGE_FRAGMENT, true)
        editor.apply()
    }

    fun isSchoolAverageFragmentOK() : Boolean {
        try {
            val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(SCHOOL_AVERAGE_FRAGMENT, false)
        } catch (exception : Exception) {
            return false
        }
    }

    fun setExamsAverageFragmentOK() {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(EXAMS_AVERAGE_FRAGMENT, true)
        editor.apply()
    }

    fun isExamsAverageFragmentOK() : Boolean {
        try {
            val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(EXAMS_AVERAGE_FRAGMENT, false)
        } catch (exception : Exception) {
            return false
        }
    }

    fun setShowAnswerMessageOK() {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(SHOW_ANSWER_MESSAGE, true)
        editor.apply()
    }

    fun isShowAnswerMessageOK() : Boolean {
        try {
            val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(SHOW_ANSWER_MESSAGE, false)
        } catch (exception : Exception) {
            return false
        }
    }

    fun saveGoogleToken(token : String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(EXAMS_AVERAGE_FRAGMENT, token)
        editor.apply()
    }

    fun storePendingPayment(hasPendingPayment : Boolean) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(HAS_PENDING_PAYMENT, hasPendingPayment)
        editor.apply()
    }

    fun getPendingPayment() : Boolean {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(HAS_PENDING_PAYMENT, false)
    }

    fun storePaymentId(paymentId :  String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(PAYMENT_ID, paymentId)
        editor.apply()
    }

    fun getPaymentId() : String {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(PAYMENT_ID, "")
    }

}