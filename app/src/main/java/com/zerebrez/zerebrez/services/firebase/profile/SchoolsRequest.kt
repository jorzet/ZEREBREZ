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

package com.zerebrez.zerebrez.services.firebase.profile

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.zerebrez.zerebrez.models.Error.GenericError
import com.zerebrez.zerebrez.models.Institute
import com.zerebrez.zerebrez.models.School
import com.zerebrez.zerebrez.services.firebase.Engagement
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager

/**
 * Created by Jorge Zepeda Tinoco on 03/06/18.
 * jorzet.94@gmail.com
 */

private const val TAG: String = "SchoolsRequest"

class SchoolsRequest(activity: Activity) : Engagement(activity) {

    /*
     * Tags
     */
    private val INSTITUTE_TAG : String = "institute"
    private val SCHOOL_TAG : String = "school"

    /*
     * Labels to be replaced
     */
    private val COURSE_LABEL : String = "course_label"

    /*
     * Node references
     */
    private val INSTITUTES_REFERENCE : String = "schools/course_label"

    /*
     * Json keys
     */
    private val INSTITUTE_NAME_KEY : String = "name"
    private val SCHOOL_NAME_KEY : String = "name"
    private val SCHOOL_SCORE_KEY : String = "score"

    /*
     * Database object
     */
    private lateinit var mFirebaseDatabase: DatabaseReference

    fun requestGetSchools(course: String) {
        // Get a reference to our posts
        mFirebaseDatabase = FirebaseDatabase
                .getInstance(Engagement.SETTINGS_DATABASE_REFERENCE)
                .getReference(INSTITUTES_REFERENCE.replace(COURSE_LABEL, course))

        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                if (post != null) {
                    val map = (post as HashMap<String, HashMap<Any, Any>>)
                    val mInstitutes = arrayListOf<Institute>()

                    Log.d(TAG, post.toString())

                    for (key in map.keys) {
                        println(key)
                        val institute = Institute()
                        institute.setInstituteId(Integer(key.replace(INSTITUTE_TAG, "")))
                        val instituteHash = map.get(key) as HashMap<String, String>
                        for (key2 in instituteHash.keys) {
                            if (key2.equals("schoolsList")) {
                                val schools = arrayListOf<School>()
                                val schoolsHash = instituteHash.get(key2) as HashMap<String, String>
                                for (key3 in schoolsHash.keys) {
                                    val school = School()
                                    school.setSchoolId(Integer(key3.replace(SCHOOL_TAG, "")))

                                    val schoolDataHash = schoolsHash.get(key3) as HashMap<String, String>
                                    for (key4 in schoolDataHash.keys) {
                                        if (key4.equals(SCHOOL_NAME_KEY)) {
                                            school.setSchoolName(schoolDataHash.get(key4).toString())
                                        } else if (key4.equals(SCHOOL_SCORE_KEY)) {
                                            school.setHitsNumber((schoolDataHash.get(key4) as java.lang.Long).toInt())
                                        }
                                    }
                                    schools.add(school)
                                }
                                institute.setSchools(schools)
                            } else if (key2.equals(INSTITUTE_NAME_KEY)) {
                                institute.setInstituteName(instituteHash.get(key2).toString())
                            }
                        }
                        mInstitutes.add(institute)
                    }

                    onRequestListenerSucces.onSuccess(mInstitutes)
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