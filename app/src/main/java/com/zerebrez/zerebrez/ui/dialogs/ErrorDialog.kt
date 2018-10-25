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

package com.zerebrez.zerebrez.ui.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.enums.DialogType
import com.zerebrez.zerebrez.models.enums.ErrorType

/**
 * Created by Jorge Zepeda Tinoco on 12/03/18.
 * jorzet.94@gmail.com
 */

class ErrorDialog : DialogFragment(){

    /*
     * UI accessors
     */
    private lateinit var mTitleTextView : TextView
    private lateinit var mMessageTextView : TextView
    private lateinit var mOKButton : View
    private lateinit var mYesButton : View
    private lateinit var mNotButton : View

    /*
     * Objects
     */
    private var mOnErrorDialogListener : OnErrorDialogListener? = null
    private lateinit var mErrorType: ErrorType

    /*
     * interface to set listeners
     */
    interface OnErrorDialogListener {
        fun onConfirmationCancel()
        fun onConfirmationNeutral()
        fun onConfirmationAccept()
    }

    /*
     * static method to create error dialog
     */
    companion object {
        /*
         * Tags
         */
        private val DIALOG_TITLE : String = "main_title"
        private val DIALOG_MESSAGE : String = "secondary_title"
        private val DIALOG_TYPE : String = "dialog_type"
        private val ARG_IS_LISTENER_ACTIVITY : String = "arg_is_listener_activity"

        fun newInstance(title : String?, message : String?, dialogType: DialogType,
                        onErrorDialogListener: OnErrorDialogListener) : ErrorDialog? {

            val errorDialog = ErrorDialog()
            val args = Bundle()

            args.putString(DIALOG_TITLE, title)
            args.putString(DIALOG_MESSAGE, message)
            args.putString(DIALOG_TYPE, dialogType.toString())

            if (onErrorDialogListener is Activity) {
                args.putBoolean(ARG_IS_LISTENER_ACTIVITY, true)
            } else {
                args.putBoolean(ARG_IS_LISTENER_ACTIVITY, false)
                errorDialog.setTargetFragment(onErrorDialogListener as Fragment, 0)
            }

            errorDialog.arguments = args
            return errorDialog
        }

        fun newInstance(title : String?, dialogType: DialogType,
                        onErrorDialogListener: OnErrorDialogListener) : ErrorDialog? {
            val errorDialog = ErrorDialog()
            val args = Bundle()

            args.putString(DIALOG_TITLE, title)
            args.putString(DIALOG_TYPE, dialogType.toString())

            if (onErrorDialogListener is Activity) {
                args.putBoolean(ARG_IS_LISTENER_ACTIVITY, true)
            } else {
                args.putBoolean(ARG_IS_LISTENER_ACTIVITY, false)
                errorDialog.setTargetFragment(onErrorDialogListener as Fragment, 0)
            }

            errorDialog.arguments = args
            return errorDialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity)
        val rootView = onCreateView(inflater, null, savedInstanceState)

        val dialog = Dialog(activity, R.style.AppDialog)
        dialog.setContentView(rootView)

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView : View?

        if (arguments!!.getString(DIALOG_TYPE).equals(DialogType.OK_DIALOG.toString())) {
            rootView = inflater.inflate(R.layout.custom_ok_error_dialog, container, false)
            initOKDialog(rootView)
        } else {
            rootView = inflater.inflate(R.layout.custom_yes_not_error_dialog, container, false)
            initYesNotDialog(rootView)
        }

        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (arguments!!.getBoolean(ARG_IS_LISTENER_ACTIVITY)) {
            mOnErrorDialogListener = activity as OnErrorDialogListener;
        } else {
            mOnErrorDialogListener = targetFragment as OnErrorDialogListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        mOnErrorDialogListener = null
    }

    private fun initOKDialog(rootView: View) {
        mOKButton = rootView.findViewById(R.id.rl_ok_container)
        mTitleTextView = rootView.findViewById(R.id.tv_dialog_title)
        mMessageTextView = rootView.findViewById(R.id.tv_dialog_message)

        mTitleTextView.text = arguments!!.getString(DIALOG_TITLE)

        if (!arguments!!.containsKey(DIALOG_MESSAGE)) {
            mMessageTextView.visibility = View.GONE
        } else {
            mMessageTextView.text = arguments!!.getString(DIALOG_MESSAGE)
        }

        mOKButton.setOnClickListener(mOKButtonListener)
    }

    private fun initYesNotDialog(rootView: View) {
        mYesButton = rootView.findViewById(R.id.rl_yes_container)
        mNotButton = rootView.findViewById(R.id.rl_not_container)
        mTitleTextView = rootView.findViewById(R.id.tv_dialog_title)
        mMessageTextView = rootView.findViewById(R.id.tv_dialog_message)

        mTitleTextView.text = arguments!!.getString(DIALOG_TITLE)

        if (!arguments!!.containsKey(DIALOG_MESSAGE)) {
            mMessageTextView.visibility = View.GONE
        } else {
            mMessageTextView.text = arguments!!.getString(DIALOG_MESSAGE)
        }

        mYesButton.setOnClickListener(mYesButtonListener)
        mNotButton.setOnClickListener(mNotButtonListener)
    }

    private val mOKButtonListener = View.OnClickListener {
        if (mOnErrorDialogListener != null) {
            mOnErrorDialogListener!!.onConfirmationNeutral()
        }
        dismiss()
    }

    override fun show(fragmentManager : FragmentManager, tag : String) {
        if (context != null) {
            super.show(fragmentManager, tag)
        }
    }

    private val mYesButtonListener = View.OnClickListener {
        if (mOnErrorDialogListener != null) {
            mOnErrorDialogListener!!.onConfirmationAccept()
        }
        dismiss()
    }

    private val mNotButtonListener = View.OnClickListener {
        if (mOnErrorDialogListener != null) {
            mOnErrorDialogListener!!.onConfirmationCancel()
        }
        dismiss()
    }

}