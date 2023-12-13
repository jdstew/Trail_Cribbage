package name.jdstew.trailcribbage.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService

class GameServerCallback(private val bluetoothBroker: BluetoothBroker) :
    BluetoothGattServerCallback() {
// re: https://developer.android.com/reference/android/bluetooth/BluetoothGattServerCallback

    // A remote client has requested to read a local characteristic. READ/COMMANDS (client -> server)
    override fun onCharacteristicReadRequest(
        device: BluetoothDevice,
        requestId: Int,
        offset: Int,
        characteristic: BluetoothGattCharacteristic
    ) {
        // todo: complete method
    }

    // A remote client has requested to write to a local characteristic. WRITE/REQUESTS (client -> server)
    override fun onCharacteristicWriteRequest(
        device: BluetoothDevice,
        requestId: Int,
        characteristic: BluetoothGattCharacteristic,
        preparedWrite: Boolean,
        responseNeeded: Boolean,
        offset: Int,
        value: ByteArray
    ) {
        // todo: complete method
    }

    // Callback indicating when a remote device has been connected or disconnected.
    override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
        // status: BluetoothGatt.GATT_SUCCESS A GATT operation completed successfully
        // ststus: BluetoothGatt.GATT_FAILURE A GATT operation failed, errors other than the above
        // new state: BluetoothProfile.STATE_DISCONNECTED
        // new state: BluetoothProfile.STATE_CONNECTED
        // todo: complete method
        bluetoothBroker.onConnectionStateChange(status, newState)
    }

    // A remote client has requested to read a local descriptor.
    override fun onDescriptorReadRequest(
        device: BluetoothDevice,
        requestId: Int,
        offset: Int,
        descriptor: BluetoothGattDescriptor
    ) {
        // todo: complete method
    }

    // A remote client has requested to write to a local descriptor.
    override fun onDescriptorWriteRequest(
        device: BluetoothDevice,
        requestId: Int,
        descriptor: BluetoothGattDescriptor,
        preparedWrite: Boolean,
        responseNeeded: Boolean,
        offset: Int,
        value: ByteArray
    ) {
        // todo: complete method
    }

    // Execute all pending write operations for this device.
    override fun onExecuteWrite(device: BluetoothDevice, requestId: Int, execute: Boolean) {
        // todo: complete method
    }

    // Callback indicating the MTU for a given device connection has changed.
    override fun onMtuChanged(device: BluetoothDevice, mtu: Int) {
        // todo: complete method
    }

    // Callback invoked when a notification or indication has been sent to a remote device.
    override fun onNotificationSent(device: BluetoothDevice, status: Int) {
        // todo: complete method
    }

    // Callback triggered as result of BluetoothGattServer#readPhy
    override fun onPhyRead(device: BluetoothDevice, txPhy: Int, rxPhy: Int, status: Int) {
        // todo: complete method
    }

    // Callback triggered as result of BluetoothGattServer#setPreferredPhy, or as a result of remote device changing the PHY.
    override fun onPhyUpdate(device: BluetoothDevice, txPhy: Int, rxPhy: Int, status: Int) {
        // todo: complete method
    }

    // Indicates whether a local service has been added successfully.
    override fun onServiceAdded(status: Int, service: BluetoothGattService) {
        // todo: complete method
    }
}