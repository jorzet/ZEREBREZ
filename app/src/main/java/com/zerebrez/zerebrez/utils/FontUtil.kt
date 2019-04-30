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

package com.zerebrez.zerebrez.utils

import android.content.Context
import android.graphics.Typeface
import java.lang.reflect.Type
import java.util.*

/**
 * Created by Jorge Zepeda Tinoco on 03/06/18.
 * jorzet.94@gmail.com
 */

class FontUtil {
    companion object {
        fun getNunitoBlack(context: Context) : Typeface{
            val am = context.applicationContext.assets

            return Typeface.createFromAsset(am,
                    String.format(Locale.US, "fonts/%s", "Nunito-Black.ttf"))
        }

        fun getNunitoBlackItalic(context: Context) : Typeface{
            val am = context.applicationContext.assets

            return Typeface.createFromAsset(am,
                    String.format(Locale.US, "fonts/%s", "Nunito-BlackItalick.ttf"))
        }

        fun getNunitoBold(context: Context) : Typeface{
            val am = context.applicationContext.assets

            return Typeface.createFromAsset(am,
                    String.format(Locale.US, "fonts/%s", "Nunito-Bold.ttf"))
        }

        fun getNunitoSemiBold(context: Context) : Typeface {
            val am = context.applicationContext.assets

            return Typeface.createFromAsset(am,
                    String.format(Locale.US, "fonts/%s", "Nunito-SemiBold.ttf"))
        }

        fun getNunitoBoldItalic(context: Context) : Typeface{
            val am = context.applicationContext.assets

            return Typeface.createFromAsset(am,
                    String.format(Locale.US, "fonts/%s", "Nunito-BoldItalic.ttf"))
        }

        fun getNunitoRegular(context: Context) : Typeface{
            val am = context.applicationContext.assets

            return Typeface.createFromAsset(am,
                    String.format(Locale.US, "fonts/%s", "Nunito-Light.ttf"))
        }
    }
}