package com.example.opendatajabar.ui.navigation

sealed class Screen(val route: String) {
    object DataEntry : Screen("dataEntry")
    object DataList : Screen("dataList")
    object Edit : Screen("edit/{id}")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object DetailReward : Screen("home/{rewardId}") {
        fun createRoute(rewardId: Long) = "home/$rewardId"
    }
}