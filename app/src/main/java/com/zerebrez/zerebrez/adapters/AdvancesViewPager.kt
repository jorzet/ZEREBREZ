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

package com.zerebrez.zerebrez.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.zerebrez.zerebrez.fragments.advances.AdvancesFragment
import com.zerebrez.zerebrez.fragments.advances.container.AdvancesContainerFragment
import com.zerebrez.zerebrez.services.sharedpreferences.SharedPreferencesManager

/**
 * Created by Jorge Zepeda Tinoco on 25/04/18.
 * jorzet.94@gmail.com
 */

class AdvancesViewPager (context : Context, fm: FragmentManager, tabCount : Int) : FragmentStatePagerAdapter(fm) {

    private val mContext : Context = context
    private val mTotalPages : Int = tabCount

    override fun getItem(position: Int): Fragment {
        if (SharedPreferencesManager(mContext).isAdvancesfragmentOK()) {
            return AdvancesFragment()
        } else {
            return AdvancesContainerFragment()
        }
    }

    override fun getCount(): Int {
        return mTotalPages;
    }

}
