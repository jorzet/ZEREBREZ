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

package com.zerebrez.zerebrez.models

/**
 * Created by Jorge Zepeda Tinoco on 29/05/18.
 * jorzet.94@gmail.com
 */

class User {
    private var email : String = ""
    private var password : String = ""
    private var course : String = ""
    private var uuid : String = ""
    private var facebookLogIn = false
    private var googleLogIn = false
    private var answeredModules : List<Module> = arrayListOf()
    private var answeredQuestions : List<Question> = arrayListOf()
    private var answeredExams : List<Exam> = arrayListOf()
    private var selectedSchools : List<School> = arrayListOf()
    private var premiumUser : Boolean = false
    private var timestamp : String = "0"

    constructor(email: String, password: String) {
        this.email = email
        this.password = password
    }

    constructor()

    fun setEmail(email : String) {
        this.email = email
    }

    fun getEmail() : String {
        return this.email
    }

    fun setPassword(password : String) {
        this.password = password
    }

    fun getPassword() : String {
        return this.password
    }

    fun setCourse(course : String) {
        this.course = course
    }

    fun getCourse() : String {
        return this.course
    }

    fun setUUID(uuid : String) {
        this.uuid = uuid
    }

    fun getUUID() : String {
        return this.uuid
    }

    fun setAnsweredModules(answeredModules : List<Module>) {
        this.answeredModules = answeredModules
    }

    fun getAnsweredModule() : List<Module> {
        return this.answeredModules
    }

    fun setAnsweredQuestions(answeredQuestions : List<Question>) {
        this.answeredQuestions = answeredQuestions
    }

    fun setAnsweredExams(answeredExams : List<Exam>) {
        this.answeredExams = answeredExams
    }

    fun getAnsweredExams() : List<Exam> {
        return this.answeredExams
    }

    fun getAnsweredQuestion() : List<Question> {
        return this.answeredQuestions
    }

    fun setSelectedShools(selectedSchools: List<School>) {
        this.selectedSchools = selectedSchools
    }

    fun getSelectedSchools() : List<School> {
        return this.selectedSchools
    }

    fun setFacebookLogIn(facebookLogIn : Boolean) {
        this.facebookLogIn = facebookLogIn
    }

    fun isFacebookLogIn() : Boolean {
        return this.facebookLogIn
    }

    fun setGoogleLogIn(googleLogIn : Boolean) {
        this.googleLogIn = googleLogIn
    }

    fun isGoogleLogIn() : Boolean {
        return this.googleLogIn
    }

    fun setPremiumUser(premiumUser : Boolean) {
        this.premiumUser = premiumUser
    }

    fun isPremiumUser() : Boolean {
        return this.premiumUser
    }

    fun isAnonymous() : Boolean {
        return email.equals("") && password.equals("")
    }

    fun setTimeStamp(timestamp : String) {
        this.timestamp = timestamp
    }

    fun getTimestamp() : String {
        return this.timestamp
    }
}