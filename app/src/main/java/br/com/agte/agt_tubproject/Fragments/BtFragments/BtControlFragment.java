package br.com.agte.agt_tubproject.Fragments.BtFragments;

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

import br.com.agte.agt_tubproject.Activities.ConnectActivity;
import br.com.agte.agt_tubproject.Activities.MainActivity;
import br.com.agte.agt_tubproject.Activities.WifiActivity;
import br.com.agte.agt_tubproject.R;

public class BtControlFragment extends Fragment {

    Animation bt_anim;

    public BtControlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bt_control, container, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), ConnectActivity.class);
                startActivity(i);
            }
        });

        ImageView next = v.findViewById(R.id.imgNextBT);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.getmViewPager().setCurrentItem(2);
            }
        });

        ImageView back = v.findViewById(R.id.imgPrevBT);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.getmViewPager().setCurrentItem(0);
            }
        });

        ImageView tub_icon = v.findViewById(R.id.imgBtIcon);
        bt_anim = AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim);
        tub_icon.startAnimation(bt_anim);

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
