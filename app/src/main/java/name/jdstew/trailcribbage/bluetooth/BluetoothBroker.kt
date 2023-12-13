package name.jdstew.trailcribbage.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import name.jdstew.trailcribbage.GameModel
import name.jdstew.trailcribbage.GameModelListener
import name.jdstew.trailcribbage.MainActivity
import name.jdstew.trailcribbage.cribbage.CUT_MY_CUT
import name.jdstew.trailcribbage.cribbage.CUT_OPPONENT_CUT
import name.jdstew.trailcribbage.ui.OpponentViewModel


class BluetoothBroker(
    private val mainActivity: MainActivity,
    private val bluetoothManager: BluetoothManager,
    private val bluetoothAdapter: BluetoothAdapter,
    private val gameModel: GameModel
) : GameModelListener {

    private val TAG = "BluetoothBroker"


    private lateinit var bluetoothDevice: BluetoothDevice

    private lateinit var clientGatt: BluetoothGatt
    val clientCallback = GameClientCallback(this)

    private lateinit var serverGatt: BluetoothGattServer
    val serverCallback = GameServerCallback(this)

    private lateinit var connectedToDevice: BluetoothDevice

    override fun updateState(message: ByteArray) { // for outbound messages
//        serverGatt.notifyCharacteristicChanged(
//            device = bluetoothDevice,
//            BluetoothGattCharacteristic characteristic,
//            confirm = false,
//            value = message
//        )
    }

    fun updateStateReceived(message: ByteArray) {
        if (message[0] == CUT_MY_CUT) {
            message[0] = CUT_OPPONENT_CUT
        }

        gameModel.updateState(message)
    }

    fun resync() { // for outbound resyncing

        // todo: finish this method

    }

    fun recyncRecieved() {

        // todo: finish this method

    }

    fun selectBluetoothDevice(device: BluetoothDevice): Boolean {

        return true // if successful
    }

    // Callback indicating when GATT client has connected/disconnected to/from a remote GATT server.
    fun onConnectionStateChange(status: Int, newState: Int) {
        // todo: signal to the GameModel to save state?
    }


    /*
        This method should be initiated from the UI ViewModel.
    */
    @SuppressLint("MissingPermission")
    fun connectToBleDevice(bluetoothDevice: BluetoothDevice): Boolean {

        // todo: check to see if a valid connection is already established?

        serverGatt = bluetoothManager.openGattServer(mainActivity, serverCallback)
        serverGatt.addService(TrailCribbageGattService())

        // todo: connect to GATT client with callback
        // todo: send message to other server to verify game is on
        // todo: verify TrailCribbageGattService from connection

        // todo: add other services to GATT server

        connectedToDevice = bluetoothDevice

        return true // if successfully connected to device with game running

    }


    // flag for determining if actively scanning for BLE devices
    private var scanning = false

    // android.os.Handler https://developer.android.com/reference/kotlin/android/os/Handler
    private val handler = Handler(Looper.getMainLooper()) // note: Callback is a nested abstract class within Handler

    // stop scanning after 6 seconds
    private val SCAN_PERIOD: Long = 6000

    @SuppressLint("MissingPermission")
    fun scanForBleDevices(opponentViewModel: OpponentViewModel) {
        // android.bluetooth.le.BluetoothLeScanner https://developer.android.com/reference/android/bluetooth/le/BluetoothLeScanner
        val bluetoothScanner = bluetoothAdapter.bluetoothLeScanner // Low Energy
        if (bluetoothScanner == null) {
            Log.e(TAG, "Bluetooth is not enabled for creating Bluetooth scanner")
            return
        }

        val scanCallback = ScanCallbackToViewModel(opponentViewModel)
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({ // a Runnable within this code block
                scanning = false
                bluetoothScanner.stopScan(scanCallback)
            }, SCAN_PERIOD)
            scanning = true
            bluetoothScanner.startScan(scanCallback)
        } else {
            scanning = false
            bluetoothScanner.stopScan(scanCallback)
        }
    }

    private class ScanCallbackToViewModel(private val opponentViewModel: OpponentViewModel) :
        ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onScanResult(callbackType: Int, scanResult: ScanResult) {
            super.onScanResult(callbackType, scanResult)
            if (scanResult.dataStatus == ScanResult.DATA_COMPLETE) {
                opponentViewModel.bleDevicesFound.add(scanResult.device)
            }
        }
    }
}