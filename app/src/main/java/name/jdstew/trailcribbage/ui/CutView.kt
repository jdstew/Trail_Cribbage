package name.jdstew.trailcribbage.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import name.jdstew.trailcribbage.GameModel
import name.jdstew.trailcribbage.GameModelListener
import name.jdstew.trailcribbage.R
import name.jdstew.trailcribbage.cribbage.Deck
import name.jdstew.trailcribbage.cribbage.GameMessaging

class CutViewModel(
    private val gameModel: GameModel
) : GameModelListener, ViewModel() {
    init {
        gameModel.addGameModelListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        gameModel.removeGameModelListener(this)
    }

    override fun updateState(newState: ByteArray) {
//        TODO("interpret the byte array")
//        TODO("if both cards have been selected, then...")
    }

    var thisPlayerCutDrawableID = CardLookup.getCardDrawableID(DEFAULT_CARD_INDEX)
        private set

    var opponentCutDrawableID = CardLookup.getCardDrawableID(DEFAULT_CARD_INDEX)
        private set

    var cutCardPosition = mutableFloatStateOf(25f)
        private set

    fun setCutCardPosition(cardNumber: Float = 25f) {
        cutCardPosition.floatValue = cardNumber
        println("view model selected cut card changed to " + cutCardPosition.floatValue)
    }

    fun selectCutCard() {
        val deck = Deck.getShuffledDeck()
        val cardIndex = deck[cutCardPosition.value.toInt() - 1].toInt()
        thisPlayerCutDrawableID = CardLookup.getCardDrawableID(cardIndex)
        gameModel.updateState(GameMessaging.getCutSelectedMessage(cardIndex))
//        TODO("if both cards have been selected, then...")
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CutViewModel(GameModel) as T
            }
        }
    }
}


@Composable
fun CutScreen(
    navController: NavController,
    cutViewModel: CutViewModel = viewModel(factory = CutViewModel.Factory)
) {
    var sliderPosition by remember { mutableFloatStateOf(25f) }
    var selectCardEnabled by remember {mutableStateOf(true)}

    Column {
        Text("Cut - lower card becomes dealer")
        Row {
            Text("Opponent:")
            Icon(
                painter = painterResource(cutViewModel.opponentCutDrawableID!!),
                contentDescription = stringResource(id = R.string.card_hole),
                modifier = Modifier.size(size = 200.dp),
                tint = Color.Unspecified
            )
        }
        Row {
            Text("You:")
            Icon(
                painter = painterResource(cutViewModel.thisPlayerCutDrawableID!!),
                contentDescription = stringResource(id = R.string.card_hole),
                modifier = Modifier.size(size = 200.dp),
                tint = Color.Unspecified
            )
        }
        Box {
            val totalWidth = LocalConfiguration.current.screenWidthDp
//            println("screen width is $totalWidth dp")

            val nonSelectedCardOffset = 12.dp
            val cardWidth = 36.dp
            var xOffset = 0f

            val offsetIncrement = (totalWidth.toFloat() - cardWidth.value) / 51f
            for (i in 1..52) {
//                println("placing card at x = $xOffset")
                val vOffset = if (i == sliderPosition.toInt()) 0.dp else nonSelectedCardOffset
                Icon(
                    painter = painterResource(R.drawable.face_card),
                    contentDescription = stringResource(id = R.string.face_card),
                    modifier = Modifier
                        .width(cardWidth)
                        .absoluteOffset(
                            x = xOffset.dp,
                            y = vOffset
                        ),
                    tint = Color.Unspecified
                )
                xOffset += offsetIncrement
            }
        }
        Slider(
            value = cutViewModel.cutCardPosition.floatValue,
            onValueChange = { sliderPosition = it },
            onValueChangeFinished = { cutViewModel.setCutCardPosition(sliderPosition) },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 52,
            enabled = selectCardEnabled,
            valueRange = 1f..52f
        )
        Button(
            onClick = {
                selectCardEnabled = false
                cutViewModel.selectCutCard()

//                navController.navigate(NavigationRoute.DealScreen.route) {
//                    popUpTo(NavigationRoute.SplashScreen.route)
//                }
            },
            enabled = selectCardEnabled
        ) {
            Text("Reveal your cut card")
        }
    }
}
