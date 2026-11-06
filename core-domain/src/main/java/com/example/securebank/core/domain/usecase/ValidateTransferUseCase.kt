package com.example.securebank.core.domain.usecase

import com.example.securebank.core.domain.model.BankAccount

class ValidateTransferUseCase {

    operator fun invoke(
        sourceAccount: BankAccount,
        beneficiaryId: String,
        amountInPaise: Long
    ): TransferValidationResult {

        if (amountInPaise <= 0L) {
            return TransferValidationResult.Invalid(
                TransferValidationResult.Reason.INVALID_AMOUNT
            )
        }

        if (sourceAccount.id == beneficiaryId) {
            return TransferValidationResult.Invalid(
                TransferValidationResult.Reason.SAME_ACCOUNT
            )
        }

        if (amountInPaise > sourceAccount.balanceInPaise) {
            return TransferValidationResult.Invalid(
                TransferValidationResult.Reason.INSUFFICIENT_BALANCE
            )
        }

        return TransferValidationResult.Valid
    }
}

sealed interface TransferValidationResult {
    data object Valid : TransferValidationResult

    data class Invalid(val reason: Reason) : TransferValidationResult

    enum class Reason {
        INVALID_AMOUNT,
        SAME_ACCOUNT,
        INSUFFICIENT_BALANCE
    }
}