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

package com.zerebrez.zerebrez.services.database

import android.content.Context
import android.util.Log
import com.zerebrez.zerebrez.models.*
import com.zerebrez.zerebrez.services.sharedpreferences.JsonParcer
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import org.json.JSONArray

/**
 * Created by Jorge Zepeda Tinoco on 29/04/18.
 * jorzet.94@gmail.com
 */

class DataHelper(context: Context) {
    private val TAG : String = "DataHelper"

    private val mContext : Context = context

    val dbHelper = DataBase(context)

    fun setisAfterLogIn(afterLogin: Boolean) {
        SharedPreferencesManager(mContext).storeAfterLogin(afterLogin)
    }

    fun isAfterLogIn() : Boolean {
        return SharedPreferencesManager(mContext).getIsAfterLogIn()
    }

    fun saveSessionData(hasSessionData : Boolean) {
        SharedPreferencesManager(mContext).storeLogInData(hasSessionData)
    }

    fun hasSessionData() : Boolean {
        val hasSessionData = SharedPreferencesManager(mContext).getLogInData()
        return hasSessionData
    }

    fun isPremiumUser() : Boolean {
        val jsonUser = SharedPreferencesManager(mContext).getJsonUser()
        if (jsonUser != null) {
            val user = JsonParcer.getObjectFromJson(jsonUser, User::class.java) as User
            return user.isPremiumUser()
        } else {
            return false
        }
    }

    fun saveModules(modules: List<Module>) {
        val json = JsonParcer.parceObjectListToJson(modules)
        SharedPreferencesManager(mContext).storeJSONModules(json)
    }

    fun getModulesAnsQuestions() : List<Module>{
        val json = SharedPreferencesManager(mContext).getJsonModules()
        val modules = arrayListOf<Module>()
        val moduleArray = JSONArray(json)
        for (i in 0 .. moduleArray.length() - 1) {
            modules.add(JsonParcer.getObjectFromJson(moduleArray.get(i).toString(), Module::class.java) as Module)
        }

        return modules
    }

    /*fun saveQuestions(questions : List<Question>) {
        val json = JsonParcer.parceObjectListToJson(questions)
        SharedPreferencesManager(mContext).storeJsonQuestions(json)
    }

    fun getQuestions() : List<Question> {
        val json = SharedPreferencesManager(mContext).getJsonQuestions()
        val quetions = arrayListOf<Question>()
        val questionArray = JSONArray(json)
        for (i in 0 .. questionArray.length() - 1) {
            quetions.add(JsonParcer.getObjectFromJson(questionArray.get(i).toString(), Question::class.java) as Question)
        }

        return quetions
    }*/

    fun saveQuestionsNewFormat(questionsNewFormat : List<QuestionNewFormat>) {
        val json = JsonParcer.parceObjectListToJson(questionsNewFormat)
        SharedPreferencesManager(mContext).storeJsonQuestionsNewFormat(json)
    }

    fun getQuestionsNewFormat() : List<QuestionNewFormat> {
        val json = SharedPreferencesManager(mContext).getJsonQuestionsNewFormat()
        val quetionsNewFormat = arrayListOf<QuestionNewFormat>()
        val questionArray = JSONArray(json)
        for (i in 0 .. questionArray.length() - 1) {
            quetionsNewFormat.add(JsonParcer.getObjectFromJson(questionArray.get(i).toString(), QuestionNewFormat::class.java) as QuestionNewFormat)
        }

        return quetionsNewFormat
    }

    /*
     * SAVE CURRENT QUESTION OLD FORMAT
     */
    /*
    fun saveCurrentQuestion(currentQuestion : Question?) {
        var json = ""
        if (currentQuestion != null) {
            json = JsonParcer.parceObjectToJson(currentQuestion)
        }
        SharedPreferencesManager(mContext).storeJsonCurrentQuestion(json)
    }

    fun getCurrentQuestion() : Question? {
        val json = SharedPreferencesManager(mContext).getJsonCurrentQuestion()
        if (json != null) {
            val currectQuestion = JsonParcer.getObjectFromJson(json, Question::class.java) as Question
            return currectQuestion
        }
        return null
    }*/

