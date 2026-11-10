package com.example.securebank.core.domain.model

data class TransferHistory(
    val id: String,
    val sourceAccountId: String,
    val beneficiaryId: String,
    val amountInMinorUnits: Long,
    val currency: String = "INR",
    val status: TransferStatus,
    val createdAtEpochMillis: Long
)