package name.jdstew.trailcribbage

interface GameModelListener {
    fun updateState(message: ByteArray)
}