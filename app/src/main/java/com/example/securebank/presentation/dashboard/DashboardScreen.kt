
package com.example.securebank.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

private fun formatBalance(
    balanceInMinorUnits: Long,
    currencyCode: String
): String {
    val locale = when (currencyCode) {
        "USD" -> Locale.US
        "INR" -> Locale("en", "IN")
        else -> Locale.US
    }

    val formatter = NumberFormat.getCurrencyInstance(locale)
    formatter.currency = Currency.getInstance(currencyCode)

    val amount = BigDecimal.valueOf(balanceInMinorUnits, 2)

    return formatter.format(amount)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    accountId: String,
    onTransferClick: () -> Unit,
    onHistoryClick: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(accountId) {
        viewModel.loadAccount(accountId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SecureBank") }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            when (val state = uiState) {

                DashboardUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                is DashboardUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )

                    Button(
                        onClick = {
                            viewModel.loadAccount(accountId)
                        }
                    ) {
                        Text("Retry")
                    }
                }

                is DashboardUiState.Success -> {
                    val account = state.account

                    Text(
                        text = "Welcome, ${account.accountHolderName}",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Available Balance")

                            Text(
                                text = formatBalance(
                                    balanceInMinorUnits = account.balanceInMinorUnits,
                                    currencyCode = account.currency
                                ),
                                style = MaterialTheme.typography.headlineLarge
                            )

                            Text(
                                text = "Account: ****${account.accountNumber.takeLast(4)}"
                            )
                        }
                    }

                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Button(
                        onClick = onTransferClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Transfer Funds")
                    }

                    OutlinedButton(
                        onClick = onHistoryClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Transaction History")
                    }

                    Text("Use the navigation options to transfer funds or view transaction history.")
                }
            }
        }
    }
}