    /*
     * SAVE CURRENT QUESTION NEW FORMAT
     */
    fun saveCurrentQuestionNewFormat(currentQuestionNewFormat : QuestionNewFormat?) {
        var json = ""
        if (currentQuestionNewFormat != null) {
            json = JsonParcer.parceObjectToJson(currentQuestionNewFormat)
        }
        SharedPreferencesManager(mContext).storeJsonCurrentQuestionNewFormat(json)
    }

    fun getCurrentQuestionNewFormat() : QuestionNewFormat? {
        val json = SharedPreferencesManager(mContext).getJsonCurrentQuestionNewFormat()
        if (json != null) {
            try {
                val currectQuestionNewFormat = JsonParcer.getObjectFromJson(json, QuestionNewFormat::class.java) as QuestionNewFormat
                return currectQuestionNewFormat
            } catch (e: java.lang.Exception) {
            } catch (e: kotlin.Exception) {}
        }
        return null
    }

    fun hasPendingPayment() : Boolean{
        val pendingPayment = SharedPreferencesManager(mContext).getPendingPayment()
        return pendingPayment
    }

    fun saveCourses(courses: List<Course>) {
        val json = JsonParcer.parceObjectListToJson(courses)
        SharedPreferencesManager(mContext).storeJsonCourses(json)
    }

    fun getCourses(): List<Course> {
        val json = SharedPreferencesManager(mContext).getJsonCourses()
        val courses = arrayListOf<Course>()
        val coursesArray = JSONArray(json)
        for (i in 0 .. coursesArray.length() - 1) {
            courses.add(JsonParcer.getObjectFromJson(coursesArray.get(i).toString(), Course::class.java) as Course)
        }

        return courses
    }

    fun getCourseFromUserCourse(course: String): Course? {
        val json = SharedPreferencesManager(mContext).getJsonCourses()
        val courses = arrayListOf<Course>()
        val coursesArray = JSONArray(json)
        for (i in 0 .. coursesArray.length() - 1) {
            courses.add(JsonParcer.getObjectFromJson(coursesArray.get(i).toString(), Course::class.java) as Course)
        }

        for (mCourse in courses) {
            if (mCourse.id.equals(course)) {
                return mCourse
            }
        }

        return null
    }

    /*fun getQuestionsByModuleId(moduleId : Integer) : List<Question>{
        val json = SharedPreferencesManager(mContext).getJsonModules()
        Log.d(TAG, json)
        val modules = arrayListOf<Module>()
        val moduleArray = JSONArray(json)
        for (i in 0 .. moduleArray.length() - 1) {
            modules.add(JsonParcer.getObjectFromJson(moduleArray.get(i).toString(), Module::class.java) as Module)
        }

        for (module in modules) {
            if(module.getId() == moduleId) {
                return module.getQuestions()
            }
        }

        return arrayListOf<Question>()
    }*/

    /*fun getWrongQuestionsByQuestionId(questionId : Integer) : List<Question> {
        val json = SharedPreferencesManager(mContext).getJsonModules()
        Log.d(TAG, json)
        val questions = arrayListOf<Question>()
        val moduleArray = JSONArray(json)
        for (i in 0 .. moduleArray.length() - 1) {
            val module = JsonParcer.getObjectFromJson(moduleArray.get(i).toString(), Module::class.java) as Module
            for (question in module.getQuestions()) {
                if (question.getQuestionId().equals(questionId)) {
                    questions.add(question)
                    return questions
                }
            }
        }

        return arrayListOf<Question>()
    }*/

