package com.zerebrez.zerebrez.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Jorge Zepeda Tinoco on 19/09/18.
 * jorzet.94@gmail.com
 */

data class Course (
        @SerializedName("comproPagoDescription")
        @Expose
        var comproPagoDescription: String = "",
        @SerializedName("description")
        @Expose
        var description: String = "",
        @SerializedName("id")
        @Expose
        var id: String = "",
        @SerializedName("image")
        @Expose
        var image: String = "",
        @SerializedName("isActive")
        @Expose
        var isActive: Boolean = false,
        @SerializedName("monthsDuration")
        @Expose
        var monthsDuration: String = "",
        // this is to identify course exmp. c1, c2, c3
        var courseId: String = ""
);