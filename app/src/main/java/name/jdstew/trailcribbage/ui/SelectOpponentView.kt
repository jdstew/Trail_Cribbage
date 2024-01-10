package name.jdstew.trailcribbage.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import name.jdstew.trailcribbage.GameModel
import name.jdstew.trailcribbage.GameModelListener
import name.jdstew.trailcribbage.cribbage.CUT_MY_CUT
import name.jdstew.trailcribbage.cribbage.CUT_OPPONENT_CUT
import name.jdstew.trailcribbage.cribbage.CUT_START

class OpponentViewModel() : GameModelListener, ViewModel() {
    var bleDevicesFound = mutableListOf<BluetoothDevice>()
        private set

    private val gameModel = GameModel
    private val bluetoothBroker = gameModel.getBluetoothBroker()
    init {
        gameModel.addGameModelListener(this)
        bluetoothBroker.scanForBleDevices(this)
    }

    override fun onCleared() { // when ViewModel is destroyed
        super.onCleared()
        gameModel.removeGameModelListener(this)
    }

    override fun updateState(message: ByteArray) {

    }

    fun addBleDeviceFound(device: BluetoothDevice) {
        bleDevicesFound.add(device)
    }

    @SuppressLint("MissingPermission")
    fun selectBleDevice(device: BluetoothDevice) {
        if (bluetoothBroker.selectBluetoothDevice(device)) {
            gameModel.setOpponentName(device.name)
            gameModel.setOpponentAddress(device.address)
            gameModel.updateState(byteArrayOf(CUT_START, 0, 0, 0, 0, 0, 0, 0))
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OpponentViewModel() as T
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun SelectOpponentScreen() {

    val opponentViewModel: OpponentViewModel = viewModel(factory = OpponentViewModel.Factory)

    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(10.dp)) {

        items(opponentViewModel.bleDevicesFound.size) { item ->
            Column() {
                Spacer(Modifier.height(10.dp))
                TextButton(onClick = {
                    opponentViewModel.selectBleDevice(opponentViewModel.bleDevicesFound[item])
                }) {
                    Text(text = opponentViewModel.bleDevicesFound[item].name)
                }
                Text(text = opponentViewModel.bleDevicesFound[item].address)
                Spacer(Modifier.height(10.dp))
                Divider()
            }
        }
    }
}