package name.jdstew.trailcribbage.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

private const val TAG = "GameServer"

object GameServer {

    // hold reference to app context to run the chat server
    private var application: Application? = null
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    // This property will be null if bluetooth is not enabled or if advertising is not
    // possible on the device
    private var advertiser: BluetoothLeAdvertiser? = null
    private var advertiseCallback: AdvertiseCallback? = null
    private var advertiseSettings: AdvertiseSettings = buildAdvertiseSettings()
    private var advertiseData: AdvertiseData = buildAdvertiseData()

    // LiveData for reporting the messages sent to the device
//    private val messagesMutable = MutableLiveData<Message>()
//    val messages = messagesMutable as LiveData<Message>

    // LiveData for reporting connection requests
    private val connectionRequestMutable = MutableLiveData<BluetoothDevice>()
    val connectionRequest = connectionRequestMutable as LiveData<BluetoothDevice>

    // LiveData for reporting the messages sent to the device
    private val requestEnableBluetoothMutable = MutableLiveData<Boolean>()
    val requestEnableBluetooth = requestEnableBluetoothMutable as LiveData<Boolean>

    private var gattServer: BluetoothGattServer? = null
    private var gattServerCallback: BluetoothGattServerCallback? = null

    private var gattClient: BluetoothGatt? = null
    private var gattClientCallback: BluetoothGattCallback? = null

    // Properties for current chat device connection
    private var currentDevice: BluetoothDevice? = null
    private val deviceConnectionMutable = MutableLiveData<DeviceConnectionState>()
    val deviceConnection = deviceConnectionMutable as LiveData<DeviceConnectionState>
    private var gatt: BluetoothGatt? = null
    private var messageCharacteristic: BluetoothGattCharacteristic? = null


    fun startServer(app: Application, btm: BluetoothManager, bla: BluetoothAdapter) {
        application = app
        bluetoothManager = btm
        bluetoothAdapter = bla

//        setupGattServer(app)
//        startAdvertisement()
    }

    fun stopServer() {
        stopAdvertising()
    }

    fun setCurrentChatConnection(device: BluetoothDevice) {
        currentDevice = device
        // Set gatt so BluetoothChatFragment can display the device data
        deviceConnectionMutable.value = DeviceConnectionState.Connected(device)
        connectToChatDevice(device)
    }

    @SuppressLint("MissingPermission") // because permission is required by manifest.xml
    private fun connectToChatDevice(device: BluetoothDevice) {
        gattClientCallback = GattClientCallback()
        gattClient = device.connectGatt(application, false, gattClientCallback)
    }

    @SuppressLint("MissingPermission") // because permission is required by manifest.xml
    fun sendMessage(message: String): Boolean {
        Log.d(TAG, "Send a message")
        messageCharacteristic?.let { characteristic ->
            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT

            val messageBytes = message.toByteArray(Charsets.UTF_8)
            characteristic.value = messageBytes
            gatt?.writeCharacteristic(messageCharacteristic)

        }
        return false
    }

    /**
     * Function to setup a local GATT server.
     * This requires setting up the available services and characteristics that other devices
     * can read and modify.
     */
    @SuppressLint("MissingPermission") // because permission is required by manifest.xml
    private fun setupGattServer(app: Application) {
        gattServerCallback = GattServerCallback()
        gattServer = bluetoothManager.openGattServer(
            app,
            gattServerCallback
        ).apply {
            addService(setupGattService())
        }
    }

