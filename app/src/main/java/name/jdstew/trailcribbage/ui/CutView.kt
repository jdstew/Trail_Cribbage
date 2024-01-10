package name.jdstew.trailcribbage.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import name.jdstew.trailcribbage.GameModel
import name.jdstew.trailcribbage.GameModelListener
import name.jdstew.trailcribbage.cribbage.CUT_MY_CUT
import name.jdstew.trailcribbage.cribbage.CUT_OPPONENT_CUT
import name.jdstew.trailcribbage.cribbage.CUT_START
import name.jdstew.trailcribbage.cribbage.Deck
import name.jdstew.trailcribbage.cribbage.GameMessaging

class CutViewModel() : GameModelListener, ViewModel() {
    var isMyCardSelected = false
        private set

    var myCutDrawableID = CardLookup.getCardDrawableID(DEFAULT_CARD_INDEX)
        private set

    var opponentCutDrawableID = CardLookup.getCardDrawableID(DEFAULT_CARD_INDEX)
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
            CUT_START -> {
                isMyCardSelected = false
                myCutDrawableID = CardLookup.getCardDrawableID(DEFAULT_CARD_INDEX)
                opponentCutDrawableID = CardLookup.getCardDrawableID(DEFAULT_CARD_INDEX)
            }

            CUT_MY_CUT -> {
                myCutDrawableID = CardLookup.getCardDrawableID(message[1].toInt())
            }

            CUT_OPPONENT_CUT -> {
                opponentCutDrawableID = CardLookup.getCardDrawableID(message[1].toInt())
            }
        }
    }

    fun selectCutCard(cardNumber: Float = 25f) {
        isMyCardSelected = true
        val deck = Deck.getShuffledDeck()
        val cardIndex = deck[cardNumber.toInt() - 1].toInt()
        myCutDrawableID = CardLookup.getCardDrawableID(cardIndex)
        gameModel.updateState(GameMessaging.getCutSelectedMessage(cardIndex))
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CutViewModel() as T
            }
        }
    }
}


@Composable
fun CutScreen() {

    // see ViewModel for view configuration
    val cutViewModel: CutViewModel = viewModel(factory = CutViewModel.Factory)
    var sliderPosition by remember { mutableFloatStateOf(25f) } // initial selection is middle card
    var selectCardEnabled by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(5.dp)) {

        Text("Select your cut card using the slider, then press button to reveal it.")
        Row {
            Text("Opponent:")
            Icon(
                painter = painterResource(cutViewModel.opponentCutDrawableID!!),
                contentDescription = Deck.getCardDisplayedName(cutViewModel.opponentCutDrawableID),
                modifier = Modifier.size(size = 200.dp),
                tint = Color.Unspecified
            )
        }
        Row {
            Text("You:")
            Icon(
                painter = painterResource(cutViewModel.myCutDrawableID!!),
                contentDescription = Deck.getCardDisplayedName(cutViewModel.myCutDrawableID),
                modifier = Modifier.size(size = 200.dp),
                tint = Color.Unspecified
            )
        }

        Box {
            val displayWidth = LocalConfiguration.current.screenWidthDp.toFloat() - 10.dp.value
//            println("screen width is $totalWidth dp")

            val nonSelectedCardOffset = 12.dp
            val cardWidth = displayWidth / 52
            var xOffset = 0f

            val offsetIncrement = (displayWidth - cardWidth) / 51f
            for (i in 1..52) {
//                println("placing card at x = $xOffset")
                val vOffset = if (i == sliderPosition.toInt()) 0.dp else nonSelectedCardOffset
                Icon(
                    painter = painterResource(CardLookup.getCardDrawableID(FACE_CARD_INDEX)),
                    contentDescription = Deck.getCardDisplayedName(FACE_CARD_INDEX),
                    modifier = Modifier
                        .width(cardWidth.dp)
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
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            enabled = !cutViewModel.isMyCardSelected,
            valueRange = 1f..52f,
            steps = 52
        )
        Button(
            onClick = {
                cutViewModel.selectCutCard(sliderPosition)
            },
            enabled = !cutViewModel.isMyCardSelected
        ) {
            Text("Reveal your cut card")
        }
    }
}
