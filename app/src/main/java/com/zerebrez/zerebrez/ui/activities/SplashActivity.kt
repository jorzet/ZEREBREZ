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
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.zerebrez.zerebrez.BuildConfig
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.services.database.DataHelper
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

    /*
     * UI accessors
     */
    private lateinit var mProgressBar : ProgressBar
    private lateinit var mComipemsImageView : ImageView

    /*
     * version values
     */
    private var mVersionName : String = ""
    private var mVersionCode : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mProgressBar = findViewById(R.id.pb_progressbar_init)
        mComipemsImageView = findViewById(R.id.tv_comipems)

        mVersionName = BuildConfig.VERSION_NAME
        mVersionCode = BuildConfig.VERSION_CODE

        requestGetMinimumVersion()
        //initHandler()
    }

    private fun initHandler() {
        Handler().postDelayed(object : Runnable {
            override fun run() {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null && checkSessionData() && !user.isAnonymous) {
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

        mComipemsImageView.setVisibility(View.GONE)
        mProgressBar.setVisibility(View.VISIBLE)

        Thread(Runnable {
            for (i in ONE..ONE_HUNDRED)
                mProgressBar.setProgress(updateProgress())

            // if user is logged show ContentActivity else show LogInActivity
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null && checkSessionData() && !user.isAnonymous) {
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
        initNotificationService()
        DataHelper(baseContext).setisAfterLogIn(false)
        val intent = Intent(this, ContentActivity::class.java)
        this.startActivity(intent)
        this.finish()
    }

    private fun goUpdateApp() {
        DataHelper(baseContext).setisAfterLogIn(false)
        val intent = Intent(this, UpdateAppActivity::class.java)
        this.startActivity(intent)
        this.finish()
    }

    private fun initNotificationService() {
        val dataHelper = DataHelper(this)
        val notifcationTime = dataHelper.getNotificationTime()
        if (notifcationTime.equals("")) { // set default notification time at 16:00
            dataHelper.saveNotificationTime("16:00")
        } else {

        }
    }

    override fun onGetMinimumVersionSuccess(minimumVersion: String) {
        super.onGetMinimumVersionSuccess(minimumVersion)

        val minVerDatabase = minimumVersion.replace(".","").toInt()
        val minVerLocal = mVersionName.replace(".","").toInt()

        if (minVerLocal < minVerDatabase) {
            goUpdateApp()
        } else {
            initHandler()
        }

    }

    override fun onGetMinimumVersionFail(throwable: Throwable) {
        super.onGetMinimumVersionFail(throwable)
        initHandler()
    }
}
