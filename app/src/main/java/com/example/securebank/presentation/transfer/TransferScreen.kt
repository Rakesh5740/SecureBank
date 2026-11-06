
package com.example.securebank.presentation.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun TransferScreen(
    viewModel: TransferViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var sourceAccountId by remember { mutableStateOf("") }
    var beneficiaryId by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Fund Transfer",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = sourceAccountId,
            onValueChange = { sourceAccountId = it },
            label = { Text("Source Account ID") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = beneficiaryId,
            onValueChange = { beneficiaryId = it },
            label = { Text("Beneficiary Account ID") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount (₹)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                val amountInPaise = runCatching {
                    BigDecimal(amount)
                        .movePointRight(2)
                        .setScale(0, RoundingMode.UNNECESSARY)
                        .longValueExact()
                }.getOrNull()

                if (
                    sourceAccountId.isNotBlank() &&
                    beneficiaryId.isNotBlank() &&
                    amountInPaise != null &&
                    amountInPaise > 0
                ) {
                    viewModel.transferFunds(
                        sourceAccountId = sourceAccountId.trim(),
                        beneficiaryId = beneficiaryId.trim(),
                        amountInPaise = amountInPaise
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState !is TransferUiState.Loading
        ) {
            Text("Transfer Money")
        }

        when (val state = uiState) {
            TransferUiState.Idle -> Unit

            TransferUiState.Loading -> {
                CircularProgressIndicator()
                Text("Processing transfer...")
            }

            is TransferUiState.Success -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("Transaction ID: ${state.transactionId}")
                TextButton(onClick = {
                    viewModel.resetState()
                    amount = ""
                }) {
                    Text("Make another transfer")
                }
            }

            is TransferUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
                TextButton(onClick = viewModel::resetState) {
                    Text("Try again")
                }
            }
        }
    }
}
