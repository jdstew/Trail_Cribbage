package name.jdstew.trailcribbage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun RoundFinishedScreen(navController: NavController) {
    Column {
        Text("Text in a Column [RoundFinishedScreen]")
        Button(onClick = { navController.navigate(NavigationRoute.DealScreen.route) {
            popUpTo(NavigationRoute.SplashScreen.route)
        }
        }) {
            Text("Start next round")
        }
    }
}