package br.com.agte.agt_tubproject.Fragments.TubFragments;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Utils.Commands;
import br.com.agte.agt_tubproject.Utils.Constants;
import br.com.agte.agt_tubproject.Utils.SaveConfigs;
import br.com.agte.agt_tubproject.Utils.Utils;
import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerView;

public class ColorFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, ColorObserver {

//    SeekBar skb_red;
//    SeekBar skb_green;
//    SeekBar skb_blue;
//    ImageView img_ColorShow;
    TextView colorTitle;
    ColorPickerView colorPicker;

    List<Integer> red_comm;
    List<Integer> green_comm;
    List<Integer> blue_comm;

    int r, g, b, old_color, led, nl;
//    boolean canSend = false;
//    Timer t_anim, t_send;
    int counter;

    public ColorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_color, container, false);

        led = getArguments().getInt(Constants.LED, -1);
        nl = getArguments().getInt(Constants.N_LED, 4);
        String dest;
        switch (led){
            case -1: dest = getString(R.string.SPOT_ALL);
                break;
            case 0: dest = getString(R.string.SPOT_STRIP);
                break;
            default: dest = getString(R.string.SPOT, led);
                break;
        }
        colorTitle = v.findViewById(R.id.txtColorTitle);
        colorTitle.setText(getString(R.string.SELECT_LIGHT_COLOR, dest.toLowerCase()));

//        skb_red = v.findViewById(R.id.skbRed);
//        skb_red.setOnSeekBarChangeListener(this);
//        skb_green = v.findViewById(R.id.skbGreen);
//        skb_green.setOnSeekBarChangeListener(this);
//        skb_blue = v.findViewById(R.id.skbBlue);
//        skb_blue.setOnSeekBarChangeListener(this);
//
//        img_ColorShow = v.findViewById(R.id.img_colorShow);
//        img_ColorShow.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                Toast.makeText(getContext(), "(RGB) : ("+r+", "+g+", "+b+")", Toast.LENGTH_LONG).show();
//                return false;
//            }
//        });
        colorPicker = v.findViewById(R.id.colorPicker);
        colorPicker.subscribe(this);

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
            int r = intent.getIntExtra(Constants.COLOR_R, -999);
            int g = intent.getIntExtra(Constants.COLOR_G, -999);
            int b = intent.getIntExtra(Constants.COLOR_B, -999);
            colorPicker.setInitialColor(Color.rgb(r,g,b));
            colorPicker.reset();
//            counter++;
//            skb_red.setOnSeekBarChangeListener(ColorFragment.this);
//            int r = intent.getIntExtra(Constants.COLOR_R, -999);
//            if (r >= 0) skb_red.setProgress(r);
//            int g = intent.getIntExtra(Constants.COLOR_G, -999);
//            if (g >= 0) skb_green.setProgress(g);
//            int b = intent.getIntExtra(Constants.COLOR_B, -999);
//            if (b >= 0) skb_blue.setProgress(b);
        }
    };

    private void decodeSavedColorString(String colorString){
        String[] rgb = colorString.split(Constants.COLOR_SPLITER);
        colorPicker.setInitialColor(Color.rgb(Integer.parseInt(rgb[0]),Integer.parseInt(rgb[1]),Integer.parseInt(rgb[2])));
        colorPicker.reset();
//        skb_red.setProgress(Integer.parseInt(rgb[0]));
//        skb_green.setProgress(Integer.parseInt(rgb[1]));
//        skb_blue.setProgress(Integer.parseInt(rgb[2]));
//        animate(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.RECEIVED_DATA));
        if(red_comm == null) red_comm = new ArrayList<>();
        if(green_comm == null) green_comm = new ArrayList<>();
        if(blue_comm == null) blue_comm = new ArrayList<>();
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
//        t_anim.cancel();
        SaveConfigs.getInstance(getContext()).saveColorPrefs(r, g, b);
    }

//    private void updateColorShow(){
//        img_ColorShow.getDrawable().setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY);
//    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean bool) {
//        switch (seekBar.getId()){
//            case R.id.skbRed:
//                r = i;
//                Utils.sendDataOverBT(getActivity(), Constants.COLOR_R, (byte) r);
////                if(canSend)
////                    red_comm.add(r); //Buffer Red
//                break;
//            case R.id.skbGreen:
//                g = i;
//                Utils.sendDataOverBT(getActivity(), Constants.COLOR_G, (byte) g);
////                if(canSend)
////                    green_comm.add(g); //Buffer Green
//                break;
//            case R.id.skbBlue:
//                b = i;
//                Utils.sendDataOverBT(getActivity(), Constants.COLOR_B, (byte) b);
////                if(canSend)
////                    blue_comm.add(b); //Buffer Blue
//                break;
//        }
//        //if(canSend) startSendingData(); //function to reduce amount of bluetooth data buffering and discarting some data
//        updateColorShow();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
//        t_anim.cancel();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

