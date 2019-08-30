package br.com.agte.agt_tubproject.Fragments.BtFragments;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Service.BluetoothService;
import br.com.agte.agt_tubproject.Utils.Constants;

public class ConnectFragment extends Fragment {

    private Button btn_conn_bt;
    private Spinner spn_disps;

    private ArrayList<String> disps = new ArrayList<String>();

    private boolean has_btConn;

    public ConnectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connect, container, false);

        BluetoothService.self(getActivity());

        Button btn_pair_bt = v.findViewById(R.id.btnPairBT);
        btn_pair_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentOpenBluetoothSettings = new Intent();
                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intentOpenBluetoothSettings);
            }
        });

        btn_conn_bt = v.findViewById(R.id.btnConnectBT);
        updateConnButton(BluetoothService.isConnected(), null);
        btn_conn_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!has_btConn) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.CONNECTING), Toast.LENGTH_SHORT).show();
                    BluetoothService.self(getActivity()).connectToDevice(spn_disps.getSelectedItem().toString());
                } else BluetoothService.self(getActivity()).disconnectFromDevice(getResources().getString(R.string.DEVICE_DISCONN));
            }
        });

        spn_disps = v.findViewById(R.id.spn_disps);

        return v;
    }

    private void updateConnButton(Boolean isConn, String message){
        if(isConn) {
            btn_conn_bt.setText(getResources().getString(R.string.DISCONNECT));
        } else {
            btn_conn_bt.setText(getResources().getString(R.string.CONNECT));
        }
        if(message != null) Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        has_btConn = isConn;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if(intent.hasExtra(Constants.BT_STATUS)){
                switch(intent.getIntExtra(Constants.BT_STATUS, Constants.BROAD_ERR)){
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(getContext(), getResources().getString(R.string.BT_OFF), Toast.LENGTH_LONG).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        setDispSpinner();
                        break;
                }
            }else {
                Boolean isConn = intent.getBooleanExtra(Constants.CONN_STATUS, false);
                String message = intent.getStringExtra(Constants.CONN_MSG);
                updateConnButton(isConn, message);
            }
        }
    };

    private void setDispSpinner(){
        disps.addAll(BluetoothService.self(getActivity()).checkPairedDevices());
        ArrayAdapter<String> adp = new ArrayAdapter<> (getActivity(), android.R.layout.simple_spinner_dropdown_item, disps);
        spn_disps.setAdapter(adp);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.ADAPTER_STATUS));
        if(BluetoothAdapter.getDefaultAdapter().isEnabled()){
            setDispSpinner();
        }else{
            BluetoothService.self(getActivity()).enableBluetoothAdapter();
        }
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
