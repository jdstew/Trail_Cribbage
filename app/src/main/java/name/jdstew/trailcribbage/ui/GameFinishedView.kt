package name.jdstew.trailcribbage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun GameFinishedScreen(navController: NavController) {
    Column {
        Text("Text in a Column [fun GameFinishedScreen(navController: NavController) {\n]")
        Button(onClick = { navController.navigate(NavigationRoute.CutScreen.route) {
            popUpTo(NavigationRoute.SplashScreen.route)
        }
        }) {
            Text("Start a new game")
        }
    }
}