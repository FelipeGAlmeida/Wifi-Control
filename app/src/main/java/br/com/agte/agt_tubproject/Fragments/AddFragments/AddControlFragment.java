package br.com.agte.agt_tubproject.Fragments.AddFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import br.com.agte.agt_tubproject.R;

public class AddControlFragment extends Fragment {

    Animation add_anim;

    public AddControlFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add, container, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), R.string.CONTACT_ADD, Toast.LENGTH_LONG).show();
            }
        });

        ImageView add_icon = v.findViewById(R.id.imgAddIcon);
        add_anim = AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim);
        add_icon.startAnimation(add_anim);

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
