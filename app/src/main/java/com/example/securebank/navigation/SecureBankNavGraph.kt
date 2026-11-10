package com.example.securebank.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.securebank.presentation.dashboard.DashboardScreen
import com.example.securebank.presentation.transfer.TransferScreen
import com.example.securebank.presentation.history.TransferHistoryScreen
import com.example.securebank.presentation.history.TransferHistoryViewModel

@Composable
fun SecureBankNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                accountId = "1",
                onTransferClick = {
                    navController.navigate(Screen.Transfer.route)
                },
                onHistoryClick = {
                    navController.navigate(
                        Screen.TransferHistory.createRoute("1")
                    )
                }
            )
        }

        composable(Screen.Transfer.route) {
            TransferScreen(
                onViewHistory = { accountId ->
                    navController.navigate(
                        Screen.TransferHistory.createRoute(accountId)
                    )
                }
            )
        }

        composable(
            route = Screen.TransferHistory.route,
            arguments = listOf(
                navArgument("accountId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val accountId = requireNotNull(
                backStackEntry.arguments?.getString("accountId")
            )

            val viewModel: TransferHistoryViewModel = hiltViewModel()

            TransferHistoryScreen(
                viewModel = viewModel,
                accountId = accountId
            )
        }
    }
}