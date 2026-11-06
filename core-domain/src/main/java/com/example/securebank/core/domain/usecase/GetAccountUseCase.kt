
package com.example.securebank.core.domain.usecase

import com.example.securebank.core.domain.model.BankAccount
import com.example.securebank.core.domain.repository.BankRepository
import kotlinx.coroutines.flow.first

class GetAccountUseCase(
    private val bankRepository: BankRepository
) {

    suspend operator fun invoke(
        accountId: String
    ): BankAccount? {
        return bankRepository
            .observeAccount(accountId)
            .first()
    }
}
