package name.jdstew.trailcribbage.ui

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import name.jdstew.trailcribbage.GameModel
import name.jdstew.trailcribbage.GameModelListener
import androidx.lifecycle.viewmodel.compose.viewModel
import name.jdstew.trailcribbage.cribbage.COMPLETION

class RoundFinishedViewModel() : GameModelListener, ViewModel() {

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
            COMPLETION -> {
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
                return RoundFinishedViewModel() as T
            }
        }
    }
}

@Composable
fun RoundFinishedScreen() {

    // see ViewModel for view configuration
    val roundFinishedViewModel: RoundFinishedViewModel = viewModel(factory = RoundFinishedViewModel.Factory)

    Column (modifier = Modifier.padding(5.dp)) {
        Row {
            Text("Opponent's score: ${roundFinishedViewModel.opponentScore}")
            LinearProgressIndicator(
                roundFinishedViewModel.opponentScore.toInt().toFloat() / 121.0f,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Green,
                trackColor = Color.Gray
            )
        }

        Row {
            Text("Your score: {$roundFinishedViewModel.myScore}")
            LinearProgressIndicator(
                roundFinishedViewModel.myScore.toInt().toFloat()/121.0f,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Red,
                trackColor = Color.Gray
            )
        }
    }
}