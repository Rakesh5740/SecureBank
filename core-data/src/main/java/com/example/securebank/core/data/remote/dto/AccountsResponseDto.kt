package com.example.securebank.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AccountsResponseDto(
    @SerializedName("total")
    val total: Int,

    @SerializedName("data")
    val data: List<BankAccountDto>,

    @SerializedName("requestId")
    val requestId: String
)