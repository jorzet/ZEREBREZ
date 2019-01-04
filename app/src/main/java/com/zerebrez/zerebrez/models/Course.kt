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