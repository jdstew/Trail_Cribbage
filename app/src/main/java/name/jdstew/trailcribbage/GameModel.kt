package name.jdstew.trailcribbage

import android.util.Log
import name.jdstew.trailcribbage.cribbage.*
import name.jdstew.trailcribbage.ui.NavigationRoute


object GameModel : GameModelListener {
    private val TAG = "GameModel"

    private lateinit var mainActivity: MainActivity

    private val stateChangeListeners = mutableSetOf<GameModelListener>()
    private var gameState = GameState()

    fun setMainActivity(activity: MainActivity) {
        mainActivity = activity
    }

    fun addGameModelListener(listener: GameModelListener) {
        stateChangeListeners.add(listener)
    }

    fun removeGameModelListener(listener: GameModelListener) {
        stateChangeListeners.remove(listener)
    }

    override fun updateState(newMessage: ByteArray) {
        if (!GameMessaging.isMessageLogical(gameState.getGameState(), newMessage)) {
            Log.e(TAG, "New message is not logical")
            // todo: process a game re-sync
            return
        }

        // share message with other listeners, except source
        stateChangeListeners.forEach{
            it.updateState(newMessage)
        }

        processMessage(newMessage)
    }

    private fun transmitStateUpdate(newMessage: ByteArray) {
        stateChangeListeners.forEach{
            it.updateState(newMessage)
        }
    }

    fun processMessage(message: ByteArray) {

        when (message[0]) {
            GAME_START -> processStart(message)
            CUT_START, CUT_MY_CUT, CUT_OPPONENT_CUT -> processCut(message)
            DEAL_START, DEAL_PONE_COMPLETE, DEAL_DEALER_COMPLETE -> processDeal(message)
            DEAL_STARTER_CUT, DEAL_STARTER_SELECTED -> processDealStarter(message)
            PLAY_START, PLAY_CARD_1, PLAY_CARD_2, PLAY_CARD_3, PLAY_CARD_4, PLAY_CARD_5, PLAY_CARD_6, PLAY_CARD_7, PLAY_CARD_8 -> processPlay(message)
            PLAY_GO -> processGo(message)
            SHOW_PONE_HAND, SHOW_DEALER_HAND, SHOW_DEALER_CRIB -> processShow(message)
            COMPLETION -> processCompletion(message)
            FINISHED -> processFinished(message)
        }
    }

    fun processStart(message: ByteArray) {
        gameState = GameState()
        gameState.setGameState(GAME_START)

        // todo: need intermediate stage of opponent selection
    }

    fun processCut(message: ByteArray) {
        // set and retransmit cut values
        when (message[0]) {
            CUT_START -> { // received by both sides
                gameState.setGameState(CUT_START)
                // ...wait for selections
            }
            CUT_MY_CUT -> {
                gameState.setGameState(CUT_MY_CUT)
                gameState.setMyCut(message[1])
            }
            CUT_OPPONENT_CUT -> {
                gameState.setGameState(CUT_OPPONENT_CUT)
                gameState.setOpponentCut(message[1])
            }
        }

        // evaluate cut values
        if (gameState.getMyCut() >= 0 && gameState.getOpponentCut() >= 0) {
            if (gameState.getMyCut() < gameState.getOpponentCut()) {
                gameState.setDealerID(DEALER_IS_ME)
                transmitStateUpdate(gameState.getHandOfOpponentMessage()) // so oppo UI gets the cards
                updateState(gameState.getHandOfMineMessage()) // so 'my' UI gets the cards

                mainActivity.announce("You will deal first")
                mainActivity.navigateTo(NavigationRoute.DealScreen, NavigationRoute.SplashScreen)
            } else if (gameState.getMyCut() > gameState.getOpponentCut()) {
                gameState.setDealerID(DEALER_IS_OPPONENT)

                mainActivity.announce("Your opponent will deal first")
                mainActivity.navigateTo(NavigationRoute.DealScreen, NavigationRoute.SplashScreen)
            } else { // case of being equal
                transmitStateUpdate(byteArrayOf(CUT_START, 0, 0, 0, 0, 0, 0, 0))

                mainActivity.announce("Cut cards are equal, let's re-cut")
                mainActivity.navigateTo(NavigationRoute.CutScreen, NavigationRoute.SplashScreen)
                return
            }
        }
    }

    fun processDeal(message: ByteArray) {
        when (message[0]) {
            DEAL_START -> {
                gameState.setGameState(DEAL_START)
                // ...wait for crib selections to come in
            }
            DEAL_PONE_COMPLETE -> {
                gameState.setOpponentCrib(message[1], message[2])
                gameState.setGameState(DEAL_PONE_COMPLETE)
            }
            DEAL_DEALER_COMPLETE -> {
                gameState.setDealerCrib(message[1], message[2])
                gameState.setGameState(DEAL_DEALER_COMPLETE)
            }
        }

        // check if all 4 cards in crib have been filled
        if (gameState.getCrib().binarySearch(-1) < 0) {
            mainActivity.announce("Let's count cards")
            mainActivity.navigateTo(NavigationRoute.DealStarterCutScreen, NavigationRoute.SplashScreen)
        }
    }

