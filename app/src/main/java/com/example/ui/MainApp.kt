package com.example.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.DasaniViewModel

sealed class AppDestination(val route: String) {
    object Auth : AppDestination("auth")
    object Home : AppDestination("home")
    object Recharge : AppDestination("recharge")
    object Withdraw : AppDestination("withdraw")
    object Service : AppDestination("service")
    object Devices : AppDestination("devices")
    object Team : AppDestination("team")
    object Share : AppDestination("share")
    object My : AppDestination("my")
}

@Composable
fun MainApp(
    incomingLinkUri: String? = null,
    viewModel: DasaniViewModel = viewModel()
) {
    val context = LocalContext.current
    val userAccount by viewModel.userAccount.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    var currentScreen by remember {
        mutableStateOf<AppDestination>(
            if (userAccount.isLoggedIn) AppDestination.Home else AppDestination.Auth
        )
    }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(incomingLinkUri) {
        if (!incomingLinkUri.isNullOrBlank()) {
            viewModel.applyReferralLink(incomingLinkUri)
        }
    }

    // Toast message listener
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    // Check auth status
    LaunchedEffect(userAccount.isLoggedIn) {
        if (!userAccount.isLoggedIn && currentScreen !is AppDestination.Auth) {
            currentScreen = AppDestination.Auth
        }
    }

    // Back handling
    BackHandler(enabled = currentScreen !is AppDestination.Home && currentScreen !is AppDestination.Auth) {
        currentScreen = AppDestination.Home
    }

    val isBottomBarVisible = currentScreen in listOf(
        AppDestination.Home,
        AppDestination.Devices,
        AppDestination.Team,
        AppDestination.Share,
        AppDestination.My
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    containerColor = Color(0xFF0C1918),
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("bottom_nav_bar")
                ) {
                    // 1. Home Tab (Matching Screenshot)
                    NavigationBarItem(
                        selected = currentScreen is AppDestination.Home,
                        onClick = { currentScreen = AppDestination.Home },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is AppDestination.Home) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DasaniPrimary,
                            selectedTextColor = DasaniPrimary,
                            unselectedIconColor = DasaniTextSecondary,
                            unselectedTextColor = DasaniTextSecondary,
                            indicatorColor = DasaniPrimary.copy(alpha = 0.2f)
                        )
                    )

                    // 2. Devices Tab (Matching Screenshot)
                    NavigationBarItem(
                        selected = currentScreen is AppDestination.Devices,
                        onClick = { currentScreen = AppDestination.Devices },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is AppDestination.Devices) Icons.Filled.PrecisionManufacturing else Icons.Outlined.PrecisionManufacturing,
                                contentDescription = "Devices"
                            )
                        },
                        label = { Text("Devices", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DasaniPrimary,
                            selectedTextColor = DasaniPrimary,
                            unselectedIconColor = DasaniTextSecondary,
                            unselectedTextColor = DasaniTextSecondary,
                            indicatorColor = DasaniPrimary.copy(alpha = 0.2f)
                        )
                    )

                    // 3. Team Tab (Matching Screenshot)
                    NavigationBarItem(
                        selected = currentScreen is AppDestination.Team,
                        onClick = { currentScreen = AppDestination.Team },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is AppDestination.Team) Icons.Filled.Groups else Icons.Outlined.Groups,
                                contentDescription = "Team"
                            )
                        },
                        label = { Text("Team", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DasaniPrimary,
                            selectedTextColor = DasaniPrimary,
                            unselectedIconColor = DasaniTextSecondary,
                            unselectedTextColor = DasaniTextSecondary,
                            indicatorColor = DasaniPrimary.copy(alpha = 0.2f)
                        )
                    )

                    // 4. Share Tab (Matching Screenshot)
                    NavigationBarItem(
                        selected = currentScreen is AppDestination.Share,
                        onClick = { currentScreen = AppDestination.Share },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is AppDestination.Share) Icons.Filled.Share else Icons.Outlined.Share,
                                contentDescription = "Share"
                            )
                        },
                        label = { Text("Share", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DasaniPrimary,
                            selectedTextColor = DasaniPrimary,
                            unselectedIconColor = DasaniTextSecondary,
                            unselectedTextColor = DasaniTextSecondary,
                            indicatorColor = DasaniPrimary.copy(alpha = 0.2f)
                        )
                    )

                    // 5. My Tab (Matching Screenshot)
                    NavigationBarItem(
                        selected = currentScreen is AppDestination.My,
                        onClick = { currentScreen = AppDestination.My },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is AppDestination.My) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "My"
                            )
                        },
                        label = { Text("My", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DasaniPrimary,
                            selectedTextColor = DasaniPrimary,
                            unselectedIconColor = DasaniTextSecondary,
                            unselectedTextColor = DasaniTextSecondary,
                            indicatorColor = DasaniPrimary.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        },
        containerColor = DasaniBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (currentScreen) {
                is AppDestination.Auth -> {
                    AuthScreen(
                        viewModel = viewModel,
                        onAuthSuccess = { currentScreen = AppDestination.Home }
                    )
                }

                is AppDestination.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToRecharge = { currentScreen = AppDestination.Recharge },
                        onNavigateToWithdraw = { currentScreen = AppDestination.Withdraw },
                        onNavigateToService = { currentScreen = AppDestination.Service },
                        onOpenTelegramChannel = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/GalaxySeederOfficial"))
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                        onNavigateToDevices = { currentScreen = AppDestination.Devices }
                    )
                }

                is AppDestination.Recharge -> {
                    RechargeScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppDestination.Home }
                    )
                }

                is AppDestination.Withdraw -> {
                    WithdrawScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppDestination.Home }
                    )
                }

                is AppDestination.Service -> {
                    ServiceScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppDestination.Home }
                    )
                }

                is AppDestination.Devices -> {
                    DevicesScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppDestination.Home },
                        onExploreProducts = { currentScreen = AppDestination.Home }
                    )
                }

                is AppDestination.Team -> {
                    TeamScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppDestination.Home }
                    )
                }

                is AppDestination.Share -> {
                    ShareScreen(
                        viewModel = viewModel
                    )
                }

                is AppDestination.My -> {
                    MyProfileScreen(
                        viewModel = viewModel,
                        onNavigateToRecharge = { currentScreen = AppDestination.Recharge },
                        onNavigateToWithdraw = { currentScreen = AppDestination.Withdraw },
                        onNavigateToDevices = { currentScreen = AppDestination.Devices },
                        onNavigateToTeam = { currentScreen = AppDestination.Team },
                        onNavigateToService = { currentScreen = AppDestination.Service },
                        onLogout = { currentScreen = AppDestination.Auth }
                    )
                }
            }
        }
    }
}
