
package com.example.securebank.core.domain.usecase

import com.example.securebank.core.domain.model.BankAccount
import com.example.securebank.core.domain.repository.BankRepository

class FetchRemoteAccountsUseCase(
    private val bankRepository: BankRepository
) {
    suspend operator fun invoke(): Result<List<BankAccount>> {
        return bankRepository.fetchRemoteAccounts()
    }
}