    /**
     * Function to create the GATT Server with the required characteristics and descriptors
     */
    private fun setupGattService(): BluetoothGattService {
        // Setup gatt service
        val service = BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        // need to ensure that the property is writable and has the write permission
        val messageCharacteristic = BluetoothGattCharacteristic(
            MESSAGE_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(messageCharacteristic)
        val confirmCharacteristic = BluetoothGattCharacteristic(
            CONFIRM_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(confirmCharacteristic)

        return service
    }

    /**
     * Start advertising this device so other BLE devices can see it and connect
     */
    @SuppressLint("MissingPermission") // because permission is required by manifest.xml
    private fun startAdvertisement() {
        advertiser = bluetoothAdapter.bluetoothLeAdvertiser
        Log.d(TAG, "startAdvertisement: with advertiser $advertiser")

        if (advertiseCallback == null) {
            advertiseCallback = DeviceAdvertiseCallback()

            advertiser?.startAdvertising(advertiseSettings, advertiseData, advertiseCallback)
        }
    }

    /**
     * Stops BLE Advertising.
     */
    @SuppressLint("MissingPermission") // because permission is required by manifest.xml
    private fun stopAdvertising() {
        Log.d(TAG, "Stopping Advertising with advertiser $advertiser")
        advertiser?.stopAdvertising(advertiseCallback)
        advertiseCallback = null
    }

    /**
     * Returns an AdvertiseData object which includes the Service UUID and Device Name.
     */
    private fun buildAdvertiseData(): AdvertiseData {
        /**
         * Note: There is a strict limit of 31 Bytes on packets sent over BLE Advertisements.
         * This limit is outlined in section 2.3.1.1 of this document:
         * https://inst.eecs.berkeley.edu/~ee290c/sp18/note/BLE_Vol6.pdf
         *
         * This limit includes everything put into AdvertiseData including UUIDs, device info, &
         * arbitrary service or manufacturer data.
         * Attempting to send packets over this limit will result in a failure with error code
         * AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE. Catch this error in the
         * onStartFailure() method of an AdvertiseCallback implementation.
         */
        val dataBuilder = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(SERVICE_UUID))
            .setIncludeDeviceName(true)

        /* For example - this will cause advertising to fail (exceeds size limit) */
        //String failureData = "asdghkajsghalkxcjhfa;sghtalksjcfhalskfjhasldkjfhdskf";
        //dataBuilder.addServiceData(Constants.Service_UUID, failureData.getBytes());
        return dataBuilder.build()
    }

    /**
     * Returns an AdvertiseSettings object set to use low power (to help preserve battery life)
     * and disable the built-in timeout since this code uses its own timeout runnable.
     */
    private fun buildAdvertiseSettings(): AdvertiseSettings {
        return AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setTimeout(0)
            .build()
    }

    /**
     * Custom callback for the Gatt Server this device implements
     */
    private class GattServerCallback : BluetoothGattServerCallback() {

        @SuppressLint("MissingPermission") // because permission is required by manifest.xml
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            val isSuccess = status == BluetoothGatt.GATT_SUCCESS
            val isConnected = newState == BluetoothProfile.STATE_CONNECTED

            Log.d(
                TAG,
                "onConnectionStateChange: Server $device ${device.name} success: $isSuccess connected: $isConnected"
            )
            if (isSuccess && isConnected) {
//                connectionRequestMutable.postValue(device)
            } else {
//                connectionRequestMutable.postValue(DeviceConnectionState.Disconnected)
            }
        }

        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)
            if (characteristic.uuid == MESSAGE_UUID) {
//                gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
                val message = value?.toString(Charsets.UTF_8)
                Log.d(TAG, "onCharacteristicWriteRequest: Have message: \"$message\"")
                message?.let {
//                    _messages.postValue(RemoteMessage(it))
                }
            }
        }
    }

    private class GattClientCallback : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            val isSuccess = status == BluetoothGatt.GATT_SUCCESS
            val isConnected = newState == BluetoothProfile.STATE_CONNECTED
            Log.d(TAG, "onConnectionStateChange: Client $gatt  success: $isSuccess connected: $isConnected")
            // try to send a message to the other device as a test
            if (isSuccess && isConnected) {
                // discover services
//                gatt.discoverServices()
            }
        }

        override fun onServicesDiscovered(discoveredGatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(discoveredGatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onServicesDiscovered: Have gatt $discoveredGatt")
//                gatt = discoveredGatt
                val service = discoveredGatt.getService(SERVICE_UUID)
//                messageCharacteristic = service.getCharacteristic(MESSAGE_UUID)
            }
        }
    }

    /**
     * Custom callback after Advertising succeeds or fails to start. Broadcasts the error code
     * in an Intent to be picked up by AdvertiserFragment and stops this Service.
     */
    private class DeviceAdvertiseCallback : AdvertiseCallback() {
        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            // Send error state to display
            val errorMessage = "Advertise failed with error: $errorCode"
            Log.d(TAG, "Advertising failed")
            //_viewState.value = DeviceScanViewState.Error(errorMessage)
        }

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
            Log.d(TAG, "Advertising successfully started")
        }
    }
}