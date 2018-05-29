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

package com.zerebrez.zerebrez.fragments.init

import android.content.Intent
import android.os.Bundle
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.InitViewPager
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.ui.activities.QuestionActivity


/**
 * Created by Jorge Zepeda Tinoco on 12/03/18.
 * jorzet.94@gmail.com
 */

class InitFragment : BaseContentFragment() {

    private val MODULE_ID = "module_id"
    private val ANONYMOUS_USER = "anonymous_user"

    private lateinit var mViewPager: ViewPager
    private lateinit var mSelectButton : Button
    private lateinit var secundary: Button
    private lateinit var comipems: Button
    private lateinit var loading : ProgressBar

    private var mCourses : List<String> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.init_fragment, container, false)!!

        mViewPager = rootView.findViewById(R.id.viewPager)
        mSelectButton = rootView.findViewById(R.id.btn_select)
        secundary = rootView.findViewById(R.id.btn1)
        comipems = rootView.findViewById(R.id.btn2)
        loading = rootView.findViewById(R.id.pb_loading)

        mSelectButton.setOnClickListener(mSelectButtonListener)
        secundary.setOnClickListener(secundaryListener)
        comipems.setOnClickListener(comipemsListener)

        requestCourses()
        loading.visibility = View.VISIBLE

        return rootView
    }

    override fun onGetCoursesSuccess(courses: List<String>) {
        super.onGetCoursesSuccess(courses)

        loading.visibility = View.GONE

        mCourses = courses
        if (fragmentManager != null) {
            val mAdapter = InitViewPager(fragmentManager!!, courses)
            mViewPager.adapter = mAdapter
            mViewPager.currentItem = 0

            if (courses.size == 1) {
                secundary.visibility = View.GONE
                comipems.visibility = View.GONE
            } else {
                setButton(secundary, 40, 40, true)
                setButton(comipems, 20, 20, false)
                setTab()
            }
        } else {
            activity!!.onBackPressed()
        }
    }

    override fun onGetCoursesFail(throwable: Throwable) {
        super.onGetCoursesFail(throwable)
        loading.visibility = View.GONE
    }

    private fun setButton(btn: Button?, h: Int, w: Int, selected: Boolean) {
        btn?.width = w
        btn?.height = h
        if (selected) {
            btn?.background = resources.getDrawable(R.drawable.rounded_button_selected)
        } else {
            btn?.background = resources.getDrawable(R.drawable.rounded_button_unselected)
        }
    }

    private fun btnAction(action: Int) {
        when (action) {
            0 -> {
                setButton(secundary, 40, 40, true)
                setButton(comipems,20, 20, false)
            }
            1 -> {
                setButton(secundary, 20, 20, false)
                setButton(comipems, 40, 40, true)
            }
        }
    }

    private fun setTab() {
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                btnAction(position)
            }

            override fun onPageSelected(position: Int) {
                btnAction(position)
            }

        })
    }

    private val secundaryListener : View.OnClickListener = View.OnClickListener {
        mViewPager.currentItem = 0
        setButton(secundary,40,40, true)
        setButton(comipems,20,20, false)
    }

    private val comipemsListener : View.OnClickListener = View.OnClickListener {
        mViewPager.currentItem = 1
        setButton(secundary, 20, 20, false)
        setButton(comipems, 40, 40, true)
    }

    private val mSelectButtonListener : View.OnClickListener = View.OnClickListener {

        val user = User()
        user.setCourse(mCourses.get(mViewPager.currentItem))
        val userFirebase = FirebaseAuth.getInstance().currentUser
        if (userFirebase != null) {
            user.setUUID(userFirebase.uid)
        }
        saveUser(user)
        goQuestionActivity()
    }

    private fun goQuestionActivity() {
        val intent = Intent(activity, QuestionActivity::class.java)
        intent.putExtra(MODULE_ID, 1) // show first module
        intent.putExtra(ANONYMOUS_USER, true)
        startActivity(intent)
        activity!!.finish()
    }
}

