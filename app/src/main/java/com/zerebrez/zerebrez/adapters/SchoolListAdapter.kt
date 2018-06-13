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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.School
import com.zerebrez.zerebrez.utils.FontUtil
import kotlinx.android.synthetic.main.custom_selected_school.view.*

/**
 * Created by Jorge Zepeda Tinoco on 29/05/18.
 * jorzet.94@gmail.com
 */

class SchoolListAdapter (schools : List<School>, context : Context) : BaseAdapter() {
    private val mSchools: List<School> = schools
    private val mContext: Context = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val currentSchool = getItem(position) as School

        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val schoolView = inflator.inflate(R.layout.custom_selected_school, null)
        schoolView.tv_school_name.text = currentSchool.getSchoolName()

        if (position.equals(0)) {
            schoolView.tv_school_name.typeface = FontUtil.getNunitoBold(mContext)
        } else {
            schoolView.tv_school_name.typeface = FontUtil.getNunitoSemiBold(mContext)
        }

        return schoolView
    }

    override fun getItem(position: Int): Any {
        return this.mSchools.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return this.mSchools.size
    }
}