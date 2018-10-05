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

package com.zerebrez.zerebrez.models.enums

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Jorge Zepeda Tinoco on 01/05/18.
 * jorzet.94@gmail.com
 */

enum class SubjectType constructor(val value : String)  {
    NONE(""),
    @SerializedName("Habilidad Verbal")
    @Expose
    VERBAL_HABILITY("Habilidad Verbal"),
    @SerializedName("Habilidad Matematica")
    @Expose
    MATHEMATICAL_HABILITY("Habilidad Matematica"),
    @SerializedName("Español")
    @Expose
    SPANISH("Español"),
    @SerializedName("Ingles")
    @Expose
    ENGLISH("Ingles"),
    @SerializedName("Matemáticas")
    @Expose
    MATHEMATICS("Matemáticas"),
    @SerializedName("Química")
    @Expose
    CHEMISTRY("Química"),
    @SerializedName("Física")
    @Expose
    PHYSICS("Física"),
    @SerializedName("Biología")
    @Expose
    BIOLOGY("Biología"),
    @SerializedName("Geografía")
    @Expose
    GEOGRAPHY("Geografía"),
    @SerializedName("Historia de México")
    @Expose
    MEXICO_HISTORY("Historia de México"),
    @SerializedName("Historia Universal")
    @Expose
    UNIVERSAL_HISTORY("Historia Universal"),
    @SerializedName("Formación cívica y ética")
    @Expose
    FCE("Formación cívica y ética"),
    @SerializedName("FCE")
    @Expose
    FCE2("FCE");
}