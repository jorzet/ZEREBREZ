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
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.SelectedSchoolsListAdapter
import com.zerebrez.zerebrez.models.Institute
import com.zerebrez.zerebrez.models.School
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.utils.FontUtil
import com.zerebrez.zerebrez.utils.NetworkUtil
import kotlin.math.round

/*
 * Created by Jorge Zepeda Tinoco on 28/04/18.
 * jorzet.94@gmail.com
 */

class ChooseSchoolsActivity : BaseActivityLifeCycle(), ErrorDialog.OnErrorDialogListener {

    /*
     * Tags
     */
    private val TAG : String = "ChooseSchoolsActivity"
    private val SHOW_CONTINUE_BUTTON : String = "show_continue_button"
    private val SHOW_BACK_BUTTON : String = "show_back_button"

    companion object {
        val UPDATE_USER_SCHOOLS : String = "update_user_schools"
    }


    /*
     * UI accessors
     */
    private lateinit var mToolBar : Toolbar
    private lateinit var mChooseSchoolTextView: TextView
    private lateinit var mFirstOptionTextView: TextView
    private lateinit var mSecondOptionTextView: TextView
    private lateinit var mThirdOptionTextView: TextView
    private lateinit var mFirstSchoolContainer : View
    private lateinit var mSecondSchoolContainer : View
    private lateinit var mThirdSchoolContainer : View
    private lateinit var mContinueContainer : View
    private lateinit var mFirstSchoolText : TextView
    private lateinit var mSecondSchoolText : TextView
    private lateinit var mThirdSchoolText : TextView
    private lateinit var mDropFirstSchool : View
    private lateinit var mDropSecondSchool : View
    private lateinit var mDropThirdSchool : View
    private lateinit var mInstitutesSchoolList : ExpandableListView
    private lateinit var mContinueButton : View
    private lateinit var mContinueText : TextView

    /*
     * Adapters
     */
    private lateinit var mSelectedSchoolsListAdapter: SelectedSchoolsListAdapter

    /*
     * Objects
     */
    private var mSchools = arrayListOf<School>()

