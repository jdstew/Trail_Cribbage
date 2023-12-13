package name.jdstew.trailcribbage.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun GameNavigation(): NavHostController {
    val navHostController = rememberNavController()
    NavHost(
        navController = navHostController,
        startDestination = NavigationRoute.SplashScreen.route
    ) { // start of builder NavGraphBuilder.compose(...) functions
        composable(route = NavigationRoute.SplashScreen.route) {
            SplashScreen(navController = navHostController) // NavBackStackEntry
        }
        composable(route = NavigationRoute.SelectOpponentScreen.route) {
            SelectOpponentScreen() // NavBackStackEntry
        }
        composable(route = NavigationRoute.CutScreen.route) {
            CutScreen() // NavBackStackEntry
        }
        composable(route = NavigationRoute.DealScreen.route) {
            DealScreen(navController = navHostController) // NavBackStackEntry
        }
        composable(route = NavigationRoute.DealStarterCutScreen.route) {
            DealStarterCutScreen(navController = navHostController) // NavBackStackEntry
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
    return navHostController
}