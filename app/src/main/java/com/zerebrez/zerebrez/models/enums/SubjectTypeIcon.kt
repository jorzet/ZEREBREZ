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

package com.zerebrez.zerebrez.models.enums

import com.zerebrez.zerebrez.R

/**
 * Created by Jorge Zepeda Tinoco on 20/03/18.
 * jorzet.94@gmail.com
 */

enum class SubjectTypeIcon constructor(val value : Int)  {
    NONE(0),
    VERBAL(R.drawable.verbal_hab_icon),
    SPANISH(R.drawable.spanish_icon),
    ENGLISH(0),
    MATHEMATICS(R.drawable.math_icon),
    CHEMISTRY(R.drawable.chemic_icon),
    BIOLOGY(0),
    GEOGRAPHY(R.drawable.geo_icon),
    MEXICO_HISTORY(0),
    UNIVERSAL_HISTORY(0)
}