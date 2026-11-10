
package com.example.securebank.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_accounts")
data class BankAccountEntity(

    @PrimaryKey
    val id: String,

    val accountHolderName: String,

    val accountNumber: String,

    val balanceInMinorUnits: Long,

    val currency: String = "INR"
)
