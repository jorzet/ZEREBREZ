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

package com.zerebrez.zerebrez.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager

/**
 * Created by Jorge Zepeda Tinoco on 29/04/18.
 * jorzet.94@gmail.com
 */

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

    override fun onBackPressed() {
        SharedPreferencesManager(baseContext).setShowAnswerMessageOK()
        val intent = Intent()
        setResult(SHOW_ANSWER_MESSAGE_RESULT_CODE, intent)
        finish()
        super.onBackPressed()
    }

    /*
     * Listener that send result code in onBackPress method
     */
    private val mItIsUnderstandButtonListener = View.OnClickListener {
        onBackPressed()
    }
}