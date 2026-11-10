package com.example.securebank.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transfer_history")
data class TransferHistoryEntity(

    @PrimaryKey
    val id: String,
    val sourceAccountId: String,
    val beneficiaryId: String,
    val amountInMinorUnits: Long,
    val currency: String = "INR",
    val status: String,
    val createdAtEpochMillis: Long
)
