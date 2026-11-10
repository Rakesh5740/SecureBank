package com.example.securebank.presentation.history

import com.example.securebank.core.domain.model.TransferHistory

sealed interface TransferHistoryUiState {
    data object Idle : TransferHistoryUiState
    data object Loading : TransferHistoryUiState

    data class Success(
        val history: List<TransferHistory>
    ) : TransferHistoryUiState

    data class Error(
        val message: String
    ) : TransferHistoryUiState
}