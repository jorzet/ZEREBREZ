package com.zerebrez.zerebrez.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager

class ShowAnswerMessageActivity : BaseActivityLifeCycle() {

    /*
     * UI accessors
     */
    private lateinit var mText : TextView
    private lateinit var mItIsUnderstandButton: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_answer_message)

        mText = findViewById(R.id.text_2)
        mItIsUnderstandButton = findViewById(R.id.btn_it_is_understand)

        mText.setText(Html.fromHtml("<span style=\"color:#205A8F;\">La pregunta se enviara a </span>&nbsp;<span style=\"color:#F7921E;\">\"erroneas\"</span>&nbsp;<span style=\"color:#205A8F;\">para que la repases despues</span>"));
        mItIsUnderstandButton.setOnClickListener(mItIsUnderstandButtonListener)

    }

    private val mItIsUnderstandButtonListener = View.OnClickListener {
        SharedPreferencesManager(baseContext).setShowAnswerMessageOK()
        val intent = Intent()
        setResult(SHOW_ANSWER_MESSAGE_RESULT_CODE, intent)
        finish()
        onBackPressed()
    }
}