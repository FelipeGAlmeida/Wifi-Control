package br.com.agte.agt_tubproject.Fragments.WifiFragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Utils.Constants;

public class FinalFragment extends Fragment {

    private ImageView imgStatus;
    private TextView txtStatus;
    private TextView txtInfo;
    private Button btnClose;

    private WifiManager wifiManager;
    private Handler handler;

    private boolean success;

    public FinalFragment() {
        handler = new Handler();
        handler.postDelayed(timeoutCallback,30500);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_final, container, false);

        imgStatus = view.findViewById(R.id.imgStatus);
        txtStatus = view.findViewById(R.id.txtStatus);
        txtInfo = view.findViewById(R.id.txtInfo);
        btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    getActivity().getFragmentManager().popBackStack(Constants.SETUP1, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getFragmentManager().popBackStack(Constants.SETUP2, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getFragmentManager().popBackStack(Constants.FINAL, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        btnClose.setEnabled(false);

        wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        return view;
    }

    public void setResponse(boolean ok){
        handler.removeCallbacks(timeoutCallback);
        success = ok;
        btnClose.setEnabled(true);
        wifiManager.disconnect();
        if(ok){
            btnClose.setText(getResources().getString(R.string.FINISH));
            imgStatus.setImageResource(R.drawable.success);
            txtStatus.setText(getResources().getString(R.string.NETWORK_APPLIED));
            txtStatus.setTextColor(Color.rgb(34,133,75));
            txtInfo.setText(getResources().getString(R.string.THANKS));
        }else{
            btnClose.setText(getResources().getString(R.string.RESTART));
            imgStatus.setImageResource(R.drawable.error);
            txtStatus.setText(getResources().getString(R.string.NETWORK_FAILED));
            txtStatus.setTextColor(Color.rgb(200,53,0));
            txtInfo.setText(getResources().getString(R.string.VERIFY_NETWORK));
        }
    }

    Runnable timeoutCallback = new Runnable() {
        @Override
        public void run() {
            setResponse(false);
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