    /*fun getQuestionsByExamId(examId : Integer) : List<Question> {
        val json = SharedPreferencesManager(mContext).getJsonExams()
        val mQuestions = getQuestions()
        Log.d(TAG, json)
        val questions = arrayListOf<Question>()
        val examsArray = JSONArray(json)
        for (i in 0 .. examsArray.length() - 1) {
            val exam = JsonParcer.getObjectFromJson(examsArray.get(i).toString(), Exam::class.java) as Exam
            if (exam.getExamId().equals(examId)) {
                for (question1 in mQuestions) {
                    for (question2 in exam.getQuestions()) {
                        if (question1.getQuestionId().equals(question2.getQuestionId())) {
                            questions.add(question1)
                        }
                    }
                }
                return questions
            }
        }
        return arrayListOf<Question>()
    }

    fun saveAnsweredQuestion(question : Question) {

    }

    fun getAnsweredQuestions() {

    }

    fun getWrongQuestions() : List<Question> {
        val json = SharedPreferencesManager(mContext).getJsonModules()
        Log.d(TAG, "json: " + json)
        val wrongQuestion = arrayListOf<Question>()
        val moduleArray = JSONArray(json)
        for (i in 0 .. moduleArray.length() - 1) {
            Log.d(TAG, "moduleArray[${i}]" + moduleArray.get(i).toString())
            val module = JsonParcer.getObjectFromJson(moduleArray.get(i).toString(), Module::class.java) as Module
            if (module.getQuestions().isNotEmpty()) {
                Log.d(TAG, "answeredQuestion not null")
                for (question in module.getQuestions()) {
                    if (!question.getWasOK()
                            && !question.getOptionChoosed().equals("")
                            && !question.getAnswer().equals(question.getOptionChoosed())) {
                        wrongQuestion.add(question)
                    }
                }
            }
        }

        return wrongQuestion
    }

    fun saveWrongQuestion(wrongQuestions : List<Question>) {
        if (wrongQuestions.isNotEmpty()) {
            for (wrongQuestion in wrongQuestions) {
                if (wrongQuestion.getWasOK()) {

                }
            }
        }
    }*/

    fun saveExams(exams : List<Exam>) {
        val json = JsonParcer.parceObjectListToJson(exams)
        SharedPreferencesManager(mContext).storeJsonExams(json)
    }

    fun getExams() : List<Exam> {
        val jsonExams = SharedPreferencesManager(mContext).getJsonExams()
        val exams = arrayListOf<Exam>()
        val examsArray = JSONArray(jsonExams)
        for (i in 0 .. examsArray.length() - 1) {
            exams.add(JsonParcer.getObjectFromJson(examsArray.get(i).toString(), Exam::class.java) as Exam)
        }
        return exams
    }

    fun saveInstitutes(institutes: List<Institute>) {
        val json = JsonParcer.parceObjectListToJson(institutes)
        SharedPreferencesManager(mContext).storeJsonInstitutes(json)
    }

    fun getInstitutes() : List<Institute> {
        val jsonInstitutes = SharedPreferencesManager(mContext).getJsonInstitutes()
        val institutes = arrayListOf<Institute>()
        if (!jsonInstitutes.equals("")) {
            val institutesArray = JSONArray(jsonInstitutes)
            for (i in 0..institutesArray.length() - 1) {
                institutes.add(JsonParcer.getObjectFromJson(institutesArray.get(i).toString(), Institute::class.java) as Institute)
            }
            return institutes
        } else {
            return arrayListOf()
        }
    }

    fun saveFreeModules(modules : List<Module>) {
        val json = JsonParcer.parceObjectListToJson(modules)
        SharedPreferencesManager(mContext).storeJsonFreeModules(json)
    }

    fun getFreeModules() : List<Module> {
        val jsonFreeModules = SharedPreferencesManager(mContext).getJsonFreeModules()
        val freeModules = arrayListOf<Module>()
        val freeExamsArray = JSONArray(jsonFreeModules)
        for (i in 0 .. freeExamsArray.length() - 1) {
            freeModules.add(JsonParcer.getObjectFromJson(freeExamsArray.get(i).toString(), Module::class.java) as Module)
        }
        return freeModules
    }

    fun saveFreeExams(exams : List<Exam>) {
        val json = JsonParcer.parceObjectListToJson(exams)
        SharedPreferencesManager(mContext).storeJsonFreeExams(json)
    }

    fun getFreeExams() : List<Exam> {
        val jsonFreeExams = SharedPreferencesManager(mContext).getJsonFreeExams()
        val freeExams = arrayListOf<Exam>()
        val freeExamsArray = JSONArray(jsonFreeExams)
        for (i in 0 .. freeExamsArray.length() - 1) {
            freeExams.add(JsonParcer.getObjectFromJson(freeExamsArray.get(i).toString(), Exam::class.java) as Exam)
        }
        return freeExams
    }

    fun saveExamScores(examScore : List<ExamScore>) {
        val json = JsonParcer.parceObjectListToJson(examScore)
        SharedPreferencesManager(mContext).storeJsonExamScores(json)
    }

