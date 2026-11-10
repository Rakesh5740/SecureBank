
package com.example.securebank.presentation.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securebank.core.domain.model.FundTransfer
import com.example.securebank.core.domain.model.TransferStatus
import com.example.securebank.core.domain.usecase.GetAccountUseCase
import com.example.securebank.core.domain.usecase.SeedDemoAccountsUseCase
import com.example.securebank.core.domain.usecase.TransferFundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val transferFundsUseCase: TransferFundsUseCase,
    private val seedDemoAccountsUseCase: SeedDemoAccountsUseCase,
    private val getAccountUseCase: GetAccountUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<TransferUiState>(TransferUiState.Idle)

    val uiState: StateFlow<TransferUiState> =
        _uiState.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            seedDemoAccountsUseCase()
            _isInitialized.value = true
        }
    }

    fun transferFunds(
        sourceAccountId: String,
        beneficiaryId: String,
        amountInMinorUnits: Long
    ) {
        if (!_isInitialized.value) return

        viewModelScope.launch {
            _uiState.value = TransferUiState.Loading

            try {
                val sourceAccount = getAccountUseCase(sourceAccountId).first()
                    ?: throw IllegalArgumentException("Source account not found")

                val beneficiaryAccount = getAccountUseCase(beneficiaryId).first()
                    ?: throw IllegalArgumentException("Beneficiary account not found")

                if (sourceAccount.currency != beneficiaryAccount.currency) {
                    throw IllegalArgumentException(
                        "Cannot transfer between different currencies"
                    )
                }

                if (amountInMinorUnits <= 0L) {
                    throw IllegalArgumentException(
                        "Enter a valid transfer amount"
                    )
                }

                val transfer = FundTransfer(
                    id = java.util.UUID.randomUUID().toString(),
                    sourceAccountId = sourceAccountId,
                    beneficiaryId = beneficiaryId,
                    amountInMinorUnits = amountInMinorUnits,
                    currency = sourceAccount.currency,
                    status = TransferStatus.PENDING
                )

                val result = transferFundsUseCase(transfer)

                _uiState.value = result.fold(
                    onSuccess = {
                        TransferUiState.Success(
                            message = "Fund transfer successful",
                            transactionId = it.id
                        )
                    },
                    onFailure = {
                        TransferUiState.Error(
                            message = it.message ?: "Transfer failed"
                        )
                    }
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = TransferUiState.Error(
                    message = e.message ?: "Transfer failed"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = TransferUiState.Idle
    }
}
