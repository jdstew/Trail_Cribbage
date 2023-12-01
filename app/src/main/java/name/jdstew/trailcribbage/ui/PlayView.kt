package name.jdstew.trailcribbage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun PlayScreen(navController: NavController) {
    Column {
        Text("Text in a Column [PlayScreen]")
        Button(onClick = { navController.navigate(NavigationRoute.ScoringScreen.route) {
            popUpTo(NavigationRoute.SplashScreen.route)
        }
        }) {
            Text("Score the hands")
        }
    }
}