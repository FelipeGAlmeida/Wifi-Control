package br.com.agte.agt_tubproject.Fragments.TubFragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

import br.com.agte.agt_tubproject.Activities.ConnectActivity;
import br.com.agte.agt_tubproject.Activities.TubActivity;
import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Service.BluetoothService;
import br.com.agte.agt_tubproject.Utils.Constants;
import br.com.agte.agt_tubproject.Utils.SaveConfigs;

public class ControlsFragment_New extends Fragment {

    ProgressBar pgbEngine;
    ProgressBar pgbColor;
    ProgressBar pgbTemp;
    TextView txtEngine;
    TextView txtTemp;
    ImageView imgColor;

    public ControlsFragment_New() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_controls_new, container, false);

        pgbEngine = v.findViewById(R.id.pgbEnginePower);
        pgbEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkIsConnected()) {
                    ((TubActivity) getActivity()).replaceFragments(new EngineFragment(), Constants.ENGINE, null);
                }
            }
        });
        pgbColor = v.findViewById(R.id.pgbColor);
        pgbColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(checkIsConnected()) {
                    ((TubActivity) getActivity()).replaceFragments(new TubLedFragment(), Constants.LED, null);
                //    ((TubActivity) getActivity()).replaceFragments(new ColorFragment(), Constants.COLOR);
                //}
            }
        });
        pgbTemp = v.findViewById(R.id.pgbTemperature);
        pgbTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(checkIsConnected()) {
                    ((TubActivity) getActivity()).replaceFragments(new TemperatureFragment(), Constants.TEMPERATURE, null);
                //}
            }
        });
        txtEngine = v.findViewById(R.id.txtEngineStatus);
        txtTemp = v.findViewById(R.id.txtTemperatureStatus);
        imgColor = v.findViewById(R.id.imgColorStatus);

        return v;
    }

    private void loadTubStatus(){
        // Engine
        int e = SaveConfigs.getInstance(getContext()).loadEnginePrefs();
        pgbEngine.setProgress(e);
        switch (e){
            case 0: int color_off = Color.parseColor(Constants.COLOR_OFF);
                    txtEngine.setText(getResources().getString(R.string.ENGINE_POWER_OFF));
                    txtEngine.setTextColor(color_off);
                break;
            case 1: int color_med = Color.parseColor(Constants.COLOR_MED);
                    txtEngine.setText(getResources().getString(R.string.ENGINE_POWER_MED));
                    pgbEngine.getProgressDrawable().setColorFilter(color_med, PorterDuff.Mode.MULTIPLY);
                    txtEngine.setTextColor(color_med);
                break;
            case 2: int color_max = Color.parseColor(Constants.COLOR_MAX);
                    txtEngine.setText(getResources().getString(R.string.ENGINE_POWER_MAX));
                    pgbEngine.getProgressDrawable().setColorFilter(color_max, PorterDuff.Mode.MULTIPLY);
                    txtEngine.setTextColor(color_max);
                break;
        }

        // Color
        String[] rgb = SaveConfigs.getInstance(getContext()).loadColorPrefs().split(Constants.COLOR_SPLITER);
        imgColor.getDrawable().setColorFilter(Color.rgb(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])), PorterDuff.Mode.MULTIPLY);
        pgbColor.getProgressDrawable().setColorFilter(Color.rgb(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])), PorterDuff.Mode.MULTIPLY);

        // Temperature
        int t = SaveConfigs.getInstance(getContext()).loadTemperaturePrefs();
        pgbTemp.setProgress(t-Constants.MIN_TEMP);
        txtTemp.setText(String.format(Locale.getDefault(), "%d", t));
    }

    private void initTubStatus(){
        // Engine
        pgbEngine.setProgress(0);
        int color_off = Color.parseColor(Constants.COLOR_OFF);
        txtEngine.setText(getResources().getString(R.string.ENGINE_POWER_OFF));
        txtEngine.setTextColor(color_off);

        // Color
        imgColor.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        pgbColor.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);

        // Temperature
        pgbTemp.setProgress(25-Constants.MIN_TEMP);
        txtTemp.setText(String.format(Locale.getDefault(), "%d", 25));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Boolean isConn = intent.getBooleanExtra(Constants.CONN_STATUS, false);
            if(!isConn) getActivity().finish();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if(BluetoothService.isConnected()) loadTubStatus();
        else initTubStatus();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.ADAPTER_STATUS));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private boolean checkIsConnected() {
        if(!BluetoothService.isConnected()){
            Intent i = new Intent(getContext(), ConnectActivity.class);
            startActivity(i);
            return false;
        }
        return true;
    }
}
