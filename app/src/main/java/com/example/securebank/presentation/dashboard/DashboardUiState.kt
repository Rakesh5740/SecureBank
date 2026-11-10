package com.example.securebank.presentation.dashboard

import com.example.securebank.core.domain.model.BankAccount

sealed interface DashboardUiState {

    data object Loading : DashboardUiState

    data class Success(
        val account: BankAccount
    ) : DashboardUiState

    data class Error(
        val message: String
    ) : DashboardUiState
}