package com.example.securebank.core.data.local.mapper

import com.example.securebank.core.data.local.entity.BankAccountEntity
import com.example.securebank.core.domain.model.BankAccount

fun BankAccountEntity.toDomain(): BankAccount {
    return BankAccount(
        id = id,
        accountHolderName = accountHolderName,
        accountNumber = accountNumber,
        balanceInMinorUnits = balanceInMinorUnits,
        currency = currency
    )
}

fun BankAccount.toEntity(): BankAccountEntity {
    return BankAccountEntity(
        id = id,
        accountHolderName = accountHolderName,
        accountNumber = accountNumber,
        balanceInMinorUnits = balanceInMinorUnits,
        currency = currency
    )
}