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

package com.zerebrez.zerebrez.utils

import com.zerebrez.zerebrez.R

/**
 * Created by Jorge Zepeda Tinoco on 24/04/18.
 * jorzet.94@gmail.com
 */

class ImagesUtil {
    companion object {
        /*
         * Define all icon resorces for bottom tab layout
         */
        val mBottomSelectedIcons = intArrayOf(
                R.drawable.practice_selected_icon,
                R.drawable.advances_selected_icon,
                R.drawable.score_selected_icon,
                R.drawable.profile_selected_icon)

        val mBottomUnselectedIcons = intArrayOf(
                R.drawable.practice_unselected_icon,
                R.drawable.advances_unselected_icon,
                R.drawable.score_unselected_icon,
                R.drawable.profile_unselected_icon)

        /*
         * Define all icon resources for top tab layout
         */

        /*
         * Icons that is showed when practice module is selected
         */
        val mPracticeTopUnselectedIcons = intArrayOf(
                R.drawable.question_module_unselected,
                R.drawable.study_module_unselected,
                R.drawable.check_module_unselected,
                R.drawable.answer_module_unselected)

        val mPracticeTopSelectedIcons = intArrayOf(
                R.drawable.question_module_selected,
                R.drawable.study_module_selected,
                R.drawable.check_module_selected,
                R.drawable.answer_module_selected)

        /*
         * This section doesn't have icons
         */
        val mAdvancesTopUnselectedIcons = intArrayOf()

        val mAdvancesTopSelectedIcons = intArrayOf()

        /*
         * Icons that is showed when score module is selected
         */
        val mScoreTopUnselectedIcons = intArrayOf(
                R.drawable.school_unselected_icon,
                R.drawable.star_unselected_icon)

        val mScoreTopSelectedIcons = intArrayOf(
                R.drawable.school_selected_icon,
                R.drawable.star_selected_icon)

        /*
         * Icons that is showed when profile module is selected
         */
        val mProfileTopUnselectedIcons = intArrayOf(
                R.drawable.user_unselected_icon,
                R.drawable.brainkey_unselected_icon)

        val mProfileTopSelectedIcons = intArrayOf(
                R.drawable.user_selected_icon,
                R.drawable.brainkey_selected_icon)

    }
}