    fun getExamScores() : List<ExamScore> {
        val json = SharedPreferencesManager(mContext).getJsonExamScores()
        val examScores = arrayListOf<ExamScore>()
        val examScoresArray = JSONArray(json)
        for (i in 0 .. examScoresArray.length() - 1) {
            examScores.add(JsonParcer.getObjectFromJson(examScoresArray.get(i).toString(), ExamScore::class.java) as ExamScore)
        }

        return examScores
    }

    fun saveLastExamDidIt(exam : Exam) {
        SharedPreferencesManager(mContext).storeLastExamDidIt(JsonParcer.parceObjectToJson(exam))
    }

    fun getLastExamDidIt() : Exam {
        val exam = JsonParcer.getObjectFromJson(SharedPreferencesManager(mContext).getJsonLastExamDidIt(), Exam::class.java) as Exam
        return exam
    }

    fun saveImagesPath(images : List<Image>) {
        val json = JsonParcer.parceObjectListToJson(images)
        SharedPreferencesManager(mContext).storeJsonImagesPath(json)
    }

    fun getImagesPath() : List<Image> {
        val json = SharedPreferencesManager(mContext).getJsonImagesPath()
        val images = arrayListOf<Image>()
        val imagesArray = JSONArray(json)
        for (i in 0 .. imagesArray.length() - 1) {
            images.add(JsonParcer.getObjectFromJson(imagesArray.get(i).toString(), Image::class.java) as Image)
        }

        return images
    }

    fun setImagesDownloaded(areDownloaded : Boolean) {
        SharedPreferencesManager(mContext).setImagesDownloaded(areDownloaded)
    }

    fun areImagesDownloaded() : Boolean {
        return SharedPreferencesManager(mContext).areImagesDownloaded()
    }

    fun saveNotificationTime(notificationTime : String) {
        SharedPreferencesManager(mContext).storeNotificationTime(notificationTime)
    }

    fun getNotificationTime() : String {
        return SharedPreferencesManager(mContext).getNotificationTime()
    }

    fun setReminderStatus(remaind : Boolean) {
        SharedPreferencesManager(mContext).storeReminderStatus(remaind)
    }

    fun getReminderStatus() : Boolean {
        return SharedPreferencesManager(mContext).getReminderStatus()
    }

