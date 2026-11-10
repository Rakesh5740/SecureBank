package com.example.securebank.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BankAccountDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("owner")
    val owner: String,

    @SerializedName("iban")
    val iban: String,

    @SerializedName("balance")
    val balance: Double,

    @SerializedName("currency")
    val currency: String,

    @SerializedName("status")
    val status: String
)