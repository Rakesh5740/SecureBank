
package com.example.securebank.core.domain.model

data class FundTransfer(
    val id: String,
    val sourceAccountId: String,
    val beneficiaryId: String,
    val amountInPaise: Long,
    val status: TransferStatus
)

enum class TransferStatus {
    PENDING,
    SUCCESS,
    FAILED
}
