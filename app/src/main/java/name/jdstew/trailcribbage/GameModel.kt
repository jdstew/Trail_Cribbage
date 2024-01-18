package name.jdstew.trailcribbage

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.os.CountDownTimer
import android.util.Log
import name.jdstew.trailcribbage.bluetooth.BluetoothBroker
import name.jdstew.trailcribbage.cribbage.*
import name.jdstew.trailcribbage.ui.NavigationRoute


object GameModel : GameModelListener {
    private val TAG = "GameModel"

    private lateinit var mainActivity: MainActivity
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothBroker: BluetoothBroker

    private val stateChangeListeners = mutableSetOf<GameModelListener>()
    private var gameState = GameState()

    private val gameRepository = GameRepository(mainActivity)

    fun configure(
        mainActivity: MainActivity,
        bluetoothManager: BluetoothManager,
        bluetoothAdapter: BluetoothAdapter,
    ) {
        this.mainActivity = mainActivity
        this.bluetoothManager = bluetoothManager
        this.bluetoothAdapter = bluetoothAdapter
        bluetoothBroker = BluetoothBroker(
            mainActivity = mainActivity,
            bluetoothManager = bluetoothManager,
            bluetoothAdapter = bluetoothAdapter,
            gameModel = this
        )
    }

    fun getBluetoothBroker(): BluetoothBroker {
        return bluetoothBroker
    }

    fun addGameModelListener(listener: GameModelListener) {
        stateChangeListeners.add(listener)
    }

    fun removeGameModelListener(listener: GameModelListener) {
        stateChangeListeners.remove(listener)
    }

    override fun updateState(message: ByteArray) {
        if (!GameMessaging.isMessageLogical(gameState.getLastMessage(), message)) {
            Log.e(TAG, "New message is not logical")
            // todo: process a game re-sync
            return
        }

        gameState.setLastMessage(message)
        processMessage(message)
        gameRepository.saveGame(gameState)
        transmitStateUpdate(message)
    }

    private fun transmitStateUpdate(newMessage: ByteArray) {
        stateChangeListeners.forEach {
            it.updateState(newMessage)
        }
    }

    private fun processMessage(message: ByteArray) {

        when (message[0]) {
            GAME_START -> processStart(message)
            SELECT_OPPONENT -> processSelectOpponent(message)
            CUT_START, CUT_MY_CUT, CUT_OPPONENT_CUT -> processCut(message)
            DEAL_START, DEAL_PONE_COMPLETE, DEAL_DEALER_COMPLETE -> processDeal(message)
            DEAL_STARTER_CUT, DEAL_STARTER_SELECTED -> processDealStarter(message)
            PLAY_START, PLAY_CARD_1, PLAY_CARD_2, PLAY_CARD_3, PLAY_CARD_4, PLAY_CARD_5, PLAY_CARD_6, PLAY_CARD_7, PLAY_CARD_8 -> processPlay(
                message
            )

            PLAY_GO -> processGo(message)
            SHOW_PONE_HAND, SHOW_DEALER_HAND, SHOW_DEALER_CRIB -> processShow(message)
            COMPLETION -> processCompletion(message)
            FINISHED -> processFinished(message)
        }
    }

    private fun processStart(message: ByteArray) {
        gameState = GameState()

        // note: navigation to select opponent is managed by the main activity
    }

    private fun processSelectOpponent(message: ByteArray) {

    }

    private fun processCut(message: ByteArray) {
        // set and retransmit cut values
        when (message[0]) {
            CUT_START -> { // received by both sides
                // ...wait for selections
            }

            CUT_MY_CUT -> {
                gameState.setMyCut(message[1])
            }

            CUT_OPPONENT_CUT -> {
                gameState.setOpponentCut(message[1])
            }
        }

        // evaluate cut values
        if (gameState.getMyCut() >= 0 && gameState.getOpponentCut() >= 0) {
            if (gameState.getMyCut() < gameState.getOpponentCut()) {
                gameState.setDealerID(DEALER_IS_ME)

                mainActivity.announce("You will deal first")
                mainActivity.navigateTo(NavigationRoute.DealScreen, NavigationRoute.SplashScreen)
                transmitStateUpdate(gameState.getHandOfOpponentMessage())
            } else if (gameState.getMyCut() > gameState.getOpponentCut()) {
                gameState.setDealerID(DEALER_IS_OPPONENT)

                mainActivity.announce("Your opponent will deal first")
                mainActivity.navigateTo(NavigationRoute.DealScreen, NavigationRoute.SplashScreen)
                // transmitStateUpdate() will be sent by opponent
            } else { // case of being equal

                mainActivity.announce("Cut cards are equal, let's re-cut")
                mainActivity.navigateTo(NavigationRoute.CutScreen, NavigationRoute.SplashScreen)
                transmitStateUpdate(byteArrayOf(CUT_START, 0, 0, 0, 0, 0, 0, 0))
            }
        }
    }

