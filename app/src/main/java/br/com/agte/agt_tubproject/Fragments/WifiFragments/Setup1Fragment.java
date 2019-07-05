package br.com.agte.agt_tubproject.Fragments.WifiFragments;

import android.Manifest;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.agte.agt_tubproject.Activities.WifiActivity;
import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Utils.Constants;

public class Setup1Fragment extends Fragment {

    private Button btn_setup2;
    private Button btn_restart;
    private Spinner spn_ssid;
    private ProgressBar pgb_loading;

    private WifiManager wifiManager;
    private List<WifiConfiguration> availableNetworks;
    private ArrayList<String> availableSSIDs;
    private boolean registered;

    public ProgressBar getPgb_loading() {
        return pgb_loading;
    }

    public Setup1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_setup1, container, false);

        pgb_loading = view.findViewById(R.id.pgb_loading);
        spn_ssid = view.findViewById(R.id.spn_ssid);

        btn_restart = view.findViewById(R.id.btn_restart);
        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registered) getActivity().unregisterReceiver(mWifiScanReceiver);
                registered = false;
                getActivity().getFragmentManager().popBackStack();
            }
        });

        btn_setup2 = view.findViewById(R.id.btn_setup2);
        btn_setup2.setEnabled(false);
        btn_setup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(availableSSIDs.size()==0){
                    Toast.makeText(getContext(), getResources().getString(R.string.TOAST_ERR_NO_NETWORK), Toast.LENGTH_LONG).show();
                    return;
                }
                btn_setup2.setEnabled(false);
                pgb_loading.setVisibility(View.VISIBLE);

                if(registered) getActivity().unregisterReceiver(mWifiScanReceiver);
                registered = false;

                Setup2Fragment setup2Fragment = new Setup2Fragment();
                Bundle b = new Bundle();
                b.putString(Constants.SSID_VALUE, spn_ssid.getSelectedItem().toString());
                setup2Fragment.setArguments(b);
                ((WifiActivity)getActivity()).replaceFragments(setup2Fragment, Constants.SETUP2);
            }
        });

        initialize(ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        return view;
    }

        private void initialize(Boolean hasPermission){
        //Check the current network connected SSID
        wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //Load the available networks into List
        if(wifiManager == null){
            Toast.makeText(getContext(), getResources().getString(R.string.TOAST_ERR_SMT_WRONG), Toast.LENGTH_LONG).show();
            return;
        }
        availableNetworks = wifiManager.getConfiguredNetworks();

        if(hasPermission) {
            //Start scan for available networks
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

    private void setSpinnerAdapter(){
        // Apply the adapter with the list to the spinner
        ArrayAdapter<String> adp = new ArrayAdapter<> (getActivity(), android.R.layout.simple_spinner_dropdown_item, availableSSIDs);
        spn_ssid.setAdapter(adp);
        if(availableSSIDs.size()>0){
            btn_setup2.setEnabled(true);
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.TOAST_ERR_NO_NETWORK), Toast.LENGTH_LONG).show();
        }
    }

    private void populateSSIDs(){
        if(availableSSIDs == null) availableSSIDs = new ArrayList<>();
        for (WifiConfiguration wc: availableNetworks) {
            if(wc.SSID.contains(Constants.ESP_SSID_PREFIX)) availableSSIDs.add(wc.SSID);
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
                    if(sr.SSID.contains(Constants.ESP_SSID_PREFIX)) {
                        for (String ssid : availableSSIDs) {
                            if (sr.SSID.equals(ssid)) contains = true;
                        }
                        if(!contains) availableSSIDs.add(sr.SSID);
                    }
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
        registered = true;
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        if(registered) getActivity().unregisterReceiver(mWifiScanReceiver);
        registered = false;
        super.onDetach();
    }
}
