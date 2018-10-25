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

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.services.firebase.DownloadImages
import java.io.IOException
import android.widget.*
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment


/**
 * Created by Jorge Zepeda Tinoco on 23/09/18.
 * jorzet.94@gmail.com
 */

class CourseFragment : BaseContentFragment() {

    /*
     * UI accessors
     */
    private lateinit var courseContainer: RelativeLayout
    private lateinit var courseImageView: ImageView
    private lateinit var courseTextView: TextView
    private lateinit var loadingCourseProgressBar: ProgressBar

    /*
     *
     */
    private var hasDrawable : Boolean = false
    private lateinit var imageDrawable : Drawable
    private lateinit var courseImagePath: String
    private lateinit var courseText: String
    private var courseColor: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.course_fragment, container, false)

        courseContainer = rootView.findViewById(R.id.rl_course_container)
        courseImageView = rootView.findViewById(R.id.iv_course_icon)
        courseTextView = rootView.findViewById(R.id.tv_course_text)
        loadingCourseProgressBar = rootView.findViewById(R.id.pb_loading_course)

        if (hasDrawable) {
            courseImageView.setImageDrawable(imageDrawable)
        } else {
            downloadToImageView(courseImagePath)
        }

        courseTextView.text = courseText
        courseContainer.setBackgroundColor(courseColor)

        return rootView
    }

    fun setCourseImage(imageDrawable: Drawable) : CourseFragment {
        this.imageDrawable = imageDrawable
        hasDrawable = true
        return this
    }

    fun setCourseImagePath(imagePath: String) : CourseFragment {
        this.courseImagePath = imagePath
        hasDrawable = false
        return  this
    }

    fun setCourseText(courseText: String) : CourseFragment{
        this.courseText = courseText
        return  this
    }

    fun setCourseColor(courseColor: Int) : CourseFragment {
        this.courseColor = courseColor
        return  this
    }

    fun BuildCoure() {


    }

    private fun downloadToImageView(imageName : String) {
        val storage = FirebaseStorage.getInstance()
        Log.d(DownloadImages.TAG,"start download: "+ imageName)
        val fileRef = storage.getReference().child("ios/images/2x/${imageName}")
        if (fileRef != null) {
            try {
                val ONE_MEGABYTE : Long = 1024 * 1024;
                fileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    courseImageView.setImageBitmap(bmp)
                    loadingCourseProgressBar.visibility = View.GONE
                }.addOnFailureListener { exception ->
                    loadingCourseProgressBar.visibility = View.GONE

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
        }
    }

}