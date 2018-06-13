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

package com.zerebrez.zerebrez.request

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.Image
import com.zerebrez.zerebrez.models.enums.ErrorType
import com.zerebrez.zerebrez.services.firebase.DownloadImages
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * Created by Jorge Zepeda Tinoco on 06/05/18.
 * jorzet.94@gmail.com
 */

class DownloadImageTask(context : Context): AbstractRequestTask<Any, Void, String>() {

    private val DOWNLOAD_COMPLETE : String = "download_complete"
    private val CANNOT_DOWNLOAD_CONTENT : String = "cannot_download_content"

    private var mProgress : Int = 0
    private var mDownloadComplete : Boolean = false
    private var mErrorOccurred : Boolean = false
    @SuppressLint("StaticFieldLeak")
    private var mContext : Context = context

    override fun onPreExecute() {
        super.onPreExecute()

        if (onRequestListenerSucces == null || onRequestLietenerFailed == null)
            return

    }

    override fun doInBackground(vararg images: Any): String? {

        val mImages = images[0] as List<Image>
        if (images.isNotEmpty()) {
            for (image in mImages) {

                if (image.isDownloadable()) {
                    downloadToLocalFile(image.getNameInStorage())
                    while (!mProgress.equals(100) && !mDownloadComplete || mErrorOccurred);
                    if (mErrorOccurred) {
                        Log.d(DownloadImages.TAG, "an error occurred while download image")
                        //val error = GenericError()
                        //error.setErrorType(ErrorType.USER_NOT_SENDED)
                        //onRequestLietenerFailed.onFailed(error)
                    }
                } else {

                }
            }
            return DOWNLOAD_COMPLETE
        }
        return CANNOT_DOWNLOAD_CONTENT
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)

        if (onRequestListenerSucces == null || onRequestLietenerFailed == null)
            return

        if (result.equals(DOWNLOAD_COMPLETE)) {
            onRequestListenerSucces.onSuccess(true)
        } else {
            val error = GenericError()
            error.setErrorType(ErrorType.CANNOT_DOWNLOAD_CONTENT)
            onRequestLietenerFailed.onFailed(error)
        }

    }

    private fun downloadToLocalFile(imageName : String) {
        val storage = FirebaseStorage.getInstance()
        Log.d(DownloadImages.TAG,"start download: "+ imageName)
        val fileRef = storage.getReference().child("ios/images/2x/${imageName}")
        if (fileRef != null) {
            try {
                val localFile: File = File.createTempFile("images", "jpg")

                fileRef.getFile(localFile)
                        .addOnSuccessListener {
                            Log.d(DownloadImages.TAG,"download complete")
                            val bmp = BitmapFactory.decodeFile(localFile.absolutePath)
                            // Assume block needs to be inside a Try/Catch block.
                            val path = Environment.getExternalStorageDirectory().toString()
                            var fOut: OutputStream? = null
                            //Create Folder
                            val folder = File(Environment.getExternalStorageDirectory().toString() + "/zerebrez/")
                            if (!folder.exists()) {
                                folder.mkdirs()
                            }
                            Log.d(DownloadImages.TAG,"saving image: ${imageName}")
                            val file = File(path + "/zerebrez/", imageName) // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                            if (!file.exists()) {
                                fOut = FileOutputStream(file)

                                val pictureBitmap = bmp // obtaining the Bitmap
                                pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut) // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                                fOut.flush() // Not really required
                                fOut.close() // do not forget to close the stream

                                MediaStore.Images.Media.insertImage(mContext.contentResolver, file.getAbsolutePath(), file.getName(), file.getName())

                                Log.d(DownloadImages.TAG, "image ${imageName} saved")
                                mDownloadComplete = true
                                mErrorOccurred = false
                            }
                        }
                        .addOnFailureListener { exception ->
                            mErrorOccurred = true
                            Log.d(DownloadImages.TAG, "an error occurred while downliading images: " + exception.stackTrace)
                            exception.printStackTrace()
                        }
                        .addOnProgressListener { taskSnapshot ->
                            // progress percentage
                            val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                            Log.d(DownloadImages.TAG, "download in progress, percentage: ${progress}%")
                            // percentage in progress
                            mProgress = progress.toInt()
                            mErrorOccurred = false
                        }
            } catch (e: IOException) {
                e.printStackTrace()
                mErrorOccurred = true
            }

        } else {
            mErrorOccurred = true
        }
    }
}