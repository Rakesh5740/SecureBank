
package com.example.securebank.core.domain.usecase

import com.example.securebank.core.domain.model.BankAccount
import com.example.securebank.core.domain.repository.BankRepository
import kotlinx.coroutines.flow.first

class SeedDemoAccountsUseCase(
    private val bankRepository: BankRepository
) {
    suspend operator fun invoke() {
        val accounts = listOf(
            BankAccount(
                id = "ACC001",
                accountHolderName = "Rahul Sharma",
                accountNumber = "123456789001",
                balanceInPaise = 1_000_000L
            ),
            BankAccount(
                id = "ACC002",
                accountHolderName = "Amit Verma",
                accountNumber = "123456789002",
                balanceInPaise = 500_000L
            )
        )

        accounts.forEach { account ->
            val existing = bankRepository
                .observeAccount(account.id)
                .first()

            if (existing == null) {
                bankRepository.addAccount(account)
            }
        }
    }
}
