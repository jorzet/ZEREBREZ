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

package com.zerebrez.zerebrez.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.zerebrez.zerebrez.R
import android.widget.EditText

/**
 * Created by Jorge Zepeda Tinoco on 31/05/18.
 * jorzet.94@gmail.com
 */

class SendEmailActivity : BaseActivityLifeCycle() {
    private lateinit var mToolBar : Toolbar

    private lateinit var et_email: EditText
    private lateinit var et_subject: EditText
    private lateinit var et_message: EditText
    private lateinit var Send: View
    private lateinit var Attachment: View
    private lateinit var email: String
    private lateinit var subject: String
    private lateinit var message: String
    private lateinit var attachmentFile: String
    private lateinit var URI: Uri
    private val PICK_FROM_GALLERY = 101
    var columnIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_email)

        mToolBar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolBar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true);

        et_email = findViewById(R.id.et_to)
        et_subject = findViewById(R.id.et_subject)
        et_message = findViewById(R.id.et_message)
        Attachment = findViewById(R.id.bt_attachment)
        Send = findViewById(R.id.bt_send)
        //send button listener
        Send.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View) {
                sendEmail()
            }
        })

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun sendEmail() {
        try {
            val email = resources.getString(R.string.support_email_text)
            subject = et_subject.getText().toString()
            message = et_message.getText().toString()
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "plain/text"
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf<String>(email))
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message)
            this.startActivity(Intent.createChooser(emailIntent, "Sending email..."))
        } catch (t: Throwable) {
            Toast.makeText(this, "Request failed try again: " + t.toString(), Toast.LENGTH_LONG).show()
        }

    }
}