    fun processDealStarter(message: ByteArray) {
        when (message[0]) {
            DEAL_STARTER_CUT -> {
                gameState.setGameState(DEAL_STARTER_CUT)
                // ...wait for opponent's message
            }
            DEAL_STARTER_SELECTED -> {
                gameState.setGameState(DEAL_STARTER_SELECTED)
                if (gameState.getDealerID() == DEALER_IS_ME) {
                    val starterIndex = gameState.getCardFromDeck(message[1])
                    updateState(byteArrayOf(DEAL_STARTER_REVEALED, starterIndex, 0, 0, 0, 0, 0, 0))
                }
            }
            DEAL_STARTER_REVEALED -> {
                val starterIndex = message[1]
                val scoringReport = Scoring.scoreStarter(starterIndex.toInt())
                if (gameState.getDealerID() == DEALER_IS_ME) {

                    if (scoringReport.score > 0) {
                        gameState.addToScore(ME_MINE, scoringReport.score.toByte())
                        mainActivity.announce(ME_MINE, listOf("+{$scoringReport.score points}"))
                        mainActivity.announce(ME_MINE, scoringReport.announcements)
                    }

                    gameState.setPlayWhosNextTurn(OPPONENT_THEIRS)
                    updateState(byteArrayOf(PLAY_START, 0, 0, 0, 0, 0, 0, 0))

                } else {
                    if (scoringReport.score > 0) {
                        gameState.addToScore(OPPONENT_THEIRS, scoringReport.score.toByte())
                        mainActivity.announce(OPPONENT_THEIRS, listOf("+{$scoringReport.score points}"))
                        mainActivity.announce(OPPONENT_THEIRS, scoringReport.announcements)
                    }

                    gameState.setPlayWhosNextTurn(ME_MINE)
                }

                // check scores for win
                if (gameState.getPlayerScore(ME_MINE) >= 121 || gameState.getPlayerScore(OPPONENT_THEIRS) >= 121) {
                    mainActivity.navigateTo(NavigationRoute.GameFinishedScreen, NavigationRoute.SplashScreen)
                } else {
                    mainActivity.navigateTo(NavigationRoute.PlayScreen, NavigationRoute.SplashScreen)
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

    fun processPlay(message: ByteArray) {
        if (message[1] == 0.toByte()) {
            // do nothing, simply initiating this game phase and return point
            gameState.setGameState(PLAY_START)
            return
        }

        // add card to stack
        gameState.setPlayedCard(message[1], message[3])
        val scoringReport = gameState.getPlayedCardsScore()
        gameState.setPlayGoCount(0) // reset since a card is played

        if (gameState.getPlayWhosNextTurn() == ME_MINE) {
            gameState.removePlayedCard(ME_MINE, message[1])
            gameState.addToScore(ME_MINE, scoringReport.score.toByte())
            mainActivity.announce(ME_MINE, listOf("+{$scoringReport.score points}"))
            mainActivity.announce(ME_MINE, scoringReport.announcements)
        } else {
            gameState.removePlayedCard(OPPONENT_THEIRS, message[1])
            gameState.addToScore(OPPONENT_THEIRS, scoringReport.score.toByte())
            mainActivity.announce(OPPONENT_THEIRS, listOf("+{$scoringReport.score points}"))
            mainActivity.announce(OPPONENT_THEIRS, scoringReport.announcements)
        }

        // check scores for win
        if (gameState.getPlayerScore(ME_MINE) >= 121 || gameState.getPlayerScore(OPPONENT_THEIRS) >= 121) {
            mainActivity.navigateTo(NavigationRoute.GameFinishedScreen, NavigationRoute.SplashScreen)
        }

        gameState.setPlayWhosNextTurn((gameState.getPlayWhosNextTurn().toInt() * -1).toByte())

        if (message[0] == PLAY_CARD_8) {
            mainActivity.navigateTo(NavigationRoute.ScoringScreen, NavigationRoute.SplashScreen)
        } // else wait for next card to be played 
    }

    fun processGo(message: ByteArray) {
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

    fun processShow(message: ByteArray) {
        when (message[0]) {
            SHOW_PONE_HAND -> {
                gameState.setGameState(SHOW_PONE_HAND)
                // score
                // check for winner
            }
            SHOW_DEALER_HAND -> {
                gameState.setGameState(SHOW_DEALER_HAND)
                // score
                // check for winner
            }
            SHOW_DEALER_CRIB -> {
                gameState.setGameState(SHOW_DEALER_CRIB)
                // score
                // check for winner
            }
        }
    }

    fun processCompletion(message: ByteArray) {
        gameState.setGameState(COMPLETION)
        transmitStateUpdate(message)

        // swap dealers
        // todo: announce dealer is switching
        if (gameState.getDealerID() == DEALER_IS_ME) {
            gameState.setDealerID(DEALER_IS_OPPONENT)
            gameState.resetRound()
        } else {
            gameState.setDealerID(DEALER_IS_ME)
            gameState.resetRound()
            transmitStateUpdate(gameState.getHandOfOpponentMessage())
            transmitStateUpdate(gameState.getHandOfMineMessage())
        }
    }

    fun processFinished(message: ByteArray) {
        gameState.setGameState(FINISHED)
        transmitStateUpdate(message)

        // todo: announce winner
        gameState = GameState()
        gameState.setGameState(GAME_START)
        transmitStateUpdate(byteArrayOf(GAME_START, 0, 0, 0, 0, 0, 0, 0))
    }

    fun reSyncGame(message: ByteArray) {

    }

}