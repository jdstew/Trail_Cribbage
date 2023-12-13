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

class OpponentViewModel() : ViewModel() {

    private val gameModel = GameModel
    private val bluetoothBroker = gameModel.getBluetoothBroker()
    val bleDevicesFound = mutableStateListOf<BluetoothDevice>()
    @SuppressLint("MissingPermission")
    fun selectBleDevice(device: BluetoothDevice) {
        if (bluetoothBroker.selectBluetoothDevice(device)) {
            gameModel.setOpponentName(device.name)
            gameModel.setOpponentAddress(device.address)
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
fun SelectOpponentScreen(
    opponentViewModel: OpponentViewModel = viewModel(factory = OpponentViewModel.Factory)
) {

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