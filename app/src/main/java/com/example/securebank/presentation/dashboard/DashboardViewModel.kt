
package com.example.securebank.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securebank.core.domain.usecase.FetchRemoteAccountsUseCase
import com.example.securebank.core.domain.usecase.GetAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val fetchRemoteAccountsUseCase: FetchRemoteAccountsUseCase,
    private val getAccountUseCase: GetAccountUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)

    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadAccount(accountId: String) {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading

            val result = fetchRemoteAccountsUseCase()

            if (result.isFailure) {
                _uiState.value = DashboardUiState.Error(
                    result.exceptionOrNull()?.message
                        ?: "Unable to fetch bank accounts"
                )
                return@launch
            }

            getAccountUseCase(accountId)
                .catch { exception ->
                    _uiState.value = DashboardUiState.Error(
                        exception.message ?: "Unable to load account"
                    )
                }
                .collect { account ->
                    _uiState.value = if (account != null) {
                        DashboardUiState.Success(account)
                    } else {
                        DashboardUiState.Error("Account not found")
                    }
                }
        }
    }
}
