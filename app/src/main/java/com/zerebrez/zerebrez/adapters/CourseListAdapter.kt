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

package com.zerebrez.zerebrez.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import com.google.firebase.storage.FirebaseStorage
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.Course
import com.zerebrez.zerebrez.models.Exam
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.services.firebase.DownloadImages
import com.zerebrez.zerebrez.utils.ColorUtils
import com.zerebrez.zerebrez.utils.FontUtil
import kotlinx.android.synthetic.main.course_item.view.*
import kotlinx.android.synthetic.main.custom_option_exam.view.*
import java.io.IOException

/**
 * Created by Jorge Zepeda Tinoco on 14/10/18.
 * jorzet.94@gmail.com
 */

class CourseListAdapter (context : Context, courses : List<Course>) : BaseAdapter() {

    private val mContext: Context = context
    private val mCourses: List<Course> = courses

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val currentCourse = getItem(position) as Course

        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val courseView = inflator.inflate(R.layout.course_item, null)

        if (currentCourse != null) {

            // set course name
            courseView.tv_course_text.text = currentCourse.description
            courseView.tv_course_text.typeface = FontUtil.getNunitoSemiBold(mContext)

            // set background color course
            val color = ColorUtils.mCourseColors[position%ColorUtils.mCourseColors.size]
            courseView.rl_course_container.setBackgroundColor(mContext.resources.getColor(color))

            // download and set course image
            downloadToImageView(currentCourse.image, courseView.iv_course_icon, courseView.pb_loading_course)

        }
        return courseView
    }

    override fun getItem(position: Int): Any? {
        //if (mCourses!= null && mCourses.isNotEmpty() && mCourses.get(position).isActive) {
        if (mCourses!= null && mCourses.isNotEmpty()) {
            return mCourses.get(position)
        } else {
            return null
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        var count = 0
        for (course in mCourses) {
            //if (course.isActive) {
                count++
            //}
        }
        return count
    }

    private fun downloadToImageView(imageName : String, courseImageView : ImageView, loadingCourseProgressBar : ProgressBar) {
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
