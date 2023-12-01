package name.jdstew.trailcribbage.bluetooth

import android.bluetooth.BluetoothDevice

sealed class DeviceConnectionState {
    class Connected(val device: BluetoothDevice) : DeviceConnectionState()
    object Disconnected : DeviceConnectionState()
}