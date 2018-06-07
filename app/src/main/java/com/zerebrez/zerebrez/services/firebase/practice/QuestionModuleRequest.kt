package com.zerebrez.zerebrez.services.firebase.practice

import android.app.Activity
import android.util.Log
import com.google.firebase.database.*
import com.zerebrez.zerebrez.models.Module
import com.zerebrez.zerebrez.models.Question
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.services.firebase.Engagement
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager
import java.util.*

private const val TAG: String = "QuestionModuleRequest"

class QuestionModuleRequest(activity: Activity) : Engagement(activity) {

    private val FREE_MODULES_REFERENCE : String = "freeUser/comipems/modules"
    private val MODULES_REFERENCE : String = "modules/comipems"
    private val USERS_REFERENCE : String = "users"
    private val PROFILE_REFERENCE : String = "profile"
    private val ANSWERED_MODULED_REFERENCE : String = "answeredModules"

    private val IS_PREMIUM_KEY : String = "isPremium"
    private val TIMESTAMP_KEY : String = "timeStamp"
    private val PREMIUM_KEY : String = "premium"

    private val mActivity : Activity = activity
    private lateinit var mFirebaseDatabase: DatabaseReference
    private var mFirebaseInstance: FirebaseDatabase

    init {
        mFirebaseInstance = FirebaseDatabase.getInstance()
        if (!SharedPreferencesManager(mActivity).isPersistanceData()) {
            mFirebaseInstance.setPersistenceEnabled(true)
            SharedPreferencesManager(mActivity).setPersistanceDataEnable(true)
        }
    }

    fun requestGetFreeModulesRefactor() {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(FREE_MODULES_REFERENCE)
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                val list = (post as List<String>)

                Log.d(TAG, post.toString())

                val mFreeModuleList = arrayListOf<Module>()

                for (m in list) {
                    val module = Module()
                    module.setId(Integer(m.replace("m", "")))
                    mFreeModuleList.add(module)
                }

                onRequestListenerSucces.onSuccess(mFreeModuleList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                onRequestLietenerFailed.onFailed(databaseError.toException())
            }
        })
    }

    fun requestGetModulesRefactor() {
        // Get a reference to our posts
        mFirebaseDatabase = mFirebaseInstance.getReference(MODULES_REFERENCE)
        mFirebaseDatabase.keepSynced(true)
        // Attach a listener to read the data at our posts reference
        mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val post = dataSnapshot.getValue()
                val map = (post as HashMap<String, String>)
                val mModules = arrayListOf<Module>()

                Log.d(TAG, post.toString())

                /*
                 * mapping map to module object
                 */
                for ( key in map.keys) {
                    println(key)
                    val module = Module()
                    val questions = arrayListOf<Question>()

                    // get question id from response
                    val list = map.get(key) as List<String>
                    for (q in list) {
                        try {
                            val question = Question()
                            question.setQuestionId(Integer(q.replace("p","")))
                            question.setModuleId(Integer(key.replace("m","")))
                            questions.add(question)
                        } catch (exception : Exception) {}
                    }

                    // set module id and question id
                    module.setId(Integer(key.replace("m","")))
                    module.setQuestions(questions)

                    // add module to list
                    mModules.add(module)
                }

                /*
                  * sort module list because service doesn't return it in order
                  */
                Collections.sort(mModules, object : Comparator<Module> {
                    override fun compare(o1: Module, o2: Module): Int {
                        return extractInt(o1) - extractInt(o2)
                    }

                    internal fun extractInt(s: Module): Int {
                        val num = s.getId().toString()
                        // return 0 if no digits found
                        return if (num.isEmpty()) 0 else Integer.parseInt(num)
                    }
                })

                onRequestListenerSucces.onSuccess(mModules)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
                //onRequestLietenerFailed.onFailed(databaseError.toException())
            }
        })
    }

    fun requestGetProfileUserRefactor() {
        // Get a reference to our posts
        val user = getCurrentUser()
        if (user != null) {
            mFirebaseDatabase = mFirebaseInstance.getReference(USERS_REFERENCE + "/" + user.uid)
            mFirebaseDatabase.keepSynced(true)

            // Attach a listener to read the data at our posts reference
            mFirebaseDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    val post = dataSnapshot.getValue()
                    val map = (post as HashMap<String, String>)
                    Log.d(TAG, "user data ------ " + map.size)

                    val user = User()
                    for ( key in map.keys) {
                        println(key)
                        if (key.equals(PROFILE_REFERENCE)) {
                            val profile = map.get(key) as HashMap<String, String>
                            val premiumHash = profile.get(PREMIUM_KEY) as HashMap<String, String>
                            for (key4 in profile.keys) {
                                if (key4.equals(IS_PREMIUM_KEY)) {
                                    val isPremium = premiumHash.get(key4) as Boolean
                                    user.setPremiumUser(isPremium)
                                } else if (key4.equals(TIMESTAMP_KEY)) {
                                    val timeStamp = premiumHash.get(key4) as String
                                    user.setTimeStamp(timeStamp)
                                }
                            }

                        } else if (key.equals(ANSWERED_MODULED_REFERENCE)) {
                            val answeredModules = map.get(key) as HashMap<String, String>
                            val modules = arrayListOf<Module>()

                            for (key2 in answeredModules.keys) {
                                val moduleAnswered = answeredModules.get(key2) as HashMap<String, String>
                                val module = Module()
                                module.setId(Integer(key2.replace("m","")))

                                for (key3 in moduleAnswered.keys) {
                                    if (key3.equals("incorrect")) {
                                        val incorrectQuestions = (moduleAnswered.get(key3) as java.lang.Long).toInt()
                                        module.setIncorrectQuestions(incorrectQuestions)
                                    } else if (key3.equals("correct")) {
                                        val correctQuestions = (moduleAnswered.get(key3) as java.lang.Long).toInt()
                                        module.setCorrectQuestions(correctQuestions)
                                    }
                                }

                                modules.add(module)
                            }
                            user.setAnsweredModules(modules)
                        }
                    }
                    Log.d(TAG, "user data ------ " + user.getUUID())
                    onRequestListenerSucces.onSuccess(user)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("The read failed: " + databaseError.code)
                    onRequestLietenerFailed.onFailed(databaseError.toException())
                }
            })
        }
    }
}