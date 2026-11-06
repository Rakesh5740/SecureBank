package com.example.securebank.presentation.transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securebank.core.domain.model.FundTransfer
import com.example.securebank.core.domain.model.TransferStatus
import com.example.securebank.core.domain.usecase.SeedDemoAccountsUseCase
import com.example.securebank.core.domain.usecase.TransferFundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val transferFundsUseCase: TransferFundsUseCase,
    private val seedDemoAccountsUseCase: SeedDemoAccountsUseCase
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
        amountInPaise: Long
    ) {
        if (!_isInitialized.value) return

        viewModelScope.launch {
            _uiState.value = TransferUiState.Loading

            val transfer = FundTransfer(
                id = java.util.UUID.randomUUID().toString(),
                sourceAccountId = sourceAccountId,
                beneficiaryId = beneficiaryId,
                amountInPaise = amountInPaise,
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
        }
    }

    fun resetState() {
        _uiState.value = TransferUiState.Idle
    }
}
