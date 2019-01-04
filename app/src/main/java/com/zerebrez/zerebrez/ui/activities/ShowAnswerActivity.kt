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
import android.widget.ListView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.OptionQuestionAdapterRefactor
import com.zerebrez.zerebrez.models.Image
import com.zerebrez.zerebrez.models.QuestionOption
import com.zerebrez.zerebrez.models.enums.QuestionType
import com.zerebrez.zerebrez.services.database.DataHelper
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.zerebrez.zerebrez.adapters.QuestionAnswerAdapterRefactor

/**
 * Created by Jorge Zepeda Tinoco on 29/04/18.
 * jorzet.94@gmail.com
 */

class ShowAnswerActivity: BaseActivityLifeCycle() {

    /*
     * UI accessors
     */
    private lateinit var mAnswerList : RecyclerView
    private lateinit var mItIsUnderstoodButton : View

    /*
     * Adapter
     */
    private lateinit var questionAnswerAdapterRefactor: QuestionAnswerAdapterRefactor

    /*
     * Objects
     */
    private lateinit var mImagesPath : List<Image>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_answer)

        mAnswerList = findViewById(R.id.nslv_container)
        mItIsUnderstoodButton = findViewById(R.id.btn_it_is_understand)

        mItIsUnderstoodButton.setOnClickListener(mItIsUnderstoodButtonListener)

        val dataHelper = DataHelper(baseContext)
        val mImagesPath = dataHelper.getImagesPath()
        val questionNewFormat = dataHelper.getCurrentQuestionNewFormat()

        if (questionNewFormat != null) {

            val realSize = questionNewFormat.questionData.size

            //optionQuestionAdapter = OptionQuestionAdapterRefactor(true, questionNewFormat, mImagesPath, baseContext)
            //mAnswerList.adapter = optionQuestionAdapter
            questionAnswerAdapterRefactor = QuestionAnswerAdapterRefactor(true, questionNewFormat, mImagesPath, getUser()!!, baseContext!!)

            val linearLayoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
            mAnswerList.setLayoutManager(linearLayoutManager)
            mAnswerList.adapter = questionAnswerAdapterRefactor
        }

    }

    override fun onBackPressed() {
        try {
            val intent = Intent()
            intent.putExtra(SET_CHECKED_TAG, true)
            setResult(SHOW_ANSWER_RESULT_CODE, intent)
            finish()
            super.onBackPressed()
        } catch (exception: Exception) {}
    }

    private val mItIsUnderstoodButtonListener = View.OnClickListener {
        DataHelper(baseContext).saveCurrentQuestionNewFormat(null)
        onBackPressed()
    }

    private fun getNameInStorage(imageId : String, mImagesPath : List<Image>) : String {
        var nameInStorage = ""
        for (image in mImagesPath) {
            if (imageId.equals("i"+image.getImageId())) {
                nameInStorage = image.getNameInStorage()
                return nameInStorage
            }
        }
        return ""
    }
}