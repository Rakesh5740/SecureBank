package com.example.securebank.core.domain.usecase

import com.example.securebank.core.domain.model.TransferHistory
import com.example.securebank.core.domain.repository.BankRepository
import kotlinx.coroutines.flow.Flow

class GetTransferHistoryUseCase(
    private val bankRepository: BankRepository
) {

    operator fun invoke(
        accountId: String
    ): Flow<List<TransferHistory>> {
        return bankRepository.observeTransferHistory(accountId)
    }
}