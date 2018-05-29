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
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager

/**
 * Created by Jorge Zepeda Tinoco on 27/02/18.
 * jorzet.94@gmail.com
 */

class SplashActivity : BaseActivityLifeCycle() {

    /* to set delay */
    private val DELAY_MILLIS : Long = 0
    private val TIME_DELAY : Long = 2000
    private val ONE_HUNDRED : Int = 100
    private val ONE : Int = 1
    private var progressBarStatus : Int = 0

    /*
     * tags
     */
    private val SHOW_START = "show_start"

    /* UI accessors */
    private lateinit var mProgressBar : ProgressBar
    private lateinit var mComipemsTextView : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mProgressBar = findViewById(R.id.pb_progressbar_init)
        mComipemsTextView = findViewById(R.id.tv_comipems)

        initHandler()
    }

    private fun initHandler() {
        Handler().postDelayed(object : Runnable {
            override fun run() {
                if (FirebaseAuth.getInstance().currentUser != null && checkSessionData()) {
                    goContentActivity()
                } else {
                    LoginManager.getInstance().logOut()
                    FirebaseAuth.getInstance().signOut()
                    SharedPreferencesManager(baseContext).removeSessionData()
                    goLogInActivity()
                }
            }
        }, TIME_DELAY)
    }

    private fun showProgressBar() {

        mComipemsTextView.setVisibility(View.GONE)
        mProgressBar.setVisibility(View.VISIBLE)

        Thread(Runnable {
            for (i in ONE..ONE_HUNDRED)
                mProgressBar.setProgress(updateProgress())

            // if user is logged show ContentActivity else show LogInActivity
            if (FirebaseAuth.getInstance().currentUser != null && checkSessionData()) {
                goContentActivity()
            } else {
                LoginManager.getInstance().logOut()
                FirebaseAuth.getInstance().signOut()
                SharedPreferencesManager(baseContext).removeSessionData()
                goLogInActivity()
            }

        }).start()

    }

    fun updateProgress() : Int{

        Thread.sleep(DELAY_MILLIS)
        progressBarStatus++; // Work process and return status

        if (progressBarStatus < ONE_HUNDRED)
            return progressBarStatus;

        return ONE_HUNDRED
    }

    private fun goLogInActivity() {
        val intent = Intent(this, LoginActivity::class.java)

        val user = getUser()
        if (user == null) {
            intent.putExtra(SHOW_START, true)
        } else {
            intent.putExtra(SHOW_START, false)
        }
        this.startActivity(intent)
        this.finish()
    }

    private fun checkSessionData() : Boolean {
        val isLoggedIn = hasLogInData()
        val persistanceData = SharedPreferencesManager(baseContext).isPersistanceData()
        if (persistanceData) {
            SharedPreferencesManager(baseContext).setPersistanceDataEnable(false)
        }
        return isLoggedIn
    }

    private fun goContentActivity() {
        val intent = Intent(this, ContentActivity::class.java)
        this.startActivity(intent)
        this.finish()
    }
}
