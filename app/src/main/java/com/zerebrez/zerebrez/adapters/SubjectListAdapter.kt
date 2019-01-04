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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.zerebrez.zerebrez.R
import com.zerebrez.zerebrez.models.SubjectRefactor
import com.zerebrez.zerebrez.models.enums.SubjectType
import com.zerebrez.zerebrez.utils.FontUtil
import kotlinx.android.synthetic.main.custom_option_sobject.view.*

/**
 * Created by Jorge Zepeda Tinoco on 03/06/18.
 * jorzet.94@gmail.com
 */

class SubjectListAdapter (subjects : List<SubjectRefactor>, context : Context) : BaseAdapter() {
    private val mSubjects: List<SubjectRefactor> = subjects
    private val mContext: Context = context

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val currentSubject = getItem(position) as SubjectRefactor

        val inflator = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val subjectView = inflator.inflate(R.layout.custom_option_sobject, null)

        subjectView.tv_subject_name.text = currentSubject.subjectType.value
        subjectView.tv_subject_name.typeface = FontUtil.getNunitoRegular(mContext)

        when (currentSubject.subjectType) {
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

            SubjectType.PHILOSOPHY_AREA -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.filo_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.history_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.history_subject_color))
            }
            SubjectType.PHILOSOPHY_AREA_4 -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.filo_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.history_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.history_subject_color))
            }
            SubjectType.PHILOSOPHY -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.filo_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.history_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.history_subject_color))
            }
            SubjectType.LITERATURE -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.hab_ver_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.verbal_habilitiy_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.verbal_habilitiy_subject_color))
            }
            SubjectType.CHEMISTRY_AREA -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.quim_plus_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.chemistry_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.chemistry_subject_color))
            }
            SubjectType.CHEMISTRY_AREA_2 -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.quim_plus_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.chemistry_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.chemistry_subject_color))
            }
            SubjectType.MATEMATICS_AREA -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.mat_plus_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.mathematics_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.mathematics_subject_color))
            }
            SubjectType.MATEMATICS_AREA_1_2 -> {
                subjectView.image.background = mContext.resources.getDrawable(R.drawable.mat_plus_subject_icon_white)
                subjectView.image_container.background = mContext.resources.getDrawable(R.drawable.mathematics_subject_background)
                subjectView.tv_subject_name.setTextColor(mContext.resources.getColor(R.color.mathematics_subject_color))
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
