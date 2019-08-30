package br.com.agte.agt_tubproject.Fragments.TubFragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import br.com.agte.agt_tubproject.Controllers.SpotListAdapter;
import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Utils.SaveConfigs;

public class TubLedFragment extends Fragment {

    private RecyclerView mRecycler;
    private SpotListAdapter mAdapter;
    private List<Integer> spots;

    public TubLedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tub_led, container, false);

        initSpotList();

        mRecycler = v.findViewById(R.id.spotList);
        mRecycler.setHasFixedSize(true);

        mAdapter = new SpotListAdapter(getActivity(), spots);

        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btn_add = v.findViewById(R.id.btn_spotAdd);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spots.add(spots.size()-1);
                SaveConfigs.getInstance(getContext()).saveNSpots(spots.size()-2);
                mAdapter.notifyDataSetChanged();
            }
        });

        Button btn_back = v.findViewById(R.id.btn_spotBack);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return v;
    }

    private void initSpotList(){
        spots = new ArrayList<>();
        int n_spots = SaveConfigs.getInstance(getContext()).loadNSpots();
        for (int i=-1;i<=n_spots;i++) {
            spots.add(i);
        }
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
