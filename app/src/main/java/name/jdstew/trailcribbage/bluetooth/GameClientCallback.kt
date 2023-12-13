package name.jdstew.trailcribbage.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor

class GameClientCallback(private val bluetoothBroker: BluetoothBroker): BluetoothGattCallback() {

    // Callback triggered as a result of a remote characteristic notification. NOTIFY/NOTIFICATION (server -> client).
    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) {
        // todo: complete method
    }

    // Callback reporting the result of a characteristic read operation. READ/COMMANDS (client -> server)
    override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray, status: Int) {
        // todo: complete method
    }

    // Callback indicating the result of a characteristic write operation. WRITE/REQUESTS (client -> server)
    override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
        // todo: complete method
    }

    // Callback indicating when GATT client has connected/disconnected to/from a remote GATT server.
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        // status: BluetoothGatt.GATT_SUCCESS A GATT operation completed successfully
        // ststus: BluetoothGatt.GATT_FAILURE A GATT operation failed, errors other than the above
        // new state: BluetoothProfile.STATE_DISCONNECTED
        // new state: BluetoothProfile.STATE_CONNECTED
        // todo: complete method
        bluetoothBroker.onConnectionStateChange(status, newState)
    }

    // Callback reporting the result of a descriptor read operation.
    override fun onDescriptorRead(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int, value: ByteArray) {
        // todo: complete method
    }

    // Callback indicating the result of a descriptor write operation.
    override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
        // todo: complete method
    }

    // Callback indicating the MTU for a given device connection has changed.
    override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
        // todo: complete method
    }

    // Callback triggered as result of BluetoothGatt#readPhy
    override fun onPhyRead(gatt: BluetoothGatt, txPhy: Int, rxPhy: Int, status: Int) {
        // todo: complete method
    }

    // Callback triggered as result of BluetoothGatt#setPreferredPhy, or as a result of remote device changing the PHY.
    override fun onPhyUpdate(gatt: BluetoothGatt, txPhy: Int, rxPhy: Int, status: Int) {
        // todo: complete method
    }

    // Callback reporting the RSSI for a remote device connection.
    override fun onReadRemoteRssi(gatt: BluetoothGatt, rssi: Int, status: Int) {
        // todo: complete method
    }

    // Callback invoked when a reliable write transaction has been completed.
    override fun onReliableWriteCompleted(gatt: BluetoothGatt, status: Int) {
        // todo: complete method
    }

    // Callback indicating service changed event is received
    override fun onServiceChanged(gatt: BluetoothGatt) {
        // todo: complete method
    }

    // Callback invoked when the list of remote services, characteristics and descriptors for the remote device have been updated, ie new services have been discovered.
    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        // todo: complete method
    }
}