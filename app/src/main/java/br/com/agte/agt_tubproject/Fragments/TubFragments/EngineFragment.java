package br.com.agte.agt_tubproject.Fragments.TubFragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Service.PostService;
import br.com.agte.agt_tubproject.Utils.SaveConfigs;
import br.com.agte.agt_tubproject.Utils.Utils;

public class EngineFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private static final int OFF_COLOR = 40;
    private static final int MIN_COLOR = 80;
    private static final int MAX_COLOR = 220;

    Animation rot_med_anim;
    Animation rot_max_anim;
    Typeface default_typeface;

    ImageView imgGear_off;
    ImageView imgGear_med;
    ImageView imgGear_max;
    ImageView imgLevels;
    TextView txtOff;
    TextView txtMed;
    TextView txtMax;

    int engine_level;
    int color = MIN_COLOR;
    boolean falling = false;
    Timer t_anim;
    Timer send_info;

    public EngineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_engine, container, false);

        txtOff = v.findViewById(R.id.txtEngineOff);
        txtMed = v.findViewById(R.id.txtEngineMed);
        txtMax = v.findViewById(R.id.txtEngineMax);

        default_typeface = Typeface.create(txtOff.getTypeface(), Typeface.NORMAL);

        imgGear_off = v.findViewById(R.id.imgGearOff);

        rot_med_anim = AnimationUtils.loadAnimation(getContext(), R.anim.rot_anim);
        rot_med_anim.setDuration(1200);
        imgGear_med = v.findViewById(R.id.imgGearMed);

        rot_max_anim = AnimationUtils.loadAnimation(getContext(), R.anim.rot_anim);
        rot_max_anim.setDuration(800);
        imgGear_max = v.findViewById(R.id.imgGearMax);

        imgLevels = v.findViewById(R.id.imgLevels);

        SeekBar skbPower = v.findViewById(R.id.skbPower);
        skbPower.setOnSeekBarChangeListener(this);

        Button btnBack = v.findViewById(R.id.btnEngineBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        engine_level = SaveConfigs.getInstance(getContext()).loadEnginePrefs();

        skbPower.setProgress(engine_level);

        Utils.setToolbar(getActivity(), null, R.string.ENGINE, R.drawable.motor);

        animate();

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
        SaveConfigs.getInstance(getContext()).saveEnginePrefs(engine_level);
    }

    private void setSelectedTextView(int selectedTextView){
        txtMax.setTextColor(Color.BLACK);
        txtMed.setTextColor(Color.BLACK);
        txtOff.setTextColor(Color.BLACK);
        color = MIN_COLOR; falling = false;
        switch (selectedTextView){
            case 0:
                imgLevels.setImageResource(R.drawable.levels_off);
                txtOff.setTypeface(default_typeface, Typeface.BOLD);
                txtMed.setTypeface(default_typeface, Typeface.NORMAL);
                txtMax.setTypeface(default_typeface, Typeface.NORMAL);
                break;
            case 1:
                imgLevels.setImageResource(R.drawable.levels_med);
                txtOff.setTypeface(default_typeface, Typeface.NORMAL);
                txtMed.setTypeface(default_typeface, Typeface.BOLD);
                txtMax.setTypeface(default_typeface, Typeface.NORMAL);
                break;
            case 2:
                imgLevels.setImageResource(R.drawable.levels_max);
                txtOff.setTypeface(default_typeface, Typeface.NORMAL);
                txtMed.setTypeface(default_typeface, Typeface.NORMAL);
                txtMax.setTypeface(default_typeface, Typeface.BOLD);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        engine_level = i;
        setSelectedTextView(engine_level);
        switch (i){
            case 0:
                imgGear_med.clearAnimation();
                imgGear_max.clearAnimation();
                break;
            case 1:
                imgGear_max.clearAnimation();
                imgGear_med.startAnimation(rot_med_anim);
                break;
            case 2:
                imgGear_med.clearAnimation();
                imgGear_max.startAnimation(rot_max_anim);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void animate() {
        t_anim = new Timer();
        t_anim.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(color <MAX_COLOR && !falling){
                            color++;
                            falling = color == MAX_COLOR;
                        }
                        else if(color >MIN_COLOR && falling){
                            color--;
                            falling = !(color == MIN_COLOR);
                        }
                        switch (engine_level){
                            case 2: txtMax.setTextColor(Color.rgb(color,0,0));
                                break;
                            case 1: txtMed.setTextColor(Color.rgb(color, color, 0));
                                break;
                            case 0: txtOff.setTextColor(Color.rgb(color -OFF_COLOR, color -OFF_COLOR, color -OFF_COLOR));
                                break;
                        }

                    }
                });
            }
        }, 0, 5);
    }
}
