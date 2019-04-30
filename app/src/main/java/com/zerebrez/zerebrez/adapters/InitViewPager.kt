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

package com.zerebrez.zerebrez.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.fragments.init.CourseFragment
import com.zerebrez.zerebrez.models.Course

/**
 * Created by Jorge Zepeda Tinoco on 12/03/18.
 * jorzet.94@gmail.com
 */

class InitViewPager(context: Context, fm: FragmentManager, courses : List<Course>) : FragmentStatePagerAdapter(fm) {

    private val mTotalPages : Int = courses.size
    private val mCourses : List<Course> = courses
    private val mContext: Context = context

    val colors = listOf(mContext.resources.getColor(R.color.comipems_background),
            mContext.resources.getColor(R.color.secundary_background),
            mContext.resources.getColor(R.color.unam_1_background),
            mContext.resources.getColor(R.color.unam_2_background),
            mContext.resources.getColor(R.color.ipn_1_background))

    override fun getItem(position: Int): Fragment {

        val fragment = CourseFragment()
        // get course by position
        val course = getCourseByPosition(position)

        if (course!= null) {
            fragment.setCourseImagePath(course.image)
                    .setCourseText(course.description)
                    .setCourseColor(colors[position])
                    .BuildCoure()
        } else {
            fragment.setCourseImage(mContext.resources.getDrawable(R.drawable.comipems_init_icon))
                    .setCourseText(mContext.resources.getString(R.string.comipems_text))
                    .setCourseColor(mContext.resources.getColor(R.color.comipems_background))
                    .BuildCoure()
        }
        return fragment
    }

    override fun getCount(): Int {
        var count = 0
        for (course in mCourses) {
            if (course.isActive) {
                count++
            }
        }
        return count
    }

    fun getCourseByPosition(position: Int) : Course? {
        if (mCourses!= null && mCourses.isNotEmpty() && mCourses.get(position).isActive) {
            return mCourses.get(position)
        } else {
            return null
        }
    }


}