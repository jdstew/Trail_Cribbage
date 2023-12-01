package name.jdstew.trailcribbage.cribbage

/*
    const val BLUETOOTH_UNKNOWN: Byte  = 0
    const val BLUETOOTH_ENABLED: Byte  = 1
    const val BLUETOOTH_PAIRED: Byte   = 2
    const val BLUETOOTH_SELECTED: Byte = 3
*/

// listing of game states - first digit is 'phase', second digit is 'step'
const val INITIALIZATION: Byte = 10
const val CUT: Byte = 20 // deck shuffled and laid out, no card selected
const val CUT_1_OF_2: Byte = 21 // 1 of 2 cards selected
const val CUT_FINISHED: Byte = 22 // 2 of 2 cards selected
const val DEAL: Byte = 30 // deck shuffled, delt, 6 cards up and 6 down
const val DEAL_1_OF_2: Byte = 31 // a player selected 2 cards for crib
const val DEAL_FINISHED: Byte = 32 // both players selected 2 cards for crib
const val DEAL_STARTER: Byte = 33 // Pone selected cut, next card shown up
const val PLAY: Byte = 40 // thru 48
const val SHOW_PONE_HAND: Byte = 50
const val SHOW_DEALER_HAND: Byte = 51
const val SHOW_DEALER_CRIB: Byte = 52
const val COMPLETION: Byte = 60
const val FINISHED: Byte = 70

class GameState {

    /*  unsure if this array is needed
        val commsState: ByteArray = byteArrayOf(
            BLUETOOTH_UNKNOWN,
            BLUETOOTH_ENABLED,
            BLUETOOTH_PAIRED,
            BLUETOOTH_SELECTED
        )
    */

    val gameSequence: ByteArray = byteArrayOf(
        INITIALIZATION,
        CUT,
        CUT_1_OF_2,
        CUT_FINISHED,
        DEAL,
        DEAL_1_OF_2,
        DEAL_FINISHED,
        DEAL_STARTER,
        PLAY,
        SHOW_PONE_HAND,
        SHOW_DEALER_HAND,
        SHOW_DEALER_CRIB,
        COMPLETION,
        FINISHED
    )

    private var bluetoothState: Byte = 0

    // todo: private var opponent: BluetoothDevice? = null
    private var opponentAddress: String? =
        null // typically 17-byte address String like 00:11:22:AA:BB:CC
    private var opponentAlias: String? =
        null // alias is local and not shared to the opponent, but defaults to device's name

    /*
        For arrays of cards: index of 0 -> me, 1 -> them
        Always sent as the above index, and swapped when recieved
    */

    // game state is 43 bytes (padded to 64 bytes)
    // ------------------------------------------------ start of game state
    //
    // game summary (5 bytes)
    private var resyncFlag: Byte =
        0  // 0 if normal data exchange, otherwise force a re-sync to this state
    private var sequenceIndex: Byte = 0  // index to GAME_SEQUENCE
    private var playerScore: ByteArray = ByteArray(2)  // [2] 0..121
    private var dealerIndex: Byte = -1 // -1 -> neither, 0 -> me, 1 -> them

    // cut (2 bytes)
    private var cut: ByteArray = ByteArray(2) // [2] playerID and ue of card

    // deal and show (17 bytes)
    private var handMine: ByteArray =
        ByteArray(6) { _ -> -1 }  // cards (-1 for cards moved to crib)
    private var handOppo: ByteArray =
        ByteArray(6) { _ -> -1 }  // cards (-1 for cards moved to crib)
    private var crib: ByteArray =
        ByteArray(4)  // [4], first two bytes are dealer's, second two are opponent's
    private var starter: Byte = -1 // index to value of card

    // play is (19 bytes)
    private var playNextToGo: Byte = -1 // 0 -> me, 1 -> them
    private var playHandMine: ByteArray =
        ByteArray(4) { _ -> -1 } //copied from hand, set to -1 once played from hand
    private var playHandOppo: ByteArray =
        ByteArray(4) { _ -> -1 } //copied from hand, set to -1 once played from hand
    private var playCards: ByteArray = ByteArray(8) { _ -> -1 }  // [8], copied from playerHand
    private var playStartIndex: Byte = 0  // 0..8
    private var playNextIndex: Byte = 1  // 1..8
    //
    // ------------------------------------------------ end of game state

    // deck is (52 bytes padded to 64 bytes), held and saved only by the dealer during DEAL phase
    private var deck: ByteArray? = null  // [52] random card indexes

    fun setBluetoothState(state: Byte) {
        bluetoothState = state
    }

    fun getBluetoothState(): Byte {
        return bluetoothState
    }

    fun shuffleDeck(): ByteArray? {
        // deck = return Deck.getShuffledDeck()
        return deck
    }

    fun getDeck(): ByteArray? {
        return deck
    }


    fun setOpponentAlias(alias: String): Unit {
        opponentAlias = alias
    }

