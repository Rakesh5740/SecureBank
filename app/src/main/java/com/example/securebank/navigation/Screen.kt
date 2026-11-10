package com.example.securebank.navigation

sealed class Screen(val route: String) {

    data object Dashboard : Screen("dashboard")

    data object Home : Screen("home")

    data object Transfer : Screen("transfer")

    data object TransferHistory : Screen("transfer_history/{accountId}") {

        fun createRoute(accountId: String): String {
            return "transfer_history/$accountId"
        }
    }
}