package br.com.agte.agt_tubproject.Fragments.BtFragments;

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

public class ConnectFragment extends Fragment {

    private Button btn_conn_bt;
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
        updateConnButton(BluetoothService.isConnected());
        btn_conn_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!has_btConn) {
                    Toast.makeText(getActivity(), "CONECTANDO....", Toast.LENGTH_SHORT).show();
                    BluetoothService.self(getActivity()).connectToDevice("Tub");
                } else BluetoothService.self(getActivity()).disconnectFromDevice();
            }
        });

        disps.add("Tub");
        ArrayAdapter<String> adp = new ArrayAdapter<> (getActivity(), android.R.layout.simple_spinner_dropdown_item, disps);
        Spinner spn_disps = v.findViewById(R.id.spn_disps);
        spn_disps.setAdapter(adp);

        return v;
    }

    private void updateConnButton(Boolean isConn){
        if(isConn) {
            btn_conn_bt.setText("Desconectar");
            if(!has_btConn  && disps.size() > 0)
                Toast.makeText(getActivity(), "CONECTADO COM SUCESSO!", Toast.LENGTH_SHORT).show();
        } else {
            btn_conn_bt.setText("Conectar");
            if(!has_btConn && disps.size() > 0)
                Toast.makeText(getActivity(), "FALHA AO CONECTAR!", Toast.LENGTH_LONG).show();
        }
        has_btConn = isConn;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Boolean isConn = intent.getBooleanExtra("CONN", false);
            updateConnButton(isConn);
        }
    };

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
        super.onResume();
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