    fun getOpponentAlias(): String? {
        return opponentAlias
    }

    fun getResyncFlag(): Byte {
        return resyncFlag
    }

    fun getGameStateIndex(): Byte {
        return sequenceIndex
    }

    fun getPlayerScore(): ByteArray {
        return playerScore
    }

    fun getDealerIndex(): Byte {
        return dealerIndex
    }

    fun getCut(): ByteArray {
        return cut
    }

    fun getHandMine(): ByteArray {
        return handMine
    }

    fun getHandOppo(): ByteArray {
        return handOppo
    }

    fun getCrib(): ByteArray {
        return crib
    }

    fun getStarter(): Byte {
        return starter
    }

    fun getPlayNextToGo(): Byte {
        return playNextToGo
    }

    fun getPlayHandMine(): ByteArray {
        return playHandMine
    }

    fun getPlayHandOppo(): ByteArray {
        return playHandOppo
    }

    fun getPlayCards(): ByteArray {
        return playCards
    }

    fun getPlayStartIndex(): Byte {
        return playStartIndex
    }

    fun getPlayNextIndex(): Byte {
        return playNextIndex
    }

    fun serializeGameState(): ByteArray {
        val output = ByteArray(64) { _ -> -1 }
        var index = 0

        // game summary (5 bytes)
        output[index++] = resyncFlag
        output[index++] = sequenceIndex
        for (i in playerScore) {
            output[index++] = i
        }
        output[index++] = dealerIndex

        // cut (2 bytes)
        for (i in cut) {
            output[index++] = i
        }

        // deal and show (17 bytes)
        for (i in handMine) {
            output[index++] = i
        }
        for (i in handOppo) {
            output[index++] = i
        }
        for (i in crib) {
            output[index++] = i
        }
        output[index++] = starter

        // play is (19 bytes)
        output[index++] = playNextToGo
        for (i in playHandMine) {
            output[index++] = i
        }
        for (i in playHandOppo) {
            output[index++] = i
        }
        for (i in playCards) {
            output[index++] = i
        }
        output[index++] = playStartIndex
        output[index] = playNextIndex

        return output
    }

    fun deserializeGameState(input: ByteArray): GameState {
        val newGameState = GameState()

        // game summary (5 bytes)
        newGameState.resyncFlag = input[0]
        newGameState.sequenceIndex = input[1]
        var index = 0
        for (i in 2..3) {
            newGameState.playerScore[index++] = input[i]
        }
        newGameState.dealerIndex = input[4]

        // cut (2 bytes)
        index = 0
        for (i in 5..6) {
            newGameState.cut[index++] = input[i]
        }

        // deal and show (17 bytes)
        index = 0
        for (i in 7..12) {
            newGameState.handOppo[index++] = input[i] // note swap in order
        }
        index = 0
        for (i in 13..18) {
            newGameState.handMine[index++] = input[i]
        }
        index = 0
        for (i in 19..22) {
            newGameState.crib[index++] = input[i]
        }
        newGameState.starter = input[23]

        // play is (19 bytes)
        newGameState.playNextToGo = input[24]
        index = 0
        for (i in 25..28) {
            newGameState.playHandOppo[index++] = input[i]
        }
        index = 0
        for (i in 29..32) {
            newGameState.playHandMine[index++] = input[i]
        }
        index = 0
        for (i in 33..40) {
            newGameState.playCards[index++] = input[i]
        }
        newGameState.playStartIndex = input[41]
        newGameState.playNextIndex = input[42]

        return newGameState
    }

    override fun toString(): String {
        val sb: StringBuilder = StringBuilder()

        // game summary (5 bytes)
        sb.append(resyncFlag)
        sb.append(',')
        sb.append(sequenceIndex)
        sb.append(',')
        for (b in playerScore) {
            sb.append(b)
            sb.append(',')
        }
        sb.append(dealerIndex)
        sb.append(',')

        // cut (2 bytes)
        for (b in cut) {
            sb.append(b)
            sb.append(',')
        }

        // deal and show (17 bytes)
        for (b in handMine) {
            sb.append(b)
            sb.append(',')
        }
        for (b in handOppo) {
            sb.append(b)
            sb.append(',')
        }
        for (b in crib) {
            sb.append(b)
            sb.append(',')
        }
        sb.append(starter)

        // play is (19 bytes)
        sb.append(playNextToGo)
        sb.append(',')
        for (b in playHandMine) {
            sb.append(b)
            sb.append(',')
        }
        for (b in playHandOppo) {
            sb.append(b)
            sb.append(',')
        }
        for (b in playCards) {
            sb.append(b)
            sb.append(',')
        }
        sb.append(playStartIndex)
        sb.append(',')
        sb.append(playNextIndex)
        sb.append(System.lineSeparator())

        return sb.toString()
    }
}

fun main() {
    var gameState = GameState()
    println(gameState)
    gameState = gameState.deserializeGameState(gameState.serializeGameState())
    println(gameState)
}
