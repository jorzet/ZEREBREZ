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

package com.zerebrez.zerebrez.fragments.practice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.Module
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.ui.activities.QuestionActivity
import android.util.DisplayMetrics

/**
 * Created by Jorge Zepeda Tinoco on 26/04/18.
 * jorzet.94@gmail.com
 */

class QuestionModulesFragment : BaseContentFragment() {

    /*
     * tags
     */
    private val TAG : String = "QuestionModulesFragment"
    private val MODULE_ID : String = "module_id"
    private val QUESTION_ID : String = "question_id"
    private val ANONYMOUS_USER : String = "anonymous_user"
    private val FROM_WRONG_QUESTION : String = "from_wrong_question"

    /*
     * UI accessors
     */
    private lateinit var mLeftTableLayout : GridLayout
    private lateinit var mCenterTableLayout : GridLayout
    private lateinit var mRightTableLayout : GridLayout

    /*
     * data helper
     */
    private lateinit var mDataHelper : DataHelper

    /*
     * atributes
     */
    private var mTotalModules : Int = 0
    private var mModuleList = arrayListOf<Module>()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.question_modules_fragment, container, false)!!

        mLeftTableLayout = rootView.findViewById(R.id.table_left)
        mCenterTableLayout = rootView.findViewById(R.id.table_center)
        mRightTableLayout = rootView.findViewById(R.id.table_right)

        mDataHelper = DataHelper(context!!)
        val modules = mDataHelper.getModulesAnsQuestions()
        if (modules == null) {
            requestModules()
        } else {
            val user = getUser()

            if (user != null && user.isPremiumUser()) {
                updateModuleList(modules)
                drawModules()
            } else {
                val freeModules = arrayListOf<Module>()
                for (module in modules) {
                    if (module.isFreeModule()) {
                       freeModules.add(module)
                    }
                }
                updateModuleList(freeModules)
                drawModules()
            }
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()

        resetValues()

        mDataHelper = DataHelper(context!!)
        val modules = mDataHelper.getModulesAnsQuestions()
        if (modules == null) {
            requestModules()
        } else {
            val user = getUser()

            if (user != null && user.isPremiumUser()) {
                updateModuleList(modules)
                drawModules()
            } else {
                val freeModules = arrayListOf<Module>()
                for (module in modules) {
                    if (module.isFreeModule()) {
                        freeModules.add(module)
                    }
                }
                updateModuleList(freeModules)
                drawModules()
            }
        }
    }

    /*
     *
     */
    private fun updateModuleList(modules : List<Module>) {
        var row = 1
        for (i in 0 .. modules.size - 1) {
            when (row) {
                1 -> {
                    mModuleList.add(modules.get(i))
                    val nothing = Module()
                    nothing.setId(Integer(-1))
                    mModuleList.add(nothing)
                    val padding = Module()
                    padding.setId(Integer(-2))
                    mModuleList.add(padding)
                }
                2 -> {
                    val nothing = Module()
                    nothing.setId(Integer(-1))
                    mModuleList.add(nothing)
                    mModuleList.add(modules.get(i))
                    val padding = Module()
                    padding.setId(Integer(-1))
                    mModuleList.add(padding)
                }
                3 -> {
                    val nothing = Module()
                    nothing.setId(Integer(-2))
                    mModuleList.add(nothing)
                    val padding = Module()
                    padding.setId(Integer(-1))
                    mModuleList.add(padding)
                    mModuleList.add(modules.get(i))
                }
                4 -> {
                    val nothing = Module()
                    nothing.setId(Integer(-1))
                    mModuleList.add(nothing)
                    mModuleList.add(modules.get(i))
                    val padding = Module()
                    padding.setId(Integer(-1))
                    mModuleList.add(padding)
                }
            }
            row++
            if(row == 5)
                row = 1
        }

    }

    override fun onGetModulesSucces(result: List<Module>) {
        super.onGetModulesSucces(result)
        updateModuleList(result)
        drawModules()
    }

    override fun onGetModulesFail(throwable: Throwable) {
        super.onGetModulesFail(throwable)
    }



    private fun drawModules() {

        var cnt : Int = 0

        for (i in 0 .. mModuleList.size - 1) {

            val view = LayoutInflater.from(context).inflate(R.layout.custom_module, null, false)
            val text : TextView = view.findViewById(R.id.text)

            val number = mModuleList.get(i).getId().toString()

            text.text = number

            // params for module
            val param = GridLayout.LayoutParams()

            if (number.equals("-1")) {
                view.background = resources.getDrawable(R.drawable.empty_square)
                text.visibility = View.GONE
                param.height = resources.getDimension(R.dimen.height_square).toInt()
                param.width = resources.getDimension(R.dimen.width_square).toInt()
                param.bottomMargin = 2
                param.rightMargin = 2
                param.leftMargin = 2
                param.topMargin = 2
                param.setGravity(Gravity.CENTER)
            } else if(number.equals("-2")) {
                view.background = resources.getDrawable(R.drawable.square_first_module_background)
                text.visibility = View.GONE
                param.height = resources.getDimension(R.dimen.width_square).toInt()
                param.width = resources.getDimension(R.dimen.width_square).toInt()
                param.bottomMargin = 2
                param.rightMargin = 2
                param.leftMargin = 2
                param.topMargin = 2
                param.setGravity(Gravity.CENTER)
            } else {
                val module = mModuleList.get(i)
                if (module.isAnsweredModule()) {
                    view.background = resources.getDrawable(R.drawable.checked_module_background)
                } else {
                    view.background = resources.getDrawable(R.drawable.unchecked_module_background)
                }
                param.height = resources.getDimension(R.dimen.height_square).toInt()
                param.width = resources.getDimension(R.dimen.width_square).toInt()
                param.bottomMargin = 2
                param.rightMargin = 2
                param.leftMargin = 2
                param.topMargin = 2
                param.setGravity(Gravity.CENTER)
                view.setOnClickListener(View.OnClickListener {
                    val textView : TextView =  view.findViewById(R.id.text)
                    val text : String = textView.text.toString()
                    Log.d(TAG, "onClick: number --- " + number)
                    goQuestionActivity(Integer.parseInt(number))
                })
            }



            when (cnt) {
                0 -> {
                    mLeftTableLayout.addView(view)
                    view.setLayoutParams(param)
                }
                1 -> {
                    mCenterTableLayout.addView(view)
                    view.setLayoutParams(param)
                }
                2 -> {
                    mRightTableLayout.addView(view)
                    view.setLayoutParams(param)
                }
            }

            cnt++
            if (cnt==3) { cnt = 0 }
        }

    }

    private fun resetValues() {
        mModuleList = arrayListOf<Module>()
        mLeftTableLayout.removeAllViews()
        mCenterTableLayout.removeAllViews()
        mRightTableLayout.removeAllViews()
    }

    fun convertPixelsToDp(px: Float, context: Context): Float {
        val resources = context.getResources()
        val metrics = resources.getDisplayMetrics()
        val dp = px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        return dp
    }

    private fun goQuestionActivity(moduleId : Int) {
        val intent = Intent(activity, QuestionActivity::class.java)
        intent.putExtra(MODULE_ID, moduleId)
        intent.putExtra(ANONYMOUS_USER, false)
        intent.putExtra(FROM_WRONG_QUESTION, false)
        this.startActivity(intent)
    }
}