//    private void startSendingData(){
//        if(t_send == null) {
//            t_send = new Timer();
//            t_send.scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
//                    if(red_comm.size() > 0) {
//                        while(red_comm.size() > 5) red_comm.remove(0);
//                        int r = red_comm.remove(0);
//                        if (canSend) {
//                            Utils.sendDataOverBT(getActivity(), Constants.COLOR_R, (byte) r);
//                        }
//                    }
//                    if(green_comm.size() > 0) {
//                        while(green_comm.size() > 5) green_comm.remove(0);
//                        int g = green_comm.remove(0);
//                        if (canSend) {
//                            Utils.sendDataOverBT(getActivity(), Constants.COLOR_G, (byte) g);
//                        }
//                    }
//                    if(blue_comm.size() > 0) {
//                        while(blue_comm.size() > 5) blue_comm.remove(0);
//                        int b = blue_comm.remove(0);
//                        if (canSend) {
//                            Utils.sendDataOverBT(getActivity(), Constants.COLOR_B, (byte) b);
//                        }
//                    }
//                }
//            }, 0, 180);
//        }
//    }

//    private void animate(final int toProgess_r, final int toProgess_g, final int toProgess_b){
//        canSend = false;
//        t_anim = new Timer();
//        t_anim.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                int progress_r = skb_red.getProgress();
//                int progress_g = skb_green.getProgress();
//                int progress_b = skb_blue.getProgress();
//                if(progress_r<toProgess_r)
//                    skb_red.setProgress(progress_r+1);
//                if(progress_g<toProgess_g)
//                    skb_green.setProgress(progress_g+1);
//                if(progress_b<toProgess_b)
//                    skb_blue.setProgress(progress_b+1);
//
//                if(progress_r == toProgess_r &&
//                    progress_g == toProgess_g &&
//                    progress_b == toProgess_b){
//                    try {
//                        Thread.sleep(450);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    canSend = true;
//                    new CtrlThread().run();
//                    cancel();
//                }
//
//            }
//        },0,5);
//    }
    private int count;
    @Override
    public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
        if(color != old_color) {
            old_color = color;
            r = (color >> 16) & 0xFF;
            g = (color >> 8) & 0xFF;
            b = color & 0xFF;

            if(led == -1){
                byte[] b_color = {(byte) nl,(byte) r, (byte) g, (byte) b};
                Utils.sendDataOverBT(getActivity(), Constants.LED_ALL, b_color);
                return;
            }
            if(led == 0){
                byte[] b_color = {(byte) nl,(byte) r, (byte) g, (byte) b};
                Utils.sendDataOverBT(getActivity(), Constants.STRIP, b_color);
                return;
            }
            byte[] b_color = {(byte)led, (byte) r, (byte) g, (byte) b};
            Utils.sendDataOverBT(getActivity(), Constants.COLOR, b_color);

            //Utils.sendDataOverBT(getActivity(), Constants.COLOR_R, (byte)r);
            //Utils.sendDataOverBT(getActivity(), Constants.COLOR_G, (byte)g);
            //Utils.sendDataOverBT(getActivity(), Constants.COLOR_B, (byte)b);
            //Log.v("COUNT:    ", Integer.toString(count++));
        }
    }

    // Control flow Thread
//    private class CtrlThread extends Thread {
//        private boolean end = false;
//        private int c = 0;
//
//        public CtrlThread() {
//
//        }
//
//        public void run() {
//            while(!end){
//                if(counter > 0) {
//                    if (counter > c) {
//                        //Atualizando
//                        Log.v("AAAAA", "Atualizando.............");
//                        canSend = false;
//                        skb_red.setOnSeekBarChangeListener(null);
//                    } else {
//                        //Acabou
//                        Log.v("AAAAA", "FIM DA ATUALIZAÇÃO !");
//                        counter = 0;
//                        canSend = true;
//                        skb_red.setOnSeekBarChangeListener(ColorFragment.this);
//                    }
//                    c = counter;
//                    try {
//                        Thread.sleep(350);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//
//        /** Will cancel the Thread exection **/
//        @Override
//        public void interrupt() {
//            super.interrupt();
//            end = true;
//        }
//
//        /** Will cancel an in-progress connection, and close the socket */
//        void cancel() {
////            try {
////
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//        }
//    }
}
