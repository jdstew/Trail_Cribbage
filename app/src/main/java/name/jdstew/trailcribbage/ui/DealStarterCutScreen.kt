package name.jdstew.trailcribbage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController


@Composable
fun DealStarterCutScreen(navController: NavController) {
    Column {
        Text("Text in a Column [DealScreen]")
        Button(onClick = { navController.navigate(NavigationRoute.PlayScreen.route) {
            popUpTo(NavigationRoute.SplashScreen.route)
        }
        }) {
            Text("Let's count cards (play)")
        }
    }
}