package com.example.securebank.core.domain.usecase

import com.example.securebank.core.domain.model.BankAccount
import com.example.securebank.core.domain.model.FundTransfer
import com.example.securebank.core.domain.model.TransferHistory
import com.example.securebank.core.domain.repository.BankRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class GetTransferHistoryUseCaseTest {

    private lateinit var fakeBankRepository: FakeBankRepository
    private lateinit var transferHistoryUseCase: GetTransferHistoryUseCase

    @Before
    fun setup(){
        fakeBankRepository = FakeBankRepository()
        transferHistoryUseCase = GetTransferHistoryUseCase(fakeBankRepository)
    }


    @Test
    fun `invoke returns transfer history for account`() = runBlocking {

        val accountId = "ACC-001"

        val result = transferHistoryUseCase(accountId).toList()

        assertEquals(emptyList<TransferHistory>(), result.first())
        assertEquals(accountId, fakeBankRepository.requestedAccountId)
    }

    private class FakeBankRepository : BankRepository {

        var requestedAccountId: String? = null

        override fun observeAccount(
            accountId: String
        ): Flow<BankAccount?> {
            return flowOf(null)
        }

        override fun observeTransferHistory(
            accountId: String
        ): Flow<List<TransferHistory>> {
            requestedAccountId = accountId
            return flowOf(emptyList())
        }

        override suspend fun addAccount(account: BankAccount) {
            // Not required for this test
        }

        override suspend fun transferFunds(
            transfer: FundTransfer
        ): Result<FundTransfer> {
            return Result.failure(
                UnsupportedOperationException("Not implemented")
            )
        }
    }
}