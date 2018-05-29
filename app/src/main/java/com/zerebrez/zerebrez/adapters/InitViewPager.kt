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

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.zerebrez.zerebrez.fragments.init.ComipemsFragment
import com.zerebrez.zerebrez.fragments.init.SecundaryFragment

/**
 * Created by Jorge Zepeda Tinoco on 12/03/18.
 * jorzet.94@gmail.com
 */

class InitViewPager(fm: FragmentManager, courses : List<String>) : FragmentStatePagerAdapter(fm) {

    private val mTotalPages : Int = courses.size
    private val mCourses : List<String> = courses

    private val COMIPEMS : String = "comipems"
    private val SECUNDARY : String = "secundary"

    override fun getItem(position: Int): Fragment {
        var fragment = Fragment()

        when(mCourses.get(position)){
            SECUNDARY -> fragment = SecundaryFragment()
            COMIPEMS -> fragment = ComipemsFragment()
        }
        return fragment
    }

    override fun getCount(): Int {
        return mTotalPages
    }
}