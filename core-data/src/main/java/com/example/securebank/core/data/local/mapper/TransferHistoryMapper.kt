
package com.example.securebank.core.data.local.mapper

import com.example.securebank.core.data.local.entity.TransferHistoryEntity
import com.example.securebank.core.domain.model.TransferHistory
import com.example.securebank.core.domain.model.TransferStatus

fun TransferHistoryEntity.toDomain(): TransferHistory {
    return TransferHistory(
        id = id,
        sourceAccountId = sourceAccountId,
        beneficiaryId = beneficiaryId,
        amountInMinorUnits = amountInMinorUnits,
        currency = currency,
        status = TransferStatus.valueOf(status),
        createdAtEpochMillis = createdAtEpochMillis
    )
}

fun TransferHistory.toEntity(): TransferHistoryEntity {
    return TransferHistoryEntity(
        id = id,
        sourceAccountId = sourceAccountId,
        beneficiaryId = beneficiaryId,
        amountInMinorUnits = amountInMinorUnits,
        currency = currency,
        status = status.name,
        createdAtEpochMillis = createdAtEpochMillis
    )
}
