package com.example.securebank.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.securebank.core.domain.model.TransferHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransferHistoryScreen(
    viewModel: TransferHistoryViewModel,
    accountId: String
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(accountId) {
        viewModel.loadTransferHistory(accountId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Transfer History",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {

            TransferHistoryUiState.Idle,
            TransferHistoryUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is TransferHistoryUiState.Success -> {
                if (state.history.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transfer history found")
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.history,
                            key = { it.id }
                        ) { transfer ->
                            TransferHistoryItem(transfer)
                        }
                    }
                }
            }

            is TransferHistoryUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(state.message)

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.loadTransferHistory(accountId)
                        }
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@Composable
private fun TransferHistoryItem(
    transfer: TransferHistory
) {
    val date = remember(transfer.createdAtEpochMillis) {
        SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            Locale.getDefault()
        ).format(Date(transfer.createdAtEpochMillis))
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "To: ${transfer.beneficiaryId}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "₹${transfer.amountInMinorUnits / 100.0}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = transfer.status.name,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}