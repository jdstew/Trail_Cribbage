package name.jdstew.trailcribbage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import name.jdstew.trailcribbage.GameModel
import name.jdstew.trailcribbage.GameModelListener
import name.jdstew.trailcribbage.R
import name.jdstew.trailcribbage.cribbage.Deck
import name.jdstew.trailcribbage.cribbage.FINISHED

class GameFinishedViewModel() : GameModelListener, ViewModel() {

    var myScore = 0
        private set

    var opponentScore = 0
        private set

    private val gameModel = GameModel
    init {
        gameModel.addGameModelListener(this)
    }

    override fun onCleared() { // when ViewModel is destroyed
        super.onCleared()
        gameModel.removeGameModelListener(this)
    }

    override fun updateState(message: ByteArray) {
        when (message[0]) {
            FINISHED -> {
                if (gameModel.isDealerMe()) {
                    myScore = message[1].toInt()
                    opponentScore = message[2].toInt()
                } else {
                    opponentScore = message[1].toInt()
                    myScore = message[2].toInt()
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GameFinishedViewModel() as T
            }
        }
    }
}

@Composable
fun GameFinishedScreen() {

    // see ViewModel for view configuration
    val gameFinishedViewModel: GameFinishedViewModel = viewModel(factory = GameFinishedViewModel.Factory)

    Column (modifier = Modifier.padding(5.dp)) {

        if (gameFinishedViewModel.myScore >= 121) {
            Text ("You won!")
            Icon(
                painter = painterResource(R.drawable.trophy),
                contentDescription = "Trophy",
                modifier = Modifier.size(size = 200.dp),
                tint = Color.Unspecified
            )
        } else {
            Text ("You lost.")
        }

        val loserScore = Math.min(gameFinishedViewModel.opponentScore, gameFinishedViewModel.myScore)
        if (loserScore < 31) {
            Text ("This is a tripple skunk win!")
            Row {
                for (i in 1..3) {
                    Icon(
                        painter = painterResource(R.drawable.skunk),
                        contentDescription = "Skunk",
                        modifier = Modifier.size(size = 50.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        } else if (loserScore < 61) {
            Text ("This is a double skunk win!")
            Row {
                for (i in 1..2) {
                    Icon(
                        painter = painterResource(R.drawable.skunk),
                        contentDescription = "Skunk",
                        modifier = Modifier.size(size = 50.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        } else if (loserScore < 91) {
            Text ("This is a skunk win!")
            Icon(
                painter = painterResource(R.drawable.skunk),
                contentDescription = "Skunk",
                modifier = Modifier.size(size = 50.dp),
                tint = Color.Unspecified
            )
        }
    }
}