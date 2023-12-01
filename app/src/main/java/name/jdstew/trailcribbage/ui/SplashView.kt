package name.jdstew.trailcribbage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun SplashScreen(navController: NavController) {
    Column {
        Text("Text in a Column [SplashScreen]")
        Button(onClick = { navController.navigate(NavigationRoute.ScanListScreen.route) {
            popUpTo(NavigationRoute.SplashScreen.route)
        }
        }) {
            Text("Find an opponent")
        }
    }
}