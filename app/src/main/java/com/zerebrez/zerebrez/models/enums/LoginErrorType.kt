package com.zerebrez.zerebrez.models.enums

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