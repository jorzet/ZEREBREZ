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

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.utils.FontUtil
import android.content.Intent
import android.net.Uri

/**
 * Created by Jorge Zepeda Tinoco on 14/11/18.
 * jorzet.94@gmail.com
 */

class UpdateAppActivity: BaseActivityLifeCycle() {

    /*
     * UI accessors
     */
    private lateinit var mUpdateText: TextView
    private lateinit var mUpdateButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_app)

        mUpdateText = findViewById(R.id.tv_need_update)
        mUpdateButton = findViewById(R.id.btn_update_button)

        mUpdateText.typeface = FontUtil.getNunitoSemiBold(baseContext)
        mUpdateButton.typeface = FontUtil.getNunitoSemiBold(baseContext)

        mUpdateButton.setOnClickListener(mUpdateButtonListener)

    }

    private val mUpdateButtonListener = View.OnClickListener {
        openPlayStore()
    }

    /*
     * This method open play store app
     */
    private fun openPlayStore() {
        val appPackageName = packageName // getPackageName() from Context or Activity object
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

}