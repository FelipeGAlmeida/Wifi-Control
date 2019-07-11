package br.com.agte.agt_tubproject.Service;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;

import br.com.agte.agt_tubproject.Utils.Commands;
import br.com.agte.agt_tubproject.Utils.Constants;

public class BluetoothService {

    private static int BT_REQ_CODE = 123;

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
        setPairedDevices();
        BluetoothDevice device = null;
        for (BluetoothDevice d : pairedDevices) {
            if(d.getName().contains(device_name))
                device = d;
        }
        if(device != null) {
            ct = new ConnectThread(device);
            ct.start();
        }else{
            if(enableBluetoothAdapter())
                disconnectFromDevice();
        }
    }

    public void disconnectFromDevice(){
        if(connected) {
            if (ct != null) {
                ct.cancel();
                ct.interrupt();
            }
            ct = null;
            setConnected(false);
        }
    }

    public static boolean isConnected() {
        return connected;
    }

    private void setConnected(boolean conn){
        connected = conn;
        Intent intent = new Intent("custom-event-name");
        intent.putExtra("CONN", connected);
        LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent);
    }

    // Get Bluetooth known devices
    private void setPairedDevices(){
        // Get a set of currently paired devices and append to 'pairedDevices'
        pairedDevices = mBtAdapter.getBondedDevices();
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
    private boolean enableBluetoothAdapter() {
        // Check device has Bluetooth and that it is turned on
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter == null) {
            Toast.makeText(mActivity, "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (mBtAdapter.isEnabled()) {
                return true;
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(enableBtIntent, BT_REQ_CODE);
                return false;
            }
        }
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
                    disconnectFromDevice();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                return;
            }
            setConnected(true);

            while(!end){
                try {
                    byte[] buffer = new byte[2];
                    int length = mmSocket.getInputStream().read(buffer);
                    String text = new String(buffer, 0, length);
                    if(buffer[0] == Commands.color_r[0]){
                        broadcastData(Constants.COLOR_R, buffer[1]);
                    }
                    if(buffer[0] == Commands.color_g[0]){
                        broadcastData(Constants.COLOR_G, buffer[1]);
                    }
                    if(buffer[0] == Commands.color_b[0]){
                        broadcastData(Constants.COLOR_B, buffer[1]);
                    }
                    if(buffer[0] == Commands.engine[0]){
                        broadcastData(Constants.ENGINE, buffer[1]);
                    }
                    if(buffer[0] == Commands.temperature[0]){
                        broadcastData(Constants.TEMPERATURE, buffer[1]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    disconnectFromDevice();
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /** Broadcast received data **/
        public void broadcastData(String type, int value){
            Intent intent = new Intent("custom-event-name2");
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
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
