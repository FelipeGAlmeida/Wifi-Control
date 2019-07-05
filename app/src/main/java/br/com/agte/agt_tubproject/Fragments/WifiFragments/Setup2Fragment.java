package br.com.agte.agt_tubproject.Fragments.WifiFragments;

import android.Manifest;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.agte.agt_tubproject.Activities.WifiActivity;
import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Utils.Constants;
import br.com.agte.agt_tubproject.Service.PostService;

public class Setup2Fragment extends Fragment {

    private FinalFragment finalFragment;

    private Button btn_setNetwork;
    private Spinner spn_ssid;
    private EditText edt_pswd;
    private ProgressBar pgb_loading;

    private WifiManager wifiManager;
    private List<WifiConfiguration> availableNetworks;
    private ArrayList<String> availableSSIDs;
    private String current_ssid;
    private String sensorSSID;

    public ProgressBar getPgb_loading() {
        return pgb_loading;
    }

    public Button getBtn_setNetwork() {
        return btn_setNetwork;
    }


    public Setup2Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_setup2, container, false);

        sensorSSID = getArguments().getString(Constants.SSID_VALUE);

        pgb_loading = view.findViewById(R.id.pgb_loading);
        spn_ssid = view.findViewById(R.id.spn_ssid);
        edt_pswd = view.findViewById(R.id.edt_pswd);
        edt_pswd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(edt_pswd.getText().toString().length()>=8){
                    btn_setNetwork.setEnabled(true);
                } else {
                    btn_setNetwork.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btn_setNetwork = view.findViewById(R.id.btn_conn);
        btn_setNetwork.setEnabled(false);
        btn_setNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_pswd.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), getResources().getString(R.string.TOAST_ERR_NO_PASSWORD), Toast.LENGTH_LONG).show();
                    return;
                }
                btn_setNetwork.setEnabled(false);
                conn_network(getContext());
                pgb_loading.setVisibility(View.VISIBLE);
                finalFragment = new FinalFragment();
                ((WifiActivity)getActivity()).replaceFragments(finalFragment, Constants.FINAL);
            }
        });

            initialize(ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        return view;
    }

        private void initialize(Boolean hasPermission){
        //Check the current network connected SSID
        wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(wifiManager == null){
            Toast.makeText(getContext(), getResources().getString(R.string.TOAST_ERR_SMT_WRONG), Toast.LENGTH_LONG).show();
            return;
        }

        current_ssid = wifiManager.getConnectionInfo().getSSID();

        //Load the available networks into List
        availableNetworks = wifiManager.getConfiguredNetworks();

        if(hasPermission) {
            //Start scan for available networks
            getActivity().registerReceiver(mWifiScanReceiver,
                    new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiManager.startScan();
        } else {
            //Populate SSIDs List
            populateSSIDs();

            //Set adapter to Spinner
            setSpinnerAdapter();

            //Cancel the loading
            pgb_loading.setVisibility(View.GONE);
        }
    }

    public void conn_network(final Context context) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + sensorSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
        //conf.preSharedKey = "\""+ networkPass +"\""; //WPA
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); //NO PSWD

        wifiManager.addNetwork(conf);
        availableNetworks = wifiManager.getConfiguredNetworks();

        String curr = wifiManager.getConnectionInfo().getSSID();
        if(!curr.equals("\""+sensorSSID+"\"")) {
            for (WifiConfiguration i : availableNetworks) {
                if (i.SSID != null && i.SSID.equals("\"" + sensorSSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();

                    break;
                }
            }
        }

        ConnectivityManager connManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connManager == null){
            Toast.makeText(getContext(), getResources().getString(R.string.TOAST_ERR_CANT_CONNECT), Toast.LENGTH_LONG).show();
            return;
        }
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            while (activeNetwork.getType() != ConnectivityManager.TYPE_WIFI) {
                Log.v(getResources().getString(R.string.D_TAG), getResources().getString(R.string.D_CONN));
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PostService ps = new PostService();
                ps.postNewNetwork(Setup2Fragment.this, context, spn_ssid.getSelectedItem().toString(), edt_pswd.getText().toString(), finalFragment);
            }
        },1800);
    }

    private void setSpinnerAdapter(){
        // Apply the adapter with the list to the spinner
        ArrayAdapter<String> adp = new ArrayAdapter<> (getActivity(), android.R.layout.simple_spinner_dropdown_item, availableSSIDs);
        spn_ssid.setAdapter(adp);

        if(availableSSIDs.size() == 0){
            Toast.makeText(getContext(), getResources().getString(R.string.TOAST_ERR_NO_AVAILABLE_NETWORK), Toast.LENGTH_LONG).show();
            return;
        }

        //Set the current one as selected
        setSelection();
    }

    private void populateSSIDs(){
        if(availableSSIDs == null) availableSSIDs = new ArrayList<>();
        for (WifiConfiguration wc: availableNetworks) {
            if(!wc.SSID.contains(Constants.ESP_SSID_PREFIX)) availableSSIDs.add(wc.SSID);
        }
    }

    private void setSelection() {
        for (String ssid : availableSSIDs) {
            if (current_ssid.equals("\""+ssid+"\"")) {
                spn_ssid.setSelection(availableSSIDs.indexOf(ssid));
            }
        }
    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if(intent.getAction() == null){
                Toast.makeText(getContext(), getResources().getString(R.string.TOAST_ERR_DETECT_NETWORK), Toast.LENGTH_LONG).show();
                return;
            }
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            List<ScanResult> mScanResults = wifiManager.getScanResults();
                if (availableSSIDs == null) availableSSIDs = new ArrayList<>();
                for (ScanResult sr : mScanResults) {
                    boolean contains = false;
                    for (String ssid : availableSSIDs) {
                        if (sr.SSID.equals(ssid)) contains = true;
                    }
                    if(!contains && !sr.SSID.contains(Constants.ESP_SSID_PREFIX)) availableSSIDs.add(sr.SSID);
                }
                setSpinnerAdapter();

                pgb_loading.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        getActivity().registerReceiver(mWifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        getActivity().unregisterReceiver(mWifiScanReceiver);
        super.onDetach();
    }
}
