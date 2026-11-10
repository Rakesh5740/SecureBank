package com.example.securebank.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securebank.core.domain.usecase.GetTransferHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferHistoryViewModel @Inject constructor(
    private val getTransferHistoryUseCase: GetTransferHistoryUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<TransferHistoryUiState>(
            TransferHistoryUiState.Idle
        )

    val uiState = _uiState.asStateFlow()

    fun loadTransferHistory(accountId: String) {
        viewModelScope.launch {

            getTransferHistoryUseCase(accountId)
                .onStart {
                    _uiState.value = TransferHistoryUiState.Loading
                }
                .catch { exception ->
                    if (exception is CancellationException) throw exception

                    _uiState.value = TransferHistoryUiState.Error(
                        exception.message ?: "Something went wrong"
                    )
                }
                .collect { history ->
                    _uiState.value =
                        TransferHistoryUiState.Success(history)
                }
        }
    }
}