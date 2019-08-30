package br.com.agte.agt_tubproject.Service;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Utils.Commands;
import br.com.agte.agt_tubproject.Utils.Constants;
import br.com.agte.agt_tubproject.Utils.SaveConfigs;

public class BluetoothService {

    private static boolean connected;

    private static Activity mActivity;
    private static BluetoothService self;
    private ConnectThread ct;

    private BluetoothAdapter mBtAdapter;
    private Set<BluetoothDevice> pairedDevices;

    private String device_name;

    // Constructor
    private BluetoothService(){
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        mActivity.getApplicationContext().registerReceiver(mAdapterReceiver,
                new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        self = this;
    }

    // Single instance
    public static BluetoothService self(Activity activity){
        mActivity = activity;
        return self != null ? self : new BluetoothService();
    }

    // Connection management
    public void connectToDevice(String bt_name){
        device_name = bt_name == null ? device_name : bt_name;
        if(device_name != null) {
            checkPairedDevices();
            BluetoothDevice device = null;
            for (BluetoothDevice d : pairedDevices) {
                if (d.getName().contains(device_name))
                    device = d;
            }
            if (device != null) {
                ct = new ConnectThread(device);
                ct.start();
            } else {
                if (enableBluetoothAdapter())
                    disconnectFromDevice(null);
            }
        }else{
            broadcastAdapterState(BluetoothAdapter.STATE_ON);
        }
    }

    public void disconnectFromDevice(String message){
        if(connected) {
            if (ct != null) {
                ct.cancel();
                ct.interrupt();
            }
            ct = null;
            setConnected(false, message);
        }
    }

    public static boolean isConnected() {
        return connected;
    }

    private void setConnected(boolean conn, String message){
        connected = conn;
        Intent intent = new Intent(Constants.ADAPTER_STATUS);
        intent.putExtra(Constants.CONN_STATUS, connected);
        intent.putExtra(Constants.CONN_MSG, message);
        LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
    }

    // Get Bluetooth known devices
    public ArrayList checkPairedDevices(){
        // Get a set of currently paired devices and append to 'pairedDevices'
        pairedDevices = mBtAdapter.getBondedDevices();
        ArrayList<String> deviceNames = new ArrayList<>();
        for(BluetoothDevice device : pairedDevices){
            deviceNames.add(device.getName());
        }
        return deviceNames;
    }

    // Bluetooth Communication
    public void sendData(byte[] msg){
        try {
            if(ct != null)
                ct.mmSocket.getOutputStream().write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Enable Bluetooth
    public boolean enableBluetoothAdapter() {
        // Check device has Bluetooth and that it is turned on
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter == null) {
            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.NO_BT_ADAPTER), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (mBtAdapter.isEnabled()) {
                return true;
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(enableBtIntent, Constants.ENABLE_BLUETOOTH_REQUEST);
                return false;
            }
        }
    }

    private final BroadcastReceiver mAdapterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        disconnectFromDevice(null);
                        broadcastAdapterState(BluetoothAdapter.STATE_OFF);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        broadcastAdapterState(BluetoothAdapter.STATE_ON);
                        break;
                }
            }
        }
    };

    /** Broadcast Adpater State **/
    private void broadcastAdapterState(int state){
        Intent i = new Intent(Constants.ADAPTER_STATUS);
        i.putExtra(Constants.BT_STATUS, state);
        LocalBroadcastManager.getInstance(mActivity).sendBroadcast(i);
    }

    // Connected thread
    private class ConnectThread extends Thread {
        private boolean end = false;
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(mmDevice.getUuids()[0].getUuid());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBtAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                connectException.printStackTrace();
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                    disconnectFromDevice(mActivity.getResources().getString(R.string.DEVICE_FAIL_CONN));
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                return;
            }
            setConnected(true, mActivity.getResources().getString(R.string.DEVICE_CONN));

            int state = 0;
            while(!end){
                try {
                    byte[] buffer = new byte[1];
                    int length = mmSocket.getInputStream().read(buffer);
                    if(length > 0) {
                        switch (state) {
                            case 0:
                                if (buffer[0] == Commands.color_r[0]) {
                                    state = 1;
                                }
                                if (buffer[0] == Commands.color_g[0]) {
                                    state = 2;
                                }
                                if (buffer[0] == Commands.color_b[0]) {
                                    state = 3;
                                }
                                if (buffer[0] == Commands.engine[0]) {
                                    state = 4;
                                }
                                if (buffer[0] == Commands.temperature[0]) {
                                    state = 5;
                                }
                                break;
                            case 1:
                                broadcastData(Constants.COLOR_R, buffer[0] & 0xFF);
                                Log.v("RRRRRR", Integer.toHexString(buffer[0] & 0xFF));
                                SaveConfigs.getInstance(mActivity).saveSpecificColor(Constants.COLOR_R, buffer[0]);
                                state = 0;
                                break;
                            case 2:
                                broadcastData(Constants.COLOR_G, buffer[0] & 0xFF);
                                Log.v("GGGGGG", Integer.toHexString(buffer[0] & 0xFF));
                                SaveConfigs.getInstance(mActivity).saveSpecificColor(Constants.COLOR_G, buffer[0]);
                                state = 0;
                                break;
                            case 3:
                                broadcastData(Constants.COLOR_B, buffer[0] & 0xFF);
                                Log.v("BBBBBB", Integer.toHexString(buffer[0] & 0xFF));
                                SaveConfigs.getInstance(mActivity).saveSpecificColor(Constants.COLOR_B, buffer[0]);
                                state = 0;
                                break;
                            case 4:
                                broadcastData(Constants.ENGINE, buffer[0]);
                                Log.v("MMMMM", Integer.toHexString(buffer[0]));
                                SaveConfigs.getInstance(mActivity).saveEnginePrefs(buffer[0]);
                                state = 0;
                                break;
                            case 5:
                                broadcastData(Constants.TEMPERATURE, buffer[0]);
                                Log.v("TTTTTT", Integer.toHexString(buffer[0]));
                                SaveConfigs.getInstance(mActivity).saveTemperaturePrefs(buffer[0]);
                                state = 0;
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    disconnectFromDevice(mActivity.getResources().getString(R.string.DEVICE_ERR_DISCONN));
                }
                try {
                    Thread.sleep(45);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /** Broadcast received data **/
        void broadcastData(String type, int value){
            Intent intent = new Intent(Constants.RECEIVED_DATA);
            intent.putExtra(type, value);
            LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
        }

        /** Will cancel the Thread exection **/
        @Override
        public void interrupt() {
            super.interrupt();
            end = true;
        }

        /** Will cancel an in-progress connection, and close the socket */
        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
