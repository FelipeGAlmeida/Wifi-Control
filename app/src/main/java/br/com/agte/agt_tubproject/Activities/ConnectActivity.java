package br.com.agte.agt_tubproject.Activities;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import br.com.agte.agt_tubproject.R;
import br.com.agte.agt_tubproject.Service.BluetoothService;
import br.com.agte.agt_tubproject.Utils.Constants;
import br.com.agte.agt_tubproject.Utils.Utils;

public class ConnectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        Utils.setToolbar(this, getSupportActionBar(), R.string.BT_SETUP_C, R.drawable.bluetooth);
    }

//    public void replaceFragments(Fragment fragment, String tag){
//        // Insert the fragment by replacing any existing fragment
//        FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
//        fragmentManager.replace(R.id.fragment_conn_container, fragment)
//                .addToBackStack(tag)
//                .commit();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else{
            Intent intent = new Intent(Constants.ADAPTER_STATUS);
            intent.putExtra(Constants.CONN_STATUS, BluetoothService.isConnected());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if(getFragmentManager().getBackStackEntryCount() <= 1){
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constants.ENABLE_BLUETOOTH_REQUEST){
            if(resultCode == RESULT_OK){
                BluetoothService.self(this).connectToDevice(null);
            }else{
                onBackPressed();
                Toast.makeText(this, getResources().getString(R.string.NO_DEVICE_CONN), Toast.LENGTH_LONG).show();
            }
        }
    }
}
