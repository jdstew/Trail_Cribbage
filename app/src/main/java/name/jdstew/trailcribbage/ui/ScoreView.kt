package name.jdstew.trailcribbage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import name.jdstew.trailcribbage.GameModel
import name.jdstew.trailcribbage.GameModelListener
import name.jdstew.trailcribbage.cribbage.Deck
import name.jdstew.trailcribbage.cribbage.SHOW_DEALER_CRIB
import name.jdstew.trailcribbage.cribbage.SHOW_DEALER_HAND
import name.jdstew.trailcribbage.cribbage.SHOW_PONE_HAND

class ScoreViewModel() : GameModelListener, ViewModel() {

    var handDealer: ByteArray = ByteArray(4) { _ -> -1 }
        private set

    var handCrib: ByteArray = ByteArray(4) { _ -> -1 }
        private set

    var handPone: ByteArray = ByteArray(4) { _ -> -1 }
        private set

    var starter = DEFAULT_CARD_INDEX
        private set

    var isDealerMe = false
        private set

    private val gameModel = GameModel

    init {
        gameModel.addGameModelListener(this)
        isDealerMe = gameModel.isDealerMe()
        starter = gameModel.getStarterCard().toInt()
    }

    override fun onCleared() { // when ViewModel is destroyed
        super.onCleared()
        gameModel.removeGameModelListener(this)
    }

    override fun updateState(message: ByteArray) {
        when (message[0]) {
            SHOW_PONE_HAND -> {
                handPone = byteArrayOf(message[1], message[2], message[3], message[4])
            }

            SHOW_DEALER_HAND -> {
                handDealer = byteArrayOf(message[1], message[2], message[3], message[4])
            }

            SHOW_DEALER_CRIB -> {
                handCrib = byteArrayOf(message[1], message[2], message[3], message[4])
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ScoreViewModel() as T
            }
        }
    }
}

@Composable
fun ScoreScreen() {

    // see ViewModel for view configuration
    val scoreViewModel: ScoreViewModel = viewModel(factory = ScoreViewModel.Factory)

    Column(modifier = Modifier.padding(5.dp)) {
        if (scoreViewModel.isDealerMe) { // show pone, starter, dealer, crib
            Row {
                for (b in scoreViewModel.handPone) {
                    if (b >= 0) {
                        Icon(
                            painter = painterResource(CardLookup.getCardDrawableID(b.toInt())),
                            contentDescription = Deck.getCardDisplayedName(b.toInt()),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    } else {
                        Icon(
                            painter = painterResource(
                                CardLookup.getCardDrawableID(
                                    DEFAULT_CARD_INDEX
                                )
                            ),
                            contentDescription = Deck.getCardDisplayedName(DEFAULT_CARD_INDEX),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
            Row {
                if (scoreViewModel.starter >= 0) {
                    Icon(
                        painter = painterResource(CardLookup.getCardDrawableID(scoreViewModel.starter)),
                        contentDescription = Deck.getCardDisplayedName(scoreViewModel.starter),
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
            Row {
                for (b in scoreViewModel.handDealer) {
                    if (b >= 0) {
                        Icon(
                            painter = painterResource(CardLookup.getCardDrawableID(b.toInt())),
                            contentDescription = Deck.getCardDisplayedName(b.toInt()),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    } else {
                        Icon(
                            painter = painterResource(
                                CardLookup.getCardDrawableID(
                                    DEFAULT_CARD_INDEX
                                )
                            ),
                            contentDescription = Deck.getCardDisplayedName(DEFAULT_CARD_INDEX),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
            Row {
                for (b in scoreViewModel.handCrib) {
                    if (b >= 0) {
                        Icon(
                            painter = painterResource(CardLookup.getCardDrawableID(b.toInt())),
                            contentDescription = Deck.getCardDisplayedName(b.toInt()),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    } else {
                        Icon(
                            painter = painterResource(
                                CardLookup.getCardDrawableID(
                                    DEFAULT_CARD_INDEX
                                )
                            ),
                            contentDescription = Deck.getCardDisplayedName(DEFAULT_CARD_INDEX),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        } else { // show crib, dealer, starter, pone
            Row {
                for (b in scoreViewModel.handCrib) {
                    if (b >= 0) {
                        Icon(
                            painter = painterResource(CardLookup.getCardDrawableID(b.toInt())),
                            contentDescription = Deck.getCardDisplayedName(b.toInt()),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    } else {
                        Icon(
                            painter = painterResource(
                                CardLookup.getCardDrawableID(
                                    DEFAULT_CARD_INDEX
                                )
                            ),
                            contentDescription = Deck.getCardDisplayedName(DEFAULT_CARD_INDEX),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
            Row {
                for (b in scoreViewModel.handDealer) {
                    if (b >= 0) {
                        Icon(
                            painter = painterResource(CardLookup.getCardDrawableID(b.toInt())),
                            contentDescription = Deck.getCardDisplayedName(b.toInt()),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    } else {
                        Icon(
                            painter = painterResource(
                                CardLookup.getCardDrawableID(
                                    DEFAULT_CARD_INDEX
                                )
                            ),
                            contentDescription = Deck.getCardDisplayedName(DEFAULT_CARD_INDEX),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
            Row {
                if (scoreViewModel.starter >= 0) {
                    Icon(
                        painter = painterResource(CardLookup.getCardDrawableID(scoreViewModel.starter)),
                        contentDescription = Deck.getCardDisplayedName(scoreViewModel.starter),
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
            Row {
                for (b in scoreViewModel.handPone) {
                    if (b >= 0) {
                        Icon(
                            painter = painterResource(CardLookup.getCardDrawableID(b.toInt())),
                            contentDescription = Deck.getCardDisplayedName(b.toInt()),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    } else {
                        Icon(
                            painter = painterResource(
                                CardLookup.getCardDrawableID(
                                    DEFAULT_CARD_INDEX
                                )
                            ),
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