    private fun processDeal(message: ByteArray) {
        when (message[0]) {
            DEAL_START -> {
                // ...wait for crib selections to come in
            }

            DEAL_PONE_COMPLETE -> {
                gameState.setOpponentCrib(message[1], message[2])
            }

            DEAL_DEALER_COMPLETE -> {
                gameState.setDealerCrib(message[1], message[2])
            }
        }

        // check if all 4 cards in crib have been filled
        if (gameState.getCrib().binarySearch(-1) < 0) {
            mainActivity.announce("Let's count cards")
            mainActivity.navigateTo(
                NavigationRoute.DealStarterCutScreen,
                NavigationRoute.SplashScreen
            )
            transmitStateUpdate(byteArrayOf(DEAL_STARTER_CUT, 0, 0, 0, 0, 0, 0, 0))
        }
    }

    private fun processDealStarter(message: ByteArray) {
        when (message[0]) {
            DEAL_STARTER_CUT -> {
                // ...wait for opponent's message
            }

            DEAL_STARTER_SELECTED -> {
                if (gameState.getDealerID() == DEALER_IS_ME) {
                    val starterIndex = gameState.getCardFromDeck(message[1])
                    transmitStateUpdate(
                        byteArrayOf(
                            DEAL_STARTER_REVEALED,
                            starterIndex,
                            0,
                            0,
                            0,
                            0,
                            0,
                            0
                        )
                    )
                }
            }

            DEAL_STARTER_REVEALED -> {
                val starterIndex = message[1]
                val scoringReport = Scoring.scoreStarter(starterIndex.toInt())
                if (gameState.getDealerID() == DEALER_IS_ME) {

                    if (scoringReport.score > 0) {
                        gameState.addToScore(ME_MINE, scoringReport.score.toByte())
                        mainActivity.announce(
                            ME_MINE,
                            mutableListOf("+${scoringReport.score} points")
                        )
                        mainActivity.announce(ME_MINE, scoringReport.announcements)
                    }

                    gameState.setPlayWhosNextTurn(OPPONENT_THEIRS)
                    transmitStateUpdate(byteArrayOf(PLAY_START, 0, 0, 0, 0, 0, 0, 0))

                } else {
                    if (scoringReport.score > 0) {
                        gameState.addToScore(OPPONENT_THEIRS, scoringReport.score.toByte())
                        mainActivity.announce(
                            OPPONENT_THEIRS,
                            mutableListOf("+{$scoringReport.score points}")
                        )
                        mainActivity.announce(OPPONENT_THEIRS, scoringReport.announcements)
                    }

                    gameState.setPlayWhosNextTurn(ME_MINE)
                    // transmitStateUpdate() only sent by dealer
                }

                // check scores for win
                if (gameState.getPlayerScore(ME_MINE) >= 121 || gameState.getPlayerScore(
                        OPPONENT_THEIRS
                    ) >= 121
                ) {
                    mainActivity.navigateTo(
                        NavigationRoute.GameFinishedScreen,
                        NavigationRoute.SplashScreen
                    )
                    transmitStateUpdate(byteArrayOf(COMPLETION, 0, 0, 0, 0, 0, 0, 0))
                } else {
                    mainActivity.navigateTo(
                        NavigationRoute.PlayScreen,
                        NavigationRoute.SplashScreen
                    )
                    transmitStateUpdate(byteArrayOf(FINISHED, 0, 0, 0, 0, 0, 0, 0))
                }
            }
        }
    }

    /*
        > 	mark non-dealer as next turn (above when deal starter cut is selected)
        > 	calculate the difference between 31 and current sum
        > 	if it is my turn AND one of my cards is less than the difference
                then I play a card, which is added to the sum and the stack is scored
            else
                then I 'GO' AND the other person gets a point
        > 	mark the other person as next to go
        >	if the other person 'GO'-ed AND I don't have a card to play
                then I restart the play
        > 	if neither person has any cards left
    */

