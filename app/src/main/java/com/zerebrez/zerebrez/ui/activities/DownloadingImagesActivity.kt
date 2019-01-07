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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.enums.ErrorType
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.services.firebase.DownloadImages

/**
 * Created by Jorge Zepeda Tinoco on 04/01/19.
 * jorzet.94@gmail.com
 */

class DownloadingImagesActivity: BaseActivityLifeCycle() {
    /*
     * Tags
     */
    private val TAG : String = "DownloadingImages"

    /*
     * UI accessors
     */
    private lateinit var mDownloadingImageProgressBar : ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloading_images)

        mDownloadingImageProgressBar = findViewById(R.id.pb_downloading_images)

        startReceiver()
    }

    private val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent != null && intent.getExtras() != null) {
                if (intent.getBooleanExtra(DownloadImages.DOWNLOAD_COMPLETE,false)) {
                    stopReceiver()
                    finishActivity()
                } else {

                    if (intent.getBooleanExtra(DownloadImages.DOWNLOAD_ERROR,false)) {
                        val errorType = intent.getSerializableExtra(DownloadImages.ERROR_WHEN_DOWNLOAD)
                        if (errorType.equals(ErrorType.CANNOT_DOWNLOAD_CONTENT_FILE_NOT_FOUND)) {
                            stopReceiver()
                            finishActivity()
                        }
                    } else {

                        val downloadProgress = intent.extras.getInt(DownloadImages.DOWNLOAD_PROGRESS)
                        Log.i(TAG, "Downloading ..." + downloadProgress)

                        if (::mDownloadingImageProgressBar.isInitialized && mDownloadingImageProgressBar != null) {
                            mDownloadingImageProgressBar.progress = downloadProgress
                        }
                    }
                }
            }
        }
    }

    /*
     * Finish the activity from BroadcastReceiver context
     */
    fun finishActivity() {
        this.finish()
    }

    /*
     * This method just register the BroadcastReceiver
     */
    private fun startReceiver() {
        try {
            if (br != null) {
                this.registerReceiver(br, IntentFilter(DownloadImages.DOWNLOAD_IMAGES_BR))
            }
        } catch (e: java.lang.Exception) {
        } catch (e: kotlin.Exception) {}
    }

    /*
     * this method unregister the BroadcastReceiver and change flag to identify
     * that all images are downloaded
     */
    private fun stopReceiver() {
        try {
            if (br != null) {
                this.unregisterReceiver(br)
            }
        } catch (e: java.lang.Exception) {
        } catch (e: kotlin.Exception) {}

        val dataHelper = DataHelper(this)
        dataHelper.setImagesDownloaded(true)
    }

    /*
     * not call onBackPressed method
     */
    override fun onBackPressed() {
        //super.onBackPressed()
    }
}