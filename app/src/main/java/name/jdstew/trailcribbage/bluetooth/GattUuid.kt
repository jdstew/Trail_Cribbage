package name.jdstew.trailcribbage.bluetooth

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import java.util.*

val SYNC_UUID: UUID = UUID.fromString("0000b81d-0000-1000-8000-00805f9b34fb")
val UPDATE_UUID: UUID = UUID.fromString("7db3e235-3608-41f3-a03c-955fcbd2ea4b")
val TRAIL_CRIBBAGE_UUID: UUID = UUID.fromString("36d4dc5c-814b-4097-a5a6-b93b39085928")
const val REQUEST_ENABLE_BT = 1

class TrailCribbageGattService(uuid: UUID = TRAIL_CRIBBAGE_UUID, serviceType: Int = BluetoothGattService.SERVICE_TYPE_PRIMARY): BluetoothGattService(uuid, serviceType) {
    init {
        addCharacteristic(BluetoothGattCharacteristic(SYNC_UUID, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ))
        addCharacteristic(BluetoothGattCharacteristic(UPDATE_UUID, BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ))
    }
}