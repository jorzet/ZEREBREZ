package com.zerebrez.zerebrez.request

import android.app.Activity
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.Question
import com.zerebrez.zerebrez.models.QuestionNewFormat
import com.zerebrez.zerebrez.models.enums.ErrorType

class SendQuestionsRequestTask(activity : Activity, questions: List<QuestionNewFormat>): AbstractRequestTask<Any, Void, String>() {

    private val TAG : String = "SendQuestionRequestTask"
    private val USERS_REFERENCE : String = "users"
    private val ANSWERED_QUESTION_MODULE : String = "answeredQuestions"
    private val IS_CORRECT_REFERENCE : String = "isCorrect"
    private val SUBJECT_REFERENCE : String = "subject"
    private val CHOSEN_OPTION_REFERENCE : String = "chosenOption"

    private val mActivity : Activity = activity
    private val mQuestions = questions
    private lateinit var mFirebaseDatabase: DatabaseReference
    private var mFirebaseInstance: FirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onPreExecute() {
        super.onPreExecute()
        if (onRequestListenerSucces == null || onRequestLietenerFailed == null)
            return
    }

    override fun doInBackground(vararg params: Any): String? {

        val course = params[0] as String
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(USERS_REFERENCE)
        mFirebaseDatabase.keepSynced(true)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userUpdates = HashMap<String, Any>()

            for (question in mQuestions) {
                if (!question.chosenOption.equals("") && question.subject != null) {
                    userUpdates.put(user.uid + "/" + ANSWERED_QUESTION_MODULE + "/" + course + "/" + question.questionId + "/" + IS_CORRECT_REFERENCE, question.wasOK)
                    userUpdates.put(user.uid + "/" + ANSWERED_QUESTION_MODULE + "/" + course + "/" + question.questionId + "/" + SUBJECT_REFERENCE, question.subject.value)
                    userUpdates.put(user.uid + "/" + ANSWERED_QUESTION_MODULE + "/" + course + "/" + question.questionId + "/" + CHOSEN_OPTION_REFERENCE, question.chosenOption)
                }
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