package com.zerebrez.zerebrez.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.utils.FontUtil
import android.content.Intent
import android.net.Uri


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