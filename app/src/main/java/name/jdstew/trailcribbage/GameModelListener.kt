package name.jdstew.trailcribbage

interface GameModelListener {
    fun updateState(newState: ByteArray)
}