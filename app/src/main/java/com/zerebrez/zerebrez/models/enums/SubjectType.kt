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

/**
 * Created by Jorge Zepeda Tinoco on 01/05/18.
 * jorzet.94@gmail.com
 */

enum class SubjectType constructor(val value : String)  {
    NONE(""),
    VERBAL_HABILITY("Habilidad Verbal"),
    MATHEMATICAL_HABILITY("Habilidad Matematica"),
    SPANISH("Español"),
    ENGLISH("Ingles"),
    MATHEMATICS("Matemáticas"),
    CHEMISTRY("Química"),
    PHYSICS("Física"),
    BIOLOGY("Biología"),
    GEOGRAPHY("Geografía"),
    MEXICO_HISTORY("Historia de México"),
    UNIVERSAL_HISTORY("Historia Universal"),
    FCE("Formación cívica y ética");
}