package com.pairlink.app.ui.navigation

/**
 * Sealed class representing all navigation destinations in PairLink.
 */
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object RegisterSuccess : Screen("register_success")
    data object Connect : Screen("connect")
    data object PairRequest : Screen("pair_request")
    data object ConnectedForever : Screen("connected_forever")
    data object RelationshipSetup : Screen("relationship_setup")
    data object Home : Screen("home")
    data object MoodHub : Screen("mood_hub")
    data object Mood : Screen("mood")
    data object Status : Screen("status")
    data object Updates : Screen("updates")
    data object Birthday : Screen("birthday")
    data object Anniversary : Screen("anniversary")
    data object Profile : Screen("profile")
    data object EditProfile : Screen("edit_profile")
    data object Settings : Screen("settings")
    data object Unpair : Screen("unpair")
}
