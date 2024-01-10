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
import name.jdstew.trailcribbage.cribbage.DEAL_STARTER_CUT
import name.jdstew.trailcribbage.cribbage.DEAL_STARTER_REVEALED
import name.jdstew.trailcribbage.cribbage.DEAL_STARTER_SELECTED
import name.jdstew.trailcribbage.cribbage.Deck

class DealStarterCutViewModel() : GameModelListener, ViewModel() {
    var isStarterCardSelected = false
        private set

    var starterCutDrawableID = CardLookup.getCardDrawableID(DEFAULT_CARD_INDEX)
        private set

    var isDealerMe = false
        private set

    private val gameModel = GameModel

    init {
        gameModel.addGameModelListener(this)
        isDealerMe = gameModel.isDealerMe()
    }

    override fun onCleared() { // when ViewModel is destroyed
        super.onCleared()
        gameModel.removeGameModelListener(this)
    }

    override fun updateState(message: ByteArray) {
        when (message[0]) {
            DEAL_STARTER_CUT -> {
                // todo: anything???
            }

            DEAL_STARTER_SELECTED -> {
                starterCutDrawableID = CardLookup.getCardDrawableID(FACE_CARD_INDEX)
            }

            DEAL_STARTER_REVEALED -> {
                starterCutDrawableID = CardLookup.getCardDrawableID(message[1].toInt())
            }
        }
    }

    fun selectCutCard(cardNumber: Float = 20f) {
        isStarterCardSelected = true
        gameModel.updateState(
            byteArrayOf(
                DEAL_STARTER_SELECTED,
                cardNumber.toInt().toByte(),
                0,
                0,
                0,
                0,
                0,
                0
            )
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DealStarterCutViewModel() as T
            }
        }
    }
}


@Composable
fun DealStarterCutScreen() {

    // see ViewModel for view configuration
    val dealStarterCutViewModel: DealStarterCutViewModel =
        viewModel(factory = DealStarterCutViewModel.Factory)
    var sliderPosition by remember { mutableFloatStateOf(20f) } // initial selection is middle card
    var selectCardEnabled by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(5.dp)) {

        if (dealStarterCutViewModel.isDealerMe) {
            Text("Waiting on pone to cut the remaining deck.")
        } else {
            Text("Select your cut card using the slider, then press button to reveal it.")
        }

        Row {
            Text("Starter:")
            Icon(
                painter = painterResource(dealStarterCutViewModel.starterCutDrawableID),
                contentDescription = Deck.getCardDisplayedName(dealStarterCutViewModel.starterCutDrawableID),
                modifier = Modifier.size(size = 200.dp),
                tint = Color.Unspecified
            )
        }

        if (!dealStarterCutViewModel.isDealerMe) {
            Box {
                val displayWidth = LocalConfiguration.current.screenWidthDp.toFloat() - 10.dp.value
                //            println("screen width is $totalWidth dp")

                val nonSelectedCardOffset = 12.dp
                val cardWidth = displayWidth / 40
                var xOffset = 0f

                val offsetIncrement = (displayWidth - cardWidth) / 39f
                for (i in 1..40) {
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
                enabled = !dealStarterCutViewModel.isStarterCardSelected,
                valueRange = 1f..40f,
                steps = 40
            )
            Button(
                onClick = {
                    dealStarterCutViewModel.selectCutCard(sliderPosition)
                },
                enabled = !dealStarterCutViewModel.isStarterCardSelected
            ) {
                Text("Reveal your cut card")
            }
        }
    }
}