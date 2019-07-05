package br.com.agte.agt_tubproject.Fragments.WifiFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import br.com.agte.agt_tubproject.Activities.WifiActivity;
import br.com.agte.agt_tubproject.R;

public class WifiControlFragment extends Fragment {

    Animation wifi_anim;

    public WifiControlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wifi_control, container, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), WifiActivity.class);
                startActivity(i);
            }
        });

        ImageView tub_icon = v.findViewById(R.id.imgWifiIcon);
        wifi_anim = AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim);
        tub_icon.startAnimation(wifi_anim);

        return v;
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
