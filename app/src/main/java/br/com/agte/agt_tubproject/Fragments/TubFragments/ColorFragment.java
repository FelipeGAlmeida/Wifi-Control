package br.com.agte.agt_tubproject.Fragments.TubFragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Utils.Constants;
import br.com.agte.agt_tubproject.Utils.SaveConfigs;
import br.com.agte.agt_tubproject.Utils.Utils;

public class ColorFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

    SeekBar skb_red;
    SeekBar skb_green;
    SeekBar skb_blue;
    ImageView img_ColorShow;

    int r, g, b;
    boolean canSend = false;
    Timer t_anim;

    public ColorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_color, container, false);

        skb_red = v.findViewById(R.id.skbRed);
        skb_red.setOnSeekBarChangeListener(this);
        skb_green = v.findViewById(R.id.skbGreen);
        skb_green.setOnSeekBarChangeListener(this);
        skb_blue = v.findViewById(R.id.skbBlue);
        skb_blue.setOnSeekBarChangeListener(this);

        img_ColorShow = v.findViewById(R.id.img_colorShow);
        img_ColorShow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getContext(), "(RGB) : ("+r+", "+g+", "+b+")", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        Button btnBack = v.findViewById(R.id.btnColorBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        decodeSavedColorString(SaveConfigs.getInstance(getContext()).loadColorPrefs());

        Utils.setToolbar(getActivity(), null, R.string.COLOR, R.drawable.color);

        return v;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int r = intent.getIntExtra(Constants.COLOR_R,-999);
            if(r >= 0) skb_red.setProgress(r);
            int g = intent.getIntExtra(Constants.COLOR_G,-999);
            if(g >= 0) skb_green.setProgress(g);
            int b = intent.getIntExtra(Constants.COLOR_B,-999);
            if(b >= 0) skb_blue.setProgress(b);
        }
    };

    private void decodeSavedColorString(String colorString){
        String[] rgb = colorString.split("~");
//        skb_red.setProgress(Integer.parseInt(rgb[0]));
//        skb_green.setProgress(Integer.parseInt(rgb[1]));
//        skb_blue.setProgress(Integer.parseInt(rgb[2]));
        animate(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name2"));
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
        SaveConfigs.getInstance(getContext()).saveColorPrefs(r, g, b);
    }

    private void updateColorShow(){
        img_ColorShow.getDrawable().setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean bool) {
        switch (seekBar.getId()){
            case R.id.skbRed:
                r = i;
                if(canSend)
                    Utils.sendDataOverBT(getActivity(), Constants.COLOR_R,(byte) r);
                break;
            case R.id.skbGreen:
                g = i;
                if(canSend)
                    Utils.sendDataOverBT(getActivity(), Constants.COLOR_G,(byte) g);
                break;
            case R.id.skbBlue:
                b = i;
                if(canSend)
                    Utils.sendDataOverBT(getActivity(), Constants.COLOR_B,(byte) b);
                break;
        }
        updateColorShow();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        t_anim.cancel();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void animate(final int toProgess_r, final int toProgess_g, final int toProgess_b){
        canSend = false;
        t_anim = new Timer();
        t_anim.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int progress_r = skb_red.getProgress();
                int progress_g = skb_green.getProgress();
                int progress_b = skb_blue.getProgress();
                if(progress_r<toProgess_r)
                    skb_red.setProgress(progress_r+1);
                if(progress_g<toProgess_g)
                    skb_green.setProgress(progress_g+1);
                if(progress_b<toProgess_b)
                    skb_blue.setProgress(progress_b+1);

                if(progress_r == toProgess_r &&
                    progress_g == toProgess_g &&
                    progress_b == toProgess_b){
                    try {
                        Thread.sleep(450);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    canSend = true;
                    cancel();
                }

            }
        },0,5);
    }
}
