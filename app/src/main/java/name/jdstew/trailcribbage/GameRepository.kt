package name.jdstew.trailcribbage

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import name.jdstew.trailcribbage.cribbage.GameState
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class GameRepository(private val activity: MainActivity) {

    private val TAG = "GameRepository"

    @SuppressLint("MissingPermission")
    fun loadOrDefaultGame(device: BluetoothDevice): GameState {
        val context = activity.peekAvailableContext()
        if (context == null) {
            val newGame = GameState()
            newGame.setOpponentAddress(device.address)
            newGame.setOpponentName(device.name)
            return newGame
        }

        val fileName = device.address + ".ser"
        val fileList = activity.fileList()  // at assets folder, return type String[]
        var found = false
        for (file in fileList) {
            if (file.contains(fileName)) {
                found = true
                break
            }
        }
        if (!found) {
            val newGame = GameState()
            newGame.setOpponentAddress(device.address)
            newGame.setOpponentName(device.name)
            return newGame
        }

        ObjectInputStream(context.openFileInput(fileName)).use {
            return it.readObject() as GameState
        }
    }

    fun saveGame(gameState: GameState): Boolean {
        val context = activity.peekAvailableContext() ?: return false

        val fileName = gameState.getOpponentAddress() + ".ser"
        ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE)).use {
            it.writeObject(gameState)
        }

        return true
    }
}