package com.zerebrez.zerebrez.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Course (
        @SerializedName("comproPagoDescription")
        @Expose val
        comproPagoDescription: String = "",
        @SerializedName("description")
        @Expose val
        description: String = "",
        @SerializedName("id")
        @Expose
        val id: String = "",
        @SerializedName("image")
        @Expose
        val image: String = "",
        @SerializedName("isActive")
        @Expose
        val isActive: Boolean = false
);