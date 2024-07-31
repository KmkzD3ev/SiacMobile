package br.com.zenitech.siacmobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.core.content.ContextCompat;

class AtivarDesativarBluetooth {

    private static final String TAG = "AtivarDesativarBluetooth";

    @SuppressLint("LongLogTag")
    void enableBT(Context context) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (!mBluetoothAdapter.isEnabled()) {
                Log.d(TAG, "Ativando Bluetooth...");
                mBluetoothAdapter.enable();
            } else {
                Log.d(TAG, "Bluetooth já está ativado.");
            }
        } else {
            Log.e(TAG, "Permissão BLUETOOTH_CONNECT não concedida.");
            throw new SecurityException("BLUETOOTH_CONNECT permission not granted");
        }
    }

    @SuppressLint("LongLogTag")
    void disableBT(Context context) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (mBluetoothAdapter.isEnabled()) {
                Log.d(TAG, "Desativando Bluetooth...");
                mBluetoothAdapter.disable();
            } else {
                Log.d(TAG, "Bluetooth já está desativado.");
            }
        } else {
            Log.e(TAG, "Permissão BLUETOOTH_CONNECT não concedida.");
            throw new SecurityException("BLUETOOTH_CONNECT permission not granted");
        }
    }
}
