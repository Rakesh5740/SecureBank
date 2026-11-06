
package com.example.securebank.presentation.transfer

sealed interface TransferUiState {

    data object Idle : TransferUiState

    data object Loading : TransferUiState

    data class Success(
        val message: String,
        val transactionId: String
    ) : TransferUiState

    data class Error(
        val message: String
    ) : TransferUiState
}
