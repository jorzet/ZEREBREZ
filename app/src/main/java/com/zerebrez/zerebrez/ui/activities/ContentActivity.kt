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

package com.zerebrez.zerebrez.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.View
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.adapters.AdvancesViewPager
import com.zerebrez.zerebrez.adapters.PracticeViewPager
import com.zerebrez.zerebrez.adapters.ProfileViewPager
import com.zerebrez.zerebrez.adapters.ScoreViewPager
import com.zerebrez.zerebrez.models.enums.NodeType
import com.zerebrez.zerebrez.utils.ImagesUtil
import android.support.design.widget.Snackbar
import android.util.Log
import com.zerebrez.zerebrez.models.ExamScore
import com.zerebrez.zerebrez.services.database.DataHelper
import com.zerebrez.zerebrez.services.firebase.DownloadImages

/**
 * This class call all components and adapters to build home view
 * with their tabs (top and bottom)
 *
 * Created by Jorge Zepeda Tinoco on 27/02/18.
 * jorzet.94@gmail.com
 */

private const val TAG : String = "ContentActivity"

class ContentActivity : BaseActivityLifeCycle() {

    /*
     * UI accessors
     */
    private lateinit var mTopTabLayout : TabLayout
    private lateinit var mBottomTabLayout : TabLayout
    private lateinit var mViewPager : ViewPager
    private lateinit var mCordinatorView : View

    /*
    * Adapters
    */
    private lateinit var mPracticeViewPager : PracticeViewPager
    private lateinit var mAdvancesViewPager: AdvancesViewPager
    private lateinit var mScoreViewPager : ScoreViewPager
    private lateinit var mProfileViewPager : ProfileViewPager

    /*
     * TabLayout tags
     */
    private val QUESTION_TAG : String = "question_tag"
    private val STUDY_TAG : String = "study_tag"
    private val CHECK_TAG : String = "check_tag"
    private val ANSWER_TAG : String = "answer_tag"
    private val SCHOOL_TAG : String = "school_tag"
    private val STAR_TAG : String = "star_tag"
    private val USER_TAG : String = "user_tag"
    private val BRAINKEY_TAG : String = "brainkey_tag"
    private val PRACTICE_TAG : String = "practice_tag"
    private val ADVANCES_TAG : String = "advances_tag"
    private val MAIN_ICON_TAG : String = "main_icon_tag"
    private val SCORE_TAG : String = "score_tag"
    private val PROFILE_TAG : String = "profile_tag"
    private val SEPARATOR : String = "separator"

    /*
     * attributes to know tab
     */
    private lateinit var currentTab : NodeType

