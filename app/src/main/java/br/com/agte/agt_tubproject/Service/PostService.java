package br.com.agte.agt_tubproject.Service;

import android.content.Context;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import br.com.agte.agt_tubproject.Fragments.WifiFragments.FinalFragment;
import br.com.agte.agt_tubproject.Fragments.WifiFragments.Setup2Fragment;
import br.com.agte.agt_tubproject.Utils.Constants;


public class PostService {

    private static int net_attempts = 0;

    public PostService(){
        //Empty constructor
    }

    public void postNewNetwork(final Setup2Fragment parent, final Context context, final String SSID, final String PSWD, final FinalFragment finalFragment) {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, Constants.ESP_SERVER_POST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                net_attempts = 0;
                parent.getPgb_loading().setVisibility(View.GONE);
                parent.getBtn_setNetwork().setEnabled(true);
                finalFragment.setResponse(true);
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                if(net_attempts < 3){
                    parent.conn_network(context);
                    net_attempts++;
                } else {
                    net_attempts = 0;
                    parent.getPgb_loading().setVisibility(View.GONE);
                    parent.getBtn_setNetwork().setEnabled(true);
                    finalFragment.setResponse(false);
                }
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put(Constants.SSID_VALUE, SSID);
                MyData.put(Constants.PSWD_VALUE, PSWD);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }

    // Communication over Wi-Fi was cancelled due to project priorities
    public void postTubValues(final Context context, final int engine_level, final String colors_level, final int temp_level, final FinalFragment finalFragment) {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, Constants.ESP_SERVER_POST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put(Constants.ENGINE, Integer.toString(engine_level));
                MyData.put(Constants.COLOR, colors_level);
                MyData.put(Constants.TEMPERATURE, Integer.toString(temp_level));
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }
}
