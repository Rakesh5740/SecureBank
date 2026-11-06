package com.example.securebank.core.domain.repository

import com.example.securebank.core.domain.model.BankAccount
import com.example.securebank.core.domain.model.FundTransfer
import kotlinx.coroutines.flow.Flow


interface BankRepository {

    fun observeAccount(accountId: String): Flow<BankAccount?>

    suspend fun addAccount(account: BankAccount)

    suspend fun transferFunds(transfer: FundTransfer): Result<FundTransfer>
}