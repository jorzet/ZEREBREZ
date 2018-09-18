package com.zerebrez.zerebrez.services.firebase.courses

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.google.gson.Gson
import com.zerebrez.zerebrez.models.Course
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.services.firebase.Engagement
import org.json.JSONObject

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
                        val course = Gson().fromJson(JSONObject(map).toString(), Course::class.java)
                        courses.add(course)
                    }

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