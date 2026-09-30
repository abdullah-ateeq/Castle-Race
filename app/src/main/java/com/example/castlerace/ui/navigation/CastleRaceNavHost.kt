package com.example.castlerace.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.castlerace.ui.screens.AboutScreen
import com.example.castlerace.ui.screens.GameScreen
import com.example.castlerace.ui.screens.HomeScreen
import com.example.castlerace.ui.screens.HowToPlayScreen
import com.example.castlerace.ui.screens.ResultScreen
import com.example.castlerace.ui.screens.SettingsScreen
import com.example.castlerace.ui.screens.SplashScreen
import com.example.castlerace.ui.viewmodel.GameViewModel
import com.example.castlerace.ui.viewmodel.SettingsViewModel

@Composable
fun CastleRaceNavHost(
    gameViewModel: GameViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val gameState by gameViewModel.state.collectAsState()
    val stats by settingsViewModel.stats.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        // 1. Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                stats = stats,
                onPlayClick = {
                    gameViewModel.restartGame()
                    navController.navigate(Screen.Game.route)
                },
                onHowToPlayClick = {
                    navController.navigate(Screen.HowToPlay.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onAboutClick = {
                    navController.navigate(Screen.About.route)
                }
            )
        }

        // 3. Game Screen
        composable(Screen.Game.route) {
            GameScreen(
                viewModel = gameViewModel,
                onNavigateToResult = {
                    navController.navigate(Screen.Result.route) {
                        popUpTo(Screen.Game.route) { inclusive = false }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // 4. Result Screen
        composable(Screen.Result.route) {
            ResultScreen(
                state = gameState,
                onPlayAgainClick = {
                    gameViewModel.startNewGame()
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                },
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // 5. How to Play Screen
        composable(Screen.HowToPlay.route) {
            HowToPlayScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // 6. Settings Screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // 7. About Screen
        composable(Screen.About.route) {
            AboutScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
