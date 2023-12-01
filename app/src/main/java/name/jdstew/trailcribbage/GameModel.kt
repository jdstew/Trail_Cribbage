package name.jdstew.trailcribbage

import android.util.Log
import name.jdstew.trailcribbage.cribbage.GameMessaging
import name.jdstew.trailcribbage.cribbage.GameState

object GameModel : GameModelListener {
    private val TAG = "GameModel"

    private val stateChangeListeners = mutableSetOf<GameModelListener>()
    private var gameState = GameState()

    fun addGameModelListener(listener: GameModelListener) {
        stateChangeListeners.add(listener)
    }

    fun removeGameModelListener(listener: GameModelListener) {
        stateChangeListeners.remove(listener)
    }

    override fun updateState(newMessage: ByteArray) {
        if (!GameMessaging.isMessageLogical(gameState.getGameState(), newMessage)) {
            Log.e(TAG, "New message is not logical")
            return
        }

//        TODO("is the update logical, in sequence and values?")
//        TODO("if valid, update the GameState")
//        TODO("if valid, retransmit to listeners")
        stateChangeListeners.forEach{
            it.updateState(newMessage)
        }
    }

}