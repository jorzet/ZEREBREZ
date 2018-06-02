package com.zerebrez.zerebrez.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.Subject
import com.zerebrez.zerebrez.models.enums.SubjectType
import kotlinx.android.synthetic.main.custom_option_sobject.view.*

class SubjectListAdapter (subjects : List<Subject>, context : Context) : BaseAdapter() {
    private val mSubjects: List<Subject> = subjects
    private val mContext: Context = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val currentSubject = getItem(position) as Subject

        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val subjectView = inflator.inflate(R.layout.custom_option_sobject, null)

        subjectView.tv_subject_name.text = currentSubject.getsubjectType().value.toUpperCase()

        when (currentSubject.getsubjectType()) {
            SubjectType.MATHEMATICS -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.mat_1_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.mathematics_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.mathematics_subject_color))
            }
            SubjectType.SPANISH -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.esp_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.spanish_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.spanish_subject_color))
            }
            SubjectType.VERBAL_HABILITY -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.hab_ver_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.verbal_habilitiy_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.verbal_habilitiy_subject_color))
            }
            SubjectType.MATHEMATICAL_HABILITY -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.hab_mat_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.mathematical_habilitiy_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.mathematical_habilitiy_subject_color))
            }
            SubjectType.BIOLOGY -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.bio_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.biology_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.biology_subject_color))
            }
            SubjectType.CHEMISTRY -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.quim_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.chemistry_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.chemistry_subject_color))
            }
            SubjectType.PHYSICS -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.fis_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.physics_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.physics_subject_color))
            }
            SubjectType.GEOGRAPHY -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.geo_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.geography_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.geography_subject_color))
            }
            SubjectType.MEXICO_HISTORY -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.his_mex_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.mexico_history_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.mexico_history_subject_color))
            }
            SubjectType.UNIVERSAL_HISTORY -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.his_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.history_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.history_subject_color))
            }
            SubjectType.FCE -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.civ_et_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.fce_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.fce_subject_color))
            }
        }

        return subjectView
    }

    override fun getItem(position: Int): Any {
        return this.mSubjects.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return this.mSubjects.size
    }
}