    /*
     * Some variables
     */
    private var showContinueButton : Boolean = false
    private var showBackButton : Boolean = false
    private var hasChanges : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_schools)

        mToolBar = findViewById(R.id.toolbar)

        setSupportActionBar(mToolBar)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()!!.setDisplayShowHomeEnabled(true);

        mFirstSchoolContainer = findViewById(R.id.rl_first_option)
        mSecondSchoolContainer = findViewById(R.id.rl_second_option)
        mThirdSchoolContainer = findViewById(R.id.rl_third_option)
        mContinueContainer = findViewById(R.id.rl_continue_container)
        mFirstSchoolText = findViewById(R.id.tv_first_selected_school)
        mSecondSchoolText = findViewById(R.id.tv_second_selected_school)
        mThirdSchoolText = findViewById(R.id.tv_third_selected_school)
        mDropFirstSchool = findViewById(R.id.iv_cross_1)
        mDropSecondSchool = findViewById(R.id.iv_cross_2)
        mDropThirdSchool = findViewById(R.id.iv_cross_3)
        mInstitutesSchoolList = findViewById(R.id.lv_schools_list)
        mContinueButton = findViewById(R.id.btn_continue)
        mContinueText= findViewById(R.id.text_continue)
        mChooseSchoolTextView = findViewById(R.id.tv_choose_school_text)
        mFirstOptionTextView = findViewById(R.id.tv_first_school)
        mSecondOptionTextView = findViewById(R.id.tv_second_school)
        mThirdOptionTextView = findViewById(R.id.tv_third_school)

        mChooseSchoolTextView.typeface = FontUtil.getNunitoSemiBold(baseContext)
        mFirstOptionTextView.typeface = FontUtil.getNunitoSemiBold(baseContext)
        mSecondOptionTextView.typeface = FontUtil.getNunitoSemiBold(baseContext)
        mThirdOptionTextView.typeface = FontUtil.getNunitoSemiBold(baseContext)
        mFirstSchoolText.typeface = FontUtil.getNunitoSemiBold(baseContext)
        mSecondSchoolText.typeface = FontUtil.getNunitoSemiBold(baseContext)
        mThirdSchoolText.typeface = FontUtil.getNunitoSemiBold(baseContext)
        mContinueText.typeface = FontUtil.getNunitoSemiBold(baseContext)

        mDropFirstSchool.setOnClickListener(mDropFirstSchoolListener)
        mDropSecondSchool.setOnClickListener(mDropSecondSchoolListener)
        mDropThirdSchool.setOnClickListener(mDropThridSchoolListener)
        mContinueButton.setOnClickListener(mContinueListener)

        mFirstSchoolContainer.visibility = View.GONE
        mSecondSchoolContainer.visibility = View.GONE
        mThirdSchoolContainer.visibility = View.GONE

        showContinueButton = intent.extras.getBoolean(SHOW_CONTINUE_BUTTON)
        showBackButton = intent.extras.getBoolean(SHOW_BACK_BUTTON)

        val actionBar = supportActionBar

        if (showBackButton) {
            actionBar!!.setDisplayHomeAsUpEnabled(true)
        } else {
            actionBar!!.setDisplayHomeAsUpEnabled(false)
        }

        if (showContinueButton) {
            // this is shown after signUp fragment
            mContinueText.setText(resources.getString(R.string.continue_text))

        } else {
            val user = getUser()

            if (user != null) {
                // this is shown in profile configuration
                mSchools.addAll(user.getSelectedSchools())
                if (mSchools.isNotEmpty()) {
                    for (i in 0..mSchools.size - 1) {
                        if (i == 0) {
                            mFirstSchoolText.text = mSchools.get(i).getInstituteName() + " " + mSchools.get(i).getSchoolName()
                            mFirstSchoolContainer.visibility = View.VISIBLE
                        } else if (i == 1) {
                            mSecondSchoolText.text = mSchools.get(i).getInstituteName() + " " + mSchools.get(i).getSchoolName()
                            mSecondSchoolContainer.visibility = View.VISIBLE
                        } else if (i == 2) {
                            mThirdSchoolText.text = mSchools.get(i).getInstituteName() + " " + mSchools.get(i).getSchoolName()
                            mThirdSchoolContainer.visibility = View.VISIBLE
                        }
                    }
                }
                mContinueText.setText(resources.getString(R.string.ok_text))
            }
        }

        val user = getUser()
        if (user != null && !user.getCourse().equals("")) {
            requestGetSchools(user.getCourse())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (hasChanges) {
            ErrorDialog.newInstance("Seguro que quiere salir sin guardar cambios",
                    DialogType.ACCEPT_CANCEL_DIALOG, this)!!.show(supportFragmentManager, "warningDialog")
        } else {
            onBackPressed()
        }
        return true
    }

    override fun onBackPressed() {

        try {
            val intent = Intent()
            intent.putExtra(UPDATE_USER_SCHOOLS, true)
            setResult(UPDATE_USER_SCHOOLS_RESULT_CODE, intent)
            finish()

            super.onBackPressed()
        } catch (exception: Exception) {}
    }

    private val mDropFirstSchoolListener = View.OnClickListener {
        onDeleteSchool(0)

        if (!mSecondSchoolText.text.equals("") && !mThirdSchoolText.text.equals("")) {
            mFirstSchoolText.text = mSecondSchoolText.text
            mSecondSchoolText.text = mThirdSchoolText.text
            mThirdSchoolText.text = ""
            mThirdSchoolContainer.visibility = View.GONE
        } else if (!mSecondSchoolText.text.equals("") && mThirdSchoolText.text.equals("")) {
            mFirstSchoolText.text = mSecondSchoolText.text
            mSecondSchoolText.text = ""
            mSecondSchoolContainer.visibility = View.GONE
        } else {
            mFirstSchoolText.text = ""
            mFirstSchoolContainer.visibility = View.GONE
        }

        hasChanges = true
    }

    private val mDropSecondSchoolListener = View.OnClickListener {
        onDeleteSchool(1)

        if (!mThirdSchoolText.text.equals("")) {
            mSecondSchoolText.text = mThirdSchoolText.text
            mThirdSchoolText.text = ""
            mThirdSchoolContainer.visibility = View.GONE
        } else {
            mSecondSchoolText.text = ""
            mSecondSchoolContainer.visibility = View.GONE
        }

        hasChanges = true
    }

    private val mDropThridSchoolListener = View.OnClickListener {
        onDeleteSchool(2)
        mThirdSchoolText.text = ""
        mThirdSchoolContainer.visibility = View.GONE
        hasChanges = true
    }

    private val onSchoolClickListener = object : ExpandableListView.OnChildClickListener {
        override fun onChildClick(expandableListView: ExpandableListView?, view: View?, groupPosition: Int, childPosition: Int, l: Long): Boolean {
            val school = mSelectedSchoolsListAdapter.getChild(groupPosition, childPosition) as School
            val institute = mSelectedSchoolsListAdapter.getGroup(groupPosition) as Institute
            school.setInstituteId(institute.getInstituteId())
            school.setInstituteName(institute.getInstituteName())
            onSchoolSelected(school)
            hasChanges = true
            return false
        }
    }

    private fun onSchoolSelected(school: School) {
        var moreThanOne = false
        for (i in 0 .. mSchools.size - 1) {
            if (mSchools.get(i).getSchoolId().equals(school.getSchoolId()) &&
                    mSchools.get(i).getInstituteId().equals(school.getInstituteId())) {
                moreThanOne = true
                break
            }
        }
        if (mSchools.size < 3 && !moreThanOne) {
            if (mFirstSchoolText.text.equals("")) {
                mFirstSchoolText.text = school.getInstituteName() + " " + school.getSchoolName()
                mFirstSchoolContainer.visibility = View.VISIBLE
                mSchools.add(school)
            } else if (mSecondSchoolText.text.equals("")) {
                mSecondSchoolText.text = school.getInstituteName() + " " + school.getSchoolName()
                mSecondSchoolContainer.visibility = View.VISIBLE
                mSchools.add(school)
            } else if (mThirdSchoolText.text.equals("")) {
                mThirdSchoolText.text = school.getInstituteName() + " " + school.getSchoolName()
                mThirdSchoolContainer.visibility = View.VISIBLE
                mSchools.add(school)
            }
        }
    }

    private fun onDeleteSchool(position : Int) {
        try {
            mSchools.removeAt(position)
        }catch (e: java.lang.Exception) {
        } catch (e: kotlin.Exception) {}
    }

    private val mContinueListener = View.OnClickListener {
        if (NetworkUtil.isConnected(baseContext)) {
            if (mSchools.isNotEmpty()) {
                val user = getUser()
                if (user != null) {
                    requestSendSelectedSchools(user, mSchools)
                }

            } else {
                ErrorDialog.newInstance("Debes elegir por lo menos una escuela",
                        DialogType.OK_DIALOG, this)!!.show(supportFragmentManager, "warningDialog")
            }
        } else {
            if (mSchools.isNotEmpty()) {

                val user = getUser()
                if (user != null) {
                    requestSendSelectedSchools(user, mSchools)
                    user.setSelectedShools(mSchools)
                    saveUser(user)
                }
                if (showContinueButton) {
                    if (baseContext !=  null) {
                        DataHelper(baseContext!!).setisAfterLogIn(true)
                    }
                    goContentActivity()
                } else {
                    onBackPressed()
                }
            } else {
                ErrorDialog.newInstance("Debes elegir por lo menos una escuela",
                        DialogType.OK_DIALOG, this)!!.show(supportFragmentManager, "warningDialog")
            }
        }
    }

    private fun goContentActivity() {
        val intent = Intent(this, ContentActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    override fun onSendSelectedSchoolsSuccess(success: Boolean) {
        super.onSendSelectedSchoolsSuccess(success)

        if (this != null) {
            val user = getUser()
            if (user != null) {
                user.setSelectedShools(mSchools)
                saveUser(user)
            }

            if (showContinueButton) {
                if (baseContext !=  null) {
                    DataHelper(baseContext!!).setisAfterLogIn(true)
                }
                goContentActivity()
            } else {
                onBackPressed()
            }
        }
    }

    override fun onSendSelectedSchoolsFail(throwable: Throwable) {
        super.onSendSelectedSchoolsFail(throwable)
        if (this != null) {
            ErrorDialog.newInstance("Ocurrio un Error", "Vuelve a intentarlo",
                    DialogType.OK_DIALOG, this)!!.show(supportFragmentManager, "warningDialog")
        }
    }

    /*
     * Dialog listeners
     */
    override fun onConfirmationCancel() {

    }

    override fun onConfirmationNeutral() {

    }

    override fun onConfirmationAccept() {
        onBackPressed()
    }


    override fun onGetSchoolsSuccess(institutes: List<Institute>) {
        super.onGetSchoolsSuccess(institutes)

        if (institutes.isNotEmpty()) {
            mSelectedSchoolsListAdapter = SelectedSchoolsListAdapter(institutes, baseContext)
            mInstitutesSchoolList.setAdapter(mSelectedSchoolsListAdapter)
            mInstitutesSchoolList.setOnChildClickListener(onSchoolClickListener)
        }

    }

    override fun onGetSchoolsFail(throwable: Throwable) {
        super.onGetSchoolsFail(throwable)
    }

}