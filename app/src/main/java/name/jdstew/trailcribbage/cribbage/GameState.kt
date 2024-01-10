package name.jdstew.trailcribbage.cribbage

import name.jdstew.trailcribbage.GameModel
import java.io.Serializable

/*
    const val BLUETOOTH_UNKNOWN: Byte  = 0
    const val BLUETOOTH_ENABLED: Byte  = 1
    const val BLUETOOTH_PAIRED: Byte   = 2
    const val BLUETOOTH_SELECTED: Byte = 3
*/

const val DEALER_IS_ME = 127.toByte()
const val DEALER_IS_OPPONENT = (-127).toByte()
const val ME_MINE = 127.toByte()
const val OPPONENT_THEIRS = (-127).toByte()

class GameState: Serializable {
    val serialVersionUID = 31415926535L

    // todo: private var opponent: BluetoothDevice? = null
    private var opponentName: String? =
        null // opponent's phone name
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
    private var lastMessage: ByteArray = ByteArray(8)
    private var playerScore: ByteArray = ByteArray(2)  // [2] 0..121
    private var dealerID: Byte = 0 // value: 0 -> neither, DEALER_IS_ME -> me, DEALER_IS_OPPONENT -> them

    // cut (2 bytes)
    private var cut: ByteArray = ByteArray(2) { _ -> -1 } // index: 0 -> me, 1 -> them

    // deal and show (17 bytes)
    private var handMine: ByteArray =
        ByteArray(6) { _ -> -1 }  // cards (-1 for cards moved to crib)
    private var handOppo: ByteArray =
        ByteArray(6) { _ -> -1 }  // cards (-1 for cards moved to crib)
    private var crib: ByteArray =
        ByteArray(4)  // [4], first two bytes are dealer's, second two are opponent's
    private var starter: Byte = -1 // index to value of card

    // play is (19 bytes)
    private var playWhosNextTurn: Byte = 0 // ME_MINE, OPPONENT_THEIRS
    private var playGoCount: Byte = 0 // count of "go's"
    private var playHandMine: ByteArray =
        ByteArray(4) { _ -> -1 } //copied from hand, set to -1 once played from hand
    private var playHandOppo: ByteArray =
        ByteArray(4) { _ -> -1 } //copied from hand, set to -1 once played from hand
    private var playedCards: ByteArray = ByteArray(8) { _ -> -1 }  // [8], copied from playerHand
    private var playedStartIndex: Byte = 0  // 0..7
    private var playedNextIndex: Byte = 1  // 0..7
    //
    // ------------------------------------------------ end of game state

    // deck is (52 bytes padded to 64 bytes), held and saved only by the dealer during DEAL phase
    private var deck: ByteArray = Deck.getShuffledDeck()  // [52] random card indexes

    fun getLastMessage(): ByteArray {
        return lastMessage
    }

    fun setLastMessage(message: ByteArray) {
        lastMessage = message.clone()
    }

    fun getCardFromDeck(deckIndex: Byte): Byte {
        return deck[deckIndex.toInt()]
    }

    fun getHandOfOpponentMessage(): ByteArray {
        val deck = Deck.getShuffledDeck()
        handOppo[0] = deck[41]
        handOppo[1] = deck[43]
        handOppo[2] = deck[45]
        handOppo[3] = deck[47]
        handOppo[4] = deck[49]
        handOppo[5] = deck[51]
        return byteArrayOf(DEAL_START, OPPONENT_THEIRS, deck[51], deck[49], deck[47], deck[45], deck[43], deck[41])
    }

    fun getHandOfMineMessage(): ByteArray {
        handMine[0] = deck[40]
        handMine[1] = deck[42]
        handMine[2] = deck[44]
        handMine[3] = deck[46]
        handMine[4] = deck[48]
        handMine[5] = deck[50]
        return byteArrayOf(DEAL_START, ME_MINE, deck[50], deck[48], deck[46], deck[44], deck[42], deck[40])
    }

    fun getMyCut(): Byte {
        return cut[0]
    }

    fun setMyCut(myCut: Byte) {
        cut[0] = myCut
    }

    fun getOpponentCut(): Byte {
        return cut[1]
    }

    fun setOpponentCut(opponentCut: Byte) {
        cut[1] = opponentCut
    }

    fun setDealerCrib(card1: Byte, card2: Byte) {
        crib[0] = card1
        crib[1] = card2
        setPlayHand(card1, card2)
    }

    fun setOpponentCrib(card1: Byte, card2: Byte) {
        crib[2] = card1
        crib[3] = card2
        setPlayHand(card1, card2)
    }

    private fun setPlayHand(card1: Byte, card2: Byte) {
        if (dealerID == DEALER_IS_ME) {
            handMine[handMine.binarySearch(card1)] = -1
            handMine[handMine.binarySearch(card2)] = -1
            handMine.sort()
            for (i in 2..5) {
                playHandMine[i - 2] = handMine[i]
            }
        } else {
            handOppo[handOppo.binarySearch(card1)] = -1
            handOppo[handOppo.binarySearch(card2)] = -1
            handOppo.sort()
            for (i in 2..5) {
                playHandOppo[i - 2] = handOppo[i]
            }
        }
    }

