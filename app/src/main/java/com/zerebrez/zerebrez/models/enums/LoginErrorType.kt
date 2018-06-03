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
 * Created by Jorge Zepeda Tinoco on 03/06/18.
 * jorzet.94@gmail.com
 */

enum class LoginErrorType constructor(val value : String)  {
    INVALID_EMAIL("Email incorrecto"),
    INVALID_CREDENTIAL("Credencial invalida"),
    WRONG_PASSWORD("Contraseña incorrecta"),
    ACCOUNT_EXIST_WITH_DIFFERENT_CREDENTIAL("Tu email ya esta asociado a otra forma de inicio de sesión. Ingresa como normalmente lo haces, ve a ajustes y vincula las demás formas de inicio de sesión"),
    USER_DISABLED("Este usuario no tiene permisos para entrar"),
    EMAIL_ADLREADY_IN_USE("No se ha podido crear la cuenta. Este email ya está registrado"),
    WEAK_PASSWORD("Contraseña demasiado débil. Añade números y letras"),
    USER_NOT_FOUND("No existe ningún usuario asociado a ese email. Por favor regístrate"),
    ERROR_CREDENTIAL_ALREADY_IN_USE("Tu credencial ya esta asociado a otra cuenta"),
    DEFAULT("Ha habido un problema para entrar. Prueba de nuevo.")
}