package name.jdstew.trailcribbage

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
import android.widget.Toast
import androidx.navigation.NavController
import name.jdstew.trailcribbage.bluetooth.BluetoothBroker
import name.jdstew.trailcribbage.cribbage.ME_MINE
import name.jdstew.trailcribbage.ui.GameNavigation
import name.jdstew.trailcribbage.ui.NavigationRoute
import name.jdstew.trailcribbage.ui.OpponentViewModel

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private lateinit var navHostController: NavController

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    // create a result contract for the callback results
    private val startActivityForResultContract = ActivityResultContracts.StartActivityForResult()

    // create an activity request launcher with a contract and ActivityResult lambda
    // 'this' below is a ComponentActivity
    private val activityResultLauncher = this.registerForActivityResult(
        startActivityForResultContract
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "Bluetooth has been turned on via ActivityResultCallback")
        } else if (activityResult.resultCode == Activity.RESULT_CANCELED) {
            Log.i(TAG, "User has rejected Bluetooth access request or an error has occurred")
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TrailCribbageTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // WARNING: construction of the navigation graph is asynchronous -
                    // early calls will produce log warnings and no actions
                    navHostController = GameNavigation()
                }
            }
        }


        bluetoothManager =
            application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        while (!bluetoothAdapter.isEnabled) {
            Log.i(TAG, "BluetoothAdapter found NOT enabled, launching permission request")
            activityResultLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))

            if (!bluetoothAdapter.isEnabled) {
                Log.i(TAG, "Waiting 10 seconds, then asking again to turn on Bluetooth")

                // blocking wait timer...
                // parameters are: duration and tick period
                val wait = object: CountDownTimer(10_000, 1_000) {
                    override fun onFinish() {
                        // do nothing
                    }

                    override fun onTick(p0: Long) {
                        // do nothing, but maybe display/toast when to retry
                    }
                }.start()
            }
        }

        GameModel.configure(
            mainActivity = this,
            bluetoothManager = bluetoothManager,
            bluetoothAdapter = bluetoothAdapter,
        )
        // todo: navigateTo() ...scan for opponents screen
    }
    
    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    fun navigateTo(destinationScreen: NavigationRoute, returnScreen: NavigationRoute) {
        if (this::navHostController.isInitialized) {
            navHostController.navigate(destinationScreen.route) {
                popUpTo(returnScreen.route)
            }
        } else {
            Log.w(TAG, "navHostController not yet initialized in Main Activity")
        }
    }

    fun announce(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun announce(player: Byte, messages: List<String>) {
        if (player == ME_MINE) {
            announce("You:")
        } else {
            announce("Your opponent:")
        }
        messages.forEach {
            announce(it)
        }
    }
}

