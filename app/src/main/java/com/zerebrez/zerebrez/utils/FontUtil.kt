package com.zerebrez.zerebrez.utils

import android.content.Context
import android.graphics.Typeface
import java.lang.reflect.Type
import java.util.*


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
                    String.format(Locale.US, "fonts/%s", "Nunito-Regular.ttf"))
        }
    }
}