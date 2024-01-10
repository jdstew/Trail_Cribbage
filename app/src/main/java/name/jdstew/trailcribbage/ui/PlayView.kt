package name.jdstew.trailcribbage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
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
import name.jdstew.trailcribbage.cribbage.Deck
import name.jdstew.trailcribbage.cribbage.PLAY_CARD_1
import name.jdstew.trailcribbage.cribbage.PLAY_CARD_2
import name.jdstew.trailcribbage.cribbage.PLAY_CARD_3
import name.jdstew.trailcribbage.cribbage.PLAY_CARD_4
import name.jdstew.trailcribbage.cribbage.PLAY_CARD_5
import name.jdstew.trailcribbage.cribbage.PLAY_CARD_6
import name.jdstew.trailcribbage.cribbage.PLAY_CARD_7
import name.jdstew.trailcribbage.cribbage.PLAY_CARD_8
import name.jdstew.trailcribbage.cribbage.PLAY_GO
import name.jdstew.trailcribbage.cribbage.PLAY_START

class PlayViewModel() : GameModelListener, ViewModel() {
    var redrawTrigger = false
        private set

    private var playMessageID: Byte = -1
    private var startIndex: Byte = 0
    private var endIndex: Byte = 0

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
            PLAY_START -> {
                redrawTrigger = !redrawTrigger
            }

            PLAY_CARD_1, PLAY_CARD_2, PLAY_CARD_3, PLAY_CARD_4, PLAY_CARD_5, PLAY_CARD_6, PLAY_CARD_7, PLAY_CARD_8 -> {
                playMessageID = message[0]
                startIndex = message[2]
                endIndex = message[3]

                redrawTrigger = !redrawTrigger
            }

            PLAY_GO -> {
                // todo: anything???
            }
        }
    }

    fun isItMyTurn(): Boolean {
        return gameModel.isItMyTurn()
    }

    fun getOppoPlayHand(): ByteArray {
        return gameModel.getOppoPlayHand()
    }

    fun getPlayStack(): ByteArray {
        return gameModel.getPlayStack()
    }

    fun getMyPlayHand(): ByteArray {
        return gameModel.getMyPlayHand()
    }

    fun playCard(card: Byte) {
        gameModel.updateState(byteArrayOf((++playMessageID).toByte(), card, startIndex, (++endIndex).toByte(), 0, 0, 0, 0))
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlayViewModel() as T
            }
        }
    }
}

@Composable
fun PlayScreen() {

    // see ViewModel for view configuration
    val playViewModel: PlayViewModel = viewModel(factory = PlayViewModel.Factory)

    Column (modifier = Modifier.padding(5.dp)) {

        val displayWidth = LocalConfiguration.current.screenWidthDp.toFloat() - 10.dp.value

        val isCardSelectable = playViewModel.isItMyTurn() // card selection is active
        Text("Play")
        Row { // opponent's cards, not selectable
            for (b in playViewModel.getOppoPlayHand()) {
                if (b >= 0) {
                    Icon(
                        painter = painterResource(CardLookup.getCardDrawableID(FACE_CARD_INDEX)),
                        contentDescription = Deck.getCardDisplayedName(FACE_CARD_INDEX),
                        modifier = Modifier.size(size = 200.dp),
                        tint = Color.Unspecified
                    )
                } else {
                    Icon(
                        painter = painterResource(CardLookup.getCardDrawableID(DEFAULT_CARD_INDEX)),
                        contentDescription = Deck.getCardDisplayedName(DEFAULT_CARD_INDEX),
                        modifier = Modifier.size(size = 200.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }
        Row { // played cards
            for (b in playViewModel.getPlayStack()) {
                Icon(
                    painter = painterResource(CardLookup.getCardDrawableID(b.toInt())),
                    contentDescription = Deck.getCardDisplayedName(b.toInt()),
                    modifier = Modifier.size(size = (displayWidth / 8).dp),
                    tint = Color.Unspecified
                )
            }
        }
        Row { // my cards
            if (isCardSelectable) {
                for (b in playViewModel.getMyPlayHand()) {
                    if (b >= 0) {
                        IconButton(
                            onClick = {
                                playViewModel.playCard(b)
                            }
                        ) {
                            Icon(
                                painter = painterResource(CardLookup.getCardDrawableID(b.toInt())),
                                contentDescription = Deck.getCardDisplayedName(b.toInt()),
                                modifier = Modifier.size(size = 200.dp),
                                tint = Color.Unspecified
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(CardLookup.getCardDrawableID(DEFAULT_CARD_INDEX)),
                            contentDescription = Deck.getCardDisplayedName(DEFAULT_CARD_INDEX),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
                Text("Tap next card to play")
            } else {
                for (b in playViewModel.getMyPlayHand()) {
                    if (b >= 0) {
                        Icon(
                            painter = painterResource(CardLookup.getCardDrawableID(b.toInt())),
                            contentDescription = Deck.getCardDisplayedName(b.toInt()),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    } else {
                        Icon(
                            painter = painterResource(CardLookup.getCardDrawableID(DEFAULT_CARD_INDEX)),
                            contentDescription = Deck.getCardDisplayedName(DEFAULT_CARD_INDEX),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }
    }
}