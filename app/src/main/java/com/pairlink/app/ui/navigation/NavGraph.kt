package com.pairlink.app.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pairlink.app.domain.model.PartnerProfile
import com.pairlink.app.ui.components.BottomNavTab
import com.pairlink.app.ui.screens.anniversary.AnniversaryScreen
import com.pairlink.app.ui.screens.auth.LoginScreen
import com.pairlink.app.ui.screens.auth.RegisterScreen
import com.pairlink.app.ui.screens.auth.RegisterSuccessScreen
import com.pairlink.app.ui.screens.splash.SplashScreen
import com.pairlink.app.ui.screens.welcome.WelcomeScreen
import com.pairlink.app.ui.screens.birthday.BirthdayScreen
import com.pairlink.app.ui.screens.home.HomeScreen
import com.pairlink.app.ui.screens.mood.MoodScreen
import com.pairlink.app.ui.screens.mood.MoodStatusHubScreen
import com.pairlink.app.ui.screens.pairing.ConnectScreen
import com.pairlink.app.ui.screens.pairing.ConnectedForeverScreen
import com.pairlink.app.ui.screens.pairing.PairRequestDialog
import com.pairlink.app.ui.screens.pairing.RelationshipSetupScreen
import com.pairlink.app.ui.screens.profile.EditProfileScreen
import com.pairlink.app.ui.screens.profile.ProfileScreen
import com.pairlink.app.ui.screens.settings.SettingsScreen
import com.pairlink.app.ui.screens.settings.UnpairDialog
import com.pairlink.app.ui.screens.status.UpdateStatusScreen
import com.pairlink.app.ui.screens.updates.UpdatesScreen
import com.pairlink.app.ui.viewmodel.AuthUiState
import com.pairlink.app.ui.viewmodel.AuthViewModel
import com.pairlink.app.ui.viewmodel.BirthdayViewModel
import com.pairlink.app.ui.viewmodel.HomeViewModel
import com.pairlink.app.ui.viewmodel.MoodViewModel
import com.pairlink.app.ui.viewmodel.NotificationsViewModel
import com.pairlink.app.ui.viewmodel.PairUiState
import com.pairlink.app.ui.viewmodel.PairViewModel
import com.pairlink.app.ui.viewmodel.PresenceUiState
import com.pairlink.app.ui.viewmodel.PresenceViewModel
import com.pairlink.app.ui.viewmodel.ProfileViewModel
import com.pairlink.app.ui.viewmodel.SettingsViewModel
import com.pairlink.app.ui.viewmodel.StatusViewModel

/**
 * Main Navigation Graph orchestrating all screen routes, ViewModels, and live real-time synchronization.
 */
