package name.jdstew.trailcribbage.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import name.jdstew.trailcribbage.ui.CutScreen
import name.jdstew.trailcribbage.ui.DealScreen
import name.jdstew.trailcribbage.ui.GameFinishedScreen
import name.jdstew.trailcribbage.ui.NavigationRoute
import name.jdstew.trailcribbage.ui.PlayScreen
import name.jdstew.trailcribbage.ui.RoundFinishedScreen
import name.jdstew.trailcribbage.ui.ScanListScreen
import name.jdstew.trailcribbage.ui.ScoringScreen
import name.jdstew.trailcribbage.ui.SplashScreen

@Composable
fun GameNavigation() {
    val navHostController = rememberNavController()
    NavHost(
        navController = navHostController,
        startDestination = NavigationRoute.SplashScreen.route
    ) { // start of builder NavGraphBuilder.compose(...) functions
        composable(route = NavigationRoute.SplashScreen.route) {
            SplashScreen(navController = navHostController) // NavBackStackEntry
        }
        composable(route = NavigationRoute.ScanListScreen.route) {
            ScanListScreen(navController = navHostController) // NavBackStackEntry
        }
        composable(route = NavigationRoute.CutScreen.route) {
            CutScreen(navController = navHostController) // NavBackStackEntry
        }
        composable(route = NavigationRoute.DealScreen.route) {
            DealScreen(navController = navHostController) // NavBackStackEntry
        }
        composable(route = NavigationRoute.PlayScreen.route) {
            PlayScreen(navController = navHostController) // NavBackStackEntry
        }
        composable(route = NavigationRoute.ScoringScreen.route) {
            ScoringScreen(navController = navHostController) // NavBackStackEntry
        }
        composable(route = NavigationRoute.RoundFinishedScreen.route) {
            RoundFinishedScreen(navController = navHostController) // NavBackStackEntry
        }
        composable(route = NavigationRoute.GameFinishedScreen.route) {
            GameFinishedScreen(navController = navHostController) // NavBackStackEntry
        }
//        composable(
//            route = NavigationRoute.CutScreen.route + "/{name}",  // vs. "?name={name}"
//            arguements = listOf(
//                navArguement("name") { //
//                    type = NavType.StringType
//                    defaultValue = "blah"
//                    nullable = true
//                }
//            )
//        ) { entry ->
//            CutScreen(name = entry.arguements?.getString("name"))  // the composable view
//        }
    }
}