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
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.zerebrez.zerebrez.fragments.practice.ExamFragment
import com.zerebrez.zerebrez.fragments.practice.QuestionModulesFragment
import com.zerebrez.zerebrez.fragments.practice.StudySubjectFragment
import com.zerebrez.zerebrez.fragments.practice.StudyWrongQuestionFragment
import com.zerebrez.zerebrez.fragments.practice.containers.DoExamContainerFragment
import com.zerebrez.zerebrez.fragments.practice.containers.QuestionContainerFragment
import com.zerebrez.zerebrez.fragments.practice.containers.StudySubjectContainerFragment
import com.zerebrez.zerebrez.fragments.practice.containers.StudyWrongQuestionContainerFragment
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager

/**
 * Created by Jorge Zepeda Tinoco on 24/04/18.
 * jorzet.94@gmail.com
 */

class PracticeViewPager (context : Context, fm: FragmentManager, tabCount : Int) : FragmentStatePagerAdapter(fm) {
    private val mContext : Context = context
    private val mTotalPages : Int = tabCount

    override fun getItem(position: Int): Fragment {
        var fragment = Fragment()
        when(position) {
            0 -> {
                if (SharedPreferencesManager(mContext).isQuestionModuleFragmentOK()) {
                    fragment = QuestionModulesFragment()
                } else {
                    fragment = QuestionContainerFragment()
                }
            }
            1 -> {
                if (SharedPreferencesManager(mContext).isStudySubjectFragmentOK()) {
                    fragment = StudySubjectFragment()
                } else {
                    fragment = StudySubjectContainerFragment()
                }
            }
            2 -> {
                if (SharedPreferencesManager(mContext).isStudyWrongQuestionFragmentOK()) {
                    fragment = StudyWrongQuestionFragment()
                } else {
                    fragment = StudyWrongQuestionContainerFragment()
                }
            }
            3 -> {
                if (SharedPreferencesManager(mContext).isExamFragmentOK()) {
                    fragment = ExamFragment()
                } else {
                    fragment = DoExamContainerFragment()
                }
            }
        }
        return fragment
    }

    override fun getCount(): Int {
        return mTotalPages
    }

}