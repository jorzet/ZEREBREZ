package com.zerebrez.zerebrez.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.zerebrez.zerebrez.models.enums.SubjectType

/**
 * Created by Jorge Zepeda Tinoco on 20/09/18.
 * jorzet.94@gmail.com
 */

data class SubjectRefactor (

        @SerializedName("internalName")
        @Expose
        var internalName: String = "",
        @SerializedName("nameToDisplay")
        @Expose
        var nameToDisplay: String = "",
        // this is to identify course exmp. s1, s2, s3
        var subjectId: String = "",
        var subjectType : SubjectType = SubjectType.NONE,
        var subjectAverage : Double = 0.0
)