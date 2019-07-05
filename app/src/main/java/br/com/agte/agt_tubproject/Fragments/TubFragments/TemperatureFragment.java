package br.com.agte.agt_tubproject.Fragments.TubFragments;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Utils.Constants;
import br.com.agte.agt_tubproject.Utils.SaveConfigs;
import br.com.agte.agt_tubproject.Utils.Utils;

public class TemperatureFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    SeekBar skbTemp;
    TextView txt_temp;

    int temp_level = 25;
    Timer t_anim;

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
        animate(temp_level);

        return v;
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
        txt_temp.setText(Integer.toString(temp_level));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        t_anim.cancel();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void animate(final int toProgess){
        t_anim = new Timer();
        t_anim.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int progress = skbTemp.getProgress();
                if(progress<toProgess-Constants.MIN_TEMP)
                    skbTemp.setProgress(progress+1);
                else
                    cancel();
            }
        },0,95);
    }
}
