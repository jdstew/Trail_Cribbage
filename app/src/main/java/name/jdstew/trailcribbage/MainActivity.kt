package name.jdstew.trailcribbage

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import name.jdstew.trailcribbage.ui.theme.TrailCribbageTheme
import android.os.Handler
import android.os.Looper
import name.jdstew.trailcribbage.ui.GameNavigation

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner

    // create a result contract for the callback results
    private val startActivityForResultContract = ActivityResultContracts.StartActivityForResult()

    // create an activity request launcher with a contract and ActivityResult lambda
    // 'this' below is a ComponentActivity
    private val activityResultLauncher = this.registerForActivityResult(
        startActivityForResultContract) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "Bluetooth has been turned on via ActivityResultCallback")
            } else if (activityResult.resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "User has rejected Bluetooth access request or an error has occurred")
            }
        }


    private var scanning = false
    private val handler = Looper.myLooper()?.let { Handler(it) }

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000

    @SuppressLint("MissingPermission")
    private fun scanLeDevice() {
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler?.postDelayed({
                scanning = false
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            scanning = true
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    private val leDeviceListAdapter: ArrayList<BluetoothDevice> = ArrayList()

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            leDeviceListAdapter.add(result.device)
            println("Bluetooth LE device found: " + result.device)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate() started")

        setContent {
            TrailCribbageTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameNavigation()
                }
            }
        }
/*
        bluetoothManager =
            application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter.isEnabled) {
            Log.i(TAG, "BluetoothAdapter found enabled")
//            GameServer.startServer(application, bluetoothManager, bluetoothAdapter)
        } else {
            Log.i(TAG, "BluetoothAdapter found NOT enabled")
            activityResultLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }

        // method fails if Bluetooth adapter is not enabled
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        scanLeDevice()
 */
    }

    // Run the game server as long as the app is on screen
    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}

