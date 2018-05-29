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

import android.os.Bundle
import android.support.v7.widget.Toolbar

import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.ui.activities.BaseActivityLifeCycle

/*
* Created by Jorge Zepeda Tinoco on 27/02/18.
* jorzet.94@gmail.com
*/

class TermsAndPrivacyActivity : BaseActivityLifeCycle() {

    private lateinit var mToolBar : Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_privacy)

        mToolBar = findViewById(R.id.toolbar)

        setSupportActionBar(mToolBar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true);
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}