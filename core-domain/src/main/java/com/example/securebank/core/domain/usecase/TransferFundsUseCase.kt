
package com.example.securebank.core.domain.usecase

import com.example.securebank.core.domain.model.FundTransfer
import com.example.securebank.core.domain.model.TransferStatus
import com.example.securebank.core.domain.repository.BankRepository

class TransferFundsUseCase(
    private val getAccountUseCase: GetAccountUseCase,
    private val validateTransferUseCase: ValidateTransferUseCase,
    private val bankRepository: BankRepository
) {

    suspend operator fun invoke(
        transfer: FundTransfer
    ): Result<FundTransfer> {

        return try {
            // 1. Get sender account
            val sourceAccount = getAccountUseCase(
                transfer.sourceAccountId
            ) ?: return Result.failure(
                IllegalArgumentException("Source account not found")
            )

            // 2. Validate transfer
            val validationResult = validateTransferUseCase(
                sourceAccount = sourceAccount,
                beneficiaryId = transfer.beneficiaryId,
                amountInPaise = transfer.amountInPaise
            )

            // 3. Handle validation result
            if (validationResult is TransferValidationResult.Invalid) {
                return Result.failure(
                    IllegalArgumentException(
                        validationResult.reason.name
                    )
                )
            }

            // 4. Execute transfer
            bankRepository.transferFunds(
                transfer.copy(status = TransferStatus.PENDING)
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}
