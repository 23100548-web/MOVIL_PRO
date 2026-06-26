package com.soltis.p2p

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.soltis.p2p.ui.screens.InitialBrandingScreen
import com.soltis.p2p.ui.screens.LoginScreen
import com.soltis.p2p.ui.screens.RegisterScreen
import com.soltis.p2p.ui.screens.DashboardScreen
import com.soltis.p2p.ui.screens.DepositScreen
import com.soltis.p2p.ui.screens.WithdrawScreen
import com.soltis.p2p.ui.screens.TransferScreen
import com.soltis.p2p.ui.screens.MovementsScreen
import com.soltis.p2p.ui.theme.NexusPayTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

object Routes {
    const val SPLASH      = "splash"
    const val LOGIN       = "login"
    const val REGISTER    = "register"
    const val DASHBOARD   = "dashboard"
    const val DEPOSIT     = "deposit"
    const val WITHDRAW    = "withdraw"
    const val TRANSFER    = "transfer"
    const val MOVEMENTS   = "movements"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Instalamos la API pero no la dejamos mostrar nada
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NexusPayTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Routes.SPLASH) {
                        composable(Routes.SPLASH) {
                            InitialBrandingScreen(onTimeout = {
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.SPLASH) { inclusive = true }
                                }
                            })
                        }
                        composable(Routes.LOGIN) {
                            LoginScreen(
                                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                                onLoginSuccess = {
                                    navController.navigate(Routes.DASHBOARD) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Routes.REGISTER) {
                            RegisterScreen(
                                onNavigateToLogin = { navController.popBackStack() },
                                onRegisterSuccess = {
                                    navController.navigate(Routes.DASHBOARD) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Routes.DASHBOARD) {
                            DashboardScreen(
                                onNavigateToDeposit   = { navController.navigate(Routes.DEPOSIT) },
                                onNavigateToWithdraw  = { navController.navigate(Routes.WITHDRAW) },
                                onNavigateToTransfer  = { navController.navigate(Routes.TRANSFER) },
                                onNavigateToMovements = { navController.navigate(Routes.MOVEMENTS) },
                                onLogout = {
                                    navController.navigate(Routes.LOGIN) {
                                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Routes.DEPOSIT) {
                            DepositScreen(onBack = { navController.popBackStack() })
                        }
                        composable(Routes.WITHDRAW) {
                            WithdrawScreen(onBack = { navController.popBackStack() })
                        }
                        composable(Routes.TRANSFER) {
                            TransferScreen(onBack = { navController.popBackStack() })
                        }
                        composable(Routes.MOVEMENTS) {
                            MovementsScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
