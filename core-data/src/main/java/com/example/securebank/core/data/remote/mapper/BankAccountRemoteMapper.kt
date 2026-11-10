
package com.example.securebank.core.data.remote.mapper

import com.example.securebank.core.data.remote.dto.BankAccountDto
import com.example.securebank.core.domain.model.BankAccount
import java.math.BigDecimal
import java.math.RoundingMode

fun BankAccountDto.toDomain(): BankAccount {
    val minorUnits = BigDecimal.valueOf(balance)
        .movePointRight(2)
        .setScale(0, RoundingMode.HALF_UP)
        .longValueExact()

    return BankAccount(
        id = id.toString(),
        accountHolderName = owner,
        accountNumber = iban,
        balanceInMinorUnits = minorUnits,
        currency = currency
    )
}
