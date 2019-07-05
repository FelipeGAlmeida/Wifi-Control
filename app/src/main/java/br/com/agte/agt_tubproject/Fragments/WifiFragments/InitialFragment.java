package br.com.agte.agt_tubproject.Fragments.WifiFragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import br.com.agte.agt_tubproject.Activities.WifiActivity;
import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Utils.Constants;

public class InitialFragment extends Fragment {

    public InitialFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_initial, container, false);

        Button btn_setup1 = view.findViewById(R.id.btn_setup1);
        btn_setup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    ((WifiActivity)getActivity()).replaceFragments(new Setup1Fragment(), Constants.SETUP1);
                } else {
                    showDialog();
                }
            }
        });

        return view;
    }

    private void showDialog(){
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(getResources().getString(R.string.DIALOG_PERMISSION_TITLE));
        builder.setMessage(getResources().getString(R.string.DIALOG_PERMISSION_TEXT));
        builder.setPositiveButton(getResources().getString(R.string.DIALOG_ALLOW), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                ((WifiActivity)getActivity()).checkLocationPermission();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.DIALOG_SKIP), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                ((WifiActivity)getActivity()).replaceFragments(new Setup1Fragment(), Constants.SETUP1);
            }
        });
        //cria e exibe o AlertDialog
        AlertDialog alert = builder.create();
        alert.show();
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
