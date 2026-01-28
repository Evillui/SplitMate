package com.example.splitmate.ui.nav

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.splitmate.ui.screens.HomeScreen
import com.example.splitmate.ui.screens.InputScreen
import com.example.splitmate.ui.screens.ResultScreen
import com.example.splitmate.viewmodel.SplitEvent
import com.example.splitmate.viewmodel.SplitUiState
import com.example.splitmate.viewmodel.SplitViewModel

object Routes {
    const val HOME = "home"
    const val INPUT = "input"
    const val RESULT = "result/{calcId}"

    fun createResultRoute(calcId: String): String = "result/$calcId"
}

@Composable
fun AppNav(viewModel: SplitViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val uiState by viewModel.uiState.observeAsState(SplitUiState())

    // Toast one-shot events
    val toastEvent by viewModel.toastEvent.observeAsState()
    LaunchedEffect(toastEvent) {
        toastEvent?.getContentIfNotHandled()?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(uiState.currentCalculation?.id) {
        val calcId = uiState.currentCalculation?.id ?: return@LaunchedEffect

        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentRoute == Routes.RESULT.replace("{calcId}", calcId)) return@LaunchedEffect

        navController.navigate(Routes.createResultRoute(calcId))
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onStartClick = { navController.navigate(Routes.INPUT) }
            )
        }

        composable(Routes.INPUT) {
            InputScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onCalculate = {
                    viewModel.onEvent(SplitEvent.Calculate)
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.RESULT,
            arguments = listOf(navArgument("calcId") { type = NavType.StringType })
        ) { backStackEntry ->
            val calcId = backStackEntry.arguments?.getString("calcId").orEmpty()

            val calculation = uiState.calculations.find { it.id == calcId }
                ?: uiState.currentCalculation
                ?: uiState.calculations.lastOrNull()

            ResultScreen(
                calculation = calculation,
                onEdit = {
                    navController.popBackStack(Routes.INPUT, inclusive = false)
                },
                onNewCalculation = {
                    viewModel.onEvent(SplitEvent.Reset)
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}