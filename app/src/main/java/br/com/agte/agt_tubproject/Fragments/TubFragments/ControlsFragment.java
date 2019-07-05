package br.com.agte.agt_tubproject.Fragments.TubFragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import br.com.agte.agt_tubproject.Activities.TubActivity;
import br.com.agte.agt_tubproject.Fragments.WifiFragments.Setup1Fragment;
import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Utils.Constants;

public class ControlsFragment extends Fragment implements Animation.AnimationListener {

    Animation fadeIn_anim;
    Animation blink_anim;

    ImageView imgEngine;
    ImageView imgColor;
    ImageView imgTemp;

    int anim_count = 0;

    public ControlsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_controls, container, false);

        fadeIn_anim = new AlphaAnimation(0, 1);
        fadeIn_anim.setStartOffset(100);
        fadeIn_anim.setInterpolator(new DecelerateInterpolator());
        fadeIn_anim.setAnimationListener(this);
        fadeIn_anim.setDuration(200);

        blink_anim = AnimationUtils.loadAnimation(getContext(), R.anim.blink_anim);
        blink_anim.setAnimationListener(this);

        imgEngine = v.findViewById(R.id.imgEngine);
        imgEngine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlButtons(false);
                imgEngine.startAnimation(blink_anim);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((TubActivity)getActivity()).replaceFragments(new EngineFragment(), Constants.ENGINE);
                        controlButtons(true);
                    }
                },180);
            }
        });
        imgEngine.setVisibility(View.INVISIBLE);
        imgColor = v.findViewById(R.id.imgColor);
        imgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlButtons(false);
                imgColor.startAnimation(blink_anim);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((TubActivity)getActivity()).replaceFragments(new ColorFragment(), Constants.COLOR);
                        controlButtons(true);
                    }
                },180);
            }
        });
        imgColor.setVisibility(View.INVISIBLE);
        imgTemp = v.findViewById(R.id.imgTemperature);
        imgTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controlButtons(false);
                imgTemp.startAnimation(blink_anim);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((TubActivity)getActivity()).replaceFragments(new TemperatureFragment(), Constants.TEMPERATURE);
                        controlButtons(true);
                    }
                },180);
            }
        });
        imgTemp.setVisibility(View.INVISIBLE);

        return v;
    }

    private void controlButtons(boolean enable){
        imgEngine.setClickable(enable);
        imgColor.setClickable(enable);
        imgTemp.setClickable(enable);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        controlButtons(false);
        imgEngine.startAnimation(fadeIn_anim);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if(animation.getDuration() == 120){
            return;
        }
        anim_count++;
        fadeIn_anim.setStartOffset(50);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(animation.getDuration() == 120){
            return;
        }
        if(anim_count == 1) {
            imgEngine.clearAnimation();
            imgEngine.setVisibility(View.VISIBLE);
            imgColor.startAnimation(fadeIn_anim);
        }
        else if(anim_count == 2) {
            imgColor.clearAnimation();
            imgColor.setVisibility(View.VISIBLE);
            imgTemp.startAnimation(fadeIn_anim);
        }
        else{
            imgTemp.clearAnimation();
            imgTemp.setVisibility(View.VISIBLE);
            anim_count = 0;
            controlButtons(true);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