    fun getPlayedCardsSum(): Byte {
        return gameState.getPlayedCardsSum()
    }

    private fun processPlay(message: ByteArray) {
        if (message[1] == 0.toByte()) {
            // do nothing, simply initiating this game phase and return point
            return
        }

        // add card to stack
        gameState.setPlayedCard(message[1], message[3])
        val scoringReport = gameState.getPlayedCardsScore()
        gameState.setPlayGoCount(0) // reset since a card is played

        if (gameState.getPlayWhosNextTurn() == ME_MINE) {
            gameState.removePlayedCard(ME_MINE, message[1])
            gameState.addToScore(ME_MINE, scoringReport.score.toByte())
            mainActivity.announce(ME_MINE, mutableListOf("+{$scoringReport.score points}"))
            mainActivity.announce(ME_MINE, scoringReport.announcements)
        } else {
            gameState.removePlayedCard(OPPONENT_THEIRS, message[1])
            gameState.addToScore(OPPONENT_THEIRS, scoringReport.score.toByte())
            mainActivity.announce(OPPONENT_THEIRS, mutableListOf("+{$scoringReport.score points}"))
            mainActivity.announce(OPPONENT_THEIRS, scoringReport.announcements)
        }

        // check scores for win
        if (gameState.getPlayerScore(ME_MINE) >= 121 || gameState.getPlayerScore(OPPONENT_THEIRS) >= 121) {
            mainActivity.navigateTo(
                NavigationRoute.GameFinishedScreen,
                NavigationRoute.SplashScreen
            )
        }

        gameState.setPlayWhosNextTurn((gameState.getPlayWhosNextTurn().toInt() * -1).toByte())

        if (message[0] == PLAY_CARD_8) {
            mainActivity.navigateTo(NavigationRoute.ScoringScreen, NavigationRoute.SplashScreen)
            if (gameState.getDealerID() == DEALER_IS_ME) {
                val opponentHand = gameState.getPlayHandOppo()
                transmitStateUpdate(
                    byteArrayOf(
                        SHOW_PONE_HAND,
                        opponentHand[0],
                        opponentHand[1],
                        opponentHand[2],
                        opponentHand[3],
                        gameState.getStarterCard(),
                        0,
                        0
                    )
                )
            }
        } // else wait for next card to be played 
    }

    private fun processGo(message: ByteArray) {
        gameState.setPlayGoCount(message[1])

        // is this the first or second GO?
        if (message[1] > 1) {
            // change playNextGo (UI to respond "it's your turn")
            // score the GO
        } else {
            // change playNextGo
            // reset playedStartIndex
            // reset playGoCount
        }
    }

