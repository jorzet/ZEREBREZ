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

package com.zerebrez.zerebrez.services.firebase.courses

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.google.gson.Gson
import com.zerebrez.zerebrez.models.Course
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.services.firebase.Engagement
import org.json.JSONObject
import java.util.*

/**
 * Created by Jorge Zepeda Tinoco on 19/09/18.
 * jorzet.94@gmail.com
 */

class CourseRequest(activity: Activity) : Engagement(activity) {

    /*
     * Tags
     */
    private val TAG : String = "CourseRequest"

    /*
     * Labels to be replace
     */
    private val COURSE_LABEL : String = "course_label"

    /*
     * Node references
     */
    private val COURSES_REFERENCE : String = "courses"
    private val PRICE_REFERENCE : String = "price/course_label/android"

    /*
     * Database object
     */
    private lateinit var mFirebaseDatabase: DatabaseReference

    fun requestGetCourses() {
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.SETTINGS_DATABASE_REFERENCE)
                .getReference(COURSES_REFERENCE)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as kotlin.collections.HashMap<*, *>)
                    Log.d(TAG, "user data ------ " + map.size)
                    val courses = ArrayList<Course>()
                    for (key in map.keys) {
                        val courseMap = map.get(key) as kotlin.collections.HashMap<*, *>
                        val course = Gson().fromJson(JSONObject(courseMap).toString(), Course::class.java)
                        course.courseId = key.toString()
                        courses.add(course)
                    }

                    /*Collections.sort(courses, object : Comparator<Course> {
                        override fun compare(o1: Course, o2: Course): Int {
                            return extractInt(o1) - extractInt(o2)
                        }

                        fun extractInt(s: Course): Int {
                            val num = s.courseId.replace("c","").replace("C","")
                            // return 0 if no digits found
                            return if (num.isEmpty()) 0 else Integer.parseInt(num)
                        }
                    })*/

                    Log.d(TAG, "courses data ------ " )
                    onRequestListenerSucces.onSuccess(courses)
                } else {
                    val error = GenericError()
                    onRequestLietenerFailed.onFailed(error)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                onRequestLietenerFailed.onFailed(databaseError.toException())
            }
        })
    }

    fun requestGetCoursePrice(course: String) {
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.SETTINGS_DATABASE_REFERENCE)
                .getReference(PRICE_REFERENCE.replace(COURSE_LABEL, course))

        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val price = (post as Long).toString()
                    Log.d(TAG, "price data ------ " )
                    onRequestListenerSucces.onSuccess(price)
                } else {
                    val error = GenericError()
                    onRequestLietenerFailed.onFailed(error)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                onRequestLietenerFailed.onFailed(databaseError.toException())
            }
        })
    }

}