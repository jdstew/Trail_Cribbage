package name.jdstew.trailcribbage.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import name.jdstew.trailcribbage.GameModel
import name.jdstew.trailcribbage.GameModelListener
import name.jdstew.trailcribbage.cribbage.DEAL_DEALER_COMPLETE
import name.jdstew.trailcribbage.cribbage.DEAL_PONE_COMPLETE
import name.jdstew.trailcribbage.cribbage.DEAL_START
import name.jdstew.trailcribbage.cribbage.DEAL_STARTER_CUT
import name.jdstew.trailcribbage.cribbage.Deck
import name.jdstew.trailcribbage.cribbage.ME_MINE

class DealViewModel() : GameModelListener, ViewModel() {

    // todo: create ViewModel variables
    var handMine: ByteArray = ByteArray(6) { _ -> -1 }
        private set

    private var cribWriteIndex = 0
    var crib: ByteArray = ByteArray(4) { _ -> -1 }
        private set

    var isSetButtonAvailable = false
        private set

    var isMyCribSelected = false
        private set

    private var isOppoCribSelected = false
    private var isCribFinished = isMyCribSelected && isOppoCribSelected

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
            DEAL_START -> { // this message is from dealer to pone
                if (message[1] == ME_MINE) {
                    handMine = message.copyOfRange(2, 7)
                }
            }
            DEAL_PONE_COMPLETE -> { // opponent has provided crib cards
                // change display from card holes to face cards
                crib[2] = FACE_CARD_INDEX.toByte()
                crib[3] = FACE_CARD_INDEX.toByte()
                if (isCribFinished) {
                    sendDealCutMessage()
                }
            }
            DEAL_DEALER_COMPLETE -> {
                crib[0] = message[1]
                crib[1] = message[2]
            }
        }
    }

    private fun sendDealCutMessage() {
        gameModel.updateState(byteArrayOf(DEAL_STARTER_CUT, 0, 0, 0, 0, 0, 0, 0))
    }

    fun selectCardForCrib(card: Byte) {
        for (i in handMine.indices) {
            if (handMine[i] == card) {
                // add to crib
                if (crib[0].toInt() == -1) {
                    crib[0] = card
                } else {
                    crib[1] = card
                }
                handMine[i] = -1 // remove from hand
                break
            }
        }

        isSetButtonAvailable = crib[0] >= 0 && crib[1] >= 0
    }

    fun deselectCardForCrib(card: Byte) {
        for (i in handMine.indices) {
            if (handMine[i].toInt() == -1) {
                // remove from crib
                if (crib[0] == card) {
                    crib[0] = -1
                } else {
                    crib[1] = -1
                }
                handMine[i] = card // add to hand
            }
        }

        isSetButtonAvailable = false
    }

    fun setCrib() {
        isMyCribSelected = true
        if (gameModel.isDealerMe()) {
            gameModel.updateState(byteArrayOf(DEAL_DEALER_COMPLETE, 0, 0, 0, 0, 0, 0, 0))
        } else {
            gameModel.updateState(byteArrayOf(DEAL_PONE_COMPLETE, crib[0], crib[1], 0, 0, 0, 0, 0))
        }

        if (isCribFinished) {
            sendDealCutMessage()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DealViewModel() as T
            }
        }
    }
}

@Composable
fun DealScreen() {

    // see ViewModel for view configuration
    val dealViewModel: DealViewModel = viewModel(factory = DealViewModel.Factory)

    Column (modifier = Modifier.padding(5.dp)) {

        Text("Tap to select your cards for the crib, then press button to lock them in.")
        Row { // for the crib
            if (dealViewModel.crib[0].toInt() != -1 && !dealViewModel.isMyCribSelected) {
                IconButton(
                    onClick = {
                        dealViewModel.deselectCardForCrib(dealViewModel.crib[0])
                    }
                ) {
                    Icon(
                        painter = painterResource(CardLookup.getCardDrawableID(dealViewModel.crib[0].toInt())),
                        contentDescription = Deck.getCardDisplayedName(dealViewModel.crib[0].toInt()),
                        modifier = Modifier.size(size = 200.dp),
                        tint = Color.Unspecified
                    )
                }
            } else {
                Icon(
                    painter = painterResource(CardLookup.getCardDrawableID(dealViewModel.crib[1].toInt())),
                    contentDescription = Deck.getCardDisplayedName(dealViewModel.crib[1].toInt()),
                    modifier = Modifier.size(size = 200.dp),
                    tint = Color.Unspecified
                )
            }
            if (dealViewModel.crib[1].toInt() != -1 && !dealViewModel.isMyCribSelected) {
                IconButton(
                    onClick = {
                        dealViewModel.deselectCardForCrib(dealViewModel.crib[1])
                    }
                ) {
                    Icon(
                        painter = painterResource(CardLookup.getCardDrawableID(dealViewModel.crib[1].toInt())),
                        contentDescription = Deck.getCardDisplayedName(dealViewModel.crib[1].toInt()),
                        modifier = Modifier.size(size = 200.dp),
                        tint = Color.Unspecified
                    )
                }
            } else {
                Icon(
                    painter = painterResource(CardLookup.getCardDrawableID(dealViewModel.crib[1].toInt())),
                    contentDescription = Deck.getCardDisplayedName(dealViewModel.crib[1].toInt()),
                    modifier = Modifier.size(size = 200.dp),
                    tint = Color.Unspecified
                )
            }
            Icon(
                painter = painterResource(CardLookup.getCardDrawableID(dealViewModel.crib[2].toInt())),
                contentDescription = Deck.getCardDisplayedName(dealViewModel.crib[2].toInt()),
                modifier = Modifier.size(size = 200.dp),
                tint = Color.Unspecified
            )
            Icon(
                painter = painterResource(CardLookup.getCardDrawableID(dealViewModel.crib[3].toInt())),
                contentDescription = Deck.getCardDisplayedName(dealViewModel.crib[3].toInt()),
                modifier = Modifier.size(size = 200.dp),
                tint = Color.Unspecified
            )
        }
        Row { // for the delt cards
            dealViewModel.handMine.forEach {
                if (it.toInt() == -1 || dealViewModel.isMyCribSelected) {
                    Icon(
                        painter = painterResource(CardLookup.getCardDrawableID(it.toInt())),
                        contentDescription = Deck.getCardDisplayedName(it.toInt()),
                        modifier = Modifier.size(size = 200.dp),
                        tint = Color.Unspecified
                    )
                } else {
                    IconButton(
                        onClick = {
                            dealViewModel.selectCardForCrib(it) // value of card, not index
                        }
                    ) {
                        Icon(
                            painter = painterResource(CardLookup.getCardDrawableID(it.toInt())),
                            contentDescription = Deck.getCardDisplayedName(it.toInt()),
                            modifier = Modifier.size(size = 200.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }
        Button(
            onClick = {
                dealViewModel.setCrib()
            },
            enabled = dealViewModel.isSetButtonAvailable
        ) {
            Text("Lock-in crib selection")
        }
    }
}