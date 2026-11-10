package com.example.securebank

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.securebank.core.domain.model.BankAccount
import com.example.securebank.core.domain.model.FundTransfer
import com.example.securebank.core.domain.model.TransferHistory
import com.example.securebank.core.domain.repository.BankRepository
import com.example.securebank.core.domain.usecase.GetTransferHistoryUseCase
import com.example.securebank.presentation.history.TransferHistoryUiState
import com.example.securebank.presentation.history.TransferHistoryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransferHistoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeBankRepository
    private lateinit var viewModel: TransferHistoryViewModel

    @Before
    fun setUp() {
        repository = FakeBankRepository()

        viewModel = TransferHistoryViewModel(
            GetTransferHistoryUseCase(repository)
        )
    }

    @Test
    fun `initial state is idle`() {
        assertEquals(
            TransferHistoryUiState.Idle,
            viewModel.uiState.value
        )
    }

    @Test
    fun `load transfer history returns success`() = runTest(
        mainDispatcherRule.dispatcher
    ) {
        val accountId = "ACC-001"

        viewModel.loadTransferHistory(accountId)

        advanceUntilIdle()

        assertEquals(
            TransferHistoryUiState.Success(emptyList()),
            viewModel.uiState.value
        )

        assertEquals(accountId, repository.requestedAccountId)
    }

    @Test
    fun `load transfer history returns error when flow fails`() =
        runTest(mainDispatcherRule.dispatcher) {

            repository.shouldThrowError = true

            viewModel.loadTransferHistory("ACC-001")

            advanceUntilIdle()

            val result = viewModel.uiState.value

            assertTrue(result is TransferHistoryUiState.Error)

            assertEquals(
                "Something went wrong",
                (result as TransferHistoryUiState.Error).message
            )
        }

    private class FakeBankRepository : BankRepository {

        var requestedAccountId: String? = null
        var shouldThrowError = false

        override fun observeAccount(
            accountId: String
        ): Flow<BankAccount?> {
            return flowOf(null)
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

        override fun observeTransferHistory(
            accountId: String
        ): Flow<List<TransferHistory>> {

            requestedAccountId = accountId

            return if (shouldThrowError) {
                flow {
                    throw RuntimeException("Something went wrong")
                }
            } else {
                flowOf(emptyList())
            }
        }
    }
}