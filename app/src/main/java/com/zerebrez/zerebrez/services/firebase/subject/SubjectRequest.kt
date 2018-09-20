package com.zerebrez.zerebrez.services.firebase.subject

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.google.gson.Gson
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.SubjectRefactor
import com.zerebrez.zerebrez.services.firebase.Engagement
import org.json.JSONObject
import java.util.*

/**
 * Created by Jorge Zepeda Tinoco on 20/09/18.
 * jorzet.94@gmail.com
 */

class SubjectRequest(activity: Activity) : Engagement(activity) {

    private val TAG: String = "SubjectRequest"
    private val SUBJECT_REFERENCE: String = "subjects"

    private val mActivity: Activity = activity
    private lateinit var mFirebaseDatabase: DatabaseReference
    private var mFirebaseInstance: FirebaseDatabase

    init {
        mFirebaseInstance = FirebaseDatabase.getInstance()
    }

    fun requestGetSubjects() {
        mFirebaseDatabase = mFirebaseInstance.getReference(SUBJECT_REFERENCE)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as HashMap<*, *>)
                    Log.d(TAG, "user data ------ " + map.size)
                    val subjects = ArrayList<SubjectRefactor>()
                    for (key in map.keys) {
                        val subjectMap = map.get(key) as HashMap<*, *>
                        val subject = Gson().fromJson(JSONObject(subjectMap).toString(), SubjectRefactor::class.java)
                        subject.subjectId = key.toString()
                        subjects.add(subject)
                    }

                    Collections.sort(subjects, object : Comparator<SubjectRefactor> {
                        override fun compare(o1: SubjectRefactor, o2: SubjectRefactor): Int {
                            return extractInt(o1) - extractInt(o2)
                        }

                        fun extractInt(s: SubjectRefactor): Int {
                            val num = s.subjectId.replace("s", "").replace("S", "")
                            // return 0 if no digits found
                            return if (num.isEmpty()) 0 else Integer.parseInt(num)
                        }
                    })

                    Log.d(TAG, "subject data ------ ")
                    onRequestListenerSucces.onSuccess(subjects)
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