    fun removePlayedCard(player: Byte, card: Byte) {
        if (player == ME_MINE) {
            for (i in 0..3) {
                // remove card from play hand
                if (playHandMine[i] == card) {
                    playHandMine[i] = -1
                }
            }
        } else {
            for (i in 0..3) {
                // remove card from play hand
                if (playHandOppo[i] == card) {
                    playHandOppo[i] = -1
                }
            }
        }
    }



    fun resetCut() {
        cut[0] = -1
        cut[1] = -1
    }

    fun resetRound() {
        // reset deal and show
        handMine = ByteArray(6) { _ -> -1 }
        handOppo = ByteArray(6) { _ -> -1 }
        crib = ByteArray(4)
        starter = -1

        // reset play
        playWhosNextTurn = 0
        playHandMine = ByteArray(4) { _ -> -1 }
        playHandOppo = ByteArray(4) { _ -> -1 }
        playedCards = ByteArray(8) { _ -> -1 }
        playedStartIndex = 0
        playedNextIndex = 1
    }

    fun getDealerID(): Byte {
        return dealerID
    }

    fun setDealerID(id: Byte) {
        dealerID = id
    }

    fun getCut(): ByteArray {
        return cut
    }

    fun getCrib(): ByteArray {
        return crib
    }

    fun getPlayWhosNextTurn(): Byte {
        return playWhosNextTurn
    }

    fun setPlayWhosNextTurn(player: Byte) {
        playWhosNextTurn = player
    }

    fun setPlayGoCount(count: Byte) {
        playGoCount = count
    }

    fun setPlayedCard(card: Byte, index: Byte) {
        playedCards[index.toInt()] = card
        playedNextIndex = (index + 1).toByte()
    }

    fun getPlayHandOppo(): ByteArray {
        return playHandOppo.clone()
    }

    fun getPlayedCards(): ByteArray {
        return playHandMine.clone()
    }

    fun getPlayHandMine(): ByteArray {
        return playedCards.clone()
    }

    fun getPlayedCardsSum(): Byte {
        var sum = 0
        for (i in playedStartIndex.toInt()..playedNextIndex.toInt()) {
            sum += Deck.getCardValue(playedCards[i].toInt())
        }

        return sum.toByte()
    }

    fun getStarterCard(): Byte {
        return starter
    }

    fun getPlayedCardsScore(): ScoringReport {
        val playStackSize = playedNextIndex.toInt() - playedStartIndex.toInt()
        val playStack = intArrayOf(playStackSize)
        var j = playedStartIndex.toInt()
        for (i in 0..playStackSize) {
            playStack[i] = playedCards[j++].toInt()
        }
        return Scoring.scorePlay(playStack, 0, playStackSize)
    }

    fun addToScore(player: Byte, points: Byte): Byte {
        if (player == ME_MINE) {
            playerScore[0] = (playerScore[0] + points).toByte()
            return playerScore[0]
        } else {
            playerScore[1] = (playerScore[1] + points).toByte()
            return playerScore[1]
        }
    }

    fun getPlayerScore(player: Byte): Byte {
        if (player == ME_MINE) {
            return playerScore[0]
        } else {
            return playerScore[1]
        }
    }

    fun getOpponentScore(): Byte {
        return playerScore[1]
    }

    fun getMyScore(): Byte {
        return playerScore[1]
    }

    fun getOpponentName(): String? {
        return opponentName
    }

    fun setOpponentName(name: String) {
        opponentName = name
    }

    fun getOpponentAddress(): String? {
        return opponentAddress
    }

    fun setOpponentAddress(address: String) {
        opponentAddress = address
    }

    fun getOpponentAlias(): String? {
        return opponentAlias
    }

    fun setOpponentAlias(alias: String) {
        opponentAlias = alias
    }

    fun serializeGameState(): ByteArray {
        val output = ByteArray(64) { _ -> -1 }
        var index = 0

        // game summary (5 bytes)
        for (i in playerScore) {
            output[index++] = i
        }
        output[index++] = dealerID

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
        output[index++] = playWhosNextTurn
        // todo: add playGoCount
        for (i in playHandMine) {
            output[index++] = i
        }
        for (i in playHandOppo) {
            output[index++] = i
        }
        for (i in playedCards) {
            output[index++] = i
        }
        output[index++] = playedStartIndex
        output[index] = playedNextIndex

        return output
    }

    fun deserializeGameState(input: ByteArray): GameState {
        val newGameState = GameState()

        // cut (2 bytes)
        var index = 0
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
        newGameState.playWhosNextTurn = input[24]
        // todo: add playGoCount
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
            newGameState.playedCards[index++] = input[i]
        }
        newGameState.playedStartIndex = input[41]
        newGameState.playedNextIndex = input[42]

        return newGameState
    }

    override fun toString(): String {
        val sb: StringBuilder = StringBuilder()

        // game summary (5 bytes)
        for (b in playerScore) {
            sb.append(b)
            sb.append(',')
        }
        sb.append(dealerID)
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
        sb.append(playWhosNextTurn)
        // todo: add playGoCount
        sb.append(',')
        for (b in playHandMine) {
            sb.append(b)
            sb.append(',')
        }
        for (b in playHandOppo) {
            sb.append(b)
            sb.append(',')
        }
        for (b in playedCards) {
            sb.append(b)
            sb.append(',')
        }
        sb.append(playedStartIndex)
        sb.append(',')
        sb.append(playedNextIndex)
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
