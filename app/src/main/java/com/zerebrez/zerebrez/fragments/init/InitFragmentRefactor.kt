package com.zerebrez.zerebrez.fragments.init

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.CourseListAdapter
import com.zerebrez.zerebrez.fragments.content.BaseContentFragment
import com.zerebrez.zerebrez.models.Course
import com.zerebrez.zerebrez.models.Image
import com.zerebrez.zerebrez.models.User
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.ui.activities.QuestionActivity

class InitFragmentRefactor : BaseContentFragment(), AdapterView.OnItemClickListener {

    /*
     * Tags
     */
    private val TAG : String = "InitFragmentRefactor"
    private var CURRENT_COURSE : String = "current_course"
    private val MODULE_ID = "module_id"
    private val ANONYMOUS_USER = "anonymous_user"

    /*
     * UI accessors
     */
    private lateinit var mCloseCourseSelection : View
    private lateinit var mCourseListView : ListView
    private lateinit var mLoadingCourses : ProgressBar

    /*
     * Objects
     */
    private var mCourses : List<Course> = arrayListOf<Course>()

    /*
     * Attributes
     */
    private var mCurrentCourse : String = "comipems"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (container == null)
            return null

        val rootView = inflater.inflate(R.layout.init_fragment_refactor, container, false)!!

        mCloseCourseSelection = rootView.findViewById(R.id.iv_close_courses)
        mCourseListView = rootView.findViewById(R.id.lv_courses)
        mLoadingCourses = rootView.findViewById(R.id.pb_loading_courses)

        mCloseCourseSelection.setOnClickListener(mCloseCourseSelectionListener)

        requestGetCoursesRefactor()

        return rootView
    }

    override fun onGetCoursesRefactorSuccess(courses: List<Course>) {
        super.onGetCoursesRefactorSuccess(courses)
        mLoadingCourses.visibility = View.GONE

        mCourses = courses

        if (context != null) {
            val courseListAdapter = CourseListAdapter(context!!, mCourses)
            mCourseListView.adapter = courseListAdapter
            mCourseListView.setOnItemClickListener(this)
        } else {
            if (activity != null) {
                activity!!.onBackPressed()
            }
        }
    }

    override fun onGetCoursesRefactorFail(throwable: Throwable) {
        super.onGetCoursesRefactorFail(throwable)
        mLoadingCourses.visibility = View.GONE
    }

    override fun onDoLogInSuccess(success: Boolean) {
        super.onDoLogInSuccess(success)
        val user = User()
        user.setCourse(mCurrentCourse)
        val userFirebase = FirebaseAuth.getInstance().currentUser
        if (userFirebase != null) {
            user.setUUID(userFirebase.uid)
        }
        saveUser(user)
        if (!mCurrentCourse.equals("")) {
            requestGetImagesPath(mCurrentCourse)
        }
    }

    override fun onDoLogInFail(throwable: Throwable) {
        super.onDoLogInFail(throwable)
        mCourseListView.visibility = View.VISIBLE
        mLoadingCourses.visibility = View.GONE
    }

    override fun onGetImagesPathSuccess(images: List<Image>) {
        super.onGetImagesPathSuccess(images)

        if (context != null) {
            val dataHelper = DataHelper(context!!)
            dataHelper.saveSessionData(true)
            dataHelper.saveImagesPath(images)
            goQuestionActivity()
            //if (!dataHelper.areImagesDownloaded()) {
            //(activity as LoginActivity).startDownloadImages()
            //}
        }

    }

    override fun onGetImagesPathFail(throwable: Throwable) {
        super.onGetImagesPathFail(throwable)
        mCourseListView.visibility = View.VISIBLE
        mLoadingCourses.visibility = View.GONE
    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, position: Int, p3: Long) {
        Log.d(TAG, "item clicked--- position: " + position)
        if (mCourses.isNotEmpty()) {
            mCourseListView.visibility = View.GONE
            mLoadingCourses.visibility = View.VISIBLE
            mCurrentCourse = mCourses[position].id

            requestLogIn(null)
        }
    }

    private val mCloseCourseSelectionListener = View.OnClickListener {
        if (activity != null) {
            activity!!.onBackPressed()
        }
    }

    private fun goQuestionActivity() {
        if (activity != null) {
            val intent = Intent(activity, QuestionActivity::class.java)
            intent.putExtra(MODULE_ID, 1) // show first module
            intent.putExtra(ANONYMOUS_USER, true)
            if (!mCurrentCourse.equals("")) {
                intent.putExtra(CURRENT_COURSE, mCurrentCourse)
                val user = getUser()
                if (user != null) {
                    user.setCourse(mCurrentCourse)
                    saveUser(user)
                }
            }

            startActivity(intent)
            activity!!.finish()
        }
    }
}