package br.com.agte.agt_tubproject.Fragments.TubFragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Utils.Constants;
import br.com.agte.agt_tubproject.Utils.SaveConfigs;
import br.com.agte.agt_tubproject.Utils.Utils;

public class TemperatureFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    SeekBar skbTemp;
    TextView txt_temp;

    List<Integer> commands;

    int temp_level = 25;
    boolean canSend = false;
    Timer t_anim, t_send;

    public TemperatureFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_temperature, container, false);

        txt_temp = v.findViewById(R.id.txtTemperature);

        skbTemp = v.findViewById(R.id.skbTemperature);
        skbTemp.setOnSeekBarChangeListener(this);

        Button btnBack = v.findViewById(R.id.btnTempBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        Utils.setToolbar(getActivity(), null, R.string.TEMPERTURE, R.drawable.temp);

        temp_level = SaveConfigs.getInstance(getContext()).loadTemperaturePrefs();
        //skbTemp.setProgress(temp_level);
        animate(temp_level);

        return v;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            int t = intent.getIntExtra(Constants.TEMPERATURE,-999);
            if(t >= 0) skbTemp.setProgress(t-Constants.MIN_TEMP);
        }
    };

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.RECEIVED_DATA));
        if(commands == null) commands = new ArrayList<>();
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
        t_anim.cancel();
        SaveConfigs.getInstance(getContext()).saveTemperaturePrefs(temp_level);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        temp_level = i + Constants.MIN_TEMP;
        txt_temp.setText(String.format(Locale.getDefault(), "%d", temp_level));
        if(canSend)
            startSendingData();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        t_anim.cancel();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void startSendingData(){
        commands.add(temp_level);
        if(t_send == null && canSend) {
            t_send = new Timer();
            t_send.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(commands.size() > 0) {
                        while(commands.size() > 4) commands.remove(0);

                        int t = commands.remove(0);
                        if (canSend) {
                            byte[] t_data = {(byte)t};
                            Utils.sendDataOverBT(getActivity(), Constants.TEMPERATURE, t_data);
                            if(commands.size() == 0){
                                Log.v("TTTTTemp", Integer.toString(t));
                                Utils.sendDataOverBT(getActivity(), Constants.TEMPERATURE, t_data);
                            }
                        }
                    }
                }
            }, 0, 250);
        }
    }

    private void animate(final int toProgess){
        canSend = false;
        t_anim = new Timer();
        t_anim.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int progress = skbTemp.getProgress();
                if(progress<toProgess-Constants.MIN_TEMP)
                    skbTemp.setProgress(progress+1);
                else {
                    try {
                        Thread.sleep(450);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    canSend = true;
                    cancel();
                }
            }
        },0,55);
    }
}