    private fun processShow(message: ByteArray) {
        when (message[0]) {
            SHOW_PONE_HAND -> {
                val intArray = intArrayOf(
                    message[1].toInt(),
                    message[2].toInt(),
                    message[3].toInt(),
                    message[4].toInt()
                )
                val scoringReport = Scoring.scoreShowInHand(intArray, message[5].toInt())
                mainActivity.announce(gameState.getDealerID(), scoringReport.announcements)
                if (scoringReport.score >= 121) {
                    mainActivity.navigateTo(
                        NavigationRoute.GameFinishedScreen,
                        NavigationRoute.SplashScreen
                    )
                    transmitStateUpdate(byteArrayOf(FINISHED, 0, 0, 0, 0, 0, 0, 0))
                } else {
                    mainActivity.navigateTo(
                        NavigationRoute.GameFinishedScreen,
                        NavigationRoute.SplashScreen
                    )
                    if (gameState.getDealerID() == DEALER_IS_ME) {
                        val dealerHand = gameState.getHandOfMineMessage()
                        transmitStateUpdate(
                            byteArrayOf(
                                SHOW_DEALER_HAND,
                                dealerHand[0],
                                dealerHand[1],
                                dealerHand[2],
                                dealerHand[3],
                                gameState.getStarterCard(),
                                0,
                                0
                            )
                        )
                    }
                }
            }

            SHOW_DEALER_HAND -> {
                val intArray = intArrayOf(
                    message[1].toInt(),
                    message[2].toInt(),
                    message[3].toInt(),
                    message[4].toInt()
                )
                val scoringReport = Scoring.scoreShowInHand(intArray, message[5].toInt())
                mainActivity.announce(gameState.getDealerID(), scoringReport.announcements)
                if (scoringReport.score >= 121) {
                    mainActivity.navigateTo(
                        NavigationRoute.GameFinishedScreen,
                        NavigationRoute.SplashScreen
                    )
                    transmitStateUpdate(byteArrayOf(FINISHED, 0, 0, 0, 0, 0, 0, 0))
                } else {
                    mainActivity.navigateTo(
                        NavigationRoute.GameFinishedScreen,
                        NavigationRoute.SplashScreen
                    )
                    if (gameState.getDealerID() == DEALER_IS_ME) {
                        val dealerCrib = gameState.getHandOfMineMessage()
                        transmitStateUpdate(
                            byteArrayOf(
                                SHOW_DEALER_CRIB,
                                dealerCrib[0],
                                dealerCrib[1],
                                dealerCrib[2],
                                dealerCrib[3],
                                gameState.getStarterCard(),
                                0,
                                0
                            )
                        )
                    }
                }
            }

            SHOW_DEALER_CRIB -> {
                val intArray = intArrayOf(
                    message[1].toInt(),
                    message[2].toInt(),
                    message[3].toInt(),
                    message[4].toInt()
                )
                val scoringReport = Scoring.scoreShowInHand(intArray, message[5].toInt())
                mainActivity.announce(gameState.getDealerID(), scoringReport.announcements)
                if (scoringReport.score >= 121) {
                    mainActivity.navigateTo(
                        NavigationRoute.GameFinishedScreen,
                        NavigationRoute.SplashScreen
                    )
                    transmitStateUpdate(byteArrayOf(FINISHED, 0, 0, 0, 0, 0, 0, 0))
                } else {
                    if (gameState.getDealerID() == DEALER_IS_ME) {
                        transmitStateUpdate(
                            byteArrayOf(
                                COMPLETION,
                                0,
                                0,
                                0,
                                0,
                                0,
                                0,
                                0
                            )
                        )
                    }
                }
            }
        }
    }

    private fun processCompletion(message: ByteArray) {
        gameState.resetRound() // reset round
        mainActivity.navigateTo(NavigationRoute.DealScreen, NavigationRoute.SplashScreen)
        if (gameState.getDealerID() == DEALER_IS_ME) {
            transmitStateUpdate(gameState.getHandOfOpponentMessage())
        }
    }

    private fun processFinished(message: ByteArray) {
        gameState = GameState() // reset game
        mainActivity.navigateTo(NavigationRoute.SplashScreen, NavigationRoute.SplashScreen)
        transmitStateUpdate(byteArrayOf(GAME_START, 0, 0, 0, 0, 0, 0, 0))
    }

    fun reSyncGame(message: ByteArray) {

    }

    fun isDealerMe(): Boolean {
        return gameState.getDealerID() == DEALER_IS_ME
    }

    fun isItMyTurn(): Boolean {
        return gameState.getPlayWhosNextTurn() == ME_MINE
    }

    fun getOppoPlayHand(): ByteArray {
        return gameState.getPlayHandOppo()
    }

    fun getPlayStack(): ByteArray {
        return gameState.getPlayedCards()
    }

    fun getMyPlayHand(): ByteArray {
        return gameState.getPlayHandMine()
    }

    fun getOpponentScore(): Byte {
        return gameState.getOpponentScore()
    }

    fun getMyScore(): Byte {
        return gameState.getMyScore()
    }

    fun getStarterCard(): Byte {
        return gameState.getStarterCard()
    }

    fun getOpponentName(): String? {
        return gameState.getOpponentName()
    }

    fun setOpponentName(name: String) {
        gameState.setOpponentName(name)
    }

    fun getOpponentAddress(): String? {
        return gameState.getOpponentAddress()
    }

    fun setOpponentAddress(address: String) {
        gameState.setOpponentAddress(address)
    }

    fun getOpponentAlias(): String? {
        return gameState.getOpponentAlias()
    }

    fun setOpponentAlias(alias: String) {
        gameState.setOpponentAlias(alias)
    }

    private fun pause(seconds: Int = 3) {
        object : CountDownTimer(seconds.toLong() * 1_000L, Long.MAX_VALUE) {
            override fun onTick(millisUntilFinished: Long) {
                // do nothing - will never occur
            }

            override fun onFinish() {
                return // or nothing?
            }
        }.start()
    }

}