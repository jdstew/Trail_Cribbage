package name.jdstew.trailcribbage

object GameModel : GameModelListener {
    private val stateChangeListeners = mutableSetOf<GameModelListener>()

    fun addGameModelListener(listener: GameModelListener) {
        stateChangeListeners.add(listener)
    }

    fun removeGameModelListener(listener: GameModelListener) {
        stateChangeListeners.remove(listener)
    }

    override fun updateState(newState: ByteArray) {
        TODO("is the update logical, in sequence and values?")
        TODO("if valid, update the GameState")
        TODO("if valid, retransmit to listeners")
    }

    private var cutSelectedCard = 1f;

    fun selectCutCard(card: Float) {
        cutSelectedCard = card
        println("game model changed to card $cutSelectedCard")
    }

}