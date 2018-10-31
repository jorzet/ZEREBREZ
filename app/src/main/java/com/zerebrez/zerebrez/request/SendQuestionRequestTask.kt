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
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.QuestionNewFormat
import com.zerebrez.zerebrez.models.enums.ErrorType

/**
 * Created by Jorge Zepeda Tinoco on 03/06/18.
 * jorzet.94@gmail.com
 */

class SendQuestionRequestTask(activity : Activity): AbstractRequestTask<Any, Void, String>() {

    private val TAG : String = "SendQuestionRequestTask"
    private val USERS_REFERENCE : String = "users"
    private val ANSWERED_QUESTION_MODULE : String = "answeredQuestions"
    private val IS_CORRECT_REFERENCE : String = "isCorrect"
    private val SUBJECT_REFERENCE : String = "subject"
    private val CHOSEN_OPTION_REFERENCE : String = "chosenOption"

    private val mActivity : Activity = activity
    private lateinit var mFirebaseDatabase: DatabaseReference
    private var mFirebaseInstance: FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onPreExecute() {
        super.onPreExecute()
        if (onRequestListenerSucces == null || onRequestLietenerFailed == null)
            return
    }

    override fun doInBackground(vararg params: Any): String? {

        val question = params[0] as QuestionNewFormat
        val course = params[1] as String
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(USERS_REFERENCE)
        mFirebaseDatabase.keepSynced(true)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userUpdates = HashMap<String, Any>()

            if (!question.chosenOption.equals("") && question.subject != null) {
                userUpdates.put(user.uid + "/" + ANSWERED_QUESTION_MODULE + "/" + course + "/" + question.questionId + "/" + IS_CORRECT_REFERENCE, question.wasOK)
                userUpdates.put(user.uid + "/" + ANSWERED_QUESTION_MODULE + "/" + course + "/" + question.questionId + "/" + SUBJECT_REFERENCE, question.subject.value)
                userUpdates.put(user.uid + "/" + ANSWERED_QUESTION_MODULE + "/" + course + "/" + question.questionId + "/" + CHOSEN_OPTION_REFERENCE, question.chosenOption)
            }

            mFirebaseDatabase.updateChildren(userUpdates).addOnCompleteListener(mActivity, object : OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if (task.isComplete) {
                        Log.d(TAG, "complete requestSendAnsweredQuestions")
                        onRequestListenerSucces.onSuccess(true)
                    } else {
                        Log.d(TAG, "cancelled requestSendAnsweredQuestions")
                        val error = GenericError()
                        error.setErrorType(ErrorType.ANSWERED_QUESTIONS_NOT_SENDED)
                        onRequestLietenerFailed.onFailed(error)
                    }
                }
            })
        }
        return ""
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)

        if (onRequestListenerSucces == null || onRequestLietenerFailed == null)
            return
    }
}