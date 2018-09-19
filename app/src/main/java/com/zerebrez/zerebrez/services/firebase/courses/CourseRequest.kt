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

class CourseRequest(activity: Activity) : Engagement(activity) {

    private val TAG : String = "CourseRequest"
    private val COURSES_REFERENCE : String = "courses"

    private val mActivity : Activity = activity
    private lateinit var mFirebaseDatabase: DatabaseReference
    private var mFirebaseInstance: FirebaseDatabase

    init {
        mFirebaseInstance = FirebaseDatabase.getInstance()
    }

    fun requestGetCourses() {
        mFirebaseDatabase = mFirebaseInstance.getReference(COURSES_REFERENCE)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as HashMap<*, *>)
                    Log.d(TAG, "user data ------ " + map.size)
                    val courses = ArrayList<Course>()
                    for (key in map.keys) {
                        val courseMap = map.get(key) as HashMap<*, *>
                        val course = Gson().fromJson(JSONObject(courseMap).toString(), Course::class.java)
                        course.courseId = key.toString()
                        courses.add(course)
                    }

                    Collections.sort(courses, object : Comparator<Course> {
                        override fun compare(o1: Course, o2: Course): Int {
                            return extractInt(o1) - extractInt(o2)
                        }

                        fun extractInt(s: Course): Int {
                            val num = s.courseId.replace("c","").replace("C","")
                            // return 0 if no digits found
                            return if (num.isEmpty()) 0 else Integer.parseInt(num)
                        }
                    })

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

}