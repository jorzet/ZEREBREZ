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


class SendEmailActivity : BaseActivityLifeCycle() {
    private lateinit var mToolBar : Toolbar

    private lateinit var et_email: EditText
    private lateinit var et_subject: EditText
    private lateinit var et_message: EditText
    private lateinit var Send: Button
    private lateinit var Attachment: Button
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