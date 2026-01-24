package ru.fefu.splitmate.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.fefu.splitmate.ui.screens.HomeScreen
import ru.fefu.splitmate.ui.screens.InputScreen
import ru.fefu.splitmate.ui.screens.ResultScreen
import ru.fefu.splitmate.ui.viewmodel.SplitEvent
import ru.fefu.splitmate.ui.viewmodel.SplitViewModel

object Routes {
    const val HOME = "home"
    const val INPUT = "input"
    const val RESULT = "result/{calcId}"

    fun createResultRoute(calcId: String): String {
        return "result/$calcId"
    }
}

@Composable
fun AppNavigation(viewModel: SplitViewModel) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

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

                    val latestCalculation = viewModel.getLatestCalculation()
                    if (latestCalculation != null) {

                        navController.navigate(Routes.createResultRoute(latestCalculation.id))
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.RESULT,
            arguments = listOf(navArgument("calcId") { type = NavType.StringType })
        ) { backStackEntry ->
            val calcId = remember {
                backStackEntry.arguments?.getString("calcId") ?: ""
            }

            val calculation = if (calcId.isNotEmpty()) {
                viewModel.getCalculationById(calcId)
            } else {
                viewModel.getLatestCalculation()
            }

            ResultScreen(
                calculation = calculation,
                onEdit = {
                    navController.popBackStack(Routes.INPUT, inclusive = false)
                },
                onNewCalculation = {
                    viewModel.onEvent(SplitEvent.Reset)
                    navController.navigate(Routes.INPUT) {
                        popUpTo(Routes.HOME) { inclusive = false }
                    }
                }
            )
        }
    }
}