    /*
     * some variables
     */
    private var doubleBackToExitPressedOnce : Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_container)

        mCordinatorView = findViewById(R.id.cordinator_view)
        mTopTabLayout = findViewById(R.id.top_tab_layout)
        mTopTabLayout.tabGravity = TabLayout.GRAVITY_FILL

        mBottomTabLayout = findViewById(R.id.bottom_tab_layout)
        mBottomTabLayout.tabGravity = TabLayout.GRAVITY_FILL

        mTopTabLayout.addTab(mTopTabLayout.newTab().setTag(QUESTION_TAG))
        mTopTabLayout.addTab(mTopTabLayout.newTab().setTag(STUDY_TAG))
        mTopTabLayout.addTab(mTopTabLayout.newTab().setTag(CHECK_TAG))
        mTopTabLayout.addTab(mTopTabLayout.newTab().setTag(ANSWER_TAG))

        mBottomTabLayout.addTab(mBottomTabLayout.newTab().setTag(PRACTICE_TAG))
        mBottomTabLayout.addTab(mBottomTabLayout.newTab().setTag(ADVANCES_TAG))
        mBottomTabLayout.addTab(mBottomTabLayout.newTab().setTag(MAIN_ICON_TAG))
        mBottomTabLayout.addTab(mBottomTabLayout.newTab().setTag(SCORE_TAG))
        mBottomTabLayout.addTab(mBottomTabLayout.newTab().setTag(PROFILE_TAG))

        mViewPager = findViewById(R.id.pager)
        mPracticeViewPager = PracticeViewPager(applicationContext, supportFragmentManager, 4)
        mAdvancesViewPager = AdvancesViewPager(applicationContext, supportFragmentManager, 1)
        mScoreViewPager = ScoreViewPager(applicationContext, supportFragmentManager, 2)
        mProfileViewPager = ProfileViewPager(applicationContext, supportFragmentManager, 2)

        mViewPager.setAdapter(mPracticeViewPager)
        mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTopTabLayout))
        mTopTabLayout.setupWithViewPager(mViewPager)

        mTopTabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.gray_soft))
        mBottomTabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.colorPrimary))

        // init top TabLayout in practice type
        currentTab = NodeType.PRACTICE
        // draw top TabLayout icons with practice node
        setTopTabIcons()
        // paint botton TabLayout icon
        setBottomTabIcons()

        requestGetExamScores()

        mTopTabLayout.setOnTabSelectedListener(onTopTabLayoutListener);
        mBottomTabLayout.setOnTabSelectedListener(onBottomTabLayoutListener)

    }

    override fun onStart() {
        super.onStart()
        startDownloadImages()
    }

    /*
     * Listener to know what tab from top TabLayout is selected
     */
    private val onTopTabLayoutListener = object : TabLayout.OnTabSelectedListener {

        override fun onTabReselected(tab: TabLayout.Tab?) {

        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            when (currentTab) {
                NodeType.PRACTICE -> {
                    mTopTabLayout.getTabAt(tab!!.position)!!.setIcon(ImagesUtil.mPracticeTopUnselectedIcons[tab.position])
                }
                NodeType.ADVANCES -> { }
                NodeType.SCORE -> {
                    mTopTabLayout.getTabAt(tab!!.position)!!.setIcon(ImagesUtil.mScoreTopUnselectedIcons[tab.position])
                }
                NodeType.PROFILE -> {
                    mTopTabLayout.getTabAt(tab!!.position)!!.setIcon(ImagesUtil.mProfileTopUnselectedIcons[tab.position])
                }
            }

        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            mViewPager.currentItem = tab!!.position
            when (currentTab) {
                NodeType.PRACTICE -> {
                    mTopTabLayout.getTabAt(tab.position)!!.setIcon(ImagesUtil.mPracticeTopSelectedIcons[tab.position])
                }
                NodeType.ADVANCES -> { }
                NodeType.SCORE -> {
                    mTopTabLayout.getTabAt(tab.position)!!.setIcon(ImagesUtil.mScoreTopSelectedIcons[tab.position])
                }
                NodeType.PROFILE -> {
                    mTopTabLayout.getTabAt(tab.position)!!.setIcon(ImagesUtil.mProfileTopSelectedIcons[tab.position])
                }
            }
            mTopTabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.gray_soft))
        }
    }

    /*
     * Listener to know what tab from bottom TabLayout is selected
     */
    private val onBottomTabLayoutListener = object : TabLayout.OnTabSelectedListener {

        override fun onTabReselected(tab: TabLayout.Tab?) {

        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            if(tab!!.position != 2) {
                mBottomTabLayout.getTabAt(tab.position)!!.setIcon(ImagesUtil.mBottomUnselectedIcons[tab.position])
            }
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            when(tab!!.position) {
                0 -> {currentTab = NodeType.PRACTICE}
                1 -> {currentTab = NodeType.ADVANCES}
                2 -> {currentTab = NodeType.BAINKEY/* this node is the brain key icon */}
                3 -> {currentTab = NodeType.SCORE}
                4 -> {currentTab = NodeType.PROFILE}
            }

            // Draw top TabLayout icons
            if (currentTab != NodeType.BAINKEY)
                setTopTabIcons()

            if (currentTab == NodeType.ADVANCES) { // when is second tab hide top TabLayout
                mTopTabLayout.visibility = View.GONE
                mBottomTabLayout.getTabAt(tab.position)!!.setIcon(ImagesUtil.mBottomSelectedIcons[tab.position])
                mBottomTabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.colorPrimary))
            } else if (currentTab != NodeType.BAINKEY) { // Draw icons if is not brain icon
                mTopTabLayout.visibility = View.VISIBLE
                mBottomTabLayout.getTabAt(tab.position)!!.setIcon(ImagesUtil.mBottomSelectedIcons[tab.position])
                mBottomTabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.colorPrimary))
            }
        }
    }

    /*
     * Method that change top TabLayout deleting and creating all tabs
     * according to the current bottom tab
     */
    private fun setTopTabIcons() {
        deleteAndCreateNodes()
        when (currentTab) {
            NodeType.PRACTICE -> {
                mViewPager.setAdapter(mPracticeViewPager)
                mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTopTabLayout))
                mTopTabLayout.setupWithViewPager(mViewPager)

                mTopTabLayout.getTabAt(0)!!.setIcon(ImagesUtil.mPracticeTopSelectedIcons[0])
                mTopTabLayout.getTabAt(1)!!.setIcon(ImagesUtil.mPracticeTopUnselectedIcons[1])
                mTopTabLayout.getTabAt(2)!!.setIcon(ImagesUtil.mPracticeTopUnselectedIcons[2])
                mTopTabLayout.getTabAt(3)!!.setIcon(ImagesUtil.mPracticeTopUnselectedIcons[3])
            }
            NodeType.ADVANCES -> {
                mViewPager.setAdapter(mAdvancesViewPager)
                mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTopTabLayout))
                mTopTabLayout.setupWithViewPager(mViewPager)
            }
            NodeType.SCORE -> {
                mViewPager.setAdapter(mScoreViewPager)
                mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTopTabLayout))
                mTopTabLayout.setupWithViewPager(mViewPager)

                mTopTabLayout.getTabAt(0)!!.setIcon(ImagesUtil.mScoreTopSelectedIcons[0])
                mTopTabLayout.getTabAt(1)!!.setIcon(ImagesUtil.mScoreTopUnselectedIcons[1])
            }
            NodeType.PROFILE -> {
                mViewPager.setAdapter(mProfileViewPager)
                mViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(mTopTabLayout))
                mTopTabLayout.setupWithViewPager(mViewPager)

                mTopTabLayout.getTabAt(0)!!.setIcon(ImagesUtil.mProfileTopSelectedIcons[0])
                mTopTabLayout.getTabAt(1)!!.setIcon(ImagesUtil.mProfileTopUnselectedIcons[1])
            }
        }
    }

    /*
     * Set icons to show in bottom TabLayout
     */
    private fun setBottomTabIcons() {
        mBottomTabLayout.getTabAt(0)!!.setIcon(ImagesUtil.mBottomSelectedIcons[0])
        mBottomTabLayout.getTabAt(1)!!.setIcon(ImagesUtil.mBottomUnselectedIcons[1])
        mBottomTabLayout.getTabAt(2)!!.setIcon(ImagesUtil.mBottomUnselectedIcons[2])
        mBottomTabLayout.getTabAt(3)!!.setIcon(ImagesUtil.mBottomUnselectedIcons[3])
        mBottomTabLayout.getTabAt(4)!!.setIcon(ImagesUtil.mBottomUnselectedIcons[4])
    }

    /*
     * The method remove all tabs and add new tabs according NodeType selected
     */
    private fun deleteAndCreateNodes() {
        mTopTabLayout.removeAllTabs()
        when(currentTab) {
            NodeType.PRACTICE -> {
                mTopTabLayout.addTab(mTopTabLayout.newTab().setTag(QUESTION_TAG))
                mTopTabLayout.addTab(mTopTabLayout.newTab().setTag(STUDY_TAG))
                mTopTabLayout.addTab(mTopTabLayout.newTab().setTag(CHECK_TAG))
                mTopTabLayout.addTab(mTopTabLayout.newTab().setTag(ANSWER_TAG))
            }
            NodeType.ADVANCES -> { }
            NodeType.SCORE -> {
                mTopTabLayout.addTab(mTopTabLayout.newTab().setTag(SCHOOL_TAG))
                mTopTabLayout.addTab(mTopTabLayout.newTab().setTag(STAR_TAG))
            }
            NodeType.PROFILE -> {
                mTopTabLayout.addTab(mTopTabLayout.newTab().setTag(USER_TAG))
                mTopTabLayout.addTab(mTopTabLayout.newTab().setTag(BRAINKEY_TAG))
            }
        }
    }

    /*
     * Backpress override method, shows snackbar when user whants to exit
     */
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        val fragment = mPracticeViewPager.getItem(mViewPager.currentItem)
        Snackbar.make(mCordinatorView,
                "Presiona otra vez para SALIR de la aplicación",
                2000)
                .show()

        this.doubleBackToExitPressedOnce = true

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onGetExamScoresSuccess(examScores: List<ExamScore>) {
        super.onGetExamScoresSuccess(examScores)
        if (examScores.isNotEmpty()) {
            val dataHelper = DataHelper(baseContext)
            dataHelper.saveExamScores(examScores)
        }
    }

    override fun onGetExamScoresFail(throwable: Throwable) {
        super.onGetExamScoresFail(throwable)
    }


    fun startDownloadImages() {
        this.startService(Intent(this, DownloadImages::class.java))
        Log.i(TAG, "Started download service **********************")
        this.registerReceiver(br, IntentFilter(DownloadImages.DOWNLOAD_IMAGES_BR))
    }

    fun stopDownloadImagesService() {
        this.stopService(Intent(this, DownloadImages::class.java))
        Log.i(TAG, "Stopped service ***************************")
    }

    private val br = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getExtras() != null) {
                if (intent.getBooleanExtra(DownloadImages.DOWNLOAD_COMPLETE,false)) {
                    stopDownloadImagesService()
                } else {
                    Log.d(TAG, "downloading .....")
                }
            }
        }
    }
}