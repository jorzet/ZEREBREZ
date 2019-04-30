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

package com.zerebrez.zerebrez.fragments.login

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.fragments.init.InitFragment
import com.zerebrez.zerebrez.fragments.init.InitFragmentRefactor
import com.zerebrez.zerebrez.models.Image
import com.zerebrez.zerebrez.models.Module
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.ui.activities.LoginActivity
import com.zerebrez.zerebrez.ui.dialogs.ErrorDialog
import com.zerebrez.zerebrez.utils.FontUtil
import com.zerebrez.zerebrez.utils.NetworkUtil

/**
 * Created by Jorge Zepeda Tinoco on 12/03/18.
 * jorzet.94@gmail.com
 */

class StartFragment : BaseContentFragment(), ErrorDialog.OnErrorDialogListener {

    private lateinit var mStartButton : Button
    private lateinit var mGoLoginButton : View
    private lateinit var mButtonsContainer : View
    private lateinit var mLoadingProgressBar : ProgressBar
    private lateinit var mTextBetweenLines : TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.start_fragment, container, false)!!

        mStartButton = rootView.findViewById(R.id.btn_start)
        mGoLoginButton = rootView.findViewById(R.id.has_account)
        mButtonsContainer = rootView.findViewById(R.id.rl_buttons_container)
        mLoadingProgressBar = rootView.findViewById(R.id.pb_loading)
        mTextBetweenLines = rootView.findViewById(R.id.text_between_lines)

        mTextBetweenLines.setTypeface(FontUtil.getNunitoBold(context!!))
        mStartButton.setTypeface(FontUtil.getNunitoRegular(context!!))

        mStartButton.setOnClickListener(mStartButtonListener)
        mGoLoginButton.setOnClickListener(mGoLogInButtonListener)

        return rootView
    }

    private val mStartButtonListener : View.OnClickListener = View.OnClickListener {
        goInitFragment()
        /*if (NetworkUtil.isConnected(context!!)) {

            //mButtonsContainer.visibility = View.GONE
            //mLoadingProgressBar.visibility = View.VISIBLE

            //requestLogIn(null)
        } else {
            ErrorDialog.newInstance("Error", "Necesitas tener conexión a intenet para poder iniciar",
                    DialogType.OK_DIALOG, this)!!.show(fragmentManager!!, "networkError")
        }*/
    }

    private val mGoLogInButtonListener : View.OnClickListener = View.OnClickListener {
        goSingInFragment()
    }


    override fun onGetModulesSucces(result: List<Module>) {
        super.onGetModulesSucces(result)
        val user = getUser()
        if (user != null && !user.getCourse().equals("")) {
            requestGetImagesPath(user.getCourse())
        }
        //goInitFragment()
    }

    override fun onGetModulesFail(throwable: Throwable) {
        super.onGetModulesFail(throwable)
        mButtonsContainer.visibility = View.VISIBLE
        mLoadingProgressBar.visibility = View.GONE
    }

    override fun onGetImagesPathSuccess(images: List<Image>) {
        super.onGetImagesPathSuccess(images)

        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(true)
            dataHelper.saveImagesPath(images)
            goInitFragment()
            //if (!dataHelper.areImagesDownloaded()) {
                //(activity as LoginActivity).startDownloadImages()
            //}
        }

    }

    override fun onGetImagesPathFail(throwable: Throwable) {
        super.onGetImagesPathFail(throwable)
        mButtonsContainer.visibility = View.VISIBLE
        mLoadingProgressBar.visibility = View.GONE
    }

    private fun goInitFragment() {
        val manager = getFragmentManager();
        val transaction = manager!!.beginTransaction();
        //transaction.replace(R.id.fragment_container, InitFragment())
        transaction.replace(R.id.fragment_container, InitFragmentRefactor())
        transaction.commit();
    }

    private fun goSingInFragment() {
        val manager = getFragmentManager();
        val transaction = manager!!.beginTransaction();
        transaction.replace(R.id.fragment_container, SignInFragment())
        transaction.commit();
    }


    /*
     * Dialog listeners
     */
    override fun onConfirmationCancel() {

    }

    override fun onConfirmationNeutral() {

    }

    override fun onConfirmationAccept() {

    }


}