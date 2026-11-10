
package com.example.securebank.core.domain.model

data class BankAccount(
    val id: String,
    val accountHolderName: String,
    val accountNumber: String,
    val balanceInMinorUnits: Long,
    val currency: String = "INR"
)
