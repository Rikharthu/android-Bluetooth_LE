package com.example.uberv.bluetoothledemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG=MainActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 307;

    private BluetoothAdapter bluetoothAdapter;
    private boolean scanning;
    private Handler handler;
    private LeScanCallback leScanCallback;
    private ArrayAdapter<String> deviceAdapter;

    private List<String> devices;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        leScanCallback = new LeScanCallback();

        handler= new Handler();

        devices=new ArrayList<>();
        deviceAdapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devices);
        ListView listView = (ListView) findViewById(R.id.devices_listview);
        listView.setAdapter(deviceAdapter);

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else{
            // everything ok
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_ENABLE_BT){
            if(requestCode==RESULT_OK){
                // turned on
            }else{
                // declined
                Toast.makeText(this, "Bluetooth is required for this app!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scanLeDevice(boolean enable){
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    scanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            scanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            scanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    public void scanLeDevicesBtnClick(View view) {
        scanLeDevice(true);
    }

    private class LeScanCallback implements BluetoothAdapter.LeScanCallback {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            // LE device found!
            Log.d(LOG_TAG,"onLeScan()");

            String deviceName = device.getName();
            String macAddress = device.getAddress();
            devices.add(deviceName+"\n"+macAddress);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    deviceAdapter.notifyDataSetChanged();
                }
            });
        }
    }

}
