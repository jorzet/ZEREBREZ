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

/*
 * This enum defines background for each module in QuestionModuleFragment
 *
 * Created by Jorge Zepeda Tinoco on 24/04/18.
 * jorzet.94@gmail.com
 */

enum class ModuleTypeIcon constructor(val value : Int) {
    CHECKED_MODULE_BACKGROUND(R.drawable.checked_module_background),
    UNCHECKED_MODULE_BACKGROUND(R.drawable.unchecked_module_background),
    SQUARE_FIRST_MODULE_BACKGROUND(R.drawable.square_first_module_background),
    SQUARE_SECOND_MODULE_BACKGROUND(R.drawable.square_second_module_background)
}