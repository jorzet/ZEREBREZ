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

package com.zerebrez.zerebrez.services.firebase

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.zerebrez.zerebrez.models.Image
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.request.AbstractRequestTask
import com.zerebrez.zerebrez.request.DownloadImageTask
import com.zerebrez.zerebrez.services.sharedpreferences.JsonParcer
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager

/**
 * Created by Jorge Zepeda Tinoco on 28/05/18.
 * jorzet.94@gmail.com
 */

class DownloadImages: Service() {

    /*
     * Tags
     */
    companion object {
        const val TAG : String = "DownloadImages"
        const val DOWNLOAD_IMAGES_BR = "com.zerebrez.zerebrez.services.firebase.DownloadImages"
        const val DOWNLOAD_COMPLETE = "download_complete"
        const val DOWNLOAD_PROGRESS = "download_progress"
    }

    /*
     * Broadcast intent
     */
    var bi = Intent(DOWNLOAD_IMAGES_BR)

    /*
     * images list path
     */
    private lateinit var mImages : List<Image>

    /*
     * Variable
     */
    private var downloadProgress = 0
    private var downloadCount = 0
    private var downloadWithError = false

    override fun onCreate() {
        super.onCreate()

        // get images from data base
        try {
            val dataHelper = DataHelper(this)
            mImages = dataHelper.getImagesPath()

            val user = getUser()
            // check if user is not null and has course
            if (user != null && !user.getCourse().equals("")) {
                // instance download image task and set listeners
                val downloadImageTask = DownloadImageTask(this, user.getCourse())

                downloadImageTask.setOnRequestSuccess(object : AbstractRequestTask.OnRequestListenerSuccess {
                    override fun onSuccess(result: Any) {
                        //bi.putExtra(DOWNLOAD_COMPLETE, true)
                        //sendBroadcast(bi)
                    }
                })

                downloadImageTask.setOnRequestFailed(object : AbstractRequestTask.OnRequestListenerFailed {
                    override fun onFailed(result: Throwable) {
                        //bi.putExtra(DOWNLOAD_COMPLETE, false)
                        //sendBroadcast(bi)
                    }
                })

                downloadImageTask.setOnDownloadStatus(object: AbstractRequestTask.OnDownloadStatusListener {
                    override fun onImageDownloaded() {
                        downloadCount++
                        downloadProgress = (downloadCount * 100) / mImages.size
                        bi.putExtra(DOWNLOAD_PROGRESS, downloadProgress)
                        sendBroadcast(bi)
                        if (downloadCount == mImages.size - 1) {
                            bi.putExtra(DOWNLOAD_COMPLETE, true)
                            sendBroadcast(bi)
                        }
                    }
                    override fun onImagesError() {
                        downloadCount++
                        downloadProgress = (downloadCount * 100) / mImages.size

                        downloadWithError = true

                        bi.putExtra(DOWNLOAD_PROGRESS, downloadProgress)
                        sendBroadcast(bi)

                        if (downloadCount == mImages.size - 1) {
                            bi.putExtra(DOWNLOAD_COMPLETE, false)
                            sendBroadcast(bi)
                        }
                    }
                })

                downloadImageTask.execute(mImages)
            }
        } catch (exception : Exception) {}

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    /*
     * This method returns current user
     */
    fun getUser() : User? {
        val json = SharedPreferencesManager(this).getJsonUser()
        if (json != null) {
            return JsonParcer.getObjectFromJson(json, User::class.java) as User
        } else {
            return null
        }
    }

}