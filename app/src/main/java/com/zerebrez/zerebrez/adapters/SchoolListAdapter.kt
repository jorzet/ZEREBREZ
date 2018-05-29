package com.zerebrez.zerebrez.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.School
import kotlinx.android.synthetic.main.custom_selected_school.view.*

class SchoolListAdapter (schools : List<School>, context : Context) : BaseAdapter() {
    private val mSchools: List<School> = schools
    private val mContext: Context = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val currentSchool = getItem(position) as School

        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val schoolView = inflator.inflate(R.layout.custom_selected_school, null)
        schoolView.tv_school_name.text = currentSchool.getSchoolName()

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