    /*fun insertModules(modules: List<Module>) {
        // Gets the data repository in write mode
        val db = dbHelper.writableDatabase

        for (module in modules) {
            // Create a new map of values, where column names are the keys
            val values = ContentValues().apply {
                put(ModuleEntry.COLUMN_MODULE_ID, module.getId().toString())
                put(ModuleEntry.COLUMN_MODULE_NAME, module.getModuleName())
            }

            // Insert the new row, returning the primary key value of the new row
            val moduleId = db?.insert(ModuleEntry.TABLE_NAME, null, values)
            Log.d(TAG, "ModuleId: " + moduleId)
            for (question in module.getQuestions()) {
                insertQuestion(question)
            }
        }
    }

    // DEPRECATED
    fun insertQuestion(question: Question) {
        // Gets the data repository in write mode
        val db = dbHelper.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(QuestionEntry.COLUMN_QUESTION_ID, question.getQuestionId().toString())
            put(QuestionEntry.COLUMN_MODULE_ID, question.getModuleId().toString())
            put(QuestionEntry.COLUMN_SUBJECT, question.getSubject())
            //put(QuestionEntry.COLUMN_TEXT, question.getText())
            put(QuestionEntry.COLUMN_OPTION_ONE, question.getOptionOne())
            put(QuestionEntry.COLUMN_OPTION_TWO, question.getOptionTwo())
            put(QuestionEntry.COLUMN_OPTION_THREE, question.getOptionThree())
            put(QuestionEntry.COLUMN_OPTION_FOUR, question.getOptionFour())
            put(QuestionEntry.COLUMN_ANSWER, question.getAnswer())
            put(QuestionEntry.COLUMN_YEAR, question.getYear())
            put(QuestionEntry.COLUMN_TYPE, question.getQuestionType())
        }

        // Insert the new row, returning the primary key value of the new row
        val questionId = db?.insert(QuestionEntry.TABLE_NAME, null, values)
        Log.d(TAG, "QuestionId: " + questionId + " inserted: "+question.getQuestionId().toString())
    }


    // DEPRECATED
    fun getQuestions(moduleId : Integer) : List<Question> {
        val questions = arrayListOf<Question>()
        val db = dbHelper.readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(
                "q.${QuestionEntry.COLUMN_QUESTION_ID}",
                "q.${QuestionEntry.COLUMN_SUBJECT}",
                "q.${QuestionEntry.COLUMN_TEXT}",
                "q.${QuestionEntry.COLUMN_OPTION_ONE}",
                "q.${QuestionEntry.COLUMN_OPTION_TWO}",
                "q.${QuestionEntry.COLUMN_OPTION_THREE}",
                "q.${QuestionEntry.COLUMN_OPTION_FOUR}",
                "q.${QuestionEntry.COLUMN_ANSWER}",
                "q.${QuestionEntry.COLUMN_YEAR}",
                "q.${QuestionEntry.COLUMN_TYPE}")

        // Filter results WHERE "module.module_id" = 'question.module_id and module.module_id = moduleId'
        val selection = "m.${ModuleEntry.COLUMN_MODULE_ID} = q.${QuestionEntry.COLUMN_MODULE_ID} " +
                "and m.${ModuleEntry.COLUMN_MODULE_ID} = ${moduleId}"

        val selectionArgs = arrayOf("q.${QuestionEntry.COLUMN_QUESTION_ID}")

        // How you want the results sorted in the resulting Cursor
        val sortOrder = "q.${QuestionEntry.COLUMN_QUESTION_ID} ASC"

        val cursor = db.query(
                ModuleEntry.TABLE_NAME + " as m JOIN " + QuestionEntry.TABLE_NAME + " as q",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        )

        Log.d(TAG, "cursor: " + cursor.count)

        with(cursor) {
            while (moveToNext()) {
                val question = Question()
                // adding values to object
                val questionId = getInt(getColumnIndexOrThrow(QuestionEntry.COLUMN_QUESTION_ID))
                question.setQuestionId(Integer(questionId))

                val subject = getString(getColumnIndexOrThrow(QuestionEntry.COLUMN_SUBJECT))
                question.setSubject(subject)

                val text = getString(getColumnIndexOrThrow(QuestionEntry.COLUMN_TEXT))
                //question.setText(text)

                val optionOne = getString(getColumnIndexOrThrow(QuestionEntry.COLUMN_OPTION_ONE))
                question.setOptionOne(optionOne)

                val optionTwo = getString(getColumnIndexOrThrow(QuestionEntry.COLUMN_OPTION_TWO))
                question.setOptionTwo(optionTwo)

                val optionThree = getString(getColumnIndexOrThrow(QuestionEntry.COLUMN_OPTION_THREE))
                question.setOptionThree(optionThree)

                val optionFour = getString(getColumnIndexOrThrow(QuestionEntry.COLUMN_OPTION_FOUR))
                question.setOptionFour(optionFour)

                val answer = getString(getColumnIndexOrThrow(QuestionEntry.COLUMN_ANSWER))
                question.setAnswer(answer)

                val year = getString(getColumnIndexOrThrow(QuestionEntry.COLUMN_YEAR))
                question.setYear(year)

                val type = getString(getColumnIndexOrThrow(QuestionEntry.COLUMN_TYPE))
                question.setQuestionType(type)

                questions.add(question)
            }
        }

        return questions
    }

    fun getModules() : List<Module> {
        val modules = arrayListOf<Module>()
        val db = dbHelper.readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf(
                ModuleEntry.COLUMN_MODULE_ID,
                ModuleEntry.COLUMN_MODULE_NAME)

        // How you want the results sorted in the resulting Cursor
        val sortOrder = "${ModuleEntry.COLUMN_MODULE_ID} ASC"

        val cursor = db.query(
                ModuleEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        )

        Log.d(TAG, "cursor: " + cursor.count)

        with(cursor) {
            while (moveToNext()) {
                val module = Module()
                val moduleId = getInt(getColumnIndexOrThrow(ModuleEntry.COLUMN_MODULE_ID))
                module.setId(Integer(moduleId))
                val questions = getQuestions(Integer(moduleId))
                module.setQuestions(questions)

                modules.add(module)
            }
        }

        return modules
    }*/



}