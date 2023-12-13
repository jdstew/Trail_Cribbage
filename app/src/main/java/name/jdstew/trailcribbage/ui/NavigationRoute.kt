package name.jdstew.trailcribbage.ui

sealed class NavigationRoute(val route: String) {
    object SplashScreen: NavigationRoute(route = "splash_screen")
    object SelectOpponentScreen: NavigationRoute(route = "select_opponent_screen")
    object CutScreen: NavigationRoute(route = "cut_screen")
    object DealScreen: NavigationRoute(route = "deal_screen")
    object DealStarterCutScreen: NavigationRoute(route = "deal_cut_screen")
    object PlayScreen: NavigationRoute(route = "play_screen")
    object ScoringScreen: NavigationRoute(route = "scoring_screen")
    object RoundFinishedScreen: NavigationRoute(route = "round_finished_screen")
    object GameFinishedScreen: NavigationRoute(route = "game_finished_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}