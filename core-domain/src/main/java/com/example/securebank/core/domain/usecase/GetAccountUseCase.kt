
package com.example.securebank.core.domain.usecase

import com.example.securebank.core.domain.model.BankAccount
import com.example.securebank.core.domain.repository.BankRepository
import kotlinx.coroutines.flow.Flow

class GetAccountUseCase(
    private val bankRepository: BankRepository
) {
    operator fun invoke(accountId: String): Flow<BankAccount?> {
        return bankRepository.observeAccount(accountId)
    }
}
