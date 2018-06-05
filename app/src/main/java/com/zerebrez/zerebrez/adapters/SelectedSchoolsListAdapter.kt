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
import android.widget.BaseExpandableListAdapter
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.Institute
import com.zerebrez.zerebrez.models.School
import com.zerebrez.zerebrez.utils.FontUtil
import kotlinx.android.synthetic.main.custom_selected_school.view.*
import kotlinx.android.synthetic.main.custom_selected_institute.view.*

/**
 * Created by Jorge Zepeda Tinoco on 29/05/18.
 * jorzet.94@gmail.com
 */

class SelectedSchoolsListAdapter (institutes : List<Institute>, context : Context) : BaseExpandableListAdapter() {

    private val mInstitutes : List<Institute> = institutes
    private val mContext : Context = context

    /*
     * create heads
     */
    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {

        val institute = getGroup(groupPosition) as Institute

        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val instituteView = inflator.inflate(R.layout.custom_selected_institute, null)
        instituteView.tv_institute_name.text = institute.getInstituteName()
        instituteView.tv_institute_name.typeface = FontUtil.getNunitoBold(mContext)

        if (isExpanded) {
            instituteView.tv_angle.setImageDrawable(mContext.resources.getDrawable(R.drawable.expand_less_icon))
        } else {
            instituteView.tv_angle.setImageDrawable(mContext.resources.getDrawable(R.drawable.expand_more_icon))
        }

        return instituteView
    }

    override fun getGroup(groupPosition: Int): Any {
        return this.mInstitutes.get(groupPosition)
    }

    override fun getGroupCount(): Int {
        return this.mInstitutes.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }


    /*
     * create childs
     */
    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {

        val school = getChild(groupPosition, childPosition) as School

        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val schoolView = inflator.inflate(R.layout.custom_selected_school, null)
        schoolView.tv_school_name.text = school.getSchoolName()
        schoolView.tv_school_name.typeface = FontUtil.getNunitoRegular(mContext)

        return schoolView
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return this.mInstitutes.get(groupPosition).getSchools().get(childPosition)
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return this.mInstitutes.get(groupPosition).getSchools().size
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }




    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }
}
