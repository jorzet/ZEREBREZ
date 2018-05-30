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

package com.zerebrez.zerebrez.services.firebase

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.zerebrez.zerebrez.models.Image
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.request.AbstractRequestTask
import com.zerebrez.zerebrez.request.DownloadImageTask

/**
 * Created by Jorge Zepeda Tinoco on 28/05/18.
 * jorzet.94@gmail.com
 */

class DownloadImages: Service() {

    companion object {
        const val TAG : String = "DownloadImages"
        const val DOWNLOAD_IMAGES_BR = "com.zerebrez.zerebrez.services.firebase.DownloadImages"
        const val DOWNLOAD_COMPLETE = "download_complete"
    }


    var bi = Intent(DOWNLOAD_IMAGES_BR)

    private lateinit var mImages : List<Image>

    override fun onCreate() {
        super.onCreate()

        // get images from data base
        val dataHelper = DataHelper(this)
        mImages = dataHelper.getImagesPath()

        // instance download image task and set listeners
        val downloadImageTask = DownloadImageTask(this)

        downloadImageTask.setOnRequestSuccess(object : AbstractRequestTask.OnRequestListenerSuccess {
            override fun onSuccess(result: Any) {
                bi.putExtra(DOWNLOAD_COMPLETE, true)
                sendBroadcast(bi)
            }
        })

        downloadImageTask.setOnRequestFailed(object : AbstractRequestTask.OnRequestListenerFailed {
            override fun onFailed(result: Throwable) {
                bi.putExtra(DOWNLOAD_COMPLETE, false)
                sendBroadcast(bi)
            }
        })

        downloadImageTask.execute(mImages)

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

}