@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentTab = when (currentRoute) {
        Screen.Home.route -> BottomNavTab.HOME
        Screen.MoodHub.route, Screen.Mood.route, Screen.Status.route -> BottomNavTab.MOOD
        Screen.Updates.route, Screen.Birthday.route, Screen.Anniversary.route -> BottomNavTab.UPDATES
        Screen.Settings.route, Screen.Profile.route, Screen.EditProfile.route -> BottomNavTab.SETTINGS
        else -> BottomNavTab.HOME
    }

    val onTabSelected: (BottomNavTab) -> Unit = { tab ->
        val targetRoute = when (tab) {
            BottomNavTab.HOME -> Screen.Home.route
            BottomNavTab.MOOD -> Screen.MoodHub.route
            BottomNavTab.UPDATES -> Screen.Updates.route
            BottomNavTab.SETTINGS -> Screen.Settings.route
        }
        if (tab == BottomNavTab.HOME) {
            navController.navigate(Screen.Home.route) {
                popUpTo(0) { inclusive = false }
                launchSingleTop = true
            }
        } else if (currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(Screen.Home.route) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            // 1. Splash Screen
            composable(Screen.Splash.route) {
                val authViewModel: AuthViewModel = hiltViewModel()
                SplashScreen(
                    onRestoreSession = { authViewModel.restoreSession() },
                    onSplashComplete = { loggedIn ->
                        val targetRoute = if (loggedIn) Screen.Home.route else Screen.Welcome.route
                        navController.navigate(targetRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // 2. Welcome Screen
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }

            // 3. Login Screen
            composable(Screen.Login.route) {
                val authViewModel: AuthViewModel = hiltViewModel()
                val authState by authViewModel.uiState.collectAsState()
                LoginScreen(
                    onLoginSubmit = { phone, pin ->
                        authViewModel.login(phone, pin) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    onNavigateToRegister = {
                        authViewModel.clearError()
                        navController.navigate(Screen.Register.route)
                    },
                    isLoading = authState is AuthUiState.Loading,
                    errorMessage = (authState as? AuthUiState.Error)?.message,
                    onClearError = { authViewModel.clearError() }
                )
            }

            // 4. Register Screen
            composable(Screen.Register.route) {
                val authViewModel: AuthViewModel = hiltViewModel()
                val authState by authViewModel.uiState.collectAsState()
                RegisterScreen(
                    onRegisterSubmit = { username, phone, dob, pin, confirmPin, imageUri ->
                        authViewModel.register(username, phone, dob, pin, confirmPin, imageUri) {
                            navController.navigate(Screen.RegisterSuccess.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                        }
                    },
                    onNavigateToLogin = {
                        authViewModel.clearError()
                        navController.navigate(Screen.Login.route)
                    },
                    isLoading = authState is AuthUiState.Loading,
                    errorMessage = (authState as? AuthUiState.Error)?.message,
                    onClearError = { authViewModel.clearError() }
                )
            }

            // 5. Registration Success Screen
            composable(Screen.RegisterSuccess.route) {
                val authViewModel: AuthViewModel = hiltViewModel()
                val user by authViewModel.currentUser.collectAsState()
                RegisterSuccessScreen(
                    username = user.username,
                    partnerId = user.partnerId,
                    onNavigateToConnect = { navController.navigate(Screen.Connect.route) }
                )
            }

            // 6. Connect Screen
            composable(Screen.Connect.route) {
                val pairViewModel: PairViewModel = hiltViewModel()
                val pairState by pairViewModel.uiState.collectAsState()
                val user by pairViewModel.currentUser.collectAsState()
                val incomingRequests by pairViewModel.incomingRequests.collectAsState()
                val latestIncoming = incomingRequests.firstOrNull()

                // Auto-navigate to celebration screen when pairing is established
                LaunchedEffect(user.isPaired) {
                    if (user.isPaired) {
                        navController.navigate(Screen.ConnectedForever.route) {
                            popUpTo(Screen.Connect.route) { inclusive = true }
                        }
                    }
                }

                ConnectScreen(
                    myPartnerId = user.partnerId,
                    partnerPreview = (pairState as? PairUiState.FoundPartner)?.partner,
                    onSearchPartner = { id -> pairViewModel.searchPartner(id) },
                    onConnectPartner = { id ->
                        pairViewModel.sendPairRequest(id) {
                            // Request sent; waiting for partner to accept
                        }
                    },
                    isLoading = pairState is PairUiState.Searching || pairState is PairUiState.Sending || pairState is PairUiState.Loading,
                    errorMessage = (pairState as? PairUiState.Error)?.message,
                    onClearError = { pairViewModel.clearError() }
                )

                // Show Incoming Request popup if partner sent a pair request to this device
                if (latestIncoming != null && !user.isPaired) {
                    PairRequestDialog(
                        partner = PartnerProfile(),
                        pairRequest = latestIncoming,
                        isLoading = pairState is PairUiState.Loading,
                        onAccept = {
                            pairViewModel.acceptPairRequest(latestIncoming.requestId) {
                                // Handled by LaunchedEffect(user.isPaired)
                            }
                        },
                        onReject = {
                            pairViewModel.rejectPairRequest(latestIncoming.requestId) { }
                        }
                    )
                }
            }

            // 7. Pair Request Screen
            composable(Screen.PairRequest.route) {
                val pairViewModel: PairViewModel = hiltViewModel()
                val pairState by pairViewModel.uiState.collectAsState()
                val user by pairViewModel.currentUser.collectAsState()
                val incomingRequests by pairViewModel.incomingRequests.collectAsState()
                val latestRequest = incomingRequests.firstOrNull()

                LaunchedEffect(user.isPaired) {
                    if (user.isPaired) {
                        navController.navigate(Screen.ConnectedForever.route) {
                            popUpTo(Screen.PairRequest.route) { inclusive = true }
                        }
                    }
                }

                if (latestRequest != null) {
                    PairRequestDialog(
                        partner = PartnerProfile(),
                        pairRequest = latestRequest,
                        isLoading = pairState is PairUiState.Loading,
                        onAccept = {
                            pairViewModel.acceptPairRequest(latestRequest.requestId) {
                                // Handled by LaunchedEffect(user.isPaired)
                            }
                        },
                        onReject = {
                            pairViewModel.rejectPairRequest(latestRequest.requestId) {
                                navController.popBackStack()
                            }
                        }
                    )
                } else if (!user.isPaired) {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }

            // 8. Connected Forever Celebration Screen
            composable(Screen.ConnectedForever.route) {
                val pairViewModel: PairViewModel = hiltViewModel()
                val user by pairViewModel.currentUser.collectAsState()
                val partner by pairViewModel.partnerProfile.collectAsState()

                ConnectedForeverScreen(
                    userAvatar = user.profileImageUrl,
                    partnerAvatar = partner?.avatarUrl ?: "",
                    onContinue = {
                        navController.navigate(Screen.RelationshipSetup.route) {
                            popUpTo(Screen.ConnectedForever.route) { inclusive = true }
                        }
                    }
                )
            }

            // 9. Personalize / Relationship Setup Screen
            composable(Screen.RelationshipSetup.route) {
                val pairViewModel: PairViewModel = hiltViewModel()

                RelationshipSetupScreen(
                    onSaveStartDate = { startDate ->
                        pairViewModel.saveRelationshipDate(startDate) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // 10. Home Screen with Live Presence
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                val presenceViewModel: PresenceViewModel = hiltViewModel()

                val homeState by homeViewModel.uiState.collectAsState()
                val presenceState by presenceViewModel.uiState.collectAsState()
                val presenceRingState by presenceViewModel.ringState.collectAsState()

                val isPartnerHolding = presenceState is PresenceUiState.PartnerHolding

                HomeScreen(
                    partner = homeState.partner,
                    user = homeState.user,
                    partnerDisplayName = homeState.user.partnerNickname,
                    togetherTime = homeState.togetherTimeText,
                    birthdayDaysLeft = homeState.birthdayDaysLeftText,
                    heartbeatCount = homeState.heartbeatCount,
                    isPartnerHolding = isPartnerHolding,
                    presenceRingState = presenceRingState,
                    onHeartPressed = { presenceViewModel.onHeartPressed() },
                    onHeartReleased = { presenceViewModel.onHeartReleased() },
                    onSendHeart = { homeViewModel.sendHeartPulse() },
                    onNavigateToBirthday = { navController.navigate(Screen.Birthday.route) },
                    onNavigateToRelationship = { navController.navigate(Screen.Anniversary.route) },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    currentTab = currentTab,
                    onTabSelected = onTabSelected
                )
            }

            // 11. Mood & Status Hub Screen (Two interactive boxes for User's Mood and Status)
            composable(Screen.MoodHub.route) {
                val authViewModel: AuthViewModel = hiltViewModel()
                val userState by authViewModel.currentUser.collectAsState()

                MoodStatusHubScreen(
                    user = userState,
                    onNavigateToMood = { navController.navigate(Screen.Mood.route) },
                    onNavigateToStatus = { navController.navigate(Screen.Status.route) },
                    onBackClick = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Home.route)
                        }
                    },
                    currentTab = currentTab,
                    onTabSelected = onTabSelected
                )
            }

            // 12. Mood Selection Screen
            composable(Screen.Mood.route) {
                val moodViewModel: MoodViewModel = hiltViewModel()
                val moodState by moodViewModel.uiState.collectAsState()
                MoodScreen(
                    currentMood = moodState.currentMood,
                    availableMoods = moodState.availableMoods,
                    onSaveMood = { mood ->
                        moodViewModel.saveMood(mood)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onDeleteMood = { moodViewModel.deleteCustomMood(it) },
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Home.route)
                        }
                    },
                    currentTab = currentTab,
                    onTabSelected = onTabSelected
                )
            }

            // 13. Status Selection Screen
            composable(Screen.Status.route) {
                val statusViewModel: StatusViewModel = hiltViewModel()
                val statusState by statusViewModel.uiState.collectAsState()
                UpdateStatusScreen(
                    currentStatus = statusState.currentStatus,
                    lastUpdated = statusState.lastUpdated,
                    availableStatuses = statusState.availableStatuses,
                    onUpdateStatus = { status ->
                        statusViewModel.updateStatus(status)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onAddCustomStatus = { status ->
                        statusViewModel.addCustomStatus(status)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onDeleteCustomStatus = { statusViewModel.deleteCustomStatus(it) },
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Home.route)
                        }
                    },
                    currentTab = currentTab,
                    onTabSelected = onTabSelected
                )
            }

            // 14. Updates Screen
            composable(Screen.Updates.route) {
                val notifViewModel: NotificationsViewModel = hiltViewModel()
                val notifState by notifViewModel.uiState.collectAsState()
                UpdatesScreen(
                    notifications = notifState.notifications,
                    unreadCount = notifState.unreadCount,
                    searchQuery = notifState.searchQuery,
                    selectedFilter = notifState.selectedFilter,
                    onSearchQueryChanged = { notifViewModel.onSearchQueryChanged(it) },
                    onFilterSelected = { notifViewModel.onFilterSelected(it) },
                    onNotificationClick = { notifViewModel.markAsRead(it.id) },
                    onMarkAllAsRead = { notifViewModel.markAllAsRead() },
                    onDeleteNotification = { notifViewModel.deleteNotification(it) },
                    currentTab = currentTab,
                    onTabSelected = onTabSelected
                )
            }

            // 15. Birthday Screen
            composable(Screen.Birthday.route) {
                val birthdayViewModel: BirthdayViewModel = hiltViewModel()
                val birthdayState by birthdayViewModel.uiState.collectAsState()
                BirthdayScreen(
                    partner = birthdayState.partner,
                    daysLeft = birthdayState.daysLeft,
                    formattedBirthday = birthdayState.formattedBirthday,
                    wishlist = birthdayState.wishlist,
                    onAddWishlistItem = { item -> birthdayViewModel.addWishlistItem(item) },
                    onDeleteWishlistItem = { id -> birthdayViewModel.deleteWishlistItem(id) },
                    onNavigateBack = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    currentTab = currentTab,
                    onTabSelected = onTabSelected
                )
            }

            // 16. Anniversary Screen
            composable(Screen.Anniversary.route) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                val homeState by homeViewModel.uiState.collectAsState()
                AnniversaryScreen(
                    togetherTime = homeState.togetherTimeText,
                    startDate = homeState.user.relationshipStartDate,
                    onSaveStartDate = { date -> homeViewModel.saveRelationshipDate(date) },
                    onNavigateBack = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    currentTab = currentTab,
                    onTabSelected = onTabSelected
                )
            }

            // 17. Profile Screen
            composable(Screen.Profile.route) {
                val homeViewModel: HomeViewModel = hiltViewModel()
                val homeState by homeViewModel.uiState.collectAsState()
                ProfileScreen(
                    partner = homeState.partner,
                    userNickname = homeState.user.partnerNickname,
                    onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) },
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Home.route)
                        }
                    },
                    currentTab = currentTab,
                    onTabSelected = onTabSelected
                )
            }

            // 18. Edit Profile Screen
            composable(Screen.EditProfile.route) {
                val profileViewModel: ProfileViewModel = hiltViewModel()
                val profileState by profileViewModel.uiState.collectAsState()
                val isSaving by profileViewModel.isSaving.collectAsState()

                EditProfileScreen(
                    user = profileState.user,
                    isLoading = isSaving,
                    onSaveProfile = { username, nickname, avatarUri ->
                        profileViewModel.saveProfile(username, nickname, avatarUri) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    },
                    onNavigateBack = {
                        if (!navController.popBackStack()) {
                            navController.navigate(Screen.Home.route)
                        }
                    },
                    currentTab = currentTab,
                    onTabSelected = onTabSelected
                )
            }

            // 19. Settings Screen
            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                val settingsState by settingsViewModel.uiState.collectAsState()

                SettingsScreen(
                    partner = settingsState.partner,
                    user = settingsState.user,
                    preferences = settingsState.preferences,
                    onTogglePresence = { settingsViewModel.togglePresenceNotification() },
                    onToggleMood = { settingsViewModel.toggleMoodNotification() },
                    onToggleStatus = { settingsViewModel.toggleStatusNotification() },
                    onToggleReachedHome = { settingsViewModel.toggleReachedHomeNotification() },
                    onToggleBirthday = { settingsViewModel.toggleBirthdayNotification() },
                    onToggleAnniversary = { settingsViewModel.toggleAnniversaryNotification() },
                    onToggleSystem = { settingsViewModel.toggleSystemNotification() },
                    onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                    onNavigateToUnpair = { navController.navigate(Screen.Unpair.route) },
                    onLogout = {
                        settingsViewModel.logout {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    currentTab = currentTab,
                    onTabSelected = onTabSelected
                )
            }

            // 20. Unpair Dialog
            composable(Screen.Unpair.route) {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                UnpairDialog(
                    onConfirmUnpair = {
                        settingsViewModel.unpair {
                            navController.navigate(Screen.Connect.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
    }
}
